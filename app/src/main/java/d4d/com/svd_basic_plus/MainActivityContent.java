package d4d.com.svd_basic_plus;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.legacy.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.ptt.UsuarioFragmentPtt;
import d4d.com.svd_basic_plus.historial.HistorialAudioFragment;
import d4d.com.svd_basic_plus.utils.SessionManager;
import d4d.com.svd_basic_plus.utils.Utils;

import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.contextWs;

/**
 * Created by JuanPablo on 19/11/2015.
 */
public class MainActivityContent extends AppCompatActivity {
    public static Context contextContent = null;
    private static AppCompatActivity activityc;
    private Toolbar toolbar;
    private static Resources res;
    public static TextView txt_usuario_logiado;
    public static TextView txt_bateria_bluetooth;
    FloatingActionButton fab_panico=null;
    public static FloatingActionButton btn_ptt_master;
    static Handler handler; // declared before onCreate
    static Runnable myRunnable;
    ProgressDialog pk_Login=null;
    public static ProgressDialog pk_usuarios_conectados;
    private static final String TAG = "MainActivityContent";
    //Usuario
    private static SessionManager session;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);
        res = getResources();
        contextContent = this;
        activityc = this;
        String id_group = getIntent().getExtras().getString("id_group");
        String name_group = getIntent().getExtras().getString("name_group");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Utils.ucFirst(name_group));
        session = new SessionManager(contextContent);
        session.setGrupoId(id_group);
        session.setGrupoNombre(name_group);
        if(WebSocketComunication.webSocketConnection!=null) {
            if(WebSocketComunication.webSocketConnection.isConnected()) {
                WebSocketComunication.enviarIdGrupo();
            }
        }
        pk_usuarios_conectados=null;
        try{
            pk_usuarios_conectados= ProgressDialog.show(contextContent, res.getString(R.string.str_sistema),res.getString(R.string.str_conectando), false, false);
        }catch (Exception e){
        }
        txt_usuario_logiado = findViewById(R.id.textView3);
        txt_bateria_bluetooth = findViewById(R.id.textView4);
        fab_panico = findViewById(R.id.fabBtnPanico);
        btn_ptt_master = findViewById(R.id.btn_ptt_master);
        initInstances();
        txt_usuario_logiado.setText(session.getNombreUsuario());
        txt_bateria_bluetooth.setText(" ");
        pk_Login = new ProgressDialog(MainActivityContent.this, R.style.AppCompatAlertDialogStyle);
        pk_Login.setTitle(res.getString(R.string.str_alerta));
        pk_Login.setMessage(res.getString(R.string.str_panico));
        pk_Login.setCancelable(false);

        pk_Login.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.removeCallbacks(myRunnable);
                Log.i("ALERTA", "CANCELADA");
                dialog.dismiss();
            }
        });

        fab_panico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(WebSocketComunication.isOnlineInternet()){
                    if (WebSocketComunication.webSocketConnection.isConnected()) {
                        confirmacionAlerta();
                    } else {
                        Toast.makeText(contextContent, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String msgToasts = contextContent.getResources().getString(R.string.sin_conexion);
                    Toast.makeText(contextContent, msgToasts, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_ptt_master.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());//Aqui
                        if (WebSocketComunication.banderaPermiso == false && WebSocketComunication.banderaPresionado == false) {
                            //Envia mensaje PTT y espera confirmación de MsgBroker
                            WebSocketComunication.enviarPTT();
                            WebSocketComunication.banderaLevantado = false;
                            btn_ptt_master.setClickable(false);
                            Log.i("BOTON PRESIONADO", "banderaPermiso:" + WebSocketComunication.banderaPermiso + " && " + "banderaPresionado:" + WebSocketComunication.banderaPresionado + "");
                            btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (WebSocketComunication.banderaLevantado == false) {
                            WebSocketComunication.banderaLevantado = true;
                            Handler handler = new Handler();
                            Runnable myRunnable = new Runnable() {
                                public void run() {
                                    // do something
                                    WebSocketComunication.terminarPTT();
                                    WebSocketComunication.banderaPresionado = false;
                                    WebSocketComunication.banderaLevantado = false;
                                    btn_ptt_master.setClickable(true);
                                    btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(contextContent,R.color.blue_semi_transparent)));
                                }
                            };
                            handler.postDelayed(myRunnable, 1000);
                        }
                        break;
                }
                return true;
            }
        });
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            public void run() {if(pk_usuarios_conectados!=null){
                try {
                    pk_usuarios_conectados.dismiss();
                }catch (Exception e){
                }
            }
            }
        };
        handler.sendEmptyMessage(0);
        handler.postDelayed(myRunnable, 5000);
    }

    public void confirmacionAlerta(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
                MainActivityContent.this);
        builder.setTitle("Mensaje");
        builder.setMessage("Esta seguro de enviar la alerta?");
        builder.setCancelable(false);
        builder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            if(pk_Login!=null) {
                                pk_Login.show();
                            }
                        }catch (Exception e){
                        }
                        handler=  new Handler();
                        myRunnable = new Runnable() {
                            public void run() {
                                WebSocketComunication.enviarMensajeAlerta();
                                if(pk_Login!=null) {
                                    pk_Login.dismiss();
                                }
                            }
                        };
                        handler.postDelayed(myRunnable,3000);
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public static int conteoPtt=0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent key) {
        Log.i("getKeyCharacterMap",key.getKeyCharacterMap().getKeyboardType()+" ____");
        Log.i("getKeyCode",key.getKeyCode()+" ____");
        Log.i("keyCode DOWN",keyCode+" ____");
            switch (keyCode) {
                case 288:
                    if(conteoPtt==0) {
                        MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());//Aqui
                        if (WebSocketComunication.banderaPermiso == false && WebSocketComunication.banderaPresionado == false) {
                            //Envia mensaje PTT y espera confirmación de MsgBroker
                            WebSocketComunication.enviarPTT();
                            WebSocketComunication.banderaLevantado = false;
                            btn_ptt_master.setClickable(false);
                            Log.i("BOTON PRESIONADO", "banderaPermiso:" + WebSocketComunication.banderaPermiso + " && " + "banderaPresionado:" + WebSocketComunication.banderaPresionado + "");
                            btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        }
                        Log.i("CODIGO onKeyDown 288", keyCode + "");
                        conteoPtt=1;
                    }
                    break;
                case 79:
                    if(conteoPtt==0) {
                        MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());//Aqui
                        if (WebSocketComunication.banderaPermiso == false && WebSocketComunication.banderaPresionado == false) {
                            //Envia mensaje PTT y espera confirmación de MsgBroker
                            WebSocketComunication.enviarPTT();
                            WebSocketComunication.banderaLevantado = false;
                            btn_ptt_master.setClickable(false);
                            Log.i("BOTON PRESIONADO", "banderaPermiso:" + WebSocketComunication.banderaPermiso + " && " + "banderaPresionado:" + WebSocketComunication.banderaPresionado + "");
                            btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        }
                        Log.i("CODIGO onKeyDown 79", keyCode + "");
                        conteoPtt=1;
                    }

                case 301:
                    if(conteoPtt==0) {
                        MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());//Aqui
                        if (WebSocketComunication.banderaPermiso == false && WebSocketComunication.banderaPresionado == false) {
                            //Envia mensaje PTT y espera confirmación de MsgBroker
                            WebSocketComunication.enviarPTT();
                            WebSocketComunication.banderaLevantado = false;
                            btn_ptt_master.setClickable(false);
                            Log.i("BOTON PRESIONADO", "banderaPermiso:" + WebSocketComunication.banderaPermiso + " && " + "banderaPresionado:" + WebSocketComunication.banderaPresionado + "");
                            btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        }
                        Log.i("CODIGO onKeyDown 79", keyCode + "");
                        conteoPtt=1;
                    }
                case 302:
                    if(conteoPtt==0) {
                        MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());//Aqui
                        if (WebSocketComunication.banderaPermiso == false && WebSocketComunication.banderaPresionado == false) {
                            //Envia mensaje PTT y espera confirmación de MsgBroker
                            WebSocketComunication.enviarPTT();
                            WebSocketComunication.banderaLevantado = false;
                            btn_ptt_master.setClickable(false);
                            Log.i("BOTON PRESIONADO", "banderaPermiso:" + WebSocketComunication.banderaPermiso + " && " + "banderaPresionado:" + WebSocketComunication.banderaPresionado + "");
                            btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        }
                        Log.i("CODIGO onKeyDown 79", keyCode + "");
                        conteoPtt=1;
                    }
                case 4:
                    //finish();
                    break;
            }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent key) {
        Log.i("getKeyCharacterMap",key.getKeyCharacterMap().getKeyboardType()+" ____");
        Log.i("getKeyCode",key.getKeyCode()+" ____");
        Log.i("keyCode UP",keyCode+" ____");
        switch (keyCode) {
            case 288:
                if (WebSocketComunication.banderaLevantado == false) {
                    WebSocketComunication.banderaLevantado = true;
                    Handler handler = new Handler();
                    Runnable myRunnable = new Runnable() {
                        public void run() {
                            // do something
                            WebSocketComunication.terminarPTT();
                            WebSocketComunication.banderaPresionado = false;
                            WebSocketComunication.banderaLevantado = false;
                            btn_ptt_master.setClickable(true);
                            btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue_semi_transparent)));
                            Log.i("BOTON LEVANTADO", "banderaPermiso:" + WebSocketComunication.banderaPermiso + " && " + "banderaPresionado:" + WebSocketComunication.banderaPresionado + "");
                        }
                    };
                    handler.postDelayed(myRunnable, 1000);
                    conteoPtt=0;
                }
                break;
            case 79:
                if (WebSocketComunication.banderaLevantado == false) {
                    WebSocketComunication.banderaLevantado = true;
                    Handler handler = new Handler();
                    Runnable myRunnable = new Runnable() {
                        public void run() {
                            WebSocketComunication.terminarPTT();
                            WebSocketComunication.banderaPresionado = false;
                            WebSocketComunication.banderaLevantado = false;
                            btn_ptt_master.setClickable(true);
                            btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(contextContent,R.color.blue_semi_transparent)));
                            Log.i("BOTON LEVANTADO", "banderaPermiso:" + WebSocketComunication.banderaPermiso + " && " + "banderaPresionado:" + WebSocketComunication.banderaPresionado + "");
                        }
                    };
                    handler.postDelayed(myRunnable, 1000);
                    conteoPtt=0;
                }
                break;
            case 301:
                if (WebSocketComunication.banderaLevantado == false) {
                    WebSocketComunication.banderaLevantado = true;
                    Handler handler = new Handler();
                    Runnable myRunnable = new Runnable() {
                        public void run() {
                            WebSocketComunication.terminarPTT();
                            WebSocketComunication.banderaPresionado = false;
                            WebSocketComunication.banderaLevantado = false;
                            btn_ptt_master.setClickable(true);
                            btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(contextContent,R.color.blue_semi_transparent)));
                            Log.i("BOTON LEVANTA", "banderaPermiso:" + WebSocketComunication.banderaPermiso + " && " + "banderaPresionado:" + WebSocketComunication.banderaPresionado + "");
                        }
                    };
                    handler.postDelayed(myRunnable, 1000);
                    conteoPtt=0;
                }
            break;
            case 302:
                if (WebSocketComunication.banderaLevantado == false) {
                    WebSocketComunication.banderaLevantado = true;
                    Handler handler = new Handler();
                    Runnable myRunnable = new Runnable() {
                        public void run() {
                            WebSocketComunication.terminarPTT();
                            WebSocketComunication.banderaPresionado = false;
                            WebSocketComunication.banderaLevantado = false;
                            btn_ptt_master.setClickable(true);
                            btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(contextContent,R.color.blue_semi_transparent)));
                            Log.i("BOTON LEVANT", "banderaPermiso:" + WebSocketComunication.banderaPermiso + " && " + "banderaPresionado:" + WebSocketComunication.banderaPresionado + "");
                        }
                    };
                    handler.postDelayed(myRunnable, 1000);
                    conteoPtt=0;
                }
                break;
        }
        return false;
    }

    /*@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            Log.i("EVENTOS", " .. "+event.getKeyCode());
            if (event.getAction() == KeyEvent.ACTION_UP){

                //enter();

                return true;
            }}
        return super.dispatchKeyEvent(event);
    };*/


    private void initInstances(){
        MyAdapter mAdapter = new MyAdapter(getFragmentManager());
        for(int i=0; i<2; i++) {
            Fragment fragment = null;
            switch(i) {
                case 0:
                    Log.i("Carga", "UsuarioFragmentPtt");
                    fragment = UsuarioFragmentPtt.newInstance();//solo se cargan el inicio
                    break;
                case 1:
                    Log.i("Carga", "HistorialAudioFragment");
                    fragment = HistorialAudioFragment.newInstance();
                    break;
            }
            mAdapter.addFragment(fragment);
        }
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mAdapter);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos=tab.getPosition();
                switch(pos) {
                    case 0:
                        btn_ptt_master.show();
                        /*new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    if(MainActivityContent.contextContent!=null) {
                                        UsuarioFragmentPtt.cargarNumeroConectados();
                                    }
                                }}, 100
                        );*/
                        break;
                    case 1:
                        btn_ptt_master.hide();
                        enviarHistorialAudio();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume() {
        WebSocketComunication.valida_login=2;
        WebSocketComunication.banderaCerrarSesion = false;
        if(contextWs==null) {
            Intent myService = new Intent(this, WebSocketComunication.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(myService);
                Log.i("startForegroundService", "ANDROID OREO O SUPERIOR");
            } else {
                startService(myService);
                Log.i("startService", "ANDROID NOUGAT O MARSMELLOW");
            }
        }
        super.onResume();
    }

    public static void enviarHistorialAudio(){
        if(WebSocketComunication.isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "historialAudio");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("grupo", session.getGrupoId());
                if (WebSocketComunication.webSocketConnection.isConnected()&& session.isLoggedIn()==true) {
                    WebSocketComunication.webSocketConnection.sendMessage(json.toString());
                    WebSocketComunication.cosumoDatos(json.toString());
                }else {
                    Toast.makeText(contextContent, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            String msgToasts=contextContent.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextContent, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragmentList;
        private String tabTitles[] = new String[] {"Comunicación","Historial"};
        public MyAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList = new ArrayList<Fragment>();
        }
        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
package d4d.com.svd_basic_plus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.group.GrupoAdapter;
import d4d.com.svd_basic_plus.group.GrupoDetalle;
import d4d.com.svd_basic_plus.proyecto.ProyectoActivity;
import d4d.com.svd_basic_plus.utils.SessionManager;
import d4d.com.svd_basic_plus.utils.Utils;
import static d4d.com.svd_basic_plus.MainActivityContent.contextContent;
import static d4d.com.svd_basic_plus.MainActivityContent.handler;
import static d4d.com.svd_basic_plus.MainActivityContent.myRunnable;
import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.contextWs;
import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.session;
import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.webSocketConnection;

public class MainActivity extends AppCompatActivity {
    private static Resources res;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private static MainActivity myInstance;
    public static Context context = null;
    //Creando Views
    public static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    //Creando Lista Categorías
    private static List<GrupoDetalle> grupoItems;
    static ProgressDialog pk_loadingCerrarSesion;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myInstance = MainActivity.this;
        context = MainActivity.this;
        res = getResources();
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        final Typeface tf =  ResourcesCompat.getFont(context, R.font.space_quest_talic);
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager llm = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        session = new SessionManager(context);
        if(session.getDetalleGrupo()!=null) {
            if(session.getDetalleGrupo().compareToIgnoreCase("")!=0) {
                cargarListaGrupos(session.getDetalleGrupo());
            }
        }

        WebSocketComunication.subscribeKuzzle("protectia","checkup");
    }

    public static void cargarAdapter(List<GrupoDetalle> list){
        //Finalmente inicializamos el adapter
        adapter = new GrupoAdapter(list, context);
        //Agregamos el adapter al RecyclerView
        recyclerView.setAdapter(adapter);
    }

    /*@Override
    protected void onDestroy() {
        Log.i("onDestroy", "onDestroy");
        super.onDestroy();
        if(session.isLoggedIn()) {
            //Intent imain = new Intent(context, MainActivity.class);
            //startActivity(imain);
        }else{
            if(WebSocketComunication.serviceWebsocket!=null) {
                WebSocketComunication.serviceWebsocket.stopForeground(true);
                WebSocketComunication.serviceWebsocket.stopSelf();
                System.exit(0);
            }
        }
    }*/

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

    public static void agregarMiembro(String grupoMiembro){
        JSONObject miembro = null;
        String lista_grupos=session.getDetalleGrupo();
        boolean bandera=false;
        JSONArray grupos_existentes = null;
        JSONArray grupos_modificados = null;
        JSONArray grupos_nuevos = null;
        try {
            miembro = new JSONObject(grupoMiembro);
            grupos_modificados=miembro.getJSONArray("gruposData");
            grupos_existentes = new JSONArray(lista_grupos);
            grupos_nuevos = new JSONArray(lista_grupos);
            for(int i=0; i<grupos_modificados.length(); i++) {
                JSONObject item = new JSONObject(grupos_modificados.getString(i));
                String grupoModificado=item.getString("ugid");
                for (int j = 0; j < grupos_existentes.length(); j++) {
                    JSONObject item1 = new JSONObject(grupos_existentes.getString(j));
                    String grupoActual=item1.getString("ugid");
                    if(grupoModificado.compareToIgnoreCase(grupoActual)==0) {
                        bandera=true;
                    }
                }
                if(bandera==false){
                    grupos_nuevos.put(grupos_modificados.getString(i));
                }
            }
            String newDetalleGrupo=grupos_nuevos.toString();
            session.setDetalleGrupo(newDetalleGrupo);
            cargarListaGrupos(newDetalleGrupo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void eliminarMiembro(String grupoMiembro){
        JSONObject miembro = null;
        String lista_grupos=session.getDetalleGrupo();
        JSONArray grupos_existentes = null;
        JSONArray grupos_modificados = null;
        try {
            miembro = new JSONObject(grupoMiembro);
            grupos_modificados=miembro.getJSONArray("grupos");
            grupos_existentes = new JSONArray(lista_grupos);
            for(int i=0; i<grupos_modificados.length(); i++) {
                String grupoModificado=grupos_modificados.getString(i);
                for (int j = 0; j < grupos_existentes.length(); j++) {
                    JSONObject item1 = new JSONObject(grupos_existentes.getString(j));
                    String grupoActual=item1.getString("ugid");
                    if(grupoModificado.compareToIgnoreCase(grupoActual)==0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            grupos_existentes.remove(j);
                        }
                    }
                }
            }
            for(int k=0; k<grupos_existentes.length();k++){
                JSONObject item1 = new JSONObject(grupos_existentes.getString(k));
                String grupoActual=item1.getString("ugid");
            }
            String newDetalleGrupo=grupos_existentes.toString();
            session.setDetalleGrupo(newDetalleGrupo);
            cargarListaGrupos(newDetalleGrupo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void cargarListaGrupos(final String detalle_grupo){
        try {
            grupoItems = new ArrayList<GrupoDetalle>();
            JSONArray ja_data=null;
            ja_data = new JSONArray(detalle_grupo);
            for(int i=0; i<ja_data.length(); i++){
                JSONObject item=new JSONObject(ja_data.getString(i));
                GrupoDetalle msg = new GrupoDetalle();
                msg.setGrupoId(item.getString("ugid"));
                msg.setGrupoNombre(Utils.ucFirst(item.getString("nombre")));
                String icono= item.getString("icono");
                Drawable d_icon;
                if(icono.compareToIgnoreCase("null")==0){
                    d_icon = ContextCompat.getDrawable(context, R.drawable.ic_gallery);
                }else {
                    byte[] decodedString = Base64.decode(icono, Base64.DEFAULT);
                    Bitmap icon = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    d_icon = new BitmapDrawable(context.getResources(), icon);
                }
                msg.setGrupoImagen(d_icon);
                grupoItems.add(msg);
            }
            cargarAdapter(grupoItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            myInstance.finish();
        }
    }

    public static void marcarLista(String detalle_grupo, String name_user_ptt) {
        try {
            if (detalle_grupo != null) {
                JSONArray ja_data = new JSONArray(detalle_grupo);
                for (int i = 0; i < ja_data.length(); i++) {
                    JSONObject item = new JSONObject(ja_data.getString(i));
                    //Log.i("DETALLE", session.getGrupoId() + "=" + item.getString("ugid") + "  __");
                    if (session.getGrupoId().compareToIgnoreCase(item.getString("ugid")) == 0) {
                        updateView(i, name_user_ptt);
                        //if (recyclerView != null) {
                           // try {
                                //recyclerView.getChildAt(i).setBackgroundColor(Color.DKGRAY);
                            //} catch (Exception e) {
                               // Log.e("ERROR", e.toString());
                           // }
                       // }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void updateView(int index, String name_user_ptt){
        View v = recyclerView.getChildAt(index - recyclerView.getItemDecorationCount());
        if(v == null)
            return;
        TextView someText = v.findViewById(R.id.id_user_ptt);
        someText.setText(name_user_ptt);
    }

    public static MainActivity getMyInstance() {
        return myInstance;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_item, menu);
        super.onCreateOptionsMenu(menu);

        MenuItem item = menu.findItem(R.id.regiter_proyecto);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPA = new Intent(MainActivity.this, ProyectoActivity.class);
                startActivity(intentPA);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.regiter_location:
                Intent intentRL = new Intent(MainActivity.this, RegisterLocationActivity.class);
                startActivity(intentRL);
                break;
            //case R.id.regiter_proyecto:

                //break;
            case R.id.reconectar:
                if (webSocketConnection == null) {
                    WebSocketComunication.startConnectionWebsocket();
                }else if(webSocketConnection.isConnected()==false) {
                    WebSocketComunication.startConnectionWebsocket();
                }
                break;
            case R.id.modo:
                Intent intentMA1 = new Intent(MainActivity.this, ActivityStreaming.class);
                startActivity(intentMA1);
                finish();
                break;
            case R.id.menu_sincronizar_Bluetooth:
                Toast.makeText(context, "Sincronizando Bluetooth . . .", Toast.LENGTH_LONG).show();
                WebSocketComunication.iniciarBluetoothBajaEnergia();
                break;
            case R.id.cerrar_sesion:
                cerrarSesionUser();
                return true;
            case R.id.acerca_de:
                Intent intentSA = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSA);
                break;
            case R.id.salir:
                //if(WebSocketComunication.serviceWebsocket!=null) {
                    WebSocketComunication.serviceWebsocket.stopForeground(true);
                    WebSocketComunication.serviceWebsocket.stopSelf();
                //}
                System.exit(0);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public static Uri soundAlarma = Uri.parse("android.resource://d4d.com.svd_basic_plus/raw/alarma");
    static MediaPlayer mpAlarma;
    public static void mensajeAlerta(String msjServer){
        JSONObject msj=null;
        soundAlarma = Uri.parse("android.resource://d4d.com.svd_basic_plus/raw/alarma");
        String msjDisplay="Alarma";
        mpAlarma = MediaPlayer.create(context, soundAlarma);
        try {
            msj = new JSONObject(msjServer);
            msjDisplay="\n Mensaje: "+msj.getString("texto")+"\n\n De: "+msj.getString("nombre");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressDialog pk_Login=null;
        if(contextContent!=null) {
            pk_Login = new ProgressDialog(contextContent, R.style.AppCompatAlertDialogStyle);
        }else{
            pk_Login = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        }
        pk_Login.setTitle(res.getString(R.string.str_alerta));
        pk_Login.setMessage(msjDisplay);
        pk_Login.setCancelable(false);
        pk_Login.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.removeCallbacks(myRunnable);
                if(mpAlarma!=null) {
                    mpAlarma.stop();
                    mpAlarma.release();
                }
                mpAlarma = null;
                dialog.dismiss();
            }
        });
        try{
            if(pk_Login!=null) {
                pk_Login.show();
            }
        }catch (Exception e){
        }
        handler=  new Handler();
        final ProgressDialog finalPk_Login = pk_Login;
        myRunnable = new Runnable() {
            public void run() {
                if(finalPk_Login !=null) {
                    finalPk_Login.dismiss();
                }
                if(mpAlarma!=null) {
                    mpAlarma.stop();
                    mpAlarma.release();
                }
                mpAlarma = null;
            }
        };
        handler.postDelayed(myRunnable,30000);
        mpAlarma.start();
    }

    public static void cerrarSesionUser(){
        context = myInstance;
        cerrarSesionUserWebsocket();
        pk_loadingCerrarSesion = ProgressDialog.show(context, res.getString(R.string.str_sistema),res.getString(R.string.str_cerrando_sesion), false, false);
        new Handler().postDelayed(
            new Runnable() {
                public void run() {
                    if(pk_loadingCerrarSesion!=null) {
                        pk_loadingCerrarSesion.dismiss();
                    }
                    session.logoutUser(context);
                    context.stopService(new Intent(context, WebSocketComunication.class));
                }}, 4000);
    }

    public static void cerrarSesionUserWebsocket(){
        String uuid= session.getUuid();
        String token=session.getToken();
        JSONObject logoutSend = new JSONObject();
        try {
            logoutSend.accumulate("tipo", "logout").accumulate("uuid", uuid)
                    .accumulate("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WebSocketComunication.valida_login=0;
        if(webSocketConnection!=null) {
            if (webSocketConnection.isConnected()) {
                webSocketConnection.sendMessage(logoutSend.toString());
                WebSocketComunication.cosumoDatos(logoutSend.toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

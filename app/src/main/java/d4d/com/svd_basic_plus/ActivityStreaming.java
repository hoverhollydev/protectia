package d4d.com.svd_basic_plus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.pedro.encoder.input.video.CameraHelper;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.view.AutoFitTextureView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.group.GrupoAdapter;
import d4d.com.svd_basic_plus.group.GrupoDetalle;
import d4d.com.svd_basic_plus.utils.SessionManager;
import d4d.com.svd_basic_plus.utils.Utils;
import static d4d.com.svd_basic_plus.MainActivityContent.contextContent;
import static d4d.com.svd_basic_plus.MainActivityContent.handler;
import static d4d.com.svd_basic_plus.MainActivityContent.myRunnable;
import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.contextWs;
import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.session;
import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.webSocketConnection;

public class ActivityStreaming extends AppCompatActivity implements ConnectCheckerRtmp{
    private static Resources res;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private static ActivityStreaming myInstance;
    public static Context context = null;
    //Creando Views
    public static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    //Creando Lista Categorías
    private static List<GrupoDetalle> grupoItems;
    static ProgressDialog pk_loadingCerrarSesion;

    //Streaming rtmp
    private static RtmpCamera1 rtmpCamera2;
    private static AutoFitTextureView textureView;
    private static Button bStartStop, bRecord, bSwitch;
    private static String currentDateAndTime = "";
    private static File folder = new File(""+WebSocketComunication.getDirectory(SessionManager.getUuid(),"Stream"));
    static String urlrtsp;
    static String usuario_consola;

    //Video
    private static int width = 640;
    private static int height = 480;
    private static int videoBibrate=900*1024;
    private static int fps=5;
    private static  boolean hardwareRotacion=true;
    //Audio
    private static  int audioBitrate=128*1024;
    private static  int sampleRate=22050;
    private static boolean isStereo=true;
    private static boolean isEchoCanceler=false;
    private static boolean isNoiseSuppressor=false;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        myInstance = ActivityStreaming.this;
        context = ActivityStreaming.this;
        res = getResources();
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        textureView = findViewById(R.id.textureView);
        rtmpCamera2 = new RtmpCamera1(textureView, this);

        /*rtmpCamera2.setVideoBitrateOnFly(videoBibrate);
        Toast toast = Toast.makeText(RtmpActivity.this, "Nuevo velocidad de bits : " + videoBibrate, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();*/

        bStartStop = findViewById(R.id.b_start_stop);
        bRecord = findViewById(R.id.b_record);
        bSwitch = findViewById(R.id.b_switch);

        bStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarStreaming();
            }
        });

        bRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarStreaming();
            }
        });

        bSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rtmpCamera2.switchCamera();
            }
        });

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                //drawerLayout.openDrawer(Gravity.END);
                //prepareEncoders();
                //textureView.setAspectRatio(480, 320);
                //rtmpCamera2.startPreview();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
                //runOnUiThread(() -> {
                //Log.i("Texture","onSurfaceTextureSizeChanged");
                rtmpCamera2.startPreview();
                //});
                //rtmpCamera2.startPreview(CameraHelper.Facing.BACK);
                //or
                //rtmpCamera2.startPreview(CameraHelper.Facing.FRONT);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {

                Log.i("Texture","onSurfaceTextureDestroyed");
                if (rtmpCamera2.isRecording()) {
                    rtmpCamera2.stopRecord();
                    if(context!=null) {
                        Toast.makeText(context,
                                "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                                Toast.LENGTH_SHORT).show();
                    }
                    currentDateAndTime = "";
                }
                if (rtmpCamera2.isStreaming()) {
                    rtmpCamera2.stopStream();
                    //bStartStop.setText(getResources().getString(R.string.start_button));
                    if(context!=null) {
                        bStartStop.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_record));
                    }
                }
                rtmpCamera2.stopPreview();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });

        //WebSocketComunication.listaArchivos();//envia lista de nombres de arvhivos de video
    }

    public static void cargarAdapter(List<GrupoDetalle> list){
        //Finalmente inicializamos el adapter
        adapter = new GrupoAdapter(list, context);
        //Agregamos el adapter al RecyclerView
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        Log.i("onDestroy", "onDestroy");
        super.onDestroy();
        ActivityStreaming.context=null;
        ActivityStreaming.myInstance=null;
        if(session.isLoggedIn()) {
            //Intent imain = new Intent(context, MainActivity1.class);
            //startActivity(imain);
        }else{
            if(WebSocketComunication.serviceWebsocket!=null) {
                WebSocketComunication.serviceWebsocket.stopForeground(true);
                WebSocketComunication.serviceWebsocket.stopSelf();
                System.exit(1);
            }
        }
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

    public static ActivityStreaming getMyInstance() {
        return myInstance;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_item, menu);
        super.onCreateOptionsMenu(menu);

        MenuItem bedMenuItem = menu.findItem(R.id.modo);

        bedMenuItem.setTitle("Modo sin video");


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.regiter_location:
                Intent intentRL = new Intent(ActivityStreaming.this, RegisterLocationActivity.class);
                startActivity(intentRL);
                break;
            case R.id.modo:
                pararStreaming();
                Intent intentMA1 = new Intent(ActivityStreaming.this, MainActivity.class);
                startActivity(intentMA1);
                finish();
                break;
            case R.id.reconectar:
                if (webSocketConnection == null) {
                    WebSocketComunication.startConnectionWebsocket();
                }else if(webSocketConnection.isConnected()==false) {
                    WebSocketComunication.startConnectionWebsocket();
                }
                break;
            case R.id.menu_sincronizar_Bluetooth:
                Toast.makeText(context, "Sincronizando Bluetooth . . .", Toast.LENGTH_LONG).show();
                WebSocketComunication.iniciarBluetoothBajaEnergia();
                break;
            case R.id.cerrar_sesion:
                cerrarSesionUser();
                return true;
            case R.id.acerca_de:
                Intent intentSA = new Intent(ActivityStreaming.this, SettingsActivity.class);
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

    @Override
    public void onConnectionSuccessRtmp() {
        Log.i("RTMP EVENT","onConnectionSuccessRtmp");
        runOnUiThread(() -> {

            String num_cam="1";
            if(rtmpCamera2.isFrontCamera()) {
                num_cam="2";
            }

            if(WebSocketComunication.tipSendSteaming) {
                WebSocketComunication.enviarMensajeStreamingRespuesta(usuario_consola, urlrtsp, "stream", 0,num_cam);
            }else{

                WebSocketComunication.enviarMensajeStreaming(urlrtsp, "normal", num_cam);//modalidad = panico/normal
            }
            Toast toast = Toast.makeText(context, "Conexión exitosa", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        });
    }

    @Override
    public void onConnectionFailedRtmp(final String reason) {
        Log.i("RTMP EVENT","onConnectionFailedRtmp");
        runOnUiThread(() -> {
            Toast toast= Toast.makeText(context, "Conexión fallida. " + reason, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            //error 61 al conectar RTMP con el servidor
            if(WebSocketComunication.tipSendSteaming) {
                String num_cam="1";
                if(rtmpCamera2.isFrontCamera()) {
                    num_cam="2";
                }
                WebSocketComunication.enviarMensajeStreamingRespuesta(usuario_consola, urlrtsp, "error", 61, num_cam);

            }else{
                WebSocketComunication.enviarMensajeErrorStreaming(usuario_consola,61);
            }
            rtmpCamera2.stopStream();
            //bStartStop.setText(getResources().getString(R.string.start_button));
            bStartStop.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_record));
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
            && rtmpCamera2.isRecording()) {
          rtmpCamera2.stopRecord();
          Toast toast1= Toast.makeText(RtmpActivity.this,
                  "Archivo " + currentDateAndTime + ".mp4 guardado en " + folder.getAbsolutePath(),
                  Toast.LENGTH_SHORT);
          toast1.setGravity(Gravity.CENTER, 0, 0);
          toast1.show();
          currentDateAndTime = "";
        }*/
        });
    }

    @Override
    public void onDisconnectRtmp() {
        Log.i("RTMP EVENT","onDisconnectRtmp");


        runOnUiThread(() -> {
            if(context!=null) {
                Toast toast = Toast.makeText(context, "Streaming Desconectado", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                    && rtmpCamera2.isRecording()) {
                rtmpCamera2.stopRecord();
                Toast toast1= Toast.makeText(context,
                        "Archivo " + currentDateAndTime + ".mp4 guardado en " + folder.getAbsolutePath(),
                        Toast.LENGTH_SHORT);
                toast1.setGravity(Gravity.CENTER, 0, 0);
                toast1.show();
                currentDateAndTime = "";
            }*/
        });

        if(SessionManager.getValidaVerificacion()==true){
            //rtmpCamera2.reConnect(5000);
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = () -> {
                String num_cam="1";
                if(rtmpCamera2.isFrontCamera()) {
                    num_cam="2";
                }
                ///WebSocketComunication.enviarReconectarStreaming(usuario_consola, urlrtsp,num_cam);
            };
            handler.sendEmptyMessage(0);
            handler.postDelayed(myRunnable, 5000);
        }
    }

    @Override
    public void onAuthErrorRtmp() {
        Log.i("RTMP EVENT","onAuthErrorRtmp");
        runOnUiThread(() -> {
            //error 62 onAuthErrorRtmp
            if(WebSocketComunication.tipSendSteaming) {
                String num_cam="1";
                if(rtmpCamera2.isFrontCamera()) {
                    num_cam="2";
                }
                WebSocketComunication.enviarMensajeStreamingRespuesta(usuario_consola, urlrtsp, "error", 62, num_cam);
            }else{
                WebSocketComunication.enviarMensajeErrorStreaming(usuario_consola,62);
            }
            Toast toast= Toast.makeText(context, "Error en autentificación", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        Log.i("RTMP EVENT","onAuthSuccessRtmp");
        runOnUiThread(() -> {
            Toast toast= Toast.makeText(context, "Autentificación exitosa", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
    }



    public static void swichtCamera(String value){
        try {
            switch (value){
                case "1":
                    if(rtmpCamera2.isFrontCamera()) {
                        rtmpCamera2.switchCamera();
                    }
                    break;

                case "2":
                    if(rtmpCamera2.isFrontCamera()==false) {
                        rtmpCamera2.switchCamera();
                    }
                    break;
            }
            WebSocketComunication.switchCameraVideo(usuario_consola,urlrtsp,value);
        } catch (final CameraOpenException e) {
            Toast toast= Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void recursiveSaveFrameVideo(){
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = () -> {
            if(SessionManager.getValidaVerificacion()==true) {
                stopSaveVideo();
                if(ActivityStreaming.context!=null) {
                    Toast toast = Toast.makeText(context,
                            "Archivo " + currentDateAndTime + ".mp4 guardado en " + folder.getAbsolutePath(),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                currentDateAndTime = "";

                guardarStreaming();
                recursiveSaveFrameVideo();
            }
        };
        handler.sendEmptyMessage(0);
        handler.postDelayed(myRunnable, 60000);
    }

    public static void iniciarStreaming(){
        if (!rtmpCamera2.isStreaming()) {
            loadedStreaming();
        } else {
            stopStreaming();

        }

    }

    public static void pararStreaming(){
        if (!rtmpCamera2.isStreaming()) {
        } else {
            stopStreaming();
        }
    }

    public static void guardarStreaming(){

        //rtmpCamera2.disableAudio();
        //rtmpCamera2.setPreviewOrientation(0);
        //rtmpCamera2.setVideoBitrateOnFly(videoBibrate);
        //rtmpCamera2.disableFaceDetection();
        //rtmpCamera2.setLimitFPSOnFly(fps);

        Log.d("TAG_R", "b_start_stop: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!rtmpCamera2.isRecording()) {
                bRecord.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.record)));
                try {
                    if (!folder.exists()) {
                        folder.mkdir();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    currentDateAndTime = sdf.format(new Date());
                    if (!rtmpCamera2.isStreaming()) {
                        //if (rtmpCamera2.prepareAudio() && rtmpCamera2.prepareVideo()) {
                        if (rtmpCamera2.prepareVideo()) {
                            rtmpCamera2.startRecord(
                                    folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                            //Toast toast= Toast.makeText(contextRtmp, "Grabando... ", Toast.LENGTH_SHORT);
                            //toast.setGravity(Gravity.CENTER, 0, 0);
                            //toast.show();
                        } else {
                            Toast toast= Toast.makeText(context, "Error al preparar el flujo de trasmisión, este dispositivo no puede hacerlo",
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else {
                        Log.i("Transmitiendo","si STREAM");
                        rtmpCamera2.startRecord(
                                folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                        Toast toast = Toast.makeText(context, "Grabando... ", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } catch (IOException e) {
                    SessionManager.setValidaVerificacion(false);
                    stopSaveVideo();
                    Toast toast= Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } else {
                //rtmpCamera2.stopRecord();

                /*Toast toast= Toast.makeText(contextRtmp, "Archivo " + currentDateAndTime + ".mp4 guardado en " + folder.getAbsolutePath(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();*/
                SessionManager.setValidaVerificacion(false);
                stopSaveVideo();

                Toast toast = Toast.makeText(context,
                        "Archivo " + currentDateAndTime + ".mp4 guardado en " + folder.getAbsolutePath(),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                currentDateAndTime = "";
            }

        } else {
            Toast toast= Toast.makeText(context, "Usted necesita como mínimo Android MARSHMALLOW(API 23) para esto...",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void stopSaveVideo(){
        //if(rtmpCamera2.isStreaming()) {
        rtmpCamera2.stopRecord();
        if(context!=null) {
            bRecord.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
        }

        //}
    }

    public static void loadedStreaming(){
        //val spacesFileRepository = UtilsKt.SpacesFileRepository(applicationContext);
        if (rtmpCamera2.isRecording() || prepareEncoders()) {
        //if (rtmpCamera2.isRecording()) {
            //bStartStop.setText(R.string.stop_button);
            bStartStop.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_stop));
            urlrtsp="rtmp://198.211.109.176:1935/live/"+session.getUuid();
            Log.i("RTMP Link", urlrtsp);
            rtmpCamera2.startStream(urlrtsp);
            WebSocketComunication.tipSendSteaming=false;
            SessionManager.setValidaVerificacion(true);
            guardarStreaming();
            recursiveSaveFrameVideo();

        } else {
            //Error 63 error el preparar el flujo en el dipositivo
            if(WebSocketComunication.tipSendSteaming) {
                String num_cam="1";
                if(rtmpCamera2.isFrontCamera()) {
                    num_cam="2";
                }
                WebSocketComunication.enviarMensajeStreamingRespuesta(usuario_consola, urlrtsp, "error", 63, num_cam);
            }else{
                WebSocketComunication.enviarMensajeErrorStreaming(usuario_consola,63);
            }
            Toast toast=Toast.makeText(context, "Error al preparar el flujo de transmisión, este dispositivo no puede hacerlo", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void stopStreaming(){
        //bStartStop.setText(R.string.start_button);
        WebSocketComunication.tipSendSteaming=false;
        bStartStop.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_record));
        rtmpCamera2.stopStream();

        WebSocketComunication.pararStreamingVideo(usuario_consola, urlrtsp);


        SessionManager.setValidaVerificacion(false);
        stopSaveVideo();
        Toast toast = Toast.makeText(context,
                "Archivo " + currentDateAndTime + ".mp4 guardado en " + folder.getAbsolutePath(),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        currentDateAndTime = "";
        if(rtmpCamera2.isFrontCamera()) {
            rtmpCamera2.switchCamera();
        }
    }

    private static boolean prepareEncoders() {

        return rtmpCamera2.prepareVideo(width,
                height,
                fps,
                videoBibrate,
                hardwareRotacion,
                CameraHelper.getCameraOrientation(context))
                && rtmpCamera2.prepareAudio(audioBitrate,
                sampleRate,
                isStereo,
                isEchoCanceler,
                isNoiseSuppressor);

        /*return rtmpCamera2.prepareVideo(width,
                height,
                fps,
                videoBibrate,
                hardwareRotacion,
                CameraHelper.getCameraOrientation(context));*/
    }


}

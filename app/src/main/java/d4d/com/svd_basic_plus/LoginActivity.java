package d4d.com.svd_basic_plus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.utils.SessionManager;
import d4d.com.svd_basic_plus.utils.Utils;

import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.webSocketConnection;

/**
 * Created by jp_leon on 24/10/2016.
 */

public class LoginActivity extends AppCompatActivity {
    //Usuario
    private static SessionManager session;
    private static boolean msjLogin=true;
    private static ProgressDialog pk_Login;
    private static ProgressDialog pk_Loginl;
    private Resources res;
    private EditText txt_password;
    private EditText txt_user;
    private EditText txt_ip;
    private EditText txt_port;
    private JSONObject jsonConsultaLogin;
    private static AppCompatActivity activity = null;
    private static Context context = null;
    private static String password = "";
    private static String user = "";
    private static String ip = "";
    private static String puerto = "";
    private static LoginActivity myInstanceL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myInstanceL = LoginActivity.this;
        activity = this;
        context = this;
        res = getResources();

        Intent myService = new Intent(this, WebSocketComunication.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(myService);
        } else {
            startService(myService);
        }

        txt_user = findViewById(R.id.txt_usuario);
        txt_password = findViewById(R.id.txt_contrasena);
        txt_ip = findViewById(R.id.txt_ip);
        txt_port = findViewById(R.id.txt_puerto);
        txt_user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_user.getText().length() == 0) {
                    txt_user.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });
        txt_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_password.getText().length() == 0) {
                    txt_password.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });
        txt_ip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_ip.getText().length() == 0) {
                    txt_ip.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });
        txt_port.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false && txt_port.getText().length() == 0) {
                    txt_port.setError(res.getString(R.string.str_campo_obligatorio));
                }
            }
        });
        txt_port.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    ip = txt_ip.getText().toString();
                    puerto = txt_port.getText().toString();
                    WebSocketComunication.wsuri="ws://" + ip + ":" + puerto;
                    if(WebSocketComunication.isOnlineInternet()){
                        WebSocketComunication.startConnectionWebsocket();//Metodo para iniciar conexion Websocket
                        WebSocketComunication.banderaCerrarSesion = false;
                        pk_Loginl=null;
                        try{
                            pk_Loginl= ProgressDialog.show(context, res.getString(R.string.str_sistema),res.getString(R.string.str_conectando), false, false);
                        }catch (Exception e){

                        }
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        //start();
                                        if(pk_Loginl!=null){
                                            try {
                                                pk_Loginl.dismiss();
                                            }catch (Exception e){

                                            }
                                        }
                                        if (webSocketConnection.isConnected()) {
                                            ejecutar_evento();
                                        }else{
                                            if(context!=null) {
                                                Utils.alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_no_conexion_servidor), context);
                                            }
                                        }
                                    }}, 6000
                        );
                    } else {
                        String msgToasts=res.getString(R.string.sin_conexion);
                        Toast.makeText(context, msgToasts, Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
    }

    private void ejecutar_evento() {
        user = txt_user.getText().toString();
        password = txt_password.getText().toString();

        if (user.equals("") || password.equals(""))
            Utils.alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_usuario_incorrecto), context);
        else {
            pk_Login=null;
            try{
                pk_Login= ProgressDialog.show(context, res.getString(R.string.str_sistema),res.getString(R.string.str_iniciando_sesion), false, false);
            }catch (Exception e){

            }
            enviarMensajeLogin();
            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(pk_Login!=null){
                            try {
                                pk_Login.dismiss();
                            }catch (Exception e){

                            }
                        }
                        if(msjLogin==true){
                            if(WebSocketComunication.valida_login==1) {
                                if(context!=null) {
                                    Utils.alertDialog(res.getString(R.string.str_alerta), res.getString(R.string.str_no_conexion_datos), context);
                                }
                            }
                        }else {
                            msjLogin = true;
                        }
                    }}, 20000
            );
        }
    }

    public static LoginActivity getMyInstance() {
        return myInstanceL;
    }

    public void enviarMensajeLogin() {
        jsonConsultaLogin = new JSONObject();
        String versionAPK=res.getString(R.string.str_version);
        try {
            jsonConsultaLogin.put("tipo", "login");
            jsonConsultaLogin.put("nombre", user);
            jsonConsultaLogin.put("contrasenia", password);
            jsonConsultaLogin.put("versionAPK", versionAPK);
            if (webSocketConnection.isConnected()) {
                WebSocketComunication.valida_login = 1;
                webSocketConnection.sendMessage(jsonConsultaLogin.toString());
                WebSocketComunication.cosumoDatos(jsonConsultaLogin.toString());
                Log.i("LOGIN",jsonConsultaLogin.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void recibirMensajeLogin(String message) {
        JSONObject messageRecive = null;
        try {
            messageRecive = new JSONObject(message);
            String status = messageRecive.getJSONObject("error").getString("status");
            if (status.compareToIgnoreCase("0") == 0) {
                String tipo = messageRecive.getString("tipo");
                if (tipo.compareToIgnoreCase("loginRespuesta") == 0) {
                    String uuid = messageRecive.getString("uuid");
                    String token = messageRecive.getString("token");
                    String nombreUsuario = messageRecive.getString("nombre");
                    String timeout =  (messageRecive.has("timeout"))? messageRecive.getString("timeout") : "60000";
                    String grupoId = "xxxxx";
                    String urlStream = (messageRecive.has("urlStream"))? messageRecive.getString("urlStream") : "rtmp://198.211.109.176:1935/live/";
                    msjLogin=false;
                    //Crear sesión Usuario
                    session = new SessionManager(context.getApplicationContext());
                    String valida_grupos = (messageRecive.has("grupos"))? messageRecive.getJSONArray("grupos").toString() : "no_hay";
                    if(valida_grupos.compareToIgnoreCase("no_hay")!=0) {
                        JSONArray ja_data = messageRecive.getJSONArray("grupos");
                        String valida_permiso = (messageRecive.has("permisos")) ? messageRecive.getJSONObject("permisos").toString() : "no_hay";
                        boolean si_ptt = true;
                        boolean si_chat = true;
                        String reservable="no_hay";
                        if (valida_permiso.compareToIgnoreCase("no_hay") != 0) {
                            si_ptt = messageRecive.getJSONObject("permisos").getBoolean("ptt");
                            si_chat = messageRecive.getJSONObject("permisos").getBoolean("texto");
                            reservable = (messageRecive.getJSONObject("permisos").has("reservable")) ? messageRecive.getJSONObject("permisos").getString("reservable").toString() : "no_hay";
                        }
                        session.createLoginSession(user, uuid, token, ja_data.toString(), nombreUsuario, grupoId, si_ptt, si_chat, "", ip, puerto, reservable, timeout, urlStream);
                        WebSocketComunication.valida_login = 2;
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        if (pk_Login != null) {
                            pk_Login.dismiss();
                        }
                        activity.finish();
                        context=null;
                    }else{
                        if (pk_Login != null)
                            pk_Login.dismiss();
                        Toast toast = Toast.makeText(context,context.getResources().getString(R.string.str_sin_grupo), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            } else {
                if (pk_Login != null) {
                    pk_Login.dismiss();
                }
                WebSocketComunication.valida_login = 1;
                String msj = messageRecive.getJSONObject("error").getString("descripcion");
                String sts = messageRecive.getJSONObject("error").getString("status");
                Utils.alertDialog(context.getResources().getString(R.string.str_sistema), sts+", "+msj, context);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.i("onDestroy", "onDestroy");
        super.onDestroy();
        if(session !=null) {
            if (session.isLoggedIn() == false) {
                if (WebSocketComunication.serviceWebsocket != null) {
                    WebSocketComunication.serviceWebsocket.stopForeground(true);
                    WebSocketComunication.serviceWebsocket.stopSelf();
                }
                System.exit(0);
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}
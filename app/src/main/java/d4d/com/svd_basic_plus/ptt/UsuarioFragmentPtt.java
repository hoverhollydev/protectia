package d4d.com.svd_basic_plus.ptt;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import d4d.com.svd_basic_plus.R;
import d4d.com.svd_basic_plus.chat.ChatActivity;
import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.location.LocationActivity;
import d4d.com.svd_basic_plus.utils.SessionManager;

import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.webSocketConnection;

public class UsuarioFragmentPtt extends Fragment {
    private Resources res;
    public static Context context;
    private static Activity activity;
    //Usuario
    private static SessionManager session;
    //public static TextView txt_numero_conectados;
    public static TextView txt_senial, txt_ping;
    public static ImageView img_desconectados, img_ptt_reservado, img_chat, img_location;
    public static TextView txt_tiempo_reservado;
    public static boolean estado_usuario_conexion=true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();
        session = new SessionManager(context);
        res = getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmento_usuario, container, false);
        txt_senial = v.findViewById(R.id.txt_senial);
        txt_ping = v.findViewById(R.id.txt_ping);
        //txt_numero_conectados = v.findViewById(R.id.txt_numero_conectados);
        img_desconectados = v.findViewById(R.id.img_usu_todos);
        img_ptt_reservado = v.findViewById(R.id.img_ptt_reservado);
        img_chat = v.findViewById(R.id.img_chat);
        img_location = v.findViewById(R.id.img_location);
        txt_tiempo_reservado  =  v.findViewById(R.id.txt_tiempo_reservado);
        int reserv= session.getReservable();
        if(reserv!=0) {
            img_ptt_reservado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    img_ptt_reservado.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.green)));
                    txt_tiempo_reservado.setTextColor(ContextCompat.getColor(context,R.color.green));
                    enviarReservarPTT();
                }
            });
        }else{
            txt_tiempo_reservado.setVisibility(View.INVISIBLE);
            img_ptt_reservado.setVisibility(View.INVISIBLE);
        }
       /* txt_numero_conectados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                estado_usuario_conexion=true;
                txt_numero_conectados.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.green)));
                Intent intent = new Intent(getActivity(), ListaUsuariosActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });*/
        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_chat.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.green)));
                estado_usuario_conexion=false;
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });
        img_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_location.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.green)));
                estado_usuario_conexion=false;
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                startActivity(intent);
            }
        });
        img_desconectados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_desconectados.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.green)));
                estado_usuario_conexion=false;
                Intent intent = new Intent(getActivity(), ListaUsuariosActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    public static void setTiempoResevado(final int segundos){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UsuarioFragmentPtt.txt_tiempo_reservado.setText(segundos+"");
            }
        });
    }

    public static void enviarReservarPTT() {
        if(WebSocketComunication.isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "reservar");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("grupo", session.getGrupoId());
                json.put("reservar", "true");
                if (webSocketConnection.isConnected()&& session.isLoggedIn()==true) {
                    webSocketConnection.sendMessage(json.toString());
                    WebSocketComunication.cosumoDatos(json.toString());
                    Log.i("Mensaje envia RESERVA",json.toString());
                }else{
                    if(context!=null) {
                        String msgToasts=context.getResources().getString(R.string.str_no_conexion_servidor);
                        Toast.makeText(context, msgToasts, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts=context.getResources().getString(R.string.sin_conexion);
            Toast.makeText(context, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }

    /*public static void cargarNumeroConectados(){
        txt_numero_conectados.setText(session.getNumUsuConectados());
    }*/

    public static void cargarIntensidadSenial(String value){
        txt_senial.setText(value);
    }

    public static void cargarTiempoRetraso(String value){
        txt_ping.setText(value);
    }

    public static UsuarioFragmentPtt newInstance() {
        UsuarioFragmentPtt fragment = new UsuarioFragmentPtt();
        return fragment;
    }
}
package d4d.com.svd_basic_plus.ptt;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import d4d.com.svd_basic_plus.R;
import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.utils.SessionManager;
import d4d.com.svd_basic_plus.utils.Utils;

/**
 * Created by jp_leon on 15/03/2017.
 */

public class ListaUsuariosActivity extends AppCompatActivity {
    private Resources res;
    private static ListView listaUsuarios;
    private static ArrayList<UsuarioDetalle> array_items;
    public static Context contextListaUsuarios;
    private static ListaUsuariosActivity myInstanceUsu;
    //Usuario
    private static SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_usuarios);
        contextListaUsuarios = this;
        myInstanceUsu = ListaUsuariosActivity.this;
        res = getResources();
        WebSocketComunication.valida_login=2;
        UsuarioFragmentPtt.img_desconectados.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(contextListaUsuarios,R.color.white)));
       // UsuarioFragmentPtt.txt_numero_conectados.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(contextListaUsuarios,R.color.blue)));
        session = new SessionManager(contextListaUsuarios);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(session.getGrupoNombre());
        listaUsuarios=findViewById(R.id.lista_usuarios);
        WebSocketComunication.enviarMensajeListaMiembros();
    }

    static ArrayList<UsuarioDetalle> usuarioItems=null;
    private static boolean si_ptt = false;
    private static boolean si_chat = false;
    private static boolean si_pttActual = false;
    private static boolean si_chatActual  = false;
    private static int indicador =0;
    private static UsuarioAdapter adapter;
    public static void cargarListaUsuarios(){
        final String listaGrupos=session.getMiembrosGrupo();
        si_ptt = false;
        si_chat = false;
        si_pttActual = false;
        si_chatActual  = false;
        indicador =0;
        try {
            usuarioItems = new ArrayList<>();
            JSONObject msjReciveServer = new JSONObject(listaGrupos);
            JSONArray ja_data;
            boolean estado;
            ja_data = msjReciveServer.getJSONArray("miembros");
            UsuarioFragmentPtt.estado_usuario_conexion=true;
            for(int i=0; i<ja_data.length(); i++){
                JSONObject item=new JSONObject(ja_data.getString(i));
                UsuarioDetalle udr = new UsuarioDetalle();
                estado=item.getBoolean("connectado");
                String ts= item.getString("ts");
                if(estado==UsuarioFragmentPtt.estado_usuario_conexion) {
                    udr.setUsuarioNombre(item.getString("nombre"));
                    udr.setEstado(estado);
                    if(ts.compareToIgnoreCase("0")==0) {
                        udr.setDescripcionEstado("                 -----");
                    }else{
                        udr.setDescripcionEstado(Utils.getTimestampToDate(ts));
                    }
                    si_ptt = item.getJSONObject("permisos").getBoolean("ptt");
                    si_chat = item.getJSONObject("permisos").getBoolean("texto");
                    udr.setSiPtt(si_ptt);
                    udr.setSiChat(si_chat);
                    if(session.getUuid().compareToIgnoreCase(item.getString("uuid"))==0);{
                        si_pttActual=si_ptt;
                        si_chatActual=si_chat;
                        indicador=1;
                    }
                    usuarioItems.add(udr);
                }
                if(indicador==1){
                    session.setKeySiPtt(si_pttActual);
                    session.setKeySiChat(si_chatActual);
                }
            }

            UsuarioFragmentPtt.estado_usuario_conexion=false;
            for(int i=0; i<ja_data.length(); i++){
                JSONObject item=new JSONObject(ja_data.getString(i));
                UsuarioDetalle udr = new UsuarioDetalle();
                estado=item.getBoolean("connectado");
                String ts= item.getString("ts");
                if(estado==UsuarioFragmentPtt.estado_usuario_conexion) {
                    udr.setUsuarioNombre(item.getString("nombre"));
                    udr.setEstado(estado);
                    if(ts.compareToIgnoreCase("0")==0) {
                        udr.setDescripcionEstado("                 -----");
                    }else{
                        udr.setDescripcionEstado(Utils.getTimestampToDate(ts));
                    }
                    si_ptt = item.getJSONObject("permisos").getBoolean("ptt");
                    si_chat = item.getJSONObject("permisos").getBoolean("texto");
                    udr.setSiPtt(si_ptt);
                    udr.setSiChat(si_chat);
                    if(session.getUuid().compareToIgnoreCase(item.getString("uuid"))==0);{
                        si_pttActual=si_ptt;
                        si_chatActual=si_chat;
                        indicador=1;
                    }
                    usuarioItems.add(udr);
                }
                if(indicador==1){
                    session.setKeySiPtt(si_pttActual);
                    session.setKeySiChat(si_chatActual);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        adapter = new UsuarioAdapter(myInstanceUsu, usuarioItems);
        listaUsuarios.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                WebSocketComunication.enviarIdGrupo();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
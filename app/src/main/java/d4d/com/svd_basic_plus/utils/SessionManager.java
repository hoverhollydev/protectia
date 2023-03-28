package d4d.com.svd_basic_plus.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import d4d.com.svd_basic_plus.SplashActivity;
import d4d.com.svd_basic_plus.comunication.WebSocketComunication;

import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.banderaCerrarSesion;

/**
 * Created by JuanPablo on 06/03/2016.
 */
public class SessionManager {

    public static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Data4DecisionPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER = "user";
    public static final String KEY_UUID = "uuid";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_DETALLE_GRUPO = "detalle_grupo";
    public static final String KEY_NOMBRE_USUARIO = "nombreUsuario";
    public static final String KEY_GRUPO_ID = "grupoId";
    public static final String KEY_GRUPO_NOMBRE = "grupoNombre";
    public static final String KEY_SI_PTT = "si_ptt";
    public static final String KEY_SI_CHAT = "si_chat";
    public static final String KEY_MIEMBROS_GRUPO = "mienbros_grupo";
    public static final String KEY_IP = "ip_dominio";
    public static final String KEY_PUERTO = "puerto";
    public static final String NUM_USU_CONECTADOS = "usu_conectados";
    public static final String KEY_RESERVABLE = "reservable";
    public static final String KEY_TIMEOUT_PTT = "timeoutptt";
    public static final String KEY_USER_CONSOLE = "userConsola";
    public static final String KEY_VALIDA = "validaVerificacion";
    public static final String KEY_TIEMPO = "tiempo";
    public static final String KEY_URL_STREAM = "utlStream";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public void createLoginSession(String user, String uuid, String token, String detalleGrupo,
                                   String nombreUsuario, String grupoId, boolean si_ptt,
                                   boolean si_chat, String miembrosGrupos, String ip, String puerto,
                                   String reservable, String timeoutptt, String urlStream){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USER, user);
        editor.putString(KEY_UUID, uuid);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_DETALLE_GRUPO, detalleGrupo);
        editor.putString(KEY_NOMBRE_USUARIO, nombreUsuario);
        editor.putString(KEY_GRUPO_ID, grupoId);
        editor.putString(KEY_GRUPO_NOMBRE, "");
        editor.putBoolean(KEY_SI_PTT, si_ptt);
        editor.putBoolean(KEY_SI_CHAT, si_chat);
        editor.putString(KEY_MIEMBROS_GRUPO, miembrosGrupos);
        editor.putString(KEY_IP, ip);
        editor.putString(KEY_PUERTO, puerto);
        editor.putString(NUM_USU_CONECTADOS, "0");
        editor.putString(KEY_RESERVABLE, reservable);
        editor.putString(KEY_TIMEOUT_PTT, timeoutptt);
        editor.putString(KEY_USER_CONSOLE, "");
        editor.putBoolean(KEY_VALIDA, false);
        editor.putString(KEY_TIEMPO, "0");
        editor.putString(KEY_URL_STREAM, urlStream);
        editor.commit();
    }

    public static void setToken(String token){
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public static void setGrupoId(String grupoId){
        editor.putString(KEY_GRUPO_ID, grupoId);
        editor.commit();
    }

    public static void setGrupoNombre(String grupoNombre){
        editor.putString(KEY_GRUPO_NOMBRE, grupoNombre);
        editor.commit();
    }

    public static void setDetalleGrupo(String detalleGrupo){
        editor.putString(KEY_DETALLE_GRUPO, detalleGrupo);
        editor.commit();
    }

    public static void setKeySiPtt(boolean si_ptt){
        editor.putBoolean(KEY_SI_PTT, si_ptt);
        editor.commit();
    }

    public static void setKeySiChat(boolean si_chat){
        editor.putBoolean(KEY_SI_CHAT, si_chat);
        editor.commit();
    }

    public static void setMiembrosGrupo(String miembrosGrupo){
        editor.putString(KEY_MIEMBROS_GRUPO, miembrosGrupo);
        editor.commit();
    }

    public static void setIp(String ip){
        editor.putString(KEY_IP, ip);
        editor.commit();
    }

    public static void setPuerto(String puerto){
        editor.putString(KEY_PUERTO, puerto);
        editor.commit();
    }

    public static void setNumUsuConectados(String numero){
        editor.putString(NUM_USU_CONECTADOS, numero);
        editor.commit();
    }

    public static void setReservable(String reservable){
        editor.putString(KEY_RESERVABLE, reservable);
        editor.commit();
    }

    public static void setKeyTimeoutPtt(String valor){
        editor.putString(KEY_TIMEOUT_PTT, valor);
        editor.commit();
    }

    public static void setKeyUserConsole(String valor){
        editor.putString(KEY_USER_CONSOLE, valor);
        editor.commit();
    }

    public static void setValidaVerificacion(boolean valida){
        editor.putBoolean(KEY_VALIDA, valida);
        editor.commit();
    }

    public static void setTiempo(String tiempo){
        editor.putString(KEY_TIEMPO, tiempo);
        editor.commit();
    }

    public static void setUrlStream(String value){
        editor.putString(KEY_URL_STREAM, value);
        editor.commit();
    }

    public static String getUser(){
        return pref.getString(KEY_USER, null);
    }

    public static String getUuid(){
        return pref.getString(KEY_UUID, null);
    }

    public static String getToken(){
        return pref.getString(KEY_TOKEN, null);
    }

    public static String getDetalleGrupo(){
        return pref.getString(KEY_DETALLE_GRUPO, null);
    }

    public static String getNombreUsuario(){
        return pref.getString(KEY_NOMBRE_USUARIO, null);
    }

    public static String getGrupoId(){
        return pref.getString(KEY_GRUPO_ID, null);
    }

    public static String getGrupoNombre(){
        return pref.getString(KEY_GRUPO_NOMBRE, null);
    }

    public static boolean getKeySiPtt(){
        return pref.getBoolean(KEY_SI_PTT, true);
    }

    public static boolean getKeySiChat(){
        return pref.getBoolean(KEY_SI_CHAT, true);
    }

    public static String getMiembrosGrupo(){
        return pref.getString(KEY_MIEMBROS_GRUPO, null);
    }

    public static String getIp(){
        return pref.getString(KEY_IP, null);
    }

    public static String getPuerto(){
        return pref.getString(KEY_PUERTO, null);
    }

    public static String getNumUsuConectados(){
        return pref.getString(NUM_USU_CONECTADOS, null);
    }

    public static int getReservable(){
        String reservable = pref.getString(KEY_RESERVABLE, null);
        int reservableNum=0;
        if(reservable.compareToIgnoreCase("no_hay")!= 0){
            reservableNum = Integer.parseInt(reservable);
        }
        return reservableNum;
    }

    public static int getTimeoutPtt(){
        String cadena = pref.getString(KEY_TIMEOUT_PTT, null);
        int numero=0;
        if(cadena.compareToIgnoreCase("no_hay")!= 0){
            numero = Integer.parseInt(cadena);
        }
        return numero;
    }

    public static String getKeyUserConsole(){
        return pref.getString(KEY_USER_CONSOLE, null);
    }

    public static boolean getValidaVerificacion(){
        return pref.getBoolean(KEY_VALIDA, true);
    }

    public static int getTiempo(){
        String t=pref.getString(KEY_TIEMPO, null);
        int tiempo=Integer.parseInt(t);
        return tiempo;
    }

    public static String getKeyUrlStream(){
        return pref.getString(KEY_URL_STREAM, null);
    }

    public static void logoutUser(Context context) {
        banderaCerrarSesion=true;
        editor.clear();
        editor.commit();
        //Process.killProcess(Process.myPid());
        //System.exit(0);
        if(WebSocketComunication.sNotificationManager!=null) {
            WebSocketComunication.sNotificationManager.cancelAll();
        }
        Intent i = new Intent(context.getApplicationContext(), SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
package d4d.com.svd_basic_plus.comunication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.kontakt.sdk.android.ble.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.EddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleEddystoneListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSecureProfileListener;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleSpaceListener;
import com.kontakt.sdk.android.ble.spec.Acceleration;
import com.kontakt.sdk.android.ble.spec.KontaktTelemetry;
import com.kontakt.sdk.android.common.KontaktSDK;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.IEddystoneDevice;
import com.kontakt.sdk.android.common.profile.IEddystoneNamespace;
import com.kontakt.sdk.android.common.profile.ISecureProfile;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import d4d.com.svd_basic_plus.ExoPlayer2Activity;
import d4d.com.svd_basic_plus.LoginActivity;
import d4d.com.svd_basic_plus.MainActivity;
import d4d.com.svd_basic_plus.ActivityStreaming;
import d4d.com.svd_basic_plus.MainActivityContent;
import d4d.com.svd_basic_plus.R;
import d4d.com.svd_basic_plus.RegisterLocationActivity;
import d4d.com.svd_basic_plus.SplashActivity;
import d4d.com.svd_basic_plus.chat.ChatActivity;
import d4d.com.svd_basic_plus.oceanspace.SpacesFileRepository;
import d4d.com.svd_basic_plus.proyecto.ProyectoActivity;
import d4d.com.svd_basic_plus.ptt.ListaUsuariosActivity;
import d4d.com.svd_basic_plus.ptt.Tiempo;
import d4d.com.svd_basic_plus.ptt.UsuarioFragmentPtt;
import d4d.com.svd_basic_plus.historial.HistorialAudioFragment;
import d4d.com.svd_basic_plus.location.LocationActivity;
import d4d.com.svd_basic_plus.utils.SessionManager;
import d4d.com.svd_basic_plus.utils.Utils;
import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.interfaces.IWebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;
import io.crossbar.autobahn.websocket.types.WebSocketOptions;
import io.kuzzle.sdk.core.Collection;
import io.kuzzle.sdk.core.Document;
import io.kuzzle.sdk.core.Kuzzle;
import io.kuzzle.sdk.core.Options;
import io.kuzzle.sdk.core.Room;
import io.kuzzle.sdk.listeners.ResponseListener;
import io.kuzzle.sdk.responses.NotificationResponse;
import io.kuzzle.sdk.responses.SearchResult;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static d4d.com.svd_basic_plus.MainActivityContent.contextContent;

/**
 * Created by jp_leon on 24/10/2016.
 */

public class WebSocketComunication extends Service {
    public static String wsuri = "ws://170.150.120.6:6504"; //Server en la nube ws://191.232.186.70:5504 PERRO VPN
    private static final String TAG = "com.data4decision";
    private static String[] protocol = {"com-protocolo"};
    private static boolean isMC67 = false;
    public static WebSocketConnection webSocketConnection;
    public static IWebSocketConnectionHandler wsHandler;
    public static int valida_login = 0;
    private static int cnd = 0;
    public static boolean banderaCerrarSesion = false;
    public static Context contextWs;
    public static WebSocketComunication WebsocketA;
    private static Resources res;
    //Usuario
    public static SessionManager session;
    //Audio PTT
    static String ipServer="170.150.120.6";
    public static Uri soundPTTInicio;
    public static Uri soundPTTFin;
    public static Uri soundPTTOcupado;
    static MediaPlayer mp1;
    static MediaPlayer mp2;
    static MediaPlayer mp3;
    public static DatagramSocket socket;
    static AudioRecord recorder=null;
    private static int sampleRate = 8000 ; //16000, 44100 for music
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    //private static int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private static int minBufSize = 640;
    private static boolean transmisionUDP = true;
    static Handler handlerMp2; // declared before onCreate
    static Runnable myRunnableMp2;
    static Handler handlerMp3; // declared before onCreate
    static Runnable myRunnableMp3;
    static Handler handlerB; // declared before onCreate
    static Runnable myRunnableB;
    public static Service serviceWebsocket;
    private static ProgressDialog pk_conexion_websocket;
    private Thread workerThread = null;
    //Intensidad de la señal GSM 3g o 4g
    TelephonyManager mTelephonyManager;
    MyPhoneStateListener mPhoneStatelistener;
    int signalSupport = 0;
    private static Handler handlerMsj;
    private static void runOnUiThread(Runnable runnable) {
        handlerMsj.post(runnable);
    }
    //Bluetooth
    private static BluetoothLeScanner BLEScanner;
    private static BluetoothDevice BLEDevice;
    private static BluetoothGatt BLEGatt;
    private static BluetoothManager BLEManager;
    private static BluetoothAdapter BLEAdapter;
    private static ScanCallback mScanCallback;
    private static BroadcastReceiver mBondReceiver;
    private static Handler TextUpdateHandler = new Handler();
    private static boolean GetSwVersion = false;
    private static boolean GetBattLevel = false;
    private static boolean GetButtons = false;
    private static boolean Bonding = false;
    public static boolean Ready = false;
    private static String State = "IDLE";
    private static int apttasb_batt_level = -0x01;
    private static int apttasb_button_mask = 0x00;
    private static String apttasb_sw_version = "";
    private static String aptt_or_asb = "";
    private static String devName = "";
    private static String classicName = "";
    private static UUID CLIENT_CHAR_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");
    private static UUID AINA_SERV = UUID.fromString("127FACE1-CB21-11E5-93D0-0002A5D5C51B");
    private static UUID BATT_SERV = UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB");
    private static UUID SW_VERS = UUID.fromString("127FC0FF-CB21-11E5-93D0-0002A5D5C51B");
    private static UUID BUTTONS = UUID.fromString("127FBEEF-CB21-11E5-93D0-0002A5D5C51B");
    private static UUID LEDS = UUID.fromString("127FDEAD-CB21-11E5-93D0-0002A5D5C51B");
    private static UUID BATT_LEVEL = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");
    //_________________________________________________________________________________________________
    //Reproducir Audio
    AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
            sampleRate, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT, minBufSize,
            AudioTrack.MODE_STREAM);
    private static int port=50005;
    public static int bandera_no_microfono=0;
    public static int contadorToast=0;
    //_________________________________________________________________________________________________
    //Obtiene la ubicación del dispositivo mediante el GPS MapBox
    private static LocationEngine locationEngine;
    private static String speed;
    public static String latitud = "-2.2071271"; //Coordenadas por default si no hay GPS en playa Chocoltera Salinas
    public static String longitud = "-81.0134809";
    public static String aux_latitud = "-2.2071271";
    public static String aux_longitud = "-81.0134809";
    // Variables needed to listen to location updates
    private static WebSocketComunicationLocationCallback callback = new WebSocketComunicationLocationCallback();
    private static long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    public static int timeSendTelemetria=10000;
    public static int cont_location=5;

    //Tipo de Conexión Datos o Wifi
    public static String type="mobile";
    public static int desconectadoWebS=0;
    //Kuzzle
    private static Kuzzle kuzzle;
    //Estado de Batería
    private static int levelaux=-1;
    //Intensidad de la señal GSM 3g o 4g
    public static String banda="";
    public static String senal="";
    public static String extra="";
    public static String manufactura ="";
    public static String modelo="";
    public static String version_app ="";
    public static String imei ="";
    public static String serial_chip="";
    public static String operador_celular="";
    public static String ping_time_response="";
    //Ptt
    static int conteoMensaje=0;
    public static boolean banderaPermiso=false;
    public static boolean banderaPresionado=false;
    public static boolean banderaLevantado=false;
    public static String grupoPermiso="grupo";
    public static NotificationManager sNotificationManager = null;
    //Beacons Kontakt
    private static ProximityManager proximityManager;

    public static MediaSession mSession;


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "onBind INICIADOO");
        return null;
    }

    @Override
    public void onCreate() {
    }

    public static void connect_kuzzle(){
        try {
            kuzzle = new Kuzzle("192.241.148.149");
            //kuzzle.lo
            kuzzle.connect();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    public static void starMediaButo(){
        mSession = new MediaSession(contextWs, "TAG");
        mSession.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(final Intent mediaButtonIntent) {
                //Toast.makeText(contextWs, "key events captured", Toast.LENGTH_LONG).show();
                //Log.i("Sample", "PTTon: " + mediaButtonIntent.getAction());
                /*Log.i("Sample", "PTTon: " + mediaButtonIntent.getDataString());
                Log.i("Sample", "PTTon: " + mediaButtonIntent.getPackage());
                Log.i("Sample", "PTTon: " + mediaButtonIntent.getType());
                Log.i("Sample", "PTTon: " + mediaButtonIntent.getCategories());*/

                contadorPTT++;
                if(contadorPTT==2) {
                    //contadorPTT=1;
                    Log.i("Sample", "PTTon: DOWN" + mediaButtonIntent.getAction());
                    if(contextContent!=null) {
                        MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());//Aqui
                    }
                    if (banderaPermiso == false && banderaPresionado == false) {
                        //Envia mensaje PTT y espera confirmación de MsgBroker
                        enviarPTT();
                        banderaLevantado = false;
                        if (contextContent != null) {
                            MainActivityContent.btn_ptt_master.setClickable(false);
                            MainActivityContent.btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(contextWs.getResources().getColor(R.color.green)));
                        }
                    }
                }else{
                    if(contadorPTT>=4) {
                        if (banderaLevantado == false) {
                            Log.i("Sample", "PTTon: UP" + mediaButtonIntent.getAction());
                            banderaLevantado = true;
                            terminarPTT();
                            Handler handler = new Handler();
                            //Runnable myRunnable = () -> {
                                contadorPTT = 0;
                                banderaPresionado = false;
                                banderaLevantado = false;
                                if (contextContent != null) {
                                    MainActivityContent.btn_ptt_master.setClickable(true);
                                    MainActivityContent.btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(contextWs.getResources().getColor(R.color.blue_semi_transparent)));
                                }
                            //};
                            //handler.postDelayed(myRunnable, 1000);
                        }
                    }
                }



                return super.onMediaButtonEvent(mediaButtonIntent);
            }
            /*@Override
            boolean onKeyDown(int keyCode, KeyEvent event) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    return super.onKeyDown(keyCode, event);
                }
                switch (keyCode) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        yourMediaController.dispatchMediaButtonEvent(event);
                        return true;
                }
                return super.onKeyDown(keyCode, event);
            }*/
        });


        /*mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);




        PlaybackState state = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackState.STATE_STOPPED, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0)
                .build();
        mSession.setPlaybackState(state);*/


        //mSession.setActive(true);

    }

    //Beacons Kontakt
    public void startKontakt(){
        KontaktSDK.initialize("fcNbWEYZhpdHWhEXjnTgmoGRZTMznjIU");

        proximityManager = ProximityManagerFactory.create(contextWs);

        setupProximityManager();

        proximityManager.setIBeaconListener(createIBeaconListener());
        proximityManager.setEddystoneListener(createEddystoneListener());


        /*proximityManager.setSpaceListener(new SpaceListener() {
            @Override
            public void onRegionEntered(IBeaconRegion region) {
                //IBeacon region has been entered
                Log.i("Sample", "onRegionEntered: " + region.toString());
                Log.i("Sample", "onRegionEntered: " + region.getIdentifier());
            }

            @Override
            public void onRegionAbandoned(IBeaconRegion region) {
                //IBeacon region has been abandoned
                Log.i("Sample", "onRegionAbandoned: " + region.toString());
            }

            @Override
            public void onNamespaceEntered(IEddystoneNamespace namespace) {
                //Eddystone namespace has been entered
                Log.i("Sample", "onNamespaceEntered: " + namespace.toString());
                Log.i("Sample", "onNamespaceEntered: " + namespace.getIdentifier());
                Log.i("Sample", "onNamespaceEntered: " + namespace.getIdentifier());
            }

            @Override
            public void onNamespaceAbandoned(IEddystoneNamespace namespace) {
                //Eddystone namespace has been abandoned
                Log.i("Sample", "onNamespaceAbandoned: " + namespace.toString());
            }
        });*/


        proximityManager.setSpaceListener(new SimpleSpaceListener() {
            @Override
            public void onRegionEntered(IBeaconRegion region) {
                //IBeacon region has been entered
                Log.i("Sample", "onRegionEntered: " + region.toString());
                Log.i("Sample", "onRegionEntered: " + region.getIdentifier());
            }

            @Override
            public void onNamespaceEntered(IEddystoneNamespace namespace) {
                //Eddystone namespace has been entered
                //Log.i("Sample", "onNamespaceEntered: " + namespace.toString());
                //Log.i("Sample", "onNamespaceEntered: " + namespace.getIdentifier());
                //Log.i("Sample", "onNamespaceEntered: " + namespace.getInstanceId());
                //Log.i("Sample", "onNamespaceEntered: " + namespace.getNamespace());
            }
        });



        //Setting up Secure Profile listener
        //proximityManager.setSecureProfileListener(createSecureProfileListener());


        proximityManager.setSecureProfileListener(new SimpleSecureProfileListener() {
            @Override
            public void onProfileDiscovered(ISecureProfile profile) {
                // Extract telemetry
                if(profile.getUniqueId()!=null) {
                    Log.i("IBeacon", "IBeacon Detectado: " + profile.toString());
                    if(RegisterLocationActivity.context!=null) {
                        RegisterLocationActivity.sendDataBeacon(profile.getUniqueId(), profile.toString(), profile.getRssi()+"");
                    }
                }

                KontaktTelemetry telemetry = profile.getTelemetry();
                if (telemetry != null) {
                    //Log.i(TAG, "Perfil Detectado telemetry: " + profile.getTelemetry().toString());
                    // Get acceleration, temperature, light level and device time
                    Acceleration acceleration = telemetry.getAcceleration();
                    int temperature = telemetry.getTemperature();
                    int lightLevel = telemetry.getLightSensor();
                    int deviceTime = telemetry.getTimestamp();
                }
            }
        });


        startScanning();


    }

    private void setupProximityManager() {
        //proximityManager = ProximityManagerFactory.create(this);
        //Configure proximity manager basic options

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            proximityManager.configuration()
                    //Using ranging for continuous scanning or MONITORING for scanning with intervals
                    .scanPeriod(ScanPeriod.RANGING)
                    //Using BALANCED for best performance/battery ratio
                    .scanMode(ScanMode.BALANCED)
                    .forceScanConfiguration(ForceScanConfiguration.DISABLED)
                    //OnDeviceUpdate callback will be received with 5 seconds interval millisecons
                    .deviceUpdateCallbackInterval(3000);
        } else {
            proximityManager.configuration()
                    //Using ranging for continuous scanning or MONITORING for scanning with intervals
                    .scanPeriod(ScanPeriod.RANGING)
                    //Using BALANCED for best performance/battery ratio
                    .scanMode(ScanMode.BALANCED)
                    //OnDeviceUpdate callback will be received with 5 seconds interval
                    .deviceUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(1));
        }



        /*proximityManager.configuration()
                .scanMode(ScanMode.BALANCED)
                .scanPeriod(ScanPeriod.RANGING)
                .activityCheckConfiguration(ActivityCheckConfiguration.DISABLED)
                .forceScanConfiguration(ForceScanConfiguration.DISABLED)
                .deviceUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(5))
                .rssiCalculator(RssiCalculators.DEFAULT)
                .cacheFileName("Example")
                .resolveShuffledInterval(3)
                .monitoringEnabled(true)
                .monitoringSyncInterval(10)
                .eddystoneFrameTypes(Arrays.asList(EddystoneFrameType.UID, EddystoneFrameType.URL));*/

    }

    private void startScanning() {
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                proximityManager.startScanning();
            }
        });
    }

    private IBeaconListener createIBeaconListener() {
        return new SimpleIBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region) {
                //Log.i("IBeacon", "IBeacon Detectado: " + ibeacon.getUniqueId());

                if(ibeacon.getUniqueId()!=null) {
                    Log.i("IBeacon", "IBeacon Detectado: " + ibeacon.toString());
                    if(RegisterLocationActivity.context!=null) {
                        RegisterLocationActivity.sendDataBeacon(ibeacon.getUniqueId(), ibeacon.toString(), ibeacon.getRssi()+"");
                    }
                }
            }
        };
    }

    private EddystoneListener createEddystoneListener() {
        return new SimpleEddystoneListener() {
            @Override
            public void onEddystoneDiscovered(IEddystoneDevice eddystone, IEddystoneNamespace namespace) {


                //Log.i("Eddystone", "Eddystone Detectado: ID "+eddystone.getEid());
                //Log.i("Eddystone", "Eddystone Detectado: INSTANCEID "+eddystone.getInstanceId());
                //Log.i("Eddystone", "Eddystone Detectado: NAMESPACE "+eddystone.getNamespace());
//                Log.i("Eddystone", "Eddystone Detectado: TELEMETRIA "+eddystone.getTelemetry().toString());
                //Log.i("Eddystone", "Eddystone Detectado: TELEMETRIA ENCR "+eddystone.getEncryptedTelemetry());
                //if(eddystone.getInstanceId()!=null)
                   // Log.i("IBeacon", "IBeacon Detectado: "+ eddystone.toString());
            }
        };
    }



    /*private SecureProfileListener createSecureProfileListener() {
        return new SecureProfileListener() {
            @Override
            public void onProfileDiscovered(ISecureProfile iSecureProfile) {
                Log.i(TAG, "Perfil Detectado: " + iSecureProfile.toString());


                String datos_beacon=iSecureProfile.toString();
                String imei= Utils.getImei(getApplicationContext());

                String id_device = iSecureProfile.getUniqueId();
                String descripcion_device = "Timestamp:"+Utils.getCurrentTimeStamp()+", MacAddress:"+iSecureProfile.getMacAddress()+", "+
                        "Modelo:"+iSecureProfile.getModel()+", "+
                        "TxPower:"+iSecureProfile.getTxPower()+", "+
                        "Rssi:"+iSecureProfile.getRssi()+", "+
                        "Telemetría:"+iSecureProfile.getTelemetry()+", "+
                        "Batería:"+iSecureProfile.getBatteryLevel()+", ESTADO: DETECTADO";


                JSONObject json= new JSONObject();
                try {
                    json.put("tipo", "registro");
                    json.put("uuid_imei_movil", imei);
                    json.put("datos_beacon", datos_beacon);
                    json.put("estado", "onProfileDiscovered");
                    Log.i(TAG, "Perfil  json: " +json);

                    sendMessageKuzzle("protectia","checkup",json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // TelemetriaService.connectKuzzle("data","beacons","info_movil_beacon",json);
                //acumu=acumu+"Perfil Detectado: " + iSecureProfile.toString()+"\n";
                //log.setText(acumu);
            }

            @Override
            public void onProfilesUpdated(List<ISecureProfile> list) {
                //Log.i(TAG, "Perfil Actualizado: " + list.toString());
                //String json = new Gson().toJson(list);
                //JSONArray jsonArray = new JSONArray(list);



                String datos_beacon= list.get(0).toString();
                String id_device= list.get(0).getUniqueId();
                String imei= Utils.getImei(getApplicationContext());



                JSONObject json= new JSONObject();
                try {
                    json.put("tipo", "registro");
                    json.put("uuid_imei_movil", imei);
                    json.put("datos_beacon", datos_beacon);
                    json.put("estado", "onProfilesUpdated");
                    Log.i(TAG, "Perfil  json: " +json);

                    sendMessageKuzzle("protectia","checkup",json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



                //Log.i(TAG, "Perfil Detectado: " + ISecureProfile.toString());
                //log.setText("Perfil Actualizado: " + list.size());
                //acumu=acumu+ "Perfil Actualizado: " + list.size()+"\n";
                //log.setText(acumu);

            }

            @Override
            public void onProfileLost(ISecureProfile iSecureProfile) {
                Log.e(TAG, "Perfil Perdido: " + iSecureProfile.toString());
                //iSecureProfile.

                String datos_beacon=iSecureProfile.toString();
                String imei= Utils.getImei(getApplicationContext());

                String id_device = iSecureProfile.getUniqueId();
                String descripcion_device = "Timestamp:"+Utils.getCurrentTimeStamp()+", MacAddress:"+iSecureProfile.getMacAddress()+", "+
                        "Modelo:"+iSecureProfile.getModel()+", "+
                        "TxPower:"+iSecureProfile.getTxPower()+", "+
                        "Rssi:"+iSecureProfile.getRssi()+", "+
                        "Telemetría:"+iSecureProfile.getTelemetry()+", "+
                        "Batería:"+iSecureProfile.getBatteryLevel()+", ESTADO: PERDIDO";


                JSONObject json= new JSONObject();
                try {
                    json.put("tipo", "registro");
                    json.put("uuid_imei_movil", imei);
                    json.put("datos_beacon", datos_beacon);
                    json.put("estado", "onProfileLost");
                    Log.i(TAG, "Perfil  json: " +json);

                    //sendMessageKuzzle("protectia","checkup",json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }*/



    public static void sendMessageKuzzle(String index, String collection_name, JSONObject json_data){
        try {
            connect_kuzzle();
            //Create a document
            Collection collection = kuzzle.collection(collection_name, index);
            Document document = new Document(collection);
            document.setContent(json_data, true);

            cosumoDatos(json_data.toString());


            collection.createDocument(document, new ResponseListener<Document>() {
                @Override
                public void onSuccess(Document response) {
                    //Log.i("onSuccess", response+", createDocument");
                    //try {
                        /*CollectionMapping dataMapping = new CollectionMapping(collection);
                        JSONObject keyword = new JSONObject();
                        keyword.put("type", "keyword");
                        keyword.put("ignore_above", 256);

                        JSONObject fields = new JSONObject();
                        fields.put("keyword", keyword);
                        JSONObject mapping = new JSONObject();
                        mapping.put("fielddata", true);
                        mapping.put("type", "text");
                        mapping.put("fields", fields);

                        Log.i("MAPPING",mapping.toString());
                        dataMapping.set("uuid", mapping);
                        dataMapping.set("timestamp", mapping);*/
                        kuzzle.disconnect();
                    //} catch (JSONException e) {
                        //e.printStackTrace();
                    //}
                }
                @Override
                public void onError(JSONObject error) {
                    Log.e("onError", error+", createDocument");
                }
            });
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    public static void receiveMessageKuzzle(String index, String collection_name, String tipo, String group_id){
        try {
            connect_kuzzle();
            /*JSONObject body = new JSONObject()
                    .put("query",new JSONObject()
                            .put("bool", new JSONObject()
                                    .put("must",new JSONArray()
                                            .put(new JSONObject()
                                                    .put("term", new JSONObject()
                                                            .put("group_id",new JSONObject()
                                                                    .put("value", group_id)
                                                            )
                                                    )
                                            )
                                    )
                            )
                    );*/

            JSONObject body = new JSONObject()
                    //.put("aggs", new JSONObject()
                            //.put("by_group_id", new JSONObject()
                                    //.put("terms", new JSONObject()
                                            //.put("field", "group_id")
                                    //)
                                    .put("aggs", new JSONObject()
                                                    .put("by_uuid", new JSONObject()
                                                            .put("terms", new JSONObject()
                                                                    .put("field", "uuid")
                                                            )
                                                            .put("aggs", new JSONObject()
                                                                    .put("by_uuid_hits", new JSONObject()
                                                                            .put("top_hits", new JSONObject()
                                                                                    .put("sort", new JSONArray()
                                                                                            .put(new JSONObject()
                                                                                                    .put("timestamp",new JSONObject()
                                                                                                            .put("order","desc"))
                                                                                            )

                                                                                    )
                                                                                    .put("_source", new JSONObject()
                                                                                            .put("includes", new JSONArray()
                                                                                                    .put("uuid")
                                                                                                    .put("lati")
                                                                                                    .put("longi")
                                                                                                    .put("timestamp")
                                                                                                    .put("name_user")
                                                                                                    .put("group_id")
                                                                                                    //.put("speed")
                                                                                            )

                                                                                    )
                                                                                    .put("size", 1)
                                                                            )

                                                                    )
                                                            )
                                                    )
                                    //)
                           // )
                    );
            Log.i("searchCriteria", "kuzzle search, "+body.toString());
            Options options = new Options();
            options.setFrom((long) 0);
            options.setSize((long) 100);
            kuzzle.collection(collection_name, index)
                    .search(body , options, new ResponseListener<SearchResult>() {
                        @Override
                        public void onSuccess(SearchResult response) {
                            //Do something with the matching documents

                            for (Document doc : response.getDocuments()) {
                                // Get documents
                                //Log.i("onSuccess", "kuzzle search, "+doc.getContent()+"  ");

                            }

                            try {
                                //Log.i("onSuccess", "kuzzle search, "+response.getTotal()+"  ");
                                //Log.i("onSuccess", "kuzzle search, "+response.getAggregations()+"  ");
                                //Log.i("onSuccess", "kuzzle search, "+response.getFilters()+"  ");
                                //Log.i("onSuccess", "kuzzle search, "+response.getDocuments().size()+"  ");
                                //Log.i("onSuccess", "kuzzle search, "+response.getCollection().getHeaders()+"  ");
                                JSONArray buckets = response.getAggregations().getJSONObject("by_uuid").getJSONArray("buckets");
                                Handler handler = new Handler(Looper.getMainLooper());
                                Runnable myRunnable = () -> {
                                        try {
                                            LocationActivity.marcadores(buckets);
                                            cosumoDatos(buckets.toString());
                                            kuzzle.disconnect();
                                        } catch (Exception e) {
                                            Log.i(TAG, e.toString());
                                        }
                                };
                                handler.sendEmptyMessage(0);
                                handler.postDelayed(myRunnable, 0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(JSONObject error) {
                            //handleError(error);
                            Log.e("onError", error.toString());
                        }
                    });
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    public static void subscribeKuzzle(String index, String collection_name){
        try {
            connect_kuzzle();

            /*JSONObject filter = new JSONObject()
                    .put("query",new JSONObject()
                            .put("bool", new JSONObject()
                                    .put("must",new JSONArray()
                                            .put(new JSONObject()
                                                    .put("term", new JSONObject()
                                                            .put("group_id",new JSONObject()
                                                                    .put("value", "grp4dBTest1")
                                                            )
                                                    )
                                            )
                                    )
                            )
                    );*/

            JSONObject filter = new JSONObject();

            kuzzle.collection(collection_name, index)
                    .subscribe(filter, new ResponseListener<NotificationResponse>() {
                        @Override
                        public void onSuccess(NotificationResponse object) {
                            // called each time a new notification on this filter is received
                            // check the Room/Notifications section of this documentation
                            // to get notification examples
                            Log.i("onSuccess", "Suscribe1 "+object.getResult().toString());
                            cosumoDatos(object.getResult().toString());
                            //Log.i("onSuccess", "Suscribe1 "+object.getDocument().getContent().toString());

                        }
                        @Override
                        public void onError(JSONObject error) {
                            // Handle error
                            Log.e("onError", "Suscribe1 "+error.toString());
                        }
                    })
                    .onDone(new ResponseListener<Room>() {
                        @Override
                        public void onSuccess(Room response) {
                            // Handle subscription success
                            Log.i("onSuccess", "Suscribe "+response.getCollection());
                            cosumoDatos(response.getCollection());
                            //Log.i("onSuccess", "Suscribe "+response.getFilters().toString());
                            //Log.i("onSuccess", "Suscribe "+response.getHeaders().toString());
                            //Log.i("onSuccess", "Suscribe "+response.getSubscribeListener());
                        }
                        @Override
                        public void onError(JSONObject error) {
                            // Handle subscription error
                            Log.e("onError", "Suscribe "+error.toString());
                        }
                    });
        } catch (Exception e) {
            Log.e("Error", "Suscribe "+e.toString());
        }
    }

    private static Symbol symbolUser;
    @SuppressLint("MissingPermission")
    private static void getLocationMapBox() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(contextWs);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, contextWs.getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    private static class WebSocketComunicationLocationCallback implements LocationEngineCallback<LocationEngineResult> {
        @Override
        public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                // Pass the new location to the Maps SDK's LocationComponent
                if (result.getLastLocation() != null) {
                    speed=result.getLastLocation().getSpeed()+"";
                    aux_latitud = result.getLastLocation().getLatitude()+"";
                    aux_longitud = result.getLastLocation().getLongitude()+"";
                    if(latitud.compareToIgnoreCase(aux_latitud)!=0 || longitud.compareToIgnoreCase(aux_longitud)!=0){
                        latitud=aux_latitud;
                        longitud=aux_longitud;
                        cont_location=6;
                    }
                    //Log.i("MAPBOX", "("+latitud+" "+longitud+") velocidad="+speed+" meters/second");
                    if(speed.compareToIgnoreCase("0.0")!=0){
                        cont_location=6;
                    }
                }


            // Pass the new location to the Maps SDK's LocationComponent
            //if (activity.mapboxMap != null && result.getLastLocation() != null) {
                //activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
            if(LocationActivity.contextLA!=null) {
                if (LocationActivity.symbolManager != null) {
                    if (symbolUser != null) {
                        LocationActivity.symbolManager.delete(symbolUser);
                    }
                    symbolUser = LocationActivity.symbolManager.create(new SymbolOptions()
                            .withLatLng(new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude()))
                            .withIconImage("my-marker")
                            //set the below attributes according to your requirements
                            //.withIconSize(1.5f)
                            //.withIconOffset(new Float[] {0f,-0.1f})
                            //.withZIndex(10)
                            .withTextField(session.getNombreUsuario())
                            .withTextJustify(Utils.getCurrentTimeStamp())
                            .withTextTransform(session.getUuid())
                            .withTextAnchor("top")
                            .withIconSize(1.1f)
                            .withTextHaloColor("rgba(255, 255, 255, 100)")
                            .withTextHaloWidth(3.0f)
                            .withTextSize(15.5f)
                            .withTextOffset(new Float[]{0f, 0.4f})
                            .withDraggable(false)
                    );
                }
            }

        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
        }
    }

    /*@SuppressLint("MissingPermission")
    public static void getLocationMapBox1() {
        //Mapbox.getInstance(contextWs, contextWs.getString(R.string.access_token));
        locationEngine= LocationEngineProvider.getBestLocationEngine(contextWs);
        if (ActivityCompat.checkSelfPermission(contextWs, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contextWs, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                if (location != null) {
                    // process last location
                    speed=result.getLastLocation().getSpeed()+"";
                    aux_latitud = result.getLastLocation().getLatitude()+"";
                    aux_longitud = result.getLastLocation().getLongitude()+"";
                    if(latitud.compareToIgnoreCase(aux_latitud)!=0 || longitud.compareToIgnoreCase(aux_longitud)!=0){
                        latitud=aux_latitud;
                        longitud=aux_longitud;
                        cont_location=6;
                    }
                    //Toast.makeText(contextWs,"("+latitud+" "+longitud+") velocidad="+speed+" meters/second",Toast.LENGTH_SHORT).show();
                    //Log.i("MAPBOX", "("+latitud+" "+longitud+") velocidad="+speed+" meters/second");
                    if(speed.compareToIgnoreCase("0.0")!=0){
                        cont_location=6;
                    }//else{
                        //cont_location++;
                    //}
                }
            }
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }*/

    BroadcastReceiver miBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("TAG", "ACTION : "+ intent.getAction());

            /*if (intent == null || !Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction()))
                return;

            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent == null || keyEvent.getAction() != KeyEvent.ACTION_DOWN) return;

            Log.i("getKeyCharacterMap",keyEvent.getKeyCharacterMap().getKeyboardType()+" ____");
            Log.i("getKeyCode",keyEvent.getKeyCode()+" ____");
            //Log.i("keyCode UP",keyCode+" ____");*/

            //com.motorolasolutions.iotservice.intent.action.KEEP_ALIVE_IOT_CONNECTION_ALARM
            //com.motorolasolutions.modemiotproxy.SYNC
            //com.motorolasolutions.iotservice.uploadCadence
            //com.motorolasolutions.ptt
            //android.intent.action.TIME_TICK

            switch (intent.getAction()){
                case "com.ainawireless.intent.action.PTT1_DOWN":
                    //botonPTTDown();
                    Log.i("BluetoohLE", "com.ainawireless.intent.action.PTT1_DOWN"+" ");
                    break;
                case "com.ainawireless.intent.action.PTT1_UP":
                    //botonPTTUp();
                    Log.i("BluetoohLE", "com.ainawireless.intent.action.PTT1_UP"+" ");
                    break;
                case "com.motorolasolutions.intent.action.ACTION_PTT_BUTTON_DOWN":
                    botonPTTDown();
                    break;
                case "com.motorolasolutions.intent.action.ACTION_PTT_BUTTON_UP":
                    botonPTTUp();
                    break;
                case "android.intent.action.PTT.down":
                    botonPTTDown();
                    break;
                case "android.intent.action.PTT.up":
                    botonPTTUp();
                    break;

                case "android.intent.action.SOS.down":
                    enviarMensajeAlerta();
                    break;
                case Intent.ACTION_HEADSET_PLUG:
                        int state = intent.getIntExtra("state", -1);
                        switch (state) {
                            case 0:
                                Log.d("HeadphoneMonitor", "Headset is unplugged");
                                //headphonesActive=false;
                                break;
                            case 1:
                                Log.d("HeadphoneMonitor", "Headset is plugged in");
                                //headphonesActive=true;
                                break;
                            default:
                                Log.d("HeadphoneMonitor", "I have no idea what the headset state is");
                                break;
                        }
                    break;
                case Intent.ACTION_SCREEN_ON:
                    KeyguardManager kManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                    boolean isLocked = kManager.inKeyguardRestrictedInputMode();
                    Log.i("TAG", "Screen ON "+isLocked+", "+ Intent.ACTION_SCREEN_ON);
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    Log.i("TAG", "Screen OFF");
                    //Intent intent1 = new Intent();
                    //intent1.setClass(context, SplashActivity.class);
                    //startActivity(intent1);
                    break;
                case "com.seuic.scanner.scankey":
                    if(contadorPTT==0) {
                        contadorPTT=1;
                        if(contextContent!=null) {
                            MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());//Aqui
                        }
                        if (banderaPermiso == false && banderaPresionado == false) {
                            //Envia mensaje PTT y espera confirmación de MsgBroker
                            enviarPTT();
                            banderaLevantado = false;
                            if (contextContent != null) {
                                MainActivityContent.btn_ptt_master.setClickable(false);
                                MainActivityContent.btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(contextWs.getResources().getColor(R.color.green)));
                            }
                        }
                    }else{
                        if(contadorPTT==1) {
                            if (banderaLevantado == false) {
                                banderaLevantado = true;
                                terminarPTT();
                                Handler handler = new Handler();
                                Runnable myRunnable = () -> {
                                        contadorPTT = 0;
                                        banderaPresionado = false;
                                        banderaLevantado = false;
                                        if (contextContent != null) {
                                            MainActivityContent.btn_ptt_master.setClickable(true);
                                            MainActivityContent.btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(contextWs.getResources().getColor(R.color.blue_semi_transparent)));
                                        }
                                };
                                handler.postDelayed(myRunnable, 1000);
                            }
                        }
                    }
                    break;
                case Intent.ACTION_BATTERY_CHANGED:
                    int level = intent.getIntExtra("level", -1);
                    levelaux = level;
                    break;
            }
        }
    };


    private void botonPTTDown(){
        //Envia mensaje PTT y espera confirmación de MsgBroker
        if(contadorPTT==0) {
            contadorPTT=1;
            if(contextContent!=null) {
                MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());//Aqui
            }
            if (banderaPermiso == false && banderaPresionado == false) {
                //Envia mensaje PTT y espera confirmación de MsgBroker
                enviarPTT();
                banderaLevantado = false;
                if (contextContent != null) {
                    MainActivityContent.btn_ptt_master.setClickable(false);
                    MainActivityContent.btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(contextWs.getResources().getColor(R.color.green)));
                }
            }
        }
    }

    private void botonPTTUp(){
        if(contadorPTT==1) {
            if (banderaLevantado == false) {
                banderaLevantado = true;
                terminarPTT();
                Handler handler = new Handler();
                Runnable myRunnable = () -> {
                    contadorPTT = 0;
                    banderaPresionado = false;
                    banderaLevantado = false;
                    if (contextContent != null) {
                        MainActivityContent.btn_ptt_master.setClickable(true);
                        MainActivityContent.btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(contextWs.getResources().getColor(R.color.blue_semi_transparent)));
                    }
                };
                handler.postDelayed(myRunnable, 1000);
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "SERVICIOINICIADO");
        contextWs=getApplicationContext();
        WebsocketA=WebSocketComunication.this;
        handlerMsj=new Handler();
        //TextUpdateHandler = new Handler();
        handlerB=  new Handler();
        //Intensidad de la señal GSM 3g o 4g
        mPhoneStatelistener = new MyPhoneStateListener();


        //beacons Kontakt
        startKontakt();
        starMediaButo();
        super.onStartCommand(intent, flags, startId);
        if(workerThread == null || !workerThread.isAlive()){
            workerThread = new Thread(new Runnable() {
                public void run() {
                    res = getResources();
                    session = new SessionManager(contextWs);
                    soundPTTInicio= Uri.parse("android.resource://d4d.com.svd_basic_plus/raw/ptt_inicio");
                    soundPTTFin= Uri.parse("android.resource://d4d.com.svd_basic_plus/raw/ptt_fin");
                    soundPTTOcupado= Uri.parse("android.resource://d4d.com.svd_basic_plus/raw/ptt_fin2");
                    mp1 = MediaPlayer.create(contextWs, soundPTTInicio);
                    mp2 = MediaPlayer.create(contextWs, soundPTTFin);
                    mp3 = MediaPlayer.create(contextWs, soundPTTOcupado);
                    iniciarBluetoothBajaEnergia();

                    playerAudioDecodingBernard();
                    //estado de bateréa en porcentaje
                    if(miBroadcast!=null) {
                        if(miBroadcast.isInitialStickyBroadcast()) {
                            unregisterReceiver(miBroadcast);
                        }
                    }
                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = () -> {
                            try {
                                registerReceiver(miBroadcast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                            } catch (Exception e) {
                                Log.i(TAG, e.toString());
                            }
                    };
                    handler.sendEmptyMessage(0);
                    handler.postDelayed(myRunnable, 5000);
                    //encendido y apagado de la pantalla
                    registerReceiver(miBroadcast, new IntentFilter(Intent.ACTION_SCREEN_OFF));
                    registerReceiver(miBroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
                    //Log.i("ACTION",Intent.ACTION_MEDIA_BUTTON);
                    registerReceiver(miBroadcast, new IntentFilter("com.motorolasolutions.intent.action.ACTION_PTT_BUTTON_UP"));
                    registerReceiver(miBroadcast, new IntentFilter("com.motorolasolutions.intent.action.ACTION_PTT_BUTTON_DOWN"));

                    //eTera
                    registerReceiver(miBroadcast, new IntentFilter("android.intent.action.PTT.down"));
                    registerReceiver(miBroadcast, new IntentFilter("android.intent.action.PTT.up"));
                    registerReceiver(miBroadcast, new IntentFilter("android.intent.action.SOS.down"));

                    //Seuic
                    registerReceiver(miBroadcast, new IntentFilter("com.seuic.scanner.scankey"));
                    IntentFilter headphone_filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
                    registerReceiver(miBroadcast, headphone_filter);
                    registerReceiver(miBroadcast, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));

                    //AINA
                    registerReceiver(miBroadcast, new IntentFilter("com.ainawireless.intent.action.PTT1_DOWN"));
                    registerReceiver(miBroadcast, new IntentFilter("com.ainawireless.intent.action.PTT1_UP"));

                    //registerReceiver(miBroadcast, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
                    //registerReceiver(miBroadcast, new IntentFilter(Intent.ACTION_MEDIA_BUTTON));





                    //contextWs.registerReceiver(miBroadcast, new IntentFilter("android.intent.action.MEDIA_BUTTON"));
                    //IntentFilter mediaFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
                    //mediaFilter.setPriority(10000); //this line sets receiver priority
                    //contextWs.registerReceiver(miBroadcast, mediaFilter);
                    //registerReceiver(miBroadcast, new IntentFilter("com.cat.intent.action"));
                    //registerReceiver(miBroadcast, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

                    //Intensidad de la señal GSM 3g o 4g
                    mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    mTelephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

                    getLocationMapBox();
                    if(MainActivity.getMyInstance()==null && session.isLoggedIn()==true) {
                        WebSocketComunication.valida_login=2;
                        WebSocketComunication.banderaCerrarSesion = false;
                        Intent imain = new Intent(contextWs,MainActivity.class);
                        imain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(imain);
                        startConnectionWebsocket();//Metodo para iniciar conexion Websocket
                    }else{
                        if (LoginActivity.getMyInstance() == null && MainActivity.getMyInstance()==null) {
                            Intent iSpl = new Intent(contextWs, SplashActivity.class);
                            iSpl.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplication().startActivity(iSpl);
                        }
                    }

                    String NOTIFICATION_CHANNEL_ID = "my_channel_01";// The id of the channel.
                    Intent resultIntent = new Intent(contextWs, MainActivity.class);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent resultPendingIntent;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT;
                        resultPendingIntent = PendingIntent.getActivity(contextWs, 5, resultIntent, PendingIntent.FLAG_IMMUTABLE );
                    }
                    else {
                        //PendingIntent.FLAG_UPDATE_CURRENT;
                        resultPendingIntent = PendingIntent.getActivity(contextWs, 5, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(contextWs, NOTIFICATION_CHANNEL_ID);
                    mBuilder.setSmallIcon(R.mipmap.ic_icon_conectado);
                    mBuilder.setColor(ContextCompat.getColor(contextWs, R.color.colorPrimary))
                            .setContentTitle("Protectia se está ejecutando")
                            .setContentText("Servicio Iniciado.")
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("Servicio Iniciado."))
                            .setTicker("Protectia")
                            .setOngoing(true)
                            .setAutoCancel(false)
                            .setWhen(0)
                            .setLights(Color.BLUE, 1, 1);
                    if (session.isLoggedIn() == true) {
                        //mBuilder.setContentIntent(resultPendingIntent);
                    }
                    sNotificationManager = (NotificationManager) contextWs.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                        notificationChannel.enableLights(false);
                        notificationChannel.setLightColor(Color.BLUE);
                        assert sNotificationManager != null;
                        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                        sNotificationManager.createNotificationChannel(notificationChannel);
                    }
                    sNotificationManager = (NotificationManager) contextWs.getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notif = mBuilder.build();
                    notif.flags |= Notification.FLAG_NO_CLEAR;
                    sNotificationManager.notify(5, notif);
                    startForeground(5, notif);
                    serviceWebsocket=WebSocketComunication.this;
                    validarConexion();
                    pk_conexion_websocket=null;
                    try{
                        pk_conexion_websocket= ProgressDialog.show(contextWs, res.getString(R.string.str_sistema),res.getString(R.string.str_conectando), false, false);
                    }catch (Exception e){
                    }
                    Handler handler2 = new Handler(Looper.getMainLooper());
                    Runnable myRunnable2 = () -> {
                            if(pk_conexion_websocket!=null){
                                try {
                                    pk_conexion_websocket.dismiss();
                                }catch (Exception e){
                                }
                            }
                    };
                    handler2.sendEmptyMessage(0);
                    handler2.postDelayed(myRunnable2, 5000);
                    validaConexionWebsocket();




                }


            });
            workerThread.start();
        }



        return START_STICKY;
    }


    public static void startConnectionWebsocket(){
        //if(webSocketConnection==null) {
            webSocketConnection = new WebSocketConnection();
        //}
        //if(wsHandler==null) {
            wsHandler = new IWebSocketConnectionHandler() {
                @Override
                public void onConnect(ConnectionResponse response) {
                }
                @Override
                public void onOpen() {
                    Log.i(TAG, "Status: Connected to " + wsuri);
                    if (valida_login == 0 || valida_login == 2) {
                        if (webSocketConnection != null) {
                            if (webSocketConnection.isConnected() && session.isLoggedIn() == true) {
                                JSONObject jsonReconexion = null;
                                if(session==null) {
                                    session = new SessionManager(contextWs);
                                }
                                try {
                                    jsonReconexion = new JSONObject().
                                            accumulate("tipo", "reconectar").
                                            accumulate("uuid", session.getUuid()).
                                            accumulate("token", session.getToken()).
                                            accumulate("grupo", session.getGrupoId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(valida_login==2) {
                                    JSONObject finalJsonReconexion = jsonReconexion;
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    Runnable myRunnable = () -> {
                                            try {
                                                webSocketConnection.sendMessage(finalJsonReconexion.toString());
                                                cosumoDatos(finalJsonReconexion.toString());
                                                enviarMensajeListaMiembros();
                                            } catch (Exception e) {
                                                Log.i(TAG, e.toString());
                                            }
                                    };
                                    handler.sendEmptyMessage(0);
                                    handler.postDelayed(myRunnable, 500);
                                }
                                cnd = 0;
                            }
                        }
                    }
                    cnd = 0;
                    if(pk_conexion_websocket!=null){
                        try {
                            pk_conexion_websocket.dismiss();
                        }catch (Exception e){
                        }
                    }
                }

                @Override
                public void onClose(int i, String s) {
                    Log.i(TAG, "ON CLOSE" + wsuri + ",num:" + i + ",s:" + s);
                    if (cnd == 0) {
                        if (valida_login == 2) {
                            if (banderaCerrarSesion==false) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (contextWs != null) {
                                            Toast.makeText(contextWs, "Se ha perdido la conexión con el servidor, por favor revise sus conexiones.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                    //Cuando Gregor recibe el mensaje de cerrar sesion cierra el websocket y me envia eel numero 1000 como codigo no como mensaje de websocket
                    if (banderaCerrarSesion==false && valida_login == 2) {
                        if (i != 1) {
                                Log.i("Reconectar", "WEBSOKCET " + i + ", " + s);
                                Handler handler = new Handler(Looper.getMainLooper());
                                Runnable myRunnable = () -> {
                                        try {
                                            startConnectionWebsocket();
                                            desconectadoWebS=0;
                                        } catch (Exception e) {
                                            Log.i(TAG, e.toString());
                                        }
                                };
                                handler.sendEmptyMessage(0);
                                handler.postDelayed(myRunnable, 3000);
                        }
                    }
                    cnd = 1;
                }

                @Override
                public void onMessage(String payload) {
                    //Log.i(TAG, "ServerJS: " + payload);
                    cosumoDatos(payload);
                    JSONObject msjSendOK = null;
                    JSONObject msjReciveServer = null;
                    try {
                        msjReciveServer = new JSONObject(payload);
                        if (valida_login == 2) {
                            //Envio de confirmación al recibir mensajes de consultas e ingresos de datos
                            if (msjReciveServer.has("idmnsj")) {
                                try {
                                    msjSendOK = new JSONObject();
                                    msjSendOK.accumulate("token", SessionManager.getToken()).
                                            accumulate("tipo", "confirmacion").
                                            accumulate("uuid", SessionManager.getUuid()).
                                            accumulate("idmnsj", msjReciveServer.getString("idmnsj"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (webSocketConnection.isConnected()) {
                                    webSocketConnection.sendMessage(msjSendOK.toString());
                                    cosumoDatos(msjSendOK.toString());
                                    //Log.i("CONFIRMACION ENVIADA", msjSendOK.toString());
                                }
                            }
                            //recibirMensajeGrupo
                            String tipo = (msjReciveServer.has("tipo")) ? msjReciveServer.getString("tipo") : "NO_HAY";
                            switch (tipo) {
                                case "mensajeTexto":
                                    try {
                                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        final MediaPlayer mp = MediaPlayer.create(contextWs, defaultSoundUri);
                                        mp.start();
                                    } catch (Exception e) {
                                    }
                                    ChatActivity.recibirMensaje(payload);
                                    break;
                                case "mensajeArchivo":
                                    //if (contextContent != null) {
                                        //if(ChatActivity.contextCHA!=null) {
                                            ChatActivity.recibirMensaje(payload);
                                        //}
                                    //}
                                    break;
                                case "historialAudioRespuesta":
                                    if (contextContent != null) {
                                        if(isOnlineInternet()){
                                            JSONObject msjReciveHistroialAudio = new JSONObject(payload);
                                            String url = msjReciveHistroialAudio.getString("link");
                                            HistorialAudioFragment.cargarLink(url);
                                        } else {
                                            String msgToasts=res.getString(R.string.sin_conexion);
                                            Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    break;
                                case "estadoMiembrosRespuesta":
                                    session.setMiembrosGrupo(payload);
                                    if (ListaUsuariosActivity.contextListaUsuarios != null) {
                                        ListaUsuariosActivity.cargarListaUsuarios();
                                    }
                                    break;
                                case "grupoRespuesta":
                                    JSONObject msjGrupoRespuesta = new JSONObject(payload);
                                    String numero = (msjGrupoRespuesta.has("conectados")) ? msjGrupoRespuesta.getString("conectados") : "0";
                                    session.setNumUsuConectados(numero);
                                    if (contextContent != null) {
                                        if(UsuarioFragmentPtt.context!=null) {
                                            Log.i("GRUPO RESPUESTA", "Conectados:" + numero);
                                            //UsuarioFragmentPtt.cargarNumeroConectados();
                                            if (MainActivityContent.pk_usuarios_conectados != null) {
                                                try {
                                                    MainActivityContent.pk_usuarios_conectados.dismiss();
                                                } catch (Exception e) {
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case "conectados":
                                    /*JSONObject msjGrupoRespuestaC = new JSONObject(payload);
                                    String numero1 = (msjGrupoRespuestaC.has("conectados")) ? msjGrupoRespuestaC.getString("conectados") : "0";
                                    session.setNumUsuConectados(numero1);*/
                                    /*if (contextContent != null) {
                                        if(UsuarioFragmentPtt.context!=null) {
                                            UsuarioFragmentPtt.cargarNumeroConectados();
                                        }
                                    }*/
                                    break;
                                case "miembroAgregado":
                                    if(MainActivity.context!=null){
                                        MainActivity.agregarMiembro(payload);
                                    }
                                    break;
                                case "miembroRemovido":
                                    if(MainActivity.context!=null) {
                                        MainActivity.eliminarMiembro(payload);
                                    }
                                    break;
                                case "mensajeAlerta":
                                    if(MainActivity.context!=null) {
                                        MainActivity.mensajeAlerta(payload);
                                    }
                                    break;
                                case "mensajeBot":
                                    //AsistenteActivity.recibirMensajeBot(stringServer);
                                    break;
                                case "ocrtextoRespuesta":
                                    //if(MainActivity.context!=null) {
                                        //MainActivity.recibirMensaje(payload);
                                   // }
                                    break;
                                case "identificacionRespuesta":
                                    //MainActivity.recibirCodigoQR(stringServer);
                                    break;
                                case "reservarRespuesta":
                                    //Log.i("reservarRespuesta",stringServer);
                                    if (contextContent != null) {
                                        JSONObject msjReservarRespuesta = new JSONObject(payload);
                                        String resultado = msjReservarRespuesta.getString("resultado");
                                        String reservado = msjReservarRespuesta.getString("reservado");
                                        if (resultado.compareToIgnoreCase("no") == 0 && reservado.compareToIgnoreCase("si") == 0) {
                                            UsuarioFragmentPtt.txt_tiempo_reservado.setText("*");
                                        }
                                        if (resultado.compareToIgnoreCase("si") == 0 && reservado.compareToIgnoreCase("si") == 0) {
                                            int tr = session.getReservable(); //tiempo de reserva de ptt para usuarios como jefes de departamento
                                            final Tiempo tiempo = new Tiempo();
                                            tiempo.Contar();
                                            Handler handler = new Handler();
                                            Runnable myRunnable = () -> {
                                                    tiempo.Detener();
                                                    UsuarioFragmentPtt.img_ptt_reservado.setBackgroundTintList(ColorStateList.valueOf(res.getColor(R.color.white)));
                                                    UsuarioFragmentPtt.txt_tiempo_reservado.setTextColor(res.getColor(R.color.white));
                                                    UsuarioFragmentPtt.txt_tiempo_reservado.setText("");
                                            };
                                            handler.postDelayed(myRunnable, tr);
                                        }
                                    }
                                    break;
                                case "iniciarStream":
                                    //Log.i("iniciarStream",payload);

                                    JSONObject msjStreaming = new JSONObject(payload);
                                    String usuariconsola = msjStreaming.getString("usuarioconsola");
                                    //Log.i("Usuario Consola",usuariconsola+" ____");
                                    SessionManager.setKeyUserConsole(usuariconsola);


                                    /*if(contextContent==null) {
                                        Intent ii = new Intent(contextWs, MainActivityContent.class);
                                        contextWs.startActivity(ii);
                                    }*/
                                    tipSendSteaming=true;

                                    if(ActivityStreaming.context!=null){
                                        ActivityStreaming.loadedStreaming();
                                    }else{
                                        Intent i =new Intent(contextWs, ActivityStreaming.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //i.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        contextWs.startActivity(i);
                                        if(MainActivity.getMyInstance()!=null) {
                                            MainActivity.getMyInstance().finish();
                                        }
                                        Handler handler = new Handler();
                                        Runnable myRunnable = () -> {
                                            //iniciar lambda
                                            ActivityStreaming.loadedStreaming();
                                        };
                                        handler.postDelayed(myRunnable, 1400);

                                    }
                                    break;
                                case "pararStream":
                                    Log.i("pararStream",payload);

                                    JSONObject msjStreaming1 = new JSONObject(payload);
                                    String usuariconsola1 = msjStreaming1.getString("usuarioconsola");
                                    //Log.i("Usuario Consola",usuariconsola+" ____");
                                    SessionManager.setKeyUserConsole(usuariconsola1);


                                    /*if(contextContent==null) {
                                        Intent ii = new Intent(contextWs, MainActivityContent.class);
                                        contextWs.startActivity(ii);
                                    }*/
                                    tipSendSteaming=false;

                                    if(ActivityStreaming.context!=null){
                                        ActivityStreaming.pararStreaming();
                                        Intent intentMA1 = new Intent(contextWs, MainActivity.class);
                                        intentMA1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        contextWs.startActivity(intentMA1);
                                        ActivityStreaming.getMyInstance().finish();

                                    }else{
                                        /*Intent i =new Intent(contextWs, MainActivity1.class);
                                        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //i.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        contextWs.startActivity(i);
                                        if(MainActivity.getMyInstance()!=null) {
                                            MainActivity.getMyInstance().finish();
                                        }
                                        Handler handler = new Handler();
                                        Runnable myRunnable = () -> {
                                            //iniciar lambda
                                            MainActivity1.stopStreaming();
                                        };
                                        handler.postDelayed(myRunnable, 1400);*/

                                    }
                                    break;
                                case "cambiarStream":
                                    Log.i("cambiarStream",payload);
                                    //Log.i("iniciarStream",payload);

                                    JSONObject msjStreaming2 = new JSONObject(payload);
                                    String usuariconsola2 = msjStreaming2.getString("usuarioconsola");
                                    String camara = msjStreaming2.getString("camara");
                                    //Log.i("Usuario Consola",usuariconsola+" ____");
                                    SessionManager.setKeyUserConsole(usuariconsola2);


                                    if(ActivityStreaming.context!=null){
                                        ActivityStreaming.swichtCamera(camara);
                                    }else{
                                        /*Intent i =new Intent(contextWs, MainActivity1.class);
                                        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //i.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        contextWs.startActivity(i);
                                        if(MainActivity.getMyInstance()!=null) {
                                            MainActivity.getMyInstance().finish();
                                        }
                                        Handler handler = new Handler();
                                        Runnable myRunnable = () -> {
                                            //iniciar lambda
                                            MainActivity1.swichtCamera(camara);
                                        };
                                        handler.postDelayed(myRunnable, 1400);*/

                                    }
                                    break;
                                case "pedirListaVideos":
                                    //if(MainActivity1.context!=null) {
                                        listaArchivos();
                                    //}
                                    break;
                                case "pedirVideos":
                                    //if(MainActivity1.context!=null) {
                                        subirArchivos(payload);
                                    //}
                                    break;
                                case "estadoStreamRespuesta":
                                    //if(MainActivity1.context!=null) {
                                    if(ExoPlayer2Activity.contextEP!=null){
                                        ExoPlayer2Activity.receiveMessageOpenStream(payload);
                                    }
                                    break;
                                default:
                                    break;
                            }
                            mensajeConfirmacionPTTAlerta(payload);
                            String status =(msjReciveServer.has("error"))?msjReciveServer.getJSONObject("error").getString("status"):"100";
                            if(status.compareToIgnoreCase("100")!=0 && (status.compareToIgnoreCase("0")!=0)) {
                                String descripcion= msjReciveServer.getJSONObject("error").getString("descripcion");
                                Toast.makeText(contextWs, "Estado "+status+", "+descripcion, Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (valida_login == 1) {
                            LoginActivity.recibirMensajeLogin(payload);
                        }
                    } catch (JSONException e) {
                        Log.i(" Error WEBSOCK", e.getMessage().toString());
                    }
                }
                @Override
                public void onMessage(byte[] payload, boolean isBinary) {
                    Log.i(TAG, "Video ha llegado");
                }
                @Override
                public void onPing() {
                }
                @Override
                public void onPing(byte[] payload) {
                    String str;
                    try {
                        str = new String(payload, "UTF-8");
                        cosumoDatos("onPing "+str);
                        Log.i("onPing",str+ "PING");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onPong() {
                }
                @Override
                public void onPong(byte[] payload) {

                    String str;
                    try {
                        str = new String(payload, "UTF-8");
                        cosumoDatos("onPong "+str);
                        Log.i("onPong",str+ "PONG");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void setConnection(WebSocketConnection connection) {
                }
            };
        try {
            WebSocketOptions webSocketOptions = new WebSocketOptions();
            if (!isMC67) {
                webSocketOptions.setMaxMessagePayloadSize(10485760);
                webSocketOptions.setMaxFramePayloadSize(10485760);
            } else {
                webSocketOptions.setMaxMessagePayloadSize(3145728);
                webSocketOptions.setMaxFramePayloadSize(3145728);
            }
            if(session.getIp()!=null) {
                wsuri = "ws://" + session.getIp() + ":" + session.getPuerto();
            }
            webSocketConnection.connect(wsuri, protocol, wsHandler, webSocketOptions, null);
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    public static void subirArchivos(String mjs){
        Log.i("SUBIR", mjs+ "------");
        try {
            JSONObject itemValues = new JSONObject(mjs);
            JSONArray list_files= itemValues.getJSONArray("listaVideos");

            for(int i=0; i<list_files.length();i++){
                JSONObject values = list_files.getJSONObject(i);
                String name=values.getString("video");
                boolean subir=values.getBoolean("subir");
                if(subir){
                    Handler handler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = () -> {
                        try {
                            cargarFile(name);
                        } catch (Exception e) {
                            Log.i(TAG, e.toString());
                        }
                    };
                    handler.sendEmptyMessage(0);
                    handler.postDelayed(myRunnable, 0);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //envia lista de nombres de archivos de video
    public static void listaArchivos(){
        File folder = new File(""+getDirectory(SessionManager.getUuid(),"Stream"));

        JSONArray list_files= new JSONArray();
        int k=0;
        File yourDir = folder;
        try{
            for (File f : yourDir.listFiles()) {
                if (f.isFile()) {
                    String name = f.getName();
                    list_files.put(k,name);
                    k++;
                    Log.i("name", ": "+name);
                }
            }
            // Do your stuff

            JSONObject json= new JSONObject();
            json.put("tipo", "listaVideos");
            json.put("uuid", session.getUuid());
            json.put("token", session.getToken());
            json.put("listaVideos", list_files);
            if (webSocketConnection.isConnected()&& WebSocketComunication.session.isLoggedIn()==true) {
                Log.i("MESSAGE",json.toString());
                webSocketConnection.sendMessage(json.toString());
                cosumoDatos(json.toString());
            }//else {
            //Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
            //}
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public static void cargarFile(String name){
        //String name="20191028_130723.mp4";
        String path_local= "sdcard/Protectia/"+SessionManager.getUuid()+"/Stream/"+name;
        String nombre=SessionManager.getNombreUsuario().replace(' ','_');
        String path_nube= nombre+"_"+SessionManager.getUuid()+"/Stream/"+name;
        Log.i("path_stream",path_local);
        File file = new File(path_local);
        SpacesFileRepository sf= new SpacesFileRepository(contextWs);
        sf.uploadExampleFile(file, name, path_nube,"chat", "","");
    }

    public static void SubirArchivosSpace(String path_local, String path_nube, String name, String tipo_archivo, String idProyecto){
        Log.d("INFO SUBIDA",path_local+" ,"+path_nube+" ,"+name+" ,"+ tipo_archivo+" ,"+ idProyecto);
        //String name="20191028_130723.mp4";
        String path_nube_sin_espacios=path_nube.replace(' ','_');
        Log.d("LINK",path_nube_sin_espacios);
        File file = new File(path_local);
        Log.d("file",file.length()+"___");
        SpacesFileRepository sf= new SpacesFileRepository(contextWs);
        sf.uploadExampleFile(file, name, path_nube_sin_espacios,"proyecto", tipo_archivo,idProyecto);
    }


    public static File getDirectory(String id_user , String carpeta) {
        File storageDir;
        File folder =  new File("/sdcard/Protectia");
        if (!folder.exists()){
            folder.mkdirs();
        }
        File folder1 =  new File("/sdcard/Protectia/"+id_user);
        if (!folder1.exists()){
            folder1.mkdirs();
        }
        storageDir =  new File("/sdcard/Protectia/"+id_user+"/"+carpeta);
        if (!storageDir.exists()){
            storageDir.mkdirs();
        }
        return storageDir;
    }

    public static void confirmaSubidaArchivo(String name, String url) {
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "archivoSubido");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("name", name);
                json.put("url", url);
                if (webSocketConnection.isConnected()&& WebSocketComunication.session.isLoggedIn()==true) {
                    webSocketConnection.sendMessage(json.toString());
                    cosumoDatos(json.toString());
                    Log.i("MESSAGE",json.toString());
                }//else{
                    //Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                //}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (contextWs != null) {
                String msgToasts = contextWs.getResources().getString(R.string.sin_conexion);
                Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void confirmaSubidaArchivoProyecto(String name, String url, String tipoArchivo, String etiqueta) {
        //String urls=url.replaceAll("\\\\/", "/");

        //Log.d("URAL", url);
        if(ProyectoActivity.contextProAct!=null) {
            ProyectoActivity.respondeRogelio("Archivo subido con éxito.");
        }

        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "archivoSubidoProjecto");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("name", name); // nombre archivo
                json.put("url", url);
                json.put("tipoArchivo", tipoArchivo);
                json.put("etiqueta", etiqueta);//id de projecto
                if (webSocketConnection.isConnected()&& WebSocketComunication.session.isLoggedIn()==true) {
                    webSocketConnection.sendMessage(json.toString());
                    cosumoDatos(json.toString());
                    Log.i("MESSAGE",json.toString());
                }//else{
                //Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                //}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (contextWs != null) {
                String msgToasts = contextWs.getResources().getString(R.string.sin_conexion);
                Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static void enviarMensajeListaMiembros() {
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "estadoMiembros");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("grupo", session.getGrupoId());
                if (webSocketConnection.isConnected()&& WebSocketComunication.session.isLoggedIn()==true) {
                    webSocketConnection.sendMessage(json.toString());
                    cosumoDatos(json.toString());
                }else{
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (contextWs != null) {
                String msgToasts = contextWs.getResources().getString(R.string.sin_conexion);
                Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            String ssignal = signalStrength.toString();
            String[] parts = ssignal.split(" ");
            ConnectivityManager manager =(ConnectivityManager) contextWs.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (null != activeNetwork) {
                type=activeNetwork.getTypeName();
                extra=activeNetwork.getExtraInfo();
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiManager = (WifiManager) contextWs.getSystemService(Context.WIFI_SERVICE);
                    //int numberOfLevels = 5;
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
                    banda="2.4G/5G";
                    senal=wifiInfo.getRssi()+"";
                }
                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager tm = (TelephonyManager)contextWs.getSystemService(Context.TELEPHONY_SERVICE);
                    if ( tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
                        //senal Excelente
                        banda="LTE";
                        // For Lte SignalStrength: dbm = ASU - 140.
                        Log.i("signalSupport",parts[9]+" ___");
                        //signalSupport = Integer.parseInt(parts[9]);
                        signalSupport=80;
                        senal=signalSupport+"";
                    }
                    else{
                        switch (tm.getNetworkType()) {
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                //senal Mala
                                banda= "2G";
                                break;
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                //senal Buena
                                banda= "3G";
                                break;
                            default:
                                banda= "Unknown";
                                break;
                        }
                        if (signalStrength.isGsm()) {
                            // For GSM Signal Strength: dbm =  (2*ASU)-113.
                            if (signalStrength.getGsmSignalStrength() != 99) {
                                signalSupport = -113 + 2 * signalStrength.getGsmSignalStrength();
                                senal=signalSupport+"";
                            }
                        }
                    }

                }
                if(MainActivityContent.contextContent!=null) {
                    if(UsuarioFragmentPtt.context!=null) {
                        int s=Integer.parseInt(senal);
                        UsuarioFragmentPtt.txt_senial.setText("Conexión " + type + " " + banda + ", RSSI " + senal);
                        if(s>(-95)) {
                            UsuarioFragmentPtt.txt_senial.setTextColor(Color.WHITE);
                        }else{
                            UsuarioFragmentPtt.txt_senial.setTextColor(Color.YELLOW);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(sNotificationManager!=null) {
            sNotificationManager.cancelAll();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String id = "my_channel_01";
                sNotificationManager.deleteNotificationChannel(id);
            }
        }
        if(this.miBroadcast!=null || this.miBroadcast.isInitialStickyBroadcast()) {
            unregisterReceiver(this.miBroadcast);
        }
        Log.i("onDestroy","onDestroy");

        //Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
    }

    static int minBufSizeAux = 640;

    public static void startStreaming() {
        Handler handlerMLColor=  new Handler();
        Runnable myRunnableMLColor = () -> {
                //iniciar lambda
                MainActivity.marcarLista(session.getDetalleGrupo(), session.getNombreUsuario());
        };
        handlerMLColor.postDelayed(myRunnableMLColor,0);

        bandera_no_microfono=0;
        contadorToast=0;
        sampleRate = 8000 ; //16000, 44100 for music
        channelConfig = AudioFormat.CHANNEL_IN_MONO;
        audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //minBufSize = 640;
        ipServer= session.getIp();

        // Lambda Runnable
        Runnable task1 = () -> {
            try {
                socket = new DatagramSocket();
                byte[] buffer = new byte[minBufSize];
                DatagramPacket packet;
                final InetAddress destination = InetAddress.getByName(ipServer);
                minBufSizeAux = 640;
                recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channelConfig, audioFormat, minBufSize);
                recorder.startRecording();
                while(transmisionUDP == true) {
                    //reading data from MIC into buffer
                    if (recorder != null){
                        minBufSize = recorder.read(buffer, 0, buffer.length);
                        minBufSizeAux=minBufSize;
                    }else{
                        minBufSizeAux=0;
                        bandera_no_microfono++;
                    }
                    if(minBufSizeAux>0) {
                        runOnUiThread(new Thread(() -> {
                            if(contadorToast==0) {
                                if (MainActivityContent.txt_usuario_logiado != null) {
                                    MainActivityContent.txt_usuario_logiado.setText("Transmitiendo ---> audio " + minBufSize);
                                }
                                Toast toast1 = Toast.makeText(contextWs, "Transmitiendo ---> audio " + minBufSize, Toast.LENGTH_LONG);
                                toast1.show();
                                contadorToast=1;
                            }
                        }));
                        packet = new DatagramPacket(buffer, buffer.length, destination, port);
                        socket.send(packet);
                    }else{
                        runOnUiThread(new Thread(() -> {
                            if(contadorToast==0) {
                                if (MainActivityContent.txt_usuario_logiado != null) {
                                    MainActivityContent.txt_usuario_logiado.setText("Micrófono no disponible ---> audio " + minBufSizeAux);
                                }
                                Toast toast1 = Toast.makeText(contextWs, "Micrófono no disponible ---> audio " + minBufSizeAux, Toast.LENGTH_LONG);
                                toast1.show();
                                contadorToast=1;
                            }
                        }));
                    }
                }
            } catch(UnknownHostException e) {
                Log.e("VS", "UnknownHostException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("VS", "IOException");
            } finally {
                runOnUiThread(new Thread(() -> {
                    if(MainActivityContent.txt_usuario_logiado!=null) {
                        MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());
                        if(recorder!=null) {
                            recorder.stop();
                            recorder.release();
                        }
                        recorder = null;
                    }
                }));
                minBufSize=640;
                minBufSizeAux=640;
                //bandera_no_microfono=0;
            }
        };
        // start the thread
        new Thread(task1).start();
    }

    public void playerAudioDecodingBernard() {
        track = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBufSize,
                AudioTrack.MODE_STREAM);
        port=50005;
        //minBufSize = 640;
        Runnable task1 = () -> {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            try {
                /*if (BLEGatt != null) {
                    BLEGatt.discoverServices();
                        BLEGatt.disconnect();
                        BLEGatt.close();
                    BLEGatt = BLEDevice.connectGatt(contextWs, false, GattCallback, BluetoothDevice.DEVICE_TYPE_LE);
                    //GattCallback.
                    Bonding = true;
                    }

                if (am != null) {
                    Log.i( "Player", "REPRODUCIENDO size"+ am.toString());
                    Log.i( "Player", "REPRODUCIENDO size");
                    //am.setBluetoothScoOn(true);
                    am.setMicrophoneMute(true);
                    am.setSpeakerphoneOn(true);
                    am.setBluetoothScoOn(false);
                }*/
                DatagramSocket sock = new DatagramSocket(port);
                byte[] buf = new byte[minBufSize];
                while (true) {
                    DatagramPacket pack = new DatagramPacket(buf, minBufSize);
                    sock.receive(pack);
                    int size = pack.getLength();
                    track.write(pack.getData(), 0, size);
                    track.play();
                    //Log.i( "Player", "REPRODUCIENDO size"+ size);
                    track.flush();

                }
            } catch (SocketException se) {
                Log.e("Error", "SocketException: " + se.toString());
            } catch (IOException ie) {
                Log.e("Error", "IOException" + ie.toString());
            }
        };
        new Thread(task1).start();
    }

    //Bluethooth
    private static AudioManager am=null;
    public static void startStreamingMicBluetoothSco() {
        am = (AudioManager) contextWs.getSystemService(Context.AUDIO_SERVICE);
        contextWs.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                Log.d("BluetoothSco", "Audio SCO state: " + state);
                if (AudioManager.SCO_AUDIO_STATE_CONNECTED == state) {
               /* Now the connection has been established to the bluetooth device.
               * Record audio or whatever (on another thread).With AudioRecord you can record with an object created like this:
               * new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
               * AudioFormat.ENCODING_PCM_16BIT, audioBufferSize);
               * After finishing, don't forget to unregister this receiver and
               * to stop the bluetooth connection with am.stopBluetoothSco();
               */
                    Log.d("BluetoothSco", "SCO_AUDIO_STATE_CONNECTED");
                    //context.unregisterReceiver(this);
                }
            }
        }, new IntentFilter(AudioManager. ACTION_SCO_AUDIO_STATE_UPDATED));

        try {
            //if(am.isWiredHeadsetOn())
                //am.startBluetoothSco();
            //if(am.isBluetoothScoOn())
                //am.stopBluetoothSco();
            Log.d("BluetoothSco", "starting bluetooth");
            Handler handlerStartB=  new Handler();
            Runnable myRunnableStartB = () -> {
                    //iniciar lambda
                    am.startBluetoothSco();
            };
            handlerStartB.postDelayed(myRunnableStartB,1);
        } catch (NullPointerException e) {
            Log.d(TAG, "startBluetoothSco() failed. no bluetooth device connected.");
        }
    }

    public static void iniciarBluetoothBajaEnergia(){
        GetSwVersion = false;
        GetBattLevel = false;
        GetButtons = false;
        Bonding = false;
        Ready = false;
        State = "IDLE";
        apttasb_batt_level = -0x01;
        apttasb_button_mask = 0x00;
        apttasb_sw_version = "";
        aptt_or_asb = "";
        devName = "";
        classicName = "";
        BLEManager = (BluetoothManager) contextWs.getSystemService(Context.BLUETOOTH_SERVICE);
        BLEAdapter = BLEManager.getAdapter();
        BLEScanner = BLEAdapter.getBluetoothLeScanner();
        System.out.println("start scanning ble devices...0");
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(AINA_SERV.toString())).build();
        List<ScanFilter> filter_list = new ArrayList<ScanFilter>(1);
        Log.i("BluetoohLE", filter_list.toString());
        Log.i("BluetoohLE", filter_list.size()+" ");

        for(int i=0; i<filter_list.size(); i++){
            Log.i("BluetoohLE", filter_list.get(i)+" ");
        }
        filter_list.add(filter);
        if(mScanCallback==null) {
            mScanCallback = new BLEScanCallback();
            //System.out.println("start scanning ble devices...1");
        }

        if(BLEGatt==null) {
            //mScanCallback = new BLEScanCallback();
            //System.out.println("start scanning ble devices...2");
            //BLEGatt = BLEDevice.connectGatt(contextWs, false, GattCallback , BluetoothDevice.DEVICE_TYPE_LE);
        }

        if(filter_list.size()>0 && BLEGatt!=null) {
            BLEScanner.startScan(filter_list, settings, mScanCallback);
        }else{
            //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = () -> {
                try {
                    Toast.makeText(contextWs, "Por favor Active el Bluetooth!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.i(TAG, e.toString());
                }
            };
            handler.sendEmptyMessage(0);
            handler.postDelayed(myRunnable, 100);

        }
    }



    public static int contadorPTT=0;
    private static final Runnable updateText = new Runnable() {
        public void run() {
            Log.i("sda",GetSwVersion+" __");
            if (GetSwVersion) {
                GetSwVersion = false;
                GetButtons = true;
            }
            if (GetButtons) {

                Log.i("BLE", apttasb_button_mask +" PRESIONADO");
                if ((apttasb_button_mask & -127) == -127 || (apttasb_button_mask ) == 1) {
                    Log.i("BLE", "(PTT1 - 0x01) ");
                    if (banderaPermiso == false) {
                            if(contadorPTT==0) {

                                /*if(am!=null) {
                                    am.setMicrophoneMute(false);
                                    //am.setMode(false);
                                    am.setBluetoothScoOn(true);
                                    am.setSpeakerphoneOn(false);
                                }*/
                            byte tmp1[] = {0x01};
                            BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS).setValue(tmp1);
                            BLEGatt.writeCharacteristic(BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS));
                            contadorPTT=1;
                            if(contextContent!=null) {
                                MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());//Aqui
                            }
                            if (banderaPermiso == false && banderaPresionado == false) {
                                //Envia mensaje PTT y espera confirmación de MsgBroker
                                enviarPTT();
                                banderaLevantado = false;
                                if (contextContent != null) {
                                    MainActivityContent.btn_ptt_master.setClickable(false);
                                    MainActivityContent.btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(contextWs.getResources().getColor(R.color.green)));
                                }
                            }
                        }
                    }
                }
                if ((apttasb_button_mask) == 0) {
                        if(contadorPTT==1) {
                            if (banderaLevantado == false) {
                                banderaLevantado = true;
                                terminarPTT();
                                Handler handler = new Handler();
                                Runnable myRunnable = () -> {
                                        // do something
                                        contadorPTT = 0;
                                        byte tmp[] = {0x00};
                                        BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS).setValue(tmp);
                                        BLEGatt.writeCharacteristic(BLEGatt.getService(AINA_SERV).getCharacteristic(LEDS));
                                        banderaPresionado = false;
                                        banderaLevantado = false;
                                        if (contextContent != null) {
                                            MainActivityContent.btn_ptt_master.setClickable(true);
                                            MainActivityContent.btn_ptt_master.setBackgroundTintList(ColorStateList.valueOf(contextWs.getResources().getColor(R.color.blue_semi_transparent)));
                                        }
                                        /*if (am != null) {
                                            if (am.isBluetoothScoOn()) {
                                                handlerB = new Handler();
                                                myRunnableB = () -> {
                                                    try {
                                                        am.stopBluetoothSco();
                                                    } catch (NullPointerException e) {
                                                        Log.d(TAG, "stopBluetoothSco failed. no bluetooth device connected.");
                                                    }
                                                };
                                                handlerB.postDelayed(myRunnableB, 0);
                                            }
                                        }*/
                                        Log.i("BLE", "(CLOSE BUTTONS - 0x00) ");
                                };
                                handler.postDelayed(myRunnable, 1000);
                            }
                        }
                    }
                if ((apttasb_button_mask & 2) == 2) {
                    Log.i("BLE", "(EMERG - 0x02) ");
                    enviarMensajeAlerta();
                }
            }
            if (GetBattLevel) {
                Log.i("BLE", "Battery level: " + apttasb_batt_level + "%");
                if(MainActivityContent.txt_bateria_bluetooth!=null) {
                    MainActivityContent.txt_bateria_bluetooth.setText(apttasb_batt_level + "%");
                }
            }
            if (State.contains("Link Loss")) {
            }
            if (State.contains("LL - Reconnect")) {
            }
        }
    };

    public static void enviarMensajeAlerta(){
        if(isOnlineInternet()){
                JSONObject json= new JSONObject();
                try {
                    json.put("tipo", "mensajeAlerta");
                    json.put("uuid", session.getUuid());
                    json.put("token", session.getToken());
                    json.put("grupo", session.getGrupoId());
                    json.put("timestamp", Utils.getCurrentTimeStamp());
                    if (webSocketConnection.isConnected()) {
                        webSocketConnection.sendMessage(json.toString());
                        cosumoDatos(json.toString());

                    }else {
                        Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String msgToasts=contextWs.getResources().getString(R.string.sin_conexion);
                Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
            }
    }

    public static String tsmp="0";
    public static void enviarPTT(){
        mp2 = MediaPlayer.create(contextWs, soundPTTFin);
        mp3 = MediaPlayer.create(contextWs, soundPTTOcupado);
        tsmp=Utils.getCurrentTimeStamp();
        if(isOnlineInternet()){
            JSONObject jsonEnvia = new JSONObject();
            try {
                jsonEnvia.put("tipo", "envia");
                jsonEnvia.put("grupo", session.getGrupoId());
                jsonEnvia.put("uuid", session.getUuid());
                jsonEnvia.put("token", session.getToken());
                jsonEnvia.put("timestamp", tsmp);
                if(webSocketConnection!=null) {
                    if (webSocketConnection.isConnected()) {
                        webSocketConnection.sendMessage(jsonEnvia.toString());
                        cosumoDatos(jsonEnvia.toString());
                    } else {
                        Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                        mp3 = MediaPlayer.create(contextWs, soundPTTOcupado);
                        if (mp3 != null) {
                            mp3.start();
                            handlerMp3 = new Handler();
                            myRunnableMp3 = () -> {
                                    if (mp3 != null) {
                                        mp3.stop();
                                        mp3.release();
                                    }
                                    mp3 = null;
                            };
                            handlerMp3.postDelayed(myRunnableMp3, 800);
                        }
                    }
                }else{
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                    mp3 = MediaPlayer.create(contextWs, soundPTTOcupado);
                    if (mp3 != null) {
                        mp3.start();
                        handlerMp3 = new Handler();
                        myRunnableMp3 = () -> {
                                if (mp3 != null) {
                                    mp3.stop();
                                    mp3.release();
                                }
                                mp3 = null;
                        };
                        handlerMp3.postDelayed(myRunnableMp3, 800);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts = contextWs.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
            mp3 = MediaPlayer.create(contextWs, soundPTTOcupado);
            if (mp3 != null) {
                mp3.start();
                handlerMp3 = new Handler();
                myRunnableMp3 = () -> {
                        if (mp3 != null) {
                            mp3.stop();
                            //recorder.reset();
                            mp3.release();
                        }
                        mp3 = null;
                };
                handlerMp3.postDelayed(myRunnableMp3, 800);
            }
        }
    }

    public static void terminarPTT(){
        if(webSocketConnection!=null) {
            JSONObject jsonTermina = new JSONObject();
            try {
                jsonTermina.put("tipo", "termino");
                jsonTermina.put("grupo", session.getGrupoId());
                jsonTermina.put("uuid", session.getUuid());
                jsonTermina.put("token", session.getToken());
                if (webSocketConnection.isConnected()) {
                    webSocketConnection.sendMessage(jsonTermina.toString());
                    cosumoDatos(jsonTermina.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mp2 = MediaPlayer.create(contextWs, soundPTTFin);
            if (mp2 != null) {
                mp2.start();
            }
            handlerMp2 = new Handler();
            myRunnableMp2 = () -> {
                    transmisionUDP = false;
                    banderaPermiso = false;
                    if (mp1 != null) {
                        mp1.stop();
                        mp1.release();
                    }
                    mp1 = null;
                    if (mp3 != null) {
                        mp3.stop();
                        mp3.release();
                    }
                    mp3 = null;
            };
            handlerMp2.postDelayed(myRunnableMp2, 500);
        }
    }

    public static void enviarIdGrupo() {
        if (webSocketConnection.isConnected()) {
            JSONObject jsonGrupo = null;
            try {
                jsonGrupo = new JSONObject().
                        accumulate("tipo", "grupo").
                        accumulate("grupo", session.getGrupoId()).
                        accumulate("uuid", session.getUuid()).
                        accumulate("token", session.getToken());
                webSocketConnection.sendMessage(jsonGrupo.toString());
                cosumoDatos(jsonGrupo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            //Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
        }
    }

    private static class BLEScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            int RSSI;
            BLEDevice = result.getDevice();
            String temp = BLEDevice.getName();
            RSSI = result.getRssi();

            if (((temp.contains("ASB")) || (temp.contains("APTT"))) && (State.contains("IDLE")) && (classicName.isEmpty())) {     // If rssi based pairing will be used, check rssi level
//          if (((temp.contains("ASB")) || (temp.contains("APTT"))) && (RSSI > -32) && (State.contains("IDLE"))) {     // If rssi based pairing will be used, check rssi level
//            if (temp.contains("ASB")) {    //Connect to first found aptt or asb
                State = "Connecting";

                if (temp.contains("ASB"))
                    aptt_or_asb = "Smart Button";
                else
                    aptt_or_asb = "Aina PTT";

                //IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                //registerReceiver(BondReceiver, intent);
                BLEScanner.flushPendingScanResults(mScanCallback);
                BLEScanner.stopScan(mScanCallback);
                Log.i("BLE","Device " + temp + " found.");
                devName = temp;
                //Log.i("BLE","Trying to connect...");
                TextUpdateHandler.post(updateText);
                if (BLEGatt != null) {
                    BLEGatt.disconnect();
                    BLEGatt.close();
                }
                System.out.println(">>>>>connectGatt (OnScanResult))");
                BLEGatt = BLEDevice.connectGatt(contextWs, false, GattCallback , BluetoothDevice.DEVICE_TYPE_LE);
                //GattCallback.
                Bonding = true;

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                    }
                }, 500);
            }
        }
    };


    private static BluetoothGattCallback GattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if ((newState == BluetoothProfile.STATE_CONNECTED) && (!State.contains("Discovering"))) {
                if (State.contains("Link Loss")) {
                    System.out.println(">>>>>State change LL -> LL - Reconnect");
                    State = "LL - Reconnect";
                }
                if((State.contains("Connecting") || State.contains("LL - Reconnect")) && Bonding == false)
                {
                    System.out.println(">>>>>Start discovering (state = connecting or LL - Reconnect)");
                    gatt.discoverServices();
                    State = "Discovering";
                }
                else {
                    System.out.println(">>>>>onConeectionStateChane  State = " + State);
                    if(State.contains("Connecting")) {
                        gatt.discoverServices();
                        State = "Discovering";
                        Bonding = false;
                    }
                }
                apttasb_button_mask = 0x00;
                TextUpdateHandler.post(updateText);
            } else {
                Ready = false;
                System.out.println(">>>>>LINK LOSS!");
                if (State.contains("Connected") || State.contains("Discovering")) State = "Link Loss";
                TextUpdateHandler.post(updateText);
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println(">>>>>GATT_SUCCESS on Services discovered");
                CLIENT_CHAR_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");
                AINA_SERV = UUID.fromString("127FACE1-CB21-11E5-93D0-0002A5D5C51B");
                BATT_SERV = UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB");
                SW_VERS = UUID.fromString("127FC0FF-CB21-11E5-93D0-0002A5D5C51B");
                BUTTONS = UUID.fromString("127FBEEF-CB21-11E5-93D0-0002A5D5C51B");
                LEDS = UUID.fromString("127FDEAD-CB21-11E5-93D0-0002A5D5C51B");
                BATT_LEVEL = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB");
                if (!State.contains("Connected") && Bonding == false) {
                    State = "Connected";
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println(">>>>>StopScan");
                            if(BLEScanner!=null) {
                                if(mScanCallback!=null) {
                                    BLEScanner.stopScan(mScanCallback);
                                }
                            }
                            GetSwVersion = true;
                            System.out.println(">>>>>GetSevice(AINA_SERV)");
                            BluetoothGattService Service = BLEGatt.getService(AINA_SERV);
                            if(Service != null) {
                                System.out.println(">>>>>Got service, now read SW_VERSION char");
                                try {
                                    BLEGatt.readCharacteristic(Service.getCharacteristic(SW_VERS));
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            System.out.println(">>>>>ReadSWversion timer");
                                            if ((GetSwVersion == true) && (State.contains("Connected"))) {
                                                State = "Connecting";
                                                BLEGatt = BLEDevice.connectGatt(contextWs, false, GattCallback, BluetoothDevice.DEVICE_TYPE_LE);
                                            }
                                        }
                                    }, 2000);
                                }catch (Exception e){
                                }
                            }
                            else {
                                System.out.println(">>>>>Didn't get services, restart from connect");
                                State = "Connecting";
                                BLEGatt = BLEDevice.connectGatt(contextWs, false, GattCallback, BluetoothDevice.DEVICE_TYPE_LE);
                            }
                        }
                    }, 1600);
                }
                else {
                    System.out.println(">>>>>on Services discovered (Bonding or connected!)");
                }
            }
            else {
                System.out.println(">>>>>GATT_ERROR on Services discovered!");
            }
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().equals(BUTTONS)) {
                apttasb_button_mask = ((int) characteristic.getValue()[0]);
            }
            if (characteristic.getUuid().equals(BATT_LEVEL)) {
                apttasb_batt_level = ((int) characteristic.getValue()[0]);
            }
            TextUpdateHandler.post(updateText);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            System.out.println(">>>>>onCharacteristicsRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (GetSwVersion) {
                    System.out.println(">>>>>Lets check SWVerion...");
                    if(characteristic.getValue().length == 6) {
                        Ready = true;
                        apttasb_sw_version = aptt_or_asb + " Version: ";
                        apttasb_sw_version += Integer.toHexString((characteristic.getValue()[3] & 0xff));
                        apttasb_sw_version += Integer.toHexString((characteristic.getValue()[4] & 0xff));
                        apttasb_sw_version += Integer.toHexString((characteristic.getValue()[5] & 0xff)).toUpperCase();
                        if (apttasb_sw_version.substring(apttasb_sw_version.length() - 1, apttasb_sw_version.length()).equals("0")) {
                            apttasb_sw_version = apttasb_sw_version.substring(0, apttasb_sw_version.length() - 1);
                        }
                        if (apttasb_sw_version.toUpperCase().contains("BE7A"))
                            apttasb_sw_version = "ASB Version: Beta release";

                        TextUpdateHandler.post(updateText);
                        GetBattLevel = true;
                        BLEGatt.readCharacteristic(BLEGatt.getService(BATT_SERV).getCharacteristic(BATT_LEVEL));
                    }
                    else {
                        System.out.println(">>>>>Not SWVersion, try again...");
                        if(BLEGatt!=null) {
                            BLEGatt.readCharacteristic(BLEGatt.getService(BATT_SERV).getCharacteristic(SW_VERS));
                        }

                    }

                } else if (GetBattLevel) {
                    apttasb_batt_level = ((int) characteristic.getValue()[0]);
                    BLEGatt.setCharacteristicNotification(BLEGatt.getService(AINA_SERV).getCharacteristic(BUTTONS), true);
                    BluetoothGattDescriptor descriptor = BLEGatt.getService(AINA_SERV).getCharacteristic(BUTTONS).getDescriptor(CLIENT_CHAR_CONFIG);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    BLEGatt.writeDescriptor(descriptor);

                    BLEGatt.setCharacteristicNotification(BLEGatt.getService(BATT_SERV).getCharacteristic(BATT_LEVEL), true);
                    descriptor = BLEGatt.getService(BATT_SERV).getCharacteristic(BATT_LEVEL).getDescriptor(CLIENT_CHAR_CONFIG);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    BLEGatt.writeDescriptor(descriptor);
                    TextUpdateHandler.post(updateText);
                }
            } else {
                System.out.println(">>>>>GATT_ERROR (onCharRead)!");
                State = "Connecting";
                BLEGatt = BLEDevice.connectGatt(contextWs, false, GattCallback, BluetoothDevice.DEVICE_TYPE_LE);
            }
        }
    };

    public static void mensajeConfirmacionPTTAlerta(String message) {
        JSONObject messageRecive;
        final String tipo, nombreSender ;
        try {
            messageRecive = new JSONObject(message);
            tipo = (messageRecive.has("tipo"))?messageRecive.getString("tipo"):"ninguno";
            switch (tipo) {
                case "enviando":
                    nombreSender = (messageRecive.has("nombreSender")) ? messageRecive.getString("nombreSender") : "ninguno";
                    if (conteoMensaje == 0) {
                        if(Ready) {
                            //startStreamingMicBluetoothSco();
                        }else{
                            //iniciarBluetoothBajaEnergia();
                        }
                        if(mp2!=null) {
                            mp2.stop();

                            mp2.release();
                        }
                        mp2 = null;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(MainActivityContent.txt_usuario_logiado!=null) {
                                    MainActivityContent.txt_usuario_logiado.setText("De: ---> " + nombreSender);
                                }
                            }
                        });
                        conteoMensaje = 1;
                        mp1 = MediaPlayer.create(contextWs, soundPTTInicio);
                        mp2 = MediaPlayer.create(contextWs, soundPTTFin);

                        if (mp1 != null) {
                            mp1.start();
                        }

                        Handler handlerMLColor=  new Handler();
                        Runnable myRunnableMLColor = () -> {
                            //iniciar lambda
                            MainActivity.marcarLista(session.getDetalleGrupo(), nombreSender);
                        };
                        handlerMLColor.postDelayed(myRunnableMLColor,0);

                        String te = "PTT entrante ---->  " + nombreSender + ".";
                        Toast.makeText(contextWs, te, Toast.LENGTH_SHORT).show();
                    }
                    break;

                case "finalizado":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(MainActivityContent.txt_usuario_logiado!=null) {
                                MainActivityContent.txt_usuario_logiado.setText(session.getNombreUsuario());
                            }
                        }
                    });
                    conteoMensaje=0;
                    if(mp1!=null) {
                        mp1.stop();
                        mp1.release();
                    }
                    mp1 = null;
                    mp2 = MediaPlayer.create(contextWs, soundPTTFin);
                    if(mp2!=null) {
                        mp2.start();
                    }else{
                    }
                    break;

                case "enviaRespuesta":
                    Log.i("ENVIARESPUEST cc",messageRecive.toString());
                    String time_stamp = (messageRecive.has("timestamp"))?messageRecive.getString("timestamp"):"0";
                    if(time_stamp.compareToIgnoreCase(tsmp)==0) {
                        String resultado = (messageRecive.has("resultado")) ? messageRecive.getString("resultado") : "si";
                        if (resultado.compareToIgnoreCase("no") == 0) {
                            banderaPermiso = false;
                            transmisionUDP = false;
                            Log.i("PERMISO", resultado + ", " + banderaPermiso);
                            //grupoPermiso = (messageRecive.has("grupo"))?messageRecive.getString("grupo"):"ninguno";
                            if (grupoPermiso.compareToIgnoreCase("xxxxx") != 0) {
                                String t = "Canal no disponible!";
                                Toast.makeText(contextWs, t, Toast.LENGTH_SHORT).show();
                            } else {
                                String t = "Seleccione un Grupo!";
                                Toast.makeText(contextWs, t, Toast.LENGTH_SHORT).show();
                            }
                            mp3 = MediaPlayer.create(contextWs, soundPTTOcupado);
                            if (mp3 != null) {
                                mp3.start();
                                handlerMp3 = new Handler();
                                myRunnableMp3 = () -> {
                                        if (mp3 != null) {
                                            mp3.stop();
                                            mp3.release();
                                        }
                                        mp3 = null;
                                };
                                handlerMp3.postDelayed(myRunnableMp3, 460);
                            }
                        } else {
                            banderaPermiso = true;
                            transmisionUDP = true;
                            if(Ready) {
                                //startStreamingMicBluetoothSco();
                            }else{
                                //iniciarBluetoothBajaEnergia();
                            }
                            handlerB=  new Handler();
                            myRunnableB = () -> {
                                    mp1 = MediaPlayer.create(contextWs, soundPTTInicio);
                                    if (mp1 != null) {
                                        mp1.start();
                                    }
                                    startStreaming();
                                    if(mp2!=null) {
                                        mp2.stop();
                                        mp2.release();
                                    }
                                    mp2 = null;
                            };
                            handlerB.postDelayed(myRunnableB,500);
                        }
                    }else{
                        String t = "Micrófono no disponible!";
                        mp3 = MediaPlayer.create(contextWs, soundPTTOcupado);
                        if (mp3 != null) {
                            mp3.start();
                            handlerMp3 = new Handler();
                            myRunnableMp3 = () -> {
                                    if (mp3 != null) {
                                        mp3.stop();
                                        mp3.release();
                                    }
                                    mp3 = null;
                            };
                            handlerMp3.postDelayed(myRunnableMp3, 460);
                        }
                        Toast.makeText(contextWs, t, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "mensajeAlertaRespuesta":
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void validaConexionWebsocket(){
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable task1 = () -> {
                if(session.isLoggedIn()) {
                    if (isOnlineInternet()) {
                        if (webSocketConnection != null) {
                            if (webSocketConnection.isConnected()) {
                                desconectadoWebS=0;
                                if (session.isLoggedIn()) {
                                    ping_time_response=Utils.ejecutarPingRetraso(session.getIp());
                                    JSONObject gpsJSON = new JSONObject();
                                    try {
                                        manufactura = Utils.getManufacturer();
                                        modelo=Utils.obtenerModelo();
                                        version_app = Utils.versionActualAPK(contextWs.getResources());
                                        imei = Utils.getImei(contextWs);
                                        serial_chip=Utils.getSerialChip(contextWs);
                                        operador_celular=Utils.getOperadorCelular(contextWs);
                                        gpsJSON.put("tipo", "estado");
                                        gpsJSON.put("uuid", session.getUuid());
                                        gpsJSON.put("token", session.getToken());
                                        gpsJSON.put("manufactura", manufactura);
                                        gpsJSON.put("modelo", modelo);
                                        gpsJSON.put("imei", imei);
                                        gpsJSON.put("bateria_movil", levelaux);
                                        gpsJSON.put("bateria_bluetooth", apttasb_batt_level);
                                        gpsJSON.put("lati", latitud);
                                        gpsJSON.put("longi", longitud);
                                        gpsJSON.put("speed", speed);
                                        gpsJSON.put("tipo_conexion", type);
                                        gpsJSON.put("extra", extra);
                                        gpsJSON.put("operadora", operador_celular);
                                        gpsJSON.put("serial_chip", serial_chip);
                                        gpsJSON.put("banda_fono", banda);
                                        gpsJSON.put("senal_fono", senal);
                                        gpsJSON.put("ping_time", ping_time_response+"");
                                        gpsJSON.put("version_app", version_app);
                                        gpsJSON.put("timestamp", Utils.getCurrentTimeStamp());
                                        gpsJSON.put("name_user", session.getNombreUsuario());
                                        gpsJSON.put("group_id", session.getGrupoId());
                                        //gpsJSON.put("active", "true");
                                        //Log.i("Mensaje Estado", m.toString());
                                        if (cont_location >= 5) {
                                            if (session.getGrupoId().compareToIgnoreCase("xxxxx") != 0){
                                                webSocketConnection.sendMessage(gpsJSON.toString());
                                                cosumoDatos(gpsJSON.toString());
                                                sendMessageKuzzle("protectia", "location", gpsJSON);
                                                Log.i("UbicaciónMAPB", "(" + latitud + ", " + longitud + "), velocidad=" + speed + " meters/second ");
                                                cosumoDatos(gpsJSON.toString());
                                                cont_location = 0;
                                            }
                                        }
                                        cont_location++;
                                    } catch(JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Log.i("Websoket", "DESCONECTADO = "+desconectadoWebS);
                                desconectadoWebS++;
                                if (desconectadoWebS >= 5) {
                                    startConnectionWebsocket();
                                    desconectadoWebS=0;
                                }
                            }
                        } else {
                            Log.i("Websoket", "NULL");
                            startConnectionWebsocket();
                        }
                    } else {
                        Toast.makeText(contextWs, "No está conectado a Internet. Por favor, revise la conexión.", Toast.LENGTH_SHORT).show();
                        Log.i("Websoket", "SIN INTERNET");
                        cnd = 1;
                        if(MainActivityContent.contextContent!=null) {
                            if(UsuarioFragmentPtt.context!=null) {
                                UsuarioFragmentPtt.txt_senial.setText("No está conectado a Internet. Por favor, revise la conexión.");
                                UsuarioFragmentPtt.txt_senial.setTextColor(Color.RED);

                            }
                        }
                    }
                }


                String valida_ping="Tiempo de retraso "+ping_time_response;
                if(MainActivityContent.contextContent!=null) {
                    if(UsuarioFragmentPtt.context!=null) {

                            //try {
                                String numberInString=ping_time_response.replace("ms","").trim();
                                //Log.i("NUMERO", ","+numberInString+"::::");
                                if(numberInString.compareToIgnoreCase("")!=0) {
                                    double s = Double.parseDouble(numberInString);
                                    if (s > (240)) {
                                        UsuarioFragmentPtt.txt_ping.setTextColor(Color.YELLOW);
                                    } else {
                                        UsuarioFragmentPtt.txt_ping.setTextColor(Color.WHITE);
                                        if (s == 0) {
                                            valida_ping = "No existe conexión con el servidor!";
                                            UsuarioFragmentPtt.txt_ping.setTextColor(Color.RED);
                                        }
                                    }
                                }else{
                                    valida_ping = "No existe conexión con el servidor!";
                                    UsuarioFragmentPtt.txt_ping.setTextColor(Color.RED);
                                }
                            //}catch (Exception e){

                            //}
                        //Log.i("ping time ",ping_time_response+" __");
                        UsuarioFragmentPtt.txt_ping.setText(valida_ping);
                        /*if(UsuarioFragmentPtt.txt_numero_conectados.getText().toString().compareToIgnoreCase("0")==0){
                            UsuarioFragmentPtt.cargarNumeroConectados();
                        }*/
                    }
                }
                if (WebSocketComunication.Ready == false) {
                    //WebSocketComunication.iniciarBluetoothBajaEnergia();
                }
                //if(locationEngine)
                //getLocationMapBox();
                validaConexionWebsocket();

        };
        handler.sendEmptyMessage(0);
        handler.postDelayed(task1, timeSendTelemetria);
    }

    public void validarConexion(){
            if (isOnlineInternet()) {
                if (webSocketConnection != null) {
                    if (webSocketConnection.isConnected()) {
                    } else {
                        Log.i("Websoket", "DESCONECTADO");
                        startConnectionWebsocket();
                    }
                } else {
                    Log.i("Websoket", "NULL");
                    startConnectionWebsocket();
                }
            } else {
                cnd = 1;
            }
    }

    public static boolean isOnlineInternet() {
        NetworkInfo netInfo=null;
        if(contextWs!=null) {
            ConnectivityManager cm = (ConnectivityManager) contextWs.getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
        }
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static class TopExceptionHandler implements Thread.UncaughtExceptionHandler {
        private Thread.UncaughtExceptionHandler defaultUEH;
        private Activity app = null;

        public TopExceptionHandler(Activity app) {
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
            this.app = app;
        }

        public void uncaughtException(Thread t, Throwable e) {
            StackTraceElement[] arr = e.getStackTrace();
            String report = e.toString()+"\n\n";
            report += "--------- Stack trace ---------\n\n";
            for (int i=0; i<arr.length; i++) {
                report += "    "+arr[i].toString()+"\n";
            }
            report += "-------------------------------\n\n";

            // If the exception was thrown in a background thread inside
            // AsyncTask, then the actual exception can be found with getCause

            report += "--------- Cause ---------\n\n";
            Throwable cause = e.getCause();
            if(cause != null) {
                report += cause.toString() + "\n\n";
                arr = cause.getStackTrace();
                for (int i=0; i<arr.length; i++) {
                    report += "    "+arr[i].toString()+"\n";
                }
            }
            report += "-------------------------------\n\n";

            try {
                FileOutputStream trace = app.openFileOutput("stack.trace",
                        Context.MODE_PRIVATE);
                trace.write(report.getBytes());
                trace.close();
            } catch(IOException ioe) {
                // ...
            }

            defaultUEH.uncaughtException(t, e);
        }
    }

    public static boolean tipSendSteaming=false;

    //Mensajes de video Streaming RTMP
    public static void enviarMensajeStreamingRespuesta(String userConsole, String URLStreamig, String nameItem, int nerror, String camara){
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "iniciarStreamRespuesta");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("usuariconsola", userConsole);
                if(nameItem.compareToIgnoreCase("error")!=0){
                    json.put("error", nerror);
                }else{
                    json.put(nameItem, URLStreamig);
                    json.put("camara", camara);

                }

                if (webSocketConnection.isConnected()) {
                    webSocketConnection.sendMessage(json.toString());
                    cosumoDatos(json.toString());
                    Log.i("iniciarStreamRespuesta", json.toString());
                }else {
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts=contextWs.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }

    public static void enviarMensajeStreaming(String URLStreamig, String modaidad, String camara) {
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "iniciarStream");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("modalidad", modaidad);
                json.put("stream", URLStreamig);
                json.put("camara", camara);
                if (webSocketConnection.isConnected()) {
                    webSocketConnection.sendMessage(json.toString());
                    cosumoDatos(json.toString());
                    Log.i("iniciarStreamRespuesta", json.toString());
                }else {
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts=contextWs.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }

    public static void enviarMensajeErrorStreaming(String userConsole, int nerror){
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "errorStream");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("usuariconsola", userConsole);
                json.put("error", nerror);
                if (webSocketConnection.isConnected()) {
                    webSocketConnection.sendMessage(json.toString());
                    cosumoDatos(json.toString());
                }else {
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts=contextWs.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }


    public static void pararStreamingVideo(String userConsole, String URLStreamig){
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "pararStreamRespuesta");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("usuariconsola", userConsole);
                json.put("stream", URLStreamig);
                if (webSocketConnection.isConnected()) {
                    webSocketConnection.sendMessage(json.toString());
                    cosumoDatos(json.toString());
                    Log.i("pararStreamRespuesta", json.toString());
                }else {
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts=contextWs.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }


    public static void switchCameraVideo(String userConsole, String URLStreamig, String camera){
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "cambiarStreamRespuesta");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("usuariconsola", userConsole);
                json.put("stream", URLStreamig);
                json.put("camara",camera);
                if (webSocketConnection.isConnected()) {
                    webSocketConnection.sendMessage(json.toString());
                    cosumoDatos(json.toString());
                    Log.i("iniciarStreamRespuesta", json.toString());
                }else {
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts=contextWs.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }

    public static void enviarReconectarStreaming(String userConsole, String URLStreamig, String camera){
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "errorStream");
                json.put("uuid", session.getUuid());
                json.put("token", session.getToken());
                json.put("usuariconsola", userConsole);
                json.put("stream", URLStreamig);
                json.put("camara",camera);
                if (webSocketConnection.isConnected()) {
                    webSocketConnection.sendMessage(json.toString());
                    cosumoDatos(json.toString());
                }else {
                    Toast.makeText(contextWs, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts=contextWs.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextWs, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }


    public static void cosumoDatos(String msj){
        byte[] b = new byte[0];
        try {
            b = msj.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            //Log.i("Msj Enviado", "Error");
        }
        Log.i("Msj", "data="+(b.length)+" bytes, "+msj);
    }

}

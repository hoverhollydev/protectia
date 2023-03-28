package d4d.com.svd_basic_plus.location;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import androidx.appcompat.widget.Toolbar;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.mapbox.android.core.permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import d4d.com.svd_basic_plus.ExoPlayer2Activity;
import d4d.com.svd_basic_plus.MainActivity;
import d4d.com.svd_basic_plus.R;
import d4d.com.svd_basic_plus.RegisterLocationActivity;
import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.ptt.UsuarioFragmentPtt;
import d4d.com.svd_basic_plus.utils.SessionManager;
import d4d.com.svd_basic_plus.utils.Utils;

import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.session;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    // Variables needed to initialize a map
    public static MapboxMap mapboxMap;
    private MapView mapView;
    // Variables needed to handle location permissions
    private PermissionsManager permissionsManager;
    private final static String TAG= "LocationActivity";
    public static Context contextLA = null;
    private Resources res;
    private static int tiempoCargaMarcadores=10000;
    private static TextView user_map,user_map1,user_map3;
    private static String urlStream="", uuidSelect="", nombreStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mapbox access token is configured here. This needs to be called either in your application
        Mapbox.getInstance(this, getString(R.string.access_token));
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_location);
        contextLA = LocationActivity.this;
        res = getResources();
        session = new SessionManager(contextLA);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(session.getGrupoNombre());
        UsuarioFragmentPtt.img_location.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(contextLA,R.color.white)));
        user_map=findViewById(R.id.user_map);
        user_map1=findViewById(R.id.user_map1);
        user_map3=findViewById(R.id.user_map3);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        recursiveAllLocationGroup();
        FloatingActionButton FAB = findViewById(R.id.myLocationButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(mapboxMap.getLocationComponent().getLastKnownLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
                    mapboxMap.animateCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(WebSocketComunication.latitud),Double.parseDouble(WebSocketComunication.longitud)), 14));
                //}
            }
        });

        FloatingActionButton map_fab = findViewById(R.id.myAllLocationButton);
        map_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if(mapboxMap.getLocationComponent().getLastKnownLocation() != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
                    mapboxMap.animateCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(WebSocketComunication.latitud),Double.parseDouble(WebSocketComunication.longitud)), 6));
                //}
            }
        });

        FloatingActionButton FABStreaming = findViewById(R.id.urlStream);
        FABStreaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uuidSelect.compareToIgnoreCase("")!=0) {
                    if (uuidSelect.compareToIgnoreCase(SessionManager.getUuid()) != 0) {
                        Intent i =new Intent(contextLA, ExoPlayer2Activity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //i.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        i.putExtra("urlStream",urlStream);
                        i.putExtra("nombreStream",nombreStream);
                        i.putExtra("uuidSelect",uuidSelect);
                        contextLA.startActivity(i);
                    } else {
                        Toast.makeText(contextLA, "Usted está intentando ver su propia trasmisión, por favor seleccione otro usuario en el mapa", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(contextLA, "Por favor, seleccione un usuario en el mapa", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public static void recursiveAllLocationGroup(){
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = () -> {
                    if (WebSocketComunication.isOnlineInternet()) {
                        WebSocketComunication.receiveMessageKuzzle("protectia", "location", "estado", session.getGrupoId());
                    } else {
                        Toast.makeText(contextLA, "No está conectado a Internet. Por favor, revise la conexión.", Toast.LENGTH_SHORT).show();
                        //Log.i("Location Activity", "SIN INTERNET");
                    }
                recursiveAllLocationGroup();
        };
        handler.sendEmptyMessage(0);
        handler.postDelayed(myRunnable, tiempoCargaMarcadores);
    }

    public static Style style;
    public static SymbolManager symbolManager;
    private static List<Symbol> symbols = new ArrayList<>();
    @Override
    public void onMapReady(@NonNull final MapboxMap map) {
        this.mapboxMap = map;
        mapboxMap.setStyle(Style.DARK,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style s) {
                        style=s;
                        //enableLocationComponent(style);
                        symbolManager = new SymbolManager(mapView, mapboxMap, style);
                        symbolManager.setIconAllowOverlap(true);  //your choice t/f
                        symbolManager.setTextAllowOverlap(true);  //your choice t/f
                        //symbolManager.setIconPadding(0.0f);
                        symbolManager.addClickListener(new OnSymbolClickListener() {
                            @Override
                            public void onAnnotationClick(Symbol symbol) {
                                String last_connection;
                                if(symbol.getTextJustify().compareToIgnoreCase("")!=0) {
                                    last_connection = Utils.getTimestampToDate(symbol.getTextJustify());
                                }else{
                                    last_connection=Utils.getTimestampToDate(Utils.getCurrentTimeStamp());
                                }

                                if(symbol.getTextTransform().compareToIgnoreCase("")!=0) {
                                    urlStream=session.getKeyUrlStream()+symbol.getTextTransform();
                                    uuidSelect=symbol.getTextTransform();
                                }else{
                                    urlStream=session.getKeyUrlStream()+session.getUuid();
                                    uuidSelect=session.getUuid();
                                }

                                nombreStream=symbol.getTextField();
                                user_map.setText(nombreStream);
                                user_map1.setText("Última conexión: "+last_connection);
                                user_map3.setText("Lat-Lon: ("+ symbol.getLatLng().getLatitude()+","+symbol.getLatLng().getLongitude()+")");

                                //Toast.makeText(contextLA,symbol.getTextField()+":\nLatitud:" + symbol.getLatLng().getLatitude()+"\nLongitud:"+symbol.getLatLng().getLongitude()+"\nÚltima conexión:"+last_connection,Toast.LENGTH_LONG).show();
                            }
                        });
                        Bitmap bm = BitmapFactory.decodeResource(contextLA.getResources(), R.drawable.mapbox_marker_icon_default);
                        mapboxMap.getStyle().addImage("my-marker",bm);

                        Bitmap bm1 = BitmapFactory.decodeResource(contextLA.getResources(), R.drawable.mapbox_marker_icon_desconect);
                        mapboxMap.getStyle().addImage("my-marker1",bm1);
                    }
                });
    }

    private static String lastConectionUser="";
    public static void marcadores(JSONArray buckets){
        try {
            if(symbols!=null) {
                symbolManager.delete(symbols);
            }
            String group_id, uuid="";
            List<SymbolOptions> options = new ArrayList<>();
            int tam=buckets.length();
            //Log.i("onSuccess", "TOTAL, "+ tam+"  ");
            for(int i=0; i<tam;i++){
                JSONObject items = buckets.getJSONObject(i).getJSONObject("by_uuid_hits").getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source");
                Log.i("onSuccess", "kuzzle search2, "+ items.toString()+"  ");
                group_id =  (items.has("group_id"))? items.getString("group_id") : session.getGrupoId();
                //group_id=items.getString("group_id");
                if(group_id.compareToIgnoreCase(session.getGrupoId())==0){
                    uuid = items.getString("uuid");
                    lastConectionUser = items.getString("timestamp");
                    String lati = items.getString("lati");
                    String longi = items.getString("longi");
                    String name_user = items.getString("name_user");
                    if(uuid.compareToIgnoreCase(session.getUuid())!=0) {
                        long tsActual = Long.parseLong(Utils.getCurrentTimeStamp());
                        long tsUser = Long.parseLong(lastConectionUser);
                        double lat = Double.parseDouble(lati);
                        double lon = Double.parseDouble(longi);
                        tsUser = tsUser + 300000;
                        if (tsActual < tsUser) {
                            //Log.i("Marcador", lat + ", " + lon + ", " + uuid);
                            options.add(new SymbolOptions()
                                    .withLatLng(new LatLng(lat, lon))
                                    .withIconImage("my-marker")
                                    //set the below attributes according to your requirements
                                    //.withIconOffset(new Float[] {0f,-1.5f})
                                    //.withZIndex(10)
                                    .withTextJustify(lastConectionUser)
                                    .withTextField(name_user)
                                    .withTextTransform(uuid)
                                    .withTextAnchor("top")
                                    .withIconSize(1.1f)
                                    .withTextHaloColor("rgba(255, 255, 255, 100)")
                                    .withTextHaloWidth(3.0f)
                                    .withTextSize(15.5f)
                                    .withTextOffset(new Float[]{0f, 0.4f})
                                    .withDraggable(false)
                            );
                        } else {
                            options.add(new SymbolOptions()
                                    .withLatLng(new LatLng(lat, lon))
                                    .withIconImage("my-marker1")
                                    .withTextJustify(lastConectionUser)
                                    .withTextField(name_user)
                                    .withTextTransform(session.getKeyUrlStream()+uuid)
                                    .withTextAnchor("top")
                                    .withIconSize(0.9f)//Para cambiar el tamanio del marcador
                                    .withIconColor("rgba(255, 255, 255, 100)")
                                    .withIconHaloColor("rgba(255, 255, 255, 100)")
                                    .withTextHaloColor("rgba(255, 255, 255, 100)")
                                    .withTextHaloWidth(3.0f)
                                    .withTextSize(15.5f)
                                    .withTextOffset(new Float[]{0f, 1.3f})// Para alinear el texto hacia abajo
                                    .withDraggable(false)
                            );
                        }
                    }
                }
            }
            symbols = symbolManager.create(options);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings( {"MissingPermission"})
    /*private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();
            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            //if (mapboxMap.getStyle() != null) {
                //enableLocationComponent(mapboxMap.getStyle());
            //}
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stream, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.cam1:
                i =new Intent(contextLA, ExoPlayer2Activity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //i.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                i.putExtra("urlStream","rtmp://198.211.109.176:1935/live/usr4dBpruebagate100010");
                i.putExtra("nombreStream","OCRW Interior");
                i.putExtra("uuidSelect","usr4dBpruebagate100010");
                contextLA.startActivity(i);
                break;
            case R.id.cam2:
                i =new Intent(contextLA, ExoPlayer2Activity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                //i.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                i.putExtra("urlStream","rtmp://198.211.109.176:1935/live/usr4dB1234567800100130");
                i.putExtra("nombreStream","OCRW Exterior");
                i.putExtra("uuidSelect","usr4dB1234567800100130");
                contextLA.startActivity(i);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}

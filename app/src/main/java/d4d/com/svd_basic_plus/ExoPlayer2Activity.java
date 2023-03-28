package d4d.com.svd_basic_plus;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/*import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;*/

import org.json.JSONException;
import org.json.JSONObject;

import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.utils.SessionManager;

import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.isOnlineInternet;
import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.webSocketConnection;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCUtil;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.io.IOException;
import java.util.ArrayList;

public class ExoPlayer2Activity extends AppCompatActivity {

    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String ASSET_FILENAME = "bbb.m4v";
    ProgressBar Pbar;
    public static Context contextEP = null;

    //rtmp://198.211.109.176:1935/live/usr4dB1570118405696MT6
    //rtmp://198.211.109.176:1935/live/usr4dB15490620156941Y9 Compa
    private static String URLStreaming = "rtmp://198.211.109.176:1935/live/usr4dB15490620156941Y9";
    //private static final String URLStreaming = "rtmp://198.211.109.176:1935/live/usr4dB1568218356418iI5";

    private VLCVideoLayout mVideoLayout = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exoplayer2);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        contextEP = ExoPlayer2Activity.this;
        String urlStream = getIntent().getExtras().getString("urlStream");
        URLStreaming=urlStream;
        String nombreStream = getIntent().getExtras().getString("nombreStream");
        String uuidSelect = getIntent().getExtras().getString("uuidSelect");

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(this.getResources().getString(R.string.str_url_stream)+" "+nombreStream);

        Pbar = findViewById(R.id.progressBar);

        sendMessageOpenStream(uuidSelect);

        final ArrayList<String> options = new ArrayList<>();

        // Create LibVLC
        // TODO: make this more robust, and sync with audio demo

        int deblocking = getDeblocking(-1);

        int networkCaching = pref.getInt("network_caching_value", 0);
        if (networkCaching > 60000)
            networkCaching = 60000;
        else if (networkCaching < 0)
            networkCaching = 0;
        //options.add("--subsdec-encoding <encoding>");
        /* CPU intensive plugin, setting for slow devices */
        options.add("--audio-time-stretch");
        options.add("--avcodec-skiploopfilter");
        options.add("" + deblocking);
        options.add("--avcodec-skip-frame");
        options.add("0");
        options.add("--avcodec-skip-idct");
        options.add("0");
        //options.add("--subsdec-encoding");
//            options.add(subtitlesEncoding);
        //options.add("--stats");
        /* XXX: why can't the default be fine ? #7792 */
        //if (networkCaching > 0)
        networkCaching=0;
        options.add("--network-caching=" + networkCaching);
        //options.add("--androidwindow-chroma");
        options.add("RV32");

        //options.add("-vv");

        //options.add("--subsdec-encoding <encoding>");
        options.add("--aout=opensles");
        options.add("--audio-time-stretch"); // time stretching
        options.add("-vvv"); // verbosity









        // args.add("--demux");
        //args.add("dump2,none");
        //args.add("--demuxdump-file");
        // args.add(filepath);
        //args.add("--no-video");
        //args.add("--no-audio");
        //args.add("--no-spu");
        //args.add("-vv");
        //options.add("-vvv");
        mLibVLC = new LibVLC(this, options);
        mMediaPlayer = new MediaPlayer(mLibVLC);

        mVideoLayout = findViewById(R.id.video_layout);

        Handler handler2 = new Handler(Looper.getMainLooper());
        Runnable myRunnable2 = () ->  Pbar.setVisibility(View.GONE);
        handler2.sendEmptyMessage(0);
        handler2.postDelayed(myRunnable2, 15000);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mLibVLC.release();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);

        try {
            //final Media media = new Media(mLibVLC, getAssets().openFd(ASSET_FILENAME));
            final Media media = new Media(mLibVLC, Uri.parse(URLStreaming));
            mMediaPlayer.setMedia(media);
            media.release();
        } catch (Exception e) {
            throw new RuntimeException("Error en abrir la trasmisión!");
        }
        mMediaPlayer.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayer.stop();
        mMediaPlayer.detachViews();
    }

    private void createPlayer(String mediaUrl) {

        if(mediaUrl.isEmpty()) return;
        try {

            // Create LibVLC
            final ArrayList<String> args = new ArrayList<>();
            args.add("-vvv");
            mLibVLC = new LibVLC(this, args);


            // Create media player
            mMediaPlayer = new MediaPlayer(mLibVLC);

            final IVLCVout vout = mMediaPlayer.getVLCVout();



            mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);


            final Media media = new Media(mLibVLC, Uri.parse(mediaUrl));
            mMediaPlayer.setMedia(media);
            media.release();

            mMediaPlayer.play();
        } catch (Exception e) {
            Toast.makeText(this, "Error en abrir la trasmisión!", Toast.LENGTH_LONG).show();
        }

    }

    private static int getDeblocking(int deblocking) {
        int ret = deblocking;
        if (deblocking < 0) {
            /**
             * Set some reasonable sDeblocking defaults:
             *
             * Skip all (4) for armv6 and MIPS by default
             * Skip non-ref (1) for all armv7 more than 1.2 Ghz and more than 2 cores
             * Skip non-key (3) for all devices that don't meet anything above
             */
            VLCUtil.MachineSpecs m = VLCUtil.getMachineSpecs();
            if (m == null)
                return ret;
            if ((m.hasArmV6 && !(m.hasArmV7)) || m.hasMips)
                ret = 4;
            else if (m.frequency >= 1200 && m.processors > 2)
                ret = 1;
            else if (m.bogoMIPS >= 1200 && m.processors > 2) {
                ret = 1;
            } else
                ret = 3;
        } else if (deblocking > 4) { // sanity check
            ret = 3;
        }
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                //mMediaPlayer.release();
                //mLibVLC.release();

                finish();
                contextEP=null;
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMessageOpenStream(String uuidSelect){
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "estadoStream");
                json.put("uuid", SessionManager.getUuid());
                json.put("token", SessionManager.getToken());
                json.put("uuiddsp",uuidSelect);
                if (webSocketConnection.isConnected()&& WebSocketComunication.session.isLoggedIn()==true) {
                    webSocketConnection.sendMessage(json.toString());
                    WebSocketComunication.cosumoDatos(json.toString());
                }else{
                    Toast.makeText(contextEP, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            String msgToasts = contextEP.getResources().getString(R.string.sin_conexion);
            Toast.makeText(contextEP, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }


    public static void receiveMessageOpenStream(String msj) {
        try {
            JSONObject items= new JSONObject(msj);
            boolean stream=items.getBoolean("stream");
            if(stream==false) {
                Toast toast = Toast.makeText(contextEP,"Trasmisión no disponible, intentelo de nuevo!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(contextEP, "Trasmisión no disponible, intentelo de nuevo!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*public static Context contextEP = null;
    SimpleExoPlayer player;
    ProgressBar Pbar;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer2);

        contextEP = ExoPlayer2Activity.this;
        String urlStream = getIntent().getExtras().getString("urlStream");
        String nombreStream = getIntent().getExtras().getString("nombreStream");
        String uuidSelect = getIntent().getExtras().getString("uuidSelect");

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(this.getResources().getString(R.string.str_url_stream)+" "+nombreStream);

        sendMessageOpenStream(uuidSelect);

        // Create Simple ExoPlayer
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        PlayerView playerView = findViewById(R.id.simple_player);
        Pbar = findViewById(R.id.progressBar);

        playerView.setPlayer(player);

        // Create RTMP Data Source
        RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();

        MediaSource videoSource = new ExtractorMediaSource
                .Factory(rtmpDataSourceFactory)
                //.createMediaSource(Uri.parse("rtmp://198.211.109.176:1935/live/usr4dB1234567800100130"));// cam ext
                //.createMediaSource(Uri.parse("rtmp://198.211.109.176:1935/live/usr4dB1570118405696MT6"));
                //.createMediaSource(Uri.parse("rtmp://198.211.109.176:1935/live/usr4dBpruebagate100010"));
                .createMediaSource(Uri.parse(urlStream));
        //.createMediaSource(Uri.parse("rtmp://198.211.109.176:1935/live/oficina"));
        //rtmp://198.211.109.176:1935/live/usr4dB1570118405696MT6


        player.prepare(videoSource);
        //player.
        player.setPlayWhenReady(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                player.stop();
                player.release();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMessageOpenStream(String uuidSelect){
        if(isOnlineInternet()){
            JSONObject json= new JSONObject();
            try {
                json.put("tipo", "estadoStream");
                json.put("uuid", SessionManager.getUuid());
                json.put("token", SessionManager.getToken());
                json.put("uuiddsp",uuidSelect);
                if (webSocketConnection.isConnected()&& WebSocketComunication.session.isLoggedIn()==true) {
                    webSocketConnection.sendMessage(json.toString());
                }else{
                    Toast.makeText(contextEP, "No existe conexión con el servidor!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
                String msgToasts = contextEP.getResources().getString(R.string.sin_conexion);
                Toast.makeText(contextEP, msgToasts, Toast.LENGTH_SHORT).show();
        }
    }


    public static void receiveMessageOpenStream(String msj) {
        try {
            JSONObject items= new JSONObject(msj);
            boolean stream=items.getBoolean("stream");
            if(stream==false) {
                Toast toast = Toast.makeText(contextEP,"Trasmisión no disponible, intentelo de nuevo!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Toast.makeText(contextEP, "Trasmisión no disponible, intentelo de nuevo!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/


}

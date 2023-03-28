package d4d.com.svd_basic_plus.chat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
/*import com.netcompss.ffmpeg4android.CommandValidationException;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.loader.LoadJNI;*/

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import d4d.com.svd_basic_plus.R;
import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.ptt.UsuarioFragmentPtt;
import d4d.com.svd_basic_plus.utils.SessionManager;
import d4d.com.svd_basic_plus.utils.Utils;
import static d4d.com.svd_basic_plus.comunication.WebSocketComunication.webSocketConnection;

public class ChatActivity extends AppCompatActivity implements InfiniteScrollListener.OnLoadMoreListener {

    private static EditText messageET;

    private ImageButton sendBtn;
    private ImageButton btn_multimedia;
    private ImageButton btn_speed_to_text=null;
    private static boolean bandera_sw_mode=false;

    private static List<ChatMessage> chatItem1s;
    public static RecyclerView listaMessage;
    private static ChatAdapter adapter;

    private static ArrayList<ChatMessage> chatHistory;
    private static ArrayList<String> chatHistoryUp;
    public static Context contextCHA;
    //Usuario
    private static SessionManager session;
    private Resources res;
    private static TextToSpeech t1;
    //Firebase Cloud Storage
    private static StorageReference mStorageRef;
    private static int acumuladorChat=10;
    private static int conteoLineas=10;
    private static boolean upValida=false;

    //recycler view Pagination
    private static final int PAGE_SIZE = 10;
    public static final int PAGE_START = 1;
    InfiniteScrollListener infiniteScrollListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        contextCHA = ChatActivity.this;
        session = new SessionManager(contextCHA);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(session.getGrupoNombre());
        res = getResources();
        FirebaseApp.initializeApp(contextCHA); //Firebase
        mStorageRef = FirebaseStorage.getInstance().getReference(); //Firebase Cloud Storag

        listaMessage = findViewById(R.id.recyclerViewChat);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        infiniteScrollListener = new InfiniteScrollListener(mLayoutManager, this);
        infiniteScrollListener.setLoaded();
        listaMessage.setLayoutManager(mLayoutManager);
        listaMessage.addOnScrollListener(infiniteScrollListener);

        //GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        //listaMessage.setLayoutManager(mLayoutManager);

        listaMessage.setHasFixedSize(true);

        messageET = findViewById(R.id.messageEdit);
        sendBtn = findViewById(R.id.chatSendButton);
        btn_multimedia = findViewById(R.id.btn_multimedia);
        btn_speed_to_text = findViewById(R.id.btn_speed_to_text);
        UsuarioFragmentPtt.img_chat.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(contextCHA,R.color.white)));

        chatItem1s = new ArrayList<>();
        chatHistory = new ArrayList<>();

        adapter = new ChatAdapter(chatItem1s, contextCHA);
        listaMessage.setAdapter(adapter);

        //adapter.addNullData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //adapter.removeNull();
                try {
                    acumuladorChat=10;
                    conteoLineas=10;
                    upValida=false;
                    leerHistorialChat(session.getUuid()+"_"+session.getGrupoId());
                    infiniteScrollListener.setLoaded();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 100);

        /*listaMessage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                listaMessage.scrollToPosition(listaMessage.getAdapter().getItemCount()-1);
            }
        });*/

        /*listaMessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    System.out.println("Scrolled true");
                }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //int visibleItemCount = mLayoutManager.getChildCount();
            //int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
            //int check = totalItemCount - visibleItemCount+firstVisibleItemPosition;
            //Log.d("Scrolled","Current Visible = "+visibleItemCount+" Total = "+totalItemCount+" Scrolled Out Items = "+firstVisibleItemPosition+" Check = "+check);


            if(firstVisibleItemPosition==0 && upValida==false){

                adapter.addNullData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.removeNull();
                        if (acumuladorChat <= conteoLineas) {
                            acumuladorChat = acumuladorChat + PAGE_SIZE;
                            try {
                                upValida = true;
                                leerHistorialChat(session.getUuid() + "_" + session.getGrupoId());
                                if(conteoLineas>=acumuladorChat) {
                                    adapter.addNullData();
                                    listaMessage.scrollToPosition(12);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.i("onScroll", "EJECUTADO Up, acumulador=" + acumuladorChat + ", conteoLineas="+conteoLineas);
                        }
                    }
                }, 2000);

                }
            }
        });*/

        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(contextCHA, ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }
            @Override
            public void onBeginningOfSpeech() {
            }
            @Override
            public void onRmsChanged(float v) {
            }
            @Override
            public void onBufferReceived(byte[] bytes) {
            }
            @Override
            public void onEndOfSpeech() {
            }
            @Override
            public void onError(int i) {
            }
            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //displaying the first match
                if (matches != null)
                    messageET.setText(matches.get(0));
            }
            @Override
            public void onPartialResults(Bundle bundle) {
            }
            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });

        btn_speed_to_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(bandera_sw_mode) {
                    switch (motionEvent.getAction()) {

                        case MotionEvent.ACTION_UP:
                            mSpeechRecognizer.stopListening();
                            messageET.setHint("Escribir mensaje");
                            break;
                        case MotionEvent.ACTION_DOWN:
                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                            messageET.setHint("Escuchando...");
                            break;
                    }
                }
                return false;
            }
        });

        t1=new TextToSpeech(contextCHA, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {

                    Locale spanish = new Locale("es", "ES");
                    t1.setLanguage(spanish);
                    //t1.setLanguage(Locale.ITALY);
                }
            }
        });

        btn_multimedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getKeySiChat() == true) {
                    openDialog();
                }else{
                    Toast.makeText(contextCHA, "Consola, no tiene permiso de Chat!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Envía texto y contenido multimedia en el chat
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.getKeySiChat() == true) {
                    String mensaje = messageET.getText().toString();
                    if (TextUtils.isEmpty(mensaje)) {
                        return;
                    }
                    cargarMe("mensajeTexto",mensaje,"texto","");
                    //scroll();
                }else{
                    Toast.makeText(contextCHA, "Consola, no tiene permiso de Chat!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static Dialog dialogMultimedia;
    public void openDialog() {
        dialogMultimedia = new Dialog(contextCHA);
        dialogMultimedia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMultimedia.setContentView(R.layout.dialog_chat_multimedia);
        dialogMultimedia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogMultimedia.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogMultimedia.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        dialogMultimedia.getWindow().getAttributes().y=350;
        dialogMultimedia.getWindow().getAttributes().flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialogMultimedia.show();
        Button btn_camara = dialogMultimedia.findViewById(R.id.btn_foto);
        Button btn_video = dialogMultimedia.findViewById(R.id.btn_video);
        Button btn_galleria = dialogMultimedia.findViewById(R.id.btn_galleria);
        Button btn_archivos = dialogMultimedia.findViewById(R.id.btn_archivos);
        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeFoto();
            }
        });

        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeVideo();
            }
        });

        btn_galleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                openGallery();
            }
        });

        btn_archivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                openFiles();
            }
        });
    }

    public static void cargarMe(String tipo, String mensaje, String tipoContenido, String path){
        String nombre=session.getNombreUsuario();
        //String fecha=DateFormat.getDateTimeInstance().format(new Date());
        String fecha = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new Date());
        //sentido true es para enviar
        cargarMensajeDisplay(tipo,nombre,mensaje,path,fecha, false, true,tipoContenido);
        //scroll();
    }

    public static void cargarObjetoMensaje( String nombre, String msj, String tipoContenido,
                                             String path, Bitmap previewFile, String fecha,
                                             boolean isMe){
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMe(isMe);
        chatMessage.setNombreUser(nombre);
        chatMessage.setMessage(msj);
        chatMessage.setTipo(tipoContenido);
        chatMessage.setPathArchivo(path);
        chatMessage.setPreviewArchivo(previewFile);
        chatMessage.setDate(fecha);
        messageET.setText("");


        displayMessage(chatMessage);
    }


    public static void cargarMensajeDisplay(String tipoMensaje, String nombre, String msj, String path,
                                            String fecha, boolean isMe, boolean sentido, String tipoContenido){
        if(dialogMultimedia!=null) {
            dialogMultimedia.dismiss();
        }
        Bitmap preview_file=null;
        switch (tipoMensaje) {
            case "mensajeTexto":
                if(sentido) {
                    if(tipoContenido.compareToIgnoreCase("texto")==0) {
                        upValida=false;
                        enviarMensaje(tipoMensaje, msj, tipoContenido, "", "");
                    }
                }
                break;
            case "mensajeArchivo":
                    switch (tipoContenido) {
                        case "imagen":
                            preview_file = Utils.bitmapDecodeFile(path);
                        break;
                        case "video":
                            preview_file = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
                            break;
                        case "archivo":
                            Drawable d = ContextCompat.getDrawable(contextCHA,R.drawable.ic_files);
                            preview_file=((BitmapDrawable)d).getBitmap();
                            break;
                    }
                    Log.i("path","path");
                    String filename_extension=path.substring(path.lastIndexOf("/")+1);
                    if(sentido){
                        upValida=false;
                        msj=tipoContenido+"_"+filename_extension;
                        String path_almacenamiento_nube="/Protectia/"+session.getNombreUsuario()+"_"+session.getUuid()+"/"+session.getGrupoId()+"_"+session.getGrupoNombre()+"/Envio/"+tipoContenido+"/"+tipoContenido+"_"+filename_extension;
                        subirNubeArchivos(tipoMensaje,path, path_almacenamiento_nube, tipoContenido, msj);
                    }else{
                        msj=filename_extension;
                    }
                break;
        }
        //Log.i("cargarMensajeDisplay",nombre+", "+path+", "+fecha+", "+isMe);
        cargarObjetoMensaje(nombre,msj,tipoContenido,path,preview_file,fecha,isMe);

    }

    public static void enviarMensaje(String tipo, String msj, String tipoContenido, String url, String path) {
        JSONObject jsonEnvioMensaje = new JSONObject();
        try {
            String uuid_user=session.getUuid();
            String id_grupo= session.getGrupoId();
            jsonEnvioMensaje.put("tipo", tipo);
            jsonEnvioMensaje.put("uuid", uuid_user);
            jsonEnvioMensaje.put("token", session.getToken());
            jsonEnvioMensaje.put("destinario", id_grupo);
            jsonEnvioMensaje.put("url", url);
            jsonEnvioMensaje.put("texto", msj);
            jsonEnvioMensaje.put("tipoContenido", tipoContenido);
            jsonEnvioMensaje.put("id", Utils.getCurrentTimeStamp());
            String jsonMsj = jsonEnvioMensaje.toString();

            if (webSocketConnection.isConnected()) {
                webSocketConnection.sendMessage(jsonMsj);
                WebSocketComunication.cosumoDatos(jsonMsj.toString());
                Log.i("mensajeTexto",jsonMsj);
            }
            jsonEnvioMensaje.put("path", path); //Guarda el path del archivo guardado en la sd local
            jsonEnvioMensaje.put("isMe", false); //diferencia si es envio=false, recibido=true
            //String fecha = DateFormat.getDateTimeInstance().format(new Date());
            String fecha = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new Date());
            jsonEnvioMensaje.put("fecha",fecha);
            Utils.guardarHistorialChat(jsonEnvioMensaje.toString().replace("\\\\",""), uuid_user+"_"+id_grupo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void recibirMensaje(String messageServer) {
        try {
            Log.i("recibirMensaje",messageServer);
            JSONObject messageRecive = new JSONObject(messageServer);
            String tipo_mensaje=messageRecive.getString("tipo");
            String uuid = session.getUuid();
            String idGrupo=messageRecive.getString("deUgid");
            String nombreSender=messageRecive.getString("nombreSender");
            String mensaje = messageRecive.getString("texto");
            String tipoContenido = (messageRecive.has("tipoContenido")) ? messageRecive.get("tipoContenido").toString() : "n";
            String url = (messageRecive.has("url")) ? messageRecive.get("url").toString() : "n";
            //String fecha = DateFormat.getDateTimeInstance().format(new Date());
            String fecha = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(new Date());
            String path = "";
            boolean isMe = true;
            messageRecive.accumulate("isMe",isMe);
            messageRecive.accumulate("fecha",fecha);
            upValida=false;
            switch (tipo_mensaje) {
                case "mensajeTexto":
                    if(contextCHA!=null){
                        cargarMensajeDisplay(tipo_mensaje, nombreSender, mensaje, "", fecha, true, false, "texto");
                        //scroll();
                    }
                    messageRecive.accumulate("path",path);
                    Utils.guardarHistorialChat(messageRecive.toString().replaceAll("\\\\/", "/"),uuid+"_"+idGrupo);
                    break;
                case "mensajeArchivo":
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference httpsReference = storage.getReferenceFromUrl(url);
                    String msj = httpsReference.getName();
                    File file = new File(getAlbumDir("Recibido",tipoContenido), msj);
                    path = file.getPath();
                    String finalPath = path;
                    messageRecive.accumulate("path",finalPath);
                    httpsReference.getFile(file)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    // Successfully downloaded data to local file
                                    Log.i("downloadFile","succcess");
                                    try {
                                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        final MediaPlayer mp = MediaPlayer.create(WebSocketComunication.contextWs, defaultSoundUri);
                                        mp.start();
                                    } catch (Exception e) {
                                    }

                                    Utils.guardarHistorialChat(messageRecive.toString().replaceAll("\\\\/", "/"),uuid+"_"+idGrupo);
                                    if(contextCHA!=null) {
                                        if (idGrupo.compareToIgnoreCase(session.getGrupoId()) == 0) {
                                            //Log.i("downloadFile", "succcess" + tipoContenido);
                                            cargarMensajeDisplay(tipo_mensaje, nombreSender, "", finalPath, fecha, isMe, false, tipoContenido);
                                            //scroll();
                                        }
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed download
                            Log.i("downloadFile","failure");
                        }
                    });
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //Firebase Cloud Storage
    private static void subirNubeArchivos(String tipo_mensaje, String path_local, String path_nube,
                                   String tipo_contenido, String mensaje){
        Uri file = Uri.fromFile(new File(path_local));
        StorageReference fileReference = mStorageRef.child(path_nube);

        fileReference.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                Log.i("getDownloadUrl", "onSuccess Upload and uri: " + url);
                                enviarMensaje(tipo_mensaje,mensaje,tipo_contenido,url,path_local);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.i("uploadFile+", "onFailure uri: no_link");
                    }
                });
    }

    private final int SELECT_FILES = 301;
    private final int SELECT_PICTURE = 300;
    //private final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath="";
    public static String mCurrentVideoPath="";

    /*public void takeFoto() {
        Intent intent = new Intent(contextCHA, Camera2ImagenActivity.class);
        startActivity(intent);
    }*/

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public void takeFoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            //Log.i("IDAPP", contextCHA.getPackageName() );
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, contextCHA.getPackageName()+".provider" , photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    static final int REQUEST_VIDEO_CAPTURE = 2;
    public void takeVideo(){
        /*Intent intent = new Intent(contextCHA, Camera2VideoActivity.class);
        startActivity(intent);*/
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                Uri videoURI = FileProvider.getUriForFile(this, contextCHA.getPackageName()+".provider" , videoFile);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
    }

    private void openFiles() {
        Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        intent.putExtra("CONTENT_TYPE", "*/*");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        if(intent.resolveActivity(contextCHA.getPackageManager()) != null) {
            startActivityForResult(intent, SELECT_FILES);
        }
        else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            //intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(intent.createChooser(intent, "Selecciona app de archivos"), SELECT_FILES);
        }
    }

    public static File getAlbumDir(String envOrRec , String carpeta) {
        File storageDir;
        File folder =  new File("/sdcard/Protectia");
        if (!folder.exists()){
            folder.mkdirs();
        }
        File folder1 =  new File("/sdcard/Protectia/"+envOrRec);
        if (!folder1.exists()){
            folder1.mkdirs();
        }
        storageDir =  new File("/sdcard/Protectia/"+envOrRec+"/"+carpeta);
        if (!storageDir.exists()){
            storageDir.mkdirs();
        }
        return storageDir;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Log.i("PATH", contentUri.getPath()+"  ");
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_VIDEO_CAPTURE || requestCode == SELECT_PICTURE || requestCode == SELECT_FILES) {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_IMAGE_CAPTURE:
                        if (mCurrentPhotoPath != null) {
                           // Bundle extras = data.getExtras();
                            //Bitmap imageBitmap = (Bitmap) extras.get("data");
                            //Log.i("TAMINIO", imageBitmap.getByteCount()+" ");

                            BitmapFactory.Options bounds = new BitmapFactory.Options();
                            //bounds.inJustDecodeBounds = true;

                            //int photoW = bounds.outWidth;
                            //int photoH = bounds.outHeight;

                            // Determine how much to scale down the image
                            //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                            //int scaleFactor = Math.min(photoW/250, photoH/250);

                            // Decode the image file into a Bitmap sized to fill the View
                            bounds.inJustDecodeBounds = false;
                            bounds.inSampleSize = 4;
                            //bmOptions.inPurgeable = true;


                            Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, bounds);
                            Log.i("TAMANIO1", bm.getByteCount()+"___");
                            String value = Utils.getManufacturer();
                            Log.i("Manufacture",value+"___");
                            if(value.compareToIgnoreCase("samsung")==0 || value.compareToIgnoreCase("Motorola Solutions")==0 || value.compareToIgnoreCase("Google")==0) {
                                // Read EXIF Data
                                ExifInterface exif = null;
                                try {
                                    exif = new ExifInterface(mCurrentPhotoPath);
                                    Log.i("exif",exif.toString());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if(exif!=null) {
                                    String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                                    int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
                                    int rotationAngle = 0;
                                    //int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
                                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
                                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
                                    // Rotate Bitmap
                                    Matrix matrix = new Matrix();
                                    matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
                                    bm = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);


                                }
                            }

                            File file = new File(mCurrentPhotoPath);

                            try {
                                FileOutputStream out = new FileOutputStream(file);
                                bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
                                Log.i("TAMANIO2", bm.getByteCount()+"___");
                                out.flush();
                                out.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            cargarMe("mensajeArchivo","","imagen",mCurrentPhotoPath);
                            //imageView.setImageBitmap(imageBitmap);
                            //cargarMe("mensajeArchivo","","imagen",mFile.toString());
                        }
                        break;
                    case REQUEST_VIDEO_CAPTURE:
                        //Uri videoUri = data.getData();

                        /*if (GeneralUtils.checkIfFileExistAndNotEmpty(mCurrentVideoPath)) {
                            new TranscdingBackground(ChatActivity.this).execute();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), mCurrentVideoPath + " not found", Toast.LENGTH_LONG).show();
                        }*/

                        ChatActivity.cargarMe("mensajeArchivo","","video",mCurrentVideoPath);

                        //data.setVideoURI(videoUri);
                        break;
                    case SELECT_PICTURE:
                        final Uri u=data.getData();
                        String path = getRealPathFromURI(contextCHA,u);
                        cargarMe("mensajeArchivo","","imagen",path);
                        break;
                    case SELECT_FILES:
                        if(Utils.getManufacturer().compareToIgnoreCase("samsung")==0) {
                            final Uri uri = data.getData();
                            String p = uri.getPath();
                            cargarMe("mensajeArchivo", "", "archivo", p);
                        }else{
                            final Uri uri = data.getData();
                            String p=getRealPathFromURI(contextCHA,uri);
                            cargarMe("mensajeArchivo", "", "archivo", p);
                        }
                        break;
                }
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        File mFile = new File(getAlbumDir("Envia","imagen"), Utils.getCurrentTimeStamp()+".jpg");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = mFile.getAbsolutePath();
        return mFile;
    }

    private File createVideoFile() throws IOException {
        // Create an image file name
        File mFile = new File(ChatActivity.getAlbumDir("Envia","video"), Utils.getCurrentTimeStamp()+".mp4");
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentVideoPath = mFile.getAbsolutePath();
        return mFile;
    }

    public static void displayMessage(ChatMessage items) {
        if(upValida) {
            adapter.addUp(items);

        } else{
            adapter.addDown(items);
            scroll();
        }
        adapter.notifyDataSetChanged();
    }

    private static void scroll() {
        //Log.i("POSITION",listaMessage.getLayoutManager().getItemCount()-1+" ;;;");
        listaMessage.getLayoutManager().scrollToPosition(listaMessage.getLayoutManager().getItemCount()-1);
    }

    public static void leerHistorialChat(String Grupo) throws IOException {
        chatHistoryUp = new ArrayList<>();
        String line;
        FileInputStream is;
        BufferedReader reader;
        File Root = Environment.getExternalStorageDirectory();
        if(Root.canRead()){
            final File file = new File("/sdcard/Protectia/"+Grupo+".txt");
            if (file.exists()) {
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                conteoLineas=0;
                line = reader.readLine();
                while(line != null){
                    conteoLineas++;
                    line = reader.readLine();
                }
                Log.i("count", conteoLineas+", acumulador= "+ acumuladorChat);
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                line = reader.readLine();
                long count2=0;
                if(conteoLineas<=10) {
                    while(line != null){
                            chatHistoryUp.add(line);
                            line = reader.readLine();
                        }
                }else{
                    long inicio=conteoLineas-(acumuladorChat);
                    long fin=inicio+10;
                    //Log.i("inicio", inicio+", fin= "+ fin+", total="+conteoLineas);

                    while(line != null){
                        count2++;
                        if(count2>=(inicio+1)&& count2 <= (fin)) {
                            chatHistoryUp.add(line);
                            //Log.i("count2", count2 + "");
                            //Log.i("StackOverflow", line);
                        }
                        line = reader.readLine();
                    }
                }
                if(upValida) {
                    int total=(chatHistoryUp.size()-1);
                    for (int i = total; i > 0; i--) {
                        String line_aux = chatHistoryUp.get(i);
                        cargarJsonHistorialChat(line_aux);
                    }
                }else{
                    for (int i = 0; i < chatHistoryUp.size(); i++) {
                        String line_aux = chatHistoryUp.get(i);
                        cargarJsonHistorialChat(line_aux);
                    }
                }
            }
        }
    }

    public static void cargarJsonHistorialChat(String line){
        JSONObject messageRecive;
        try {
            messageRecive = new JSONObject(line);
            String tipo = messageRecive.getString("tipo");
            boolean isMe = (messageRecive.has("isMe")) ? messageRecive.getBoolean("isMe") : true;
            String nombreSender = (messageRecive.has("nombreSender")) ? messageRecive.getString("nombreSender") : session.getNombreUsuario();
            String msj = messageRecive.getString("texto");
            String tipoContenido = messageRecive.getString("tipoContenido");
            String path = (messageRecive.has("path")) ? messageRecive.getString("path") : "";
            String fecha = (messageRecive.has("fecha")) ? messageRecive.getString("fecha") : "";
            //Log.i("TIPO",tipo+", "+path);
            if(tipo.compareToIgnoreCase("mensajeArchivo")==0) {
                if(path.compareToIgnoreCase("")!=0) {
                    cargarMensajeDisplay(tipo, nombreSender, msj, path, fecha, isMe, false, tipoContenido);
                }
            }else{
                cargarMensajeDisplay(tipo, nombreSender, msj, path, fecha, isMe, false, tipoContenido);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        MenuItem item = menu.findItem(R.id.speech_to_text_chat);
        super.onCreateOptionsMenu(menu);

        Switch mySwitch = item.getActionView().findViewById(R.id.speech_to_text_chat);
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something based on isChecked
                Log.i("CHEK",isChecked+"");
                if (isChecked) {
                    //sw_mode.setText("Modo voz");
                    bandera_sw_mode = true;
                } else {
                    bandera_sw_mode = false;
                    //sw_mode.setText("Modo texto");
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if(t1!=null) {
                    t1.shutdown();
                }
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadMore() {
        adapter.addNullData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.removeNull();
                if (acumuladorChat <= conteoLineas) {
                    acumuladorChat = acumuladorChat + PAGE_SIZE;
                    try {
                        upValida = true;
                        leerHistorialChat(session.getUuid() + "_" + session.getGrupoId());
                        if(conteoLineas>=acumuladorChat) {
                            listaMessage.scrollToPosition(9);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.i("onScroll", "EJECUTADO Up, acumulador=" + acumuladorChat + ", conteoLineas="+conteoLineas);
                    infiniteScrollListener.setLoaded();
                }
            }
        }, 2000);

    }


    /*public class TranscdingBackground extends AsyncTask<String, Integer, Integer> {
        ProgressDialog progressDialog;
        Context _act;
        String commandStr;

        public TranscdingBackground (Context act) {
            _act = act;
        }

        @Override
        protected void onPreExecute() {
            //EditText commandText = (EditText)findViewById(R.id.CommandText);
            //commandStr = commandText.getText().toString();

            progressDialog = new ProgressDialog(_act);
            progressDialog.setMessage("FFmpeg4Android Transcoding in progress...");
            progressDialog.show();

        }

        protected Integer doInBackground(String... paths) {
            Log.i(Prefs.TAG, "doInBackground started...");

            // delete previous log
            //boolean isDeleted = GeneralUtils.deleteFileUtil(workFolder + "/vk.log");
            //Log.i(Prefs.TAG, "vk deleted: " + isDeleted);

            PowerManager powerManager = (PowerManager)_act.getSystemService(Activity.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
            Log.d(Prefs.TAG, "Acquire wake lock");
            wakeLock.acquire();

            ///////////// Set Command using code (overriding the UI EditText) /////
            commandStr = "ffmpeg -y -i "+mCurrentVideoPath+" -strict experimental -s 320x240 -r 30 -aspect 3:4 -ab 48000 -ac 2 -ar 22050 -vcodec mpeg4 -b 2097152 "+mCurrentVideoPath;
            //String[] complexCommand = {"ffmpeg", "-y" ,"-i", "/sdcard/videokit/in.mp4","-strict","experimental","-s", "160x120","-r","25", "-vcodec", "mpeg4", "-b", "150k", "-ab","48000", "-ac", "2", "-ar", "22050", "/sdcard/videokit/out.mp4"};
            ///////////////////////////////////////////////////////////////////////


            LoadJNI vk = new LoadJNI();
            try {

                // complex command
                //vk.run(complexCommand, workFolder, getApplicationContext());

                vk.run(GeneralUtils.utilConvertToComplex(commandStr), mCurrentVideoPath, getApplicationContext());

                // running without command validation
                //vk.run(complexCommand, workFolder, getApplicationContext(), false);

                // copying vk.log (internal native log) to the videokit folder
                //GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);

            } catch (CommandValidationException e) {
                Log.e(Prefs.TAG, "vk run exeption.", e);
                //commandValidationFailedFlag = true;

            } catch (Throwable e) {
                Log.e(Prefs.TAG, "vk run exeption.", e);
            }
            finally {
                if (wakeLock.isHeld())
                    wakeLock.release();
                else{
                    Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
                }
            }
            Log.i(Prefs.TAG, "doInBackground finished");
            return Integer.valueOf(0);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onCancelled() {
            Log.i(Prefs.TAG, "onCancelled");
            //progressDialog.dismiss();
            super.onCancelled();
        }


        @Override
        protected void onPostExecute(Integer result) {
            Log.i(Prefs.TAG, "onPostExecute");
            progressDialog.dismiss();
            super.onPostExecute(result);

            // finished Toast
            /*String rc = null;
            if (commandValidationFailedFlag) {
                rc = "Command Vaidation Failed";
            }
            else {
                rc = GeneralUtils.getReturnCodeFromLog(vkLogPath);
            }
            final String status = rc;
            ChatActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ChatActivity.this, status, Toast.LENGTH_LONG).show();
                    if (status.equals("Transcoding Status: Failed")) {
                        Toast.makeText(ChatActivity.this, "Check: " + vkLogPath + " for more information.", Toast.LENGTH_LONG).show();
                    }
                }
            });*/
        //}

    //}
}
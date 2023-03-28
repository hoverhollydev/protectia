package d4d.com.svd_basic_plus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;
import d4d.com.svd_basic_plus.utils.SessionManager;

/**
 * Created by jp_leon on 24/10/2016.
 */

public class SplashActivity extends AppCompatActivity {
    //Usuario
    private SessionManager session;
    private int Splash_time = 3000;
    public static AppCompatActivity splash;
    private static Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = this;
        inicializarPermisos();
    }

    private static final int PERMISSION_ALL = 1;
    String[] PERMISSIONS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.NFC,
            Manifest.permission.BLUETOOTH_SCAN};

    private void inicializarPermisos(){
        if(!permisosAndroid6plus(this, PERMISSIONS)){
            //Permisos SO Android 6.x.x o superior
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }else{
            validarInicio();
        }
    }

    private void validarInicio(){
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn() == false) {
            ProgressBar mBar = (ProgressBar) findViewById(R.id.progressBarCircular);
            mBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            iniciarLoginActivity();
        } else {
            Splash_time = 0;
            iniciarMainActivity();
        }
    }

    private void iniciarMainActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, Splash_time);
        splash = this;
    }

    private void iniciarLoginActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, Splash_time);
        splash = this;
    }

    @Override
    public void onBackPressed() {
    }

    public static boolean permisosAndroid6plus(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                if ((grantResults.length > 0)
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[2] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[3] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[4] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[5] == PackageManager.PERMISSION_GRANTED)
                        && (grantResults[6] == PackageManager.PERMISSION_GRANTED)) {
                    validarInicio();
                }else{
                    if(!permisosAndroid6plus(this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                    }
                }
                break;
            default:
                break;
        }
    }
}

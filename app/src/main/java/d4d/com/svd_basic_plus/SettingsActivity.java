package d4d.com.svd_basic_plus;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import d4d.com.svd_basic_plus.comunication.WebSocketComunication;
import d4d.com.svd_basic_plus.utils.Utils;

/**
 * Created by jp_leon on 15/03/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(this.getResources().getString(R.string.str_acerca_de));

        Thread.setDefaultUncaughtExceptionHandler(new WebSocketComunication.TopExceptionHandler(this));
        FloatingActionButton map_fab = findViewById(R.id.fab_info_error);
        map_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarErrorAppCorreo();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void enviarErrorAppCorreo(){
        String line;
        String trace="Dispositivo "+Utils.getManufacturer()+" - "+Utils.obtenerModelo()+"\nLog de Errores APP PROTECTia:\n";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(SettingsActivity.this.openFileInput("stack.trace")));
            while((line = reader.readLine()) != null) {
                trace += line+"\n";
            }
        } catch(FileNotFoundException fnfe) {
            // ...
        } catch(IOException ioe) {
            // ...
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = Utils.getManufacturer()+" - "+Utils.obtenerModelo()+" - Log de errores";
        String body = trace + "\n";
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"protectiapiloto@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");
        SettingsActivity.this.startActivity(Intent.createChooser(sendIntent, "Title:"));
        SettingsActivity.this.deleteFile("stack.trace");
    }

}

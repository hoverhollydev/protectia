package d4d.com.svd_basic_plus.ptt;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class Tiempo {
    private Timer timer = new Timer();
    private int segundos=0;

    private static Handler handler;
    private static void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    //Clase interna que funciona como contador
    class Contador extends TimerTask {
        public void run() {
            segundos++;
            System.out.println("segundo: " + segundos);
            /*handler=new Handler();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UsuarioFragmentPtt.txt_tiempo_reservado.setText("segundo: " + segundos);
                }
            });*/

            UsuarioFragmentPtt.setTiempoResevado(segundos);
        }
    }
    //Crea un timer, inicia segundos a 0 y comienza a contar
    public void Contar()
    {
        this.segundos=0;
        timer = new Timer();
        timer.schedule(new Contador(), 0, 1000);
    }
    //Detiene el contador
    public void Detener() {
    timer.cancel();
    }
    //Metodo que retorna los segundos transcurridos
    public int getSegundos()
    {
        return this.segundos;
    }
}
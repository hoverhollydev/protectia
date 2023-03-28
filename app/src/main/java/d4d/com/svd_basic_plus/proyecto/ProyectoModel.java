package d4d.com.svd_basic_plus.proyecto;

import android.graphics.Bitmap;

public class ProyectoModel {
    private boolean isMe;
    private String message;
    private String nombreUser;
    private String tipo;
    private Bitmap previewArchivo;
    private String pathArchivo;
    private String dateTime;

    public boolean getIsme() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNombreUser() {
        return nombreUser;
    }

    public void setNombreUser(String nombreUser) {
        this.nombreUser = nombreUser;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }

    public Bitmap getPreviewArchivo() {
        return previewArchivo;
    }

    public void setPreviewArchivo(Bitmap previewArchivo) {
        this.previewArchivo = previewArchivo;
    }

    public String getPathArchivo() {
        return pathArchivo;
    }

    public void setPathArchivo(String pathArchivo) {
        this.pathArchivo = pathArchivo;
    }
}
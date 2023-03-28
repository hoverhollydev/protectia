package d4d.com.svd_basic_plus.group;

import android.graphics.drawable.Drawable;

public class GrupoDetalle {
        private String grupoId;
        private String grupoNombre;
        private Drawable grupoImagen;

    public GrupoDetalle() {
        super();
    }

    public GrupoDetalle(String grupoId, String grupoNombre, Drawable grupoImagen) {
        super();
        this.grupoId = grupoId;
        this.grupoNombre = grupoNombre;
        this.grupoImagen = grupoImagen;
    }

    public String getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(String grupoId) {
        this.grupoId = grupoId;
    }

    public String getGrupoNombre(){
        return grupoNombre;
    }

    public void setGrupoNombre(String grupoNombre){
        this.grupoNombre = grupoNombre;
    }

    public Drawable getGrupoImagen() {
        return grupoImagen;
    }

    public void setGrupoImagen(Drawable grupoImagen) {
        this.grupoImagen = grupoImagen;
    }

}

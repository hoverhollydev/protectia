package d4d.com.svd_basic_plus.ptt;

public class UsuarioDetalle {
    private String usuarioNombre;
    private String descripcionEstado;
    private boolean estado;
    private boolean si_ptt;
    private boolean si_chat;

    public UsuarioDetalle() {
    }

    public UsuarioDetalle(String usuarioNombre, String descripcionEstado, boolean estado,
                          boolean si_ptt, boolean si_chat) {
        this.usuarioNombre = usuarioNombre;
        this.descripcionEstado = descripcionEstado;
        this.estado = estado;
        this.si_ptt = si_ptt;
        this.si_chat = si_chat;
    }

    public String getUsuarioNombre(){
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre){
        this.usuarioNombre = usuarioNombre;
    }

    public String getDescripcionEstado() {
        return descripcionEstado;
    }

    public void setDescripcionEstado(String descripcionEstado) {
        this.descripcionEstado = descripcionEstado;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean getSiPtt() {
        return si_ptt;
    }

    public void setSiPtt(boolean si_ptt) {
        this.si_ptt = si_ptt;
    }

    public boolean getSiChat() {
        return si_chat;
    }

    public void setSiChat(boolean si_chat) {
        this.si_chat = si_chat;
    }
}
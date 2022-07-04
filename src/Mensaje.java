public class Mensaje
{
    private Integer tipo_mensaje;
    private Integer longitud_mensaje;
    private String  contenido_mensaje;

    public Mensaje() {
        this.tipo_mensaje = 0x00;
        this.longitud_mensaje = 0x00;
        this.contenido_mensaje = "";
    }

    public Mensaje(Integer t, Integer l, String c) {
        this.tipo_mensaje = t;
        this.longitud_mensaje = l;
        this.contenido_mensaje = c;
    }

    public Integer get_tipo_mensaje() {
        return tipo_mensaje;
    }

    public void set_tipo_mensaje(Integer tipo_mensaje) {
        this.tipo_mensaje = tipo_mensaje;
    }

    public Integer get_longitud_mensaje() {
        return longitud_mensaje;
    }

    public void set_longitud_mensaje(Integer longitud_mensaje) {
        this.longitud_mensaje = longitud_mensaje;
    }

    public String get_contenido_mensaje() {
        return contenido_mensaje;
    }

    public void set_contenido_mensaje(String contenido_mensaje) {
        this.contenido_mensaje = contenido_mensaje;
    }
}

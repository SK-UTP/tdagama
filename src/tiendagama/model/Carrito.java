package tiendagama.model;

import java.util.Date;
import java.util.List;

public class Carrito {
    private int id;
    private int idUsuario;
    private Date fecha;
    private List<CarritoDetalle> detalles;

    public Carrito() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public List<CarritoDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<CarritoDetalle> detalles) { this.detalles = detalles; }
}

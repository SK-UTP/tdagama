package tiendagama.model;

public class CarritoDetalle {
    private int id;
    private int idCarrito;
    private int idProducto;
    private int cantidad;

    public CarritoDetalle() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCarrito() { return idCarrito; }
    public void setIdCarrito(int idCarrito) { this.idCarrito = idCarrito; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}


package tiendagama.services;

import tiendagama.dao.CarritoDAO;
import tiendagama.dao.CarritoDetalleDAO;
import tiendagama.dao.ProductoDAO;
import tiendagama.dao.PedidoDAO;
import tiendagama.dao.PedidoDetalleDAO;
import tiendagama.model.Carrito;
import tiendagama.model.CarritoDetalle;
import tiendagama.model.Pedido;
import tiendagama.model.PedidoDetalle;

import java.util.List;
import tiendagama.model.Producto;

public class CarritoService {

    private final CarritoDAO carritoDAO = new CarritoDAO();
    private final CarritoDetalleDAO detalleDAO = new CarritoDetalleDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final PedidoDetalleDAO pedidoDetalleDAO = new PedidoDetalleDAO();

    // Guarda carrito normal y actualiza stock
    public boolean guardarCarrito(Carrito carrito) {

        // 1. Guardar carrito
        int idCar = carritoDAO.crearCarrito(carrito);
        if (idCar <= 0) return false;

        List<CarritoDetalle> lista = carrito.getDetalles();

        for (CarritoDetalle det : lista) {
            det.setIdCarrito(idCar);

            if (!detalleDAO.guardarDetalle(det)) return false;

            boolean okStock = productoDAO.actualizarStock(det.getIdProducto(), det.getCantidad());
            if (!okStock) {
                System.err.println("Stock insuficiente: " + det.getIdProducto());
                return false;
            }
        }
        return true;
    }

 // Convierte carrito en pedido (SIN descontar stock)
public boolean guardarCarritoComoPedido(Carrito carrito) {

    List<CarritoDetalle> lista = carrito.getDetalles();
    if (lista.isEmpty()) return false;

    // 1. Crear pedido
    Pedido pedido = new Pedido();
    pedido.setIdUsuario(carrito.getIdUsuario());
    pedido.setEstado("PENDIENTE");

    double total = 0;
    for (CarritoDetalle det : lista) {
        Producto p = productoDAO.findById(det.getIdProducto());
        if (p == null) {
            System.err.println("Producto no encontrado: " + det.getIdProducto());
            return false;
        }
        total += p.getPrecio() * det.getCantidad();
    }
    pedido.setTotal(total);

    int idPedido = pedidoDAO.crearPedido(pedido);
    if (idPedido <= 0) return false;

    // 2. Guardar detalles del pedido (SIN descontar stock)
    for (CarritoDetalle det : lista) {
        PedidoDetalle pd = new PedidoDetalle();
        pd.setIdPedido(idPedido);
        pd.setIdProducto(det.getIdProducto());
        pd.setCantidad(det.getCantidad());

        Producto p = productoDAO.findById(det.getIdProducto());
        pd.setPrecioUnitario(p.getPrecio());
        pd.setSubtotal(p.getPrecio() * det.getCantidad());

        if (!pedidoDetalleDAO.guardarDetalle(pd)) return false;
    }

    return true;
}
}



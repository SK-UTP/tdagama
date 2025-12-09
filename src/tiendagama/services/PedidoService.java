package tiendagama.services;

import tiendagama.dao.PedidoDAO;
import tiendagama.dao.PedidoDetalleDAO;
import tiendagama.model.Pedido;
import tiendagama.model.PedidoDetalle;
import java.util.List;

public class PedidoService {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final PedidoDetalleDAO detalleDAO = new PedidoDetalleDAO();

    /**
     * Genera un pedido completo con sus detalles.
     * Retorna true si se guarda correctamente, false si ocurre algún error.
     */
    public boolean generarPedido(Pedido pedido) {

        // 1. Crear el pedido principal
        int idPedido = pedidoDAO.crearPedido(pedido);
        if (idPedido <= 0) {
            System.out.println("Error al crear el pedido principal.");
            return false;
        }

        // 2. Guardar cada detalle
        List<PedidoDetalle> detalles = pedido.getDetalles();
        for (PedidoDetalle det : detalles) {
            det.setIdPedido(idPedido);
            if (!detalleDAO.guardarDetalle(det)) {
                System.out.println("Error al guardar el detalle del producto ID " + det.getIdProducto());
                return false;
            }
        }

        System.out.println("Pedido " + idPedido + " generado correctamente con " + detalles.size() + " ítems.");
        return true;
    }
}

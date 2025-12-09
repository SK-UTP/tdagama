package tiendagama.services;

import java.util.List;
import tiendagama.dao.VentaDAO;
import tiendagama.dao.PedidoDAO;
import tiendagama.model.Venta;

public class VentaService {

    private final VentaDAO ventaDAO = new VentaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    public boolean procesarVenta(Venta venta) {

        // registrar venta
        boolean okVenta = ventaDAO.registrarVenta(venta);
        if (!okVenta) return false;

        // actualizar estado del pedido a PAGADO
        return pedidoDAO.cambiarEstado(venta.getIdPedido(), "PAGADO");
    }
    
     
    public List<Venta> listarVentas() {
        return ventaDAO.listar();
    }
}


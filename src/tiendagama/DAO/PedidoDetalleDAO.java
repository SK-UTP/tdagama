package tiendagama.dao;

import tiendagama.config.DBUtil;
import tiendagama.model.PedidoDetalle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDetalleDAO {

    public boolean guardarDetalle(PedidoDetalle detalle) {
        String sql = "INSERT INTO pedido_detalle(id_pedido, id_producto, cantidad, precio_unitario, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detalle.getIdPedido());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getPrecioUnitario());
            ps.setDouble(5, detalle.getSubtotal());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<PedidoDetalle> obtenerDetalles(int idPedido) {
    List<PedidoDetalle> lista = new ArrayList<>();

    String sql = "SELECT * FROM pedido_detalle WHERE id_pedido = ?";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idPedido);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PedidoDetalle det = new PedidoDetalle();
                det.setId(rs.getInt("id"));
                det.setIdPedido(rs.getInt("id_pedido"));
                det.setIdProducto(rs.getInt("id_producto"));
                det.setCantidad(rs.getInt("cantidad"));
                det.setPrecioUnitario(rs.getDouble("precio_unitario"));
                det.setSubtotal(rs.getDouble("subtotal"));
                lista.add(det);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return lista;
}

}

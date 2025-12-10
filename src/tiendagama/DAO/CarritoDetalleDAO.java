package tiendagama.dao;

import tiendagama.config.DBUtil;
import tiendagama.model.CarritoDetalle;
import java.sql.*;

public class CarritoDetalleDAO {

    public boolean guardarDetalle(CarritoDetalle det) {
        String sql = "INSERT INTO carrito_detalle(id_carrito, id_producto, cantidad) VALUES (?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, det.getIdCarrito());
            ps.setInt(2, det.getIdProducto());
            ps.setInt(3, det.getCantidad());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

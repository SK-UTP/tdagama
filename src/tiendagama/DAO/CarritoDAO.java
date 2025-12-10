package tiendagama.dao;

import tiendagama.config.DBUtil;
import tiendagama.model.Carrito;
import java.sql.*;

public class CarritoDAO {

    public int crearCarrito(Carrito carrito) {
        String sql = "INSERT INTO carrito(id_usuario) VALUES (?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, carrito.getIdUsuario());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet g = ps.getGeneratedKeys()) {
                    if (g.next()) return g.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}

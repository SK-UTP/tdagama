package tiendagama.dao;

import tiendagama.config.DBUtil;
import tiendagama.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public int crearPedido(Pedido pedido) {

        String sql = "INSERT INTO pedido(id_usuario, total, estado) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // SE LLAMA getIdUsuario(), getTotal(), getEstado()
            ps.setInt(1, pedido.getIdUsuario());
            ps.setDouble(2, pedido.getTotal());
            ps.setString(3, pedido.getEstado());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // ID generado
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
    public List<Pedido> listarPedidosPendientes() {
    List<Pedido> lista = new ArrayList<>();

    String sql = "SELECT * FROM pedido WHERE estado = 'PENDIENTE'";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Pedido p = new Pedido();
            p.setId(rs.getInt("id"));
            p.setIdUsuario(rs.getInt("id_usuario"));
            p.setFecha(rs.getTimestamp("fecha"));
            p.setTotal(rs.getDouble("total"));
            p.setEstado(rs.getString("estado"));
            lista.add(p);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return lista;
}
    
    public boolean cambiarEstado(int idPedido, String estado) {
    String sql = "UPDATE pedido SET estado = ? WHERE id = ?";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, estado);
        ps.setInt(2, idPedido);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}


}

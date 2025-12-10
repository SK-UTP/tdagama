package tiendagama.dao;

import tiendagama.config.DBUtil;
import tiendagama.model.Venta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    // Registrar una venta
    public boolean registrarVenta(Venta venta) {
        String sql = "INSERT INTO venta(id_pedido, monto_total, tipo_pago) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, venta.getIdPedido());
            ps.setDouble(2, venta.getMontoTotal());
            ps.setString(3, venta.getTipoPago());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                // Obtener el ID generado automáticamente
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        venta.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Listar todas las ventas
    public List<Venta> listar() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT id, id_pedido, monto_total, tipo_pago, fecha FROM venta";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venta v = new Venta();
                v.setId(rs.getInt("id"));
                v.setIdPedido(rs.getInt("id_pedido"));
                v.setMontoTotal(rs.getDouble("monto_total"));
                v.setTipoPago(rs.getString("tipo_pago"));
                v.setFecha(rs.getTimestamp("fecha")); // Timestamp a Date
                lista.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Buscar venta por ID
    public Venta buscarPorId(int id) {
        String sql = "SELECT id, id_pedido, monto_total, tipo_pago, fecha FROM venta WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Venta v = new Venta();
                    v.setId(rs.getInt("id"));
                    v.setIdPedido(rs.getInt("id_pedido"));
                    v.setMontoTotal(rs.getDouble("monto_total"));
                    v.setTipoPago(rs.getString("tipo_pago"));
                    v.setFecha(rs.getTimestamp("fecha"));
                    return v;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Eliminar venta por ID
    public boolean eliminar(int id) {
        String sql = "DELETE FROM venta WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

package tiendagama.dao;

import tiendagama.model.Documento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DocumentoDAO {
    private Connection conn;

    public DocumentoDAO(Connection conn) {
        this.conn = conn;
    }

    public void guardar(Documento doc) throws SQLException {
        String sql = "INSERT INTO documento (id_venta, tipo, serie, folio, subtotal, igv, total, metodo_pago, dni_cliente, ruc_cliente, razon_social) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, doc.getIdVenta());
        ps.setString(2, doc.getTipo());
        ps.setString(3, doc.getSerie());
        ps.setString(4, doc.getFolio());
        ps.setDouble(5, doc.getSubtotal());
        ps.setDouble(6, doc.getIgv());
        ps.setDouble(7, doc.getTotal());
        ps.setString(8, doc.getMetodoPago());
        ps.setString(9, doc.getDniCliente());
        ps.setString(10, doc.getRucCliente());
        ps.setString(11, doc.getRazonSocial());
        ps.executeUpdate();
    }
}

package tiendagama.service;

import tiendagama.model.Documento;
import tiendagama.dao.DocumentoDAO;
import java.sql.Connection;
import java.sql.SQLException;

public class DocumentoService {
    private DocumentoDAO documentoDAO;

    public DocumentoService(Connection conn) {
        this.documentoDAO = new DocumentoDAO(conn);
    }

    public Documento crearDocumento(int idVenta, double montoTotal, String metodoPago, String tipo, String dniCliente, String rucCliente, String razonSocial) throws SQLException {
        Documento doc = new Documento();
        doc.setIdVenta(idVenta);
        doc.setTipo(tipo);

        // Numeración automática
        String serie = tipo.equals("BOLETA") ? "B001" : "F001";
        String folio = String.valueOf(System.currentTimeMillis());
        doc.setSerie(serie);
        doc.setFolio(folio);

        // Calcular IGV
        double subtotal = montoTotal / 1.18;
        double igv = montoTotal - subtotal;
        doc.setSubtotal(subtotal);
        doc.setIgv(igv);
        doc.setTotal(montoTotal);

        doc.setMetodoPago(metodoPago);
        doc.setDniCliente(dniCliente);
        doc.setRucCliente(rucCliente);
        doc.setRazonSocial(razonSocial);

        documentoDAO.guardar(doc);
        return doc;
    }
}

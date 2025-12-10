package tiendagama.controller;

import tiendagama.model.Documento;
import tiendagama.service.DocumentoService;
import java.sql.Connection;

public class DocumentoController {
    private DocumentoService documentoService;

    public DocumentoController(Connection conn) {
        this.documentoService = new DocumentoService(conn);
    }

    public Documento generarDocumento(int idVenta, double montoTotal, String metodoPago, String tipo, String dni, String ruc, String razonSocial) throws Exception {
        return documentoService.crearDocumento(idVenta, montoTotal, metodoPago, tipo, dni, ruc, razonSocial);
    }
}

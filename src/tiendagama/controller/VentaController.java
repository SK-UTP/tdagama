package tiendagama.controller;

import java.util.List;
import tiendagama.model.Venta;
import tiendagama.services.VentaService;

public class VentaController {

    private final VentaService service = new VentaService();

    public boolean procesarVenta(Venta venta) {
        return service.procesarVenta(venta);
    }
     public List<Venta> listar() {
        return service.listarVentas();
    }
}

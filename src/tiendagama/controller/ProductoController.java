package tiendagama.controller;

import tiendagama.model.Producto;
import tiendagama.services.ProductoService;

import java.util.List;

public class ProductoController {

    private final ProductoService service = new ProductoService();

    // API usada por CrudProductos
    public List<Producto> listar() {
        return service.listar();
    }

    public boolean crear(Producto p) {
        return service.crear(p);
    }

    public boolean actualizar(Producto p) {
        return service.actualizar(p);
    }

    public boolean eliminar(int id) {
        return service.eliminar(id);
    }

    // alias para compatibilidad con nombres antiguos (ProductoForm usaba 'insertar')
    public boolean insertar(Producto p) {
        return crear(p);
    }

    // NUEVO: buscar producto por id
    public Producto findById(int id) {
        return service.findById(id);
    }
}

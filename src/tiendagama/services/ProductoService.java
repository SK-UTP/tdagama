package tiendagama.services;

import tiendagama.dao.ProductoDAO;
import tiendagama.model.Producto;
import java.util.List;

public class ProductoService {

    private final ProductoDAO dao = new ProductoDAO();

    public List<Producto> listar() {
        return dao.listar();
    }

    public boolean crear(Producto p) {
        return dao.crear(p);
    }

    public boolean actualizar(Producto p) {
        return dao.actualizar(p);
    }

    public boolean eliminar(int id) {
        return dao.eliminar(id);
    }

    public boolean actualizarStock(int idProducto, int cantidad) {
        return dao.actualizarStock(idProducto, cantidad);
    }
    
     public Producto findById(int id) {
        return dao.getById(id);
    }
}

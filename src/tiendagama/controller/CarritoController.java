package tiendagama.controller;

import tiendagama.model.Carrito;
import tiendagama.services.CarritoService;

public class CarritoController {

    private final CarritoService service = new CarritoService();

      
public boolean guardarCarritoComoPedido(Carrito carrito) {
    return service.guardarCarritoComoPedido(carrito);
}
}

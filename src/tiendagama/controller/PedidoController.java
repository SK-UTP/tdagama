package tiendagama.controller;

import tiendagama.model.Pedido;
import tiendagama.services.PedidoService;

public class PedidoController {

    private final PedidoService service = new PedidoService();

    public boolean generarPedido(Pedido p) {
        return service.generarPedido(p);
    }
}


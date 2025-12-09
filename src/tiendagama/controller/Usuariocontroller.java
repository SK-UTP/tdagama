package tiendagama.controller;

import tiendagama.model.Usuario;
import tiendagama.service.UsuarioService;
import java.util.List;

public class Usuariocontroller {

    private final UsuarioService service = new UsuarioService();

    public Usuario autenticar(String correo, String pass) {
        return service.autenticar(correo, pass);
    }

    public boolean crear(Usuario u) {
        return service.crear(u);
    }

    public boolean actualizar(Usuario u) {
        return service.actualizar(u);
    }

    public boolean eliminar(int id) {
        return service.eliminar(id);
    }

    public List<Usuario> listar() {
        return service.listar();
    }
}

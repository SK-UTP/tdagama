package tiendagama.service;

import tiendagama.dao.UsuarioDAO;
import tiendagama.model.Usuario;

import java.util.List;

public class UsuarioService {

    public final UsuarioDAO dao = new UsuarioDAO();

    public Usuario autenticar(String correo, String pass) {
        return dao.autenticar(correo, pass);
    }

    public boolean crear(Usuario u) {
        return dao.crear(u);
    }

    public boolean actualizar(Usuario u) {
        return dao.actualizar(u);
    }

    public boolean eliminar(int id) {
        return dao.eliminar(id);
    }

    public List<Usuario> listar() {
        return dao.listarUsuarios();
    }
}

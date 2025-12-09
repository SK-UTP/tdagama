package tiendagama.view;

import tiendagama.model.Usuario;
import tiendagama.utils.UIUtils;
// importa tu controlador exactamente como esté en tu proyecto
import tiendagama.controller.Usuariocontroller;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CrudUsuarios extends JFrame {

    private final Usuariocontroller controller = new Usuariocontroller();
    private DefaultTableModel modelo;
    private JTable table;

    public CrudUsuarios() {
        UIUtils.applyAppStyle();
        setTitle("Gestión de Usuarios");
        setSize(900,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        init();
        load();
    }

    private void init() {
        modelo = new DefaultTableModel(new Object[]{"ID","Nombre","Apellido","Correo","Teléfono","Rol"},0);
        table = new JTable(modelo);

        JScrollPane scroll = new JScrollPane(table);

        JButton btnNuevo = UIUtils.makePrimary("Nuevo");
        JButton btnEditar = UIUtils.makeSecondary("Editar");
        JButton btnEliminar = UIUtils.makeSecondary("Eliminar");
        JButton btnCerrar = UIUtils.makeSmall("Cerrar");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(btnCerrar);
        top.add(btnNuevo);
        top.add(btnEditar);
        top.add(btnEliminar);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnNuevo.addActionListener(e -> {
            UsuarioForm f = new UsuarioForm(this, null);
            f.setVisible(true);
        });

        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());

        btnCerrar.addActionListener(e -> this.dispose());

        table.getSelectionModel().addListSelectionListener(e -> {
            // se puede activar botones según selección
        });
    }

    public void load() {
        modelo.setRowCount(0);
        List<Usuario> list = controller.listar(); // asumir listar() público en el controlador
        if (list == null) return;
        for (Usuario u : list) {
            modelo.addRow(new Object[]{u.getId(), u.getNombre(), u.getApellido(), u.getCorreo(), u.getTelefono(), u.getRol()});
        }
    }

    private void editar() {
        int r = table.getSelectedRow(); if (r<0) { JOptionPane.showMessageDialog(this,"Seleccione"); return; }
        Usuario u = new Usuario();
        u.setId((int)modelo.getValueAt(r,0));
        u.setNombre((String)modelo.getValueAt(r,1));
        u.setApellido((String)modelo.getValueAt(r,2));
        u.setCorreo((String)modelo.getValueAt(r,3));
        u.setTelefono((String)modelo.getValueAt(r,4));
        u.setRol((String)modelo.getValueAt(r,5));
        UsuarioForm f = new UsuarioForm(this, u);
        f.setVisible(true);
    }

    private void eliminar() {
        int r = table.getSelectedRow(); if (r<0) { JOptionPane.showMessageDialog(this,"Seleccione"); return; }
        int id = (int)modelo.getValueAt(r,0);
        if (JOptionPane.showConfirmDialog(this,"Eliminar usuario "+id+" ?")==JOptionPane.YES_OPTION) {
            if (controller.eliminar(id)) load(); else JOptionPane.showMessageDialog(this,"Error al eliminar");
        }
    }
}

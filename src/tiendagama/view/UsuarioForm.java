package tiendagama.view;

import tiendagama.model.Usuario;
import tiendagama.utils.UIUtils;
import tiendagama.controller.Usuariocontroller;

import javax.swing.*;
import java.awt.*;

public class UsuarioForm extends JDialog {

    private final Usuariocontroller controller = new Usuariocontroller();
    private final CrudUsuarios padre;
    private Usuario usuario;

    private JTextField txtNombre, txtApellido, txtCorreo, txtTelefono;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;

    public UsuarioForm(CrudUsuarios padre, Usuario usuario) {
        super(padre, true);
        UIUtils.applyAppStyle();
        this.padre = padre;
        this.usuario = usuario;

        setTitle(usuario == null ? "Nuevo Usuario" : "Editar Usuario");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        pack(); // ajusta ventana al contenido
        setLocationRelativeTo(padre);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        txtNombre = new JTextField(20);
        txtApellido = new JTextField(20);
        txtCorreo = new JTextField(20);
        txtTelefono = new JTextField(15);
        txtPassword = new JPasswordField(20);
        cmbRol = new JComboBox<>(new String[]{"ADMIN","VENDEDOR","CLIENTE"});

        int row = 0;
        c.gridx=0; c.gridy=row; panel.add(new JLabel("Nombre:"), c);
        c.gridx=1; panel.add(txtNombre, c);

        row++;
        c.gridx=0; c.gridy=row; panel.add(new JLabel("Apellido:"), c);
        c.gridx=1; panel.add(txtApellido, c);

        row++;
        c.gridx=0; c.gridy=row; panel.add(new JLabel("Correo:"), c);
        c.gridx=1; panel.add(txtCorreo, c);

        row++;
        c.gridx=0; c.gridy=row; panel.add(new JLabel("Teléfono:"), c);
        c.gridx=1; panel.add(txtTelefono, c);

        row++;
        c.gridx=0; c.gridy=row; panel.add(new JLabel("Password:"), c);
        c.gridx=1; panel.add(txtPassword, c);

        row++;
        c.gridx=0; c.gridy=row; panel.add(new JLabel("Rol:"), c);
        c.gridx=1; panel.add(cmbRol, c);

        // Panel inferior con botones centrados
        row++;
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));
        JButton btnGuardar = UIUtils.makePrimary("Guardar");
        JButton btnCerrar = UIUtils.makeSecondary("Cerrar");
        bottom.add(btnCerrar);
        bottom.add(btnGuardar);

        c.gridx=0; c.gridy=row; c.gridwidth=2; panel.add(bottom, c);

        add(panel);

        if (usuario != null) {
            txtNombre.setText(usuario.getNombre());
            txtApellido.setText(usuario.getApellido());
            txtCorreo.setText(usuario.getCorreo());
            txtTelefono.setText(usuario.getTelefono());
            txtPassword.setText(usuario.getPassword());
            cmbRol.setSelectedItem(usuario.getRol() != null ? usuario.getRol().toUpperCase() : "CLIENTE");
        }

        btnGuardar.addActionListener(e -> guardar());
        btnCerrar.addActionListener(e -> dispose());
    }

    private void guardar() {
        if (usuario == null) usuario = new Usuario();
        try {
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setApellido(txtApellido.getText().trim());
            usuario.setCorreo(txtCorreo.getText().trim());
            usuario.setTelefono(txtTelefono.getText().trim());
            usuario.setPassword(new String(txtPassword.getPassword()).trim());
            usuario.setRol(String.valueOf(cmbRol.getSelectedItem()).toUpperCase().trim());

            if (usuario.getNombre().isEmpty() || usuario.getCorreo().isEmpty() || usuario.getPassword().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete nombre, correo y contraseña.");
                return;
            }

            boolean ok = (usuario.getId() == 0) ? controller.crear(usuario) : controller.actualizar(usuario);

            if (ok) {
                padre.load();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,"Error al guardar (revisar consola)");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,"Datos inválidos: " + ex.getMessage());
        }
    }
}

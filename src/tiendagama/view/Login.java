package tiendagama.view;

import tiendagama.controller.Usuariocontroller;
import tiendagama.model.Usuario;
import tiendagama.utils.UIUtils;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {

    private final Usuariocontroller usuarioController = new Usuariocontroller();
    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public Login() {
        UIUtils.applyAppStyle();
        setTitle("Login - TiendaGama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setResizable(false);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("TIENDAGAMA - LOGIN");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(lblTitulo, c);

        c.gridwidth = 1;
        c.gridy++;

        panel.add(new JLabel("Correo:"), c);
        txtCorreo = new JTextField();
        txtCorreo.setPreferredSize(new Dimension(200, 30));
        c.gridx = 1;
        panel.add(txtCorreo, c);

        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Contraseña:"), c);
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(200, 30));
        c.gridx = 1;
        panel.add(txtPassword, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(180, 35));
        panel.add(btnLogin, c);

        add(panel);

        btnLogin.addActionListener(e -> btnLoginActionPerformed());
    }

    private void btnLoginActionPerformed() {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (correo.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos");
            return;
        }

        Usuario usuario = usuarioController.autenticar(correo, pass);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Bienvenido " + usuario.getNombre());
            this.dispose();
            new DashBoard(usuario.getId(), usuario.getRol()).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}

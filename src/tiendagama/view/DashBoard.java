package tiendagama.view;

import tiendagama.utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DashBoard extends JFrame {

    private final int usuarioId;
    private final String rol;

    public DashBoard(int usuarioId, String rol) {
        UIUtils.applyAppStyle();
        this.usuarioId = usuarioId;
        this.rol = rol;

        setTitle("TiendaGama - Panel Principal");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        initComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;

        JButton btnProductos = UIUtils.makePrimary("Gestión de Productos");
        JButton btnUsuarios = UIUtils.makePrimary("Gestión de Usuarios");
        JButton btnCarrito = UIUtils.makeSecondary("Carrito");
        JButton btnPedidos = UIUtils.makeSecondary("Pedidos");
        JButton btnVentas = UIUtils.makeSecondary("Ventas");
        JButton btnReportes = UIUtils.makeSecondary("Reportes");
        JButton btnDocumentos = UIUtils.makeSecondary("Boletas/Facturas");
        JButton btnSalir = UIUtils.makeSmall("Salir");
        
        
        btnSalir.setPreferredSize(new Dimension(60, 25));  
        btnSalir.setFont(new Font("Arial", Font.PLAIN, 10));
        btnProductos.setPreferredSize(new Dimension(150, 40));
        btnUsuarios.setPreferredSize(new Dimension(150, 40));
        btnCarrito.setPreferredSize(new Dimension(150, 40));
        btnPedidos.setPreferredSize(new Dimension(150, 40));
        btnVentas.setPreferredSize(new Dimension(150, 40));
        btnReportes.setPreferredSize(new Dimension(150, 40));
        btnDocumentos.setPreferredSize(new Dimension(150, 40));
        

        // permisos por rol
        if (!"ADMIN".equalsIgnoreCase(rol)) {
            btnUsuarios.setEnabled(false);
        }
        if ("CLIENTE".equalsIgnoreCase(rol)) {
            btnVentas.setEnabled(false);
            btnPedidos.setEnabled(false);
            btnDocumentos.setEnabled(false); // opcional: clientes no generan documentos
        }

       
        c.gridx = 0; c.gridy = 0; panel.add(btnProductos, c);
        c.gridx = 1; c.gridy = 0; panel.add(btnUsuarios, c);
        c.gridx = 0; c.gridy = 1; panel.add(btnCarrito, c);
        c.gridx = 1; c.gridy = 1; panel.add(btnPedidos, c);
        c.gridx = 0; c.gridy = 2; panel.add(btnVentas, c);
        c.gridx = 1; c.gridy = 2; panel.add(btnReportes, c);
        c.gridx = 0; c.gridy = 3; panel.add(btnDocumentos, c); 
        c.gridx = 0; c.gridy = 4; c.gridwidth = 2; panel.add(btnSalir, c);
        c.gridx = 1; 
        c.gridy = 4; 
        c.gridwidth = 1; 
        c.fill = GridBagConstraints.NONE;   
        c.anchor = GridBagConstraints.LINE_END; 
        panel.add(btnSalir, c);


        
       
       add(panel);

        
        btnProductos.addActionListener(e -> new CrudProductos().setVisible(true));
        btnUsuarios.addActionListener(e -> new CrudUsuarios().setVisible(true));
        btnCarrito.addActionListener(e -> new CarritoView(usuarioId).setVisible(true));
        btnPedidos.addActionListener(e -> new OrdenPedido(usuarioId).setVisible(true));
        btnVentas.addActionListener(e -> new VentasView().setVisible(true));
        btnReportes.addActionListener(e -> new ReportesView().setVisible(true));
        btnDocumentos.addActionListener(e -> new DocumentoView().setVisible(true)); // ACCIÓN NUEVA
        btnSalir.addActionListener(e -> confirmExit());
    }

    private void confirmExit() {
        int opt = JOptionPane.showConfirmDialog(this, "¿Desea cerrar la aplicación?", "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "¡Hasta luego!");
            dispose();
        }
    }
}

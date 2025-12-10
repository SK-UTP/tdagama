package tiendagama.view;

import tiendagama.model.Documento;
import tiendagama.controller.DocumentoController;
import tiendagama.config.DBconfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class DocumentoView extends JFrame {
    private JComboBox<String> cmbVentas;   // Combo para seleccionar venta
    private JTextField txtMontoTotal, txtMetodoPago, txtDni, txtCliente;
    private JComboBox<String> cmbTipo;
    private JButton btnGenerar;

    private DocumentoController documentoController;

    public DocumentoView() {
        setTitle("Generar Boleta/Factura");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Venta
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Venta:"), gbc);
        gbc.gridx = 1;
        cmbVentas = new JComboBox<>();
        panel.add(cmbVentas, gbc);

        // Monto Total
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Monto Total:"), gbc);
        gbc.gridx = 1;
        txtMontoTotal = new JTextField();
        txtMontoTotal.setEditable(false);
        panel.add(txtMontoTotal, gbc);

        // Método Pago
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Método Pago:"), gbc);
        gbc.gridx = 1;
        txtMetodoPago = new JTextField();
        txtMetodoPago.setEditable(false);
        panel.add(txtMetodoPago, gbc);

        // Tipo Documento
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tipo Documento:"), gbc);
        gbc.gridx = 1;
        cmbTipo = new JComboBox<>(new String[]{"BOLETA", "FACTURA"});
        panel.add(cmbTipo, gbc);

        // Cliente
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1;
        txtCliente = new JTextField();
        txtCliente.setEditable(false);
        panel.add(txtCliente, gbc);

        // DNI Cliente
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("DNI Cliente:"), gbc);
        gbc.gridx = 1;
        txtDni = new JTextField();
        txtDni.setEditable(false);
        panel.add(txtDni, gbc);

        // Botón Generar
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnGenerar = new JButton("Generar Documento");
        panel.add(btnGenerar, gbc);

        add(panel);

        try {
            Connection conn = DriverManager.getConnection(DBconfig.URL, DBconfig.USER, DBconfig.PASS);
            documentoController = new DocumentoController(conn);

            // Cargar ventas en el combo con JOIN a usuario
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT v.id, v.monto_total, v.tipo_pago, u.nombre, u.apellido, u.dni " +
                "FROM venta v " +
                "JOIN pedido p ON v.id_pedido = p.id " +
                "JOIN usuario u ON p.id_usuario = u.id"
            );
            while (rs.next()) {
                String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                cmbVentas.addItem(rs.getInt("id") + " - " + nombreCompleto);
            }

            // Listener para cargar datos de la venta seleccionada
            cmbVentas.addActionListener(e -> {
                try {
                    String seleccion = (String) cmbVentas.getSelectedItem();
                    if (seleccion != null) {
                        int idVenta = Integer.parseInt(seleccion.split(" - ")[0]);
                        PreparedStatement ps = conn.prepareStatement(
                            "SELECT v.monto_total, v.tipo_pago, u.nombre, u.apellido, u.dni " +
                            "FROM venta v " +
                            "JOIN pedido p ON v.id_pedido = p.id " +
                            "JOIN usuario u ON p.id_usuario = u.id " +
                            "WHERE v.id=?"
                        );
                        ps.setInt(1, idVenta);
                        ResultSet rsVenta = ps.executeQuery();
                        if (rsVenta.next()) {
                            txtMontoTotal.setText(String.valueOf(rsVenta.getDouble("monto_total")));
                            txtMetodoPago.setText(rsVenta.getString("tipo_pago"));
                            txtCliente.setText(rsVenta.getString("nombre") + " " + rsVenta.getString("apellido"));
                            txtDni.setText(rsVenta.getString("dni"));
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error cargando datos de venta: " + ex.getMessage());
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage());
        }

        btnGenerar.addActionListener((ActionEvent e) -> generarDocumento());
    }

    private void generarDocumento() {
        try {
            String seleccion = (String) cmbVentas.getSelectedItem();
            int idVenta = Integer.parseInt(seleccion.split(" - ")[0]);
            double montoTotal = Double.parseDouble(txtMontoTotal.getText());
            String metodoPago = txtMetodoPago.getText();
            String tipo = cmbTipo.getSelectedItem().toString();
            String dni = txtDni.getText();

            Documento doc = documentoController.generarDocumento(idVenta, montoTotal, metodoPago, tipo, dni, null, null);

            JOptionPane.showMessageDialog(this,
                    "Documento generado:\n" +
                    "Tipo: " + doc.getTipo() + "\n" +
                    "Serie: " + doc.getSerie() + "\n" +
                    "Folio: " + doc.getFolio() + "\n" +
                    "Total: " + doc.getTotal());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

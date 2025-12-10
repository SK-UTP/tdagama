package tiendagama.view;

import tiendagama.dao.PedidoDAO;
import tiendagama.dao.PedidoDetalleDAO;
import tiendagama.model.Pedido;
import tiendagama.model.PedidoDetalle;
import tiendagama.model.Producto;
import tiendagama.controller.ProductoController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportePedidosView extends JFrame {

    private PedidoDAO pedidoDAO = new PedidoDAO();
    private PedidoDetalleDAO detalleDAO = new PedidoDetalleDAO();
    private ProductoController productoController = new ProductoController();

    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnExportar, btnFiltrar, btnImprimir;
    private JSpinner fechaInicio, fechaFin;
    private JTextField txtUsuario;

    public ReportePedidosView() {
        setTitle("Reporte de Pedidos");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        cargarPedidos(null, null, null);
    }

    private void initComponents() {
        modelo = new DefaultTableModel(
                new Object[]{"ID Pedido", "Usuario", "Fecha", "Total", "Estado"}, 0
        );
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        // Filtros
        fechaInicio = new JSpinner(new SpinnerDateModel());
        fechaFin = new JSpinner(new SpinnerDateModel());
        txtUsuario = new JTextField(10);
        JSpinner.DateEditor deInicio = new JSpinner.DateEditor(fechaInicio, "yyyy-MM-dd");
        JSpinner.DateEditor deFin = new JSpinner.DateEditor(fechaFin, "yyyy-MM-dd");
        fechaInicio.setEditor(deInicio);
        fechaFin.setEditor(deFin);

        btnFiltrar = new JButton("Aplicar Filtro");
        btnFiltrar.addActionListener(e -> {
            Date inicio = (Date) fechaInicio.getValue();
            Date fin = (Date) fechaFin.getValue();
            Integer usuarioId = null;
            if (!txtUsuario.getText().isEmpty()) {
                try { usuarioId = Integer.parseInt(txtUsuario.getText()); } catch (Exception ignored) {}
            }
            cargarPedidos(inicio, fin, usuarioId);
        });

        btnExportar = new JButton("Exportar a CSV");
        btnExportar.addActionListener(e -> exportarCSV());

        btnImprimir = new JButton("Imprimir");
        btnImprimir.addActionListener(e -> {
            try { tabla.print(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error al imprimir: " + ex.getMessage()); }
        });

        JPanel filtros = new JPanel(new FlowLayout());
        filtros.add(new JLabel("Fecha inicio:")); filtros.add(fechaInicio);
        filtros.add(new JLabel("Fecha fin:")); filtros.add(fechaFin);
        filtros.add(new JLabel("Usuario ID:")); filtros.add(txtUsuario);
        filtros.add(btnFiltrar);
        filtros.add(btnExportar);
        filtros.add(btnImprimir);

        add(filtros, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void cargarPedidos(Date inicio, Date fin, Integer usuarioId) {
        modelo.setRowCount(0);
        List<Pedido> pedidos = pedidoDAO.listarPedidosPendientes();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Pedido p : pedidos) {
            boolean incluir = true;
            if (inicio != null && p.getFecha().before(inicio)) incluir = false;
            if (fin != null && p.getFecha().after(fin)) incluir = false;
            if (usuarioId != null && p.getIdUsuario() != usuarioId) incluir = false;

            if (incluir) {
                modelo.addRow(new Object[]{
                        p.getId(),
                        p.getIdUsuario(),
                        sdf.format(p.getFecha()),
                        p.getTotal(),
                        p.getEstado()
                });
            }
        }
    }

    private void exportarCSV() {
        String nombreArchivo = "ReportePedidos.csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            for (int i = 0; i < modelo.getColumnCount(); i++) {
                writer.print(modelo.getColumnName(i));
                if (i < modelo.getColumnCount() - 1) writer.print(",");
            }
            writer.println();

            for (int i = 0; i < modelo.getRowCount(); i++) {
                for (int j = 0; j < modelo.getColumnCount(); j++) {
                    writer.print(modelo.getValueAt(i, j));
                    if (j < modelo.getColumnCount() - 1) writer.print(",");
                }
                writer.println();
            }

            JOptionPane.showMessageDialog(this, "Exportado correctamente a:\n" + new java.io.File(nombreArchivo).getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage());
        }
    }
}

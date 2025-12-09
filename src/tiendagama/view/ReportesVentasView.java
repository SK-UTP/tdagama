package tiendagama.view;

import tiendagama.controller.VentaController;
import tiendagama.model.Venta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportesVentasView extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnExportar, btnFiltrar, btnImprimir;
    private JLabel lblTotal;
    private VentaController ventaController = new VentaController();
    private JSpinner fechaInicio, fechaFin;
    private JComboBox<String> cmbPago;

    public ReportesVentasView() {
        setTitle("Reporte de Ventas");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        cargarVentas(null, null, null);
    }

    private void initComponents() {
        modelo = new DefaultTableModel(
                new Object[]{"ID Venta", "ID Pedido", "Monto", "Pago", "Fecha"}, 0
        );
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        lblTotal = new JLabel("Total recaudado: 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));

        fechaInicio = new JSpinner(new SpinnerDateModel());
        fechaFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor deInicio = new JSpinner.DateEditor(fechaInicio, "yyyy-MM-dd");
        JSpinner.DateEditor deFin = new JSpinner.DateEditor(fechaFin, "yyyy-MM-dd");
        fechaInicio.setEditor(deInicio);
        fechaFin.setEditor(deFin);

        cmbPago = new JComboBox<>(new String[]{"TODOS", "EFECTIVO", "TARJETA", "TRANSFERENCIA"});

        btnFiltrar = new JButton("Aplicar Filtro");
        btnFiltrar.addActionListener(e -> {
            Date inicio = (Date) fechaInicio.getValue();
            Date fin = (Date) fechaFin.getValue();
            String tipoPago = cmbPago.getSelectedItem().toString();
            if (tipoPago.equals("TODOS")) tipoPago = null;
            cargarVentas(inicio, fin, tipoPago);
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
        filtros.add(new JLabel("Tipo de pago:")); filtros.add(cmbPago);
        filtros.add(btnFiltrar);
        filtros.add(btnExportar);
        filtros.add(btnImprimir);

        add(filtros, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);
    }

    private void cargarVentas(Date inicio, Date fin, String tipoPago) {
        modelo.setRowCount(0);
        List<Venta> lista = ventaController.listar();
        double total = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Venta v : lista) {
            boolean incluir = true;
            if (inicio != null && v.getFecha().before(inicio)) incluir = false;
            if (fin != null && v.getFecha().after(fin)) incluir = false;
            if (tipoPago != null && !v.getTipoPago().equalsIgnoreCase(tipoPago)) incluir = false;

            if (incluir) {
                modelo.addRow(new Object[]{
                        v.getId(),
                        v.getIdPedido(),
                        v.getMontoTotal(),
                        v.getTipoPago(),
                        sdf.format(v.getFecha())
                });
                total += v.getMontoTotal();
            }
        }

        lblTotal.setText("Total recaudado: " + total);
    }

    private void exportarCSV() {
        String nombreArchivo = "ReporteVentas.csv";
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

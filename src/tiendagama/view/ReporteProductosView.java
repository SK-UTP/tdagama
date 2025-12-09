package tiendagama.view;

import tiendagama.controller.ProductoController;
import tiendagama.model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class ReporteProductosView extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private ProductoController controller = new ProductoController();
    private JButton btnExportar, btnFiltrar, btnImprimir;
    private JTextField txtBuscar;

    public ReporteProductosView() {
        setTitle("Reporte de Productos");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        cargarProductos(null);
    }

    private void initComponents() {
        modelo = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción", "Stock", "Precio"}, 0
        );
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);

        txtBuscar = new JTextField(10);

        btnFiltrar = new JButton("Aplicar Filtro");
        btnFiltrar.addActionListener(e -> {
            String filtro = txtBuscar.getText().trim();
            cargarProductos(filtro.isEmpty() ? null : filtro);
        });

        btnExportar = new JButton("Exportar a CSV");
        btnExportar.addActionListener(e -> exportarCSV());

        btnImprimir = new JButton("Imprimir");
        btnImprimir.addActionListener(e -> {
            try { tabla.print(); } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error al imprimir: " + ex.getMessage()); }
        });

        JPanel filtros = new JPanel(new FlowLayout());
        filtros.add(new JLabel("Buscar:")); filtros.add(txtBuscar);
        filtros.add(btnFiltrar);
        filtros.add(btnExportar);
        filtros.add(btnImprimir);

        add(filtros, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void cargarProductos(String filtro) {
        modelo.setRowCount(0);
        List<Producto> lista = controller.listar();
        for (Producto p : lista) {
            if (filtro != null && !p.getNombre().toLowerCase().contains(filtro.toLowerCase()) && !String.valueOf(p.getId()).equals(filtro)) {
                continue;
            }
            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getDescripcion(),
                    p.getStock(),
                    p.getPrecio()
            });
        }
    }

    private void exportarCSV() {
        String nombreArchivo = "ReporteProductos.csv";
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

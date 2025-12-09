package tiendagama.view;

import javax.swing.*;
import java.awt.*;

public class ReportesView extends JFrame {

    private JButton btnVentas, btnPedidos, btnProductos;

    public ReportesView() {
        setTitle("Reportes - TiendaGama");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        btnVentas = new JButton("Reporte de Ventas");
        btnPedidos = new JButton("Reporte de Pedidos");
        btnProductos = new JButton("Reporte de Productos");

        panel.add(btnVentas);
        panel.add(btnPedidos);
        panel.add(btnProductos);

        add(panel);

        btnVentas.addActionListener(e -> new ReportesVentasView().setVisible(true));
        btnPedidos.addActionListener(e -> new ReportePedidosView().setVisible(true));
        btnProductos.addActionListener(e -> new ReporteProductosView().setVisible(true));
    }
}

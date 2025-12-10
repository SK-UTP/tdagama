package tiendagama.view;

import tiendagama.controller.VentaController;
import tiendagama.dao.PedidoDAO;
import tiendagama.dao.PedidoDetalleDAO;
import tiendagama.model.Pedido;
import tiendagama.model.PedidoDetalle;
import tiendagama.model.Producto;
import tiendagama.model.Venta;
import tiendagama.controller.ProductoController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentasView extends JFrame {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final PedidoDetalleDAO detalleDAO = new PedidoDetalleDAO();
    private final VentaController ventaController = new VentaController();
    private final ProductoController productoController = new ProductoController();
    private JTable tablaVentas;
    private DefaultTableModel modeloVentas;
    
    private JTable tablaPedidos, tablaDetalles;
    private DefaultTableModel modeloPedidos, modeloDetalles;
    private JComboBox<String> cmbPago;
    private JButton btnProcesar;

    public VentasView() {
        setTitle("Registro de Ventas - TiendaGama");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        cargarPedidos();  // SOLO pedidos ENVIADOS
        cargarVentas();

    }

    private void initComponents() {
        JPanel base = new JPanel(new BorderLayout(10,10));
        base.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloPedidos = new DefaultTableModel(new Object[]{"ID", "Usuario", "Fecha", "Total"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaPedidos = new JTable(modeloPedidos);

        JScrollPane sp1 = new JScrollPane(tablaPedidos);
        sp1.setBorder(BorderFactory.createTitledBorder("Pedidos ENVIADOS (Listos para Venta)"));

        modeloDetalles = new DefaultTableModel(new Object[]{"Producto", "Descripción", "Cant", "Precio", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaDetalles = new JTable(modeloDetalles);

        JScrollPane sp2 = new JScrollPane(tablaDetalles);
        sp2.setBorder(BorderFactory.createTitledBorder("Detalles del Pedido"));

        modeloVentas = new DefaultTableModel(
        new Object[]{"ID Venta", "ID Pedido", "Total", "Pago", "Fecha"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaVentas = new JTable(modeloVentas);

        JScrollPane sp3 = new JScrollPane(tablaVentas);
        sp3.setBorder(BorderFactory.createTitledBorder("Ventas Registradas"));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cmbPago = new JComboBox<>(new String[]{"EFECTIVO","TARJETA","TRANSFERENCIA"});
        btnProcesar = new JButton("Procesar Venta");

        bottom.add(new JLabel("Tipo de pago: "));
        bottom.add(cmbPago);
        bottom.add(btnProcesar);

        JSplitPane splitTop = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp1, sp2);
        splitTop.setResizeWeight(0.50);

        JSplitPane splitMain = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitTop, sp3);
        splitMain.setResizeWeight(0.75);

        base.add(splitMain, BorderLayout.CENTER);
       // JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp1, sp2);
      //  split.setResizeWeight(0.55);

       // base.add(split, BorderLayout.CENTER);
        base.add(bottom, BorderLayout.SOUTH);

        add(base);

        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarDetalles();
        });

        btnProcesar.addActionListener(e -> procesarVenta());
    }

    private void cargarPedidos() {
      modeloPedidos.setRowCount(0);

      // Cargar solo pedidos enviados
      List<Pedido> lista = pedidoDAO.listarPedidosPendientes();

      for (Pedido p : lista) {

          double total = p.getTotal();  // ← p.getTotal() YA ES DOUBLE

          modeloPedidos.addRow(new Object[]{
                  p.getId(),
                  p.getIdUsuario(),
                  p.getFecha(),
                  total
          });
      }
  }

        private void cargarVentas() {
            modeloVentas.setRowCount(0);

            List<Venta> lista = ventaController.listar();

            for (Venta v : lista) {
                modeloVentas.addRow(new Object[]{
                        v.getId(),
                        v.getIdPedido(),
                        v.getMontoTotal(),
                        v.getTipoPago(),
                        v.getFecha()
                });
            }
        }

   private void cargarDetalles() {
    int fila = tablaPedidos.getSelectedRow();
    if (fila < 0) return;
    int modelRow = tablaPedidos.convertRowIndexToModel(fila);
    int idPedido = (int) modeloPedidos.getValueAt(modelRow, 0);

    modeloDetalles.setRowCount(0);
    List<PedidoDetalle> lista = detalleDAO.obtenerDetalles(idPedido);

    for (PedidoDetalle d : lista) {

        // Obtener descripción del producto
        String descripcion = "Producto #" + d.getIdProducto();
        try {
            Producto p = productoController.findById(d.getIdProducto());
            if (p != null) {
                if (p.getDescripcion() != null && !p.getDescripcion().isEmpty()) {
                    descripcion = p.getDescripcion();
                } else if (p.getNombre() != null && !p.getNombre().isEmpty()) {
                    descripcion = p.getNombre();
                }
            }
        } catch (Exception ignored) { }

        // ← YA SON DOUBLE, NO NECESITAN CONVERSIONES
        double precio = d.getPrecioUnitario();
        double subtotal = d.getSubtotal();

        modeloDetalles.addRow(new Object[]{
                d.getIdProducto(),
                descripcion,
                d.getCantidad(),
                precio,
                subtotal
        });
    }
}


    private void procesarVenta() {
        int fila = tablaPedidos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido.");
            return;
        }

        int modelRow = tablaPedidos.convertRowIndexToModel(fila);
        int idPedido = (int) modeloPedidos.getValueAt(modelRow, 0);

        double total = Double.parseDouble(modeloPedidos.getValueAt(modelRow, 3).toString());
        String tipoPago = cmbPago.getSelectedItem().toString();

        Venta v = new Venta();
        v.setIdPedido(idPedido);
        v.setMontoTotal(total);
        v.setTipoPago(tipoPago);

        boolean ok = ventaController.procesarVenta(v);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Venta procesada correctamente.");
            cargarPedidos();
            modeloDetalles.setRowCount(0);
            cargarVentas();
        } else {
            JOptionPane.showMessageDialog(this, "Error al procesar venta.");
        }
    }
}

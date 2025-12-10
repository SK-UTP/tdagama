package tiendagama.view;

import tiendagama.dao.PedidoDAO;
import tiendagama.dao.PedidoDetalleDAO;
import tiendagama.dao.VentaDAO;
import tiendagama.dao.ProductoDAO;

import tiendagama.model.Pedido;
import tiendagama.model.PedidoDetalle;
import tiendagama.model.Venta;
import tiendagama.model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PedidosView extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final PedidoDetalleDAO detalleDAO = new PedidoDetalleDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    public PedidosView() {
        setTitle("Lista de Pedidos");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Usuario", "Fecha", "Total", "Estado"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);

        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnVerDetalle = new JButton("Ver Detalle");
        JButton btnEnviarVenta = new JButton("Enviar a Ventas");

        btnRefrescar.addActionListener(e -> cargarPedidos());
        btnVerDetalle.addActionListener(e -> verDetalle());
        btnEnviarVenta.addActionListener(e -> enviarAVentas());

        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(btnRefrescar);
        bottom.add(btnVerDetalle);
        bottom.add(btnEnviarVenta);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(tabla), BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        cargarPedidos();
        setVisible(true);
    }

    private void cargarPedidos() {
        modelo.setRowCount(0);
        List<Pedido> lista = pedidoDAO.listarPedidosPendientes();

        for (Pedido p : lista) {
            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getIdUsuario(),
                    p.getFecha(),
                    p.getTotal(),
                    p.getEstado()
            });
        }
    }

    private void verDetalle() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido.");
            return;
        }

        int idPedido = (int) modelo.getValueAt(fila, 0);

        List<PedidoDetalle> detalles = detalleDAO.obtenerDetalles(idPedido);

        StringBuilder sb = new StringBuilder("DETALLE DEL PEDIDO #" + idPedido + "\n\n");

        for (PedidoDetalle d : detalles) {
            sb.append("Producto: ").append(d.getIdProducto())
              .append(" | Cant: ").append(d.getCantidad())
              .append(" | PU: ").append(d.getPrecioUnitario())
              .append(" | Subtotal: ").append(d.getSubtotal()).append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString());
    }

    private void enviarAVentas() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido para enviarlo a ventas.");
            return;
        }

        int idPedido = (int) modelo.getValueAt(fila, 0);
        double total = (double) modelo.getValueAt(fila, 3);

        int conf = JOptionPane.showConfirmDialog(
                this,
                "¿CONFIRMAR que desea enviar el pedido #" + idPedido + " a VENTAS?",
                "Confirmar Envío",
                JOptionPane.YES_NO_OPTION
        );

        if (conf != JOptionPane.YES_OPTION)
            return;

        // 1. Crear venta
        Venta venta = new Venta();
        venta.setIdPedido(idPedido);
        venta.setMontoTotal(total);
        venta.setTipoPago("EFECTIVO"); // Puedes agregar opción más adelante

        boolean okVenta = ventaDAO.registrarVenta(venta);
        if (!okVenta) {
            JOptionPane.showMessageDialog(this, "Error al registrar la venta.");
            return;
        }

        // 2. Cambiar estado del pedido
        boolean okEstado = pedidoDAO.cambiarEstado(idPedido, "ENVIADO");
        if (!okEstado) {
            JOptionPane.showMessageDialog(this, "Se registró la venta, pero no se actualizó el estado del pedido.");
        }

        JOptionPane.showMessageDialog(this, "Pedido enviado a ventas correctamente.");

        cargarPedidos(); // refrescar tabla
    }
}

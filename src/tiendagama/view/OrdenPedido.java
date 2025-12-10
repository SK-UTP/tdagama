package tiendagama.view;

import tiendagama.controller.PedidoController;
import tiendagama.controller.ProductoController;
import tiendagama.model.Producto;
import tiendagama.model.Pedido;
import tiendagama.model.PedidoDetalle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista para crear pedidos: muestra productos (izq) y carrito (der).
 * Constructor: new OrdenPedido(usuarioId)
 */
public class OrdenPedido extends JFrame {

    private final int usuarioId;
    private final ProductoController productoController = new ProductoController();
    private final PedidoController pedidoController = new PedidoController();

    private JTable tablaProductos;
    private JTable tablaCarrito;
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloCarrito;

    private JLabel lblTotal;
    private JButton btnAgregar, btnQuitar, btnGenerar, btnRefrescar;

    public OrdenPedido(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Generar Pedido - TiendaGama");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        cargarProductos();
        actualizarTotal();
    }

    private void initComponents() {
        JPanel panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Productos
        JPanel panelProductos = new JPanel(new BorderLayout(6, 6));
        panelProductos.setBorder(BorderFactory.createTitledBorder("Productos Disponibles"));

        modeloProductos = new DefaultTableModel(new Object[]{"ID", "Nombre", "Precio", "Stock"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollProductos = new JScrollPane(tablaProductos);

        JPanel panelProdBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRefrescar = new JButton("Refrescar");
        panelProdBtn.add(btnRefrescar);

        panelProductos.add(scrollProductos, BorderLayout.CENTER);
        panelProductos.add(panelProdBtn, BorderLayout.SOUTH);

        // Panel Carrito
        JPanel panelCarrito = new JPanel(new BorderLayout(6, 6));
        panelCarrito.setBorder(BorderFactory.createTitledBorder("Carrito / Pedido"));

        modeloCarrito = new DefaultTableModel(new Object[]{"ID", "Nombre", "Cantidad", "Precio unit.", "Subtotal"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);

        // Botones entre tablas
        JPanel panelBtns = new JPanel();
        panelBtns.setLayout(new BoxLayout(panelBtns, BoxLayout.Y_AXIS));
        btnAgregar = new JButton("Agregar →");
        btnQuitar = new JButton("← Quitar");
        panelBtns.add(Box.createVerticalStrut(20));
        panelBtns.add(btnAgregar);
        panelBtns.add(Box.createVerticalStrut(10));
        panelBtns.add(btnQuitar);
        panelBtns.add(Box.createVerticalGlue());

        // Panel inferior carrito
        JPanel rightBottom = new JPanel(new BorderLayout());
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: S/ 0.00");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));
        panelTotal.add(lblTotal);

        btnGenerar = new JButton("Generar Pedido");
        JPanel panelGenerar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelGenerar.add(btnGenerar);

        rightBottom.add(panelTotal, BorderLayout.CENTER);
        rightBottom.add(panelGenerar, BorderLayout.SOUTH);

        panelCarrito.add(scrollCarrito, BorderLayout.CENTER);
        panelCarrito.add(rightBottom, BorderLayout.SOUTH);

        // Combino en holder
        JPanel holder = new JPanel(new BorderLayout());
        holder.add(panelProductos, BorderLayout.WEST);
        holder.add(panelBtns, BorderLayout.CENTER);
        holder.add(panelCarrito, BorderLayout.EAST);

        panelProductos.setPreferredSize(new Dimension(480, 400));
        panelCarrito.setPreferredSize(new Dimension(480, 400));
        panelBtns.setPreferredSize(new Dimension(100, 400));

        panelMain.add(holder, BorderLayout.CENTER);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelMain, BorderLayout.CENTER);

        // EVENTOS
        btnRefrescar.addActionListener(e -> cargarProductos());

        btnAgregar.addActionListener(e -> agregarProducto());

        btnQuitar.addActionListener(e -> quitarProducto());

        btnGenerar.addActionListener(e -> generarPedido());
    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        List<Producto> lista = productoController.listar();
        for (Producto p : lista) {
            modeloProductos.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio(), p.getStock()});
        }
    }

    private void agregarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para agregar.");
            return;
        }

        int id = (int) modeloProductos.getValueAt(fila, 0);
        String nombre = String.valueOf(modeloProductos.getValueAt(fila, 1));
        double precio = Double.parseDouble(String.valueOf(modeloProductos.getValueAt(fila, 2)));
        int stock = Integer.parseInt(String.valueOf(modeloProductos.getValueAt(fila, 3)));

        String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad:", "1");
        if (cantidadStr == null) return;
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr.trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cantidad inválida.");
            return;
        }
        if (cantidad > stock) {
            JOptionPane.showMessageDialog(this, "No hay suficiente stock.");
            return;
        }

        // Si ya existe en carrito, sumar cantidad
        boolean encontrado = false;
        for (int r = 0; r < modeloCarrito.getRowCount(); r++) {
            int idCar = (int) modeloCarrito.getValueAt(r, 0);
            if (idCar == id) {
                int existCant = Integer.parseInt(String.valueOf(modeloCarrito.getValueAt(r, 2)));
                int nuevaCant = existCant + cantidad;
                if (nuevaCant > stock) {
                    JOptionPane.showMessageDialog(this, "Excede stock disponible.");
                    return;
                }
                modeloCarrito.setValueAt(nuevaCant, r, 2);
                modeloCarrito.setValueAt(nuevaCant * precio, r, 4);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            double subtotal = cantidad * precio;
            modeloCarrito.addRow(new Object[]{id, nombre, cantidad, precio, subtotal});
        }

        actualizarTotal();
    }

    private void quitarProducto() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem del carrito para quitar.");
            return;
        }
        modeloCarrito.removeRow(fila);
        actualizarTotal();
    }

    private void generarPedido() {
        if (modeloCarrito.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }

        Pedido pedido = new Pedido();
        pedido.setIdUsuario(usuarioId);

        List<PedidoDetalle> detalles = new ArrayList<>();
        double total = 0.0;
        for (int r = 0; r < modeloCarrito.getRowCount(); r++) {
            int idProd = (int) modeloCarrito.getValueAt(r, 0);
            String nom = String.valueOf(modeloCarrito.getValueAt(r, 1));
            int cant = Integer.parseInt(String.valueOf(modeloCarrito.getValueAt(r, 2)));
            double precio = Double.parseDouble(String.valueOf(modeloCarrito.getValueAt(r, 3)));
            double subtotal = cant * precio;
            total += subtotal;

            PedidoDetalle det = new PedidoDetalle();
            det.setIdProducto(idProd);
            det.setCantidad(cant);
            det.setPrecioUnitario(precio);
            det.setSubtotal(subtotal);
            detalles.add(det);
        }

        pedido.setDetalles(detalles);
        pedido.setTotal(total);
        pedido.setEstado("PENDIENTE");

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Generar pedido para el cliente %d por total S/ %.2f ?", usuarioId, total),
                "Confirmar Pedido", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = pedidoController.generarPedido(pedido);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Pedido generado correctamente.");
            modeloCarrito.setRowCount(0);
            actualizarTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Error al generar el pedido. Intente nuevamente.");
        }
    }

    private void actualizarTotal() {
        double total = 0.0;
        for (int r = 0; r < modeloCarrito.getRowCount(); r++) {
            total += Double.parseDouble(String.valueOf(modeloCarrito.getValueAt(r, 4)));
        }
        lblTotal.setText(String.format("Total: S/ %.2f", total));
    }
}

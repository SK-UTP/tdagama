package tiendagama.view;

import tiendagama.controller.CarritoController;
import tiendagama.controller.ProductoController;
import tiendagama.model.Carrito;
import tiendagama.model.CarritoDetalle;
import tiendagama.model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista para gestionar carrito: agregar productos y guardar carrito en BD.
 * Uso: new CarritoView(usuarioId).setVisible(true);
 */
public class CarritoView extends JFrame {

    private final int usuarioId;
    private final ProductoController productoController = new ProductoController();
    private final CarritoController carritoController = new CarritoController();

    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloCarrito;
    private JTable tablaProductos;
    private JTable tablaCarrito;
    private JLabel lblTotal;
    private JButton btnAgregar, btnQuitar, btnGuardar, btnRefrescar;

    public CarritoView(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Carrito - TiendaGama");
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        cargarProductos();
        actualizarTotal();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // productos
        modeloProductos = new DefaultTableModel(new Object[]{"ID","Nombre","Precio","Stock"}, 0) {
            @Override public boolean isCellEditable(int r, int c){return false;}
        };
        tablaProductos = new JTable(modeloProductos);
        JScrollPane spProd = new JScrollPane(tablaProductos);

        JPanel pProd = new JPanel(new BorderLayout());
        pProd.setBorder(BorderFactory.createTitledBorder("Productos"));
        pProd.add(spProd, BorderLayout.CENTER);

        btnRefrescar = new JButton("Refrescar");
        JPanel prodBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prodBtns.add(btnRefrescar);
        pProd.add(prodBtns, BorderLayout.SOUTH);

        // carrito
        modeloCarrito = new DefaultTableModel(new Object[]{"ID","Nombre","Cantidad","Precio unit.","Subtotal"}, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        tablaCarrito = new JTable(modeloCarrito);
        JScrollPane spCar = new JScrollPane(tablaCarrito);

        JPanel pCar = new JPanel(new BorderLayout());
        pCar.setBorder(BorderFactory.createTitledBorder("Carrito"));
        pCar.add(spCar, BorderLayout.CENTER);

        lblTotal = new JLabel("Total: S/ 0.00");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));
        btnGuardar = new JButton("Guardar Carrito");
        JPanel bottomCar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomCar.add(lblTotal);
        bottomCar.add(btnGuardar);
        pCar.add(bottomCar, BorderLayout.SOUTH);

        // botones entre
        JPanel mid = new JPanel();
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));
        btnAgregar = new JButton("Agregar →");
        btnQuitar = new JButton("← Quitar");
        mid.add(Box.createVerticalStrut(40));
        mid.add(btnAgregar);
        mid.add(Box.createVerticalStrut(10));
        mid.add(btnQuitar);
        mid.add(Box.createVerticalGlue());

        // layout principal
        JPanel center = new JPanel(new BorderLayout());
        center.add(pProd, BorderLayout.WEST);
        center.add(mid, BorderLayout.CENTER);
        center.add(pCar, BorderLayout.EAST);

        pProd.setPreferredSize(new Dimension(460, 380));
        pCar.setPreferredSize(new Dimension(460, 380));
        mid.setPreferredSize(new Dimension(80, 380));

        main.add(center, BorderLayout.CENTER);
        getContentPane().add(main);

        // eventos
        btnRefrescar.addActionListener(e -> cargarProductos());

        btnAgregar.addActionListener(e -> {
            int fila = tablaProductos.getSelectedRow();
            if (fila < 0) { JOptionPane.showMessageDialog(this, "Seleccione un producto"); return; }

            int id = (int) modeloProductos.getValueAt(fila, 0);
            String nombre = String.valueOf(modeloProductos.getValueAt(fila,1));
            double precio = Double.parseDouble(String.valueOf(modeloProductos.getValueAt(fila,2)));
            int stock = Integer.parseInt(String.valueOf(modeloProductos.getValueAt(fila,3)));

            String s = JOptionPane.showInputDialog(this, "Cantidad:", "1");
            if (s==null) return;
            int cantidad;
            try {
                cantidad = Integer.parseInt(s.trim());
                if (cantidad <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida");
                return;
            }
            if (cantidad > stock) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente");
                return;
            }

            // si ya existe -> sumar
            boolean enc=false;
            for (int r=0;r<modeloCarrito.getRowCount();r++) {
                int idc = (int) modeloCarrito.getValueAt(r,0);
                if (idc==id) {
                    int exist = Integer.parseInt(String.valueOf(modeloCarrito.getValueAt(r,2)));
                    int nueva = exist + cantidad;
                    if (nueva > stock) { JOptionPane.showMessageDialog(this,"Excede stock"); return; }
                    modeloCarrito.setValueAt(nueva, r, 2);
                    double subtotal = nueva * precio;
                    modeloCarrito.setValueAt(subtotal, r, 4);
                    enc=true; break;
                }
            }
            if (!enc) {
                double subtotal = cantidad * precio;
                modeloCarrito.addRow(new Object[]{id, nombre, cantidad, precio, subtotal});
            }
            actualizarTotal();
        });

        btnQuitar.addActionListener(e -> {
            int r = tablaCarrito.getSelectedRow();
            if (r<0) { JOptionPane.showMessageDialog(this,"Seleccione ítem a quitar"); return; }
            modeloCarrito.removeRow(r);
            actualizarTotal();
        });

   btnGuardar.addActionListener(e -> {
    if (modeloCarrito.getRowCount()==0) { 
        JOptionPane.showMessageDialog(this,"Carrito vacío"); 
        return; 
    }

    Carrito carrito = new Carrito();
    carrito.setIdUsuario(usuarioId);

    List<CarritoDetalle> detalles = new ArrayList<>();
    for (int r=0;r<modeloCarrito.getRowCount();r++) {
        CarritoDetalle det = new CarritoDetalle();
        det.setIdProducto((int)modeloCarrito.getValueAt(r,0));
        det.setCantidad(Integer.parseInt(String.valueOf(modeloCarrito.getValueAt(r,2))));
        detalles.add(det);
    }
    carrito.setDetalles(detalles);

    int opt = JOptionPane.showOptionDialog(this,
            "¿Desea guardar este carrito como pedido?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new Object[]{"Sí, como pedido", "No, solo carrito"},
            "Sí, como pedido");

    boolean ok = false;
    if (opt == JOptionPane.YES_OPTION) {
        ok = carritoController.guardarCarritoComoPedido(carrito);
        if (ok) JOptionPane.showMessageDialog(this,"Pedido generado correctamente.");
        else JOptionPane.showMessageDialog(this,"Error al generar pedido.");
    } else if (opt == JOptionPane.NO_OPTION) {
        ok = carritoController.guardarCarritoComoPedido(carrito);
        if (ok) JOptionPane.showMessageDialog(this,"Carrito guardado correctamente.");
        else JOptionPane.showMessageDialog(this,"Error al guardar carrito.");
    }

    if (ok) {
        modeloCarrito.setRowCount(0);
        actualizarTotal();

        // 👉 ABRIR VISTA DE PEDIDOS
        new PedidosView().setVisible(true);

        // 👉 CERRAR ESTA VISTA
        this.dispose();
    }
});

    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        List<Producto> lista = productoController.listar();
        for (Producto p : lista) {
            modeloProductos.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio(), p.getStock()});
        }
    }

    private void actualizarTotal() {
        double total = 0.0;
        for (int r=0; r<modeloCarrito.getRowCount(); r++) {
            total += Double.parseDouble(String.valueOf(modeloCarrito.getValueAt(r,4)));
        }
        lblTotal.setText(String.format("Total: S/ %.2f", total));
    }
    
    
}

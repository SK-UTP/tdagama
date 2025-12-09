package tiendagama.view;

import tiendagama.controller.ProductoController;
import tiendagama.model.Producto;
import tiendagama.utils.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class CrudProductos extends JFrame {

    private final ProductoController controller = new ProductoController();
    private DefaultTableModel modelo;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;

    public CrudProductos() {
        UIUtils.applyAppStyle();
        setTitle("CRUD Productos - TiendaGama");
        setSize(980, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel top = new JPanel(new BorderLayout(8,8));
        top.setBorder(BorderFactory.createEmptyBorder(10,10,6,10));
        top.add(UIUtils.makeTitle("Productos"), BorderLayout.WEST);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8,0));
        txtBuscar = UIUtils.sizedField();
        txtBuscar.setToolTipText("Buscar por nombre...");
        JButton btnBuscar = UIUtils.makeSmall("Buscar");
        JButton btnNuevo = UIUtils.makePrimary("Nuevo");
        rightTop.add(txtBuscar);
        rightTop.add(btnBuscar);
        rightTop.add(btnNuevo);
        top.add(rightTop, BorderLayout.EAST);

        modelo = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripción", "Precio", "Stock"}, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(modelo);
        table.setFillsViewportHeight(true);
        sorter = new TableRowSorter<>(modelo);
        table.setRowSorter(sorter);

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245,245,245));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // bottom: izquierda Cerrar, derecha acciones
        JPanel bottom = new JPanel(new BorderLayout());
        JPanel leftBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCerrar = UIUtils.makeSmall("Cerrar");
        leftBottom.add(btnCerrar);

        JPanel rightBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnEditar = UIUtils.makeSecondary("Editar");
        JButton btnEliminar = UIUtils.makeSecondary("Eliminar");
        JButton btnRefresh = UIUtils.makeSecondary("Refrescar");
        rightBottom.add(btnRefresh);
        rightBottom.add(btnEditar);
        rightBottom.add(btnEliminar);

        bottom.add(leftBottom, BorderLayout.WEST);
        bottom.add(rightBottom, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // listeners
        btnNuevo.addActionListener(e -> {
            ProductoForm form = new ProductoForm(this, null);
            form.setVisible(true);
        });

        btnRefresh.addActionListener(e -> loadData());

        btnBuscar.addActionListener(e -> {
            String q = txtBuscar.getText().trim();
            if (q.isEmpty()) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + q, 1));
        });

        btnEditar.addActionListener(e -> editarSelected());
        btnEliminar.addActionListener(e -> eliminarSelected());

        btnCerrar.addActionListener(e -> {
            // simplemente cierra la ventana para regresar al dashboard
            this.dispose();
        });
    }

    public void loadData() {
        modelo.setRowCount(0);
        List<Producto> lista = controller.listar();
        for (Producto p : lista) {
            modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock()});
        }
    }

    private void editarSelected() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Seleccione un producto"); return; }
        int modelRow = table.convertRowIndexToModel(r);
        Producto p = new Producto();
        p.setId((int) modelo.getValueAt(modelRow, 0));
        p.setNombre((String) modelo.getValueAt(modelRow, 1));
        p.setDescripcion((String) modelo.getValueAt(modelRow, 2));
        Object precioObj = modelo.getValueAt(modelRow, 3);
        double precio = precioObj instanceof Number ? ((Number)precioObj).doubleValue() : Double.parseDouble(precioObj.toString());
        Object stockObj = modelo.getValueAt(modelRow, 4);
        int stock = stockObj instanceof Number ? ((Number)stockObj).intValue() : Integer.parseInt(stockObj.toString());
        p.setPrecio(precio);
        p.setStock(stock);
        ProductoForm f = new ProductoForm(this, p);
        f.setVisible(true);
    }

    private void eliminarSelected() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Seleccione un producto"); return; }
        int modelRow = table.convertRowIndexToModel(r);
        int id = (int) modelo.getValueAt(modelRow, 0);
        if (JOptionPane.showConfirmDialog(this, "Eliminar producto " + id + " ?") != JOptionPane.YES_OPTION) return;
        if (controller.eliminar(id)) {
            JOptionPane.showMessageDialog(this, "Eliminado");
            loadData();
        } else JOptionPane.showMessageDialog(this, "Error al eliminar");
    }
}

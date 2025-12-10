package tiendagama.view;

import tiendagama.controller.ProductoController;
import tiendagama.model.Producto;
import tiendagama.utils.UIUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProductoForm extends JDialog {

    private final ProductoController controller = new ProductoController();
    private final CrudProductos padre;
    private Producto producto;

    private JTextField txtNombre, txtPrecio, txtStock, txtImagen;
    private JTextArea txtDescripcion;
    private JLabel lblPreview;

    public ProductoForm(CrudProductos padre, Producto producto) {
        super(padre, true);
        UIUtils.applyAppStyle();
        this.padre = padre;
        this.producto = producto;

        setTitle(producto == null ? "Nuevo Producto" : "Editar Producto");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        pack(); // ajusta ventana al contenido
        setLocationRelativeTo(padre);
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(15,15));
        main.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        // Panel izquierdo con campos
        JPanel left = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = new JTextField(20);
        txtPrecio = new JTextField(10);
        txtStock = new JTextField(10);
        txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtImagen = new JTextField(20);

        int row = 0;
        c.gridx=0; c.gridy=row; left.add(new JLabel("Nombre:"), c);
        c.gridx=1; left.add(txtNombre, c);

        row++;
        c.gridx=0; c.gridy=row; left.add(new JLabel("Precio:"), c);
        c.gridx=1; left.add(txtPrecio, c);

        row++;
        c.gridx=0; c.gridy=row; left.add(new JLabel("Stock:"), c);
        c.gridx=1; left.add(txtStock, c);

        row++;
        c.gridx=0; c.gridy=row; left.add(new JLabel("Descripción:"), c);
        c.gridx=1; left.add(new JScrollPane(txtDescripcion), c);

        row++;
        c.gridx=0; c.gridy=row; left.add(new JLabel("Imagen (ruta):"), c);
        c.gridx=1; left.add(txtImagen, c);

        // Panel derecho con preview
        JPanel right = new JPanel(new BorderLayout(10,10));
        lblPreview = new JLabel("Preview", SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(220,220));
        lblPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        right.add(lblPreview, BorderLayout.NORTH);

        JButton btnCargar = new JButton("Cargar Imagen");
        right.add(btnCargar, BorderLayout.CENTER);

        // Panel inferior con botones centrados
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));
        JButton btnGuardar = UIUtils.makePrimary("Guardar");
        JButton btnCancelar = UIUtils.makeSecondary("Cancelar");
        bottom.add(btnCancelar);
        bottom.add(btnGuardar);

        main.add(left, BorderLayout.CENTER);
        main.add(right, BorderLayout.EAST);
        main.add(bottom, BorderLayout.SOUTH);

        add(main);

        // Si es edición, cargar datos
        if (producto != null) {
            txtNombre.setText(producto.getNombre());
            txtPrecio.setText(String.valueOf(producto.getPrecio()));
            txtStock.setText(String.valueOf(producto.getStock()));
            txtDescripcion.setText(producto.getDescripcion());
            txtImagen.setText(producto.getImagen());
            loadPreview(producto.getImagen());
        }

        // Listeners
        btnCargar.addActionListener(e -> {
            JFileChooser j = new JFileChooser();
            int r = j.showOpenDialog(this);
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = j.getSelectedFile();
                txtImagen.setText(f.getAbsolutePath());
                loadPreview(f.getAbsolutePath());
            }
        });

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardar());
    }

    private void loadPreview(String path) {
        try {
            if (path == null || path.trim().isEmpty()) {
                lblPreview.setIcon(null);
                lblPreview.setText("Preview");
                return;
            }
            Image img = ImageIO.read(new File(path));
            if (img != null) {
                Image scaled = img.getScaledInstance(lblPreview.getWidth(), lblPreview.getHeight(), Image.SCALE_SMOOTH);
                lblPreview.setIcon(new ImageIcon(scaled));
                lblPreview.setText("");
            } else {
                lblPreview.setIcon(null);
                lblPreview.setText("No preview");
            }
        } catch (Exception ex) {
            lblPreview.setIcon(null);
            lblPreview.setText("No preview");
        }
    }

    private void guardar() {
        try {
            if (producto == null) producto = new Producto();
            producto.setNombre(txtNombre.getText().trim());
            producto.setDescripcion(txtDescripcion.getText().trim());
            producto.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            producto.setStock(Integer.parseInt(txtStock.getText().trim()));
            producto.setImagen(txtImagen.getText().trim());

            boolean ok = (producto.getId() == 0)
                    ? controller.crear(producto)
                    : controller.actualizar(producto);

            if (ok) {
                JOptionPane.showMessageDialog(this, "Guardado correctamente");
                padre.loadData();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio o stock inválido");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}

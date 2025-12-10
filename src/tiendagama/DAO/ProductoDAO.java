package tiendagama.dao;

import tiendagama.config.DBUtil;
import tiendagama.model.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));

                // Manejo de id_categoria que puede ser NULL en la BD
                Object objIdCat = rs.getObject("id_categoria");
                if (objIdCat != null) {
                    // object puede ser Integer, BigDecimal, etc. Convertimos a Number
                    p.setIdCategoria(((Number) objIdCat).intValue());
                } else {
                    p.setIdCategoria(null);
                }

                p.setImagen(rs.getString("imagen"));

                lista.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean crear(Producto p) {
        String sql = "INSERT INTO producto(nombre, descripcion, precio, stock, id_categoria, imagen) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());

            // Manejo defensivo de idCategoria (puede ser null)
            Integer idCat = p.getIdCategoria();
            if (idCat == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, idCat);
            }

            ps.setString(6, p.getImagen());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean actualizar(Producto p) {
        String sql = "UPDATE producto SET nombre=?, descripcion=?, precio=?, stock=?, id_categoria=?, imagen=? WHERE id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setInt(4, p.getStock());

            Integer idCat = p.getIdCategoria();
            if (idCat == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, idCat);
            }

            ps.setString(6, p.getImagen());
            ps.setInt(7, p.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM producto WHERE id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean actualizarStock(int idProducto, int cantidadRestar) {
        String sql = "UPDATE producto SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cantidadRestar);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidadRestar);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
     public Producto getById(int id) {
        String sql = "SELECT id, nombre, descripcion, precio, stock, id_categoria, imagen FROM producto WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setId(rs.getInt("id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock"));
                    // si tu modelo/producto tiene estos campos:
                    try { p.setIdCategoria(rs.getInt("id_categoria")); } catch (Exception ignore) {}
                    try { p.setImagen(rs.getString("imagen")); } catch (Exception ignore) {}
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
     
     public int obtenerStock(int idProducto) {
    String sql = "SELECT stock FROM producto WHERE id = ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idProducto);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("stock");
        }
    } catch (Exception e) { e.printStackTrace(); }
    return 0;
}

public double obtenerPrecio(int idProducto) {
    String sql = "SELECT precio FROM producto WHERE id = ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idProducto);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("precio");
        }
    } catch (Exception e) { e.printStackTrace(); }
    return 0.0;
}

public boolean descontarStock(int idProducto, int cantidad) {
    String sql = "UPDATE producto SET stock = stock - ? WHERE id = ? AND stock >= ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, cantidad);
        ps.setInt(2, idProducto);
        ps.setInt(3, cantidad);
        return ps.executeUpdate() > 0;
    } catch (Exception e) { e.printStackTrace(); }
    return false;
}

public Producto findById(int idProducto) {
    String sql = "SELECT * FROM producto WHERE id = ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, idProducto);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Producto p = new Producto();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setStock(rs.getInt("stock"));
                p.setPrecio(rs.getDouble("precio"));
                return p;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
}

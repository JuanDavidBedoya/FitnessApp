package com.example.repositorio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.modelos.Progreso;

public class ProgresoRepositorio {
    
    //CREATE
    public boolean crearProgreso(Progreso progreso) {
        String sql = "INSERT INTO Progreso (fechaRegistro, peso, caloriasQuemadas, observaciones, General_Usuario_cedula) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDate(1, Date.valueOf(progreso.getFechaRegistro()));
            stmt.setDouble(2, progreso.getPeso());
            stmt.setDouble(3, progreso.getCaloriasQuemadas());
            stmt.setString(4, progreso.getObservaciones());
            stmt.setString(5, progreso.getCedulaUsuario());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        progreso.setCodProgreso(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al crear el progreso.");
            e.printStackTrace();
            return false;
        }
    }
    
    //READ
    public List<Progreso> obtenerProgresosPorUsuario(String cedulaUsuario) {
        List<Progreso> lista = new ArrayList<>();
        String sql = "SELECT * FROM Progreso WHERE General_Usuario_cedula = ? ORDER BY fechaRegistro DESC";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedulaUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Progreso p = new Progreso(
                        rs.getInt("codProgreso"),
                        rs.getDate("fechaRegistro").toLocalDate(),
                        rs.getDouble("peso"),
                        rs.getDouble("caloriasQuemadas"),
                        rs.getString("observaciones"),
                        rs.getString("General_Usuario_cedula")
                    );
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al leer los progresos.");
            e.printStackTrace();
        }
        return lista;
    }
    
    //UPDATE
    public boolean actualizarProgreso(Progreso progreso) {
        String sql = "UPDATE Progreso SET fechaRegistro = ?, peso = ?, caloriasQuemadas = ?, observaciones = ? WHERE codProgreso = ?";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(progreso.getFechaRegistro()));
            stmt.setDouble(2, progreso.getPeso());
            stmt.setDouble(3, progreso.getCaloriasQuemadas());
            stmt.setString(4, progreso.getObservaciones());
            stmt.setInt(5, progreso.getCodProgreso());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el progreso.");
            e.printStackTrace();
            return false;
        }
    }
    
    //DELETE
    public boolean eliminarProgreso(int codProgreso) {
        String sql = "DELETE FROM Progreso WHERE codProgreso = ?";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codProgreso);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el progreso.");
            e.printStackTrace();
            return false;
        }
    }
}

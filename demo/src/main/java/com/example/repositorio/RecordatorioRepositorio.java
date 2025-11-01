package com.example.repositorio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.example.modelos.Recordatorio;

public class RecordatorioRepositorio {

    // CREATE
    public boolean crearRecordatorio(Recordatorio recordatorio) {
        String sql = "INSERT INTO Recordatorio (mensaje, fecha, hora, General_Usuario_cedula) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, recordatorio.getMensaje());
            stmt.setDate(2, Date.valueOf(recordatorio.getFecha()));
            stmt.setTime(3, Time.valueOf(recordatorio.getHora()));
            stmt.setString(4, recordatorio.getCedulaUsuario());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        recordatorio.setCodRecordatorio(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al crear el recordatorio.");
            e.printStackTrace();
            return false;
        }
    }
    
    //READ
    public List<Recordatorio> obtenerRecordatoriosPorUsuario(String cedulaUsuario) {
        List<Recordatorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM Recordatorio WHERE General_Usuario_cedula = ? ORDER BY fecha DESC, hora ASC";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedulaUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recordatorio r = new Recordatorio(
                        rs.getInt("codRecordatorio"),
                        rs.getString("mensaje"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        rs.getString("General_Usuario_cedula")
                    );
                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al leer los recordatorios.");
            e.printStackTrace();
        }
        return lista;
    }
    
    // UPDATE
    public boolean actualizarRecordatorio(Recordatorio recordatorio) {
        String sql = "UPDATE Recordatorio SET mensaje = ?, fecha = ?, hora = ? WHERE codRecordatorio = ?";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, recordatorio.getMensaje());
            stmt.setDate(2, Date.valueOf(recordatorio.getFecha()));
            stmt.setTime(3, Time.valueOf(recordatorio.getHora()));
            stmt.setInt(4, recordatorio.getCodRecordatorio());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el recordatorio.");
            e.printStackTrace();
            return false;
        }
    }
    
    // DELETE
    public boolean eliminarRecordatorio(int codRecordatorio) {
        String sql = "DELETE FROM Recordatorio WHERE codRecordatorio = ?";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, codRecordatorio);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el recordatorio.");
            e.printStackTrace();
            return false;
        }
    }
}
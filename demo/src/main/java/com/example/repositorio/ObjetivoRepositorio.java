package com.example.repositorio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.modelos.ObjetivoAsignado;
import com.example.utils.Alerts;

import javafx.scene.control.Alert;

public class ObjetivoRepositorio {

    //READ - Obtener todos los objetivos POSIBLES
    public List<ObjetivoAsignado> obtenerTodosLosObjetivos() {
        List<ObjetivoAsignado> lista = new ArrayList<>();
        String sql = "SELECT codObjetivo, nombre, descripcion FROM ObjetivoDeportivo";
        
        try (Connection conn = Conexion.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ObjetivoAsignado o = new ObjetivoAsignado(
                    rs.getInt("codObjetivo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    null,
                    null 
                );
                lista.add(o);
            }
        } catch (SQLException e) {
            System.err.println("Error al leer todos los objetivos disponibles.");
            e.printStackTrace();
        }
        return lista;
    }
    
    //READ - Obtener objetivos asignados al usuario
    public List<ObjetivoAsignado> obtenerObjetivosAsignados(String cedulaUsuario) {
        List<ObjetivoAsignado> lista = new ArrayList<>();
        String sql = "SELECT OD.codObjetivo, OD.nombre, OD.descripcion, UO.fechaAsignacion " +
                     "FROM UsuarioObjetivo UO " +
                     "JOIN ObjetivoDeportivo OD ON UO.ObjetivoDeportivo_codObjetivo = OD.codObjetivo " +
                     "WHERE UO.General_Usuario_cedula = ? " +
                     "ORDER BY UO.fechaAsignacion DESC";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedulaUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ObjetivoAsignado oa = new ObjetivoAsignado(
                        rs.getInt("codObjetivo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDate("fechaAsignacion").toLocalDate(),
                        cedulaUsuario
                    );
                    lista.add(oa);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al leer los objetivos asignados.");
            e.printStackTrace();
        }
        return lista;
    }

    //CREATE - Asignar un objetivo
    public boolean asignarObjetivo(String cedulaUsuario, int codObjetivo, LocalDate fechaAsignacion) {
        String sql = "INSERT INTO UsuarioObjetivo (General_Usuario_cedula, ObjetivoDeportivo_codObjetivo, fechaAsignacion) VALUES (?, ?, ?)";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedulaUsuario);
            stmt.setInt(2, codObjetivo);
            stmt.setDate(3, Date.valueOf(fechaAsignacion));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Error: El objetivo ya está asignado al usuario.");
            Alerts.showAlert(Alert.AlertType.WARNING, "Objetivo Duplicado", "Este objetivo ya lo tienes asignado.");
            return false;
        } catch (SQLException e) {
            System.err.println("Error al asignar el objetivo.");
            e.printStackTrace();
            return false;
        }
    }
    
    //DELETE - Eliminar/Desasignar un objetivo
    public boolean desasignarObjetivo(String cedulaUsuario, int codObjetivo) {
        String sql = "DELETE FROM UsuarioObjetivo WHERE General_Usuario_cedula = ? AND ObjetivoDeportivo_codObjetivo = ?";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedulaUsuario);
            stmt.setInt(2, codObjetivo);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desasignar el objetivo.");
            e.printStackTrace();
            return false;
        }
    }
    
    // Nota: El UPDATE no se usa aqui, ya que actualizar un objetivo asignado es simplemente eliminarlo y crear uno nuevo
}

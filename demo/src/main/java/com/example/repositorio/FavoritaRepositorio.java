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

import com.example.modelos.RutinaFavoritaModelo;
import com.example.utils.Alerts;

import javafx.scene.control.Alert;

public class FavoritaRepositorio {

    //READ - Obtener todas las rutinas favoritas ACTIVAS del usuario
    public List<RutinaFavoritaModelo> obtenerFavoritasActivas(String cedulaUsuario) {
        List<RutinaFavoritaModelo> lista = new ArrayList<>();
        String sql = "SELECT R.codRutina, R.nombre, R.descripcion, R.duracion, RF.fechaFavorito " +
                     "FROM RutinaFavorita RF " +
                     "JOIN Rutina R ON RF.Rutina_codRutina = R.codRutina " +
                     "WHERE RF.General_Usuario_cedula = ? AND RF.fechaNoFavorito IS NULL " +
                     "ORDER BY RF.fechaFavorito DESC";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedulaUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RutinaFavoritaModelo rf = new RutinaFavoritaModelo(
                        rs.getInt("codRutina"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("duracion"),
                        rs.getDate("fechaFavorito").toLocalDate(),
                        null,
                        cedulaUsuario
                    );
                    lista.add(rf);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al leer las rutinas favoritas activas.");
            e.printStackTrace();
        }
        return lista;
    }
    
    //READ - Obtener todas las rutinas DISPONIBLES para seleccionar
    public List<RutinaFavoritaModelo> obtenerRutinasDisponibles() {
        List<RutinaFavoritaModelo> lista = new ArrayList<>();
        String sql = "SELECT codRutina, nombre, descripcion, duracion FROM Rutina";
        
        try (Connection conn = Conexion.obtenerConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                RutinaFavoritaModelo r = new RutinaFavoritaModelo(
                    rs.getInt("codRutina"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getString("duracion"),
                    null, null, null
                );
                lista.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error al leer las rutinas disponibles.");
            e.printStackTrace();
        }
        return lista;
    }

    //CREATE - Marcar como favorita
    public boolean marcarComoFavorita(String cedulaUsuario, int codRutina) {
        String sql = "INSERT INTO RutinaFavorita (General_Usuario_cedula, Rutina_codRutina, fechaFavorito, fechaNoFavorito) VALUES (?, ?, ?, NULL)";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cedulaUsuario);
            stmt.setInt(2, codRutina);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLIntegrityConstraintViolationException e) {
             Alerts.showAlert(Alert.AlertType.WARNING, "Rutina Favorita Duplicada", "Esta rutina ya está marcada como favorita.");
            return false;
        } catch (SQLException e) {
            System.err.println("Error al marcar como favorita.");
            e.printStackTrace();
            return false;
        }
    }
    
    //DELETE - Desmarcar/Desactivar como favorita (Actualiza fechaNoFavorito)
    public boolean desmarcarComoFavorita(String cedulaUsuario, int codRutina) {
        String sql = "UPDATE RutinaFavorita SET fechaNoFavorito = ? WHERE General_Usuario_cedula = ? AND Rutina_codRutina = ?";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setString(2, cedulaUsuario);
            stmt.setInt(3, codRutina);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desmarcar la rutina favorita.");
            e.printStackTrace();
            return false;
        }
    }
}

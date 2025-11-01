package com.example.repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.modelos.Usuario;

public class UsuarioRepositorio {

    public Usuario loginGeneral(String email, String contrasena) {
        String sql = "SELECT U.cedula, U.primerNombre, U.email " +
                     "FROM Usuario U " +
                     "INNER JOIN General G ON U.cedula = G.Usuario_cedula " +
                     "WHERE U.email = ? AND U.contrasena = ?"; 

        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, contrasena);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String cedula = rs.getString("cedula");
                    String nombre = rs.getString("primerNombre");
                    String userEmail = rs.getString("email");
                    
                    return new Usuario(cedula, nombre, userEmail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error de base de datos durante el login.");
            e.printStackTrace();
        }
        return null;
    }
}

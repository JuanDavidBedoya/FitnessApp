package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import com.example.formularios.MenuPrincipal;
import com.example.modelos.Usuario;
import com.example.repositorio.UsuarioRepositorio;
import com.example.utils.Alerts;

public class Login {

    @FXML private TextField emailField;
    @FXML private TextField passwordField;

    private final UsuarioRepositorio usuarioRepo = new UsuarioRepositorio();

    @FXML
    public void initialize() {
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String contrasena = passwordField.getText();

        if (email.isEmpty() || contrasena.isEmpty()) {
            Alerts.showAlert(Alert.AlertType.ERROR, "Error de Validación", "Por favor, ingrese email y contraseña.");
            return;
        }

        Usuario usuarioLogeado = usuarioRepo.loginGeneral(email, contrasena);

        if (usuarioLogeado != null) {
            Alerts.showAlert(Alert.AlertType.INFORMATION, "Login Exitoso", "Bienvenid@, " + usuarioLogeado.getPrimerNombre() + ".");
            abrirMenuPrincipal(usuarioLogeado);
            
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, "Login Fallido", "Credenciales incorrectas o el usuario no es un cliente.");
        }
    }

    private void abrirMenuPrincipal(Usuario usuarioLogeado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formularios/MenuPrincipal.fxml"));
            AnchorPane menuPane = loader.load();

            MenuPrincipal menuController = loader.getController();
            menuController.setUsuarioData(usuarioLogeado);

            Stage stage = (Stage) emailField.getScene().getWindow();
            
            stage.setTitle("Fitness App - Menú Principal");
            stage.setScene(new Scene(menuPane));
            stage.setResizable(true);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana del Menú Principal.");
            e.printStackTrace();
        }
    }

}

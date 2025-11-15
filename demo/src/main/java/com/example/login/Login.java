package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import com.example.formularios.SelectorPrincipal;
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
            
            abrirSelectorPrincipal(usuarioLogeado); 
            
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, "Login Fallido", "Credenciales incorrectas o el usuario no es un cliente.");
        }
    }

    private void abrirSelectorPrincipal(Usuario usuarioLogeado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formularios/SelectorPrincipal.fxml"));
            VBox selectorPane = loader.load();

            SelectorPrincipal selectorController = loader.getController();
            selectorController.setUsuarioData(usuarioLogeado);

            Stage stage = (Stage) emailField.getScene().getWindow();
            
            stage.setTitle("Fitness App - Selector Principal");
            stage.setScene(new Scene(selectorPane));
            stage.setResizable(true);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana del Selector Principal.");
            e.printStackTrace();
        }
    }

    public static void showLoginWindow() {
        try {
            Stage loginStage = new Stage();
            FXMLLoader loader = new FXMLLoader(Login.class.getResource("/login/Login.fxml"));

            Scene scene = new Scene(loader.load());
            loginStage.setScene(scene);
            loginStage.setTitle("Fitness App - Iniciar Sesión");
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la ventana de Login.");
        }
    }
}
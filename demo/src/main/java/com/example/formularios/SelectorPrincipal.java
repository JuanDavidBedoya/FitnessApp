package com.example.formularios;

import java.io.IOException;

import com.example.login.Login;
import com.example.modelos.Usuario;
import com.example.utils.Alerts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SelectorPrincipal {

    private Usuario usuarioLogeado;

    public void setUsuarioData(Usuario usuario) {
        this.usuarioLogeado = usuario;
    }

    private void navegar(ActionEvent event, String fxmlPath, String title) {
        try {
            Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent pane = loader.load();
            Object controller = loader.getController();
            if (controller instanceof CRUDSForm) {
                ((CRUDSForm) controller).setUsuarioData(usuarioLogeado);
            } else if (controller instanceof ReportesForm) {
                ((ReportesForm) controller).setUsuarioData(usuarioLogeado);
            }

            stageActual.setTitle(title);
            stageActual.setScene(new Scene(pane));
            stageActual.show();

        } catch (IOException e) {
            Alerts.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la ventana: " + fxmlPath);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleIrACruds(ActionEvent event) {
        navegar(event, "/formularios/CRUDSForm.fxml", "Fitness App - Módulos CRUD");
    }

    @FXML
    private void handleIrAReportes(ActionEvent event) {
        navegar(event, "/formularios/Reportes.fxml", "Fitness App - Generación de Reportes");
    }
    
    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
        Login.showLoginWindow(); 
    }
}
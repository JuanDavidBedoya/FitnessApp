package com.example.formularios;

import java.io.IOException;

import com.example.modelos.Usuario;
import com.example.utils.Alerts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CRUDSForm {

    private Usuario usuarioLogeado;

    public void setUsuarioData(Usuario usuario) {
        this.usuarioLogeado = usuario;
    }

    private void abrirFormulario(javafx.event.ActionEvent event, String fxmlFileName, String title, ControllerSetup controllerSetup) {
    try {
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();

        String rutaAbsoluta = "/formularios/" + fxmlFileName;
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaAbsoluta));
        Parent pane = loader.load();
        
        controllerSetup.setup(loader.getController());

        stageActual.setTitle(title);
        stageActual.setScene(new Scene(pane));
        stageActual.show();
        
    } catch (IOException e) {
        System.err.println("Error al cargar el formulario: " + fxmlFileName);
        e.printStackTrace();
    } catch (Exception e) {
        System.err.println("Error no esperado durante la navegación: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    @FXML
    private void handleProgreso(javafx.event.ActionEvent event) {
        abrirFormulario(event, "ProgresoForm.fxml", "Fitness App - Mi Progreso", controller -> 
            ((ProgresoForm) controller).setUsuarioLogeado(usuarioLogeado)
        );
    }
    
    @FXML
    private void handleObjetivo(javafx.event.ActionEvent event) {
        abrirFormulario(event, "ObjetivoForm.fxml", "Fitness App - Mis Objetivos", controller -> 
            ((ObjetivoForm) controller).setUsuarioLogeado(usuarioLogeado)
        );
    }

    @FXML
    private void handleRecordatorio(javafx.event.ActionEvent event) {
        abrirFormulario(event, "RecordatorioForm.fxml", "Fitness App - Mis Recordatorios", controller -> 
            ((RecordatorioForm) controller).setUsuarioLogeado(usuarioLogeado)
        );
    }
    
    @FXML
    private void handleRutinaFavorita(javafx.event.ActionEvent event) {
        abrirFormulario(event, "RutinaFavoritaForm.fxml", "Fitness App - Rutinas Favoritas", controller -> 
            ((RutinaFavoritaForm) controller).setUsuarioLogeado(usuarioLogeado)
        );
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
        
        abrirVentanaLogin();
    }

    private void abrirVentanaLogin() {
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/Login.fxml"));
            Parent loginPane = loader.load();

            newStage.setTitle("Fitness App - Iniciar Sesión");
            newStage.setScene(new Scene(loginPane));
            newStage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de Login.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolverAlSelector(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formularios/SelectorPrincipal.fxml"));
            Parent selectorPane = loader.load();

            SelectorPrincipal selectorController = loader.getController();
            selectorController.setUsuarioData(usuarioLogeado); 

            stageActual.setTitle("Fitness App - Selecciona una Opción");
            stageActual.setScene(new Scene(selectorPane));
            stageActual.show();
            
        } catch (IOException e) {
            Alerts.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar el Selector Principal.");
            e.printStackTrace();
        }
    }
    
    @FunctionalInterface
    private interface ControllerSetup {
        void setup(Object controller);
    }
}
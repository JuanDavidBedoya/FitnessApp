package com.example.formularios;

import java.io.IOException;

import com.example.modelos.Usuario;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MenuPrincipal {

    private Usuario usuarioLogeado;

    public void setUsuarioData(Usuario usuario) {
        this.usuarioLogeado = usuario;
    }

    private void abrirFormulario(javafx.event.ActionEvent event, String fxmlFileName, String title, ControllerSetup controllerSetup) {
    try {
        // 1. Obtener la Stage actual a partir del botón presionado
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();

        String rutaAbsoluta = "/formularios/" + fxmlFileName;
        
        // 2. Cargar el FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaAbsoluta));
        AnchorPane pane = loader.load();
        
        // 3. Configurar el controlador específico
        controllerSetup.setup(loader.getController());

        // 4. Reutilizar la Stage actual para la nueva escena
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
            AnchorPane loginPane = loader.load();
            
            newStage.setTitle("Fitness App - Iniciar Sesión");
            newStage.setScene(new Scene(loginPane));
            newStage.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de Login.");
            e.printStackTrace();
        }
    }
    
    @FunctionalInterface
    private interface ControllerSetup {
        void setup(Object controller);
    }
}
package com.example.inicio;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class InicioApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Fitness App - Inicio de Sesión");

        mostrarVentanaLogin();
    }

    public void mostrarVentanaLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/Login.fxml"));
            AnchorPane loginPane = loader.load();

            Scene scene = new Scene(loginPane);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de Login.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

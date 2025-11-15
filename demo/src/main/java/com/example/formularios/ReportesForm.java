package com.example.formularios;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.login.Login;
import com.example.modelos.Usuario;
import com.example.repositorio.Conexion;
import com.example.utils.Alerts;
import com.example.utils.PDFGenerator;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReportesForm {

    @FXML
    private TableView<Map<String, String>> reportesTable;
    @FXML
    private Label reporteTitle;

    private Usuario usuarioLogeado;
    private List<String> currentColumnNames;
    private List<Map<String, String>> currentData;
    private String currentReportName;

    private final Map<String, String> QUERIES = new HashMap<String, String>() {{
        
        // 1. Simple 1: Lista de Entrenadores
        put("Reporte 1: Lista de Entrenadores",
            "SELECT U.cedula AS Cedula, U.primerNombre AS PrimerNombre, U.primerApellido AS PrimerApellido FROM Usuario U JOIN Entrenador E ON U.cedula = E.Usuario_cedula;");

        // 2. Intermedio 1: Progreso de Peso (Clientes)
        put("Reporte 2: Progreso de Peso (Clientes)",
            "SELECT U.primerNombre, U.primerApellido, MIN(P.peso) AS PesoInicialMasBajo, MAX(P.peso) AS PesoRecienteMasAlto FROM Usuario U JOIN General G ON U.cedula = G.Usuario_cedula JOIN Progreso P ON G.Usuario_cedula = P.General_Usuario_cedula GROUP BY U.cedula, U.primerNombre, U.primerApellido ORDER BY U.cedula;");

        // 3. Intermedio 2: Popularidad de Rutinas por Nombre
        put("Reporte 3: Popularidad de Rutinas",
            "SELECT R.nombre AS NombreRutina, COUNT(RF.Rutina_codRutina) AS VecesMarcadaFavorita FROM RutinaFavorita RF JOIN Rutina R ON RF.Rutina_codRutina = R.codRutina GROUP BY R.codRutina, R.nombre ORDER BY VecesMarcadaFavorita DESC;");

        // 4. Complejo 1: Planes por Cliente y Entrenador
        put("Reporte 4: Planes por Cliente/Entrenador",
            "SELECT C.primerNombre AS NombreCliente, C.primerApellido AS ApellidoCliente, E.primerNombre AS NombreEntrenador, PE.nombre AS NombrePlan, OD.nombre AS Objetivo FROM Usuario C JOIN General G ON C.cedula = G.Usuario_cedula JOIN UsuarioOtroUsuario UOU ON G.Usuario_cedula = UOU.General_Usuario_cedula JOIN Usuario E ON UOU.Entrenador_Usuario_cedula = E.cedula JOIN PlanEntrenamiento PE ON E.cedula = PE.Entrenador_Usuario_cedula JOIN ObjetivoDeportivo OD ON PE.ObjetivoDeportivo_codObjetivo = OD.codObjetivo WHERE UOU.tipoRelacion = 'Entrenador-Cliente' ORDER BY E.primerNombre, C.primerNombre;");
    }};


    public void setUsuarioData(Usuario usuario) {
        this.usuarioLogeado = usuario;
    }
    
    @FXML private void handleReporte1(ActionEvent event) { ejecutarReporte("Reporte 1: Lista de Entrenadores", QUERIES.get("Reporte 1: Lista de Entrenadores")); }
    @FXML private void handleReporte2(ActionEvent event) { ejecutarReporte("Reporte 2: Progreso de Peso (Clientes)", QUERIES.get("Reporte 2: Progreso de Peso (Clientes)")); }
    @FXML private void handleReporte3(ActionEvent event) { ejecutarReporte("Reporte 3: Popularidad de Rutinas", QUERIES.get("Reporte 3: Popularidad de Rutinas")); }
    @FXML private void handleReporte4(ActionEvent event) { ejecutarReporte("Reporte 4: Planes por Cliente/Entrenador", QUERIES.get("Reporte 4: Planes por Cliente/Entrenador")); }


    private void ejecutarReporte(String nombreReporte, String sql) {
        reporteTitle.setText("Mostrando: " + nombreReporte);
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            var result = mostrarDatosEnTabla(rs);
            currentColumnNames = result.getKey();
            currentData = result.getValue();
            currentReportName = nombreReporte;

        } catch (SQLException e) {
            Alerts.showAlert(AlertType.ERROR, "Error SQL", "Ocurrió un error al ejecutar la consulta:\n" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Alerts.showAlert(AlertType.ERROR, "Error", "Ocurrió un error inesperado al generar el reporte:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private AbstractMap.SimpleEntry<List<String>, List<Map<String, String>>> mostrarDatosEnTabla(ResultSet rs) throws SQLException {
        reportesTable.getColumns().clear();
        reportesTable.getItems().clear();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<String> columnNames = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i);
            columnNames.add(columnName);
            TableColumn<Map<String, String>, String> column = new TableColumn<>(columnName);

            column.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().get(columnName)
            ));
            reportesTable.getColumns().add(column);
        }

        List<Map<String, String>> data = new ArrayList<>();
        while (rs.next()) {
            Map<String, String> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                row.put(columnName, rs.getString(i) == null ? "" : rs.getString(i));
            }
            data.add(row);
        }
        reportesTable.getItems().addAll(data);
        return new AbstractMap.SimpleEntry<>(columnNames, data);
    }
    
    @FXML
    private void handleVolverAlSelector(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formularios/SelectorPrincipal.fxml")); 
            VBox selectorPane = loader.load();

            SelectorPrincipal selectorController = loader.getController();
            selectorController.setUsuarioData(usuarioLogeado);

            stageActual.setTitle("Fitness App - Selecciona una Opción");
            stageActual.setScene(new Scene(selectorPane));
            stageActual.show();
            
        } catch (IOException e) {
            Alerts.showAlert(AlertType.ERROR, "Error de Navegación", "No se pudo cargar el Selector Principal.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleGenerarPDF(ActionEvent event) {
        if (currentData == null || currentData.isEmpty()) {
            Alerts.showAlert(AlertType.WARNING, "Sin Datos", "No hay datos para generar el PDF. Ejecuta un reporte primero.");
            return;
        }
        try {
            PDFGenerator.generateReport(currentReportName, currentColumnNames, currentData);
        } catch (Exception e) {
            Alerts.showAlert(AlertType.ERROR, "Error al Generar PDF", "Ocurrió un error al generar el PDF:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
        Login.showLoginWindow();
    }
}
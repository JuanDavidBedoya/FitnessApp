package com.example.formularios;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import com.example.modelos.Recordatorio;
import com.example.modelos.Usuario;
import com.example.repositorio.RecordatorioRepositorio;
import com.example.utils.Alerts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RecordatorioForm {

    @FXML private DatePicker fechaPicker;
    @FXML private TextField horaField;
    @FXML private TextArea mensajeArea;
    @FXML private TableView<Recordatorio> recordatorioTable;
    @FXML private TableColumn<Recordatorio, LocalDate> fechaCol;
    @FXML private TableColumn<Recordatorio, LocalTime> horaCol;
    @FXML private TableColumn<Recordatorio, String> mensajeCol;
    @FXML private Button guardarBtn;
    
    private Usuario usuarioLogeado;
    private final RecordatorioRepositorio recordatorioRepo = new RecordatorioRepositorio();
    private ObservableList<Recordatorio> data;
    private Recordatorio recordatorioSeleccionado = null;

    public void setUsuarioLogeado(Usuario usuario) {
        this.usuarioLogeado = usuario;
        fechaPicker.setValue(LocalDate.now());
        cargarDatosRecordatorios();
    }
    
    @FXML
    public void initialize() {
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        horaCol.setCellValueFactory(new PropertyValueFactory<>("hora"));
        mensajeCol.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        
        recordatorioTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    cargarRecordatorioParaEdicion(newSelection);
                }
            }
        );
        
        data = FXCollections.observableArrayList();
        recordatorioTable.setItems(data);
    }

    private void cargarDatosRecordatorios() {
        if (usuarioLogeado != null) {
            data.clear();
            data.addAll(recordatorioRepo.obtenerRecordatoriosPorUsuario(usuarioLogeado.getCedula()));
        }
    }

    private void cargarRecordatorioParaEdicion(Recordatorio recordatorio) {
        this.recordatorioSeleccionado = recordatorio;
        fechaPicker.setValue(recordatorio.getFecha());
        horaField.setText(recordatorio.getHora().toString());
        mensajeArea.setText(recordatorio.getMensaje());
        guardarBtn.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {
        if (mensajeArea.getText().isEmpty() || fechaPicker.getValue() == null || horaField.getText().isEmpty()) {
            Alerts.showAlert(AlertType.ERROR, "Error de Validación", "Complete todos los campos de mensaje, fecha y hora.");
            return;
        }

        try {
            LocalDate fecha = fechaPicker.getValue();
            LocalTime hora = LocalTime.parse(horaField.getText()); // Formato HH:MM:SS
            String mensaje = mensajeArea.getText();
            String cedula = usuarioLogeado.getCedula();
            
            boolean exito = false;

            if (recordatorioSeleccionado == null) {
                Recordatorio nuevoRecordatorio = new Recordatorio(mensaje, fecha, hora, cedula);
                exito = recordatorioRepo.crearRecordatorio(nuevoRecordatorio);
            } else {
                recordatorioSeleccionado.setMensaje(mensaje);
                recordatorioSeleccionado.setFecha(fecha);
                recordatorioSeleccionado.setHora(hora);
                exito = recordatorioRepo.actualizarRecordatorio(recordatorioSeleccionado);
            }

            if (exito) {
                Alerts.showAlert(AlertType.INFORMATION, "Éxito", "Recordatorio guardado correctamente.");
                limpiarCampos();
                cargarDatosRecordatorios();
            } else {
                Alerts.showAlert(AlertType.ERROR, "Error de DB", "No se pudo guardar el recordatorio.");
            }

        } catch (DateTimeParseException e) {
            Alerts.showAlert(AlertType.ERROR, "Error de Formato", "La hora debe estar en formato HH:MM:SS (ej: 08:30:00).");
        }
    }
    
    @FXML
    private void handleEliminar() {
        Recordatorio recordatorioAEliminar = recordatorioTable.getSelectionModel().getSelectedItem();
        
        if (recordatorioAEliminar != null) {
            Optional<ButtonType> result = Alerts.showConfirmation("Confirmar Eliminación", 
                "¿Está seguro de que desea eliminar el recordatorio del " + recordatorioAEliminar.getFecha() + "?");
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (recordatorioRepo.eliminarRecordatorio(recordatorioAEliminar.getCodRecordatorio())) {
                    Alerts.showAlert(AlertType.INFORMATION, "Éxito", "Recordatorio eliminado correctamente.");
                    limpiarCampos();
                    cargarDatosRecordatorios();
                } else {
                    Alerts.showAlert(AlertType.ERROR, "Error de DB", "No se pudo eliminar el recordatorio.");
                }
            }
        } else {
            Alerts.showAlert(AlertType.WARNING, "Advertencia", "Seleccione un recordatorio para eliminar.");
        }
    }

    @FXML
    private void handleNuevo() {
        limpiarCampos();
    }

    @FXML
    private void handleVolverAlMenu(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formularios/MenuPrincipal.fxml"));
            AnchorPane menuPane = loader.load();

            MenuPrincipal menuController = loader.getController();
            menuController.setUsuarioData(usuarioLogeado);

            stageActual.setTitle("Fitness App - Menú Principal");
            stageActual.setScene(new Scene(menuPane));
            stageActual.show();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana del Menú Principal.");
            e.printStackTrace();
        }
    }
    
    private void limpiarCampos() {
        recordatorioSeleccionado = null;
        fechaPicker.setValue(LocalDate.now());
        horaField.clear();
        mensajeArea.clear();
        guardarBtn.setText("Guardar");
        recordatorioTable.getSelectionModel().clearSelection();
    }
}
package com.example.formularios;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import com.example.modelos.Progreso;
import com.example.modelos.Usuario;
import com.example.repositorio.ProgresoRepositorio;
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

public class ProgresoForm {

    @FXML private DatePicker fechaRegistroPicker;
    @FXML private TextField pesoField;
    @FXML private TextField caloriasField;
    @FXML private TextArea observacionesArea;
    @FXML private TextField busquedaTextField;
    @FXML private TableView<Progreso> progresoTable;
    @FXML private TableColumn<Progreso, LocalDate> fechaCol;
    @FXML private TableColumn<Progreso, Double> pesoCol;
    @FXML private TableColumn<Progreso, Double> caloriasCol;
    @FXML private TableColumn<Progreso, String> observacionesCol;
    @FXML private Button guardarBtn;
    
    private Usuario usuarioLogeado;
    private final ProgresoRepositorio progresoRepo = new ProgresoRepositorio();
    private ObservableList<Progreso> data;
    private Progreso progresoSeleccionado = null;

    public void setUsuarioLogeado(Usuario usuario) {
        this.usuarioLogeado = usuario;
        fechaRegistroPicker.setValue(LocalDate.now());
        cargarDatosProgreso();
    }
    
    @FXML
    public void initialize() {

        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        pesoCol.setCellValueFactory(new PropertyValueFactory<>("peso"));
        caloriasCol.setCellValueFactory(new PropertyValueFactory<>("caloriasQuemadas"));
        observacionesCol.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
        
        progresoTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    cargarProgresoParaEdicion(newSelection);
                }
            }
        );

        busquedaTextField.textProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            filtrarProgresos(valorNuevo);
        });
        
        data = FXCollections.observableArrayList();
        progresoTable.setItems(data);
    }

    private void cargarDatosProgreso() {
        if (usuarioLogeado != null) {
            data.clear();
            data.addAll(progresoRepo.obtenerProgresosPorUsuario(usuarioLogeado.getCedula()));
        }
    }

    private void cargarProgresoParaEdicion(Progreso progreso) {
        this.progresoSeleccionado = progreso;
        fechaRegistroPicker.setValue(progreso.getFechaRegistro());
        pesoField.setText(String.valueOf(progreso.getPeso()));
        caloriasField.setText(String.valueOf(progreso.getCaloriasQuemadas()));
        observacionesArea.setText(progreso.getObservaciones());
        guardarBtn.setText("Actualizar");
    }

    @FXML
    private void handleGuardar() {

        if (fechaRegistroPicker.getValue() == null || pesoField.getText().isEmpty() || caloriasField.getText().isEmpty()) {
            Alerts.showAlert(AlertType.ERROR, "Error de Validación", "Complete al menos Fecha, Peso y Calorías.");
            return;
        }

        try {
            LocalDate fecha = fechaRegistroPicker.getValue();
            double peso = Double.parseDouble(pesoField.getText());
            double calorias = Double.parseDouble(caloriasField.getText());
            String observaciones = observacionesArea.getText();
            String cedula = usuarioLogeado.getCedula();
            
            boolean exito = false;

            if (progresoSeleccionado == null) {
                Progreso nuevoProgreso = new Progreso(fecha, peso, calorias, observaciones, cedula);
                exito = progresoRepo.crearProgreso(nuevoProgreso);
            } else {
                progresoSeleccionado.setFechaRegistro(fecha);
                progresoSeleccionado.setPeso(peso);
                progresoSeleccionado.setCaloriasQuemadas(calorias);
                progresoSeleccionado.setObservaciones(observaciones);
                exito = progresoRepo.actualizarProgreso(progresoSeleccionado);
            }

            if (exito) {
                Alerts.showAlert(AlertType.INFORMATION, "Éxito", "Registro guardado correctamente.");
                limpiarCampos();
                cargarDatosProgreso();
            } else {
                Alerts.showAlert(AlertType.ERROR, "Error de DB", "No se pudo guardar el registro en la base de datos.");
            }

        } catch (NumberFormatException e) {
            Alerts.showAlert(AlertType.ERROR, "Error de Formato", "Peso y Calorías deben ser números válidos.");
        }
    }
    
    @FXML
    private void handleEliminar() {
        Progreso progresoAEliminar = progresoTable.getSelectionModel().getSelectedItem();
        
        if (progresoAEliminar != null) {
            Optional<ButtonType> result = Alerts.showConfirmation("Confirmar Eliminación", 
                "¿Está seguro de que desea eliminar el registro del " + progresoAEliminar.getFechaRegistro() + "?");
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (progresoRepo.eliminarProgreso(progresoAEliminar.getCodProgreso())) {
                    Alerts.showAlert(AlertType.INFORMATION, "Éxito", "Registro eliminado correctamente.");
                    limpiarCampos();
                    cargarDatosProgreso();
                } else {
                    Alerts.showAlert(AlertType.ERROR, "Error de DB", "No se pudo eliminar el registro.");
                }
            }
        } else {
            Alerts.showAlert(AlertType.WARNING, "Advertencia", "Seleccione un registro para eliminar.");
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
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formularios/CRUDSForm.fxml"));
            AnchorPane menuPane = loader.load();

            CRUDSForm menuController = loader.getController();
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
        progresoSeleccionado = null;
        fechaRegistroPicker.setValue(LocalDate.now());
        pesoField.clear();
        caloriasField.clear();
        observacionesArea.clear();
        guardarBtn.setText("Guardar");
        progresoTable.getSelectionModel().clearSelection();
    }

    public void handleBuscarProgreso(ActionEvent actionEvent) {
        filtrarProgresos(busquedaTextField.getText());
    }

    private void filtrarProgresos(String busqueda) {
        cargarDatosProgreso();

        if (busqueda == null || busqueda.trim().isEmpty()) {
            return;
        }

        String filtro = busqueda.toLowerCase();

        data.setAll(
                data.stream()
                        .filter(progreso ->
                                String.valueOf(progreso.getPeso()).contains(filtro) ||
                                        String.valueOf(progreso.getCaloriasQuemadas()).contains(filtro) ||
                                        (progreso.getFechaRegistro() != null &&
                                                progreso.getFechaRegistro().toString().contains(filtro)) ||
                                        (progreso.getObservaciones() != null &&
                                                progreso.getObservaciones().toLowerCase().contains(filtro))
                        )
                        .toList()
        );
    }
}
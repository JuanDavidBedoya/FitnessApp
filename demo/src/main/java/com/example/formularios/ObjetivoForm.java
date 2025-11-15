package com.example.formularios;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.modelos.ObjetivoAsignado;
import com.example.modelos.Usuario;
import com.example.repositorio.ObjetivoRepositorio;
import com.example.utils.Alerts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ObjetivoForm {

    @FXML private ComboBox<String> objetivoComboBox;
    @FXML private DatePicker fechaAsignacionPicker;
    @FXML private TextArea descripcionObjetivoArea;
    @FXML private TextField busquedaTextField;
    @FXML private TableView<ObjetivoAsignado> objetivosTable;
    @FXML private TableColumn<ObjetivoAsignado, String> nombreCol;
    @FXML private TableColumn<ObjetivoAsignado, String> descripcionCol;
    @FXML private TableColumn<ObjetivoAsignado, LocalDate> fechaAsignacionCol;
    
    private Usuario usuarioLogeado;
    private final ObjetivoRepositorio objetivoRepo = new ObjetivoRepositorio();
    private ObservableList<ObjetivoAsignado> objetivosDisponibles;
    private ObservableList<ObjetivoAsignado> objetivosAsignados;

    private List<ObjetivoAsignado> listaTodosObjetivos;

    public void setUsuarioLogeado(Usuario usuario) {
        this.usuarioLogeado = usuario;
        fechaAsignacionPicker.setValue(LocalDate.now());
        cargarDatosIniciales();
    }
    
    @FXML
    public void initialize() {

        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionCol.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        fechaAsignacionCol.setCellValueFactory(new PropertyValueFactory<>("fechaAsignacion"));
        
        objetivosAsignados = FXCollections.observableArrayList();
        objetivosTable.setItems(objetivosAsignados);
      
        objetivoComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            mostrarDescripcion(newValue);
        });
        busquedaTextField.textProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            filtrarObjetivos(valorNuevo);
        });
    }
    
    private void cargarDatosIniciales() {
        if (usuarioLogeado != null) {

            listaTodosObjetivos = objetivoRepo.obtenerTodosLosObjetivos();
            objetivosDisponibles = FXCollections.observableArrayList();
            for (ObjetivoAsignado obj : listaTodosObjetivos) {
                objetivosDisponibles.add(obj);
                objetivoComboBox.getItems().add(obj.getNombre());
            }

            cargarObjetivosAsignados();
        }
    }
    
    private void cargarObjetivosAsignados() {
        if (usuarioLogeado != null) {
            objetivosAsignados.clear();
            objetivosAsignados.addAll(objetivoRepo.obtenerObjetivosAsignados(usuarioLogeado.getCedula()));
        }
    }
    
    private void mostrarDescripcion(String nombreObjetivo) {
        if (nombreObjetivo == null) return;
        for (ObjetivoAsignado obj : listaTodosObjetivos) {
            if (obj.getNombre().equals(nombreObjetivo)) {
                descripcionObjetivoArea.setText(obj.getDescripcion());
                return;
            }
        }
        descripcionObjetivoArea.setText("");
    }

    @FXML
    private void handleAsignarObjetivo() {
        String nombreSeleccionado = objetivoComboBox.getSelectionModel().getSelectedItem();
        LocalDate fecha = fechaAsignacionPicker.getValue();

        if (nombreSeleccionado == null || fecha == null) {
            Alerts.showAlert(AlertType.ERROR, "Error de Validación", "Seleccione un objetivo y una fecha de asignación.");
            return;
        }
        
        int codObjetivo = -1;
        for (ObjetivoAsignado obj : listaTodosObjetivos) {
            if (obj.getNombre().equals(nombreSeleccionado)) {
                codObjetivo = obj.getCodObjetivo();
                break;
            }
        }
        
        if (codObjetivo != -1) {
            if (objetivoRepo.asignarObjetivo(usuarioLogeado.getCedula(), codObjetivo, fecha)) {
                Alerts.showAlert(AlertType.INFORMATION, "Éxito", "Objetivo asignado correctamente.");
                cargarObjetivosAsignados();
            }
        }
    }
    
    @FXML
    private void handleDesasignarObjetivo() {
        ObjetivoAsignado objetivoAEliminar = objetivosTable.getSelectionModel().getSelectedItem();
        
        if (objetivoAEliminar != null) {
            Optional<ButtonType> result = Alerts.showConfirmation("Confirmar Desasignación", 
                "¿Está seguro de que desea desasignar el objetivo: " + objetivoAEliminar.getNombre() + "?");
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (objetivoRepo.desasignarObjetivo(usuarioLogeado.getCedula(), objetivoAEliminar.getCodObjetivo())) {
                    Alerts.showAlert(AlertType.INFORMATION, "Éxito", "Objetivo desasignado correctamente.");
                    cargarObjetivosAsignados();
                } else {
                    Alerts.showAlert(AlertType.ERROR, "Error de DB", "No se pudo desasignar el objetivo.");
                }
            }
        } else {
            Alerts.showAlert(AlertType.WARNING, "Advertencia", "Seleccione un objetivo para desasignar.");
        }
    }

    @FXML
    private void handleVolverAlMenu(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/formularios/CRUDSForm.fxml"));
            AnchorPane menuPane = loader.load();

            CRUDSForm menuController = loader.getController();
            menuController.setUsuarioData(usuarioLogeado);

            stageActual.setTitle("Fitness App - Administración de Datos");
            stageActual.setScene(new Scene(menuPane));
            stageActual.show();
            
        } catch (IOException e) {
            System.err.println("Error al cargar la ventana de los CRUDs.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBuscarObjetivo(ActionEvent actionEvent) {
        filtrarObjetivos(busquedaTextField.getText());
    }

    private void filtrarObjetivos(String busqueda) {
        cargarObjetivosAsignados();

        if (busqueda == null || busqueda.trim().isEmpty()) {
            return;
        }

        String filtro = busqueda.toLowerCase();

        objetivosAsignados.setAll(
                objetivosAsignados.stream()
                        .filter(objetivo ->
                                objetivo.getNombre().toLowerCase().contains(filtro) ||
                                        (objetivo.getDescripcion() != null && objetivo.getDescripcion().toLowerCase().contains(filtro)) ||
                                        (objetivo.getFechaAsignacion() != null && objetivo.getFechaAsignacion().toString().contains(filtro))
                        )
                        .toList()
        );
    }
}

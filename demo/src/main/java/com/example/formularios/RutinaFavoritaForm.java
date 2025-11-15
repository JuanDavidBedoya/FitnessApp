package com.example.formularios;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.modelos.RutinaFavoritaModelo;
import com.example.modelos.Usuario;
import com.example.repositorio.FavoritaRepositorio;
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

public class RutinaFavoritaForm {

    @FXML private ComboBox<String> rutinasComboBox;
    @FXML private TextArea descripcionRutinaArea;
    @FXML private TableView<RutinaFavoritaModelo> favoritasTable;
    @FXML private TableColumn<RutinaFavoritaModelo, String> nombreCol;
    @FXML private TableColumn<RutinaFavoritaModelo, String> duracionCol;
    @FXML private TableColumn<RutinaFavoritaModelo, LocalDate> fechaFavCol;
    @FXML private TextField busquedatextField;

    private Usuario usuarioLogeado;
    private final FavoritaRepositorio favoritaRepo = new FavoritaRepositorio();
    private ObservableList<RutinaFavoritaModelo> favoritasActivas;

    private List<RutinaFavoritaModelo> listaTodasRutinas;

    public void setUsuarioLogeado(Usuario usuario) {
        this.usuarioLogeado = usuario;
        cargarDatosIniciales();
    }

    @FXML
    public void initialize() {

        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombreRutina"));
        duracionCol.setCellValueFactory(new PropertyValueFactory<>("duracionRutina"));
        fechaFavCol.setCellValueFactory(new PropertyValueFactory<>("fechaFavorito"));

        busquedatextField.textProperty().addListener((observable, valorAnterior, valorNuevo) -> {
            filtrarRutinas(valorNuevo);
        });
        favoritasActivas = FXCollections.observableArrayList();
        favoritasTable.setItems(favoritasActivas);

        rutinasComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            mostrarDescripcion(newValue);
        });
    }

    private void cargarDatosIniciales() {
        if (usuarioLogeado != null) {
            listaTodasRutinas = favoritaRepo.obtenerRutinasDisponibles();
            for (RutinaFavoritaModelo rut : listaTodasRutinas) {
                rutinasComboBox.getItems().add(rut.getNombreRutina());
            }

            cargarFavoritasActivas();
        }
    }

    private void cargarFavoritasActivas() {
        if (usuarioLogeado != null) {
            favoritasActivas.clear();
            favoritasActivas.addAll(favoritaRepo.obtenerFavoritasActivas(usuarioLogeado.getCedula()));
        }
    }

    private void mostrarDescripcion(String nombreRutina) {
        if (nombreRutina == null) return;
        for (RutinaFavoritaModelo rut : listaTodasRutinas) {
            if (rut.getNombreRutina().equals(nombreRutina)) {
                descripcionRutinaArea.setText(rut.getDescripcionRutina());
                return;
            }
        }
        descripcionRutinaArea.setText("");
    }

    @FXML
    private void handleMarcarFavorita() {
        String nombreSeleccionado = rutinasComboBox.getSelectionModel().getSelectedItem();

        if (nombreSeleccionado == null) {
            Alerts.showAlert(AlertType.ERROR, "Error de Validación", "Seleccione una rutina para marcar como favorita.");
            return;
        }

        int codRutina = -1;
        for (RutinaFavoritaModelo rut : listaTodasRutinas) {
            if (rut.getNombreRutina().equals(nombreSeleccionado)) {
                codRutina = rut.getCodRutina();
                break;
            }
        }

        if (codRutina != -1) {
            if (favoritaRepo.marcarComoFavorita(usuarioLogeado.getCedula(), codRutina)) {
                Alerts.showAlert(AlertType.INFORMATION, "Éxito", "Rutina marcada como favorita.");
                cargarFavoritasActivas();
            }
        }
    }

    @FXML
    private void handleDesmarcarFavorita() {
        RutinaFavoritaModelo rutinaADesmarcar = favoritasTable.getSelectionModel().getSelectedItem();

        if (rutinaADesmarcar != null) {
            Optional<ButtonType> result = Alerts.showConfirmation("Confirmar Desmarque",
                    "¿Está seguro de que desea desmarcar la rutina: " + rutinaADesmarcar.getNombreRutina() + "?");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (favoritaRepo.desmarcarComoFavorita(usuarioLogeado.getCedula(), rutinaADesmarcar.getCodRutina())) {
                    Alerts.showAlert(AlertType.INFORMATION, "Éxito", "Rutina desmarcada correctamente.");
                    cargarFavoritasActivas();
                } else {
                    Alerts.showAlert(AlertType.ERROR, "Error de DB", "No se pudo desmarcar la rutina.");
                }
            }
        } else {
            Alerts.showAlert(AlertType.WARNING, "Advertencia", "Seleccione una rutina para desmarcar.");
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

            stageActual.setTitle("Fitness App - Menú Principal");
            stageActual.setScene(new Scene(menuPane));
            stageActual.show();

        } catch (IOException e) {
            System.err.println("Error al cargar la ventana del Menú Principal.");
            e.printStackTrace();
        }
    }

    public void handleBuscarRutina(ActionEvent actionEvent) {
        filtrarRutinas(busquedatextField.getText());
    }

    private void filtrarRutinas(String busqueda) {
        cargarFavoritasActivas();

        if (busqueda == null || busqueda.trim().isEmpty()) {
            return;
        }

        String filtro = busqueda.toLowerCase();


        favoritasActivas.setAll(
                favoritasActivas.stream()
                        .filter(recordatorio ->
                                recordatorio.getNombreRutina().toLowerCase().contains(filtro) ||
                                        (recordatorio.getDuracionRutina() != null && recordatorio.getDuracionRutina().toString().contains(filtro)) ||
                                        (recordatorio.getFechaFavorito() != null && recordatorio.getFechaFavorito().toString().contains(filtro))
                        )
                        .toList()
        );
    }

}
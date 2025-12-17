package sk.upjs.paz.enclosure;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.security.Auth;
import sk.upjs.paz.user.Role;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnclosureViewController {

    @FXML
    public TableColumn<Enclosure, String> animalsCountCol;
    @FXML
    public ComboBox<String> zoneFilterComboBox;
    @FXML
    public Label userNameLabel;
    @FXML
    public Label roleLabel;
    @FXML
    public TextField searchTextField;
    @FXML
    private Button addEnclosureButton;
    @FXML
    private TableView<Enclosure> enclosuresTable;
    @FXML
    private TableColumn<Enclosure, String> lastMaintainanceCol;
    @FXML
    private TableColumn<Enclosure, String> nameCol;
    @FXML
    private TableColumn<Enclosure, String> zoneCol;

    private final EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    private final ObservableList<Enclosure> masterData = FXCollections.observableArrayList();
    private FilteredList<Enclosure> filteredData;

    @FXML
    void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();

        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        if (principal.getRole() != Role.ADMIN) {
            addEnclosureButton.setDisable(true);
        }

        if (principal.getRole() == Role.ADMIN || principal.getRole() == Role.MANAGER) {
            SceneManager.setupDoubleClick(
                    enclosuresTable,
                    "/sk.upjs.paz/enclosure/EnclosureEdit.fxml",
                    "Upraviť výbeh",
                    EnclosureEditController::setEnclosure
            );
        } else {
            enclosuresTable.setOnMouseClicked(Event::consume);
        }

        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        zoneCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getZone()));

        animalsCountCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getAnimalsCount())));

        lastMaintainanceCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLastMaintainance() != null) {
                return new SimpleStringProperty(cellData.getValue().getLastMaintainance().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            } else {
                return new SimpleStringProperty("");
            }
        });

        loadZones();

        zoneFilterComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(String item) {
                if (item == null) return "";
                if (item.equals("filter.all")) {
                    try {
                        return SceneManager.getBundle().getString(item);
                    } catch (Exception e) {
                        return item;
                    }
                }
                String zoneKey = "zone." + item;
                try {
                    return SceneManager.getBundle().getString(zoneKey);
                } catch (Exception e) {
                    return item;
                }
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });
        zoneFilterComboBox.getSelectionModel().select("filter.all");
        zoneFilterComboBox.setOnAction(e -> applyFilters());

        // nastavenie filteredData
        filteredData = new FilteredList<>(masterData, p -> true);
        enclosuresTable.setItems(filteredData);

        // search bar listener
        searchTextField.textProperty().addListener((obs, oldText, newText) -> applyFilters());

        loadEnclosures();
    }

    private void loadZones() {
        List<Enclosure> enclosures = enclosureDao.getAll();
        Set<String> zones = new HashSet<>();
        for (Enclosure enclosure : enclosures) {
            zones.add(enclosure.getZone());
        }
        zoneFilterComboBox.getItems().clear();
        zoneFilterComboBox.getItems().add("filter.all");
        zoneFilterComboBox.getItems().addAll(zones);
    }

    private void applyFilters() {
        String searchText = searchTextField.getText();
        String selectedZone = zoneFilterComboBox.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(enclosure -> {

            if (selectedZone != null && !selectedZone.equals("filter.all")) {
                if (!enclosure.getZone().equals(selectedZone)) {
                    return false;
                }
            }

            if (searchText == null || searchText.isBlank()) {
                return true;
            }

            String filter = searchText.toLowerCase();

            return enclosure.getName().toLowerCase().contains(filter)
                    || enclosure.getZone().toLowerCase().contains(filter)
                    || enclosure.getAnimalsCount().toString().contains(filter)
                    || (enclosure.getLastMaintainance() != null && enclosure.getLastMaintainance().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).contains(filter));
        });
    }

    private void loadEnclosures() {
        List<Enclosure> enclosures = enclosureDao.getAllSortedByZone();
        masterData.setAll(enclosures);
        applyFilters();
    }

    @FXML
    void addEnclosureButtonAction(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/enclosure/EnclosureEdit.fxml", "Pridavanie výbehu");
    }

    @FXML
    void goBackButtonAction(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}

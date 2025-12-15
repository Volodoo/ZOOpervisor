package sk.upjs.paz.enclosure;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.animal.AnimalEditController;
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
    private Button addEnclosureButton;

    @FXML
    private TableView<Enclosure> enclosuresTable;


    @FXML
    private TableColumn<Enclosure, String> lastMaintainanceCol;

    @FXML
    private TableColumn<Enclosure, String> nameCol;

    @FXML
    private TableColumn<Enclosure, String> zoneCol;


    private EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    @FXML
    void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();

        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        if (principal.getRole() != Role.ADMIN) {
            addEnclosureButton.setDisable(true);
        }
        if (principal.getRole() == Role.ADMIN || principal.getRole() == Role.MANAGER) {
            SceneManager.setupDoubleClick(enclosuresTable, "/sk.upjs.paz/enclosure/EnclosureEdit.fxml", "Upraviť zviera", EnclosureEditController::setEnclosure);
        } else {
            enclosuresTable.setOnMouseClicked(Event::consume);
        }


        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        zoneCol.setCellValueFactory(new PropertyValueFactory<>("zone"));

        animalsCountCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAnimalsCount())));

        lastMaintainanceCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLastMaintainance() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                return new SimpleStringProperty(cellData.getValue().getLastMaintainance().format(formatter));
            } else {
                return new SimpleStringProperty("");
            }
        });

        loadZones();
        zoneFilterComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String item) {
                if (item == null) return "";

                // 1. Ak je to kľúč pre "Všetky"
                if (item.equals("filter.all")) {
                    try {
                        return SceneManager.getBundle().getString(item);
                    } catch (Exception e) {
                        return item;
                    }
                }

                // 2. Ak je to názov zóny (napr. "Savana")
                // Skúsime nájsť kľúč "zone.Savana"
                String zoneKey = "zone." + item;
                try {
                    return SceneManager.getBundle().getString(zoneKey);
                } catch (Exception e) {
                    // Ak preklad neexistuje (zabudol si ho pridať do súboru),
                    // vráti pôvodný názov z databázy.
                    return item;
                }
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });
        loadEnclosures();
        zoneFilterComboBox.setOnAction(event -> filterEnclosuresByZone());
    }

    private void loadZones() {
        List<Enclosure> enclosures = enclosureDao.getAll();
        Set<String> zones = new HashSet<>();

        for (Enclosure enclosure : enclosures) {
            zones.add(enclosure.getZone());
        }

        zoneFilterComboBox.getItems().add("filter.all");
        zoneFilterComboBox.getItems().addAll(zones);
        zoneFilterComboBox.getSelectionModel().select("filter.all");
    }

    private void filterEnclosuresByZone() {
        String selectedZone = zoneFilterComboBox.getSelectionModel().getSelectedItem();

        if (selectedZone != null) {
            if (selectedZone.equals("filter.all")) {
                loadEnclosures();
            } else {
                List<Enclosure> enclosures = enclosureDao.getByZone(selectedZone);
                ObservableList<Enclosure> enclosureObservableList = FXCollections.observableArrayList(enclosures);
                enclosuresTable.setItems(enclosureObservableList);
            }
        }
    }

    private void loadEnclosures() {
        List<Enclosure> enclosures = enclosureDao.getAllSortedByZone();
        ObservableList<Enclosure> enclosureObservableList = FXCollections.observableArrayList(enclosures);
        enclosuresTable.setItems(enclosureObservableList);
    }

    @FXML
    void addEnclosureButtonAction(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/enclosure/EnclosureEdit.fxml", "Pridavanie vybehu");
    }

    @FXML
    void goBackButtonAction(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}

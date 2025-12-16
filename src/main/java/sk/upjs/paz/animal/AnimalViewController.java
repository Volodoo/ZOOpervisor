package sk.upjs.paz.animal;

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
import sk.upjs.paz.security.Auth;
import sk.upjs.paz.user.Role;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AnimalViewController {

    @FXML
    public TableColumn<Animal, String> birthdayCol;

    @FXML
    public TableColumn<Animal, String> lastCheckCol;

    @FXML
    public TableColumn<Animal, String> statusCol;

    @FXML
    public ComboBox<String> speciesFilterComboBox;

    @FXML
    public Button addAnimalButton;
    @FXML
    public Label userNameLabel;
    @FXML
    public Label roleLabel;
    @FXML
    private TableColumn<Animal, String> enclosureCol;

    @FXML
    private TableView<Animal> animalsTable;

    @FXML
    private TableColumn<Animal, String> nicknameCol;

    @FXML
    private TableColumn<Animal, String> sexCol;

    @FXML
    private TableColumn<Animal, String> speciesCol;

    private AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();

    @FXML
    void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();

        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        if (principal == null || principal.getRole() != Role.ADMIN) {
            addAnimalButton.setDisable(true);
        }
        if (principal.getRole() == Role.ADMIN || principal.getRole() == Role.MANAGER) {
            SceneManager.setupDoubleClick(animalsTable, "/sk.upjs.paz/animal/AnimalEdit.fxml", "Upraviť zviera", AnimalEditController::setAnimal);
        } else {
            animalsTable.setOnMouseClicked(Event::consume);
        }

        speciesFilterComboBox.getItems().add("filter.all");
        speciesFilterComboBox.getItems().add("filter.active");
        speciesFilterComboBox.getItems().add("filter.inactive");
        speciesFilterComboBox.getItems().add("filter.treatment");

        speciesFilterComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String key) {
                if (key == null) return "";
                try {
                    return SceneManager.getBundle().getString(key);
                } catch (Exception e) {
                    return key;
                }
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });

        speciesFilterComboBox.getSelectionModel().select("filter.all");

        speciesFilterComboBox.setOnAction(event -> filterAnimalsByStatus());

        nicknameCol.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        speciesCol.setCellValueFactory(new PropertyValueFactory<>("species"));
        sexCol.setCellValueFactory(new PropertyValueFactory<>("sex"));
        birthdayCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return new SimpleStringProperty(cellData.getValue().getBirthDay().format(formatter));
        });
        enclosureCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnclosure().getName()));
        lastCheckCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLastCheck() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                return new SimpleStringProperty(cellData.getValue().getLastCheck().format(formatter));
            } else {
                return new SimpleStringProperty("");
            }
        });
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        loadAnimals();
    }

    private void filterAnimalsByStatus() {
        String selectedStatus = speciesFilterComboBox.getSelectionModel().getSelectedItem();

        if (selectedStatus != null) {
            if (selectedStatus.equals("filter.all")) {
                loadAnimals();
            } else {
                Status status = convertStringToStatus(selectedStatus);
                List<Animal> animals = animalDao.getByStatus(status);
                ObservableList<Animal> animalObservableList = FXCollections.observableArrayList(animals);
                animalsTable.setItems(animalObservableList);
            }
        }
    }

    @FXML
    public void OnActionSearchTextField(){}

    private Status convertStringToStatus(String statusString) {
        switch (statusString) {
            case "filter.active":
                return Status.ACTIVE;
            case "filter.inactive":
                return Status.INACTIVE;
            case "filter.treatment":
                return Status.TREATMENT;
            default:
                throw new IllegalArgumentException("Neznámy status: " + statusString);
        }
    }

    private void loadAnimals() {
        String selectedStatus = speciesFilterComboBox.getSelectionModel().getSelectedItem();

        List<Animal> animals;
        if (selectedStatus == null || selectedStatus.equals("filter.all")) {
            animals = animalDao.getAllSortedByZoneSpecies();
        } else {
            Status status = convertStringToStatus(selectedStatus);
            animals = animalDao.getByStatus(status);
        }

        ObservableList<Animal> animalObservableList = FXCollections.observableArrayList(animals);
        animalsTable.setItems(animalObservableList);
    }

    @FXML
    void addAnimalButtonAction(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/animal/AnimalEdit.fxml", "Pridavanie Zvierata");
    }

    @FXML
    void goBackButtonAction(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}
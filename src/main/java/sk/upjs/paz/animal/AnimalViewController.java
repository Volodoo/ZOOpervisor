package sk.upjs.paz.animal;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
            animalsTable.setOnMouseClicked(Event::consume);
        } else {
            SceneManager.setupDoubleClick(animalsTable, "/sk.upjs.paz/animal/AnimalEdit.fxml", "Upraviť zviera", AnimalEditController::setAnimal);
        }

        speciesFilterComboBox.getItems().add("Všetky");
        speciesFilterComboBox.getItems().add("Aktívne");
        speciesFilterComboBox.getItems().add("Neaktívne");
        speciesFilterComboBox.getItems().add("Liečba");

        speciesFilterComboBox.getSelectionModel().select("Všetky");

        speciesFilterComboBox.setOnAction(event -> filterAnimalsByStatus());

        // Nastavenie stĺpcov pre TableView
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
            if (selectedStatus.equals("Všetky")) {
                loadAnimals();
            } else {
                Status status = convertStringToStatus(selectedStatus);
                List<Animal> animals = animalDao.getByStatus(status);
                ObservableList<Animal> animalObservableList = FXCollections.observableArrayList(animals);
                animalsTable.setItems(animalObservableList);
            }
        }
    }

    private Status convertStringToStatus(String statusString) {
        switch (statusString) {
            case "Aktívne":
                return Status.ACTIVE;
            case "Neaktívne":
                return Status.INACTIVE;
            case "Liečba":
                return Status.TREATMENT;
            default:
                throw new IllegalArgumentException("Neznámy status: " + statusString);
        }
    }

    private void loadAnimals() {
        String selectedStatus = speciesFilterComboBox.getSelectionModel().getSelectedItem();

        List<Animal> animals;
        if (selectedStatus == null || selectedStatus.equals("Všetky")) {
            animals = animalDao.getAllSortedBySpecies();
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
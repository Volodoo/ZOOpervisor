package sk.upjs.paz.animal;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;

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
            return new SimpleStringProperty(
                    cellData.getValue().getLastCheck() != null ?
                            cellData.getValue().getLastCheck().toLocalDate().toString() :
                            "Ešte Neprebehla"
            );
        });
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        SceneManager.setupDoubleClick(
                animalsTable,
                "/sk.upjs.paz/animal/AnimalEdit.fxml",
                "Upraviť zviera",
                (AnimalEditController ctrl, Animal animal) -> ctrl.setAnimal(animal)
        );

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
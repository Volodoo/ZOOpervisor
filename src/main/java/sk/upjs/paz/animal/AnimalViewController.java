package sk.upjs.paz.animal;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;

import java.io.IOException;
import java.time.LocalDate;
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
    private TableColumn<Animal, String> enclosureCol;

    @FXML
    private Button addAnimalButton;

    @FXML
    private TableView<Animal> animalsTable;

    @FXML
    private Button deleteAnimalButton;

    @FXML
    private Button goBackButton;

    @FXML
    private TableColumn<Animal, String> nicknameCol;

    @FXML
    private TableColumn<Animal, String> sexCol;

    @FXML
    private TableColumn<Animal, String> speciesCol;

    private AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();

    @FXML
    void initialize() {
        nicknameCol.setCellValueFactory(new PropertyValueFactory<>("nickname"));

        speciesCol.setCellValueFactory(new PropertyValueFactory<>("species"));

        sexCol.setCellValueFactory(new PropertyValueFactory<>("sex"));

        birthdayCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return new SimpleStringProperty(cellData.getValue().getBirthDay().format(formatter));
        });

        enclosureCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEnclosure().getName())
        );

        lastCheckCol.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(
                    cellData.getValue().getLastCheck() != null ?
                            cellData.getValue().getLastCheck().toLocalDate().toString() :
                            "Ešte Neprebehla"
            );
        });

        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().toString())
        );

        loadAnimals();
    }

    private void loadAnimals() {
        List<Animal> animals = animalDao.getAllSortedBySpecies();
        ObservableList<Animal> animalObservableList = FXCollections.observableArrayList(animals);
        animalsTable.setItems(animalObservableList);
    }

    @FXML
    void addAnimalButtonAction(ActionEvent event) {
        //Animal newAnimal = new Animal();
        //loadAnimals();
        return;

    }

    @FXML
    void deleteAnimalButtonAction(ActionEvent event) {
        Animal selectedAnimal = animalsTable.getSelectionModel().getSelectedItem();
        selectedAnimal.setStatus(Status.INACTIVE);
        if (selectedAnimal != null) {
            animalDao.update(selectedAnimal);
            loadAnimals();
        } else {
            System.out.println("Žiadne zviera nebolo vybrané na odstránenie.");
        }
    }

    @FXML
    void goBackButtonAction(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}
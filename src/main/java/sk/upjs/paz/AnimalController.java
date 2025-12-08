package sk.upjs.paz;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.animal.AnimalDao;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class AnimalController {
    @FXML
    private TableView<Animal> animalsTable;

    // Pozor na typy: Animal je entita, Long/String sú typy stĺpcov
    @FXML private TableColumn<Animal, Long> idCol;
    @FXML private TableColumn<Animal, String> nameCol;
    @FXML private TableColumn<Animal, String> speciesCol;
    @FXML private Button removeButton;
    @FXML private Button addButton;
    @FXML private Button updateButton;

    @FXML
    private AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();

    @FXML
    public void addAnimal(ActionEvent event) {};

    @FXML
    public void removeAnimal(ActionEvent event){};

    @FXML
    public void updateAnimal(ActionEvent event){};

    @FXML
    public void goBack(ActionEvent event){
    }

}

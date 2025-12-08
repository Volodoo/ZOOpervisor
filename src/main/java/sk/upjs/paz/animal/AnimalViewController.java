package sk.upjs.paz.animal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AnimalViewController {

    @FXML
    private TableColumn<?, ?> EnclosureCol;

    @FXML
    private Button addAnimalButton;

    @FXML
    private TableView<?> animalsTable;

    @FXML
    private TableColumn<?, ?> birthDateCol;

    @FXML
    private Button deleteAnimalButton;

    @FXML
    private Button goBackButton;

    @FXML
    private TableColumn<?, ?> nameCol;

    @FXML
    private TableColumn<?, ?> sexCol;

    @FXML
    private TableColumn<?, ?> speciesCol;

    @FXML
    void addAnimalButtonAction(ActionEvent event) {

    }

    @FXML
    void deleteAnimalButtonAction(ActionEvent event) {

    }

    @FXML
    void goBackButtonAction(ActionEvent event) {

    }

}

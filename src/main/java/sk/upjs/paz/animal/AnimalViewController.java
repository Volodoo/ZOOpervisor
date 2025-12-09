package sk.upjs.paz.animal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import sk.upjs.paz.SceneManager;

import java.io.IOException;

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
    void goBackButtonAction(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}

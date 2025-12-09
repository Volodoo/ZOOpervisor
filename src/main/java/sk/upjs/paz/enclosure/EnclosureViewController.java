package sk.upjs.paz.enclosure;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sk.upjs.paz.SceneManager;

import java.io.IOException;

public class EnclosureViewController {

    @FXML
    private Button addEnclosureButton;

    @FXML
    private TableView<?> enclosureTable;

    @FXML
    private Button goBackButton;

    @FXML
    private TableColumn<?, ?> idCol;

    @FXML
    private TableColumn<?, ?> lastMaintainanceCol;

    @FXML
    private TableColumn<?, ?> nameCol;

    @FXML
    private HBox occupationHbox;

    @FXML
    private TableColumn<?, ?> zoneCol;

    @FXML
    void addEnclosureButtonAction(ActionEvent event) {

    }

    @FXML
    void goBackButtonAction(ActionEvent event) throws IOException {
        SceneManager.changeScene(event,"/sk.upjs.paz/MainView.fxml","Hlavne okno");
    }

}

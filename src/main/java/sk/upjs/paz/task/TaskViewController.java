package sk.upjs.paz.task;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import sk.upjs.paz.SceneManager;

public class TaskViewController {

    @FXML
    private Button addTaskButton;

    @FXML
    private Button goBackButton;

    @FXML
    private Label taskLabel;

    @FXML
    void addTaskButtonAction(ActionEvent event) {

    }

    @FXML
    void goBack(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/MainView.fxml","Hlavne okno");
    }

}

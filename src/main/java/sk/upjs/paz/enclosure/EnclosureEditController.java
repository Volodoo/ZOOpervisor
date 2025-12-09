package sk.upjs.paz.enclosure;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sk.upjs.paz.SceneManager;

public class EnclosureEditController {

    @FXML
    private Button goBackButton;

    @FXML
    private DatePicker lastMaintainanceDatePicker;

    @FXML
    private Label lastMaitainanceLabel;

    @FXML
    private TextField nameField;

    @FXML
    private Label nameLabel;

    @FXML
    private Button updateEnclosureButton;

    @FXML
    private TextField zoneField;

    @FXML
    private Label zoneLabel;

    @FXML
    void goBackButtonAction(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/EnclosureView.fxml","Hlavne okno");
    }

    @FXML
    void updateEnclosureButtonAction(ActionEvent event) {

    }

}

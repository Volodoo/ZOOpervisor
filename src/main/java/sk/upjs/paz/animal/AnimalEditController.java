package sk.upjs.paz.animal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import sk.upjs.paz.SceneManager;

public class AnimalEditController {

    @FXML
    private DatePicker birthDayPicker;

    @FXML
    private DatePicker birthDayPicker1;

    @FXML
    private Button goBackButton;

    @FXML
    private TextField nicknameField;

    @FXML
    private ComboBox<?> sexComboBox;

    @FXML
    private ComboBox<?> sexComboBox1;

    @FXML
    private TextField speciesField;

    @FXML
    private ComboBox<?> statusComboBox;

    @FXML
    private Button updateAnimalButton;

    @FXML
    void goBackButtonAction(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/AnimalView.fxml","Hlavne okno");
    }

    @FXML
    void updateAnimalButtonAction(ActionEvent event) {

    }

}
package sk.upjs.paz.enclosure;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EnclosureEditController {
    @FXML
    public Spinner<Integer> hourSpinner;

    @FXML
    public Spinner<Integer> minuteSpinner;

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

    private boolean editMode = false;

    private Enclosure enclosure;

    EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    @FXML
    void initialize() {
        // Nastavenie hodín a minút spinnerov
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        // Ak je editMode a enclosure má lastMaintainance, nastav hodnoty spinnerov
        if (editMode && enclosure != null && enclosure.getLastMaintainance() != null) {
            LocalDateTime dt = enclosure.getLastMaintainance();
            lastMaintainanceDatePicker.setValue(dt.toLocalDate());
            hourSpinner.getValueFactory().setValue(dt.getHour());
            minuteSpinner.getValueFactory().setValue(dt.getMinute());
        }
    }

    @FXML
    void goBackButtonAction(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/enclosure/EnclosureView.fxml","Zobrazenie výbehov");
    }

    @FXML
    void updateEnclosureButtonAction(ActionEvent event) {
        if(editMode) {
            enclosure.setName(nameField.getText());
            enclosure.setZone(zoneField.getText());
            if (lastMaintainanceDatePicker.getValue() != null) {
                enclosure.setLastMaintainance(
                        lastMaintainanceDatePicker.getValue()
                                .atTime(hourSpinner.getValue(), minuteSpinner.getValue())
                );
            } else {
                enclosure.setLastMaintainance(null);
            }
            this.editMode = false;
            enclosureDao.update(enclosure);
        } else {
            Enclosure enclosure = new Enclosure();
            enclosure.setName(nameField.getText());
            enclosure.setZone(zoneField.getText());
            if (lastMaintainanceDatePicker.getValue() != null) {
                enclosure.setLastMaintainance(
                        lastMaintainanceDatePicker.getValue()
                                .atTime(hourSpinner.getValue(), minuteSpinner.getValue())
                );
            } else {
                enclosure.setLastMaintainance(null);
            }
            enclosureDao.create(enclosure);
        }
        SceneManager.changeScene(event, "/sk.upjs.paz/enclosure/EnclosureView.fxml","Zobrazenie výbehov");
    }

    public void setEnclosure(Enclosure enclosure) {
        this.editMode = true;
        this.enclosure = enclosure;
        nameField.setText(enclosure.getName());
        zoneField.setText(enclosure.getZone());
        if(enclosure.getLastMaintainance() != null) {
            LocalDateTime dt = enclosure.getLastMaintainance();
            lastMaintainanceDatePicker.setValue(dt.toLocalDate());
            hourSpinner.getValueFactory().setValue(dt.getHour());
            minuteSpinner.getValueFactory().setValue(dt.getMinute());
        }
    }
}

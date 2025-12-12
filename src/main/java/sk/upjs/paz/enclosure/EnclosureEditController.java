package sk.upjs.paz.enclosure;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;

import java.time.LocalDate;

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

    private boolean editMode = false;

    private Enclosure enclosure;

    EnclosureDao enclosureDao= Factory.INSTANCE.getEnclosureDao();
    @FXML
    void goBackButtonAction(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/enclosure/EnclosureView.fxml","Zobrazenie výbehov");
    }


    @FXML
    void updateEnclosureButtonAction(ActionEvent event) {
        if(editMode) {
            boolean suhlas = SceneManager.confirm("Naozaj chcete uložiť zmeny?");
            if(!suhlas){
                return;
            }
            enclosure.setName(nameField.getText());
            enclosure.setZone(zoneField.getText());
            if (lastMaintainanceDatePicker.getValue() != null) {

                enclosure.setLastMaintainance(lastMaintainanceDatePicker.getValue().atStartOfDay());
            } else {
                enclosure.setLastMaintainance(null);
            }
            this.editMode = false;
            enclosureDao.update(enclosure);
        }
        else {
            boolean suhlas = SceneManager.confirm("Naozaj chcete pridať nový výbeh?");
            if(!suhlas){
                return;
            }
            Enclosure newEnclosure = new Enclosure();
            newEnclosure.setName(nameField.getText());
            newEnclosure.setZone(zoneField.getText());
            if (lastMaintainanceDatePicker.getValue() != null) {
                newEnclosure.setLastMaintainance(lastMaintainanceDatePicker.getValue().atStartOfDay());
            } else {
                newEnclosure.setLastMaintainance(null);
            }
            enclosureDao.create(newEnclosure);

        }
        SceneManager.changeScene(event, "/sk.upjs.paz/enclosure/EnclosureView.fxml","Zobrazenie výbehov");
    }

    public void setEnclosure(Enclosure enclosure) {
        this.editMode = true;
        this.updateEnclosureButton.setText("Uložiť");
        this.enclosure = enclosure;
        nameField.setText(enclosure.getName());
        zoneField.setText(enclosure.getZone());
        if(enclosure.getLastMaintainance() != null) {
            lastMaintainanceDatePicker.setValue(LocalDate.from(enclosure.getLastMaintainance()));
        }
    }
}

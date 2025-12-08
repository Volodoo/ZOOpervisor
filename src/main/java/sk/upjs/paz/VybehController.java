package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import sk.upjs.paz.animal.AnimalDao;
import sk.upjs.paz.enclosure.EnclosureDao;

public class VybehController {
    @FXML
    private EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    @FXML
    public void addEnclosure(ActionEvent event) {};

    @FXML
    public void removeEnclosure(ActionEvent event){};

    @FXML
    public void updateEnclosure(ActionEvent event){};

    @FXML
    public void goBack(ActionEvent event){
    }
}

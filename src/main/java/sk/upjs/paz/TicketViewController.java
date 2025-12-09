package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class TicketViewController {

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }


    @FXML
    public void addTicket(ActionEvent event) {}

}

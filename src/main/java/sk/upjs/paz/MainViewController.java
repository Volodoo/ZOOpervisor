package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {


    @FXML
    public void displayAnimals(ActionEvent event) throws IOException {
        SceneManager.changeScene(event,"/sk.upjs.paz/AnimalView.fxml", "Zobrazenie zvierat");
    }
    @FXML
    public void displayEnclosures(ActionEvent event) throws IOException {
        SceneManager.changeScene(event,"/sk.upjs.paz/EnclosureView.fxml", "Zobrazenie výbehov");
    }
    @FXML
    public void displayTasks(ActionEvent event) throws IOException {
        SceneManager.changeScene(event,"/sk.upjs.paz/TaskView.fxml", "Zobrazenie úloh");
    }

    @FXML
    public void displayUsers(ActionEvent event) throws IOException {
        SceneManager.changeScene(event,"/sk.upjs.paz/UserView.fxml", "Zobrazenie používateľov");

    }
    @FXML
    public void displayTickets(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/TicketView.fxml", "Zobrazenie lístkov");
    }

    @FXML
    public void signOut(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/LoginView.fxml", "Login");
    }

    @FXML
    public void displayTicketSell(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/SellTicketView.fxml", "Predaj Listka");
    }

    private void changeScene(ActionEvent event,String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
    }


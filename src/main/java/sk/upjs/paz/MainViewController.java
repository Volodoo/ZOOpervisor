package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import sk.upjs.paz.security.Auth;
import sk.upjs.paz.user.Role;

import java.io.IOException;
import java.util.Objects;

public class MainViewController {


    public Button tasksButton;
    public Button userButton;
    public Button ticketButton;
    public Button sellTicketButton;
    public Label userNameLabel;
    public Label roleLabel;
    public Button animalsButton;
    public Button enclosuresButton;
    public ToggleButton themeToggle;

    @FXML
    void initialize() {

        if (themeToggle != null) {
            themeToggle.setSelected(SceneManager.isDarkMode());
            themeToggle.setText(SceneManager.isDarkMode() ? "Svetlý režim" : "Tmavý režim");
        }

        var principal = Auth.INSTANCE.getPrincipal();

        // VYTVOR SI ADMINA A TU SI PO PRIHLASINU JAK ADMIN MOZES ZMINITY ROLU A UVIDIS JAK TO VYZERAT
        //principal.setRole(Role.ADMIN);

        if (principal == null) {
            userNameLabel.setText("Neprihlásený");
            roleLabel.setText("-");
            return;
        }
        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        if (principal == null || principal.getRole() == Role.INACTIVE || principal.getRole() == Role.CASHIER) {
            tasksButton.setDisable(true);
        }
        if (principal == null || principal.getRole() == Role.INACTIVE || principal.getRole() == Role.MAINTAINER || principal.getRole() == Role.ZOOKEEPER) {
            sellTicketButton.setDisable(true);
        }
        if (principal == null || principal.getRole() == Role.INACTIVE || principal.getRole() != Role.ADMIN) {
            userButton.setDisable(true);
        }
        if (principal == null || principal.getRole() == Role.INACTIVE || principal.getRole() != Role.ADMIN) {
            ticketButton.setDisable(true);
        }
        if (principal == null || principal.getRole() == Role.INACTIVE) {
            animalsButton.setDisable(true);
            enclosuresButton.setDisable(true);
        }
    }

    @FXML
    public void displayAnimals(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/animal/AnimalView.fxml", "Zobrazenie zvierat");
    }

    @FXML
    public void displayEnclosures(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/enclosure/EnclosureView.fxml", "Zobrazenie výbehov");
    }

    @FXML
    public void displayTasks(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/task/TaskView.fxml", "Zobrazenie úloh");
    }

    @FXML
    public void displayUsers(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/user/UserView.fxml", "Zobrazenie používateľov");

    }

    @FXML
    public void displayTickets(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/ticket/TicketView.fxml", "Zobrazenie lístkov");
    }

    @FXML
    public void signOut(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/security/LoginView.fxml", "Login");
    }

    @FXML
    public void displayTicketSell(ActionEvent event) {
        SceneManager.changeScene( event,"/sk.upjs.paz/ticket/SellTicketView.fxml", "Predaj Lístkov");
    }

    @FXML
    void switchLanguage(ActionEvent event) {
        SceneManager.toggleLanguage();
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "ZOOpervisor");
    }

    @FXML
    void switchTheme(javafx.event.ActionEvent event) {
        SceneManager.toggleTheme();

        themeToggle.setText(SceneManager.isDarkMode() ? "Svetlý režim" : "Tmavý režim");

        if (themeToggle.getScene() != null) {
            SceneManager.applyTheme(themeToggle.getScene());
        }
    }
}


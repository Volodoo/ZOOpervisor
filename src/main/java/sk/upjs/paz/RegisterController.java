package sk.upjs.paz;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.upjs.paz.user.Gender;

import java.io.IOException;


public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private ComboBox<Gender> genderField;

    public void cancelRegistration(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Prihlásenie");
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    public void initialize() {
        genderField.getItems().setAll(Gender.values());
    }
    public void signUserIn(ActionEvent event) {
    }


}




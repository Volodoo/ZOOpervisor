package sk.upjs.paz.security;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.upjs.paz.Factory;
import sk.upjs.paz.exceptions.AuthenticationException;
import java.io.IOException;

public class LoginViewController {

    private final AuthDao authDao = Factory.INSTANCE.getAuthDao();

    @FXML
    private Button logInButton;

    @FXML
    private Button signInButton;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    void logIn(ActionEvent event) {
        var email = emailField.getText();
        var password = passwordField.getText();

        Principal principal;

        try {
            principal = authDao.authenticate(email, password);
        } catch (AuthenticationException e) {
            return;
        }
        Auth.INSTANCE.setPrincipal(principal);
        openMainScene();
    }

    private void openMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/MainView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("ZOOpervisor - Hlavné okno");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void signIn(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/Register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrácia nového usera");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
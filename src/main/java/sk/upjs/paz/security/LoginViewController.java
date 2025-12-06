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
    private Label incorrectPasswordLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button signInButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    void login(ActionEvent event) {
        var email = emailTextField.getText();
        var password = passwordTextField.getText();

        Principal principal;

        try {
            principal = authDao.authenticate(email, password);
        } catch (AuthenticationException e) {
            incorrectPasswordLabel.setText(e.getMessage());
            return;
        }
        Auth.INSTANCE.setPrincipal(principal);
        incorrectPasswordLabel.getScene().getWindow().hide();
        openMainScene();

        /*
        // 1. Základná kontrola, či niečo zadal
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorLabel.setText("Vyplňte meno a heslo!");
            return;
        }


        User user = userDao.verify(email, password);

        if (user != null) {
            // 3. Ak sme našli usera -> Prepneme scénu
            System.out.println("Úspešné prihlásenie: " + user.getFirstName());
            openMainScene();
        }

         */
    }

    private void openMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/View.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailTextField.getScene().getWindow();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrácia nového usera");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.storage.Factory;
import sk.upjs.paz.storage.UserDao;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    // Načítame si prístup k databáze
    private UserDao userDao = Factory.INSTANCE.getUserDao();

    @FXML
    void login(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

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
    }

    private void openMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/View.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
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
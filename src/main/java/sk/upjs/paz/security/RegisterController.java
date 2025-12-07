package sk.upjs.paz.security;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.paz.Factory;
import sk.upjs.paz.user.Gender;
import sk.upjs.paz.user.Role;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

import java.io.IOException;
import java.time.LocalDate;

public class RegisterController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ComboBox<Gender> genderField;
    @FXML
    private DatePicker birthDayField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordAgainField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button confirmSigningInButton;
    @FXML
    private Button chancelRegistrationField;

    private UserDao userDao;

    public RegisterController() {
        this.userDao = Factory.INSTANCE.getUserDao();
    }

    @FXML
    public void signUserIn(ActionEvent event) {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        Gender gender = genderField.getValue();
        LocalDate birthDay = birthDayField.getValue();
        String email = emailField.getText();
        String password = passwordField.getText();
        String passwordAgain = passwordAgainField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || gender == null || birthDay == null || email.isEmpty() || password.isEmpty() || passwordAgain.isEmpty()) {
            statusLabel.setText("Všetky polia musia byť vyplnené!");
            return;
        }

        if (!password.equals(passwordAgain)) {
            statusLabel.setText("Heslá sa nezhodujú!");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setGender(gender);
        newUser.setBirthDay(birthDay);
        newUser.setRole(Role.INACTIVE);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        try {
            User createdUser = userDao.create(newUser);
            statusLabel.setText("Registrácia bola úspešná!");
            openMainScene();
        } catch (Exception e) {
            statusLabel.setText("Chyba pri registrácii: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/View.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("ZOOpervisor - Hlavné okno");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancelRegistration(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) chancelRegistrationField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Prihlásenie");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        genderField.getItems().setAll(Gender.values());
    }
}
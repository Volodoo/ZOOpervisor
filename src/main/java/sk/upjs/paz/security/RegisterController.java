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
            Alert alert= new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Upozornenie");
            alert.setHeaderText(null);
            alert.setContentText("Všetky polia musia byť vyplnené!");
            alert.showAndWait();
            return;
        }


        if(passwordAgain.length()<8) {
                Alert alert= new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Upozornenie");
                alert.setHeaderText(null);
                alert.setContentText("Heslo musí byť dĺžky 8 znakov a obsahovať aspon jednu číslicu a jeden špeciálny znak!");
                alert.showAndWait();
                return;
        }
        else if(!password.matches("\\d+") && !passwordAgain.matches(".*[^a-zA-Z0-9].*")) {
            Alert alert= new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Upozornenie");
            alert.setHeaderText(null);
            alert.setContentText("Heslo musí obsahovať aspon jeden špeciálny znak a jednu číslicu!");
            alert.showAndWait();
            return;
        }

        if (!password.equals(passwordAgain)) {
            Alert alert= new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Hesla sa nezhodujú!");
            alert.showAndWait();
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
            Alert alert= new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uspech");
            alert.setHeaderText(null);
            alert.setContentText("Registrácia bola úspešná");
            alert.showAndWait();
            openMainScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMainScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/MainView.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk.upjs.paz/security/LoginView.fxml"));
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
package sk.upjs.paz.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;


public class UserEditController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField lastNameField;
    @FXML
    private ComboBox<Gender> genderComboBox;
    @FXML
    private ComboBox<Role> roleComboBox;
    @FXML
    private DatePicker birthDatePicker;

    UserDao userDao = Factory.INSTANCE.getUserDao();


    private User user;
    private boolean editMode = false;

    @FXML
    void initialize() {
        genderComboBox.getItems().setAll(Gender.values());
        roleComboBox.getItems().setAll(Role.values());
    }

    @FXML
    public void saveUser(ActionEvent event) {
        if (editMode) {
            boolean suhlas = SceneManager.confirm("Naozaj chcete uložiť zmeny?");
            if (!suhlas) {
                return;
            }
            user.setFirstName(firstNameField.getText());
            if (lastNameField.getText() != null && !lastNameField.getText().equals("")) {
                user.setLastName(lastNameField.getText());
            }
            if (emailField.getText() != null && !emailField.getText().equals("")) {
                user.setEmail(emailField.getText());
            }
            user.setBirthDay(birthDatePicker.getValue());
            user.setRole(roleComboBox.getValue());
            user.setGender(genderComboBox.getValue());
            this.editMode = false;
            userDao.update(user);
            SceneManager.changeScene(event, "/sk.upjs.paz/user/UserView.fxml", "Zobrazenie userov");
        }

    }

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/user/UserView.fxml", "Zobrazenie userov");
    }

    public void setUser(User user) {
        this.editMode = true;
        this.user = user;

        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        birthDatePicker.setValue(user.getBirthDay());
        roleComboBox.setValue(user.getRole());
        genderComboBox.setValue(user.getGender());
    }

}

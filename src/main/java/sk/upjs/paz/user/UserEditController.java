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
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField surnameField;
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
            user.setFirstName(nameField.getText());
            user.setLastName(nameField.getText());
            user.setEmail(emailField.getText());
            user.setBirthDay(birthDatePicker.getValue());
            user.setRole(roleComboBox.getValue());
            user.setGender(genderComboBox.getValue());
            this.editMode = false;
            userDao.update(user);
            SceneManager.changeScene(event, "/sk.upjs.paz/user/UserView.fxml", "Zobrazenie userov");
        } else {
            User user = new User();
            user.setFirstName(nameField.getText());
            user.setLastName(nameField.getText());
            user.setEmail(emailField.getText());
            user.setBirthDay(birthDatePicker.getValue());
            user.setRole(roleComboBox.getValue());
            user.setGender(genderComboBox.getValue());
            userDao.create(user);
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

        nameField.setText(user.getFirstName());
        surnameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        birthDatePicker.setValue(user.getBirthDay());
        roleComboBox.setValue(user.getRole());
        genderComboBox.setValue(user.getGender());
    }

}

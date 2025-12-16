package sk.upjs.paz.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.security.Auth;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserViewController {

    @FXML
    public TableColumn<User, String> firstNameCol;

    @FXML
    public TableColumn<User, String> lastNameCol;

    @FXML
    public TableColumn<User, String> genderCol;

    @FXML
    public TableColumn<User, String> birthdayCol;

    @FXML
    public TableColumn<User, String> emailCol;

    @FXML
    public TableColumn<User, String> roleCol;

    @FXML
    public TableView<User> userTable;

    @FXML
    public ComboBox<String> roleFilterComboBox;
    @FXML
    public Label userNameLabel;
    @FXML
    public Label roleLabel;

    private UserDao userDao = Factory.INSTANCE.getUserDao();

    @FXML
    public void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();

        if (principal == null || principal.getRole() == Role.ADMIN) {
            SceneManager.setupDoubleClick(userTable, "/sk.upjs.paz/user/UserEdit.fxml", "Upraviť používateľa", UserEditController::setUser);
        }

        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        roleFilterComboBox.getItems().add("filter.all");
        roleFilterComboBox.getItems().add("filter.admin");
        roleFilterComboBox.getItems().add("filter.manager");
        roleFilterComboBox.getItems().add("filter.zookeeper");
        roleFilterComboBox.getItems().add("filter.maintainer");
        roleFilterComboBox.getItems().add("filter.cashier");
        roleFilterComboBox.getItems().add("filter.inactive");
        roleFilterComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String key) {
                if (key == null) return "";
                try {
                    return SceneManager.getBundle().getString(key);
                } catch (Exception e) {
                    return key;
                }
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });
        roleFilterComboBox.getSelectionModel().select("filter.all");

        roleFilterComboBox.setOnAction(event -> filterUsersByRole());

        firstNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        lastNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        genderCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender().toString()));
        birthdayCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return new SimpleStringProperty(cellData.getValue().getBirthDay().format(formatter));
        });
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        roleCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().toString()));

        loadUsers();
    }

    private void filterUsersByRole() {
        String selectedRole = roleFilterComboBox.getSelectionModel().getSelectedItem();

        if (selectedRole != null) {
            if (selectedRole.equals("filter.all")) {
                loadUsers();
            } else {
                Role role = convertStringToRole(selectedRole);
                List<User> users = userDao.getByRole(role);
                ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
                userTable.setItems(userObservableList);
            }
        }
    }
    @FXML
    public void OnActionSearchTextField(){}
    private Role convertStringToRole(String roleString) {
        switch (roleString) {
            case "filter.admin":
                return Role.ADMIN;
            case "filter.manager":
                return Role.MANAGER;
            case "filter.zookeeper":
                return Role.ZOOKEEPER;
            case "filter.maintainer":
                return Role.MAINTAINER;
            case "filter.cashier":
                return Role.CASHIER;
            case "filter.inactive":
                return Role.INACTIVE;
            default:
                throw new IllegalArgumentException("Neznáma rola: " + roleString);
        }
    }

    private void loadUsers() {
        String selectedRole = roleFilterComboBox.getSelectionModel().getSelectedItem();

        List<User> users;
        if (selectedRole == null || selectedRole.equals("filter.all")) {
            users = userDao.getAll();
        } else {
            Role role = convertStringToRole(selectedRole);
            users = userDao.getByRole(role);
        }

        ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
        userTable.setItems(userObservableList);
    }

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno"); // "Main Window"
    }
}
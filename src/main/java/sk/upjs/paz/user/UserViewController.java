package sk.upjs.paz.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;

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

    private UserDao userDao = Factory.INSTANCE.getUserDao();

    @FXML
    public void initialize() {
        roleFilterComboBox.getItems().add("Všetky");
        roleFilterComboBox.getItems().add("Admin");
        roleFilterComboBox.getItems().add("Manažér");
        roleFilterComboBox.getItems().add("Ošetrovateľ");
        roleFilterComboBox.getItems().add("Údržbár");
        roleFilterComboBox.getItems().add("Predavač");
        roleFilterComboBox.getItems().add("Neaktívny");



        roleFilterComboBox.getSelectionModel().select("Všetky");

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

        SceneManager.setupDoubleClick(
                userTable,
                "/sk.upjs.paz/user/UserEdit.fxml",
                "Upraviť usera", // "Edit User"
                (UserEditController ctrl, User user) -> ctrl.setUser(user)
        );

        loadUsers();
    }

    private void filterUsersByRole() {
        String selectedRole = roleFilterComboBox.getSelectionModel().getSelectedItem();

        if (selectedRole != null) {
            if (selectedRole.equals("Všetky")) {
                loadUsers();
            } else {
                Role role = convertStringToRole(selectedRole);
                List<User> users = userDao.getByRole(role);
                ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
                userTable.setItems(userObservableList);
            }
        }
    }

    private Role convertStringToRole(String roleString) {
        switch (roleString) {
            case "Admin":
                return Role.ADMIN;
            case "Manažér":
                return Role.MANAGER;
            case "Ošetrovateľ":
                return Role.ZOOKEEPER;
            case "Údržbár":
                return Role.MAINTAINER;
            case "Predavač":
                return Role.CASHIER;
            case "Neaktívny":
                return Role.INACTIVE;
            default:
                throw new IllegalArgumentException("Neznáma rola: " + roleString);
        }
    }

    private void loadUsers() {
        String selectedRole = roleFilterComboBox.getSelectionModel().getSelectedItem();

        List<User> users;
        if (selectedRole == null || selectedRole.equals("Všetky")) {
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
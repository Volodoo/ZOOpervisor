package sk.upjs.paz.user;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    public TextField searchTextField;

    @FXML
    public Label userNameLabel;
    @FXML
    public Label roleLabel;

    private final UserDao userDao = Factory.INSTANCE.getUserDao();

    private ObservableList<User> masterData = FXCollections.observableArrayList();

    private FilteredList<User> filteredData;

    @FXML
    public void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();

        if (principal == null || principal.getRole() == Role.ADMIN) {
            SceneManager.setupDoubleClick(
                    userTable,
                    "/sk.upjs.paz/user/UserEdit.fxml",
                    "Upraviť používateľa",
                    UserEditController::setUser
            );
        }

        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        roleFilterComboBox.getItems().addAll(
                "filter.all",
                "filter.admin",
                "filter.manager",
                "filter.zookeeper",
                "filter.maintainer",
                "filter.cashier",
                "filter.inactive"
        );

        roleFilterComboBox.setConverter(new StringConverter<>() {
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
        roleFilterComboBox.setOnAction(event -> loadUsers());

        firstNameCol.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getFirstName()));

        lastNameCol.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getLastName()));

        genderCol.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getGender().toString()));

        birthdayCol.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getBirthDay().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));

        emailCol.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getEmail()));

        roleCol.setCellValueFactory(celldata ->
                new SimpleStringProperty(celldata.getValue().getRole().toString()));

        filteredData = new FilteredList<>(masterData, p -> true);
        userTable.setItems(filteredData);

        searchTextField.textProperty().addListener((obs, oldText, newText) -> applyFilters());

        loadUsers();
    }

    private void applyFilters() {
        String searchText = searchTextField.getText();
        String selectedRole = roleFilterComboBox.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(user -> {

            if (selectedRole != null && !selectedRole.equals("filter.all")) {
                Role role = convertStringToRole(selectedRole);
                if (user.getRole() != role) {
                    return false;
                }
            }

            if (searchText == null || searchText.isBlank()) {
                return true;
            }

            String filter = searchText.toLowerCase();

            return user.getFirstName().toLowerCase().contains(filter)
                    || user.getLastName().toLowerCase().contains(filter)
                    || user.getGender().toString().toLowerCase().contains(filter)
                    || user.getBirthDay().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).contains(filter)
                    || user.getEmail().toLowerCase().contains(filter)
                    || user.getRole().toString().toLowerCase().contains(filter);
        });
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

        masterData.setAll(users);
        applyFilters();
    }

    private Role convertStringToRole(String roleString) {
        return switch (roleString) {
            case "filter.admin" -> Role.ADMIN;
            case "filter.manager" -> Role.MANAGER;
            case "filter.zookeeper" -> Role.ZOOKEEPER;
            case "filter.maintainer" -> Role.MAINTAINER;
            case "filter.cashier" -> Role.CASHIER;
            case "filter.inactive" -> Role.INACTIVE;
            default -> throw new IllegalArgumentException("Neznáma rola: " + roleString);
        };
    }

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}

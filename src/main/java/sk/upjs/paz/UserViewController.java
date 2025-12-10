package sk.upjs.paz;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

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
    public TableView userTable;

    private UserDao userDao = Factory.INSTANCE.getUserDao();

    @FXML
    public void initialize() {
        firstNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFirstName())
        );

        lastNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLastName())
        );

        genderCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGender().toString())
        );

        birthdayCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return new SimpleStringProperty(cellData.getValue().getBirthDay().format(formatter));
        });

        emailCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEmail())
        );

        roleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getRole().toString())
        );

        loadUsers();
    }

    private void loadUsers() {
        List<User> users = userDao.getAll();

        ObservableList<User> userObservableList = FXCollections.observableArrayList(users);

        userTable.setItems(userObservableList);
    }

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }

    @FXML
    public void addUser(ActionEvent event) {

    }
}
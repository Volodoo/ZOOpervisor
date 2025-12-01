package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.storage.DaoFactory;
import sk.upjs.paz.storage.UserDao;

import java.util.List;

public class ViewController {

    private UserDao userDao = DaoFactory.INSTANCE.getUserDao();

    @FXML
    private ListView<User> usersListView;

    @FXML
    private Button loadUsersButton;


    public void loadUsersButtonAction(ActionEvent actionEvent) {
        usersListView.getItems().clear();
        List<User> users = userDao.getAll();
        System.out.println(users);
        usersListView.getItems().addAll(users);
    }


}
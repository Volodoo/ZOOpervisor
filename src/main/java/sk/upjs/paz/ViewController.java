package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import sk.upjs.paz.entity.Animal;
import sk.upjs.paz.entity.Enclosure;
import sk.upjs.paz.entity.Task;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.storage.*;

import java.util.List;

public class ViewController {

    private UserDao userDao = (UserDao) Factory.INSTANCE.getUserDao();
    private AnimalDao animalDao = (AnimalDao) Factory.INSTANCE.getAnimalDao();
    private EnclosureDao enclosureDao = (EnclosureDao) Factory.INSTANCE.getEnclosureDao();
    private TaskDao taskDao = (TaskDao) Factory.INSTANCE.getTaskDao();

    @FXML
    private ListView<Object> itemsListView;

    public void loadUsersButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        List<User> users = userDao.getAll();
        itemsListView.getItems().addAll(users);
    }

    public void loadAnimalsButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        List<Animal> animals = animalDao.getAll();
        itemsListView.getItems().addAll(animals);
    }

    public void loadEnclosuresButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        List<Enclosure> enclosures = enclosureDao.getAll();
        itemsListView.getItems().addAll(enclosures);
    }

    public void loadTasksButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        List<Task> tasks = taskDao.getAll();
        itemsListView.getItems().addAll(tasks);
    }

    public void loadTicketsButtonAction(ActionEvent actionEvent) {

    }
}

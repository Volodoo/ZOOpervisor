package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.animal.AnimalDao;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.enclosure.EnclosureDao;
import sk.upjs.paz.task.Task;
import sk.upjs.paz.task.TaskDao;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

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
        System.out.println("*****");
        System.out.println(users);
        System.out.println("*****");

        System.out.println(userDao.getById(-1L));
        for (long id = 1L; id <= (long) users.size() - 1; id++) {
            System.out.println(userDao.getById(id));
        }

        System.out.println("*****");
        itemsListView.getItems().addAll(users);
    }

    public void loadAnimalsButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        List<Animal> animals = animalDao.getAll();
        System.out.println("*****");
        System.out.println(animals);
        System.out.println("*****");

        for (long id = 1L; id <= (long) animals.size(); id++) {
            System.out.println(animalDao.getById(id));
        }

        System.out.println("*****");
        itemsListView.getItems().addAll(animals);
    }

    public void loadEnclosuresButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        List<Enclosure> enclosures = enclosureDao.getAll();
        System.out.println("*****");
        System.out.println(enclosures);
        System.out.println("*****");

        for (long id = 1L; id <= (long) enclosures.size(); id++) {
            System.out.println(enclosureDao.getById(id));
            System.out.println(enclosureDao.getAnimals(id));
            System.out.println("Pocet zvierat vo vybehu: " + enclosureDao.getAnimalsCount(id));
            System.out.println("*****");
        }

        System.out.println("*****");
        itemsListView.getItems().addAll(enclosures);
    }

    public void loadTasksButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        List<Task> tasks = taskDao.getAll();
        System.out.println("*****");
        System.out.println(tasks);
        System.out.println("*****");

        for (long id = 1L; id <= (long) tasks.size(); id++) {
            System.out.println(taskDao.getById(id));
        }

        System.out.println("*****");
        itemsListView.getItems().addAll(tasks);

    }

    public void loadTicketsButtonAction(ActionEvent actionEvent) {

    }
}

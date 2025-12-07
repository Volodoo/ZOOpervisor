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
import sk.upjs.paz.ticket.Ticket;
import sk.upjs.paz.ticket.TicketDao;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

import java.util.List;

public class ViewController {

    private UserDao userDao = (UserDao) Factory.INSTANCE.getUserDao();
    private AnimalDao animalDao = (AnimalDao) Factory.INSTANCE.getAnimalDao();
    private EnclosureDao enclosureDao = (EnclosureDao) Factory.INSTANCE.getEnclosureDao();
    private TaskDao taskDao = (TaskDao) Factory.INSTANCE.getTaskDao();
    private TicketDao ticketDao =(TicketDao) Factory.INSTANCE.getTicketDao();

    List<User> users = userDao.getAll();

    List<Animal> animals = animalDao.getAll();
    List<Animal> animalsSorted = animalDao.getAllSortedBySpecies();

    List<Enclosure> enclosures = enclosureDao.getAll();
    List<Enclosure> enclosuresSorted = enclosureDao.getAllSortedByZone();

    List<Task> tasks = taskDao.getAll();
    List<Task> tasksSorted = taskDao.getAllSortedByDeadline();

    List<Ticket> tickets = ticketDao.getAll();
    List<Ticket> ticketsSortedByCashier = ticketDao.getAllSortedByCashier();
    List<Ticket> ticketsSortedByPurchaseTimestamp= ticketDao.getAllSortedByPurchaseDateTime();

    @FXML
    private ListView<Object> itemsListView;

    public void loadUsersButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        /*
        System.out.println("-----");
        System.out.println(users);
        System.out.println("-----");

        System.out.println(userDao.getById(-1L));
        for (long id = 1L; id <= (long) users.size() - 1; id++) {
            System.out.println(userDao.getById(id));
        }

        System.out.println("-----");
         */
        itemsListView.getItems().addAll(users);
    }

    public void loadAnimalsButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        /*
        System.out.println("-----");
        System.out.println("getAll(): ");
        System.out.println(animals);
        System.out.println("-----");
        System.out.println("getAllSortedBySpecies(): ");
        System.out.println(animalsSorted);
        System.out.println("-----");
        for (long id = 1L; id <= 8L; id++) {
            System.out.println(animalDao.getById(id));
        }
        System.out.println("-----");

         */
        itemsListView.getItems().addAll(animalsSorted);
    }

    public void loadEnclosuresButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        /*
        System.out.println("-----");
        System.out.println("getAll(): ");
        System.out.println(enclosures);
        System.out.println("-----");
        System.out.println("getAllSortedByZone(): ");
        System.out.println(enclosuresSorted);
        System.out.println("-----");

        for (long id = 1L; id <= (long) enclosures.size(); id++) {
            System.out.println(enclosureDao.getById(id));
            System.out.println("getAnimals(): ");
            System.out.println(enclosureDao.getAnimals(id));
            System.out.println("getAnimalsCount(): " + enclosureDao.getAnimalsCount(id));
            System.out.println("-----");
        }

        System.out.println("-----");

         */
        itemsListView.getItems().addAll(enclosuresSorted);
    }

    public void loadTasksButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        /*
        System.out.println("-----");
        System.out.println("getAll(): ");
        System.out.println(tasks);
        System.out.println("-----");
        System.out.println("getAllSortedByDeadline(): ");
        System.out.println(tasksSorted);
        System.out.println("-----");

        for (long id = 1L; id <= (long) tasks.size(); id++) {
            System.out.println(taskDao.getById(id));
        }
        System.out.println("-----");

         */
        itemsListView.getItems().addAll(tasksSorted);

    }

    public void loadTicketsButtonAction(ActionEvent actionEvent) {
        itemsListView.getItems().clear();
        itemsListView.getItems().addAll(tickets);

    }
}

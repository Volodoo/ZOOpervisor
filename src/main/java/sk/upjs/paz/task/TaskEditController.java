package sk.upjs.paz.task;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.animal.AnimalDao;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.enclosure.EnclosureDao;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class TaskEditController {

    @FXML
    private VBox animalsVBox;

    @FXML
    private DatePicker deadlineDatePicker;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private VBox enclosuresVBox;

    @FXML
    private Button goBackButton;

    @FXML
    private TextField nameField;

    @FXML
    private Button saveTaskButon;

    @FXML
    private ComboBox<Status> statusComboBox;

    @FXML
    private ComboBox<User> userComboBox;

    @FXML
    private VBox studentsVBox;

    @FXML
    private VBox studentsVBox1;


    private Task task;
    private boolean editMode = false;

    TaskDao taskDao = Factory.INSTANCE.getTaskDao();
    UserDao userDao = Factory.INSTANCE.getUserDao();
    AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();
    EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    List<Animal> animals = animalDao.getAllSortedBySpecies();
    List<Enclosure> enclosures = enclosureDao.getAllSortedByZone();

    @FXML
    void initialize() {
        statusComboBox.getItems().setAll(Status.values());
        userComboBox.getItems().setAll(userDao.getAll());

    }

    @FXML
    void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/TaskView.fxml", "Zobrazenie úloh");
    }

    @FXML
    void saveTaskButtonAction(ActionEvent event) {
        Task task = null;
        if (editMode) {
            task.setName(nameField.getText());
            task.setDescription(descriptionTextArea.getText());
            task.setStatus(statusComboBox.getValue());
            task.setUser(userComboBox.getValue());
            task.setDeadline(deadlineDatePicker.getValue().atStartOfDay());
            taskDao.update(task);
            editMode = false;
        } else {
            task = new Task();
            task.setName(nameField.getText());
            task.setDescription(descriptionTextArea.getText());
            task.setStatus(statusComboBox.getValue());
            task.setUser(userComboBox.getValue());
            task.setDeadline(deadlineDatePicker.getValue().atStartOfDay());
            taskDao.create(task);
        }
        SceneManager.changeScene(event, "/sk.upjs.paz/TaskView.fxml", "Zobrazenie úloh");
    }

    public void setTasks(Task task) {
        this.editMode = true;
        this.task = task;
        nameField.setText(task.getName());
        descriptionTextArea.setText(task.getDescription());
        statusComboBox.setValue(task.getStatus());
        userComboBox.setValue(task.getUser());
        deadlineDatePicker.setValue(LocalDate.from(task.getDeadline()));
    }

}
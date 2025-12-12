package sk.upjs.paz.task;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.animal.AnimalDao;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.enclosure.EnclosureDao;
import sk.upjs.paz.user.Role;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TextField nameField;

    @FXML
    private ComboBox<Status> statusComboBox;

    @FXML
    private ComboBox<User> userComboBox;

    private Task task;
    private boolean editMode = false;

    TaskDao taskDao = Factory.INSTANCE.getTaskDao();
    UserDao userDao = Factory.INSTANCE.getUserDao();
    AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();
    EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    private Map<Animal, CheckBox> animalCheckBoxes = new HashMap<>();
    private Map<Enclosure, CheckBox> enclosureCheckBoxes = new HashMap<>();

    @FXML
    void initialize() {
        statusComboBox.getItems().setAll(Status.values());


        List<User> users = userDao.getAll();

        for (User user : users) {
            if (user.getRole() == Role.MANAGER || user.getRole() == Role.ADMIN || user.getRole() == Role.CASHIER || user.getRole() == Role.INACTIVE) {
                continue;
            }
            userComboBox.getItems().add(user);
        }

        if(!editMode){
            loadAnimals();
            loadEnclosures();
        }
    }

    private void loadAnimals() {
        animalsVBox.getChildren().clear();
        animalCheckBoxes.clear();
        List<Animal> animals = animalDao.getAllSortedBySpecies();
        for (Animal a : animals) {
            CheckBox cb = new CheckBox(a.getNickname());
            if (editMode && task != null && task.getAnimals().contains(a)) {
                cb.setSelected(true);
            }
            animalCheckBoxes.put(a, cb);
            animalsVBox.getChildren().add(cb);
        }
    }

    private void loadEnclosures() {
        enclosuresVBox.getChildren().clear();
        enclosureCheckBoxes.clear();
        List<Enclosure> enclosures = enclosureDao.getAllSortedByZone();
        for (Enclosure e : enclosures) {
            CheckBox cb = new CheckBox(e.getName());
            if (editMode && task != null && task.getEnclosures().contains(e)) {
                cb.setSelected(true);
            }
            enclosureCheckBoxes.put(e, cb);
            enclosuresVBox.getChildren().add(cb);
        }
    }

    public void setTask(Task task) {
        this.editMode = true;
        this.task = task;

        loadAnimals();
        loadEnclosures();

        nameField.setText(task.getName());
        descriptionTextArea.setText(task.getDescription());
        statusComboBox.setValue(task.getStatus());
        userComboBox.setValue(task.getUser());
        deadlineDatePicker.setValue(task.getDeadline().toLocalDate());
    }


    @FXML
    void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/task/TaskView.fxml", "Zobrazenie úloh");
    }

    @FXML
    void saveTaskButtonAction(ActionEvent event) {
        Task taskToSave = editMode ? task : new Task();


        taskToSave.setName(nameField.getText());
        taskToSave.setDescription(descriptionTextArea.getText());
        taskToSave.setStatus(statusComboBox.getValue());
        taskToSave.setUser(userComboBox.getValue());
        taskToSave.setDeadline(deadlineDatePicker.getValue().atStartOfDay());

        taskToSave.setAnimals(animalCheckBoxes.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet())
        );

        taskToSave.setEnclosures(enclosureCheckBoxes.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet())
        );

        if (editMode) {
            taskDao.update(taskToSave);
            editMode = false;
        } else {
            taskDao.create(taskToSave);
        }

        SceneManager.changeScene(event, "/sk.upjs.paz/task/TaskView.fxml", "Zobrazenie úloh");
    }



}
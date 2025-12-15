package sk.upjs.paz.task;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.animal.AnimalDao;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.enclosure.EnclosureDao;
import sk.upjs.paz.security.Auth;
import sk.upjs.paz.user.Role;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskViewController {

    @FXML
    public TableView<Task> taskTable;
    public TableColumn<Task, String> nameCol;
    public TableColumn<Task, String> descriptionCol;
    public TableColumn<Task, String> deadlineCol;
    public TableColumn<Task, String> statusCol;
    public TableColumn<Task, String> userCol;
    public TableColumn<Task, String> animalsCol;
    public TableColumn<Task, String> enclosuresCol;
    public ComboBox<String> userFilterComboBox;
    public Label userNameLabel;
    public Label roleLabel;
    public Button deleteTaskButton;

    @FXML
    private Button addTaskButton;

    @FXML
    private Button goBackButton;

    private TaskDao taskDao = Factory.INSTANCE.getTaskDao();
    private UserDao userDao = Factory.INSTANCE.getUserDao();
    private AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();
    private EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    @FXML
    void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();

        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        if (principal == null || (principal.getRole() != Role.ADMIN && principal.getRole() != Role.MANAGER)) {
            addTaskButton.setDisable(true);
            userFilterComboBox.setDisable(true);
            deleteTaskButton.setDisable(true);
            taskTable.setOnMouseClicked(Event::consume);
        } else {
            SceneManager.setupDoubleClick(taskTable, "/sk.upjs.paz/task/TaskEdit.fxml", "Upraviť výbeh", TaskEditController::setTask);
        }

        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        deadlineCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return new SimpleStringProperty(cellData.getValue().getDeadline().format(formatter));
        });
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        userCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getFirstName() + " " + cellData.getValue().getUser().getLastName() + " (" + cellData.getValue().getUser().getRole() + ")"));

        animalsCol.setCellValueFactory(cellData -> {
            Set<Animal> animals = cellData.getValue().getAnimals();
            String animalsString = animals.stream()
                    .map(animal -> animal.getNickname() + " (" + animal.getId() + ")")
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(animalsString.isEmpty() ? "" : animalsString);
        });

        enclosuresCol.setCellValueFactory(cellData -> {
            Set<Enclosure> enclosures = cellData.getValue().getEnclosures();
            String enclosuresString = enclosures.stream()
                    .map(Enclosure::getName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(enclosuresString.isEmpty() ? "" : enclosuresString);
        });

        loadUsers();
        loadTasks();

        userFilterComboBox.setOnAction(event -> filterTasksByUser());
    }

    private void loadUsers() {
        List<User> users = userDao.getAll();

        userFilterComboBox.setConverter(new StringConverter<String>() {
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

        userFilterComboBox.getItems().add("filter.all.task");

        for (User user : users) {
            if (user.getRole().equals(Role.MANAGER) || user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CASHIER) || user.getRole().equals(Role.INACTIVE)) {
                continue;
            }
            String displayString = String.format("%s %s (%s) (%d)",
                    user.getFirstName(), user.getLastName(), user.getRole(), user.getId());
            userFilterComboBox.getItems().add(displayString);
        }

        userFilterComboBox.getSelectionModel().select("filter.all.task");
    }

    private void loadTasks() {
        var principal = Auth.INSTANCE.getPrincipal();

        List<Task> tasks;

        if (principal.getRole() == Role.ADMIN || principal.getRole() == Role.MANAGER) {
            tasks = taskDao.getAll();
        } else {
            tasks = taskDao.getByUser(principal.getId());
        }

        ObservableList<Task> taskObservableList = FXCollections.observableArrayList(tasks);
        taskTable.setItems(taskObservableList);
    }

    private void filterTasksByUser() {
        String selectedUser = userFilterComboBox.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            if (selectedUser.equals("filter.all.task")) {
                loadTasks();
            } else {
                String userIdString = selectedUser.substring(selectedUser.lastIndexOf("(") + 1, selectedUser.lastIndexOf(")"));
                try {
                    long userId = Long.parseLong(userIdString);

                    List<Task> tasks = taskDao.getByUser(userId);
                    ObservableList<Task> taskObservableList = FXCollections.observableArrayList(tasks);
                    taskTable.setItems(taskObservableList);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void addTaskButtonAction(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/task/TaskEdit.fxml", "Pridanie úlohy");
    }

    @FXML
    void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }

    @FXML
    public void finishTaskButtonAction(ActionEvent actionEvent) {
        boolean suhlas = SceneManager.confirm("Naozaj chcete označiť úlohu ako dokončenú?");
        if (!suhlas) {
            return;
        }

        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            selectedTask.setStatus(Status.COMPLETED);

            taskDao.update(selectedTask);

            if (!selectedTask.getAnimals().isEmpty()) {
                for (Animal animal : selectedTask.getAnimals()) {
                    Animal animalToUpdate = animalDao.getById(animal.getId());
                    animalToUpdate.setLastCheck(LocalDateTime.now());
                    animalDao.update(animalToUpdate);
                }
            }

            if (!selectedTask.getEnclosures().isEmpty()) {
                for (Enclosure enclosure : selectedTask.getEnclosures()) {
                    Enclosure enclosureToUpdate = enclosureDao.getById(enclosure.getId());
                    enclosureToUpdate.setLastMaintainance(LocalDateTime.now());
                    enclosureDao.update(enclosureToUpdate);
                }
            }

            taskTable.refresh();
        } else {
            System.out.println("Žiadna úloha nebola vybraná.");
        }
    }

    public void deleteTaskButtonAction(ActionEvent event) {
        boolean suhlas = SceneManager.confirm("Naozaj chcete vymazať úlohu zo zoznamu?");
        if (!suhlas) {
            return;
        }

        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            taskDao.delete(selectedTask.getId());
            loadTasks();


            List<Task> tasks = taskDao.getAll();
            ObservableList<Task> taskObservableList = FXCollections.observableArrayList(tasks);
            taskTable.setItems(taskObservableList);
        }
    }


}
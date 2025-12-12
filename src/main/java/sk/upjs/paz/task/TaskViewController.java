package sk.upjs.paz.task;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.user.Role;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

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
    public ComboBox<String> userFilterComboBox;  // ComboBox pre filter používateľov

    @FXML
    private Button addTaskButton;

    @FXML
    private Button goBackButton;

    private TaskDao taskDao = Factory.INSTANCE.getTaskDao();
    private UserDao userDao = Factory.INSTANCE.getUserDao();  // Na získanie používateľov

    @FXML
    void initialize() {
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
                    .map(Animal::getNickname)  // Predpokladáme, že Animal má metódu getNickname
                    .collect(Collectors.joining(", "));  // Zoznam mien oddelených čiarkami
            return new SimpleStringProperty(animalsString.isEmpty() ? "" : animalsString);
        });

        enclosuresCol.setCellValueFactory(cellData -> {
            Set<Enclosure> enclosures = cellData.getValue().getEnclosures();
            String enclosuresString = enclosures.stream()
                    .map(Enclosure::getName)  // Predpokladáme, že Enclosure má metódu getName
                    .collect(Collectors.joining(", "));  // Zoznam mien oddelených čiarkami
            return new SimpleStringProperty(enclosuresString.isEmpty() ? "" : enclosuresString);
        });

        SceneManager.setupDoubleClick(
                taskTable,
                "/sk.upjs.paz/task/TaskEdit.fxml",
                "Upraviť úlohu",
                (TaskEditController ctrl, Task task) -> ctrl.setTasks(task));

        loadUsers();
        loadTasks();

        userFilterComboBox.setOnAction(event -> filterTasksByUser());
    }

    private void loadUsers() {
        List<User> users = userDao.getAll();

        userFilterComboBox.getItems().add("Všetci");

        for (User user : users) {
            if (user.getRole().equals(Role.MANAGER) || user.getRole().equals(Role.ADMIN)) {
                continue;
            }
            String displayString = String.format("%s %s (%s) (%d)",
                    user.getFirstName(), user.getLastName(), user.getRole(), user.getId());
            userFilterComboBox.getItems().add(displayString);
        }

        // Prednastavenie na "Všetci"
        userFilterComboBox.getSelectionModel().select("Všetci");
    }

    private void loadTasks() {
        List<Task> tasks = taskDao.getAll();
        ObservableList<Task> taskObservableList = FXCollections.observableArrayList(tasks);
        taskTable.setItems(taskObservableList);
    }

    private void filterTasksByUser() {
        String selectedUser = userFilterComboBox.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            if (selectedUser.equals("Všetci")) {
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
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            selectedTask.setStatus(Status.COMPLETED);

            taskDao.update(selectedTask);

            taskTable.refresh();
        } else {
            System.out.println("Žiadna úloha nebola vybraná.");
        }
    }
}
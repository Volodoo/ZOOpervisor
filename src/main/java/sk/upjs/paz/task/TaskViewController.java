package sk.upjs.paz.task;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    public TextField searchTextField;

    @FXML
    private Button addTaskButton;

    @FXML
    private Button goBackButton;

    private TaskDao taskDao = Factory.INSTANCE.getTaskDao();
    private UserDao userDao = Factory.INSTANCE.getUserDao();
    private AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();
    private EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    private ObservableList<Task> masterData = FXCollections.observableArrayList();
    private FilteredList<Task> filteredData;

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
            SceneManager.setupDoubleClick(taskTable, "/sk.upjs.paz/task/TaskEdit.fxml", "Upraviť úlohu", TaskEditController::setTask);
        }

        // Lambda pre všetky stĺpce
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        descriptionCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription()));

        deadlineCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));

        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        userCol.setCellValueFactory(cellData -> {
            User user = cellData.getValue().getUser();
            return new SimpleStringProperty(user.getFirstName() + " " + user.getLastName() + " (" + user.getRole() + ")");
        });

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

        // načítanie užívateľov do comboboxu
        loadUsers();

        // masterData + filteredData
        filteredData = new FilteredList<>(masterData, p -> true);
        taskTable.setItems(filteredData);

        // search bar listener
        searchTextField.textProperty().addListener((obs, oldText, newText) -> applyFilters());

        // combobox listener
        userFilterComboBox.setOnAction(event -> applyFilters());

        loadTasks();
    }

    private void loadUsers() {
        List<User> users = userDao.getAll();

        userFilterComboBox.getItems().clear();
        userFilterComboBox.getItems().add("filter.all.task");

        userFilterComboBox.setConverter(new StringConverter<>() {
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

        for (User user : users) {
            if (user.getRole() == Role.MANAGER || user.getRole() == Role.ADMIN || user.getRole() == Role.CASHIER || user.getRole() == Role.INACTIVE) {
                continue;
            }
            String displayString = String.format("%s %s (%s) (%d)", user.getFirstName(), user.getLastName(), user.getRole(), user.getId());
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

        masterData.setAll(tasks);
        applyFilters();
    }

    private void applyFilters() {
        String searchText = searchTextField.getText();
        String selectedUser = userFilterComboBox.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(task -> {

            // filter podľa usera
            if (selectedUser != null && !selectedUser.equals("filter.all.task")) {
                String userIdString = selectedUser.substring(selectedUser.lastIndexOf("(") + 1, selectedUser.lastIndexOf(")"));
                try {
                    long userId = Long.parseLong(userIdString);
                    if (task.getUser().getId() != userId) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            // filter podľa search textu
            if (searchText == null || searchText.isBlank()) {
                return true;
            }

            String filter = searchText.toLowerCase();

            return task.getName().toLowerCase().contains(filter)
                    || task.getDescription().toLowerCase().contains(filter)
                    || task.getStatus().toString().toLowerCase().contains(filter)
                    || task.getUser().getFirstName().toLowerCase().contains(filter)
                    || task.getUser().getLastName().toLowerCase().contains(filter)
                    || task.getUser().getRole().toString().toLowerCase().contains(filter)
                    || task.getAnimals().stream().anyMatch(a -> a.getNickname().toLowerCase().contains(filter) || a.getId().toString().contains(filter)) || task.getEnclosures().stream().anyMatch(e -> e.getName().toLowerCase().contains(filter))
                    || task.getDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).contains(filter);
        });
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
        if (!suhlas) return;

        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            selectedTask.setStatus(Status.COMPLETED);
            taskDao.update(selectedTask);

            if (!selectedTask.getAnimals().isEmpty()) {
                for (Animal animal : selectedTask.getAnimals()) {
                    Animal animalToUpdate = animalDao.getById(animal.getId());
                    animalToUpdate.setLastCheck(java.time.LocalDateTime.now());
                    animalDao.update(animalToUpdate);
                }
            }

            if (!selectedTask.getEnclosures().isEmpty()) {
                for (Enclosure enclosure : selectedTask.getEnclosures()) {
                    Enclosure enclosureToUpdate = enclosureDao.getById(enclosure.getId());
                    enclosureToUpdate.setLastMaintainance(java.time.LocalDateTime.now());
                    enclosureDao.update(enclosureToUpdate);
                }
            }

            taskTable.refresh();
        }
    }

    public void deleteTaskButtonAction(ActionEvent event) {
        boolean suhlas = SceneManager.confirm("Naozaj chcete vymazať úlohu zo zoznamu?");
        if (!suhlas) return;

        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskDao.delete(selectedTask.getId());
            loadTasks();
        }
    }
}

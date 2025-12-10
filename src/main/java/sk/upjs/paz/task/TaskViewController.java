package sk.upjs.paz.task;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.enclosure.EnclosureEditController;
import sk.upjs.paz.user.User;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.enclosure.Enclosure;

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

    @FXML
    private Button addTaskButton;

    @FXML
    private Button goBackButton;


    private TaskDao taskDao = Factory.INSTANCE.getTaskDao();

    @FXML
    void initialize() {
        // Nastavenie stĺpcov pre zobrazenie údajov o úlohách
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName())
        );

        descriptionCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescription())
        );

        deadlineCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return new SimpleStringProperty(cellData.getValue().getDeadline().format(formatter));
        });

        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().toString())
        );

        userCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUser().getFirstName() + " " + cellData.getValue().getUser().getLastName() + " (" + cellData.getValue().getUser().getRole() + ")")
        );

        animalsCol.setCellValueFactory(cellData -> {
            Set<Animal> animals = cellData.getValue().getAnimals();
            String animalsString = animals.stream()
                    .map(Animal::getNickname)  // Predpokladáme, že Animal má metódu getNickname
                    .collect(Collectors.joining(", "));  // Zoznam mien oddelených čiarkami
            return new SimpleStringProperty(animalsString.isEmpty() ? "" : animalsString);
        });

        // Výbehy ako zoznam mien oddelených čiarkami
        enclosuresCol.setCellValueFactory(cellData -> {
            Set<Enclosure> enclosures = cellData.getValue().getEnclosures();
            String enclosuresString = enclosures.stream()
                    .map(Enclosure::getName)  // Predpokladáme, že Enclosure má metódu getName
                    .collect(Collectors.joining(", "));  // Zoznam mien oddelených čiarkami
            return new SimpleStringProperty(enclosuresString.isEmpty() ? "" : enclosuresString);
        });

        SceneManager.setupDoubleClick(
                taskTable,
                "/sk.upjs.paz/EditTask.fxml",
                "Upraviť úlohu",
                (TaskEditController ctrl, Task task) -> ctrl.setTasks(task));

        loadTasks();
    }

    private void loadTasks() {
        // Načítame úlohy z databázy
        List<Task> tasks = taskDao.getAll();

        // Prevod na ObservableList, aby sa údaje aktualizovali v TableView
        ObservableList<Task> taskObservableList = FXCollections.observableArrayList(tasks);

        // Nastavenie ObservableList ako dátový zdroj pre TableView
        taskTable.setItems(taskObservableList);
    }

    @FXML
    void addTaskButtonAction(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/EditTask.fxml","Pridanie tasku");
    }

    @FXML
    void goBack(ActionEvent event) {
        // Zmena scény na hlavné okno
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}
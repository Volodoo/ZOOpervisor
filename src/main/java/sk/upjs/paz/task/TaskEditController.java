package sk.upjs.paz.task;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.user.User;

import java.time.LocalDate;

public class TaskEditController {

    @FXML
    private DatePicker deadlineDatePicker;

    @FXML
    private TextArea descriptionCol;

    @FXML
    private Button goBackButton;

    @FXML
    private TextField nameField;

    @FXML
    private Button showAnimalsButton;

    @FXML
    private Button showEnclosuresButton;

    @FXML
    private ComboBox<Status> statusComboBox;

    @FXML
    private Button updateTaskButton;

    @FXML
    private ComboBox<User> userComboBox;

    @FXML
    void ShowEnclosuresButtonAction(ActionEvent event) {
    }
    private Task task;
    private boolean editMode = false;

    TaskDao taskDao= Factory.INSTANCE.getTaskDao();

    @FXML
    void goBackButtonAction(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/TaskView.fxml","Hlavne okno");
    }

    @FXML
    void showAnimalsButtonAction(ActionEvent event) {

    }

    @FXML
    void saveEdit(ActionEvent event) {
        if (editMode) {
            task.setName(nameField.getText());
            task.setDescription(descriptionCol.getText());
            if (statusComboBox.getValue() != null) {
                task.setStatus(statusComboBox.getValue());

            }
            else{
                task.setStatus(Status.INCOMPLETE);
            }
            if(userComboBox.getValue() != null) {
                task.setUser(userComboBox.getValue());
            }
            if(deadlineDatePicker.getValue() != null) {
                task.setDeadline(deadlineDatePicker.getValue().atStartOfDay());
            }

            taskDao.update(task);
            editMode = false;
        }
        else{
            Task task = new Task();
            task.setName(nameField.getText());
            task.setDescription(descriptionCol.getText());
            if (statusComboBox.getValue() != null) {
                task.setStatus(statusComboBox.getValue());
            }
            else{
                task.setStatus(Status.INCOMPLETE);
            }
            if(userComboBox.getValue() != null) {
                task.setUser(userComboBox.getValue());
            }
            if(deadlineDatePicker.getValue() != null) {
                task.setDeadline(deadlineDatePicker.getValue().atStartOfDay());
            }
            taskDao.create(task);
        }
        SceneManager.changeScene(event,"/sk.upjs.paz/TaskView.fxml","Zobrazenie úloh");
    }

    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/TaskView.fxml","Zobrazenie úloh");
    }

    public void setTasks(Task task) {
        this.editMode = true;
        this.task=task;
        nameField.setText(task.getName());
        descriptionCol.setText(task.getDescription());
        statusComboBox.setValue(task.getStatus());
        if(userComboBox.getValue() != null) {
            userComboBox.setValue(task.getUser());
        }
        if(deadlineDatePicker.getValue() != null) {
            deadlineDatePicker.setValue(LocalDate.from(task.getDeadline()));
        }
    }
}

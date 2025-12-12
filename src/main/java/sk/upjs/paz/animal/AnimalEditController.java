package sk.upjs.paz.animal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.enclosure.EnclosureDao;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AnimalEditController {

    @FXML
    public Spinner<Integer> hourSpinner;
    @FXML
    public Spinner<Integer> minuteSpinner;

    @FXML
    private DatePicker birthDayPicker;

    @FXML
    private DatePicker lastCheckPicker;

    @FXML
    private Button goBackButton;

    @FXML
    private TextField nicknameField;

    @FXML
    private ComboBox<Enclosure> enclosureComboBox;

    @FXML
    private ComboBox<Sex> sexComboBox1;

    @FXML
    private TextField speciesField;

    @FXML
    private ComboBox<Status> statusComboBox;

    @FXML
    private Button updateAnimalButton;

    private Animal animal;

    private AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();
    private EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();
    private boolean editMode = false;

    @FXML
    void initialize() {
        // Nastavenie ComboBoxov
        sexComboBox1.getItems().setAll(Sex.values());
        statusComboBox.getItems().setAll(Status.values());
        enclosureComboBox.getItems().setAll(enclosureDao.getAll());

        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        lastCheckPicker.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isBlank()) {
                lastCheckPicker.setValue(null);
            }
        });

        if (editMode && animal != null && animal.getLastCheck() != null) {
            LocalDateTime dt = animal.getLastCheck();
            lastCheckPicker.setValue(dt.toLocalDate());
            hourSpinner.getValueFactory().setValue(dt.getHour());
            minuteSpinner.getValueFactory().setValue(dt.getMinute());
        }
    }

    @FXML
    void goBackButtonAction(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/animal/AnimalView.fxml", "Hlavne okno");
    }

    @FXML
    void updateAnimalButtonAction(ActionEvent event) {
        if (!editMode) {
            animal = new Animal();
        }

        animal.setNickname(nicknameField.getText());
        animal.setSpecies(speciesField.getText());
        animal.setStatus(statusComboBox.getValue());
        animal.setEnclosure(enclosureComboBox.getValue());
        animal.setSex(sexComboBox1.getValue());
        animal.setBirthDay(birthDayPicker.getValue());

        LocalDate lastCheckDate = lastCheckPicker.getValue();
        if (lastCheckDate != null) {
            int hour = hourSpinner.getValue();
            int minute = minuteSpinner.getValue();
            animal.setLastCheck(LocalDateTime.of(lastCheckDate, java.time.LocalTime.of(hour, minute)));
        } else {
            animal.setLastCheck(null); // ak je vymazané, nastavíme null
        }

        if (editMode) {
            animalDao.update(animal);
        } else {
            animalDao.create(animal);
        }

        editMode = false;
        SceneManager.changeScene(event, "/sk.upjs.paz/animal/AnimalView.fxml", "Zvierata");
    }

    public void setAnimal(Animal animal) {
        this.editMode = true;
        this.animal = animal;
        nicknameField.setText(animal.getNickname());
        speciesField.setText(animal.getSpecies());
        sexComboBox1.setValue(animal.getSex());
        statusComboBox.setValue(animal.getStatus());
        enclosureComboBox.setValue(animal.getEnclosure());
        birthDayPicker.setValue(animal.getBirthDay());

        if (animal.getLastCheck() != null) {
            LocalDateTime dt = animal.getLastCheck();
            lastCheckPicker.setValue(dt.toLocalDate());
            hourSpinner.getValueFactory().setValue(dt.getHour());
            minuteSpinner.getValueFactory().setValue(dt.getMinute());
        }
    }
}
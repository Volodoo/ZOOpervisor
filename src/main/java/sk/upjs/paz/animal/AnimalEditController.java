package sk.upjs.paz.animal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.enclosure.EnclosureDao;

import java.time.LocalDate;


public class AnimalEditController {

    @FXML
    private DatePicker birthDayPicker;

    @FXML
    private DatePicker birthDayPicker1;

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

    private AnimalDao animalDao= Factory.INSTANCE.getAnimalDao();

    private EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    private boolean editMode = false;

    @FXML
    void initialize() {
        sexComboBox1.getItems().setAll(Sex.values());
        statusComboBox.getItems().setAll(Status.values());
        enclosureComboBox.getItems().setAll(enclosureDao.getAll());
    }

    @FXML
    void goBackButtonAction(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/animal/AnimalView.fxml","Hlavne okno");
    }

    @FXML
    void updateAnimalButtonAction(ActionEvent event) {

        if (editMode) {
            animal.setNickname(nicknameField.getText());
            animal.setSpecies(speciesField.getText());
            animal.setStatus(statusComboBox.getValue());
            animal.setEnclosure(enclosureComboBox.getValue());
            animal.setSex(sexComboBox1.getSelectionModel().getSelectedItem());
            animal.setBirthDay(birthDayPicker.getValue());
            animalDao.update(animal);
            this.editMode = false;
        }
        else{
            Animal animal=new Animal();
            animal.setNickname(nicknameField.getText());
            animal.setSpecies(speciesField.getText());
            animal.setStatus(statusComboBox.getValue());
            animal.setEnclosure(enclosureComboBox.getValue());
            animal.setSex(sexComboBox1.getSelectionModel().getSelectedItem());
            animal.setBirthDay(birthDayPicker.getValue());
            animalDao.create(animal);
        }

        SceneManager.changeScene(event, "/sk.upjs.paz/animal/AnimalView.fxml", "Zvierata");
    }

    public void setAnimal(Animal animal) {
        this.editMode = true;
        this.animal = animal;
        nicknameField.setText(animal.getNickname());
        speciesField.setText(animal.getSpecies());
        sexComboBox1.setValue(animal.getSex());
        statusComboBox.setValue(animal.getStatus());

        if (animal.getEnclosure() != null) {

                    enclosureComboBox.setValue(animal.getEnclosure());

                }
        if (animal.getBirthDay() != null) {
            birthDayPicker.setValue(animal.getBirthDay());
        }

        if (animal.getLastCheck() != null) {
            birthDayPicker1.setValue(LocalDate.from(animal.getLastCheck()));
        }
    }

}
package sk.upjs.paz.animal;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.security.Auth;
import sk.upjs.paz.user.Role;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AnimalViewController {

    @FXML
    public TableColumn<Animal, String> birthdayCol;
    @FXML
    public TableColumn<Animal, String> lastCheckCol;
    @FXML
    public TableColumn<Animal, String> statusCol;
    @FXML
    public ComboBox<String> speciesFilterComboBox;
    @FXML
    public Button addAnimalButton;
    @FXML
    public Label userNameLabel;
    @FXML
    public Label roleLabel;
    @FXML
    public TextField searchTextField;
    @FXML
    private TableColumn<Animal, String> enclosureCol;
    @FXML
    private TableView<Animal> animalsTable;
    @FXML
    private TableColumn<Animal, String> nicknameCol;
    @FXML
    private TableColumn<Animal, String> sexCol;
    @FXML
    private TableColumn<Animal, String> speciesCol;

    private AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();

    private ObservableList<Animal> masterData = FXCollections.observableArrayList();
    private FilteredList<Animal> filteredData;

    @FXML
    void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();

        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        if (principal == null || principal.getRole() != Role.ADMIN) {
            addAnimalButton.setDisable(true);
        }

        if (principal.getRole() == Role.ADMIN || principal.getRole() == Role.MANAGER) {
            SceneManager.setupDoubleClick(
                    animalsTable,
                    "/sk.upjs.paz/animal/AnimalEdit.fxml",
                    "Upraviť zviera",
                    AnimalEditController::setAnimal
            );
        } else {
            animalsTable.setOnMouseClicked(Event::consume);
        }

        speciesFilterComboBox.getItems().addAll(
                "filter.all",
                "filter.active",
                "filter.inactive",
                "filter.treatment"
        );

        speciesFilterComboBox.setConverter(new StringConverter<>() {
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

        speciesFilterComboBox.getSelectionModel().select("filter.all");
        speciesFilterComboBox.setOnAction(event -> applyFilters());

        nicknameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNickname()));

        speciesCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSpecies()));

        sexCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSex().toString()));

        birthdayCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBirthDay().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));

        enclosureCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEnclosure().getName()));

        lastCheckCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLastCheck() != null) {
                return new SimpleStringProperty(cellData.getValue().getLastCheck().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            } else {
                return new SimpleStringProperty("");
            }
        });

        statusCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        // nastavenie filteredData
        filteredData = new FilteredList<>(masterData, p -> true);
        animalsTable.setItems(filteredData);

        searchTextField.textProperty().addListener((obs, oldText, newText) -> applyFilters());

        loadAnimals();
    }

    private void applyFilters() {
        String searchText = searchTextField.getText();
        String selectedStatus = speciesFilterComboBox.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(animal -> {

            // Filter podľa statusu
            if (selectedStatus != null && !selectedStatus.equals("filter.all")) {
                Status status = convertStringToStatus(selectedStatus);
                if (animal.getStatus() != status) {
                    return false;
                }
            }

            // Filter podľa search textu
            if (searchText == null || searchText.isBlank()) {
                return true;
            }

            String filter = searchText.toLowerCase();

            return animal.getNickname().toLowerCase().contains(filter)
                    || animal.getSpecies().toLowerCase().contains(filter)
                    || animal.getSex().toString().toLowerCase().contains(filter)
                    || animal.getBirthDay().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")).contains(filter)
                    || animal.getEnclosure().getName().toLowerCase().contains(filter)
                    || animal.getStatus().toString().toLowerCase().contains(filter)
                    || (animal.getLastCheck() != null && animal.getLastCheck().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).contains(filter));
        });
    }

    private Status convertStringToStatus(String statusString) {
        return switch (statusString) {
            case "filter.active" -> Status.ACTIVE;
            case "filter.inactive" -> Status.INACTIVE;
            case "filter.treatment" -> Status.TREATMENT;
            default -> throw new IllegalArgumentException("Neznámy status: " + statusString);
        };
    }

    private void loadAnimals() {
        List<Animal> animals = animalDao.getAllSortedByZoneSpecies();
        masterData.setAll(animals);
        applyFilters();
    }

    @FXML
    void addAnimalButtonAction(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/animal/AnimalEdit.fxml", "Pridavanie Zvierata");
    }

    @FXML
    void goBackButtonAction(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}

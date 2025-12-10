package sk.upjs.paz.enclosure;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class EnclosureViewController {

    @FXML
    public TableColumn<Enclosure, String> animalsCountCol;
    @FXML
    private Button addEnclosureButton;

    @FXML
    private TableView<Enclosure> enclosureTable;

    @FXML
    private Button goBackButton;

    @FXML
    private TableColumn<Enclosure, String> lastMaintainanceCol;

    @FXML
    private TableColumn<Enclosure, String> nameCol;  // Názov výbehu

    @FXML
    private HBox occupationHbox;

    @FXML
    private TableColumn<Enclosure, String> zoneCol;  // Zóna výbehu

    private EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();

    @FXML
    void initialize() {

        // Pre názov výbehu
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName())
        );

        // Pre zónu
        zoneCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getZone())
        );

        // Pre počet zvierat v výbehu
        animalsCountCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getAnimalsCount()))
        );

        lastMaintainanceCol.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(
                    cellData.getValue().getLastMaintainance() != null ?
                            cellData.getValue().getLastMaintainance().toLocalDate().toString() :
                            "Ešte Neprebehla"
            );
        });

        loadEnclosures();
    }

    private void loadEnclosures() {
        List<Enclosure> enclosures = enclosureDao.getAllSortedByZone();

        ObservableList<Enclosure> enclosureObservableList = FXCollections.observableArrayList(enclosures);

        enclosureTable.setItems(enclosureObservableList);
    }

    @FXML
    void addEnclosureButtonAction(ActionEvent event) {
        // Implementácia pre pridanie nového výbehu
        // Môžeš pridať dialóg na zadanie údajov pre nový výbeh
    }

    @FXML
    void goBackButtonAction(ActionEvent event) throws IOException {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}
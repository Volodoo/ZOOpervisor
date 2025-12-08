package sk.upjs.paz.enclosure;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class EnclosureViewController {

    @FXML
    private Button addEnclosureButton;

    @FXML
    private TableView<?> enclosureTable;

    @FXML
    private Button goBackButton;

    @FXML
    private TableColumn<?, ?> idCol;

    @FXML
    private TableColumn<?, ?> lastMaintainanceCol;

    @FXML
    private TableColumn<?, ?> nameCol;

    @FXML
    private HBox occupationHbox;

    @FXML
    private TableColumn<?, ?> zoneCol;

    @FXML
    void addEnclosureButtonAction(ActionEvent event) {

    }

    @FXML
    void goBackButtonAction(ActionEvent event) {

    }

}

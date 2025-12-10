package sk.upjs.paz.ticket;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketViewController {

    @FXML
    public TableView<Ticket> ticketTable;

    @FXML
    public TableColumn<Ticket, String> typeCol;

    @FXML
    public TableColumn<Ticket, String> purchaseDateTimeCol;

    @FXML
    public TableColumn<Ticket, String> priceCol;

    @FXML
    public TableColumn<Ticket, String> cashierCol;

    private TicketDao ticketDao = Factory.INSTANCE.getTicketDao();

    @FXML
    public void initialize() {
        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(getTypeInSlovak(cellData.getValue().getType()))
        );


        purchaseDateTimeCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return new SimpleStringProperty(cellData.getValue().getPurchaseDateTime().format(formatter));
        });

        priceCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice()))
        );

        cashierCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCashier().getFirstName() + " " + cellData.getValue().getCashier().getLastName())
        );

        loadTickets();
    }

    private String getTypeInSlovak(String type) {
        String typeSlovak = "";
        switch (type) {
            case "Child":
                typeSlovak = "Dieťa";
                break;
            case "Student_Senior":
                typeSlovak = "Študent/Senior";
                break;
            case "Adult":
                typeSlovak = "Dospelý";
                break;
            default:
                typeSlovak = type;
        }
        return typeSlovak;
    }

    private void loadTickets() {
        List<Ticket> tickets = ticketDao.getAll();

        ObservableList<Ticket> ticketObservableList = FXCollections.observableArrayList(tickets);

        ticketTable.setItems(ticketObservableList);
    }

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }

    @FXML
    public void addTicket(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/SellTicketView.fxml", "Predaj lístka");
    }
}
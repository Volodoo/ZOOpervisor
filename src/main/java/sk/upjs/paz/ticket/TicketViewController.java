package sk.upjs.paz.ticket;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.security.Auth;
import sk.upjs.paz.user.Role;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

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

    @FXML
    public ComboBox<String> cashierFilterComboBox;
    @FXML
    public Label userNameLabel;
    @FXML
    public Label roleLabel;
    public Spinner ticket1CountSpinner;

    private TicketDao ticketDao = Factory.INSTANCE.getTicketDao();
    private UserDao userDao = Factory.INSTANCE.getUserDao();

    @FXML
    public void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();

        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");


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

        loadCashiers();
        loadTickets();

        cashierFilterComboBox.setConverter(new StringConverter<String>() {
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

        cashierFilterComboBox.setOnAction(event -> filterTicketsByCashier());
    }

    private String getTypeInSlovak(String type) {
        switch (type) {
            case "Child":
                return "Dieťa";
            case "Student_Senior":
                return "Študent/Senior";
            case "Adult":
                return "Dospelý";
            default:
                return type;
        }
    }

    @FXML
    public void OnActionSearchTextField(){}

    private void loadCashiers() {
        List<User> users = userDao.getAll();

        cashierFilterComboBox.getItems().clear();

        cashierFilterComboBox.getItems().add("filter.all.task");

        for (User user : users) {
            if (user.getRole().equals(Role.CASHIER)) {
                String cashierFullNameWithId = user.getFirstName() + " " + user.getLastName() + " (" + user.getId() + ")";
                cashierFilterComboBox.getItems().add(cashierFullNameWithId);
            }
        }

        cashierFilterComboBox.getSelectionModel().select("filter.all.task");
    }

    private void filterTicketsByCashier() {
        String selectedCashier = cashierFilterComboBox.getSelectionModel().getSelectedItem();

        if (selectedCashier != null) {
            if (selectedCashier.equals("filter.all.task")) {
                loadTickets();
            } else {
                Long cashierId = extractCashierIdFromString(selectedCashier);
                if (cashierId != null) {
                    List<Ticket> tickets = ticketDao.getByCashier(cashierId);
                    ObservableList<Ticket> ticketObservableList = FXCollections.observableArrayList(tickets);
                    ticketTable.setItems(ticketObservableList);
                }
            }
        }
    }

    private Long extractCashierIdFromString(String fullNameWithId) {
        String idString = fullNameWithId.substring(fullNameWithId.lastIndexOf('(') + 1, fullNameWithId.lastIndexOf(')'));
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException e) {
            return null;
        }
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
    public void showStatisticButton(ActionEvent event) {
        SceneManager.openNewWindow("/sk.upjs.paz/ticket/TicketStatsView.fxml", "Štatistika");

    }
}
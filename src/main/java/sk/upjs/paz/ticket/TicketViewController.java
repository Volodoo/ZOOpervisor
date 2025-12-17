package sk.upjs.paz.ticket;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

    @FXML
    public TextField searchTextField;

    private TicketDao ticketDao = Factory.INSTANCE.getTicketDao();
    private UserDao userDao = Factory.INSTANCE.getUserDao();

    private ObservableList<Ticket> masterData = FXCollections.observableArrayList();
    private FilteredList<Ticket> filteredData;

    @FXML
    public void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();

        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        typeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(getTypeInSlovak(cellData.getValue().getType()))
        );

        purchaseDateTimeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue()
                                .getPurchaseDateTime()
                                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                )
        );

        priceCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getPrice()))
        );

        cashierCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getCashier().getFirstName() + " " +
                                cellData.getValue().getCashier().getLastName()
                )
        );

        loadCashiers();

        filteredData = new FilteredList<>(masterData, p -> true);
        ticketTable.setItems(filteredData);

        cashierFilterComboBox.setConverter(new StringConverter<>() {
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

        cashierFilterComboBox.getSelectionModel().select("filter.all.task");
        cashierFilterComboBox.setOnAction(e -> applyFilters());

        searchTextField.textProperty().addListener((obs, oldText, newText) -> applyFilters());

        loadTickets();
    }

    private String getTypeInSlovak(String type) {
        return switch (type) {
            case "Child" -> "Dieťa";
            case "Student_Senior" -> "Študent/Senior";
            case "Adult" -> "Dospelý";
            default -> type;
        };
    }

    private void loadCashiers() {
        List<User> users = userDao.getAll();

        cashierFilterComboBox.getItems().clear();
        cashierFilterComboBox.getItems().add("filter.all.task");

        for (User user : users) {
            if (user.getRole() == Role.CASHIER) {
                cashierFilterComboBox.getItems().add(
                        user.getFirstName() + " " + user.getLastName());
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

    private void applyFilters() {
        String searchText = searchTextField.getText();
        String selectedCashier = cashierFilterComboBox.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(ticket -> {

            // Filter podľa pokladníka
            if (selectedCashier != null && !selectedCashier.equals("filter.all.task")) {
                Long cashierId = extractCashierIdFromString(selectedCashier);
                if (!ticket.getCashier().getId().equals(cashierId)) {
                    return false;
                }
            }

            // Filter podľa search textu
            if (searchText == null || searchText.isBlank()) {
                return true;
            }

            String filter = searchText.toLowerCase();

            return getTypeInSlovak(ticket.getType()).toLowerCase().contains(filter)
                    || ticket.getPurchaseDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")).contains(filter)
                    || ticket.getPrice().toString().contains(filter)
                    || (ticket.getCashier().getFirstName() + " " + ticket.getCashier().getLastName()).toLowerCase().contains(filter);
        });
    }

    private void loadTickets() {
        List<Ticket> tickets = ticketDao.getAll();
        masterData.setAll(tickets);
        applyFilters();
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

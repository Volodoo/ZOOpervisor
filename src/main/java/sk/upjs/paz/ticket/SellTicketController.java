package sk.upjs.paz.ticket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.security.Auth;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SellTicketController {

    @FXML
    private Spinner<Integer> childSpinner;

    @FXML
    private Spinner<Integer> seniorStudentSpinner;

    @FXML
    private Spinner<Integer> adultSpinner;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label childTotalPrice;

    @FXML
    private Label seniorStudentTotalPrice;

    @FXML
    private Label adultTotalPrice;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Label successLabel;

    private static final BigDecimal PRICE_CHILD = new BigDecimal("4.00");
    private static final BigDecimal PRICE_SENIOR_STUDENT = new BigDecimal("7.00");
    private static final BigDecimal PRICE_ADULT = new BigDecimal("10.00");

    TicketDao ticketDao = Factory.INSTANCE.getTicketDao();
    UserDao userDao = Factory.INSTANCE.getUserDao();

    @FXML
    public void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();
        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        childSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        seniorStudentSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        adultSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));

        childSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateTicketPrices());
        seniorStudentSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateTicketPrices());
        adultSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateTicketPrices());

        updateTicketPrices();
    }

    private void updateTicketPrices() {
        int childCount = childSpinner.getValue();
        int seniorStudentCount = seniorStudentSpinner.getValue();
        int adultCount = adultSpinner.getValue();

        BigDecimal childTotal = PRICE_CHILD.multiply(new BigDecimal(childCount));
        BigDecimal seniorStudentTotal = PRICE_SENIOR_STUDENT.multiply(new BigDecimal(seniorStudentCount));
        BigDecimal adultTotal = PRICE_ADULT.multiply(new BigDecimal(adultCount));

        String priceLabelText = "Cena:";
        try {
            priceLabelText = SceneManager.getBundle().getString("priceSell");
        } catch (Exception e) {
        }

        childTotalPrice.setText(priceLabelText + childTotal + " €");
        seniorStudentTotalPrice.setText(priceLabelText + seniorStudentTotal + " €");
        adultTotalPrice.setText(priceLabelText + adultTotal + " €");

        String totalLabelText = "Celková cena:";
        try {
            totalLabelText = SceneManager.getBundle().getString("priceTogheder");
        } catch (Exception e) {
        }

        BigDecimal totalPrice = childTotal.add(seniorStudentTotal).add(adultTotal);
        totalPriceLabel.setText(totalLabelText + totalPrice + " €");
    }

    @FXML
    public void sellTicket(ActionEvent event) {
        boolean suhlas = SceneManager.confirm("Naozaj chcete predať lístok?");
        if (!suhlas) {
            return;
        }

        boolean ticketsSold = false;

        if (childSpinner.getValue() > 0) {
            createTicket("Child", PRICE_CHILD, childSpinner.getValue());
            ticketsSold = true;
        }

        if (seniorStudentSpinner.getValue() > 0) {
            createTicket("Student_Senior", PRICE_SENIOR_STUDENT, seniorStudentSpinner.getValue());
            ticketsSold = true;
        }

        if (adultSpinner.getValue() > 0) {
            createTicket("Adult", PRICE_ADULT, adultSpinner.getValue());
            ticketsSold = true;
        }

        if (ticketsSold) {
            successLabel.setText("Lístky boli úspešne predané!");
            successLabel.setVisible(true);
            clearSpinners();
        } else {
            successLabel.setText("Neboli predané žiadne lístky.");
            successLabel.setVisible(true);
        }
    }

    private void createTicket(String type, BigDecimal price, int count) {
        User currentCashier = userDao.getById(Auth.INSTANCE.getPrincipal().getId());

        for (int i = 0; i < count; i++) {
            Ticket ticket = new Ticket();
            ticket.setType(type);
            ticket.setPrice(price);
            ticket.setPurchaseDateTime(LocalDateTime.now());
            ticket.setCashier(currentCashier);

            ticketDao.create(ticket);
        }
    }

    private void clearSpinners() {
        childSpinner.getValueFactory().setValue(0);
        seniorStudentSpinner.getValueFactory().setValue(0);
        adultSpinner.getValueFactory().setValue(0);
    }

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }
}
package sk.upjs.paz.ticket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.security.Auth;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class SellTicketController {

    @FXML
    private CheckBox childCheckBox;

    @FXML
    private CheckBox seniorStudentCheckBox;

    @FXML
    private CheckBox adultCheckBox;

    @FXML
    public Label userNameLabel;

    @FXML
    public Label roleLabel;


    TicketDao ticketDao = Factory.INSTANCE.getTicketDao();
    UserDao userDao = Factory.INSTANCE.getUserDao();

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }

    @FXML
    public void initialize() {
        var principal = Auth.INSTANCE.getPrincipal();
        userNameLabel.setText(principal.getFirstName() + " " + principal.getLastName());
        roleLabel.setText("(" + principal.getRole().toString() + ")");

        childCheckBox.setOnAction(e -> {
            if (childCheckBox.isSelected()) {
                adultCheckBox.setSelected(false);
                seniorStudentCheckBox.setSelected(false);
            }
        });

        adultCheckBox.setOnAction(e -> {
            if (adultCheckBox.isSelected()) {
                childCheckBox.setSelected(false);
                seniorStudentCheckBox.setSelected(false);
            }
        });

        seniorStudentCheckBox.setOnAction(e -> {
            if (seniorStudentCheckBox.isSelected()) {
                childCheckBox.setSelected(false);
                adultCheckBox.setSelected(false);
            }
        });
    }

    @FXML
    public void sellTicket(ActionEvent event) {

        if (seniorStudentCheckBox.isSelected()) {
            this.createTicket("Študent/Senior", new BigDecimal("7.00"));
        }

        if (childCheckBox.isSelected()) {
            this.createTicket("Child", new BigDecimal("5.00"));
        }

        if (adultCheckBox.isSelected()) {
            this.createTicket("Dospelý", new BigDecimal("10.00"));
        }

        SceneManager.changeScene(event, "/sk.upjs.paz/MainView.fxml", "Hlavne okno");
    }

    private void createTicket(String type, BigDecimal price) {
        UserDao userDao = Factory.INSTANCE.getUserDao();
        Ticket ticket = new Ticket();

        User currentCashier = userDao.getById(Auth.INSTANCE.getPrincipal().getId());

        ticket.setType(type);
        ticket.setPrice(price);
        ticket.setPurchaseDateTime(LocalDateTime.now());
        ticket.setCashier(currentCashier);

        ticketDao.create(ticket);
    }

}

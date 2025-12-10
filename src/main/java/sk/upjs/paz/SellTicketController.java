package sk.upjs.paz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import sk.upjs.paz.ticket.Ticket;
import sk.upjs.paz.ticket.TicketDao;
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
    User loggedUser;


    TicketDao ticketDao=Factory.INSTANCE.getTicketDao();
    UserDao userDao=Factory.INSTANCE.getUserDao();

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.changeScene(event,"/sk.upjs.paz/MainView.fxml","Hlavne okno");
    }

    @FXML
    public void sellTicket(ActionEvent event) {
        User cashier = null;

        try {
            cashier = userDao.getById(1L);

            if (cashier == null) {
                var allUsers = userDao.getAll();
                if (!allUsers.isEmpty()) {
                    cashier = allUsers.get(0);
                }
            }
        } catch (Exception e) {
            System.out.println("Nepodarilo sa nájsť žiadneho usera v DB.");
        }

        if(seniorStudentCheckBox.isSelected()) {
            this.createTicket("Študent/Senior",new BigDecimal("7.00"),cashier);
        }

        if(childCheckBox.isSelected()) {
            this.createTicket("Child",new BigDecimal("5.00"),cashier);
        }

        if(adultCheckBox.isSelected()) {
            this.createTicket("Dospelý",new BigDecimal("10.00"),cashier);
        }

        SceneManager.changeScene(event,"/sk.upjs.paz/MainView.fxml","Hlavne okno");
    }

    private void createTicket(String type, BigDecimal price, User cashier) {
        Ticket ticket = new Ticket();

        ticket.setType(type);
        ticket.setPrice(price);
        ticket.setPurchaseDateTime(LocalDateTime.now());
        ticket.setCashier(cashier);

        ticketDao.create(ticket);
    }

}

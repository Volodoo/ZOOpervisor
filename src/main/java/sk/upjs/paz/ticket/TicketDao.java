package sk.upjs.paz.ticket;


import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

public interface TicketDao {

    List<Ticket> getAll();

    List<Ticket> getByCashier(long userId);

    List<Ticket> getAllSortedByPurchaseDateTime();

    List<Ticket> getTicketsBetween(LocalDate start, LocalDate end);

    Ticket getById(long id);

    Ticket create(Ticket ticket);

}

package sk.upjs.paz.ticket;


import sk.upjs.paz.exceptions.NotFoundException;

import java.util.List;

public interface TicketDao {

    List<Ticket> getAll();

    List<Ticket> getAllSortedByPurchaseTimestamp();

    List<Ticket> getAllSortedByCashier();

    Ticket getById(long id);

    Ticket create(Ticket ticket);

    Ticket update(Ticket ticket) throws NotFoundException, IllegalArgumentException;

    void delete(long id);
}

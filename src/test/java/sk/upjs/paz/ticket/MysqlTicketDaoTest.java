package sk.upjs.paz.ticket;

import org.junit.jupiter.api.BeforeEach;
import sk.upjs.paz.TestContainers;
import org.junit.jupiter.api.Test;
import sk.upjs.paz.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MysqlTicketDaoTest extends TestContainers {
    MysqlTicketDao dao;

    @BeforeEach
    public void setUp() {
        dao = new MysqlTicketDao(jdbc);
    }

    @Test
    void getAll() {
        List<Ticket> tickets = dao.getAll();
        assertEquals(84, tickets.size());

        assertEquals("Child", tickets.getFirst().getType());
        assertEquals("Student_Senior", tickets.get(10).getType());
        assertEquals("Adult", tickets.get(20).getType());
        assertEquals("Child", tickets.get(29).getType());
        assertEquals("Child", tickets.get(30).getType());
        assertEquals("Adult", tickets.get(50).getType());
        assertEquals("Child", tickets.get(60).getType());
        assertEquals("Child", tickets.get(70).getType());
        assertEquals("Student_Senior", tickets.get(80).getType());
        assertEquals("Adult", tickets.get(83).getType());
    }

    @Test
    void getByCashier() {
        List<Ticket> Janacashier = dao.getByCashier(2L);
        assertEquals(27, Janacashier.size());

        for (Ticket ticket : Janacashier) {
            assertEquals(2L, ticket.getCashier().getId());
        }


        List<Ticket> Tomascashier = dao.getByCashier(3L);
        assertEquals(33, Tomascashier.size());

        for (Ticket ticket : Tomascashier) {
            assertEquals(3L, ticket.getCashier().getId());
        }

        List<Ticket> Evacashier = dao.getByCashier(4L);
        assertEquals(24, Evacashier.size());

        for (Ticket ticket : Evacashier) {
            assertEquals(4L, ticket.getCashier().getId());
        }
    }

    @Test
    void getAllSortedByPurchaseDateTime() {
        List<Ticket> tickets = dao.getAllSortedByPurchaseDateTime();
        assertEquals(84, tickets.size());

        assertEquals("2025-12-06T09:00", tickets.get(0).getPurchaseDateTime().toString());
        assertEquals("2025-12-08T16:30", tickets.get(83).getPurchaseDateTime().toString());

        for (int i = 0; i < tickets.size() - 1; i++) {
            LocalDateTime current = tickets.get(i).getPurchaseDateTime();
            LocalDateTime next = tickets.get(i + 1).getPurchaseDateTime();

            assertFalse(current.isAfter(next),
                    "Chyba zoradenia na indexe " + i + ": " + current + " je po " + next);
        }

    }

    @Test
    void getTicketsBetween() {
        List<Ticket> tickets = dao.getTicketsBetween(
                LocalDate.of(2025, 12, 6),
                LocalDate.of(2025, 12, 8)
        );

        assertEquals(84, tickets.size());

        // prvý a posledný záznam
        assertEquals(
                LocalDateTime.of(2025, 12, 6, 9, 0),
                tickets.getFirst().getPurchaseDateTime()
        );

        assertEquals(
                LocalDateTime.of(2025, 12, 8, 16, 30),
                tickets.get(83).getPurchaseDateTime()
        );

        // kontrola zoradenia
        for (int i = 0; i < tickets.size() - 1; i++) {
            LocalDateTime current = tickets.get(i).getPurchaseDateTime();
            LocalDateTime next = tickets.get(i + 1).getPurchaseDateTime();

            assertFalse(current.isAfter(next),
                    "Chyba zoradenia na indexe " + i + ": " + current + " je po " + next);
        }

        // kontrola, že všetky dátumy sú v intervale
        for (Ticket ticket : tickets) {
            LocalDate date = ticket.getPurchaseDateTime().toLocalDate();
            assertFalse(date.isBefore(LocalDate.of(2025, 12, 6)));
            assertFalse(date.isAfter(LocalDate.of(2025, 12, 8)));
        }
    }


    @Test
    void getById() {
        Ticket t1 = dao.getById(1L);
        assertEquals(1, t1.getId());
        assertEquals("Child", t1.getType());
        assertEquals(2L, t1.getCashier().getId());

        Ticket t2 = dao.getById(2L);
        assertEquals(2L, t2.getId());
        assertEquals("Child", t2.getType());
        assertEquals(2L, t2.getCashier().getId());

        Ticket lastTicket = dao.getById(84L);
        assertEquals(84L, lastTicket.getId());
        assertEquals("Adult", lastTicket.getType());
        assertEquals(4L, lastTicket.getCashier().getId());
    }

    @Test
    void create() {
        User user = new User();
        user.setId(3L);
        Ticket newTicket = new Ticket();
        newTicket.setCashier(user);
        LocalDateTime time = LocalDateTime.now().withNano(0);
        newTicket.setPurchaseDateTime(time);
        newTicket.setType("Child");
        newTicket.setPrice(BigDecimal.valueOf(4));

        Ticket created = dao.create(newTicket);
        assertNotNull(created.getId());

        Ticket fromDb = dao.getById(created.getId());
        assertEquals(created.getId(), fromDb.getId());
        assertEquals(created.getCashier().getId(), fromDb.getCashier().getId());
        assertEquals(created.getPurchaseDateTime(), fromDb.getPurchaseDateTime());
        assertEquals(created.getType(), fromDb.getType());
        assertEquals(created.getPrice(), fromDb.getPrice());
    }
}

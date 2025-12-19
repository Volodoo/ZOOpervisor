package sk.upjs.paz.ticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.paz.TestContainers;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TicketServiceTest extends TestContainers {

    private TicketService ticketService;

    @BeforeEach
    public void setUp() {
        ticketService = new TicketService(new MysqlTicketDao(jdbc));
    }

    @Test
    void getTicketCountByCashierDay() {
        Map<String, Long> counts = ticketService.getTicketCountByCashier(
                LocalDate.of(2025, 12, 6),
                LocalDate.of(2025, 12, 8),
                1 // DAY
        );

        // Počet lístkov pre každého predajcu a deň
        assertEquals(8, counts.get("2 (06.12.2025)"));
        assertEquals(9, counts.get("3 (06.12.2025)"));
        assertEquals(9, counts.get("4 (06.12.2025)"));

        assertEquals(8, counts.get("2 (07.12.2025)"));
        assertEquals(10, counts.get("3 (07.12.2025)"));
        assertEquals(9, counts.get("4 (07.12.2025)"));

        assertEquals(11, counts.get("2 (08.12.2025)"));
        assertEquals(14, counts.get("3 (08.12.2025)"));
        assertEquals(6, counts.get("4 (08.12.2025)"));
    }

    @Test
    void getTicketCountByCashierWeek() {
        Map<String, Long> counts = ticketService.getTicketCountByCashier(
                LocalDate.of(2025, 12, 6),
                LocalDate.of(2025, 12, 8),
                2 // WEEK
        );

        assertEquals(11, counts.get("2 (Week 50 2025)"));
        assertEquals(14, counts.get("3 (Week 50 2025)"));
        assertEquals(6, counts.get("4 (Week 50 2025)"));
    }

    @Test
    void getTicketCountByCashierMonth() {
        Map<String, Long> counts = ticketService.getTicketCountByCashier(
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 31),
                3 // MONTH
        );

        // Súčet za mesiac 12, 2025
        assertEquals(27, counts.get("2 (12 2025)"));
        assertEquals(33, counts.get("3 (12 2025)"));
        assertEquals(24, counts.get("4 (12 2025)"));
    }

    @Test
    void getTicketCountByTypeDay() {
        Map<String, Long> counts = ticketService.getTicketCountByType(
                LocalDate.of(2025, 12, 6),
                LocalDate.of(2025, 12, 8),
                1
        );

        assertEquals(11, counts.get("Child (06.12.2025)"));
        assertEquals(7, counts.get("Adult (06.12.2025)"));
        assertEquals(8, counts.get("Student_Senior (06.12.2025)"));

        assertEquals(12, counts.get("Child (07.12.2025)"));
        assertEquals(7, counts.get("Adult (07.12.2025)"));
        assertEquals(8, counts.get("Student_Senior (07.12.2025)"));

        assertEquals(17, counts.get("Child (08.12.2025)"));
        assertEquals(6, counts.get("Adult (08.12.2025)"));
        assertEquals(8, counts.get("Student_Senior (08.12.2025)"));
    }

    @Test
    void getTicketCountByTypeWeek() {
        Map<String, Long> counts = ticketService.getTicketCountByType(
                LocalDate.of(2025, 12, 6),
                LocalDate.of(2025, 12, 8),
                2
        );

        assertEquals(17, counts.get("Child (Week 50 2025)"));
        assertEquals(6, counts.get("Adult (Week 50 2025)"));
        assertEquals(8, counts.get("Student_Senior (Week 50 2025)"));
    }

    @Test
    void getTicketCountByTypeMonth() {
        Map<String, Long> counts = ticketService.getTicketCountByType(
                LocalDate.of(2025, 12, 6),
                LocalDate.of(2025, 12, 8),
                3
        );

        assertEquals(40, counts.get("Child (12 2025)"));
        assertEquals(20, counts.get("Adult (12 2025)"));
        assertEquals(24, counts.get("Student_Senior (12 2025)"));
    }
}

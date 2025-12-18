package sk.upjs.paz.ticket;

import sk.upjs.paz.user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TicketService {

    private final TicketDao ticketDao;

    public TicketService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public Map<String, Long> getTicketCountByCashier(LocalDate start, LocalDate end, String period) {
        List<Ticket> tickets = ticketDao.getTicketsBetween(start, end);
        Map<String, Long> result = new HashMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (Ticket t : tickets) {
            String key;
            switch (period.toUpperCase()) {
                case "WEEK":
                    int weekNumber = t.getPurchaseDateTime().get(weekFields.weekOfWeekBasedYear());
                    int year = t.getPurchaseDateTime().getYear();
                    key = t.getCashier().getFirstName() + " " + t.getCashier().getLastName() + " (Week " + weekNumber + " " + year + ")";
                    break;
                case "MONTH":
                    key = t.getCashier().getFirstName() + " " + t.getCashier().getLastName() + " (" + t.getPurchaseDateTime().getMonth() + " " + t.getPurchaseDateTime().getYear() + ")";
                    break;
                default: // DAY
                    key = t.getCashier().getFirstName() + " " + t.getCashier().getLastName() + " (" + t.getPurchaseDateTime().format(dayFormatter) + ")";
                    break;
            }
            result.put(key, result.getOrDefault(key, 0L) + 1);
        }

        return result;
    }

    public Map<String, Long> getTicketCountByType(LocalDate start, LocalDate end, String period) {
        List<Ticket> tickets = ticketDao.getTicketsBetween(start, end);
        Map<String, Long> result = new HashMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (Ticket t : tickets) {
            String key;
            switch (period.toUpperCase()) {
                case "WEEK":
                    int weekNumber = t.getPurchaseDateTime().get(weekFields.weekOfWeekBasedYear());
                    int year = t.getPurchaseDateTime().getYear();
                    key = t.getType() + " (Week " + weekNumber + " " + year + ")";
                    break;
                case "MONTH":
                    key = t.getType() + " (" + t.getPurchaseDateTime().getMonth() + " " + t.getPurchaseDateTime().getYear() + ")";
                    break;
                default: // DAY
                    key = t.getType() + " (" + t.getPurchaseDateTime().format(dayFormatter) + ")";
                    break;
            }
            result.put(key, result.getOrDefault(key, 0L) + 1);
        }

        return result;
    }

}

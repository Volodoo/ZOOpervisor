package sk.upjs.paz.ticket;

import sk.upjs.paz.user.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class TicketService {

    private final TicketDao ticketDao;
    private final WeekFields weekFields = WeekFields.of(Locale.getDefault());

    public TicketService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public Map<String, Long> getTicketCountByCashier(LocalDate start, LocalDate end, String period) {
        List<Ticket> tickets = ticketDao.getTicketsBetween(start, end);

        Map<String, Long> result = new LinkedHashMap<>(); // <-- LinkedHashMap zachová poradie

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (Ticket t : tickets) {
            String key;
            switch (period.toUpperCase()) {
                case "WEEK":
                    int weekNumber = t.getPurchaseDateTime().get(weekFields.weekOfWeekBasedYear());
                    int year = t.getPurchaseDateTime().getYear();
                    key = t.getCashier().getFirstName() + " " + t.getCashier().getLastName()
                            + " (Week " + weekNumber + " " + year + ")";
                    break;
                case "MONTH":
                    key = t.getCashier().getFirstName() + " " + t.getCashier().getLastName()
                            + " (" + t.getPurchaseDateTime().getMonthValue() + " " + t.getPurchaseDateTime().getYear() + ")";
                    break;
                default: // DAY
                    key = t.getCashier().getFirstName() + " " + t.getCashier().getLastName()
                            + " (" + t.getPurchaseDateTime().format(dayFormatter) + ")";
                    break;
            }
            result.put(key, result.getOrDefault(key, 0L) + 1);
        }

        return result;
    }

    public Map<String, Long> getTicketCountByType(LocalDate start, LocalDate end, String period) {
        List<Ticket> tickets = ticketDao.getTicketsBetween(start, end);
        Map<String, Long> result = new LinkedHashMap<>(); // <-- zachová poradie

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Ticket t : tickets) {
            String key = createPeriodKey(t.getType(), t.getPurchaseDateTime().toLocalDate(), period, dayFormatter);
            result.put(key, result.getOrDefault(key, 0L) + 1);
        }

        return result;
    }

    private String createPeriodKey(String seriesName, LocalDate date, String period, DateTimeFormatter dayFormatter) {
        switch (period.toUpperCase()) {
            case "WEEK":
                int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                int year = date.getYear();
                return seriesName + " (Week " + weekNumber + " " + year + ")";
            case "MONTH":
                return seriesName + " (Month " + date.getMonthValue() + " " + date.getYear() + ")";
            default: // DAY
                return seriesName + " (" + date.format(dayFormatter) + ")";
        }
    }
}

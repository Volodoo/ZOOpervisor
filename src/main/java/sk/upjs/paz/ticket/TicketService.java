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

    public Map<String, Long> getTicketCountByCashier(LocalDate start, LocalDate end, int period) {
        List<Ticket> tickets = ticketDao.getTicketsBetween(start, end);
        Map<String, Long> result = new LinkedHashMap<>();

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Ticket t : tickets) {
            String key;
            String cashierName = t.getCashier().getFirstName() + " " + t.getCashier().getLastName();

            switch (period) {
                case 2: // WEEK
                    int weekNumber = t.getPurchaseDateTime().get(weekFields.weekOfWeekBasedYear());
                    int year = t.getPurchaseDateTime().getYear();
                    key = cashierName + " (Week " + weekNumber + " " + year + ")";
                    break;

                case 3: // MONTH
                    key = cashierName + " (" +
                            t.getPurchaseDateTime().getMonthValue() + " " +
                            t.getPurchaseDateTime().getYear() + ")";
                    break;

                case 1: // DAY
                default:
                    key = cashierName + " (" +
                            t.getPurchaseDateTime().format(dayFormatter) + ")";
                    break;
            }

            result.put(key, result.getOrDefault(key, 0L) + 1);
        }

        return result;
    }

    public Map<String, Long> getTicketCountByType(LocalDate start, LocalDate end, int period) {
        List<Ticket> tickets = ticketDao.getTicketsBetween(start, end);
        Map<String, Long> result = new LinkedHashMap<>();

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Ticket t : tickets) {
            LocalDate date = t.getPurchaseDateTime().toLocalDate();
            String key;

            switch (period) {
                case 2: // WEEK
                    int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                    int year = date.getYear();
                    key = t.getType() + " (Week " + weekNumber + " " + year + ")";
                    break;

                case 3: // MONTH
                    key = t.getType() + " (" +
                            date.getMonthValue() + " " +
                            date.getYear() + ")";
                    break;

                case 1: // DAY
                default:
                    key = t.getType() + " (" + date.format(dayFormatter) + ")";
                    break;
            }

            result.put(key, result.getOrDefault(key, 0L) + 1);
        }

        return result;
    }


}

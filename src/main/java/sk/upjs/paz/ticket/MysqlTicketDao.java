package sk.upjs.paz.ticket;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import sk.upjs.paz.exceptions.NotFoundException;
import sk.upjs.paz.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MysqlTicketDao implements TicketDao {
    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<Ticket>> resultSetExtractor = rs -> {
        var tickets = new ArrayList<Ticket>();
        var processedTickets = new HashMap<Long, Ticket>();
        var processedUsers = new HashMap<Long, User>();


        while (rs.next()) {
            var id = rs.getLong("id");
            var ticket = processedTickets.get(id);

            if (ticket == null) {
                ticket = Ticket.fromResultSet(rs);
                processedTickets.put(id, ticket);
                tickets.add(ticket);
            }

            var userId = rs.getLong("us_id");
            var user = processedUsers.get(userId);
            if (user == null) {
                user = User.fromResultSet(rs, "us_");
                processedUsers.put(userId, user);
            }

            if (ticket.getCashier() == null) {
                ticket.setCashier(user);
            }

        }

        return tickets;
    };

    private final String selectTicketQuery =
            "SELECT ti.id, ti.type, ti.purchase_date_time, ti.price, " +
                    "us.id AS us_id, us.first_name AS us_first_name, us.last_name AS us_last_name, us.gender AS us_gender,us.birth_day AS us_birth_day, us.role AS us_role, us.email AS us_email, us.password AS us_password " +
                    "FROM ticket ti " +
                    "LEFT JOIN user us ON ti.user_id = us.id";


    public MysqlTicketDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Ticket> getAll() {
        return jdbcOperations.query(selectTicketQuery, resultSetExtractor);
    }

    @Override
    public List<Ticket> getByCashier(long userId) {
        String selectTicketByCashierQuery = selectTicketQuery + " WHERE user_id = ?";
        return jdbcOperations.query(selectTicketByCashierQuery, resultSetExtractor, userId);
    }

    @Override
    public List<Ticket> getAllSortedByPurchaseDateTime() {
        String selectTicketSortedByPurchaseTimestamp = selectTicketQuery + " ORDER BY ti.purchase_date_time ASC";
        return jdbcOperations.query(selectTicketSortedByPurchaseTimestamp, resultSetExtractor);
    }

    @Override
    public List<Ticket> getTicketsBetween(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay(); // 00:00:00
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        String getTicketsBetweenQuery = selectTicketQuery
                + " WHERE ti.purchase_date_time BETWEEN ? AND ?"
                + " ORDER BY ti.purchase_date_time ASC";

        return jdbcOperations.query(getTicketsBetweenQuery, resultSetExtractor, startDateTime, endDateTime);
    }


    @Override
    public Ticket getById(long id) {
        String SelectTicketByIdQuery = selectTicketQuery + " WHERE ti.id = ?";

        var tickets = jdbcOperations.query(SelectTicketByIdQuery, resultSetExtractor, id);
        if (tickets == null || tickets.isEmpty()) {
            throw new NotFoundException("Ticket with id " + id + " not found.");
        }
        return tickets.get(0);
    }

    @Override
    public Ticket create(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket is null.");
        }

        if (ticket.getId() != null) {
            throw new IllegalArgumentException("Ticket id is already set.");
        }

        var keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO ticket (type, purchase_date_time, price, user_id) VALUES (?, ?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, ticket.getType());
            ps.setObject(2, ticket.getPurchaseDateTime());
            ps.setBigDecimal(3, ticket.getPrice());
            ps.setLong(4, ticket.getCashier().getId());
            return ps;
        }, keyHolder);

        var id = keyHolder.getKey().longValue();

        return getById(id);
    }
}

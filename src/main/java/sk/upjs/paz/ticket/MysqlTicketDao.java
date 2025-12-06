package sk.upjs.paz.ticket;

import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.paz.user.UserDao;

public class MysqlTicketDao implements TicketDao {
    public MysqlTicketDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
    }
}

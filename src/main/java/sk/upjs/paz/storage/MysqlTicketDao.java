package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlTicketDao implements TicketDao {
    public MysqlTicketDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
    }
}

package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlTasklDao implements TaskDao {
    public MysqlTasklDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
    }
}

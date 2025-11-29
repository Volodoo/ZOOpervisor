package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlAnimalDao implements AnimalDao {
    public MysqlAnimalDao(JdbcTemplate jdbcTemplate, UserDao userDao) {
    }
}

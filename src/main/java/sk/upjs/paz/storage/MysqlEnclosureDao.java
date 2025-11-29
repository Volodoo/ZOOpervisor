package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcTemplate;

public class MysqlEnclosureDao implements EnclosureDao {

    public MysqlEnclosureDao(
            JdbcTemplate jdbcTemplate,
            AnimalDao animalDao) {
    }
}

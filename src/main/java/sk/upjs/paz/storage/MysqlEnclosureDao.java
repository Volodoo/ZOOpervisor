package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import sk.upjs.paz.entity.Animal;
import sk.upjs.paz.entity.Enclosure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MysqlEnclosureDao implements EnclosureDao {

    public MysqlEnclosureDao(JdbcTemplate jdbcTemplate) {

    }

    @Override
    public List<Enclosure> getAll() {
        return List.of();
    }
}

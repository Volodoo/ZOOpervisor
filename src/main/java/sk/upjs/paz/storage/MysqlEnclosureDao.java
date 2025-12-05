package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.paz.entity.Enclosure;


import java.util.List;

public class MysqlEnclosureDao implements EnclosureDao {


    private final JdbcOperations jdbcOperations;


    public MysqlEnclosureDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


    @Override
    public List<Enclosure> getAll() {
        return List.of();
    }

    @Override
    public Enclosure getById(long id) {
        return null;
    }

    @Override
    public Enclosure create(Enclosure enclosure) {
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Enclosure update(Enclosure enclosure) throws NotFoundException, IllegalArgumentException {
        return null;
    }

    public List<Enclosure> getAllSortedByZone() {
        return List.of();
    }
}

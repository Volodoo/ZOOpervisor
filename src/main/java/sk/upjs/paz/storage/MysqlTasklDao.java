package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcOperations;
import sk.upjs.paz.entity.Enclosure;
import sk.upjs.paz.entity.Task;

import java.util.List;

public class MysqlTasklDao implements TaskDao {
    private final JdbcOperations jdbcOperations;

    public MysqlTasklDao(JdbcOperations jdbcOperations) { this.jdbcOperations = jdbcOperations; }

    @Override
    public List<Task> getAll() {
        return List.of();
    }

    @Override
    public Task getById(long id) {
        return null;
    }

    @Override
    public Task create(Task task) {
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Task update(Task task) throws NotFoundException, IllegalArgumentException {
        return null;
    }

    @Override
    public List<Task> getAllSortedByDate() {
        return List.of();
    }

    @Override
    public List<Task> getAllSortedByEnclosure() {
        return List.of();
    }
}

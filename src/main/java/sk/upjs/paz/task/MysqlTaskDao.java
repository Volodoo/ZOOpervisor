package sk.upjs.paz.task;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import sk.upjs.paz.exceptions.NotFoundException;
import sk.upjs.paz.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MysqlTaskDao implements TaskDao {
    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<Task>> resultSetExtractor = rs -> {
        var tasks = new ArrayList<Task>();
        var processedTasks = new HashMap<Long, Task>();
        var processedUsers = new HashMap<Long, User>();


        while (rs.next()) {
            var id = rs.getLong("id");
            var task = processedTasks.get(id);

            if (task == null) {
                task = Task.fromResultSet(rs);
                processedTasks.put(id, task);
                tasks.add(task);
            }

            var userId = rs.getLong("us_id");
            var user = processedUsers.get(userId);
            if (user == null) {
                user = User.fromResultSet(rs, "us_");
                processedUsers.put(userId, user);
            }

            if (task.getUser() == null) {
                task.setUser(user);
            }

        }

        return tasks;
    };

    private final String selectTaskQuery =
            "SELECT ta.id, ta.name, ta.description, ta.deadline, " +
                    "us.id AS us_id, us.first_name AS us_first_name, us.last_name AS us_last_name, us.gender AS us_gender, us.birth_day AS us_birth_day, us.role AS us_role, us.email AS us_email, us.password AS us_password " +
                    "FROM task ta " +
                    "LEFT JOIN user us ON ta.user_id = us.id";

    public MysqlTaskDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


    @Override
    public List<Task> getAll() {
        return jdbcOperations.query(selectTaskQuery, resultSetExtractor);
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
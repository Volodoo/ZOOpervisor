package sk.upjs.paz.task;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.enclosure.Enclosure;
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
        var processedAnimals = new HashMap<Long, Animal>();
        var processedEnclosures = new HashMap<Long, Enclosure>();

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

            var animalId = rs.getLong("an_id");
            var animal = processedAnimals.get(animalId);
            if (animal == null) {
                animal = Animal.fromResultSet(rs, "an_");
                processedAnimals.put(animalId, animal);
            }

            if (task.getAnimals() == null) {
                task.getAnimals().add(animal);
            }


            var enclosureId = rs.getLong("en_id");
            var enclosure = processedEnclosures.get(enclosureId);
            if (enclosure == null) {
                enclosure = Enclosure.fromResultSet(rs, "en_");
                processedEnclosures.put(enclosureId, enclosure);
            }


            if (task.getEnclosures() == null) {
                task.getEnclosures().add(enclosure);
            }
        }


        return tasks;
    };

    private final String selectTaskQuery =
            "SELECT ta.id, ta.name, ta.description, ta.deadline, " +
                    "us.id AS us_id, us.first_name AS us_first_name, us.last_name AS us_last_name, us.gender AS us_gender,us.birth_day AS us_birth_day, us.role AS us_role, us.email AS us_email, us.password AS us_password, " +
                    "an.id AS an_id, an.nickname AS an_nickname, an.species AS an_species, an.sex AS an_sex, an.birth_day AS an_birth_day, an.last_check AS an_last_check, " +
                    "en.id AS en_id, en.name AS en_name, en.zone AS en_zone, en.last_maintainance AS en_last_maintainance " +
                    "FROM task ta " +
                    "LEFT JOIN user us ON ta.user_id = us.id " +
                    "LEFT JOIN task_has_animal tha ON ta.id = tha.task_id " +
                    "LEFT JOIN animal an ON tha.animal_id = an.id " +
                    "LEFT JOIN task_has_enclosure the ON ta.id = the.task_id " +
                    "LEFT JOIN enclosure en ON the.enclosure_id = en.id";


    public MysqlTaskDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Task> getAll() {
        return jdbcOperations.query(selectTaskQuery, resultSetExtractor);
    }

    @Override
    public List<Task> getAllSortedByDeadline() {
        String selectTasksSortedByDateTimeQuery = selectTaskQuery + " ORDER BY ta.deadline ASC";
        return jdbcOperations.query(selectTasksSortedByDateTimeQuery, resultSetExtractor);
    }

    @Override
    public Task getById(long id) {
        String SelectTaskByIdQuery = selectTaskQuery + " WHERE ta.id = ?";

        var tasks = jdbcOperations.query(SelectTaskByIdQuery, resultSetExtractor, id);
        if (tasks == null || tasks.isEmpty()) {
            throw new NotFoundException("Task with id " + id + " not found.");
        }
        return tasks.get(0);
    }

    @Override
    public Task create(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task is null.");
        }

        if (task.getId() != null) {
            throw new IllegalArgumentException("Task id is already set.");
        }

        var keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO task (name, description, deadline, user_id) VALUES (?, ?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, task.getName());
            ps.setString(2, task.getDescription());
            ps.setObject(3, task.getDeadline());
            ps.setLong(4, task.getUser().getId());
            return ps;
        }, keyHolder);

        var id = keyHolder.getKey().longValue();

        if (task.getAnimals() != null) {
            for (var animal : task.getAnimals()) {
                jdbcOperations.update(
                        "INSERT INTO task_has_animal (task_id, animal_id) VALUES (?, ?)",
                        id,
                        animal.getId()
                );
            }
        }

        if (task.getEnclosures() != null) {
            for (var enclosure : task.getEnclosures()) {
                jdbcOperations.update(
                        "INSERT INTO task_has_enclosure (task_id, enclosure_id) VALUES (?, ?)",
                        id,
                        enclosure.getId()
                );
            }
        }

        return getById(id);
    }

    @Override
    public Task update(Task task) throws NotFoundException, IllegalArgumentException {
        if (task == null) {
            throw new IllegalArgumentException("Task is null.");
        }

        if (task.getId() == null) {
            throw new IllegalArgumentException("Task id is not set.");
        }

        jdbcOperations.update(
                "UPDATE task SET name = ?, description = ?, deadline = ?, user_id = ? WHERE id = ?",
                task.getName(),
                task.getDescription(),
                task.getDeadline(),
                task.getUser().getId(),

                task.getId()
        );

        jdbcOperations.update(
                "DELETE FROM task_has_animal WHERE task_id = ?",
                task.getId()
        );

        jdbcOperations.update(
                "DELETE FROM task_has_enclosure WHERE task_id = ?",
                task.getId()
        );

        for (var animal : task.getAnimals()) {
            jdbcOperations.update(
                    "INSERT INTO task_has_animal (task_id, animal_id) VALUES (?, ?)",
                    task.getId(),
                    animal.getId()
            );
        }

        for (var enclosure : task.getEnclosures()) {
            jdbcOperations.update(
                    "INSERT INTO task_has_enclosure (task_id, enclosure_id) VALUES (?, ?)",
                    task.getId(),
                    enclosure.getId()
            );
        }

        return getById(task.getId());
    }

    @Override
    public void delete(long id) {
        jdbcOperations.update("DELETE FROM task_has_animal WHERE task_id = ?", id);
        jdbcOperations.update("DELETE FROM task_has_enclosure WHERE task_id = ?", id);
        jdbcOperations.update("DELETE FROM task WHERE id = ?", id);
    }


}

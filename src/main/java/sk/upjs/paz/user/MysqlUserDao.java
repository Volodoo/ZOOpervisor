package sk.upjs.paz.user;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import sk.upjs.paz.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MysqlUserDao implements UserDao {

    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<User>> resultSetExtractor = rs -> {
        var users = new ArrayList<User>();
        var processedUsers = new HashMap<Long, User>();


        while (rs.next()) {
            var id = rs.getLong("id");
            var user = processedUsers.get(id);

            if (user == null) {
                user = User.fromResultSet(rs);
                processedUsers.put(id, user);
                users.add(user);
            }

        }

        return users;
    };


    private final String selectUserQuery = "SELECT id, first_name, last_name, gender, birth_day, role, email, password FROM user";

    public MysqlUserDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }


    @Override
    public List<User> getAll() {
        return jdbcOperations.query(selectUserQuery, resultSetExtractor);
    }

    @Override
    public User getById(long id) {
        var users = jdbcOperations.query(selectUserQuery + " WHERE id = ?", resultSetExtractor, id);
        if (users == null || users.isEmpty()) {
            throw new NotFoundException("User with id " + id + " not found.");
        }
        return users.get(0);
    }


    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null.");
        }

        if (user.getId() != null) {
            throw new IllegalArgumentException("User id is already set.");
        }

        var keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO user (first_name, last_name, gender, birth_day, role, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getGender().toString());
            ps.setObject(4, user.getBirthDay());
            ps.setString(5, user.getRole().toString());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getPassword());

            return ps;
        }, keyHolder);

        var id = keyHolder.getKey().longValue();
        System.out.println(id);
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) throws NotFoundException, IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("Attendance is null.");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException("Attendance id is not set.");
        }

        jdbcOperations.update(
                "UPDATE user SET first_name = ?, last_name = ?, gender = ?, birth_day = ?, role = ?, email = ?, password = ? WHERE id = ?",
                user.getFirstName(),
                user.getLastName(),
                user.getGender().toString(),
                user.getBirthDay(),
                user.getRole().toString(),
                user.getEmail(),
                user.getPassword(),
                user.getId()
        );

        return user;
    }

    @Override
    public void delete(long id) {
        jdbcOperations.update("DELETE FROM user WHERE id = ?", id);
    }


}

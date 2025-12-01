package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import sk.upjs.paz.entity.User;

import java.util.ArrayList;
import java.util.List;

public class MysqlUserDao implements UserDao {

    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<User>> resultSetExtractor = rs -> {
        var users = new ArrayList<User>();
        while (rs.next()) {
            var user = User.fromResultSet(rs);
            users.add(user);
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
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public User update(User user) throws sk.upjs.paz.storage.NotFoundException, IllegalArgumentException {
        return null;
    }


}

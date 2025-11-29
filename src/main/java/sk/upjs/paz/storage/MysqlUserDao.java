package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import sk.upjs.paz.entity.Gender;
import sk.upjs.paz.entity.Role;
import sk.upjs.paz.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MysqlUserDao implements UserDao {

    private JdbcTemplate jdbcTemplate;

    public MysqlUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long id = rs.getLong("id");
            String firstName = rs.getString("first_name");
            String surname = rs.getString("last_name");
            String genderString = rs.getString("gender");
            Gender gender = Gender.valueOf(genderString);
            LocalDate birthDay = rs.getDate("birth_day").toLocalDate();
            String roleString = rs.getString("role");
            Role role = Role.valueOf(roleString);
            String email = rs.getString("email");
            String password = rs.getString("password");


            User user = new User(id, firstName, surname, gender, birthDay, role, email, password);
            return user;
        }
    };

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM user", rowMapper);
        return users;
    }

    @Override
    public User create(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement prepared = con.prepareStatement("INSERT INTO user " +
                                "(first_name, last_name, gender, birth_day, role, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                prepared.setString(1, user.getFirstName());
                prepared.setString(2, user.getLastName());
                prepared.setString(3, user.getGender().toString());
                prepared.setDate(4, java.sql.Date.valueOf(user.getBirthDay()));
                prepared.setString(5, user.getRole().toString());
                prepared.setString(6, user.getEmail());
                prepared.setString(7, user.getPassword());
                return prepared;
            }
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        user.setId(id);
        return user;
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM user WHERE id = ?", id);
    }

    @Override
    public User update(User user) throws NotFoundException, IllegalArgumentException {
        if (user.getId() == null) {
            throw new IllegalArgumentException("user id must be set");
        }
        int updated = jdbcTemplate.update("UPDATE user SET first_name=?, last_name=?, gender=?, birth_day=?, role=?, email=?, password=? WHERE id=?",
                user.getFirstName(), user.getLastName(), user.getGender().toString(),
                user.getBirthDay(), user.getRole(), user.getEmail(), user.getPassword(), user.getId());
        if (updated == 0) {
            throw new NotFoundException("user with id " + user.getId() + " not found");
        }
        return user;
    }

    @Override
    public User getById(Long userId) {
        List<User> users = jdbcTemplate.query("SELECT * FROM user WHERE id = ?", rowMapper, userId);
        if (users == null || users.isEmpty()) throw new NotFoundException("User with id " + userId + " not found");
        return users.get(0);
    }
}

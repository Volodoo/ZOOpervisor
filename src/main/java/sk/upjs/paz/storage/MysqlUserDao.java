package sk.upjs.paz.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.paz.entity.User;

import java.util.List;

public class MysqlUserDao implements UserDao {
    public MysqlUserDao(JdbcTemplate jdbcTemplate) {
    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public User update(User user) throws NotFoundException, IllegalArgumentException {
        return null;
    }
}

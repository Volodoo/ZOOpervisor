package sk.upjs.paz.storage;

import sk.upjs.paz.entity.User;

import java.util.List;

public interface UserDao {
    List<User> getAll();
    User create(User user);
    void delete(long id);
    User update(User user) throws NotFoundException, IllegalArgumentException;
}

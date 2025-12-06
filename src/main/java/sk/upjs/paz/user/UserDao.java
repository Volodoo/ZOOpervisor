package sk.upjs.paz.user;

import sk.upjs.paz.exceptions.NotFoundException;

import java.util.List;

public interface UserDao {
    List<User> getAll();

    User getById(long id);

    User create(User user);

    void delete(long id);

    User update(User user) throws NotFoundException, IllegalArgumentException;

    User verify(String email, String password);

}

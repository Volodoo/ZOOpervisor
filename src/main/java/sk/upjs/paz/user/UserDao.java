package sk.upjs.paz.user;

import sk.upjs.paz.exceptions.NotFoundException;

import java.util.List;

public interface UserDao {
    List<User> getAll();

    List<User> getByRole(Role role);

    User getById(long id);

    User create(User user);

    User update(User user) throws NotFoundException, IllegalArgumentException;

}

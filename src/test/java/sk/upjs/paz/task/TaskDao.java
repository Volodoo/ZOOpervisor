package sk.upjs.paz.task;

import java.util.List;

public interface TaskDao {
    List<Task> getAll();

    List<Task> getAllSortedByDeadline();

    List<Task> getByUser(long userId);

    Task getById(long id);

    Task create(Task task);

    Task update(Task task) throws NotFoundException, IllegalArgumentException;

    void delete(long id);
}

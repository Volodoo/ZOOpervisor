package sk.upjs.paz.storage;

import sk.upjs.paz.entity.Task;

import java.util.List;

public interface TaskDao {
    List<Task> getAll();

    Task getById(long id);

    Task create(Task task);

    void delete(long id);

    Task update(Task task) throws NotFoundException, IllegalArgumentException;

    List<Task> getAllSortedByDate();

    List<Task> getAllSortedByEnclosure();
}

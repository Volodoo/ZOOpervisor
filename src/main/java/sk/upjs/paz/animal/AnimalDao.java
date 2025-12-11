package sk.upjs.paz.animal;

import sk.upjs.paz.exceptions.NotFoundException;

import java.util.List;

public interface AnimalDao {
    List<Animal> getAll();

    List<Animal> getAllSortedBySpecies();

    List<Animal> getByStatus(Status status);

    Animal getById(long id);

    Animal create(Animal animal);

    Animal update(Animal animal) throws NotFoundException, IllegalArgumentException;

    void delete(long id);
}

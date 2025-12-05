package sk.upjs.paz.storage;

import sk.upjs.paz.entity.Animal;

import java.util.List;

public interface AnimalDao {
    List<Animal> getAll();

    Animal getById(long id);

    Animal create(Animal animal);

    void delete(long id);

    Animal update(Animal animal) throws NotFoundException, IllegalArgumentException;

    List<Animal> getAllSortedBySpecies();

}

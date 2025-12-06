package sk.upjs.paz.enclosure;

import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.exceptions.NotFoundException;

import java.util.List;
import java.util.Set;

public interface EnclosureDao {
    List<Enclosure> getAll();

    Set<Animal> getAnimals(long id);

    Integer getAnimalsCount(long id);

    Enclosure getById(long id);

    Enclosure create(Enclosure enclosure);

    void delete(long id);

    Enclosure update(Enclosure enclosure) throws NotFoundException, IllegalArgumentException;
}

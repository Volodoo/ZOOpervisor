package sk.upjs.paz.enclosure;

import sk.upjs.paz.exceptions.NotFoundException;

import java.util.List;

public interface EnclosureDao {
    List<Enclosure> getAll();

    Enclosure getById(long id);

    Enclosure create(Enclosure enclosure);

    void delete(long id);

    Enclosure update(Enclosure enclosure) throws NotFoundException, IllegalArgumentException;
}

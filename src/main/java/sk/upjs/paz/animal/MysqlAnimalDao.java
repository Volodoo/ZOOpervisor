package sk.upjs.paz.animal;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MysqlAnimalDao implements AnimalDao {

    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<Animal>> resultSetExtractor = rs -> {
        var animals = new ArrayList<Animal>();
        var processedAnimals = new HashMap<Long, Animal>();
        var processedEnclosures = new HashMap<Long, Enclosure>();


        while (rs.next()) {
            var id = rs.getLong("id");
            var animal = processedAnimals.get(id);

            if (animal == null) {
                animal = Animal.fromResultSet(rs);
                processedAnimals.put(id, animal);
                animals.add(animal);
            }

            var enclosureId = rs.getLong("en_id");
            var enclosure = processedEnclosures.get(enclosureId);
            if (enclosure == null) {
                enclosure = Enclosure.fromResultSet(rs, "en_");
                processedEnclosures.put(enclosureId, enclosure);
            }

            if (animal.getEnclosure() == null) {
                animal.setEnclosure(enclosure);
            }

        }

        return animals;
    };

    private final String selectAnimalQuery =
            "SELECT an.id, an.nickname, an.species, an.sex, an.birth_day, an.last_check, " +
                    "en.id AS en_id, en.name AS en_name, en.zone AS en_zone, en.last_maintainance AS en_last_maintainance " +
                    "FROM animal an " +
                    "LEFT JOIN enclosure en ON an.enclosure_id = en.id";


    public MysqlAnimalDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Animal> getAll() {
        return jdbcOperations.query(selectAnimalQuery, resultSetExtractor);
    }

    @Override
    public Animal getById(long id) {
        String SelectAnimalByIdQuery = selectAnimalQuery + " WHERE an.id = ?";

        var tasks = jdbcOperations.query(SelectAnimalByIdQuery, resultSetExtractor, id);
        if (tasks == null || tasks.isEmpty()) {
            throw new NotFoundException("Animal with id " + id + " not found.");
        }
        return tasks.get(0);

    }

    @Override
    public Animal create(Animal animal) {
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Animal update(Animal animal) throws NotFoundException, IllegalArgumentException {
        return null;
    }

    public List<Animal> getAllSortedBySpecies() {
        return List.of();
    }


}

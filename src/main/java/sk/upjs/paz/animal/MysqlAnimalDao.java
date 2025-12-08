package sk.upjs.paz.animal;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
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
            "SELECT an.id, an.nickname, an.species, an.sex, an.birth_day, an.last_check, an.status, " +
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
    public List<Animal> getAllSortedBySpecies() {
        String selectAnimalsSortedBySpeciesQuery = selectAnimalQuery + " ORDER BY en.zone, an.species";

        return jdbcOperations.query(selectAnimalsSortedBySpeciesQuery, resultSetExtractor);
    }

    @Override
    public Animal getById(long id) {
        String SelectAnimalByIdQuery = selectAnimalQuery + " WHERE an.id = ?";

        var animals = jdbcOperations.query(SelectAnimalByIdQuery, resultSetExtractor, id);
        if (animals == null || animals.isEmpty()) {
            throw new NotFoundException("Animal with id " + id + " not found.");
        }
        return animals.get(0);
    }

    @Override
    public Animal create(Animal animal) {
        if (animal == null) {
            throw new IllegalArgumentException("Animal is null.");
        }

        if (animal.getId() != null) {
            throw new IllegalArgumentException("Animal id is already set.");
        }

        var keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, animal.getNickname());
            ps.setString(2, animal.getSpecies());
            ps.setString(3, animal.getSex().toString());
            ps.setObject(4, animal.getBirthDay());
            ps.setObject(5, animal.getLastCheck());
            ps.setString(6, animal.getStatus().toString());
            ps.setLong(7, animal.getEnclosure().getId());
            return ps;
        }, keyHolder);

        var id = keyHolder.getKey().longValue();


        return getById(id);
    }

    @Override
    public Animal update(Animal animal) throws NotFoundException, IllegalArgumentException {
        if (animal == null) {
            throw new IllegalArgumentException("Animal is null.");
        }

        if (animal.getId() == null) {
            throw new IllegalArgumentException("Animal id is not set.");
        }

        jdbcOperations.update(
                "UPDATE animal SET nickname = ?, species = ?, sex = ?, birth_day = ?, last_check = ?, status = ?, enclosure_id = ? WHERE id = ?",
                animal.getNickname(),
                animal.getSpecies(),
                animal.getSex().toString(),
                animal.getBirthDay(),
                animal.getLastCheck(),
                animal.getStatus().toString(),
                animal.getEnclosure().getId(),

                animal.getId()
        );

        return getById(animal.getId());
    }

    @Override
    public void delete(long id) {
        jdbcOperations.update("DELETE FROM animal WHERE id = ?", id);
    }
}

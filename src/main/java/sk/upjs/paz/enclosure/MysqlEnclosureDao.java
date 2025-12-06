package sk.upjs.paz.enclosure;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MysqlEnclosureDao implements EnclosureDao {

    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<Enclosure>> resultSetExtractor = rs -> {
        var enclosures = new ArrayList<Enclosure>();
        while (rs.next()) {
            var enclosure = Enclosure.fromResultSet(rs);
            int animalCount = rs.getInt("animal_count");
            enclosure.setAnimalCount(animalCount);
            enclosures.add(enclosure);
        }
        return enclosures;
    };

    private final String selectEnclosureQuery =
            "SELECT en.id, en.name, en.zone, en.last_maintainance, " +
                    "COUNT(an.id) AS animal_count " +
                    "FROM enclosure en " +
                    "LEFT JOIN animal an ON an.enclosure_id = en.id " +
                    "GROUP BY en.id";

    public MysqlEnclosureDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Enclosure> getAll() {
        return jdbcOperations.query(selectEnclosureQuery, resultSetExtractor);
    }

    @Override
    public Set<Animal> getAnimals(long id) {
        String selectAnimalsQuery = "SELECT an.id, an.nickname, an.species, an.sex, an.birth_day, an.last_check " +
                "FROM animal an " +
                "WHERE an.enclosure_id = ?";

        Set<Animal> animals = new HashSet<>(jdbcOperations.query(selectAnimalsQuery, (rs, rowNum) -> Animal.fromResultSet(rs), id));
        return animals;
    }


    @Override
    public Enclosure getById(long id) {
        String selectEnclosureByIdQuery =
                "SELECT en.id, en.name, en.zone, en.last_maintainance, " +
                        "COUNT(an.id) AS animal_count " +
                        "FROM enclosure en " +
                        "LEFT JOIN animal an ON an.enclosure_id = en.id " +
                        "WHERE en.id = ? " +
                        "GROUP BY en.id";

        List<Enclosure> enclosures = jdbcOperations.query(selectEnclosureByIdQuery, resultSetExtractor, id);

        if (enclosures.isEmpty()) {
            return null;  // Ak neexistuje žiadne enclosure s daným ID, vrátime null.
        }

        return enclosures.get(0);
    }

    @Override
    public Enclosure create(Enclosure enclosure) {
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Enclosure update(Enclosure enclosure) throws NotFoundException, IllegalArgumentException {
        return null;
    }

    public List<Enclosure> getAllSortedByZone() {
        return List.of();
    }
}

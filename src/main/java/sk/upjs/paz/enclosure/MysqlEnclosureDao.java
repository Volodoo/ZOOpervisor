package sk.upjs.paz.enclosure;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.exceptions.NotFoundException;
import sk.upjs.paz.user.User;

import java.util.*;

public class MysqlEnclosureDao implements EnclosureDao {

    private final JdbcOperations jdbcOperations;

    private final ResultSetExtractor<List<Enclosure>> resultSetExtractor = rs -> {
        var enclosures = new ArrayList<Enclosure>();
        var processedEnclosures = new HashMap<Long, Enclosure>();

        while (rs.next()) {
            var id = rs.getLong("id");
            var enclosure = processedEnclosures.get(id);

            if (enclosure == null) {
                enclosure = Enclosure.fromResultSet(rs);
                processedEnclosures.put(id, enclosure);
                enclosures.add(enclosure);
            }

        }
        return enclosures;
    };

    private final String selectEnclosureQuery =
            "SELECT en.id, en.name, en.zone, en.last_maintainance " +
                    "FROM enclosure en";

    public MysqlEnclosureDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<Enclosure> getAll() {
        List<Enclosure> enclosures = jdbcOperations.query(selectEnclosureQuery, resultSetExtractor);
        return enclosures;
    }

    @Override
    public Set<Animal> getAnimals(long id) {
        String selectAnimalsQuery =
                "SELECT an.id, an.nickname, an.species, an.sex, an.birth_day, an.last_check " +
                        "FROM animal an " +
                        "WHERE an.enclosure_id = ?";

        Set<Animal> animals = new HashSet<>(jdbcOperations.query(selectAnimalsQuery, (rs, rowNum) -> Animal.fromResultSet(rs), id));
        return animals;
    }

    @Override
    public Integer getAnimalsCount(long id) {
        String selectAnimalsCountQuery =
                "SELECT COUNT(*) " +
                        "FROM animal an " +
                        "WHERE an.enclosure_id = ?";

        return jdbcOperations.queryForObject(selectAnimalsCountQuery, Integer.class, id);
    }

    @Override
    public Enclosure getById(long id) {
        String selectEnclosureByIdQuery = selectEnclosureQuery + " WHERE id = ?";

        var enclosures = jdbcOperations.query(selectEnclosureByIdQuery, resultSetExtractor, id);
        if (enclosures == null || enclosures.isEmpty()) {
            throw new NotFoundException("Enclosure with id " + id + " not found.");
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

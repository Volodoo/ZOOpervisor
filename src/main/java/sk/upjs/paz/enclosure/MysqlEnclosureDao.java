package sk.upjs.paz.enclosure;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.exceptions.NotFoundException;

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

            enclosure.setAnimalsCount(getAnimalsCount(id));

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
        return jdbcOperations.query(selectEnclosureQuery, resultSetExtractor);
    }

    @Override
    public List<Enclosure> getAllSortedByZone() {
        String selectEnclosuresSortedBySpeciesQuery = selectEnclosureQuery + " ORDER BY zone";
        return jdbcOperations.query(selectEnclosuresSortedBySpeciesQuery, resultSetExtractor);
    }

    @Override
    public List<Enclosure> getByZone(String zone) {
        String selectEnclosuresByZoneQuery = selectEnclosureQuery + " WHERE zone = ?";
        return jdbcOperations.query(selectEnclosuresByZoneQuery, resultSetExtractor, zone);
    }


    @Override
    public Set<Animal> getAnimals(long id) {
        String selectAnimalsQuery =
                "SELECT an.id, an.nickname, an.species, an.sex, an.birth_day, an.last_check, an.status " +
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
        if (enclosure == null) {
            throw new IllegalArgumentException("Enclosure is null.");
        }

        if (enclosure.getId() != null) {
            throw new IllegalArgumentException("Enclosure id is already set.");
        }

        var keyHolder = new GeneratedKeyHolder();
        jdbcOperations.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO enclosure (name, zone, last_maintainance) VALUES (?, ?, ?)",
                    new String[]{"id"}
            );
            ps.setString(1, enclosure.getName());
            ps.setString(2, enclosure.getZone());
            ps.setObject(3, enclosure.getLastMaintainance());

            return ps;
        }, keyHolder);

        var id = keyHolder.getKey().longValue();
        System.out.println(id);
        enclosure.setId(id);
        return enclosure;
    }

    @Override
    public void delete(long id) {
        jdbcOperations.update("DELETE FROM enclosure WHERE id = ?", id);
    }

    @Override
    public Enclosure update(Enclosure enclosure) throws NotFoundException, IllegalArgumentException {
        if (enclosure == null) {
            throw new IllegalArgumentException("Enclosure is null.");
        }

        if (enclosure.getId() == null) {
            throw new IllegalArgumentException("Enclosure id is not set.");
        }

        jdbcOperations.update(
                "UPDATE enclosure SET name = ?, zone = ?, last_maintainance = ? WHERE id = ?",
                enclosure.getName(),
                enclosure.getZone(),
                enclosure.getLastMaintainance(),

                enclosure.getId()
        );

        return enclosure;
    }
}

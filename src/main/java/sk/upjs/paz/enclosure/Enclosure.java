package sk.upjs.paz.enclosure;

import lombok.Data;
import sk.upjs.paz.animal.Animal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class Enclosure {
    private Long id;
    private String name;
    private String zone;
    private LocalDateTime lastMaintainance;
    private Integer animalCount;
    private Set<Animal> animals;

    public static Enclosure fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static Enclosure fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        Enclosure enclosure = new Enclosure();
        enclosure.setId(id);
        enclosure.setName(rs.getString(aliasPrefix + "name"));
        enclosure.setZone(rs.getString(aliasPrefix + "zone"));

        Timestamp ts = rs.getTimestamp(aliasPrefix + "last_maintainance");
        LocalDateTime lastMaint = null;
        if (ts != null) {
            lastMaint = ts.toLocalDateTime();
        }
        enclosure.setLastMaintainance(lastMaint);

        enclosure.setAnimals(new HashSet<>());

        return enclosure;
    }
}
package sk.upjs.paz.entity;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class Enclosure {
    private Long id;
    private String name;
    private String zone;
    private LocalDateTime lastMaintainance;
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
        enclosure.setLastMaintainance(rs.getTimestamp(aliasPrefix + "last_maintainance").toLocalDateTime());
        enclosure.setAnimals(new HashSet<>());
        return enclosure;
    }


}

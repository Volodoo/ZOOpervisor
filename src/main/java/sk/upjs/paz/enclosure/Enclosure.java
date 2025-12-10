package sk.upjs.paz.enclosure;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Data
public class Enclosure {
    private Long id;
    private String name;
    private String zone;
    private LocalDateTime lastMaintainance;

    private Integer animalsCount;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedLastMaintainance = (lastMaintainance != null) ? lastMaintainance.format(formatter) : "Ešte neprebehla";

        return String.format("Názov: %s\nZóna: %s",
                name,
                zone);
    }

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
        enclosure.setAnimalsCount(null);

        return enclosure;
    }
}
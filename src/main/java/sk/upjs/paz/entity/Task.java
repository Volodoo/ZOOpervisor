package sk.upjs.paz.entity;

import jdk.jfr.DataAmount;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class Task {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private User user;
    private Set<Animal> animals;
    private Set<Enclosure> enclosures;


    public static Task fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static Task fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        var task = new Task();
        task.setId(id);
        task.setName(rs.getString(aliasPrefix + "name"));
        task.setDescription(rs.getString(aliasPrefix + "description"));
        task.setDeadline(rs.getTimestamp(aliasPrefix + "deadline").toLocalDateTime());
        task.setUser(null);
        task.setAnimals(null);
        task.setEnclosures(null);
        return task;
    }


}

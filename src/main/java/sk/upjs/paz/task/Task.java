package sk.upjs.paz.task;

import lombok.Data;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class Task {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private Set<Animal> animals;
    private Set<Enclosure> enclosures;
    private User user;

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
        task.setAnimals(new HashSet<>());
        task.setEnclosures(new HashSet<>());
        task.setUser(null);

        return task;
    }
}

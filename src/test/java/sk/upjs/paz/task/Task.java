package sk.upjs.paz.task;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Data
public class Task {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private Status status;
    private User user;
    private Set<Animal> animals;
    private Set<Enclosure> enclosures;



    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDeadline = (deadline != null) ? deadline.format(formatter) : "Neurčený termín";

        String enclosureNames = enclosures != null && !enclosures.isEmpty() ?
                String.join(", ", enclosures.stream().map(Enclosure::getName).toArray(String[]::new)) : "Žiadne výbehy";

        String animalIds = animals != null && !animals.isEmpty() ?
                String.join(", ", animals.stream().map(animal -> String.valueOf(animal.getId())).toArray(String[]::new)) : "Žiadne zvieratá";

        String userInfo = (user != null) ?
                String.format("%s %s (%s)", user.getFirstName(), user.getLastName(), user.getRole()) : "Neznámy používateľ";

        return String.format("Názov úlohy: %s\nDeadline: %s\nPopis: %s\nVýbehy: %s\nID Zvierat: %s\nPoužívateľ: %s",
                name != null ? name : "Neznámy názov",
                formattedDeadline,
                description != null ? description : "Žiadny popis",
                enclosureNames,
                animalIds,
                userInfo);
    }




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
        task.setStatus(Status.valueOf(rs.getString(aliasPrefix + "status")));
        task.setUser(null);
        task.setAnimals(new HashSet<>());
        task.setEnclosures(new HashSet<>());


        return task;
    }
}

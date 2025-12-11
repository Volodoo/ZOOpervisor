package sk.upjs.paz.animal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import sk.upjs.paz.enclosure.Enclosure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data

public class Animal {
    private Long id;
    private String nickname;
    private String species;
    private Sex sex;
    private LocalDate birthDay;
    private LocalDateTime lastCheck;
    private Status status;
    private Enclosure enclosure;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Animal animal = (Animal) o;
        return id != null && id.equals(animal.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedLastCheck = (lastCheck != null) ? lastCheck.format(formatter) : "Ešte neprebehla";

        return String.format("Prezývka: %s\nDruh: %s\nPohlavie: %s\nVýbeh: %s\nPosledná Kontrola: %s",
                (nickname != null && !nickname.isEmpty()) ? nickname : "Žiadna prezývka",
                species,
                sex,
                (enclosure != null ? enclosure.getName() : "Nezadaný výbeh"),
                formattedLastCheck);
    }



    public static Animal fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static Animal fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        var animal = new Animal();
        animal.setId(id);
        animal.setNickname(rs.getString(aliasPrefix + "nickname"));
        animal.setSpecies(rs.getString(aliasPrefix + "species"));
        animal.setSex(Sex.valueOf(rs.getString(aliasPrefix + "sex")));
        animal.setBirthDay(rs.getDate(aliasPrefix + "birth_day").toLocalDate());

        Timestamp ts = rs.getTimestamp(aliasPrefix + "last_check");
        LocalDateTime lastCheck = null;
        if (ts != null) {
            lastCheck = ts.toLocalDateTime();
        }
        animal.setLastCheck(lastCheck);
        animal.setStatus(Status.valueOf(rs.getString(aliasPrefix + "status")));

        animal.setEnclosure(null);
        return animal;
    }
}
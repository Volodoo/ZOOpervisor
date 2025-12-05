package sk.upjs.paz.entity;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Animal {
    private Long id;
    private String nickname;
    private String species;
    private Sex sex;
    private LocalDate birthDay;
    private LocalDateTime lastCheck;
    private Enclosure enclosure;


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

        animal.setEnclosure(null);
        return animal;
    }
}
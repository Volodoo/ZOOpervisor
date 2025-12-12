package sk.upjs.paz.user;

import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Data
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate birthDay;
    private Role role;
    private String email;
    private String password;

    @Override
    public String toString() {
        return String.format("%s %s (%s) (%d)", firstName, lastName, role, id);
    }


    public static User fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static User fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        var user = new User();
        user.setId(id);
        user.setFirstName(rs.getString(aliasPrefix + "first_name"));
        user.setLastName(rs.getString(aliasPrefix + "last_name"));
        user.setGender(Gender.valueOf(rs.getString(aliasPrefix + "gender")));
        user.setBirthDay(rs.getDate(aliasPrefix + "birth_day").toLocalDate());
        user.setRole(Role.valueOf(rs.getString(aliasPrefix + "role")));
        user.setEmail(rs.getString(aliasPrefix + "email"));
        user.setPassword(rs.getString(aliasPrefix + "password"));
        return user;
    }

}


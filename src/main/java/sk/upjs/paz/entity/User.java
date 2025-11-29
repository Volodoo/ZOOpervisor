package sk.upjs.paz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
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


}

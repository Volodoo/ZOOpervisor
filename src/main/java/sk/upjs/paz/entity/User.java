package sk.upjs.paz.entity;

import java.time.LocalDate;

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

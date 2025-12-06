package sk.upjs.paz.security;

import lombok.Data;
import sk.upjs.paz.user.Role;


@Data
public class Principal {
    private Long id;
    private String email;
    private Role role;
}

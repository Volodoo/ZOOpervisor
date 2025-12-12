package sk.upjs.paz.security;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.paz.exceptions.AuthenticationException;
import sk.upjs.paz.user.Role;

import java.util.List;

public class MysqlAuthDao implements AuthDao {

    private final JdbcOperations jdbcOperations;

    public MysqlAuthDao(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    private final String selectUserQuery = "SELECT id, first_name, last_name, email, role, password " +
            "FROM user WHERE email = ?";

    private final RowMapper<PrincipalWithPassword> rowMapper = (rs, rowNum) -> {
        var principal = new Principal();
        principal.setId(rs.getLong("id"));
        principal.setFirstName(rs.getString("first_name"));
        principal.setLastName(rs.getString("last_name"));
        principal.setEmail(rs.getString("email"));
        principal.setRole(Role.valueOf(rs.getString("role")));

        var principalWithPassword = new PrincipalWithPassword();
        principalWithPassword.setPrincipal(principal);
        principalWithPassword.setPassword(rs.getString("password"));
        return principalWithPassword;
    };

    @Override
    public Principal authenticate(String email, String password) throws AuthenticationException {
        List<PrincipalWithPassword> principals = jdbcOperations.query(selectUserQuery, rowMapper, email);
        if (principals.size() > 1) {
            throw new IllegalStateException("Multiple users with the same email found.");
        }

        if (principals.isEmpty()) {
            throw new AuthenticationException("Invalid credentials.");
        }

        var principal = principals.get(0);

        boolean ok = BCrypt.checkpw(password, principal.getPassword());
        if (!ok) {
            throw new AuthenticationException("Invalid credentials.");
        }

        return principal.getPrincipal();
    }
}
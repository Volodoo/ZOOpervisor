package sk.upjs.paz.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.paz.TestContainers;
import sk.upjs.paz.exceptions.AuthenticationException;
import sk.upjs.paz.user.Role;

import static org.junit.jupiter.api.Assertions.*;

class MysqlAuthDaoTest extends TestContainers {

    private MysqlAuthDao dao;

    @BeforeEach
    void setUp() {
        dao = new MysqlAuthDao(jdbc);
    }

    @Test
    void authenticate_success() throws AuthenticationException {
        Principal principal = dao.authenticate("peter.horvath@zoo.sk", "peter.horvath@zoo.sk1");

        assertNotNull(principal);
        assertEquals("peter.horvath@zoo.sk", principal.getEmail());
        assertEquals(Role.ZOOKEEPER, principal.getRole());
    }
}

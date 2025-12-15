package sk.upjs.paz.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.paz.TestContainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MysqlUserDaoTest extends TestContainers {

    private UserDao dao;

    @BeforeEach
    void setUp() {
        dao = new MysqlUserDao(jdbc);
    }

    @Test
    void getAll() {
        List<User> users = dao.getAll();
        assertEquals(8, users.size());

        assertEquals("Marek", users.get(0).getFirstName());
        assertEquals("Jana", users.get(1).getFirstName());
        assertEquals("Tomáš", users.get(2).getFirstName());
        assertEquals("Eva", users.get(3).getFirstName());
        assertEquals("Peter", users.get(4).getFirstName());
        assertEquals("Anna", users.get(5).getFirstName());
        assertEquals("Lukáš", users.get(6).getFirstName());
        assertEquals("Martin", users.get(7).getFirstName());
    }

    @Test
    void getById() {
        User u1 = dao.getById(1L);
        assertEquals("Marek", u1.getFirstName());
        assertEquals("Tóth", u1.getLastName());
        assertEquals(Role.MANAGER, u1.getRole());

        User u4 = dao.getById(4L);
        assertEquals("Eva", u4.getFirstName());
        assertEquals(Role.CASHIER, u4.getRole());

        assertThrows(Exception.class, () -> dao.getById(999L));
    }

    @Test
    void getByRole() {
        List<User> cashiers = dao.getByRole(Role.CASHIER);
        assertEquals(3, cashiers.size());

        assertEquals("Jana", cashiers.get(0).getFirstName());
        assertEquals("Tomáš", cashiers.get(1).getFirstName());
        assertEquals("Eva", cashiers.get(2).getFirstName());

        for (User user : cashiers) {
            assertEquals(Role.CASHIER, user.getRole());
        }

        List<User> maintainers = dao.getByRole(Role.MAINTAINER);
        assertEquals(2, maintainers.size());
        assertEquals("Lukáš", maintainers.get(0).getFirstName());
        assertEquals("Martin", maintainers.get(1).getFirstName());

        for (User user : maintainers) {
            assertEquals(Role.MAINTAINER, user.getRole());
        }
    }

    @Test
    void create() {
        User newUser = new User();
        newUser.setFirstName("Test");
        newUser.setLastName("User");
        newUser.setGender(Gender.UNKNOWN);
        newUser.setBirthDay(LocalDate.of(2000, 1, 1));
        newUser.setRole(Role.INACTIVE);
        newUser.setEmail("test.user@zoo.sk");
        newUser.setPassword("password");

        User created = dao.create(newUser);
        assertNotNull(created.getId());

        User fromDb = dao.getById(created.getId());
        assertEquals("Test", fromDb.getFirstName());
        assertEquals("User", fromDb.getLastName());
        assertEquals(Role.INACTIVE, fromDb.getRole());
    }

    @Test
    void update() {
        User u2 = dao.getById(2L);
        u2.setFirstName("Updated");

        User updated = dao.update(u2);
        assertEquals("Updated", updated.getFirstName());

        User fromDb = dao.getById(2L);
        assertEquals("Updated", fromDb.getFirstName());
    }
}

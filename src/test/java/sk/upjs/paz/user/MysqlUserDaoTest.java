package sk.upjs.paz.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.paz.TestContainers;
import sk.upjs.paz.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MysqlUserDaoTest extends TestContainers {

    private MysqlUserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new MysqlUserDao(jdbc);
    }

    @Test
    void create() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setGender(Gender.UNKNOWN);
        user.setBirthDay(LocalDate.of(2000, 1, 1));
        user.setRole(Role.INACTIVE);
        user.setEmail("test.user@example.com");
        user.setPassword("password123");

        User created = userDao.create(user);

        assertNotNull(created.getId(), "ID should be set after creation");

        User fromDb = userDao.getById(created.getId());
        assertEquals("Test", fromDb.getFirstName());
    }

    @Test
    void getAll() {
        List<User> users = userDao.getAll();
        assertNotNull(users);
        assertTrue(users.size() >= 8, "Database should contain initial 8 users");
    }

    @Test
    void getById() {
        // Skúsime získať existujúceho používateľa Mareka (id = 1)
        User fromDb = userDao.getById(1L);
        assertEquals("Marek", fromDb.getFirstName());
        assertThrows(NotFoundException.class, () -> userDao.getById(9999L));
    }

    @Test
    void update() {
        User user = new User();
        user.setFirstName("Update");
        user.setLastName("Test");
        user.setGender(Gender.UNKNOWN);
        user.setBirthDay(LocalDate.of(1990, 5, 5));
        user.setRole(Role.CASHIER);
        user.setEmail("update@test.com");
        user.setPassword("pass");

        User created = userDao.create(user);

        created.setFirstName("UpdatedName");
        User updated = userDao.update(created);

        assertEquals("UpdatedName", updated.getFirstName());
    }

    @Test
    void delete() {
        User user = new User();
        user.setFirstName("Delete");
        user.setLastName("Me");
        user.setGender(Gender.UNKNOWN);
        user.setBirthDay(LocalDate.of(1995, 6, 6));
        user.setRole(Role.ZOOKEEPER);
        user.setEmail("delete@test.com");
        user.setPassword("pass");

        User created = userDao.create(user);
        long id = created.getId();

        userDao.delete(id);

        assertThrows(NotFoundException.class, () -> userDao.getById(id));
    }

    @Test
    void getByRole() {
        // Overenie existujúcich používateľov CASHIER
        List<User> cashiers = userDao.getByRole(Role.CASHIER);
        assertTrue(cashiers.size() >= 3);
        assertTrue(cashiers.stream().anyMatch(u -> u.getFirstName().equals("Jana")));
        assertTrue(cashiers.stream().anyMatch(u -> u.getFirstName().equals("Tomáš")));
        assertTrue(cashiers.stream().anyMatch(u -> u.getFirstName().equals("Eva")));
    }
}

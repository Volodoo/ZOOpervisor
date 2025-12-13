package sk.upjs.paz.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.paz.Factory;
import sk.upjs.paz.TestContainers;
import sk.upjs.paz.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MysqlUserDaoTest extends TestContainers {

    private MysqlUserDao dao;

    @BeforeEach
    void setUp() {
        dao = new MysqlUserDao(Factory.INSTANCE.getMysqlJdbcOperations());
    }

    @Test
    void getAllUsers() {
        List<User> users = dao.getAll().stream()
                .sorted(Comparator.comparing(User::getId))
                .toList();

        assertFalse(users.isEmpty());
        assertEquals(8, users.size()); // podľa tvojho init.sql

        assertEquals("Marek", users.get(0).getFirstName());
        assertEquals("Tóth", users.get(0).getLastName());

        assertEquals("Jana", users.get(1).getFirstName());
        assertEquals("Nováková", users.get(1).getLastName());
    }

    @Test
    void getByIdExistingAndNotExisting() {
        User user = dao.getById(1L);

        assertNotNull(user);
        assertEquals("Marek", user.getFirstName());
        assertEquals(LocalDate.parse("1979-06-30"), user.getBirthDay());

        // Test NotFoundException
        assertThrows(NotFoundException.class, () -> dao.getById(-1L));
    }

    @Test
    void getByRole() {
        List<User> cashiers = dao.getByRole(Role.CASHIER);
        assertFalse(cashiers.isEmpty());

        for (User u : cashiers) {
            assertEquals(Role.CASHIER, u.getRole());
        }
    }

    @Test
    void createAndRetrieveUser() {
        User user = new User();
        user.setFirstName("Peter");
        user.setLastName("Parker");
        user.setGender(Gender.MALE);
        user.setBirthDay(LocalDate.parse("1999-05-05"));
        user.setRole(Role.INACTIVE);
        user.setEmail("peter@marvel.com");
        user.setPassword("secret");

        User created = dao.create(user);
        assertNotNull(created.getId());

        User fromDb = dao.getById(created.getId());
        assertEquals("Peter", fromDb.getFirstName());
        assertEquals("Parker", fromDb.getLastName());
        assertEquals("peter@marvel.com", fromDb.getEmail());
    }

    @Test
    void updateUser() {
        User user = dao.getById(1L);
        user.setFirstName("MarekUpdated");
        user.setEmail("marek.updated@zoo.sk");

        dao.update(user);

        User updated = dao.getById(1L);
        assertEquals("MarekUpdated", updated.getFirstName());
        assertEquals("marek.updated@zoo.sk", updated.getEmail());
    }

    @Test
    void deleteUser() {
        dao.delete(1L);

        assertThrows(NotFoundException.class, () -> dao.getById(1L));
    }
}
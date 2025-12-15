package sk.upjs.paz.animal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.paz.TestContainers;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MysqlAnimalDaoTest extends TestContainers {

    private AnimalDao dao;

    @BeforeEach
    void setUp() {
        dao = new MysqlAnimalDao(jdbc);
    }

    @Test
    void getAll() {
        List<Animal> animals = dao.getAll();
        assertEquals(13, animals.size());

        assertEquals("Alex", animals.get(0).getNickname());
        assertEquals("Marty", animals.get(1).getNickname());
        assertEquals("Melman", animals.get(2).getNickname());
        assertEquals("Gloria", animals.get(3).getNickname());
        assertEquals("Skipper", animals.get(4).getNickname());
        assertEquals("Kowalski", animals.get(5).getNickname());
        assertEquals("Rico", animals.get(6).getNickname());
        assertEquals("Pešiak", animals.get(7).getNickname());
        assertEquals("Kráľ Julien", animals.get(8).getNickname());
        assertEquals("Maurice", animals.get(9).getNickname());
        assertEquals("Mort", animals.get(10).getNickname());
        assertEquals("Mason", animals.get(11).getNickname());
        assertEquals("Phil", animals.get(12).getNickname());
    }

    @Test
    void getById() {
        Animal a1 = dao.getById(1L);
        assertEquals("Alex", a1.getNickname());
        assertEquals("Lev", a1.getSpecies());
        assertEquals("MALE", a1.getSex().toString());
        assertEquals("Výbeh pre levy", a1.getEnclosure().getName());

        Animal a5 = dao.getById(5L);
        assertEquals("Skipper", a5.getNickname());
        assertEquals("Tučniak", a5.getSpecies());
        assertEquals("Polárna", a5.getEnclosure().getZone());

        assertThrows(NotFoundException.class, () -> dao.getById(999L));
    }

    @Test
    void getByStatus() {
        List<Animal> active = dao.getByStatus(Status.ACTIVE);
        assertEquals(13, active.size());

        for (Animal a : active) {
            assertEquals(Status.ACTIVE, a.getStatus());
        }
    }

    @Test
    void create() {
        Enclosure enclosure = new Enclosure();
        enclosure.setId(1L);

        Animal newAnimal = new Animal();
        newAnimal.setNickname("Simba");
        newAnimal.setSpecies("Lev");
        newAnimal.setSex(Sex.MALE);
        newAnimal.setBirthDay(LocalDate.of(2020, 1, 1));
        newAnimal.setStatus(Status.ACTIVE);
        newAnimal.setEnclosure(enclosure);

        Animal created = dao.create(newAnimal);
        assertNotNull(created.getId());
        assertEquals("Simba", created.getNickname());
        assertEquals("Lev", created.getSpecies());
        assertEquals(1L, created.getEnclosure().getId());
    }

    @Test
    void update() {
        Animal a1 = dao.getById(1L);
        a1.setNickname("Alex Updated");

        Animal updated = dao.update(a1);
        assertEquals("Alex Updated", updated.getNickname());

        Animal fromDb = dao.getById(1L);
        assertEquals("Alex Updated", fromDb.getNickname());
    }

    @Test
    void getAllSortedByZoneSpecies() {
        List<Animal> animals = dao.getAllSortedByZoneSpecies();
        assertEquals(13, animals.size());

        assertEquals("Kráľ Julien", animals.get(0).getNickname());
        assertEquals("Maurice", animals.get(1).getNickname());
        assertEquals("Mort", animals.get(2).getNickname());
        assertEquals("Mason", animals.get(3).getNickname());
        assertEquals("Phil", animals.get(4).getNickname());
        assertEquals("Skipper", animals.get(5).getNickname());
        assertEquals("Kowalski", animals.get(6).getNickname());
        assertEquals("Rico", animals.get(7).getNickname());
        assertEquals("Pešiak", animals.get(8).getNickname());
        assertEquals("Gloria", animals.get(9).getNickname());
        assertEquals("Alex", animals.get(10).getNickname());
        assertEquals("Marty", animals.get(11).getNickname());
        assertEquals("Melman", animals.get(12).getNickname());


    }
}
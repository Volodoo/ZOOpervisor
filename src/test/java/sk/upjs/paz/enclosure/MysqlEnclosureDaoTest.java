package sk.upjs.paz.enclosure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.paz.TestContainers;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.exceptions.NotFoundException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MysqlEnclosureDaoTest extends TestContainers {

    private MysqlEnclosureDao dao;

    @BeforeEach
    void setUp() {
        dao = new MysqlEnclosureDao(jdbc);
    }

    /* -------------------- getAll -------------------- */
    @Test
    void getAll() {
        List<Enclosure> enclosures = dao.getAll();
        assertEquals(7, enclosures.size());

        assertEquals("Výbeh pre levy", enclosures.get(0).getName());
        assertEquals("Výbeh pre zebry", enclosures.get(1).getName());
        assertEquals("Výbeh pre žirafy", enclosures.get(2).getName());
        assertEquals("Výbeh pre hrochy", enclosures.get(3).getName());
        assertEquals("Výbeh pre tučniaky", enclosures.get(4).getName());
        assertEquals("Výbeh pre lemury", enclosures.get(5).getName());
        assertEquals("Výbeh pre šimpanzy", enclosures.get(6).getName());
    }

    /* -------------------- getAllSortedByZone -------------------- */
    @Test
    void getAllSortedByZone() {
        List<Enclosure> enclosures = dao.getAllSortedByZone();
        assertEquals(7, enclosures.size());

        // lexikografické zoradenie zón
        assertEquals("Džungľa", enclosures.get(0).getZone());
        assertEquals("Džungľa", enclosures.get(1).getZone());
        assertEquals("Polárna", enclosures.get(2).getZone());
        assertEquals("Savana", enclosures.get(3).getZone());
        assertEquals("Savana", enclosures.get(4).getZone());
        assertEquals("Savana", enclosures.get(5).getZone());
        assertEquals("Savana", enclosures.get(6).getZone());
    }

    /* -------------------- getByZone -------------------- */
    @Test
    void getByZone() {
        List<Enclosure> savana = dao.getByZone("Savana");
        assertEquals(4, savana.size());
        assertEquals("Výbeh pre levy", savana.get(0).getName());
        assertEquals("Výbeh pre zebry", savana.get(1).getName());
        assertEquals("Výbeh pre žirafy", savana.get(2).getName());
        assertEquals("Výbeh pre hrochy", savana.get(3).getName());

        List<Enclosure> dzungla = dao.getByZone("Džungľa");
        assertEquals(2, dzungla.size());
        assertEquals("Výbeh pre lemury", dzungla.get(0).getName());
        assertEquals("Výbeh pre šimpanzy", dzungla.get(1).getName());
    }

    /* -------------------- getById -------------------- */
    @Test
    void getById() {
        Enclosure e1 = dao.getById(1L);
        assertEquals("Výbeh pre levy", e1.getName());
        Enclosure e5 = dao.getById(5L);
        assertEquals("Výbeh pre tučniaky", e5.getName());

        assertThrows(NotFoundException.class, () -> dao.getById(999L));
    }

    /* -------------------- create -------------------- */
    @Test
    void create() {
        Enclosure newEnclosure = new Enclosure();
        newEnclosure.setName("Výbeh pre pandy");
        newEnclosure.setZone("Džungľa");

        Enclosure created = dao.create(newEnclosure);
        assertNotNull(created.getId());
        assertEquals("Výbeh pre pandy", created.getName());
        assertEquals("Džungľa", created.getZone());
    }

    @Test
    void create_nullEnclosure() {
        assertThrows(IllegalArgumentException.class, () -> dao.create(null));
    }

    @Test
    void create_withIdSet() {
        Enclosure e = new Enclosure();
        e.setId(10L);
        e.setName("Test");
        e.setZone("TestZone");
        assertThrows(IllegalArgumentException.class, () -> dao.create(e));
    }

    /* -------------------- update -------------------- */
    @Test
    void update() {
        Enclosure e1 = dao.getById(1L);
        e1.setName("Výbeh pre veľké levy");

        Enclosure updated = dao.update(e1);
        assertEquals("Výbeh pre veľké levy", updated.getName());

        Enclosure fromDb = dao.getById(1L);
        assertEquals("Výbeh pre veľké levy", fromDb.getName());
    }

    @Test
    void update_nullEnclosure() {
        assertThrows(IllegalArgumentException.class, () -> dao.update(null));
    }

    @Test
    void update_noId() {
        Enclosure e = new Enclosure();
        e.setName("Test");
        e.setZone("Test");
        assertThrows(IllegalArgumentException.class, () -> dao.update(e));
    }

    /* -------------------- getAnimals -------------------- */
    @Test
    void getAnimals() {
        Set<Animal> a1 = dao.getAnimals(1L);
        assertEquals(1, a1.size());
        Set<Animal> a5 = dao.getAnimals(5L);
        assertEquals(4, a5.size());
        Set<Animal> a6 = dao.getAnimals(6L);
        assertEquals(3, a6.size());
        Set<Animal> a7 = dao.getAnimals(7L);
        assertEquals(2, a7.size());
    }

    /* -------------------- getAnimalsCount -------------------- */
    @Test
    void getAnimalsCount_manual() {
        assertEquals(1, dao.getAnimalsCount(1L), "Výbeh pre levy ma 1 zviera");
        assertEquals(1, dao.getAnimalsCount(2L), "Výbeh pre zebry ma 1 zviera");
        assertEquals(1, dao.getAnimalsCount(3L), "Výbeh pre žirafy ma 1 zviera");
        assertEquals(1, dao.getAnimalsCount(4L), "Výbeh pre hrochy ma 1 zviera");
        assertEquals(4, dao.getAnimalsCount(5L), "Výbeh pre tučniaky ma 4 zvierata");
        assertEquals(3, dao.getAnimalsCount(6L), "Výbeh pre lemury ma 3 zvierata");
        assertEquals(2, dao.getAnimalsCount(7L), "Výbeh pre šimpanzy ma 2 zvierata");
    }
}

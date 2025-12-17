package sk.upjs.paz.task;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.upjs.paz.TestContainers;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.user.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MysqlTaskDaoTest extends TestContainers {

    private MysqlTaskDao dao;


    @BeforeEach
    public void setUp() { dao= new MysqlTaskDao(jdbc);}


    @Test
    void getAll() {
        List<Task> tasks = dao.getAll();
        assertEquals(10,tasks.size());

        assertEquals("Uspatie a kontrola parazitov u leva Alexa", tasks.get(0).getName());
        assertEquals("Ošetrenie kopyt zebry Marty", tasks.get(1).getName());
        assertEquals("Podanie liekov žirafe Melmanovi", tasks.get(2).getName());
        assertEquals("Uspatie a ošetrenie zubov hrošice Glorie", tasks.get(3).getName());
        assertEquals("Zváženie tučniakov", tasks.get(4).getName());
        assertEquals("Odobratie krvi lemurom", tasks.get(5).getName());
        assertEquals("Interakcia a tréning so šimpanzmi", tasks.get(6).getName());
        assertEquals("Údržba výbehov v zóne savana", tasks.get(7).getName());
        assertEquals("Údržba výbehu pre tučniaky", tasks.get(8).getName());
        assertEquals("Údržba výbehov v zóne džungľa", tasks.get(9).getName());
    }


    @Test
    void getAllSortedByDeadline(){
        List<Task> tasks = dao.getAllSortedByDeadline();
        assertEquals(10,tasks.size());

        assertEquals("2025-12-16T10:00",tasks.get(2).getDeadline().toString());
        assertEquals("2025-12-17T08:00",tasks.get(3).getDeadline().toString());
        assertEquals("2025-12-18T11:00",tasks.get(5).getDeadline().toString());
        assertEquals("2025-12-15T10:00",tasks.get(0).getDeadline().toString());
        assertEquals("2025-12-16T09:00",tasks.get(1).getDeadline().toString());
        assertEquals("2025-12-17T14:00",tasks.get(4).getDeadline().toString());
        assertEquals("2025-12-19T10:00",tasks.get(6).getDeadline().toString());
        assertEquals("2025-12-20T08:00",tasks.get(7).getDeadline().toString());
        assertEquals("2025-12-21T10:00",tasks.get(9).getDeadline().toString());
    }

    @Test
    void getByUser(){
        List<Task> peterTasks=dao.getByUser(5L);
        assertEquals(4,peterTasks.size());

        for (Task task : peterTasks) {
            assertEquals(5L, task.getUser().getId(), "Task musí patriť Petrovi (ID 5)");
        }
        List<Task> annaTasks=dao.getByUser(6L);
        assertEquals(3,annaTasks.size());

        for (Task task : annaTasks) {
            assertEquals(6L, task.getUser().getId(), "Task musí patriť Anne (ID 6)");
        }
        List<Task> lukasTasks=dao.getByUser(7L);
        assertEquals(1,lukasTasks.size());

        for (Task task : lukasTasks) {
            assertEquals(7L, task.getUser().getId(), "Task musí patriť Lukasovi (ID 7)");
        }

        List<Task> martinTasks=dao.getByUser(8L);
        assertEquals(2,martinTasks.size());

        for (Task task : martinTasks) {
            assertEquals(8L, task.getUser().getId(), "Task musí patriť Martinovi (ID 8)");
        }

    }

    @Test
    void getById(){
        Task t1=dao.getById(1L);
        assertEquals("Uspatie a kontrola parazitov u leva Alexa",t1.getName());
        assertEquals("Uspite leva Alexa a skontrolujte, či nemá parazity, poprípade aplikujte lieky proti parazitom.",t1.getDescription());
        assertEquals(Status.INCOMPLETE,t1.getStatus());
        assertEquals("2025-12-21T09:00",t1.getDeadline().toString());
        assertEquals(5L,t1.getUser().getId());
        assertThrows(Exception.class, () -> dao.getById(999L));
    }

    @Test
    void create(){
        User user=new User();
        user.setId(1L);

        Animal animal=new Animal();
        Set<Animal> animals=new HashSet<>();
        animal.setId(1L);
        animals.add(animal);

        Enclosure enclosure=new Enclosure();
        enclosure.setId(1L);
        Set<Enclosure> enclosures=new HashSet<>();
        enclosures.add(enclosure);

        Task newTask=new Task();
        newTask.setName("Test task");
        newTask.setDescription("Test description");
        newTask.setUser(user);
        LocalDateTime time=LocalDateTime.now().withNano(0);
        newTask.setDeadline(time);
        newTask.setStatus(Status.INCOMPLETE);
        newTask.setAnimals(animals);
        newTask.setEnclosures(enclosures);

        Task created=dao.create(newTask);
        assertNotNull(created.getId());

        Task fromDb=dao.getById(created.getId());
        assertEquals("Test task",fromDb.getName());
        assertEquals("Test description",fromDb.getDescription());
        assertEquals(user.getId(),fromDb.getUser().getId());
        assertEquals(animals,fromDb.getAnimals());
        assertEquals(enclosures,fromDb.getEnclosures());
        assertEquals(Status.INCOMPLETE,fromDb.getStatus());
        assertEquals(time,fromDb.getDeadline());

    }

    @Test
    void update(){
        Task t1 = dao.getById(1L);
        t1.setName("Aktualizovaný názov");
        t1.setStatus(Status.COMPLETED);

        Task updated = dao.update(t1);
        assertEquals("Aktualizovaný názov", updated.getName());
        assertEquals(Status.COMPLETED, updated.getStatus());

        Task fromDb = dao.getById(1L);
        assertEquals("Aktualizovaný názov", fromDb.getName());
        assertEquals(Status.COMPLETED, fromDb.getStatus());
    }


}

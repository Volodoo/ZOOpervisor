package sk.upjs.paz;

import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.animal.AnimalDao;
import sk.upjs.paz.animal.Sex;
import sk.upjs.paz.enclosure.Enclosure;
import sk.upjs.paz.enclosure.EnclosureDao;
import sk.upjs.paz.task.Task;
import sk.upjs.paz.task.TaskDao;
import sk.upjs.paz.user.UserDao;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class IDELauncher {

    public static void main(String[] args) throws IOException {
        AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();

        animalDao.delete(11L);
        animalDao.delete(12L);


        Main.main(args);
    }
}
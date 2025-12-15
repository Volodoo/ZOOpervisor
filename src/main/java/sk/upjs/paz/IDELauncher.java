package sk.upjs.paz;

import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.paz.animal.AnimalDao;
import sk.upjs.paz.enclosure.EnclosureDao;
import sk.upjs.paz.task.TaskDao;
import sk.upjs.paz.ticket.TicketDao;
import sk.upjs.paz.user.Role;
import sk.upjs.paz.user.UserDao;

import java.io.IOException;

public class IDELauncher {


    public static void main(String[] args) throws IOException {
        AnimalDao ad=Factory.INSTANCE.getAnimalDao();
        System.out.println(ad.getAllSortedByZoneSpecies());

        Main.main(args);
    }
}
package sk.upjs.paz;

import sk.upjs.paz.animal.AnimalDao;
import sk.upjs.paz.enclosure.EnclosureDao;
import sk.upjs.paz.task.TaskDao;
import sk.upjs.paz.ticket.TicketDao;
import sk.upjs.paz.user.Role;
import sk.upjs.paz.user.UserDao;

import java.io.IOException;

public class IDELauncher {


    public static void main(String[] args) throws IOException {
        AnimalDao animalDao = Factory.INSTANCE.getAnimalDao();
        EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();
        TaskDao taskDao = Factory.INSTANCE.getTaskDao();
        UserDao userDao = Factory.INSTANCE.getUserDao();
        TicketDao ticketDao = Factory.INSTANCE.getTicketDao();


        System.out.println(ticketDao.getByCashier(4L));
        Main.main(args);
    }
}
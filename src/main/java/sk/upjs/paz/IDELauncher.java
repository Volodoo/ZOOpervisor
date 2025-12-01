package sk.upjs.paz;

import sk.upjs.paz.entity.Gender;
import sk.upjs.paz.entity.Role;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.storage.DaoFactory;
import sk.upjs.paz.storage.UserDao;

import java.io.IOException;
import java.time.LocalDate;

public class IDELauncher {

    // This main method is used for running the app from IntelliJ IDEA.
    public static void main(String[] args) throws IOException {
        UserDao userDao = DaoFactory.INSTANCE.getUserDao();

        System.out.println(userDao.getAll());





    }
}
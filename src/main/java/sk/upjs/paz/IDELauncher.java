package sk.upjs.paz;


import sk.upjs.paz.entity.Gender;
import sk.upjs.paz.entity.Role;
import sk.upjs.paz.entity.User;
import sk.upjs.paz.storage.Factory;
import sk.upjs.paz.storage.UserDao;

import java.io.IOException;
import java.time.LocalDate;

public class IDELauncher {

    public static void main(String[] args) throws IOException {

        UserDao userDao = Factory.INSTANCE.getUserDao();
/*
        User user = new User();
        user.setFirstName("Janik");
        user.setLastName("Kowalski");
        user.setGender(Gender.OTHER);
        user.setBirthDay(LocalDate.now());
        user.setRole(Role.CASHIER);
        System.out.println(user);
        userDao.create(user);

        System.out.println(userDao.getAll());
*/

//      userDao.delete(13);

        Main.main(args);



    }
}
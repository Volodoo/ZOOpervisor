package sk.upjs.paz;


import sk.upjs.paz.user.UserDao;

import java.io.IOException;

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
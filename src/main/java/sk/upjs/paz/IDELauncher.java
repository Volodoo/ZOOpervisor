package sk.upjs.paz;


import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.enclosure.EnclosureDao;

import java.io.IOException;
import java.util.Set;

public class IDELauncher {

    public static void main(String[] args) throws IOException {

        EnclosureDao enclosureDao = Factory.INSTANCE.getEnclosureDao();
        long enclosureId = 5L;

// Získame výbeh
        var enclosure = enclosureDao.getById(enclosureId);
        System.out.println(enclosureDao.getAnimalsCount(enclosureId));


        if (enclosure != null) {
            // Získame zvieratá pre tento výbeh pomocou DAO
            Set<Animal> animals = enclosureDao.getAnimals(enclosureId);

            System.out.println("Zvieratá v výbehu " + enclosure.getName() + ":");
            for (Animal animal : animals) {
                System.out.println(animal.getNickname());
            }



        } else {
            System.out.println("Výbeh s ID " + enclosureId + " neexistuje.");
        }


        //  UserDao userDao = Factory.INSTANCE.getUserDao();
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
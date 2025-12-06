package sk.upjs.paz.security;

import org.springframework.security.crypto.bcrypt.BCrypt;
import sk.upjs.paz.Factory;
import sk.upjs.paz.user.Gender;


import java.util.Scanner;

public class InitAdminGenerator {

    public static void main(String[] args) {
        var jdbcOperations = Factory.INSTANCE.getMysqlJdbcOperations();

        var scanner = new Scanner(System.in);

        System.out.println("This program will create an admin user in the database.");
        System.out.println("Please enter new email:");
        var adminEmail = scanner.nextLine();
        System.out.println("Please enter new password:");
        var adminPassword = scanner.nextLine();

        var salt = BCrypt.gensalt();
        var adminPasswordHash = BCrypt.hashpw(adminPassword, salt);

        long id = -1;
        var existingMinId = jdbcOperations.queryForObject("SELECT MIN(id) FROM user WHERE id < 0", Long.class);
        if (existingMinId != null) {
            id = existingMinId - 1;
        }


        jdbcOperations.update(
                "INSERT INTO user (id, first_name, last_name, gender, birth_day, role, email, password) VALUES (?, 'Admin', 'Admin', 'UNKNOWN', '2000-01-01', 'ADMIN', ?, ?)",
                id,
                adminEmail,
                adminPasswordHash
        );

        System.out.printf("Admin created with email `%s`, password: `%s`%n", adminEmail, adminPassword);
    }
}

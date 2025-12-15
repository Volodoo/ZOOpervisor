package sk.upjs.paz;

import org.testcontainers.containers.MySQLContainer;


public class DockerTest {
    public static void main(String[] args) {
        try (MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4.2")) {
            mysql.start();
            System.out.println("JDBC URL: " + mysql.getJdbcUrl());
        }
    }
}
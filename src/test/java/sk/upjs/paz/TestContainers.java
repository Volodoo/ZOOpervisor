package sk.upjs.paz;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;

public abstract class TestContainers {

    protected JdbcTemplate jdbc;

    /**
     * Pripojenie na MySQL testovaciu DB a načítanie init_test.sql pred každým testom
     */
    @BeforeEach
    void resetDatabase() {
        try {
            // 1. DataSource na testovaciu databázu
            DriverManagerDataSource ds = new DriverManagerDataSource(
                    "jdbc:mysql://localhost:3308/zoopervisor_test",
                    "root",
                    "root"
            );

            // 2. JdbcTemplate na vykonanie DROP a TRUNCATE
            jdbc = new JdbcTemplate(ds);
            clearTables();

            // 3. Načítanie init_test.sql
            try (Connection connection = ds.getConnection()) {
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("init_test.sql"));
            }

        } catch (Exception e) {
            throw new RuntimeException("Chyba pri resetovaní testovacej databázy", e);
        }
    }

    private void clearTables() {
        jdbc.execute("SET FOREIGN_KEY_CHECKS = 0");

        // DROP všetkých testovacích tabuliek, ak existujú
        jdbc.execute("DROP TABLE IF EXISTS task_has_enclosure");
        jdbc.execute("DROP TABLE IF EXISTS task_has_animal");
        jdbc.execute("DROP TABLE IF EXISTS ticket");
        jdbc.execute("DROP TABLE IF EXISTS task");
        jdbc.execute("DROP TABLE IF EXISTS animal");
        jdbc.execute("DROP TABLE IF EXISTS enclosure");
        jdbc.execute("DROP TABLE IF EXISTS user");

        jdbc.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}
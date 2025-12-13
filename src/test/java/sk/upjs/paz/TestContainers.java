package sk.upjs.paz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public abstract class TestContainers {

    protected static MySQLContainer<?> mysql;

    @BeforeAll
    static void startContainer() {
        try {
            DockerImageName imageName = DockerImageName.parse("mysql:8.0.34");
            mysql = new MySQLContainer<>(imageName)
                    .withDatabaseName("zoopervisor")
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("init.sql"),
                            "/docker-entrypoint-initdb.d/init.sql"
                    );

            mysql.start();

            // Nastavenie systémových premenných pre JDBC
            System.setProperty("DB_JDBC", mysql.getJdbcUrl());
            System.setProperty("DB_USER", mysql.getUsername());
            System.setProperty("DB_PASSWORD", mysql.getPassword());

        } catch (Exception e) {
            System.err.println("Docker nie je dostupný. Testy používajúce Testcontainers nebudú fungovať.");
            e.printStackTrace();
        }
    }

    // Metóda na vyčistenie tabuliek pred každým testom
    protected void clearTables() {
        if (mysql == null) return; // Ak kontajner neběží, preskočíme čistenie

        var jdbc = Factory.INSTANCE.getMysqlJdbcOperations();

        // Vymazať tabuľky v poradí deti → rodičia kvôli FK
        jdbc.update("DELETE FROM ticket");               // deti
        jdbc.update("DELETE FROM task_has_animal");     // deti
        jdbc.update("DELETE FROM task_has_enclosure");  // deti
        jdbc.update("DELETE FROM task");                // rodič
        jdbc.update("DELETE FROM animal");              // rodič
        jdbc.update("DELETE FROM enclosure");           // rodič
        jdbc.update("DELETE FROM user");                // rodič
    }

    @BeforeEach
    void setUpBase() {
        clearTables();
    }
}
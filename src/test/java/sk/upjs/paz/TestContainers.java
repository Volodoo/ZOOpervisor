package sk.upjs.paz;

import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.MountableFile;

// Code taken from https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/
public abstract class TestContainers {
    /**
     * This is a MySQL container that is started before all tests and stopped after all tests.
     * It is used to test the MySQLUserDao.
     */
    static final MySQLContainer<?> mysql;

    static {
        mysql = new MySQLContainer<>("mysql:8.4.6")
                .withDatabaseName("zoopervisor")
                .withCopyFileToContainer(
                        MountableFile.forHostPath("./init.sql"), "./src/main/resources/init.sql:/docker-entrypoint-initdb.d/init.sql"
                );
        mysql.start();
        System.setProperty("DB_JDBC", mysql.getJdbcUrl());
        System.setProperty("DB_USER", mysql.getUsername());
        System.setProperty("DB_PASSWORD", mysql.getPassword());
    }

    protected void clearTables() {
        var jdbc = Factory.INSTANCE.getMysqlJdbcOperations();
        jdbc.update("DELETE FROM task_has_animal");
        jdbc.update("DELETE FROM task_has_enclosure");
        jdbc.update("DELETE FROM task");
        jdbc.update("DELETE FROM animal");
        jdbc.update("DELETE FROM enclosure");
        jdbc.update("DELETE FROM user");
    }

    @BeforeEach
    void setUpBase() {
        clearTables();

        var jdbc = Factory.INSTANCE.getMysqlJdbcOperations();

        // fill users
        jdbc.update("INSERT INTO zoopervisor.user (first_name, last_name, gender, birth_day, role, email, password) VALUES ('Marek','Tóth','MALE','1979-06-30','MANAGER','marek.toth@zoo.sk','$2a$10$YpZQuU3S97xRSxeiR2ND/.HGMdbrjFNmMArUh9UJX7wWad.IdPkym')");
        jdbc.update("INSERT INTO zoopervisor.user (first_name, last_name, gender, birth_day, role, email, password) VALUES ('Jana','Nováková','FEMALE','1985-04-20','CASHIER','jana.novakova@zoo.sk','$2a$10$nemoxadZeCzlyz.mGFXXNe/KiI4AZ6QClxU7NKdr2loLaImnS2jxi')");
        jdbc.update("INSERT INTO zoopervisor.user (first_name, last_name, gender, birth_day, role, email, password) VALUES ('Tomáš','Kováč','MALE','1992-07-15','CASHIER','tomas.kovac@zoo.sk','$2a$10$eH7DDvrysIK5wT9zDRDmK.yxlPgOwmjuEBD8d.VTSK40YF0LOTNGa')");
        jdbc.update("INSERT INTO zoopervisor.user (first_name, last_name, gender, birth_day, role, email, password) VALUES ('Eva','Hrušková','FEMALE','1989-10-10','CASHIER','eva.hruskova@zoo.sk','$2a$10$u1CERJt8kf0nXoowH2ydEOg9CLYemNBmhRLB2br4k4fSxlOvh31Fi')");
        jdbc.update("INSERT INTO zoopervisor.user (first_name, last_name, gender, birth_day, role, email, password) VALUES ('Peter','Horváth','MALE','1987-05-25','ZOOKEEPER','peter.horvath@zoo.sk','$2a$10$Azi4gGofai4VQUhFyPRUZ.1sEyU0eSX17naK/iiopUx2iQyLinsFu')");
        jdbc.update("INSERT INTO zoopervisor.user (first_name, last_name, gender, birth_day, role, email, password) VALUES ('Anna','Kováčová','FEMALE','1980-02-17','ZOOKEEPER','anna.kovacova@zoo.sk','$2a$10$lxjnJ8lcTuF1Bam7kzUjMuyoYcLbj77b9f79MKsOUvXCEPgkIId22')");
        jdbc.update("INSERT INTO zoopervisor.user (first_name, last_name, gender, birth_day, role, email, password) VALUES ('Lukáš','Mikula','MALE','1994-09-02','MAINTAINER','lukas.mikula@zoo.sk','$2a$10$2cH6DFH6PaJFs60dxreTE.yuL0z/XRTlv8MJGSUSHizfnmdX5/SKq')");
        jdbc.update("INSERT INTO zoopervisor.user (first_name, last_name, gender, birth_day, role, email, password) VALUES ('Martin','Doležal','MALE','1982-11-05','MAINTAINER','martin.dolezal@zoo.sk','$2a$10$nT22uFGSr5SimWlllx5HDOaIrympazg782BlbByE7oYPl4ElNEUzm')");

        // fill enclosure
        jdbc.update("INSERT INTO zoopervisor.enclosure (name, zone, last_maintainance) VALUES ('Výbeh pre levy','Savana',NULL)");
        jdbc.update("INSERT INTO zoopervisor.enclosure (name, zone, last_maintainance) VALUES ('Výbeh pre zebry','Savana',NULL)");
        jdbc.update("INSERT INTO zoopervisor.enclosure (name, zone, last_maintainance) VALUES ('Výbeh pre žirafy','Savana',NULL)");
        jdbc.update("INSERT INTO zoopervisor.enclosure (name, zone, last_maintainance) VALUES ('Výbeh pre hrochy','Savana',NULL)");
        jdbc.update("INSERT INTO zoopervisor.enclosure (name, zone, last_maintainance) VALUES ('Výbeh pre tučniaky','Polárna',NULL)");
        jdbc.update("INSERT INTO zoopervisor.enclosure (name, zone, last_maintainance) VALUES ('Výbeh pre lemury','Džungľa',NULL)");
        jdbc.update("INSERT INTO zoopervisor.enclosure (name, zone, last_maintainance) VALUES ('Výbeh pre šimpanzy','Džungľa',NULL)");

        // fill animals
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Alex','Lev','MALE','2010-05-20',NULL,'ACTIVE',1)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Marty','Zebra','MALE','2011-03-15',NULL,'ACTIVE',2)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Melman','Žirafa','MALE','2012-01-30',NULL,'ACTIVE',3)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Gloria','Hroch','FEMALE','2009-08-12',NULL,'ACTIVE',4)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Skipper','Tučniak','MALE','2015-03-10',NULL,'ACTIVE',5)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Kowalski','Tučniak','MALE','2015-04-12',NULL,'ACTIVE',5)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Rico','Tučniak','MALE','2015-05-08',NULL,'ACTIVE',5)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Pešiak','Tučniak','MALE','2015-06-20',NULL,'ACTIVE',5)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Kráľ Julien','Lemur','MALE','2013-07-15',NULL,'ACTIVE',6)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Maurice','Lemur','MALE','2012-12-01',NULL,'ACTIVE',6)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Mort','Lemur','MALE','2014-03-05',NULL,'ACTIVE',6)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Mason','Šimpanz','MALE','2014-09-23',NULL,'ACTIVE',7)");
        jdbc.update("INSERT INTO zoopervisor.animal (nickname, species, sex, birth_day, last_check, status, enclosure_id) VALUES ('Phil','Šimpanz','MALE','2015-02-05',NULL,'ACTIVE',7)");


        // fill tasks
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Uspatie a kontrola parazitov u leva Alexa', 'Uspite leva Alexa a skontrolujte, či nemá parazity, poprípade aplikujte lieky proti parazitom.', '2025-12-21 09:00:00', 'INCOMPLETE', 5)");
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Ošetrenie kopyt zebry Marty', 'Skontrolujte a ošetrite kopytá zebry Marty, odstráňte nečistoty a skontrolujte, či nemajú známky infekcií alebo poranení.', '2025-12-16 10:00:00', 'INCOMPLETE', 5)");
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Podanie liekov žirafe Melmanovi', 'Podajte lieky žirafe Melmanovi na zlepšenie trávenia a sledujte jeho reakciu.', '2025-12-17 08:00:00', 'INCOMPLETE', 5)");
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Uspatie a ošetrenie zubov hrošice Glorie', 'Uspite hrocha Gloriu a vykonajte kontrolu zubov. Ak sú potrebné, vykonajte ošetrenie alebo extrakciu.', '2025-12-18 11:00:00', 'INCOMPLETE', 5)");
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Zváženie tučniakov', 'Zvážte všetkých tučniakov. Uistite sa, že každý tučniak má správnu hmotnosť a neprejavuje známky podvýživy alebo zdravotných problémov.', '2025-12-15 10:00:00', 'INCOMPLETE', 6)");
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Odobratie krvi lemurom', 'Odoberte krv všetkým lemurom. Skontrolujte zdravie zvierat a zabezpečte vzorky pre analýzu.', '2025-12-16 09:00:00', 'INCOMPLETE', 6)");
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Interakcia a tréning so šimpanzmi', 'Trénujte šimpanzov pomocou základných povelov. Poskytnite im interaktívne hračky a stimulujte ich k pohybu a intelektuálnemu rozvoju.', '2025-12-17 14:00:00', 'INCOMPLETE', 6)");
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Údržba výbehov v zóne savana', 'Skontrolujte výbehy pre levy, zebry, žirafy a hrochy. Uistite sa, že výbehy sú čisté a že sú dobre bezpečnostne zabezpečené.', '2025-12-19 10:00:00', 'INCOMPLETE', 7)");
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Údržba výbehu pre tučniaky', 'Skontrolujte a výbeh pre tučniaky. Vymeňte vodu v bazéne a uistite sa, že vo výbehu je vhodná teplota.', '2025-12-20 08:00:00', 'INCOMPLETE', 8)");
        jdbc.update("INSERT INTO zoopervisor.task (name, description, deadline, status, user_id) VALUES ('Údržba výbehov v zóne džungľa', 'Skontrolujte a upravte výbehy pre lemury a šimpanzy. Vyčistite sklenené panely, cez ktoré sa diváci pozerajú do výbehu, aby bol výhľad jasný a bezpečný.', '2025-12-21 10:00:00', 'INCOMPLETE', 8)");

        // fill task_has_animal
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (1,1)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (2,2)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (3,3)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (4,4)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (5,5)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (5,6)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (5,7)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (5,8)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (6,9)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (6,10)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (6,11)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (7,12)");
        jdbc.update("INSERT INTO zoopervisor.task_has_animal VALUES (7,13)");

        // fill task_has_enclosure
        jdbc.update("INSERT INTO zoopervisor.task_has_enclosure (task_id, enclosure_id) VALUES (8,1)");
        jdbc.update("INSERT INTO zoopervisor.task_has_enclosure (task_id, enclosure_id) VALUES (8,2)");
        jdbc.update("INSERT INTO zoopervisor.task_has_enclosure (task_id, enclosure_id) VALUES (8,3)");
        jdbc.update("INSERT INTO zoopervisor.task_has_enclosure (task_id, enclosure_id) VALUES (8,4)");
        jdbc.update("INSERT INTO zoopervisor.task_has_enclosure (task_id, enclosure_id) VALUES (9,5)");
        jdbc.update("INSERT INTO zoopervisor.task_has_enclosure (task_id, enclosure_id) VALUES (10,6)");
        jdbc.update("INSERT INTO zoopervisor.task_has_enclosure (task_id, enclosure_id) VALUES (10,7)");

        //fill tickets
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 09:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 10:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 11:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 12:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-08 09:15:00',7.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-08 10:15:00',7.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-08 11:15:00',10.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-08 13:15:00',10.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 14:15:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 15:15:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-08 16:15:00',7.00,2)");

        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 09:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 10:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-07 11:00:00',10.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-07 12:00:00',7.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-07 13:00:00',7.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 14:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 15:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-07 16:00:00',10.00,2)");

        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 09:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-06 10:00:00',10.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-06 11:00:00',7.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 12:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 13:00:00',4.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-06 14:00:00',10.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-06 15:00:00',7.00,2)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-06 16:00:00',10.00,2)");



        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 09:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 10:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 11:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 12:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 13:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-08 09:30:00',7.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-08 10:30:00',7.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-08 11:30:00',7.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-08 12:30:00',10.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-08 14:30:00',10.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 15:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 15:30:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-08 16:00:00',7.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-08 16:30:00',10.00,3)");

        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 09:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 10:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 11:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-07 12:00:00',10.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-07 09:30:00',7.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-07 10:30:00',7.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 11:30:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 12:30:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-07 13:00:00',7.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-07 14:00:00',10.00,3)");

        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 09:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 10:00:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-06 09:30:00',7.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 10:30:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 11:30:00',4.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-06 12:00:00',7.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-06 13:00:00',10.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-06 14:00:00',10.00,3)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-06 15:00:00',7.00,3)");



        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 09:00:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 10:00:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-08 10:30:00',7.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-08 12:30:00',10.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 13:00:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-08 15:00:00',4.00,4)");

        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 09:15:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-07 11:15:00',10.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-07 09:30:00',7.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-07 10:30:00',7.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 12:30:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-07 13:00:00',7.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-07 14:00:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-07 15:00:00',10.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-07 16:00:00',10.00,4)");

        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 09:00:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 10:00:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-06 09:30:00',7.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 10:30:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-06 13:00:00',10.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-06 13:30:00',7.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Child','2025-12-06 14:00:00',4.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Student_Senior','2025-12-06 15:00:00',7.00,4)");
        jdbc.update("INSERT INTO zoopervisor.ticket (type, purchase_date_time, price, user_id) VALUES ('Adult','2025-12-06 16:30:00',10.00,4)");
    }
}

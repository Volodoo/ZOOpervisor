-- Test init script for MySQL tests

-- Vytvor databazu pre testy
CREATE DATABASE IF NOT EXISTS `zoopervisor_test` DEFAULT CHARACTER SET utf8;
USE `zoopervisor_test`;

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user`
(
    `id`         BIGINT                                                                      NOT NULL AUTO_INCREMENT,
    `first_name` VARCHAR(100)                                                                NULL,
    `last_name`  VARCHAR(100)                                                                NOT NULL,
    `gender`     ENUM ('MALE', 'FEMALE', 'OTHER', 'UNKNOWN')                                 NOT NULL DEFAULT 'UNKNOWN',
    `birth_day`  DATE                                                                        NOT NULL,
    `role`       ENUM ('INACTIVE', 'CASHIER', 'MAINTAINER', 'ZOOKEEPER', 'MANAGER', 'ADMIN') NOT NULL DEFAULT 'INACTIVE',
    `email`      VARCHAR(100)                                                                NOT NULL,
    `password`   VARCHAR(100)                                                                NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `enclosure`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `enclosure`
(
    `id`                BIGINT       NOT NULL AUTO_INCREMENT,
    `name`              VARCHAR(100) NOT NULL,
    `zone`              VARCHAR(100) NOT NULL,
    `last_maintainance` DATETIME     NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `animal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `animal`
(
    `id`           BIGINT                                              NOT NULL AUTO_INCREMENT,
    `nickname`     VARCHAR(100)                                        NULL,
    `species`      VARCHAR(100)                                        NOT NULL,
    `sex`          ENUM ('MALE', 'FEMALE', 'HERMAPHRODITE', 'UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',
    `birth_day`    DATE                                                NOT NULL,
    `last_check`   DATETIME                                            NULL,
    `status`       ENUM ('ACTIVE', 'INACTIVE', 'TREATMENT')            NOT NULL DEFAULT 'ACTIVE',
    `enclosure_id` BIGINT                                              NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_animal_enclosure_idx` (`enclosure_id` ASC),
    CONSTRAINT `fk_animal_enclosure` FOREIGN KEY (`enclosure_id`) REFERENCES `enclosure` (`id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `ticket`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ticket`
(
    `id`                 BIGINT         NOT NULL AUTO_INCREMENT,
    `type`               VARCHAR(100)   NOT NULL,
    `purchase_date_time` DATETIME       NOT NULL,
    `price`              DECIMAL(10, 2) NOT NULL,
    `user_id`            BIGINT         NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_ticket_user_idx` (`user_id` ASC),
    CONSTRAINT `fk_ticket_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task`
(
    `id`          BIGINT                           NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100)                     NOT NULL,
    `description` TEXT                             NOT NULL,
    `deadline`    DATETIME                         NOT NULL,
    `status`      ENUM ('INCOMPLETE', 'COMPLETED') NOT NULL DEFAULT 'INCOMPLETE',
    `user_id`     BIGINT                           NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_task_user_idx` (`user_id` ASC),
    CONSTRAINT `fk_task_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `task_has_animal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task_has_animal`
(
    `task_id`   BIGINT NOT NULL,
    `animal_id` BIGINT NOT NULL,
    PRIMARY KEY (`task_id`, `animal_id`),
    INDEX `fk_task_has_animal_task_idx` (`task_id` ASC),
    INDEX `fk_task_has_animal_animal_idx` (`animal_id` ASC),
    CONSTRAINT `fk_task_has_animal_task` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`),
    CONSTRAINT `fk_task_has_animal_animal` FOREIGN KEY (`animal_id`) REFERENCES `animal` (`id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `task_has_enclosure`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task_has_enclosure`
(
    `task_id`      BIGINT NOT NULL,
    `enclosure_id` BIGINT NOT NULL,
    PRIMARY KEY (`task_id`, `enclosure_id`),
    INDEX `fk_task_has_enclosure_task_idx` (`task_id` ASC),
    INDEX `fk_task_has_enclosure_enclosure_idx` (`enclosure_id` ASC),
    CONSTRAINT `fk_task_has_enclosure_task` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`),
    CONSTRAINT `fk_task_has_enclosure_enclosure` FOREIGN KEY (`enclosure_id`) REFERENCES `enclosure` (`id`)
) ENGINE = InnoDB;

-- -----------------------------------------------------
-- Sample users for tests
-- -----------------------------------------------------
INSERT INTO `user` (`first_name`, `last_name`, `gender`, `birth_day`, `role`, `email`, `password`)
VALUES ('Marek', 'Tóth', 'MALE', '1979-06-30', 'MANAGER', 'marek.toth@zoo.sk',
        '$2a$10$YpZQuU3S97xRSxeiR2ND/.HGMdbrjFNmMArUh9UJX7wWad.IdPkym'),
       ('Jana', 'Nováková', 'FEMALE', '1985-04-20', 'CASHIER', 'jana.novakova@zoo.sk',
        '$2a$10$nemoxadZeCzlyz.mGFXXNe/KiI4AZ6QClxU7NKdr2loLaImnS2jxi'),
       ('Tomáš', 'Kováč', 'MALE', '1992-07-15', 'CASHIER', 'tomas.kovac@zoo.sk',
        '$2a$10$eH7DDvrysIK5wT9zDRDmK.yxlPgOwmjuEBD8d.VTSK40YF0LOTNGa'),
       ('Eva', 'Hrušková', 'FEMALE', '1989-10-10', 'CASHIER', 'eva.hruskova@zoo.sk',
        '$2a$10$u1CERJt8kf0nXoowH2ydEOg9CLYemNBmhRLB2br4k4fSxlOvh31Fi'),
       ('Peter', 'Horváth', 'MALE', '1987-05-25', 'ZOOKEEPER', 'peter.horvath@zoo.sk',
        '$2a$10$Azi4gGofai4VQUhFyPRUZ.1sEyU0eSX17naK/iiopUx2iQyLinsFu'),
       ('Anna', 'Kováčová', 'FEMALE', '1980-02-17', 'ZOOKEEPER', 'anna.kovacova@zoo.sk',
        '$2a$10$lxjnJ8lcTuF1Bam7kzUjMuyoYcLbj77b9f79MKsOUvXCEPgkIId22'),
       ('Lukáš', 'Mikula', 'MALE', '1994-09-02', 'MAINTAINER', 'lukas.mikula@zoo.sk',
        '$2a$10$2cH6DFH6PaJFs60dxreTE.yuL0z/XRTlv8MJGSUSHizfnmdX5/SKq'),
       ('Martin', 'Doležal', 'MALE', '1982-11-05', 'MAINTAINER', 'martin.dolezal@zoo.sk',
        '$2a$10$nT22uFGSr5SimWlllx5HDOaIrympazg782BlbByE7oYPl4ElNEUzm');

-- Vloženie výbehov
INSERT INTO `enclosure` (`name`, `zone`, `last_maintainance`)
VALUES ('Výbeh pre levy', 'Savana', NULL),
       ('Výbeh pre zebry', 'Savana', NULL),
       ('Výbeh pre žirafy', 'Savana', NULL),
       ('Výbeh pre hrochy', 'Savana', NULL),
       ('Výbeh pre tučniaky', 'Polárna', NULL),
       ('Výbeh pre lemury', 'Džungľa', NULL),
       ('Výbeh pre šimpanzy', 'Džungľa', NULL);

-- Vloženie zvierat
INSERT INTO `animal` (`nickname`, `species`, `sex`, `birth_day`, `last_check`, `status`, `enclosure_id`)
VALUES ('Alex', 'Lev', 'MALE', '2010-05-20', NULL, 'ACTIVE', 1),
       ('Marty', 'Zebra', 'MALE', '2011-03-15', NULL, 'ACTIVE', 2),
       ('Melman', 'Žirafa', 'MALE', '2012-01-30', NULL, 'ACTIVE', 3),
       ('Gloria', 'Hroch', 'FEMALE', '2009-08-12', NULL, 'ACTIVE', 4),
       ('Skipper', 'Tučniak', 'MALE', '2015-03-10', NULL, 'ACTIVE', 5),
       ('Kowalski', 'Tučniak', 'MALE', '2015-04-12', NULL, 'ACTIVE', 5),
       ('Rico', 'Tučniak', 'MALE', '2015-05-08', NULL, 'ACTIVE', 5),
       ('Pešiak', 'Tučniak', 'MALE', '2015-06-20', NULL, 'ACTIVE', 5),
       ('Kráľ Julien', 'Lemur', 'MALE', '2013-07-15', NULL, 'ACTIVE', 6),
       ('Maurice', 'Lemur', 'MALE', '2012-12-01', NULL, 'ACTIVE', 6),
       ('Mort', 'Lemur', 'MALE', '2014-03-05', NULL, 'ACTIVE', 6),
       ('Mason', 'Šimpanz', 'MALE', '2014-09-23', NULL, 'ACTIVE', 7),
       ('Phil', 'Šimpanz', 'MALE', '2015-02-05', NULL, 'ACTIVE', 7);

-- Vloženie úloh
INSERT INTO `task` (`name`, `description`, `deadline`, `status`, `user_id`)
VALUES ('Uspatie a kontrola parazitov u leva Alexa',
        'Uspite leva Alexa a skontrolujte, či nemá parazity, poprípade aplikujte lieky proti parazitom.',
        '2025-12-21 09:00:00', 'INCOMPLETE', 5),
       ('Ošetrenie kopyt zebry Marty',
        'Skontrolujte a ošetrite kopytá zebry Marty, odstráňte nečistoty a skontrolujte, či nemajú známky infekcií alebo poranení.',
        '2025-12-16 10:00:00', 'INCOMPLETE', 5),
       ('Podanie liekov žirafe Melmanovi',
        'Podajte lieky žirafe Melmanovi na zlepšenie trávenia a sledujte jeho reakciu.', '2025-12-17 08:00:00',
        'INCOMPLETE', 5),
       ('Uspatie a ošetrenie zubov hrošice Glorie',
        'Uspite hrocha Gloriu a vykonajte kontrolu zubov. Ak sú potrebné, vykonajte ošetrenie alebo extrakciu.',
        '2025-12-18 11:00:00', 'INCOMPLETE', 5),
       ('Zváženie tučniakov',
        'Zvážte všetkých tučniakov. Uistite sa, že každý tučniak má správnu hmotnosť a neprejavuje známky podvýživy alebo zdravotných problémov.',
        '2025-12-15 10:00:00', 'INCOMPLETE', 6),
       ('Odobratie krvi lemurom',
        'Odoberte krv všetkým lemurom. Skontrolujte zdravie zvierat a zabezpečte vzorky pre analýzu.',
        '2025-12-16 09:00:00', 'INCOMPLETE', 6),
       ('Interakcia a tréning so šimpanzmi',
        'Trénujte šimpanzov pomocou základných povelov. Poskytnite im interaktívne hračky a stimulujte ich k pohybu a intelektuálnemu rozvoju.',
        '2025-12-17 14:00:00', 'INCOMPLETE', 6),
       ('Údržba výbehov v zóne savana',
        'Skontrolujte výbehy pre levy, zebry, žirafy a hrochy. Uistite sa, že výbehy sú čisté a že sú dobre bezpečnostne zabezpečené.',
        '2025-12-19 10:00:00', 'INCOMPLETE', 7),
       ('Údržba výbehu pre tučniaky',
        'Skontrolujte a výbeh pre tučniaky. Vymeňte vodu v bazéne a uistite sa, že vo výbehu je vhodná teplota.',
        '2025-12-20 08:00:00', 'INCOMPLETE', 8),
       ('Údržba výbehov v zóne džungľa',
        'Skontrolujte a upravte výbehy pre lemury a šimpanzy. Vyčistite sklenené panely, cez ktoré sa diváci pozerajú do výbehu, aby bol výhľad jasný a bezpečný.',
        '2025-12-21 10:00:00', 'INCOMPLETE', 8);


INSERT INTO `task_has_animal`
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (5, 6),
       (5, 7),
       (5, 8),
       (6, 9),
       (6, 10),
       (6, 11),
       (7, 12),
       (7, 13);

INSERT INTO `task_has_enclosure` (`task_id`, `enclosure_id`)
VALUES (8, 1),
       (8, 2),
       (8, 3),
       (8, 4),
       (9, 5),
       (10, 6),
       (10, 7);

-- Predaj pre pokladníka s ID = 2 (30 lístkov)
INSERT INTO `ticket` (`type`, `purchase_date_time`, `price`, `user_id`)
VALUES
    -- Dnes (striedanie pomeru, 12 lístkov)
    ('Child', '2025-12-08 09:00:00', 4.00, 2),
    ('Child', '2025-12-08 10:00:00', 4.00, 2),
    ('Child', '2025-12-08 11:00:00', 4.00, 2),
    ('Child', '2025-12-08 12:00:00', 4.00, 2),
    ('Student_Senior', '2025-12-08 09:15:00', 7.00, 2),
    ('Student_Senior', '2025-12-08 10:15:00', 7.00, 2),
    ('Adult', '2025-12-08 11:15:00', 10.00, 2),
    ('Adult', '2025-12-08 13:15:00', 10.00, 2),
    ('Child', '2025-12-08 14:15:00', 4.00, 2),
    ('Child', '2025-12-08 15:15:00', 4.00, 2),
    ('Student_Senior', '2025-12-08 16:15:00', 7.00, 2),
    -- Včerajšok (striedanie pomeru, 9 lístkov)
    ('Child', '2025-12-07 09:00:00', 4.00, 2),
    ('Child', '2025-12-07 10:00:00', 4.00, 2),
    ('Adult', '2025-12-07 11:00:00', 10.00, 2),
    ('Student_Senior', '2025-12-07 12:00:00', 7.00, 2),
    ('Student_Senior', '2025-12-07 13:00:00', 7.00, 2),
    ('Child', '2025-12-07 14:00:00', 4.00, 2),
    ('Child', '2025-12-07 15:00:00', 4.00, 2),
    ('Adult', '2025-12-07 16:00:00', 10.00, 2),
    -- Predvčerajšok (striedanie pomeru, 9 lístkov)
    ('Child', '2025-12-06 09:00:00', 4.00, 2),
    ('Adult', '2025-12-06 10:00:00', 10.00, 2),
    ('Student_Senior', '2025-12-06 11:00:00', 7.00, 2),
    ('Child', '2025-12-06 12:00:00', 4.00, 2),
    ('Child', '2025-12-06 13:00:00', 4.00, 2),
    ('Adult', '2025-12-06 14:00:00', 10.00, 2),
    ('Student_Senior', '2025-12-06 15:00:00', 7.00, 2),
    ('Adult', '2025-12-06 16:00:00', 10.00, 2);

INSERT INTO `ticket` (`type`, `purchase_date_time`, `price`, `user_id`)
VALUES
    -- Dnes (2025-12-08, 25 lístkov)
    ('Child', '2025-12-08 09:00:00', 4.00, 3),
    ('Child', '2025-12-08 10:00:00', 4.00, 3),
    ('Child', '2025-12-08 11:00:00', 4.00, 3),
    ('Child', '2025-12-08 12:00:00', 4.00, 3),
    ('Child', '2025-12-08 13:00:00', 4.00, 3),
    ('Student_Senior', '2025-12-08 09:30:00', 7.00, 3),
    ('Student_Senior', '2025-12-08 10:30:00', 7.00, 3),
    ('Student_Senior', '2025-12-08 11:30:00', 7.00, 3),
    ('Adult', '2025-12-08 12:30:00', 10.00, 3),
    ('Adult', '2025-12-08 14:30:00', 10.00, 3),
    ('Child', '2025-12-08 15:00:00', 4.00, 3),
    ('Child', '2025-12-08 15:30:00', 4.00, 3),
    ('Student_Senior', '2025-12-08 16:00:00', 7.00, 3),
    ('Adult', '2025-12-08 16:30:00', 10.00, 3),

    -- Včerajšok (2025-12-07, 10 lístkov)
    ('Child', '2025-12-07 09:00:00', 4.00, 3),
    ('Child', '2025-12-07 10:00:00', 4.00, 3),
    ('Child', '2025-12-07 11:00:00', 4.00, 3),
    ('Adult', '2025-12-07 12:00:00', 10.00, 3),
    ('Student_Senior', '2025-12-07 09:30:00', 7.00, 3),
    ('Student_Senior', '2025-12-07 10:30:00', 7.00, 3),
    ('Child', '2025-12-07 11:30:00', 4.00, 3),
    ('Child', '2025-12-07 12:30:00', 4.00, 3),
    ('Student_Senior', '2025-12-07 13:00:00', 7.00, 3),
    ('Adult', '2025-12-07 14:00:00', 10.00, 3),

    -- Predvčerajšok (2025-12-06, 15 lístkov)
    ('Child', '2025-12-06 09:00:00', 4.00, 3),
    ('Child', '2025-12-06 10:00:00', 4.00, 3),
    ('Student_Senior', '2025-12-06 09:30:00', 7.00, 3),
    ('Child', '2025-12-06 10:30:00', 4.00, 3),
    ('Child', '2025-12-06 11:30:00', 4.00, 3),
    ('Student_Senior', '2025-12-06 12:00:00', 7.00, 3),
    ('Adult', '2025-12-06 13:00:00', 10.00, 3),
    ('Adult', '2025-12-06 14:00:00', 10.00, 3),
    ('Student_Senior', '2025-12-06 15:00:00', 7.00, 3);

INSERT INTO `ticket` (`type`, `purchase_date_time`, `price`, `user_id`)
VALUES
    -- Dnes (2025-12-08, 10 lístkov)
    ('Child', '2025-12-08 09:00:00', 4.00, 4),
    ('Child', '2025-12-08 10:00:00', 4.00, 4),
    ('Student_Senior', '2025-12-08 10:30:00', 7.00, 4),
    ('Adult', '2025-12-08 12:30:00', 10.00, 4),
    ('Child', '2025-12-08 13:00:00', 4.00, 4),
    ('Child', '2025-12-08 15:00:00', 4.00, 4),

    -- Včerajšok (2025-12-07, 12 lístkov)
    ('Child', '2025-12-07 09:15:00', 4.00, 4),
    ('Adult', '2025-12-07 11:15:00', 10.00, 4),
    ('Student_Senior', '2025-12-07 09:30:00', 7.00, 4),
    ('Student_Senior', '2025-12-07 10:30:00', 7.00, 4),
    ('Child', '2025-12-07 12:30:00', 4.00, 4),
    ('Student_Senior', '2025-12-07 13:00:00', 7.00, 4),
    ('Child', '2025-12-07 14:00:00', 4.00, 4),
    ('Adult', '2025-12-07 15:00:00', 10.00, 4),
    ('Adult', '2025-12-07 16:00:00', 10.00, 4),

    -- Predvčerajšok (2025-12-06, 18 lístkov)
    ('Child', '2025-12-06 09:00:00', 4.00, 4),
    ('Child', '2025-12-06 10:00:00', 4.00, 4),
    ('Student_Senior', '2025-12-06 09:30:00', 7.00, 4),
    ('Child', '2025-12-06 10:30:00', 4.00, 4),
    ('Adult', '2025-12-06 13:00:00', 10.00, 4),
    ('Student_Senior', '2025-12-06 13:30:00', 7.00, 4),
    ('Child', '2025-12-06 14:00:00', 4.00, 4),
    ('Student_Senior', '2025-12-06 15:00:00', 7.00, 4),
    ('Adult', '2025-12-06 16:30:00', 10.00, 4);

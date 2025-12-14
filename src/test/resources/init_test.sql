-- Test init script for MySQL tests

-- Vytvor databazu pre testy
CREATE DATABASE IF NOT EXISTS `zoopervisor_test` DEFAULT CHARACTER SET utf8;
USE `zoopervisor_test`;

-- -----------------------------------------------------
-- Table `user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT,
                                      `first_name` VARCHAR(100) NULL,
                                      `last_name` VARCHAR(100) NOT NULL,
                                      `gender` ENUM('MALE', 'FEMALE', 'OTHER', 'UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',
                                      `birth_day` DATE NOT NULL,
                                      `role` ENUM('INACTIVE', 'CASHIER', 'MAINTAINER', 'ZOOKEEPER', 'MANAGER', 'ADMIN') NOT NULL DEFAULT 'INACTIVE',
                                      `email` VARCHAR(100) NOT NULL,
                                      `password` VARCHAR(100) NULL,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `enclosure`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `enclosure` (
                                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                                           `name` VARCHAR(100) NOT NULL,
                                           `zone` VARCHAR(100) NOT NULL,
                                           `last_maintainance` DATETIME NULL,
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `animal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `animal` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                        `nickname` VARCHAR(100) NULL,
                                        `species` VARCHAR(100) NOT NULL,
                                        `sex` ENUM('MALE', 'FEMALE', 'HERMAPHRODITE', 'UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',
                                        `birth_day` DATE NOT NULL,
                                        `last_check` DATETIME NULL,
                                        `status` ENUM('ACTIVE', 'INACTIVE', 'TREATMENT') NOT NULL DEFAULT 'ACTIVE',
                                        `enclosure_id` BIGINT NOT NULL,
                                        PRIMARY KEY (`id`),
                                        INDEX `fk_animal_enclosure_idx` (`enclosure_id` ASC),
                                        CONSTRAINT `fk_animal_enclosure` FOREIGN KEY (`enclosure_id`) REFERENCES `enclosure` (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `ticket`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `ticket` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                        `type` VARCHAR(100) NOT NULL,
                                        `purchase_date_time` DATETIME NOT NULL,
                                        `price` DECIMAL(10,2) NOT NULL,
                                        `user_id` BIGINT NOT NULL,
                                        PRIMARY KEY (`id`),
                                        INDEX `fk_ticket_user_idx` (`user_id` ASC),
                                        CONSTRAINT `fk_ticket_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT,
                                      `name` VARCHAR(100) NOT NULL,
                                      `description` TEXT NOT NULL,
                                      `deadline` DATETIME NOT NULL,
                                      `status` ENUM('INCOMPLETE', 'COMPLETED') NOT NULL DEFAULT 'INCOMPLETE',
                                      `user_id` BIGINT NOT NULL,
                                      PRIMARY KEY (`id`),
                                      INDEX `fk_task_user_idx` (`user_id` ASC),
                                      CONSTRAINT `fk_task_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `task_has_animal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task_has_animal` (
                                                 `task_id` BIGINT NOT NULL,
                                                 `animal_id` BIGINT NOT NULL,
                                                 PRIMARY KEY (`task_id`, `animal_id`),
                                                 INDEX `fk_task_has_animal_task_idx` (`task_id` ASC),
                                                 INDEX `fk_task_has_animal_animal_idx` (`animal_id` ASC),
                                                 CONSTRAINT `fk_task_has_animal_task` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`),
                                                 CONSTRAINT `fk_task_has_animal_animal` FOREIGN KEY (`animal_id`) REFERENCES `animal` (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `task_has_enclosure`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `task_has_enclosure` (
                                                    `task_id` BIGINT NOT NULL,
                                                    `enclosure_id` BIGINT NOT NULL,
                                                    PRIMARY KEY (`task_id`, `enclosure_id`),
                                                    INDEX `fk_task_has_enclosure_task_idx` (`task_id` ASC),
                                                    INDEX `fk_task_has_enclosure_enclosure_idx` (`enclosure_id` ASC),
                                                    CONSTRAINT `fk_task_has_enclosure_task` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`),
                                                    CONSTRAINT `fk_task_has_enclosure_enclosure` FOREIGN KEY (`enclosure_id`) REFERENCES `enclosure` (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Sample users for tests
-- -----------------------------------------------------
INSERT INTO `user` (`first_name`, `last_name`, `gender`, `birth_day`, `role`, `email`, `password`) VALUES
                                                                                                       ('Marek','Tóth','MALE','1979-06-30','MANAGER','marek.toth@test','123'),
                                                                                                       ('Jana','Nováková','FEMALE','1985-04-20','CASHIER','jana.novakova@test','123');
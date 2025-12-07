-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema zoopervisor
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema zoopervisor
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `zoopervisor` DEFAULT CHARACTER SET utf8 ;
USE `zoopervisor` ;

-- -----------------------------------------------------
-- Table `zoopervisor`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zoopervisor`.`user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `first_name` VARCHAR(100) NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `gender` ENUM('MALE', 'FEMALE', 'OTHER', 'UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',
    `birth_day` DATE NOT NULL,
    `role` ENUM('INACTIVE', 'CASHIER', 'MAINTAINER', 'ZOOKEEPER', 'MANAGER', 'ADMIN') NOT NULL DEFAULT 'INACTIVE',
    `email` VARCHAR(100) NULL,
    `password` VARCHAR(100) NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE)
    ENGINE = InnoDB;

INSERT INTO `user` VALUES
    (1,'Ján','Novák','MALE','1990-05-12','MAINTAINER',NULL,NULL),
    (2,'Eva','Hrušková','FEMALE','1985-11-03','CASHIER',NULL,NULL),
    (3,'Alex','Kováč','MALE','2000-02-20','ZOOKEEPER',NULL,NULL);

-- -----------------------------------------------------
-- Table `zoopervisor`.`enclosure`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zoopervisor`.`enclosure` (
     `id` BIGINT NOT NULL AUTO_INCREMENT,
     `name` VARCHAR(100) NOT NULL,
     `zone` VARCHAR(100) NOT NULL,
     `last_maintainance` DATETIME NULL,
     PRIMARY KEY (`id`))
    ENGINE = InnoDB;

INSERT INTO `enclosure` VALUES
    (1,'Výbeh pre levy','Savana',NULL),
    (2,'Výbeh pre zebry','Savana',NULL),
    (3,'Výbeh pre žirafy','Savana',NULL),
    (4,'Výbeh pre hrochy','Savana',NULL),
    (5,'Výbeh pre tučniaky','Polárna',NULL);

-- -----------------------------------------------------
-- Table `zoopervisor`.`animal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zoopervisor`.`animal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `nickname` VARCHAR(100) NULL,
    `species` VARCHAR(100) NOT NULL,
    `sex` ENUM('MALE', 'FEMALE', 'HERMAPHRODITE', 'UNKNOWN') NOT NULL DEFAULT 'UNKNOWN',
    `birth_day` DATE NOT NULL,
    `last_check` DATETIME NULL,
    `enclosure_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_animal_enclosure1_idx` (`enclosure_id` ASC) VISIBLE,
    CONSTRAINT `fk_animal_enclosure1`
    FOREIGN KEY (`enclosure_id`)
    REFERENCES `zoopervisor`.`enclosure` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;

INSERT INTO `animal` VALUES
    (1,'Alex','Lev','MALE','2010-05-20',NULL,1),
    (2,'Marty','Zebra','MALE','2011-03-15',NULL,2),
    (3,'Melman','Žirafa','MALE','2012-01-30',NULL,3),
    (4,'Gloria','Hroch','FEMALE','2009-08-12',NULL,4),
    (5,'Skipper','Tučniak','MALE','2015-03-10',NULL,5),
    (6,'Kowalski','Tučniak','MALE','2015-04-12',NULL,5),
    (7,'Rico','Tučniak','MALE','2015-05-08',NULL,5),
    (8,'Pešiak','Tučniak','MALE','2015-06-20',NULL,5);

-- -----------------------------------------------------
-- Table `zoopervisor`.`ticket`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zoopervisor`.`ticket` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(100) NOT NULL,
    `purchase_date_time` DATETIME NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `user_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_ticket_user_idx` (`user_id` ASC) VISIBLE,
    CONSTRAINT `fk_ticket_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `zoopervisor`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `zoopervisor`.`task`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zoopervisor`.`task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT NOT NULL,
    `deadline` DATETIME NOT NULL,
    `user_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_task_user1_idx` (`user_id` ASC) VISIBLE,
    CONSTRAINT `fk_task_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `zoopervisor`.`user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;

INSERT INTO `task` VALUES
    (1,'Kontrola výbehu tučniakov','Skontrolovať stav výbehu tučniakov a opraviť svetlo.','2025-12-20 12:00:00',1),
    (2,'Správa účtov','Skontrolovať pokladňu a pripraviť report.','2025-12-21 17:00:00',2),
    (3,'Vakcinácia leva','Zaočkovať leva a skontrolovať či nemá parazitov.','2025-12-19 09:00:00',3);

-- -----------------------------------------------------
-- Table `zoopervisor`.`task_has_animal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zoopervisor`.`task_has_animal` (
    `task_id` BIGINT NOT NULL,
    `animal_id` BIGINT NOT NULL,
    PRIMARY KEY (`task_id`, `animal_id`),
    INDEX `fk_task_has_animal_animal1_idx` (`animal_id` ASC) VISIBLE,
    INDEX `fk_task_has_animal_task1_idx` (`task_id` ASC) VISIBLE,
    CONSTRAINT `fk_task_has_animal_task1`
    FOREIGN KEY (`task_id`)
    REFERENCES `zoopervisor`.`task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_task_has_animal_animal1`
    FOREIGN KEY (`animal_id`)
    REFERENCES `zoopervisor`.`animal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;

INSERT INTO `task_has_animal` VALUES
    (3,1);

-- -----------------------------------------------------
-- Table `zoopervisor`.`task_has_enclosure`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `zoopervisor`.`task_has_enclosure` (
    `task_id` BIGINT NOT NULL,
    `enclosure_id` BIGINT NOT NULL,
    PRIMARY KEY (`task_id`, `enclosure_id`),
    INDEX `fk_task_has_enclosure_enclosure1_idx` (`enclosure_id` ASC) VISIBLE,
    INDEX `fk_task_has_enclosure_task1_idx` (`task_id` ASC) VISIBLE,
    CONSTRAINT `fk_task_has_enclosure_task1`
    FOREIGN KEY (`task_id`)
    REFERENCES `zoopervisor`.`task` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_task_has_enclosure_enclosure1`
    FOREIGN KEY (`enclosure_id`)
    REFERENCES `zoopervisor`.`enclosure` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;

INSERT INTO `task_has_enclosure` VALUES
    (1,5);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
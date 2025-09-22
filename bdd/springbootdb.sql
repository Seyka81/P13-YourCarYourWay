-- =====================================================================
-- Base : springbootdb
-- Schéma propre (tables en premier), données à la fin
-- Objectifs : 3NF, intégrité référentielle, indexes utiles
-- =====================================================================

-- ------------------------------------------------------
-- DROP de la BDD (si elle existe) et création
-- ------------------------------------------------------
DROP DATABASE IF EXISTS `springbootdb`;
CREATE DATABASE `springbootdb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `springbootdb`;

-- ------------------------------------------------------
-- Table : users
-- ------------------------------------------------------
CREATE TABLE `users` (
  `id`            BIGINT NOT NULL AUTO_INCREMENT,
  `email`         VARCHAR(255) NOT NULL,
  `name`          VARCHAR(255) NOT NULL,
  `password`      VARCHAR(255) NOT NULL,
  `role`          ENUM('SUPPORT','USER') NOT NULL DEFAULT 'USER',
  `created_at`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_name` (`name`)
);

-- ------------------------------------------------------
-- Table : chat
-- ------------------------------------------------------
CREATE TABLE `chat` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `owner` VARCHAR(255) NOT NULL,                 -- FK vers users.email
  `status` ENUM('CLOSE','OPEN') NOT NULL DEFAULT 'OPEN',
  `title` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_chat_owner` (`owner`),
  KEY `idx_chat_status` (`status`),
  CONSTRAINT `fk_chat_owner_user`
    FOREIGN KEY (`owner`) REFERENCES `users`(`email`)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
);

-- ------------------------------------------------------
-- Table : message
-- ------------------------------------------------------
CREATE TABLE `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `content` VARCHAR(1024) NOT NULL,
  `sender` VARCHAR(255) NOT NULL,                -- FK vers users.name
  `sent_at` DATETIME(6) NOT NULL DEFAULT (CURRENT_TIMESTAMP(6)),
  `chat_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_message_chat_id` (`chat_id`),
  KEY `idx_message_chat_sentat` (`chat_id`,`sent_at`),
  KEY `idx_message_sender` (`sender`),
  CONSTRAINT `fk_message_chat`
    FOREIGN KEY (`chat_id`) REFERENCES `chat`(`id`)
    ON UPDATE RESTRICT ON DELETE CASCADE,
  CONSTRAINT `fk_message_sender_user`
    FOREIGN KEY (`sender`) REFERENCES `users`(`name`)
    ON UPDATE CASCADE ON DELETE RESTRICT
);


-- ------------------------------------------------------
-- Table : addresses
-- ------------------------------------------------------
CREATE TABLE `addresses` (
  `id`           BIGINT NOT NULL AUTO_INCREMENT,
  `street_number` INT NOT NULL,
  `street_name`  VARCHAR(255) NOT NULL,
  `postcode`     VARCHAR(20) NOT NULL,
  `state`        VARCHAR(50) NOT NULL,
  `country`      VARCHAR(50) NOT NULL,
  `created_at`   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_address_postcode` (`postcode`),
  KEY `idx_address_country_state` (`country`,`state`),
  UNIQUE KEY `uk_address_unique` (`street_number`,`street_name`,`postcode`,`country`)
);

-- ------------------------------------------------------
-- Table : agencies
-- ------------------------------------------------------
CREATE TABLE `agencies` (
  `id`           BIGINT NOT NULL AUTO_INCREMENT,
  `address_id`   BIGINT NOT NULL,
  `name`         VARCHAR(255) NOT NULL,
  `created_at`   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agency_name_address` (`name`,`address_id`),
  KEY `idx_agency_address` (`address_id`),
  CONSTRAINT `fk_agency_address`
    FOREIGN KEY (`address_id`) REFERENCES `addresses`(`id`)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT
);

-- ------------------------------------------------------
-- Table : vehicles
-- ------------------------------------------------------
CREATE TABLE `vehicles` (
  `id`                 BIGINT NOT NULL AUTO_INCREMENT,
  `acriss_code`        VARCHAR(4) NOT NULL,
  `matriculation`      VARCHAR(20) NOT NULL,
  `brand`              VARCHAR(100) NOT NULL,
  `category`           VARCHAR(255) NOT NULL,
  `type`               VARCHAR(255) NOT NULL,
  `transmission`       VARCHAR(255) NOT NULL,
  `passenger_capacity` VARCHAR(2) NOT NULL,
  `fuel`               VARCHAR(50) NOT NULL,
  `year`               SMALLINT NOT NULL,
  `image_url`          VARCHAR(2048),
  `created_at`         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vehicle_matriculation` (`matriculation`),
  KEY `idx_vehicle_brand_year` (`brand`,`year`)
);


-- ------------------------------------------------------
-- Table : rentals
-- ------------------------------------------------------
CREATE TABLE `rentals` (
  `id`                   BIGINT NOT NULL AUTO_INCREMENT,
  `agency_id`            BIGINT NOT NULL,
  `vehicle_id`           BIGINT NOT NULL,
  `client_user_id`       BIGINT NOT NULL,
  `depart_date_time`  DATETIME(6) NOT NULL,
  `arrival_date_time`    DATETIME(6) NOT NULL,
  `price`                DECIMAL(10,2) NOT NULL,
  `created_at`           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_rentals_agency` (`agency_id`),
  KEY `idx_rentals_vehicle` (`vehicle_id`),
  KEY `idx_rentals_client` (`client_user_id`),
  KEY `idx_rentals_vehicle_depart` (`vehicle_id`,`depart_date_time`),
  CONSTRAINT `fk_rentals_agency`
    FOREIGN KEY (`agency_id`)  REFERENCES `agencies`(`id`)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `fk_rentals_vehicle`
    FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles`(`id`)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `fk_rentals_client`
    FOREIGN KEY (`client_user_id`) REFERENCES `users`(`id`)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT `chk_rentals_price_nonneg`
    CHECK (`price` >= 0),
  CONSTRAINT `chk_rentals_time_order`
    CHECK (`arrival_date_time` > `depart_date_time`)
);

-- =====================================================================
-- Données (inserts) — respecter l'ordre : users -> chat -> message
-- =====================================================================

-- Données : users
INSERT INTO `users`
  (`id`, `created_at`, `email`,       `name`,          `password`,                                                          `role`,    `updated_at`)
VALUES
  (1,    '2025-09-20', 'test@test.fr','Supportest-1',  '$2a$10$JV5o6F/RzObSqzyTONkSCO0Whl6Do8K61rSkE.DkwslhhSxos40Se',      'SUPPORT', '2025-09-20'),
  (2,    '2025-09-20', 'aze@aze.fr',  'Clientaze-1',   '$2a$10$kf8rAN9gDje4Hz/QEfZ2ZexAgxNjm4quPoU07BmLBJEsTG/EUfREi',      'USER',    '2025-09-20');

-- Données : chat
INSERT INTO `chat` (`id`, `owner`,       `status`, `title`) VALUES
  (1,               'aze@aze.fr',        'OPEN',   'Problème');

-- Données : message
INSERT INTO `message` (`id`, `content`,                                `sender`,         `sent_at`,                          `chat_id`) VALUES
  (1,                 'Bonjour j ai un probleme',                      'Clientaze-1',    '2025-09-20 23:11:46.422288',       1),
  (2,                 'Bonjour, Pouvez vous détailler votre soucis.',   'Supportest-1',   '2025-09-20 23:12:18.872825',       1);

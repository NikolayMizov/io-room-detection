CREATE TABLE `Meeting_Room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(100) NOT NULL,
    `current_capacity` INT NOT NULL DEFAULT 0,
    `max_capacity` INT NOT NULL DEFAULT 0,
    `camera_connection_id` BIGINT,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`camera_connection_id`) REFERENCES `Camera_Connection` (`id`),
    CONSTRAINT `email_unique` UNIQUE (`email`)
);
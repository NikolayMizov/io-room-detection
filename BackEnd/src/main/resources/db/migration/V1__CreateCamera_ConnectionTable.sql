CREATE TABLE `Camera_Connection` (
 `id` BIGINT NOT NULL AUTO_INCREMENT,
 `mac_address` VARCHAR(17) NOT NULL,
 PRIMARY KEY (`id`),
 CONSTRAINT `mac_address_unique` UNIQUE (`mac_address`)
);
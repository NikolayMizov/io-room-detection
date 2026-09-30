CREATE TABLE `Reservation` (
   `id` VARCHAR(152) NOT NULL,
   `room_id` BIGINT NOT NULL,
   `max_occupancy` INT NOT NULL,
   PRIMARY KEY (`id`),
   FOREIGN KEY (`room_id`) REFERENCES `Meeting_Room` (`id`)
);
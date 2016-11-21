CREATE TABLE IF NOT EXISTS `games`( `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                                    `map_id` varchar(36) NOT NULL,
                                    `score` bigint(20) NOT NULL,
                                    `nick` varchar(24) NOT NULL);

CREATE TABLE IF NOT EXISTS `config`(`key` varchar(36) PRIMARY KEY,
                                    `value` TEXT);

CREATE TABLE IF NOT EXISTS `users`( `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                                    `nick` VARCHAR(64),

                                    `type` INTEGER);

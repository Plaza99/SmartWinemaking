DROP DATABASE IF EXISTS smart_wine;

CREATE DATABASE smart_wine;

USE smart_wine;

CREATE TABLE `float level` (
	`id`		INT NOT NULL,
	`timestamp`	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`level`		INT NOT NULL,
	
	PRIMARY KEY (`id`, `timestamp`)
);

CREATE TABLE `temperature`(
	`id`		INT NOT NULL,
	`timestamp`	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`value`		FLOAT NOT NULL,
	
	PRIMARY KEY (`id`, `timestamp`)
);

CREATE TABLE `actuator` (
    -- `id`        INT PRIMARY KEY,
    `ip`        VARBINARY(16) PRIMARY KEY,
    `type`      VARCHAR(40) NOT NULL
);

DROP DATABASE IF EXISTS smart_wine;

CREATE DATABASE smart_wine;

USE smart_wine;

CREATE TABLE `float_level` (
	`id`		INT NOT NULL,
	`timestamp`	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`level`		INT NOT NULL,
	
	PRIMARY KEY (`id`, `timestamp`)
);

CREATE TABLE `co2` (
	`id`		INT NOT NULL,
	`timestamp`	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`value`		FLOAT NOT NULL,
	
	PRIMARY KEY (`id`, `timestamp`)
);

CREATE TABLE `temperature`(
	`id`		INT NOT NULL,
	`timestamp`	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`value`		FLOAT NOT NULL,
	
	PRIMARY KEY (`id`, `timestamp`)
);

CREATE TABLE `actuator` (
    `ip`        VARCHAR(40) NOT NULL,
    `type`      VARCHAR(40) NOT NULL,
	
	PRIMARY KEY (`ip`)
);

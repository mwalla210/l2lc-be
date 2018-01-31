CREATE TABLE Address (
	id INTEGER PRIMARY KEY,
	street TEXT NOT NULL,
	city TEXT NOT NULL,
	state TEXT,
	country TEXT,
	zip TEXT NOT NULL,
	UNIQUE (street, city)
);

CREATE TABLE Customer (
	id INTEGER PRIMARY KEY,
	name TEXT NOT NULL,
	email TEXT UNIQUE,
	website TEXT,
	shipping_addr_id INTEGER NOT NULL,
	billing_addr_id INTEGER,
	is_past_due BOOLEAN NOT NULL,
	phone TEXT NOT NULL UNIQUE,
	FOREIGN KEY (shipping_addr_id) REFERENCES Address (id),
	FOREIGN KEY (billing_addr_id) REFERENCES Address (id)
);

CREATE TABLE Employee (
	id INTEGER PRIMARY KEY,
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL
);

CREATE TABLE CostCenter (
	id INTEGER PRIMARY KEY,
	name TEXT NOT NULL UNIQUE
);

CREATE TABLE JobType (
	id INTEGER PRIMARY KEY,
	title TEXT NOT NULL UNIQUE,
	cost_center_id INTEGER,
	FOREIGN KEY (cost_center_id) REFERENCES CostCenter (id)
);

CREATE TABLE Priority (
	id INTEGER PRIMARY KEY,
	name TEXT NOT NULL UNIQUE
);

CREATE TABLE Project (
	id INTEGER PRIMARY KEY,
	job_type_id INTEGER NOT NULL,
	customer_id INTEGER,
	created DATE NOT NULL,
	finished DATE,
	title TEXT NOT NULL,
	description TEXT,
	priority INTEGER,
	part_count INTEGER,
	ref_name TEXT,
	FOREIGN KEY (job_type_id) REFERENCES JobType (id),
	FOREIGN KEY (customer_id) REFERENCES Customer (id),
	FOREIGN KEY (priority) REFERENCES Priority (id)
);

CREATE TABLE Station (
	id INTEGER PRIMARY KEY,
	name TEXT NOT NULL UNIQUE
);

CREATE TABLE ProjectStatus (
	id INTEGER PRIMARY KEY,
	title TEXT NOT NULL
);

CREATE TABLE Task (
	id INTEGER PRIMARY KEY,
	title TEXT NOT NULL,
	is_required BOOLEAN NOT NULL,
	station_id INTEGER,
	project_id INTEGER NOT NULL,
	FOREIGN KEY (station_id) REFERENCES Station (id),
	FOREIGN KEY (project_id) REFERENCES Project (id)
);
	
CREATE TABLE Login (
	id INTEGER PRIMARY KEY,
	username TEXT NOT NULL,
	password TEXT NOT NULL,
	is_admin INTEGER NOT NULL,
	station_id INTEGER,
	FOREIGN KEY (station_id) REFERENCES Station (id)
);

CREATE TABLE ProjectTimeEntry (
	id INTEGER PRIMARY KEY,
	project_id INTEGER NOT NULL,
	employee_id INTEGER NOT NULL,
	station_id INTEGER NOT NULL,
	created DATE NOT NULL,
	FOREIGN KEY (project_id) REFERENCES Project (id),
	FOREIGN KEY (employee_id) REFERENCES Employee (id),
	FOREIGN KEY (station_id) REFERENCES Station (id)
);

CREATE TABLE ProjectRework (
	id INTEGER PRIMARY KEY,
	description TEXT,
	created DATE NOT NULL,
	project_id INTEGER NOT NULL,
	employee_id INTEGER,
	FOREIGN KEY (project_id) REFERENCES Project (id),
	FOREIGN KEY (employee_id) REFERENCES Employee (id)
);

CREATE TABLE ProjectHold (
	id INTEGER PRIMARY KEY,
	description TEXT,
	project_id INTEGER NOT NULL,
	employee_id INTEGER,
	FOREIGN KEY (project_id) REFERENCES Project (id),
	FOREIGN KEY (employee_id) REFERENCES Employee (id)
);

/* Inserting stations into Station table */
INSERT INTO Station (id, name) VALUES (1, "Receiving");
INSERT INTO Station (id, name) VALUES (2, "Ticketing");
INSERT INTO Station (id, name) VALUES (3, "Preperation");
INSERT INTO Station (id, name) VALUES (4, "Coating and Curing");
INSERT INTO Station (id, name) VALUES (5, "Quality Control and Packaging");

/* Insert priorities into Priority table */
INSERT INTO Priority (id, name) VALUES (1, "Low");
INSERT INTO Priority (id, name) VALUES (2, "High");

/* Insert cost centers into CostCenter table */
INSERT INTO CostCenter (id, name) VALUES (1, "APC Job");
INSERT INTO CostCenter (id, name) VALUES (2, "Decorative Job");
INSERT INTO CostCenter (id, name) VALUES (3, "Maintenance");
INSERT INTO CostCenter (id, name) VALUES (4, "Administration");
INSERT INTO CostCenter (id, name) VALUES (5, "Production");
INSERT INTO CostCenter (id, name) VALUES (6, "Research and Development");
INSERT INTO CostCenter (id, name) VALUES (7, "Other");

/* Insert job types into JobType table */
INSERT INTO JobType (id, title, cost_center_id) VALUES (1, "Piston", 1);
INSERT INTO JobType (id, title, cost_center_id) VALUES (2, "Turbo", 1);
INSERT INTO JobType (id, title, cost_center_id) VALUES (3, "Rotor", 1);
INSERT INTO JobType (id, title, cost_center_id) VALUES (4, "Pump", 1);
INSERT INTO JobType (id, title, cost_center_id) VALUES (5, "Avaslick", 1);
INSERT INTO JobType (id, title, cost_center_id) VALUES (6, "Specialty", 1);



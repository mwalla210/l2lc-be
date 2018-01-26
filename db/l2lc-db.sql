CREATE TABLE Address (
	id INTEGER PRIMARY KEY,
	street TEXT,
	city TEXT,
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
	is_past_due BOOLEAN,
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
	name TEXT NOT NULL
);

CREATE TABLE JobType (
	id INTEGER PRIMARY KEY,
	title TEXT NOT NULL,
	cost_center_id INTEGER,
	FOREIGN KEY (cost_center_id) REFERENCES CostCenter (id)
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
	name TEXT NOT NULL
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

INSERT INTO Station VALUES (1, "Receiving");
INSERT INTO Station VALUES (2, "Ticketing");
INSERT INTO Station VALUES (3, "Preperation");
INSERT INTO Station VALUES (4, "Coating and Curing");
INSERT INTO Station VALUES (5, "Quality Control and Packaging");



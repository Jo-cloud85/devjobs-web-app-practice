DROP DATABASE IF EXISTS appliedJobs;

CREATE DATABASE appliedJobs;

USE appliedJobs;

CREATE TABLE jobs (
    application_id CHAR(8) NOT NULL PRIMARY KEY,
    id INT NOT NULL,
    company VARCHAR(32) NOT NULL,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL,
    mobileNumber VARCHAR(16) NOT NULL,
    position VARCHAR(64) NOT NULL,
    startDate DATE NOT NULL,
    feedback TEXT NOT NULL
);

-- When adding this database to Railway, comment this off as we are using Railway root user
grant all privileges on appliedJobs.* to 'abcde'@'%';
flush privileges;
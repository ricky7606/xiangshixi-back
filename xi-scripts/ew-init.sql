DROP DATABASE if exists eyewater;
GRANT USAGE ON eyewater.* TO 'eyewater'@'localhost';
DROP USER 'eyewater'@'localhost';
CREATE DATABASE eyewater DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE USER 'eyewater'@'localhost' identified by 'ddcreative';
GRANT all privileges on eyewater.* to 'eyewater'@'localhost';
flush privileges;

use eyewater;


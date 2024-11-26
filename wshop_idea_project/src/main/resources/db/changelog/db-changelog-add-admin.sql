--liquibase formatted sql
--changeset baturkin:10
INSERT INTO "User" (username,password,roleid)
VALUES ('admin','{noop}admin',1);
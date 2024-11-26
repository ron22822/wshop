--liquibase formatted sql
--changeset baturkin:9
INSERT INTO "Role" (rolename)
VALUES ('ADMIN'),
        ('USER');

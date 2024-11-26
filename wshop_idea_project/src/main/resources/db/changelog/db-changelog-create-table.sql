--liquibase formatted sql
--changeset baturkin:1
CREATE TABLE "Role" (
    roleid SERIAL PRIMARY KEY,
    rolename VARCHAR(10) NOT NULL
);
--changeset baturkin:2
CREATE TABLE "User" (
    userid SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roleid INT REFERENCES "Role"(roleid) ON DELETE SET NULL,
    email VARCHAR(255)
);
--changeset baturkin:3
CREATE TABLE "Profile" (
    profileid SERIAL PRIMARY KEY,
    userid INT UNIQUE REFERENCES "User"(userid) ON DELETE CASCADE,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    birthday DATE,
    gender VARCHAR(50)
);
--changeset baturkin:4
CREATE TABLE "Order" (
    orderid SERIAL PRIMARY KEY,
    userid INT REFERENCES "User"(userid) ON DELETE CASCADE,
    orderdate DATE,
    status VARCHAR(10),
    totalprice DECIMAL(10, 2),
    positioncount INT
);
--changeset baturkin:5
CREATE TABLE "Category" (
    categoryid SERIAL PRIMARY KEY,
    categoryname VARCHAR(255) NOT NULL,
    info TEXT
);
--changeset baturkin:6
CREATE TABLE "Supplier" (
    supplierid SERIAL PRIMARY KEY,
    suppliername VARCHAR(255) NOT NULL,
    contactinfo TEXT
);
--changeset baturkin:7
CREATE TABLE "Product" (
    productid SERIAL PRIMARY KEY,
    productname VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    totalquantity INT NOT NULL,
    activity VARCHAR(10) NOT NULL,
    categoryid INT REFERENCES "Category"(categoryid) ON DELETE SET NULL,
    supplierid INT REFERENCES "Supplier"(supplierid) ON DELETE SET NULL
);
--changeset baturkin:8
CREATE TABLE "OrderItem" (
    orderid INT,
    productid INT,
    itemcount INT NOT NULL,
    PRIMARY KEY (orderid, productid),
    FOREIGN KEY (orderid) REFERENCES "Order"(orderid) ON DELETE CASCADE,
    FOREIGN KEY (productid) REFERENCES "Product"(productid) ON DELETE CASCADE
);

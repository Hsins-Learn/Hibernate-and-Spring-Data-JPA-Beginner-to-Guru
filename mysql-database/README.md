# MySQL Database

- [Overview](#overview)
  - [Database and Database Management Systems](#database-and-database-management-systems)
  - [Types of Databases](#types-of-databases)
  - [MySQL History](#mysql-history)
- [Relational Database Concepts](#relational-database-concepts)
  - [Introduction](#introduction)
  - [SQL (Structured Query Language)](#sql-structured-query-language)
  - [Primary Key](#primary-key)
  - [Database Table Relations](#database-table-relations)
  - [Database Constraints](#database-constraints)
  - [Database Transactions and ACID](#database-transactions-and-acid)
  - [Locking](#locking)
  - [Administration Best Practices](#administration-best-practices)
- [Data Mapping SQL to Java](#data-mapping-sql-to-java)
- [Create Schema and User for Spring Boot](#create-schema-and-user-for-spring-boot)

## Overview

### Database and Database Management Systems

A **Database** is a set of related data and how it is organized. Tracking data with the spreadsheet could be considered as a database. It would be a set of related data that could grow over time and served as record.

A **Database Management Systems** (DBMS) are specialized computer programs for databases. The DBMSs have 4 important characteristics:

- **Data Definition**: define the data being tracked.
- **Data Manipulation**: add, update or remove data.
- **Data Retrieval**: extract and report on the data in the database.
- **Administration**: define users on the system, security, monitoring and system administration.

### Types of Databases

There are a number of different types of databases. Some are general purpose, others are very specialized:

- **Flat File Database**: Data is kept in a file on the operating system. Very simple, generally considered out-dated.
- **Relational Database**: Data is kept in database tables, which have "relationship" to each other.
- **Hierarchical Database**: Data is kept in a tree-like structure.
- **NoSQL Database**: This segment is a group of specialized databases which have a variety of data models but not use SQL. The data models include: Key-Value Store, Document Based or Column Based.
- **Distributed Database**: Designed to run on many servers for massively scalable and highly available systems. The dsitributed databases often use NoSQL database.

### MySQL History

- 1970: Dr. Codd published a papper about the Relational Data Model.
- 1973: Chamberlin and Boyce started working on SEQUEL (Structured English Query Language). It changed later to SQL due to the trademark conflict.
- 1979: Relational Software, Inc. released the first commercially available Relational Database Management System. It changed it's name to Oracle later.
- 1995: The Swedish company MySQL AB released MySQL for Internal use.
- 2008: Sun Microsystems purchases MySQL.
- 2010: Oracle buys Sun Microsystems with MySQL and Java.
- 2012: Michael (Monty) Widenius left Sun Microsystems and developed a fork of MySQL called MariaDB

<br/>
<div align="right">
  <b><a href="#mysql-database">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Relational Database Concepts

### Introduction

**Relational Databases** are the most widely used database in the world that store data in tables which have relations to other tables.

- A database **Table** is a lot like a spreadsheet.
- Data is kept in **Columns** and **Rows**.
  - Each Column is assigned a unique name, a data type and optionally constraints.
  - Each Row is a distinct database **Record**.
- Nearly all modern relation databases use **SQL (Structured Query Language)** for data definition and manipulation.

### SQL (Structured Query Language)

**SQL (Structured Query Language)** is a standard language for accessing and manipulating databases.

- Almost the relational database management systems support ANSI SQL (the standard for SQL).
- Relational database management sysmtes also support their own version of SQL. e.g. MySQL is different from Oracle which is different from MS SQL Server.
- They may support their own language for stored procedures and triggers.

These SQL commands are mainly categorized into four categories as:

- **DDL (Data Definition Language)** is used to define the relational model. e.g. `CREATE`, `DROP`, `ALTER`, `TRUNCATE`, `COMMENT` and `RENAME`, etc...
- **DML (Data Manipulation Language)** allows us to add (`INSERT`), change (`UPDATE`), or remove (`DELETE`) data.
- **DQL (Data Query Language)** statemetns are used for performing queries on the data within schema objects.
- **DCL (Data Control Language)** include commands such as `GRANT` and `REVOKE` which mainly deal with the rights, permissions, and other controls of the database system.

### Primary Key

A **Primary Key** is an optional special database column or columns used to identify a database record. It's optional but not recommended going without primary key. There are two types of Primary Key:

- **Surrogate Keys**
  - Type of Primary Key which used a unique generated value.
  - It should have no business value and never change.
  - Typically, developers would use UUID or the system generated self incrementing number.
  - Using Surrogate Keys is considered a best practice in relational database design.
- **Natural Keys**
  - Type of Primary Key which uses one or more data columns.
  - Typically encountered with legacy databases.
  - Using Natural Keys is not considered a best practice because it tied to business data which can change.

### Database Table Relations

The **Database Table Relations** defined through **Foreign Key Constraints** in conjunction with Primary Keys. The types of relations:

- **One-To-One** (symbol: `—————`): record in table A matches exactly one record in table B.
- **One-To-Many** (symbol: `————<`): record in table A matches many records in table B, but table B matches only one record in table A.
- **Many-To-Many** (symbol: `>———<`): record in table A matches many records in table B, and table B matches many record in table A.

### Database Constraints

A **Database Constraint** is a rule applied to the data stored in the database:

- **Not Null**: column is not allowed to have null values
- **Unique**: column value must be unique
- **Primary Key**: identifier of row, combines Not Null and Unique
- **Foreign Key**: value must exist in referenced (foreign) table aka, referential integrity
- **Check Constraint**: sets condition on data, like `min`, `max`, or list of values (ENUM)

### Database Transactions and ACID

A **Database Transaction** is a series of one or more DML statements. Committing will make the data changes permanent and a rollback will revert the changed data to the original state. In database systems, **ACID (Atomicity, Consistency, Isolation, Durability)** refers to a standard set of properties that guarantee database transactions are processed reliably:

- **Atomicity** guarantee that either all of the transaction succeeds or none of it does. With atomicity, it's either "all or nothing" and make all statements must be able to complete.
- **Consistency** ensures all data will be consistent since all all data will be valid according the defined rules. This means changes do not violate constraints.
- **Isolation** guarantees all transactions will occur in isolation and no transaction will be affected by any other transaction. So a transaction cannot read data from any other transaction that has not yet completed.
- **Durability** means once a transaction is committed, it will remain in the system.

### Locking

Transactions and ACID can be easy with one user but will become very complex with many transacting users. When there are multiple transacting users, the ACID can lead to lost updates. Then the **Locking** is one technique which can be used to prevent lost updates:

- **Pessimistic Locking** uses a database lock to prevent inflight transactions and will allow transactions to complete sequentially.
- **Optimistic Locking** uses a version property which is checked in the update.

### Administration Best Practices

- Use a schema for our application.
- Do not use a SUPER USER account for application users.
- Apply the principle of minimal authorities.
- Account used by application often called a "service account".
  - Service account should only have CRUD access.
  - Service account should not be able to perform DDL operations.
- Prevents malicious acts via SQL Injection attacks.

<br/>
<div align="right">
  <b><a href="#mysql-database">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Data Mapping SQL to Java

Since SQL data types and Java data types are not identical, there needs to be some mechanism for reading and writing data between an application using Java types and a database using SQL types.

- [SQL Data Types](https://www.journaldev.com/16774/sql-data-types)
- [MySQL Data Types](https://dev.mysql.com/doc/refman/8.0/en/data-types.html)
- [Hivernate Data Types](https://docs.jboss.org/hibernate/orm/5.0/mappingGuide/en-US/html/ch03.html)

> Furthermore, there are significant variations between the SQL types supported by different database products. Even when different databases support SQL types with the same semantics, they may give those types different names. e.g. For large binary values, Oracle use calls `LONG RAW`; Sybase calls `IMAGE`; Informix calls `BYTE`...

The JDBC provides multiple methods to accomplish this.

<br/>
<div align="right">
  <b><a href="#mysql-database">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Create Schema and User for Spring Boot

```sql
DROP DATABASE IF EXISTS bookdb;
DROP USER IF EXISTS `bookadmin`@`%`;
DROP USER IF EXISTS `bookuser`@`%`;

CREATE DATABASE IF NOT EXISTS bookdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS `bookadmin`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `bookdb`.* TO `bookadmin`@`%`;

CREATE USER IF NOT EXISTS `bookuser`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `bookdb`.* TO `bookuser`@`%`;

FLUSH PRIVILEGES;
```

<br/>
<div align="right">
  <b><a href="#mysql-database">[ ↥ Back To Top ]</a></b>
</div>
<br/>
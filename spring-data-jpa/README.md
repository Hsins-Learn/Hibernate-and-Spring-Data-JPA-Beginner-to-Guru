# Spring Data JPA

- [Overview](#overview)
  - [JDBC (Java DataBase Connectivity)](#jdbc-java-database-connectivity)
  - [JPA (Java Persistence API)](#jpa-java-persistence-api)
  - [Spring Data JPA](#spring-data-jpa-1)
    - [Introduction](#introduction)
    - [When to Use and When Not to Use](#when-to-use-and-when-not-to-use)
  - [Hibernate](#hibernate)
  - [The Repository Pattern](#the-repository-pattern)
- [Use Spring Initializr to Create Project](#use-spring-initializr-to-create-project)
- [JPA Entites](#jpa-entites)
- [Equality in Hibernate](#equality-in-hibernate)
- [Spring Data Repositories](#spring-data-repositories)
- [Initializing Data with Spring](#initializing-data-with-spring)
- [SQL Logging](#sql-logging)
- [H2 Database Console](#h2-database-console)

## Overview

### JDBC (Java DataBase Connectivity)

Java DataBase Connectivity (JDBC) is the Java API for connecting to databases.

- It's just the API, not the implementation.
- The JDBC implementation is typically referred to as a JDBC Driver.
- Each database will have it's own JDBC Driver implementation.
- The JDBC Driver handles the low level communications with the database.
- Each Driver will implement the JDBC API, and have platform specific extensions.

### JPA (Java Persistence API)

**Java Persistence API (JPA)** is a common API for working with data from relational databases.

- Support SQL Databases typically, but it has been used with some NoSQL datastores.
- Hibernate is an implementation of JPA.
- Spring Data JPA is an abstraction layer built on top of JPA.
- Other implementations include EclipseLink, Apache OpenJPA and TopLink.

### Spring Data JPA

#### Introduction

**Spring Data JPA** is the JPA based version of data repository access of the Spring Data family of projects. It's an abstraction layer to simplify our programming experience (hide the complexity of SQL, JDBC, Hibernate). We're not worried about low level things such as database connections and transactions when using it.

There is something different between Spring Data and Spring Data JPA:

- **Spring Data** has many implementations for various data stores including MongoDB, Redis, Cassandra, GemFile, Couchbase, and Neo4j, etc...
- **Spring Data JPA** focuses on supporting the JPA API standard.

The Spring Data JPA typically used for accessing SQL based Relational Databases, but there are some NoSQL Datastores offer JPA support. Furthermore, we can still access implementation specific features when needed since the Spring Data JPA uses the JPA API layer.

#### When to Use and When Not to Use

- When to Use JPA?
  - Spring Data JPA is very good for single object CRUD operations.
  - When we have multiple operations against a small set of objects.
    - e.g. Checkout operation in a web store application. Hibernate will cache and batch Database operations for efficiency.
  - When we have control of the database schema.
- When not to Use JPA?
  - Spring Data JPA is not very good for batch operations.
  - There is a cost to fetching a single record from the database and mapping it to a Java object.
  - If we're performing batch operations on 10's of thousands of records, we should consider using SQL/JDBC.

### Hibernate

**Hibernate** is a Object Relational Mapping (ORM) Tool which also implements the JPA API specification.

- Object Relational Mapping (ORM) map data between the Relational and Object-Oriented paradigms.
- **Leakage** is the term for one paradigm "leaking" into the other model. e.g. The ID value is considered "leakage" because it's the Primary Key for the relational model, and needs to be carried over to the Object Model.

### The Repository Pattern

Spring Data JPA implements the Repository Pattern which was instrouced in the book "Domain Driven Design" in 2004. Let's see the overview of it:

<p align="center">
  <img src="https://i.imgur.com/xtF7iXC.png" alt="The Repository Pattern"/>
</p>

In the Repository Pattern:

- The Repository is an abstraction of data and persistence operations.
- We wish to keep the domain objects "Persistence Ignorant".
- The Repository Layer provide an Interface for CRUD Operations, implementation will handle all persistence operations.

In Spring Data JPA: programmers provide the interface and the Spring Data JPA provides the implementation.

<br/>
<div align="right">
  <b><a href="#spring-data-jpa">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Use Spring Initializr to Create Project

Visit the [Spring Initializr website](https://start.spring.io/) to create our project:

- **Project**: Maven Project
- **Language**: Java
- **Spring Boot**: 2.5.7
- **Group**: `guru.springframework`
- **Artifact**: `sdjpa-intro`
- **Name**: `sdjpa-intro`
- **Description**: Introduction to Spring Data JPA
- **Package name**: `guru.springframework.demo`
- **Package**: `Jar`
- **Java**: 11
- **Dependencies**
  - Spring Web
  - Spring Data JPA
  - H2 Database

Click [GENERATE] to download the `*.zip` file and then open the project with Intellij.

> **H2 Database** is an open-source lightweight Java database. It can be embedded in Java applications or run in the client-server mode. Mainly, H2 database can be configured to run as inmemory database, which means that data will not persist on the disk. Because of embedded database it is not used for production development, but mostly used for development and testing.

<br/>
<div align="right">
  <b><a href="#spring-data-jpa">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## JPA Entites

First, we're going to create the first domain object. By convention, lots of people create a package called `*.domain` (some people also use `*.model` or `*.entity`). Then create the `Book` class:

```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String isbn;
    private String publisher;

    // constructor
    public Book() {

    }

    public Book(String title, String isbn, String publisher) {
        this.title = title;
        this.isbn = isbn;
        this.publisher = publisher;
    }

    // getters and setters
    ...
}
```

<br/>
<div align="right">
  <b><a href="#spring-data-jpa">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Equality in Hibernate

The equality is important if we're going to be dealing with sets of objects or entities that are persistent.

```java
@Entity
public class Book {
    // fields
    ...

    // constructors

    // equality method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return Objects.equals(id, book.id);
    }

    // getters and setters
    ...
}
```

<br/>
<div align="right">
  <b><a href="#spring-data-jpa">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Spring Data Repositories

We're going to add the implementation of repositories. Now, create a package named `repositories` and then add the `BookRepositories` interface:

```java
import guru.springframework.sdjpaintro.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

}
```

<br/>
<div align="right">
  <b><a href="#spring-data-jpa">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Initializing Data with Spring

Create a package named `bootstrape` and then add the `DataIntialize` class:

```java
@Component
public class DataInitializer implements CommandLineRunner {
    private final BookRepository bookRepository;

    public DataInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Book bookDDD = new Book("Domain Driven Design", "123", "RandomHouse");
        System.out.println("Id: " + bookDDD.getId());
        Book savedDDD = bookRepository.save(bookDDD);
        System.out.println("Id: " + savedDDD.getId());

        Book bookSIA = new Book("Spring in Action", "234434", "Oriely");
        Book savedSIA = bookRepository.save(bookSIA);
        System.out.println("Id: " + savedSIA.getId());

        bookRepository.findAll().forEach(book -> {
            System.out.println("Book id: " + book.getId());
            System.out.println("Book Title: " + book.getTitle());
        });
    }
}
```

<br/>
<div align="right">
  <b><a href="#spring-data-jpa">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## SQL Logging

To show logs of the SQL statements, we should open `application.properties` and enable the feature:

```config
# spring.jpa.show-sql=true

# Show SQL
spring.jpa.properties.hibernate.show_sql=true

# Format SQL
spring.jpa.properties.hibernate.format_sql=true

# Show bind values
logging.level.org.hibernate.type.descriptor.sql=trace
```

<br/>
<div align="right">
  <b><a href="#spring-data-jpa">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## H2 Database Console

By default, Spring Boot is going to configure the database console automatically. When we use H2 databse, we're running an in-memory database and have the develop toolset.

To enable the H2 Database console, we need to open `application.properties` and add following setting:

```config
spring.h2.console.enabled=true
```

Now, we can run the application and visit [`http://localhost:8080/h2-console/`](http://localhost:8080/h2-console/). The JDBC URL of our H2 Database could be found in the log:

```
2021-12-08 12:48:36.988  INFO 8488 --- [           main] o.s.b.a.h2.H2ConsoleAutoConfiguration    : H2 console available at '/h2-console'. Database available at 'jdbc:h2:mem:2cc943af-04ec-4e81-8a7f-41b4824a2c2b'
```

<br/>
<div align="right">
  <b><a href="#spring-data-jpa">[ ↥ Back To Top ]</a></b>
</div>
<br/>
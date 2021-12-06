# Hibernate Primary Key

- [Overview](#overview)
  - [Primary Keys in Hibernate](#primary-keys-in-hibernate)
  - [Which Primary Keys to Use?](#which-primary-keys-to-use)
- [Auto Incremented Primary Key](#auto-incremented-primary-key)
- [Vendor Specific Flyway Migrations](#vendor-specific-flyway-migrations)
- [UUID Primary Key](#uuid-primary-key)
- [UUID RFC 4122 Primary Key](#uuid-rfc-4122-primary-key)
- [Natural Primary Key](#natural-primary-key)
- [Composite Primary Key](#composite-primary-key)
- [Embedded Composite Primary key](#embedded-composite-primary-key)

## Overview

### Primary Keys in Hibernate

We use annotation for specify the primary key with Hibernate:

```java
@Id
@GeneratedValue(strategy = Generation.AUTO)
```

There are different primary keys can be used:

- **Numeric Primary Keys**
  - `GenerationType.AUTO`: let Hibernate pick - best practice is to specify
  - `GenerationType.SEQUENCE`: use database sequence (this is NOT a feature of MySQL)
  - `GenerationType.IDENTITY`: use auto-incremented database columns
  - `GenerationType.TABLE`: use database table to simulate sequence
- **UUID Primary Keys**
  - Common to use as primary key, it can help index performance.
  - The drawback is the UUID primary keys use more disk space.
  - Hibernate implements a custom generator by default, but it can be configured to generate a IETF RFC 4122 compliant UUID.
- **Natural Primary Keys**
  - A unique value with business meaning outside of the database.
  - A UPC or ISBN could be considered a natural key, since both are expected to be unique.
  - It's common in old legacy databases, but it's NOT considered the best practice.
- **Composite Primary Keys**
  - Two values with business meaning combined to make a unique value
  - It's common in old legacy databases, but it's NOT considered the best practice.

### Which Primary Keys to Use?

- **Small Table**: favor Numeric Primary Keys (`Integer` or `Long`) for few million rows.
- **Large Table**: favor UUID if disk space allows for 10's of millions or billions rows.
- Generally avoid using Natural Primary Keys or Composite Primary Keys.

<br/>
<div align="right">
  <b><a href="#hibernate-primary-key">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Auto Incremented Primary Key

First, use `@GeneratedValue(strategy = GenerationType.IDENTITY)` annotation to class:

```java
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    ...
```

Then add `V4__autoincrement_pk.sql` for Flyway migrations:

```sql
alter table book change id id BIGINT auto_increment;
alter table author change id id BIGINT auto_increment;
```

<br/>
<div align="right">
  <b><a href="#hibernate-primary-key">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Vendor Specific Flyway Migrations

In the previous seciton, the `V4__autoincrement_pk.sql` works fine for MySQL, but it doesn't work with H2 Database. To solve this problem, we're going to setup for the vendor-specific migrations:

1. Edit `application.properties` and add `spring.flyway.locations`.
    ```ini
    spring.flyway.locations=classpath:db/migration/common,classpath:db/migration/{vendor}
    ```
2. Create directories for different database.
    - `./src/main/resources/db/migration/common`
    - `./src/main/resources/db/migration/h2`
3. Add `V4.1__h2_auto.sql` for H2 database:
    ```sql
    drop table if exists book;
    drop table if exists author;

    create table book
    (
        id IDENTITY not null,
        isbn      varchar(255),
        publisher varchar(255),
        title     varchar(255),
        author_id BIGINT,
        primary key (id)
    );

    create table author
    (
        id IDENTITY not null,
        first_name varchar(255),
        last_name  varchar(255),
        primary key (id)
    );
    ```

<br/>
<div align="right">
  <b><a href="#hibernate-primary-key">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## UUID Primary Key

```java
@Entity
public class AuthorUuid {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;
    ...
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-primary-key">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## UUID RFC 4122 Primary Key

```java
@Entity
public class BookUuid {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;
    ...
}
```

> To deal with the Hibernate and H2 Database UUID validation error, we should use `"VARBINARY(16)"` instead of `"BINARY(16)"`.

<br/>
<div align="right">
  <b><a href="#hibernate-primary-key">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Natural Primary Key

```java
@Entity
public class BookNatural {
    @Id
    private String title;
    private String isbn;
    private String publisher;
    ...
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-primary-key">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Composite Primary Key

```java
@Entity
@IdClass(NameId.class)
public class AuthorComposite {
    @Id
    private String firstName;

    @Id
    private String lastName;
    private String country;
    ...
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-primary-key">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Embedded Composite Primary key

```java
@Entity
@Table(name = "author_composite")
public class AuthorEmbedded {
    @EmbeddedId
    private NameId nameId;

    private String country;
    ...
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-primary-key">[ ↥ Back To Top ]</a></b>
</div>
<br/>
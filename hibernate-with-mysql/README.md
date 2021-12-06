# Hibernate with MySQL

- [Hibernate DDL Schema Generation Tool](#hibernate-ddl-schema-generation-tool)
  - [`hbm2ddl.auto`](#hbm2ddlauto)
  - [HBM DDL Auto Properties](#hbm-ddl-auto-properties)
- [MySQL Spring Boot Configuration](#mysql-spring-boot-configuration)
- [Integration Test for MySQL](#integration-test-for-mysql)
- [H2 Compatibility Mode](#h2-compatibility-mode)
- [Schema Initialization with Hibernate](#schema-initialization-with-hibernate)
  - [Hibernate](#hibernate)
  - [MySQL](#mysql)
- [Use H2 Database for Spring Boot Application](#use-h2-database-for-spring-boot-application)


## Hibernate DDL Schema Generation Tool

### `hbm2ddl.auto`

The **Hibernate DDL Schema Generation Tool** is also known as `hbm2ddl.auto` configuration property. Hibernate has the ability to reflect on JPA annotated classes to determine necessary database structure.

It means Hibernate can:

- Create DDL statements to file.
- Execute DDL statements to create or update database tables.

Spring Boot is auto configuring this property to automatically generate database tables. It allows for minimalist JPA configuration and do following things:

- Table names and column names inferred from type and property names.
  - Default is `camelCase` to `SNAKE_CASE`.
  - e.g. `productDescription` > `PRODUCT_DESCRIPTION`.
- Datatypes are also defaulted.
- If JPA mappings are present, they will be used. We can set table names, columns, types, etc in JPA.

### HBM DDL Auto Properties

The Hibernate DDL Schema Generation Tool allows us rapidly evolve our object model without maintaining SQL DDL statements. There are available modes for that:

- `none`: disables schema generation tool
- `create-only`: create database schema from JPA Entities
- `drop`: drops database tables related to JPA Entities
- `create`: drops database schema and re-creates from JPA Entities
- `create-drop`: drops database schema and re-creates from JPA Entities, then will drop when shutting down
- `validate`: validates schema, fatal error if wrong
- `update`: updates schema from JPA Entities

Although it's great to use for rapid development, **it's NOT recommended for production databases**. We should use `validate` or `none` mode for production databases. Moreover, don't forget to use data source user requiring elevated database priviledges.

<br/>
<div align="right">
  <b><a href="#hibernate-with-mysql">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## MySQL Spring Boot Configuration

First, we need to update our Maven dependency to change the scope of H2 Database from `runtime` to `test`. And then add MySQL (use artifactId with `mysql-connector-java`) as dependency.

```xml
<project>
  ...
  <dependencies>
    ...
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <scope>runtime</scope>
    </dependency>

    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>runtime</scope>
    </dependency>
    ...
  </dependencies>
  ...
</project>
```

After reload the Maven project, create the spring profiles `./src/main/resources/application-local.properties` to control which database we're connecting to:

```ini
spring.datasource.username=bookadmin
spring.datasource.password=password
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/bookdb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.jpa.database=mysql
spring.jpa.hibernate.ddl-auto=update
```

Don't forget to edit the Run/Debug Configurations to use `application-local.properties`:

<p align="center">
  <img src="https://i.imgur.com/FKERmug.png" alt="Edit the Run/Debug Configurations"/>
</p>

## Integration Test for MySQL

If we want to use the MySQL Database instead of H2 Database for testing, use the `@AutoConfigureTestDatabase` annotation. Let's create `MySQLIntegrationTest` class under `./src/test/java/guru.springframework.sdjpaintro`:

```java
@ActiveProfiles("local")
@DataJpaTest
@ComponentScan(basePackages = {"guru.springframework.sdjpaintro.bootstrap"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MySQLIntegrationTest {
    @Autowired
    BookRepository bookRepository;

    @Test
    void testMySQL() {
        long countBefore = bookRepository.count();
        assertThat(countBefore).isEqualTo(2);
    }
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-with-mysql">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## H2 Compatibility Mode

All database engines behave a little bit different. Where possible, H2 supports the ANSI SQL standard, and tries to be compatible to other databases. e.g. Case Insensitive Columns in MySQL v.s. Case Insensitive Columns in H2.

For certain features, this database can emulate the behavior of specific databases. Let's add following code to `application.properties` for the MySQL compatibility mode:

```ini
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database=mysql
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
```

<br/>
<div align="right">
  <b><a href="#hibernate-with-mysql">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Schema Initialization with Hibernate

### Hibernate

Hibernate has the basic initialization capabilities, and one of them is to run from a file on the classpath call `schema.sql`. It's a very rudimentary way of populating databases.

Let's create the `schema.sql` in the `./src/resources/` directory:

```sql
drop table if exists book;
drop table if exists hibernate_sequence;

create table book
(
    id        bigint not null,
    isbn      varchar(255),
    publisher varchar(255),
    title     varchar(255),
    primary key (id)
) engine=InnoDB;

create table hibernate_sequence
(
    next_val bigint
) engine=InnoDB;

insert into hibernate_sequence values ( 1 );
```

And then edit and add following code in the `application.properties` file:

```ini
spring.jpa.defer-datasource-initialization=false
```

### MySQL

```ini
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode=always
```

<br/>
<div align="right">
  <b><a href="#hibernate-with-mysql">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Use H2 Database for Spring Boot Application

Let's see how to use the Maven profile for two enabled database:

```xml
<xml>
  ...
  <profiles>
    <profile>
      <id>h2</id>
      <dependencies>
        <dependency>
          <groupId>com.h2database</groupId>
          <artifactId>h2</artifactId>
          <scope>test</scope>
        </dependency>
      </dependencies>
    </profile>
  </profiles>
  ...
</xml>
```

<br/>
<div align="right">
  <b><a href="#hibernate-with-mysql">[ ↥ Back To Top ]</a></b>
</div>
<br/>
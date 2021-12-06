# Liquibase and Flyway

- [Overview](#overview)
  - [Migration](#migration)
  - [Migration Tools](#migration-tools)
  - [Liquibase v.s. Flyway](#liquibase-vs-flyway)
- [Liquibase](#liquibase)
  - [Liquibase Terminology](#liquibase-terminology)
  - [Liquibase Best Practices](#liquibase-best-practices)
  - [Liquibase Maven Plugin](#liquibase-maven-plugin)
  - [Generate Changeset from Database](#generate-changeset-from-database)
  - [Organizing Change Logs](#organizing-change-logs)
  - [Spring Boot Configuration](#spring-boot-configuration)
  - [Initializing Data with Spring](#initializing-data-with-spring)
  - [Alter Table with Liquibase](#alter-table-with-liquibase)
- [Flyway](#flyway)
  - [Flyway Commands](#flyway-commands)
  - [Spring Boot Configuration](#spring-boot-configuration-1)
  - [Alter Table with Flyway](#alter-table-with-flyway)
  - [Clean and Rebuild with Flyway](#clean-and-rebuild-with-flyway)

## Overview

### Migration

**Migrations** are the process of moving programming code from one system to another system. This is fairly large and complex topic of maintaining computer applications.

- Database Migrations typically need to occur prior to, or in conjunction with application. It can lead to run time errors if database doesn't match what is expected.
- Database Migrations are a very important part of the process of moving our application code to production.
- Keep in mind, in larger organizations, we as the developers will NOT be doing the migration.

### Migration Tools

Although Hibernate can manage the schema fine, the Migration Tools can do more things include:

- Create new databases.
- Hold the histories of migrations.
- Have a reproducible state of the database.
- Help manage changes being applied to numerous database instances.

[Liquibase](https://www.liquibase.org/) and [Flyway](https://flywaydb.org/) are popular Open Source Database Migration Tools integrated with Maven, Gradle and Spring Boot.

### Liquibase v.s. Flyway

- Spring Boot offers support for both Liquibase and Flyway.
- Both tools may be used independently of Spring Boot.
- Both tools share the same concepts with slightly different terminology.
- Liquibase supports SQL, XML, YAML and JSON; Flyway supports SQL and Java only.
- Liquibase is larger and more robust; Flyway seems to have more popularity.

Liquibase is probably a better solution for large enterprises with complex environment. Flyway is simple an easy to use for 90% of applications which don't need the additional capabilities.

<br/>
<div align="right">
  <b><a href="#liquibase-and-flyway">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Liquibase

### Liquibase Terminology

- **ChangeSet**: a set of changes to be applied to the database.
- **Change**: a single change to be applied to the database.
- **Changelog**: a file which has a list of changeSet’s to be applied.
- **Preconditions**: conditions which control the execution.
- **Context**: an expression to help control if the script should or should not run.
- **ChangeLog Parameters**: placeholders which can be replaced at run time.

### Liquibase Best Practices

- Organizing Change Logs - Create a master change log to organize Change Sets
- One Change Per Change Set - Allows for easier rollback if there is a failure.
- Never Modify a Change Set - Changes should be additive
- Use Meaningful Change Set Ids - some use a sequence number, others use a descriptive name

### Liquibase Maven Plugin

It's possible to have a centralized Liquibase plugin configuration that applies to all the Maven child projects through the usage of the super-POM. Wa may add configuration properties to `./src/pom.xml`:

```xml
<project>
  ...
  <build>
    <plugins>
      ...
      <plugin>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-maven-plugin</artifactId>
        <version>4.6.2</version>
        <configuration>
          <url>jdbc:mysql://127.0.0.1:3306/bookdb?useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=UTC</url>
          <username>bookadmin</username>
          <password>password</password>
        </configuration>
        <dependencies>
          <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
          </dependency>
        </dependencies>
      </plugin>
      ...
    </plugins>
  </build>
</project>
```

### Generate Changeset from Database

`Maven generateChangeLog` creates a changelog file that has a sequence of changesets which describe how to re-create the current state of the database.

- It's typically used when you want to capture the current state of a database, then apply those changes to any number of databases.
- This is typically only done when a project has an existing database but hasn't used Liquibase before. See How to set up Liquibase with an Existing Project and Multiple Environments for more details.

```xml
<project>
  ...
  <build>
    <plugins>
      ...
      <plugin>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-maven-plugin</artifactId>
        <version>4.6.2</version>
        <configuration>
          <url>jdbc:mysql://127.0.0.1:3306/bookdb?useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=UTC</url>
          <username>bookadmin</username>
          <password>password</password>
          <outputChangeLogFile>changelog.xml</outputChangeLogFile>
          <changeSetAuthor>JT</changeSetAuthor>
          <changelogSchemaName>bookdb</changelogSchemaName>
        </configuration>
        <dependencies>
          <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
          </dependency>
        </dependencies>
      </plugin>
      ...
    </plugins>
  </build>
</project>
```

Now, we can run the `generateChangeLog` command:

```bash
$ mvn liquibase:generateChangeLog
```

With IntelliJ IDEA, we can just double-click the `liquibase:generateChangeLog` in Maven Tab to run that command:

<p align="center">
  <img src="https://i.imgur.com/UZHMELD.png" alt="Run liquibase:generateChangeLog"/>
</p>

Check [How to set up Liquibase with an Existing Project and Multiple Environments](https://docs.liquibase.com/workflows/liquibase-community/existing-project.html) and [Maven generateChangeLog](https://docs.liquibase.com/tools-integrations/maven/commands/maven-generatechangelog.html) for more information.

### Organizing Change Logs

The most common way to organize our changelogs is by major release. Choose a package in the classpath to store the changelogs, preferably near your database access classes, e.g. `com/example/db/changelog`.

```
com
  example
    db
      changelog
       db.changelog-master.xml
       db.changelog-1.0.xml
       db.changelog-1.1.xml
       db.changelog-2.0.xml
      DatabasePool.java
      AbstractDAO.java
```

Check [Liquibase Best Practices](https://docs.liquibase.com/concepts/bestpractices.html) for more information.

### Spring Boot Configuration

1. Open `pom.xml` to add dependency.

    ```xml
    <dependency>
      <groupId>org.liquibase</groupId>
      <artifactId>liquibase-core</artifactId>
    </dependency>
    ```

2. Edit `application.properties` to add changelog path.

    ```ini
    spring.liquibase.change-log=db/changelog/changelog-master.xml
    ```

3. Edit `application-local.properties` to add connection information.

    ```ini
    spring.liquibase.user=bookadmin
    spring.liquibase.password=password
    ```

Check [Using Liquibase with Spring Boot](https://docs.liquibase.com/tools-integrations/springboot/springboot.html) for more information.

### Initializing Data with Spring

1. Create `init-hibernate.xml` under `./src/resources/db.changelog`:

    ```xml
    <?xml version="1.1" encoding="UTF-8" standalone="no"?>
    <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                      xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
                      xmlns:pro="http://www.liquibase.org/xml/ns/pro"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog-ext
                                          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd
                                          http://www.liquibase.org/xml/ns/pro
                                          http://www.liquibase.org/xml/ns/pro/liquibase-pro-4.1.xsd
                                          http://www.liquibase.org/xml/ns/dbchangelog
                                          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.1.xsd">
        <changeSet author="JT" id="3">
            <sql>insert info hibernate_sequence values (0)</sql>
        </changeSet>
    </databaseChangeLog>
    ```

2. Add it to `changelog-master.xml`:

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <databaseChangeLog
            xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:pro="http://www.liquibase.org/xml/ns/pro"
            xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                                http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.1.xsd
                                http://www.liquibase.org/xml/ns/pro
                                http://www.liquibase.org/xml/ns/pro/liquibase-pro-4.1.xsd">
        <include  file="db/changelog/baseline-changelog.xml"/>
        <include  file="db/changelog/init-hibernate.xml"/>
    </databaseChangeLog>
    ```

### Alter Table with Liquibase

1. Create `add-author-id-to-book.xml` under `./src/resources/db.changelog`:

    ```xml
    <?xml version="1.1" encoding="UTF-8" standalone="no"?>
    <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                      xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
                      xmlns:pro="http://www.liquibase.org/xml/ns/pro"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog-ext
                                          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd
                                          http://www.liquibase.org/xml/ns/pro
                                          http://www.liquibase.org/xml/ns/pro/liquibase-pro-4.1.xsd
                                          http://www.liquibase.org/xml/ns/dbchangelog
                                          http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.1.xsd">
        <changeSet author="JT" id="5">
            <addColumn tableName="book">
                <column name="author_id" type="BIGINT" />
            </addColumn>
        </changeSet>
    </databaseChangeLog>
    ```

2. Add it to `changelog-master.xml`:

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <databaseChangeLog
            xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns:pro="http://www.liquibase.org/xml/ns/pro"
            xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
                                http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.1.xsd
                                http://www.liquibase.org/xml/ns/pro
                                http://www.liquibase.org/xml/ns/pro/liquibase-pro-4.1.xsd">
        <include  file="db/changelog/baseline-changelog.xml"/>
        <include  file="db/changelog/init-hibernate.xml"/>
        <include  file="db/changelog/add-author-id-to-book.xml"/>
    </databaseChangeLog>
    ```

<br/>
<div align="right">
  <b><a href="#liquibase-and-flyway">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Flyway

### Flyway Commands

- **Migrate**: migrate to latest version.
- **Clean**: drops all database objects (NOT FOR PRODUCTION USE).
- **Info**: prints info about migrations.
- **Validate**: validates applied migrations against available.
- **Undo**: reverts most recently applied migration.
- **Baseline**: baselines an existing database.
- **Repair**: used to fix problems with schema history table.

### Spring Boot Configuration

1. Edit `pom.xml` and add Flyway as dependency.

    ```xml
    <project>
      ...
      <dependencies>
        ...
        <dependency>
          <groupId>org.flywaydb</groupId>
          <artifactId>flyway-core</artifactId>
        </dependency>
        ...
      </dependencies>
      ...
    </project>
    ```

2. Add user and password in `application-local.properties`.

    ```ini
    ...
    spring.flyway.user=bookadmin
    spring.flyway.password=password
    ```

3. Create `db.migration` folder under `./src/main/resources` and then put `*.sql` file with naming convention as `V1__init_database.sql`.

### Alter Table with Flyway

Add `V3__add_author_id_to_book.sql` file:

```sql
alter table book ADD author_id BIGINT;
```

### Clean and Rebuild with Flyway

Add package `guru.springframework.sdjpaintro.config` and add `DbClean.class`:

```java
@Profile("clean")
@Configuration
public class DbClean {
    @Bean
    public FlywayMigrationStrategy clean() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        }
    }
}
```

<br/>
<div align="right">
  <b><a href="#liquibase-and-flyway">[ ↥ Back To Top ]</a></b>
</div>
<br/>
# Spring JDBC Template

- [Overview](#overview)
- [Create Row Mapper](#create-row-mapper)
- [Implement Get Author by ID](#implement-get-author-by-id)
- [Implement Find Author by Name](#implement-find-author-by-name)
- [Save New Author](#save-new-author)
- [Update Author](#update-author)
- [Delete Author](#delete-author)

## Overview

The **Spring JDBC Template** is a powerful mechanism to connect to the database and execute SQL queries. It internally uses JDBC api, but eliminates a lot of problems of JDBC API. e.g.

- We need to write a lot of code before and after executing the query, such as creating connection, statement, closing resultset, connection etc.
- We need to perform exception handling code on the database logic.
- We need to handle transaction.
- Repetition of all these codes from one to another database logic is a time consuming task.

We can perform all the database operations by the help of `JdbcTemplate` class such as insertion, updation, deletion and retrieval of the data from the database.

## Create Row Mapper

```java
public class AuthorMapper implements RowMapper<Author> {
    @Override
    public Author mapRow(ResultSet rs, int rowNum) throws SQLException {
        Author author = new Author();
        author.setId(rs.getLong("id"));
        author.setFirstName(rs.getString("first_name"));
        author.setLastName(rs.getString("last_name"));

        return author;
    }
}
```

<br/>
<div align="right">
  <b><a href="#spring-jdbc-template">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Implement Get Author by ID

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final JdbcTemplate jdbcTemplate;

    public AuthorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    ...

    @Override
    public Author getById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM author WHERE id = ?", getRowMapper(), id);
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#spring-jdbc-template">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Implement Find Author by Name

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final JdbcTemplate jdbcTemplate;

    public AuthorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    ...

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        return jdbcTemplate.queryForObject("SELECT * FROM author WHERE first_name = ? and last_name = ?", getRowMapper(), firstName, lastName);
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#spring-jdbc-template">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Save New Author

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final JdbcTemplate jdbcTemplate;

    public AuthorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    ...

    @Override
    public Author saveNewAuthor(Author author) {
        jdbcTemplate.update("INSERT INTO author (first_name, last_name) VALUES (?, ?)", author.getFirstName(), author.getLastName());
        Long createdId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        return this.getById(createdId);
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#spring-jdbc-template">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Update Author

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final JdbcTemplate jdbcTemplate;

    public AuthorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    ...

    @Override
    public Author updateAuthor(Author author) {
        jdbcTemplate.update("UPDATE author SET first_name = ?, last_name = ? WHERE id = ?", author.getFirstName(), author.getLastName(), author.getId());

        return this.getById(author.getId());
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#spring-jdbc-template">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Delete Author

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final JdbcTemplate jdbcTemplate;

    public AuthorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    ...

    @Override
    public void deleteAuthorById(Long id) {
        jdbcTemplate.update("DELETE FROM author WHERE id = ?", id);
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#spring-jdbc-template">[ ↥ Back To Top ]</a></b>
</div>
<br/>

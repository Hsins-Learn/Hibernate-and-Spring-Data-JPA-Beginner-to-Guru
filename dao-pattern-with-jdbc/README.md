# DAO Pattern with JDBC

- [Overview](#overview)
  - [The DAO Pattern](#the-dao-pattern)
  - [DAO Pattern with Java](#dao-pattern-with-java)
  - [Spring Boot JDBC](#spring-boot-jdbc)
- [Create Author DAO](#create-author-dao)
- [Implement Get Author By ID](#implement-get-author-by-id)
- [Using Prepared Statements](#using-prepared-statements)
- [Refactoring Duplicate Code](#refactoring-duplicate-code)
- [Save New Author](#save-new-author)
- [Update Author](#update-author)
- [Delete Author](#delete-author)
- [Refactor Author id to Author](#refactor-author-id-to-author)

## Overview

### The DAO Pattern

The **DAO (Data Access Object) Pattern** is very similar to the Repository Pattern used by Spring Data. It was a precursor to JPA before ORMs become popular, and it's very common to see in legacy J2EE applications.

- Purpose is to isolate persistence operations from the application layer.
- The application should not need to understand the underlying persistence technology to persist an object.

### DAO Pattern with Java

To use DAO Pattern with Java, we're going to create the following different parts:

- **Domain Class**: just simple POJOs, as same as JPA entities.
- **DAO API**: provide interface for CRUD operations (similar to Repository).
- **DAO Implementation**: implement persistence functionality

Note that the DAO pattern will not utilize JPA annotations.

### Spring Boot JDBC

In Spring Boot, it will auto configure the database connection for us instead of managing the database connection with JDBC ourselves. The Database connection components are available as Spring Beans in the Spring Context.

<br/>
<div align="right">
  <b><a href="#dao-pattern-with-jdbc">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Create Author DAO

First, create `AuthorDao` interface under `./src/main/java/guru.springframework.jdbc/`:

```java
public interface AuthorDao {
    Author getById(Long id);
}
```

Then the implementation `AuthorDaoImpl` class:

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    @Override
    public Author getById(Long id) {
        return null;
    }
}
```

<br/>
<div align="right">
  <b><a href="#dao-pattern-with-jdbc">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Implement Get Author By ID

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final DataSource source;

    public AuthorDaoImpl(DataSource source) {
        this.source = source;
    }

    @Override
    public Author getById(Long id) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = source.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM author where id = " + id);

            if (resultSet.next()) {
                Author author = new Author();
                author.setId(id);
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName((resultSet.getString("last_name")));

                return author;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (statement != null) {
                    statement.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
```

Don't forget to release the database resources!

<br/>
<div align="right">
  <b><a href="#dao-pattern-with-jdbc">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Using Prepared Statements

To write SQL statements directly in code is definitely not a best practices. Let's refactor the code to use a prepared statement:

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final DataSource source;

    public AuthorDaoImpl(DataSource source) {
        this.source = source;
    }

    @Override
    public Author getById(Long id) {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = source.getConnection();
            ps = connection.prepareStatement("SELECT * FROM author WHERE id = ?");
            ps.setLong(1, id);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                Author author = new Author();
                author.setId(id);
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName((resultSet.getString("last_name")));

                return author;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (ps != null) {
                    ps.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
```

<br/>
<div align="right">
  <b><a href="#dao-pattern-with-jdbc">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Refactoring Duplicate Code

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final DataSource source;

    public AuthorDaoImpl(DataSource source) {
        this.source = source;
    }

    @Override
    public Author getById(Long id) {
        Connection connection = null;
        Statement statement = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = source.getConnection();
            ps = connection.prepareStatement("SELECT * FROM author WHERE id = ?");
            ps.setLong(1, id);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                Author author = new Author();
                author.setId(id);
                author.setFirstName(resultSet.getString("first_name"));
                author.setLastName((resultSet.getString("last_name")));

                return author;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeAll(resultSet, ps, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = source.getConnection();
            ps = connection.prepareStatement("SELECT * FROM author where first_name = ? and last_name = ?");
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return getAuthorFromRS(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeAll(resultSet, ps, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Author getAuthorFromRS(ResultSet resultSet) throws SQLException {
        Author author = new Author();
        author.setId(resultSet.getLong("id"));
        author.setFirstName(resultSet.getString("first_name"));
        author.setLastName(resultSet.getString("last_name"));
        return author;
    }

    private void closeAll(ResultSet resultSet, PreparedStatement ps, Connection connection) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }

        if (ps != null) {
            ps.close();
        }

        if (connection != null) {
            connection.close();
        }
    }
}
```

<br/>
<div align="right">
  <b><a href="#dao-pattern-with-jdbc">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Save New Author

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final DataSource source;

    public AuthorDaoImpl(DataSource source) {
        this.source = source;
    }
    ...

    @Override
    public Author saveNewAuthor(Author author) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = source.getConnection();
            ps = connection.prepareStatement("INSERT INTO author (first_name, last_name) VALUES (?, ?)");
            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            ps.execute();

            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");

            if (resultSet.next()) {
                return getAuthorFromRS(resultSet);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeAll(resultSet, ps, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#dao-pattern-with-jdbc">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Update Author

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final DataSource source;

    public AuthorDaoImpl(DataSource source) {
        this.source = source;
    }
    ...

    @Override
    public Author updateAuthor(Author author) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            connection = source.getConnection();
            ps = connection.prepareStatement("UPDATE author SET first_name = ?, last_name = ? WHERE author.id = ?");
            ps.setString(1, author.getFirstName());
            ps.setString(2, author.getLastName());
            ps.setLong(3, author.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeAll(resultSet, ps, connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return this.getById(author.getId());
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#dao-pattern-with-jdbc">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Delete Author

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final DataSource source;

    public AuthorDaoImpl(DataSource source) {
        this.source = source;
    }
    ...

    @Override
    public void deleteAuthorById(Long id) {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = source.getConnection();
            ps = connection.prepareStatement("DELETE FROM autho WHERE id = ?");
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                closeAll(null, ps, connection);
            } catch (SQLException e) {

            }
        }
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#dao-pattern-with-jdbc">[ ↥ Back To Top ]</a></b>
</div>
<br/>

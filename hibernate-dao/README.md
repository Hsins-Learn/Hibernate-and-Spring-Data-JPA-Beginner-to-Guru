# Hibernate DAO


- [Overview](#overview)
  - [Hibernate](#hibernate)
  - [Hibernate Terms](#hibernate-terms)
  - [Persistence Context](#persistence-context)
  - [Caching and Caching Problem](#caching-and-caching-problem)
- [Get Author by ID](#get-author-by-id)
- [Find Author by Name](#find-author-by-name)
- [Save New Author](#save-new-author)
- [Update Author](#update-author)
- [Delete Author](#delete-author)

## Overview

### Hibernate

**Hibernate** is a very popular and mature ORM solution for Java. It also implements the Java JPA specification and a lot of the API specification was actually based on Hibernate:

- Coding to the Java API specification will keep the code independent of Hibernate.
- Coding to the native Hibernate API will make the code dependent on Hibernate

### Hibernate Terms

- **Session Factory**: it's expensive to create, your application should only have one instance.
- **Entity Manager Factory**: JPA Equivalent of Session Factory.
- **Session**: it's a single threaded, short lived object. this is cheap to create and each session wraps a JDBC connection.
- **Entity Manager**: JPA Equivalent of Session
- **Transaction**: it's a single threaded, short lived object to define transaction boundaries.
- **Entity Transaction**: JPA equivalent of Transaction

### Persistence Context

Session or Entity Manager will create a context for dealing with persistent data:

- **Transient**: the entity has just been instantiated and is not associated with a persistence context. It has no persistent representation in the database and typically no identifier value has been assigned (unless the assigned generator was used).
- **Managed or Persistent**: the entity has an associated identifier and is associated with a persistence context. It may or may not physically exist in the database yet.
- **Detached**: the entity has an associated identifier but is no longer associated with a persistence context (usually because the persistence context was closed or the instance was evicted from the context).
- **Removed**: the entity has an associated identifier and is associated with a persistence context, however, it is scheduled for removal from the database.

The Detached Entities is a very common error. The common root cause is working outside of the session scope or a closed session. Spring Data JPA will do an implicit transaction, and we can see this error when accesing entity properties outside of a transaction.

### Caching and Caching Problem

Hibernate will cache entities in the **Persistence Context** by default. There are different level of caches:

- **First Level Cache**
  - Changes outside the context might not be seen.
  - It's very efficient for doing work within the context of a session.
- **Second Level Cache**
  - The JVM or cluster level cache, and it's disable by default.
  - Recommend to enable on a per entity basis.
  - Broad support for popular options such as jCache, Ehcache, and Infinispan

In the following example code, Hibernate would add 100,000 objects to session level cache. It's possible out of memory.

```java
for (int = 0; i < 100_000; i++) {
  Person person = new Person(String.format("Person %d", i));
  entityManager.persist(person);
}
```

- It could deplete transaction pool with long running transaction.
- JDBC Batching not enabled by default, each insert is a round trip to the Database.
- `flush()` and `clear()` methods can be used to clear session cach.

<br/>
<div align="right">
  <b><a href="#hibernate-dao">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Get Author by ID

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    ...

    @Override
    public Author getById(Long id) {
        return getEntityManager().find(Author.class, id);
    }

    ...

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-dao">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Find Author by Name

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    ...

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        TypedQuery<Author> query = getEntityManager().createQuery("SELECT a FROM Author a " +
                "WHERE a.firstName = :first_name and a.lastName = :last_name", Author.class);
        query.setParameter("first_name", firstName);
        query.setParameter("last_name", lastName);

        return query.getSingleResult();
    }

    ...

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-dao">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Save New Author

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    ...

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();
        em.joinTransaction();
        em.persist(author);
        em.flush();
        em.getTransaction().commit();

        return author;
    }

    ...

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-dao">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Update Author

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    ...

    @Override
    public Author updateAuthor(Author author) {
        EntityManager em = getEntityManager();

        em.joinTransaction();
        em.merge(author);
        em.flush();
        em.clear();

        return em.find(Author.class, author.getId());
    }

    ...

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-dao">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Delete Author

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    ...

    @Override
    public void deleteAuthorById(Long id) {
        EntityManager em = getEntityManager();

        em.getTransaction();
        Author author = em.find(Author.class, id);
        em.remove(author);
        em.flush();
        em.getTransaction().commit();
    }

    ...

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-dao">[ ↥ Back To Top ]</a></b>
</div>
<br/>
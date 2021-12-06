# Hibernate Query

- [Query](#query)
- [Typed Query](#typed-query)
- [Named Query](#named-query)
- [Named Query with Parameters](#named-query-with-parameters)
- [Criteria Query](#criteria-query)

## Query

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    ...

    @Override
    public List<Author> listAuthorByLastNameLike(String lastName) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createQuery("SELECT a from Author a where a.lastName like :last_name");
            query.setParameter("last_name", lastName + "%");
            List<Author> authors = query.getResultList();

            return authors;
        } finally {
            em.close();
        }
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-query">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Typed Query

```java
@Component
public class BookDaoImpl implements BookDao {
    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    ...

    @Override
    public Book findByISBN(String isbn) {
        EntityManager em = getEntityManager();

        try {
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
            query.setParameter("isbn", isbn);

            Book book = query.getSingleResult();
            return book;
        } finally {
            em.close();
        }
    }
    ...
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-query">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Named Query

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    ...

    @Override
    public List<Author> findAll() {
        EntityManager em = getEntityManager();

        try{
            TypedQuery<Author> typedQuery = em.createNamedQuery("author_find_all", Author.class);

            return typedQuery.getResultList();
        } finally {
            em.close();
        }
    }

    ...
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-query">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Named Query with Parameters

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
        EntityManager em = getEntityManager();

        TypedQuery<Author> query = em.createNamedQuery("find_by_name", Author.class);

        query.setParameter("first_name", firstName);
        query.setParameter("last_name", lastName);

        Author author = query.getSingleResult();
        em.close();
        return author;
    }

    ...
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-query">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Criteria Query

```java
@Component
public class AuthorDaoImpl implements AuthorDao {
    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Author findAuthorByNameCriteria(String firstName, String lastName) {
        EntityManager em = getEntityManager();

        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Author> criteriaQuery = criteriaBuilder.createQuery(Author.class);

            Root<Author> root = criteriaQuery.from(Author.class);

            ParameterExpression<String> firstNameParam = criteriaBuilder.parameter(String.class);
            ParameterExpression<String> lastNameParam = criteriaBuilder.parameter(String.class);

            Predicate firstNamePred = criteriaBuilder.equal(root.get("firstName"), firstNameParam);
            Predicate lastNamePred = criteriaBuilder.equal(root.get("lastName"), lastNameParam);

            criteriaQuery.select(root).where(criteriaBuilder.and(firstNamePred, lastNamePred));

            TypedQuery<Author> typedQuery = em.createQuery(criteriaQuery);
            typedQuery.setParameter(firstNameParam, firstName);
            typedQuery.setParameter(lastNameParam, lastName);

            return typedQuery.getSingleResult();
        } finally {
            em.close();
        }
    }
}
```

<br/>
<div align="right">
  <b><a href="#hibernate-query">[ ↥ Back To Top ]</a></b>
</div>
<br/>
# Spring Boot Test

- [Execute Testing](#execute-testing)
- [Spring Boot JPA Test Slice](#spring-boot-jpa-test-slice)
- [Test Transactions](#test-transactions)
- [Bootstrapping Data](#bootstrapping-data)

## Execute Testing

Add following code in `SdjpaIntroApplicationTests.class` inside `./src/test/java/guru.springframework.sdjpaintro`:

```java
package guru.springframework.sdjpaintro;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class SdjpaIntroApplicationTests {
  @Autowired
  BookRepository bookRepository;

  @Test
  void testBookRepository() {
    long count = bookRepository.count();
    assertThat(count).isGreaterThan(0);
  }

  @Test
  void contextLoads() {
  }
}
```

Then execute the method and then the Spring Boot Test will run it with entire context.

<br/>
<div align="right">
  <b><a href="#spring-boot-test">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Spring Boot JPA Test Slice

The Spring Boot Test is bring up the entire context and it will slow down our tests if we get into more complex project. The **Test Slice** is a minimal context for us to work with only the database layer or others.

Create the `SpringBootJpaTestSlice` class with `@DataJpaTest` annotation:

```java
@DataJpaTest
public class SpringBootJpaTestSlice {
    @Autowired
    BookRepository bookRepository;

    @Test
    void testJpaTestSplice() {
        long countBefore = bookRepository.count();
        bookRepository.save(new Book("My Book", "1235555", "Self"));

        long countAfter = bookRepository.count();
        assertThat(countBefore).isLessThan(countAfter);
    }
}
```

<br/>
<div align="right">
  <b><a href="#spring-boot-test">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Test Transactions

A database transaction is a series of steps in the database that we do and then commit rollback. The Spring Boot will do an implicit transaction for it.

By default, each test will run in a transactional context and then roll back, and it may cause some problems. We can use `@Rollback(value = false)` or `@Commit` annotation:

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
public class SpringBootJpaTestSlice {
    @Autowired
    BookRepository bookRepository;

    @Rollback(value = false)
    @Order(1)
    @Test
    void testJpaTestSplice() {
        long countBefore = bookRepository.count();
        assertThat(countBefore).isEqualTo(0);
        bookRepository.save(new Book("My Book", "1235555", "Self"));

        long countAfter = bookRepository.count();
        assertThat(countBefore).isLessThan(countAfter);
    }

    @Order(2)
    @Test
    void testJpaTestSpliceTransaction() {
        long countBefore = bookRepository.count();
        assertThat(countBefore).isEqualTo(1);
    }
}
```

The sequence of the test will become very important when running test without rolling back.

<br/>
<div align="right">
  <b><a href="#spring-boot-test">[ ↥ Back To Top ]</a></b>
</div>
<br/>

## Bootstrapping Data

We can boostrap data with initializer methods with `@ComponentScan` annotation:

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataJpaTest
@ComponentScan(basePackages = {"guru.springframework.sdjpaintro.bootstrap"})
public class SpringBootJpaTestSlice {
    @Autowired
    BookRepository bookRepository;

    @Rollback(value = false)
    @Order(1)
    @Test
    void testJpaTestSplice() {
        long countBefore = bookRepository.count();
        assertThat(countBefore).isEqualTo(2);
        bookRepository.save(new Book("My Book", "1235555", "Self"));

        long countAfter = bookRepository.count();
        assertThat(countBefore).isLessThan(countAfter);
    }

    @Order(2)
    @Test
    void testJpaTestSpliceTransaction() {
        long countBefore = bookRepository.count();
        assertThat(countBefore).isEqualTo(3);
    }
}
```

<br/>
<div align="right">
  <b><a href="#spring-boot-test">[ ↥ Back To Top ]</a></b>
</div>
<br/>
package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BookDaoImpl implements BookDao {
    private final JdbcTemplate jdbcTemplate;

    public BookDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Book getById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM book where id = ?", getBookMapper(), id);
    }

    @Override
    public Book findBookByTitle(String title) {
        return jdbcTemplate.queryForObject("SELECT * FROM book where title = ?", getBookMapper(), title);
    }

    @Override
    public Book saveNewBook(Book book) {
        jdbcTemplate.update("INSERT INTO book (isbn, publisher, title, author_id) VALUES (?, ?, ?, ?)",
                book.getIsbn(), book.getPublisher(), book.getTitle(), book.getAuthorId());

        Long createdId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        return this.getById(createdId);
    }

    @Override
    public Book updateBook(Book book) {
        jdbcTemplate.update("UPDATE book set isbn = ?, publisher = ?, title = ?, author_id = ? where id = ?",
                book.getIsbn(), book.getPublisher(), book.getTitle(), book.getAuthorId(), book.getId());

        return this.getById(book.getId());
    }

    @Override
    public void deleteBookById(Long id) {
        jdbcTemplate.update("DELETE from book where id = ?", id);
    }

    private BookMapper getBookMapper(){
        return new BookMapper();
    }
}

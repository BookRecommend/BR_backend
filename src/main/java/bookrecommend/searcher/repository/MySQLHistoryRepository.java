package bookrecommend.searcher.repository;

import bookrecommend.searcher.domain.BookHistory;
import bookrecommend.searcher.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class MySQLHistoryRepository implements HistoryRepository {

    private final EntityManager em;

    public MySQLHistoryRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public BookHistory save(Member member, String title, String date, String author, String publisher, String isbn,String image) {
        BookHistory bookHistory = BookHistory.builder()
                .date(date)
                .author(author)
                .publisher(publisher)
                .isbn(isbn)
                .member(member)
                .title(title)
                .image(image)
                .build();
        em.persist(bookHistory);
        return bookHistory;
    }


    @Override
    public Optional<BookHistory> findById(int bookId) {
        BookHistory history = em.find(BookHistory.class, bookId);
        return Optional.ofNullable(history);
    }

    @Override
    public List<BookHistory> findAll() {
        return null;
    }

    @Override
    public void delete(int bookId) {
        BookHistory history = em.find(BookHistory.class, bookId);
        em.remove(history);
    }
}

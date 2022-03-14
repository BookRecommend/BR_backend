package bookrecommend.searcher.repository;

import bookrecommend.searcher.domain.BookHistory;
import bookrecommend.searcher.domain.Member;


import java.util.List;
import java.util.Optional;

public interface HistoryRepository {

    BookHistory save(Member member, String title, String date, String author, String publisher, String isbn, String image);
    Optional<BookHistory> findById(int bookId);
    List<BookHistory> findAll();
    void delete(int bookId);
}


package bookrecommend.searcher.repository;

import bookrecommend.searcher.domain.BookHistory;
import bookrecommend.searcher.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(String userId);
    Optional<Member> findById(String userId);
    List<Member> findAll();
    void delete(String userId);
}

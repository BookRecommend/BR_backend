package bookrecommend.searcher.service;

import bookrecommend.searcher.domain.BookHistory;
import bookrecommend.searcher.domain.Member;
import bookrecommend.searcher.repository.HistoryRepository;
import bookrecommend.searcher.repository.MemberRepository;
import bookrecommend.searcher.repository.MySQLHistoryRepository;
import bookrecommend.searcher.service.DTO.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository, HistoryRepository historyRepository){
        this.memberRepository = memberRepository;
        this.historyRepository = historyRepository;
    }


    public RegisterResponse checkRegisteredJoin(String userId) throws NoSuchElementException{
        Optional<Member> member = memberRepository.findById(userId);

//        return member.isPresent() ?
//                member.get() : memberRepository.save(userId);
//
        return member.isPresent() ?
                new RegisterResponse(true,member.get())
                : new RegisterResponse(false,memberRepository.save(userId));


    }


    public void changeInfo(Member changed) throws NoSuchElementException{
        Optional<Member> member = memberRepository.findById(changed.getMemberId());
        member.get().updateInfo(changed.getGender(), changed.getAge(), changed.getRegion(),changed.getSubregion());
    }

    public BookHistory saveHistory(String userId, String title, String date, String author, String publisher, String isbn, String image){
        Member member = memberRepository.findById(userId).get();
         return historyRepository.save(member,title, date, author, publisher, isbn, image);
    }

    public void deleteHistory(int bookId) throws NoSuchElementException {
        historyRepository.delete(bookId);
    }

    public void withdrawl(String userId) throws NoSuchElementException{
        memberRepository.delete(userId);
    }


    public List<Member> findMembers(){return memberRepository.findAll();}

    public Optional<Member> findOne(String MemberId) {return memberRepository.findById(MemberId);}

}

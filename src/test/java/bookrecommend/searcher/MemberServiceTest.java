package bookrecommend.searcher;

import bookrecommend.searcher.domain.BookHistory;
import bookrecommend.searcher.domain.Member;
import bookrecommend.searcher.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceTest {
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
    @Autowired MemberService memberService;

//    public MemberServiceTest(MemberService memberService,MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//        this.memberService = memberService;
//    }

    @Test
    public void registerUpdateDelete(){
        //Given register
        String userId = "test";

        Member changed = Member.builder()
                .memberId("test")
                .age(30)
                .gender("M")
                .region("11")
                .subregion("010")
                .build();

        //when
        memberService.checkRegisteredJoin(userId);

        //then
        Member testsubject = memberService.findOne("test").get();

        assertEquals(userId,testsubject.getMemberId());
        assertNull(testsubject.getGender());

        //when update
        memberService.changeInfo(changed);
        //then
        testsubject = memberService.findOne("test").get();
        assertEquals(30,testsubject.getAge());
        assertEquals("11",testsubject.getRegion());

        //when delete
        memberService.withdrawl("test");
        //then
        assertThrows(NoSuchElementException.class,()->memberService.findOne("test").get());
    }

    @Test
    public void bookRecord(){
        //Given
        String userId = "test";
        memberService.checkRegisteredJoin(userId);
        BookHistory book = BookHistory.builder()
                .isbn("9788983927927")
                .title("greatbook")
                .author("greatauthor")
                .date("2020-04-02")
                .image("goodimage")
                .build();

        memberService.saveHistory(userId,book.getTitle(),book.getDate(),book.getAuthor(),book.getPublisher(),book.getIsbn(),book.getImage());
        Member testsubject = memberService.findOne(userId).get();
        assertEquals(userId,testsubject.getMemberId());
        assertEquals("greatbook",testsubject.getHistory().get(0).getTitle());

        memberService.deleteHistory(testsubject.getHistory().get(0).getBookId());
        assertNull(testsubject.getHistory().get(0));


    }

}

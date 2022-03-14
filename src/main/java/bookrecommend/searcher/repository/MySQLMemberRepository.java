package bookrecommend.searcher.repository;

import bookrecommend.searcher.domain.Member;

import javax.persistence.EntityManager;
import java.util.*;

public class MySQLMemberRepository implements MemberRepository{
    private final EntityManager em;

    public MySQLMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Member save(String userId) {
        Member member = Member.builder()
                .memberId(userId)
                .build();
        em.persist(member);
        return member;
    }


    @Override
    public Optional<Member> findById(String id) {
        Member member= em.find(Member.class, id);
        return Optional.ofNullable(member);
    }


    @Override
    public List<Member> findAll() {
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }

    @Override
    public void delete(String id) {
        Member member= em.find(Member.class, id);
        em.remove(member);
    }
}

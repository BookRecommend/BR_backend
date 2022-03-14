package bookrecommend.searcher.controller;


import bookrecommend.searcher.controller.DTO.MemberDto;
import bookrecommend.searcher.domain.Member;
import bookrecommend.searcher.service.DTO.RegisterResponse;
import bookrecommend.searcher.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.NoSuchElementException;

@RestController // restapi 기능만 수행할시.
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService){this.memberService = memberService;}

    @PostMapping("/login")
    public MemberDto.Response login(@RequestBody MemberDto.Request req) {
        try {
            RegisterResponse response = memberService.checkRegisteredJoin(req.getUsername());
            Member member = response.getMember();
            Collections.reverse(member.getHistory());
            MemberDto.Info info = MemberDto.Info.builder()
                    .username(member.getMemberId())
                    .age(member.getAge())
                    .region(member.getRegion())
                    .subregion(member.getSubregion())
                    .gender(member.getGender())
                    .history(member.getHistory())
                    .build();

        if(response.getRegistered()) return new MemberDto.Response(info, 200, "registered");
        else return new MemberDto.Response(info, 200, "new");

        }catch(Exception e) {
            return new MemberDto.Response(null,400, e.getMessage());
        }
    }

    @PutMapping("/member")
    public MemberDto.Response updateMember(@RequestBody MemberDto.Request req){
        try {
            Member member = Member.builder()
                    .memberId(req.getUsername())
                    .age(req.getAge())
                    .gender(req.getGender())
                    .region(req.getRegion())
                    .subregion(req.getSubregion())
                    .build();

            memberService.changeInfo(member);
            return new MemberDto.Response(null,200,"success");
        }catch(NoSuchElementException e){
            return new MemberDto.Response(null,400,"not found");
        }catch(Exception e) {
            return new MemberDto.Response(null,400, e.getMessage());
        }
    }


    @DeleteMapping("/member")
    public MemberDto.Response removeMember(@RequestBody MemberDto.Request req){
        try{
            memberService.withdrawl(req.getUsername());
            return new MemberDto.Response(null,200,"success");
        }catch(NoSuchElementException e){
            return new MemberDto.Response(null,400,"not found");
        }catch(Exception e) {
            return new MemberDto.Response(null,400, e.getMessage());
        }
    }

    @DeleteMapping("/member/history")
    public MemberDto.Response removeOneHistory(@RequestBody MemberDto.bookRequest req){
        try{
            memberService.deleteHistory(req.getBookId());
            return new MemberDto.Response(null,200,"success");
        }catch(NoSuchElementException e){
            return new MemberDto.Response(null,400,"not found");
        }catch(Exception e) {
            return new MemberDto.Response(null,400, e.getMessage());
        }
    }


}


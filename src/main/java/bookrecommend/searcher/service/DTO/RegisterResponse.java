package bookrecommend.searcher.service.DTO;

import bookrecommend.searcher.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RegisterResponse {

    private Boolean registered;
    private Member member;

    public RegisterResponse(Boolean registered,Member member){
        this.registered= registered;
        this.member = member;
    }
}

package bookrecommend.searcher.controller.DTO;

import bookrecommend.searcher.domain.BookHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class MemberDto{

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info{
        private String username;
        private String gender;
        private int age;
        private String region;
        private String subregion;
        private List<BookHistory> history;

    }

    @Getter
    @Setter
    public static class Request{
        private String username;
        private String gender;
        private int age;
        private String region;
        private String subregion;
    }

    @Getter
    @Setter
    public static class bookRequest{
        private int bookId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response{
        private Info info;
        private int returnCode;
        private String message;
    }

}

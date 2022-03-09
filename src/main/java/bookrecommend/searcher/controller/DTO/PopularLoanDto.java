package bookrecommend.searcher.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PopularLoanDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info{
        private List<PopularLoanDto.PopularLoan> result;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response{
        private PopularLoanDto.Info info;
        private int returnCode;
        private String message;
    }

    @Getter
    @Setter
    @Builder
    public static class PopularLoan{
        private String rank;
        private String title;
        private String author;
        private String publisher;
        private String isbn;
        private String image;
        private String year;
    }
}

package bookrecommend.searcher.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class SearchDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info{
        List<SrcResult> result;
    }

    @Getter
    @Setter
    public static class Request{
        private String title;
        private String author;
        private String publisher;
        private String isbn;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response{
        private SearchDto.Info info;
        private int returnCode;
        private String message;
    }

    @Getter
    @Setter
    @Builder
    public static class SrcResult{
        private String title;
        private String author;
        private String publisher;
        private String isbn;
        private String year;
        private String image;
        private String description;
    }
}

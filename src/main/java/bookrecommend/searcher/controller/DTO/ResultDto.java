package bookrecommend.searcher.controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ResultDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info{
        private String price;
        private String stock;
        private int bookId;
        private List<Library> libraries;
    }

    @Getter
    public static class Request{
        private String username;
        private String title;
        private String date;
        private String author;
        private String publisher;
        private String region;
        private String subregion;
        private String isbn;
        private String image;
        private Boolean frommypage;
    }



    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response{
        private ResultDto.Info info;
        private int returnCode;
        private String message;
    }

    @AllArgsConstructor
    @Getter
    public static class Library{
        private String name;
        private String address;
        private String latitude;
        private String longitude;
        private String available;
    }
}

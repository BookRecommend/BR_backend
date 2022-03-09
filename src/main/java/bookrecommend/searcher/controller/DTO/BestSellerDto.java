package bookrecommend.searcher.controller.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class BestSellerDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Info{
        private String category;
        private List<BestSellerDto.BestSeller> bookList;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response{
        private BestSellerDto.Info info;
        private int returnCode;
        private String message;
    }
    @Getter
    @Setter
    @Builder
    public static class BestSeller{
        private int rank;
        private String description;
        private String title;
        private String author;
        private String publisher;
        private String image;
        private String isbn;
    }
}



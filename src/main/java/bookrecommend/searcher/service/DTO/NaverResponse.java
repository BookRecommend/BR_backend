package bookrecommend.searcher.service.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NaverResponse {
    private List<BookSearchResult> items;


    @Getter
    @Setter
    public static class BookSearchResult {
        private String title;
        private String author;
        private String publisher;
        private String image;
        private String isbn;
        private String description;
        private String pubdate;

        public BookSearchResult(){};
        public BookSearchResult(String description){
            this.description = description;
        }

    }
}
package bookrecommend.searcher.service.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LibraryResponse {

    private Response response;

    @Getter
    @Setter
    public static class Response {
        private List<Doc> docs;
        private List<Lib> libs;
        private searchResult result;
    }

    @Getter
    @Setter
    public static class Doc {
        private Content doc;

    }

    @Getter
    @Setter
    public static class Content {
        private String ranking;
        private String bookname;
        private String authors;
        private String publisher;
        private String isbn13;
        private String publication_year;
        private String bookImageURL;
    }

    @Getter
    @Setter
    public static class Lib{
        private Library lib;
    }


    @Getter
    @Setter
    public static class Library {
        private String libCode;
        private String latitude;
        private String longitude;
        private String libName;
        private String address;
    }

    @Getter
    @Setter
    public static class searchResult{
        private String hasBook;
        private String loanAvailable;
    }
}

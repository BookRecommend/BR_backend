package bookrecommend.searcher.service.DTO;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;
import java.util.List;

@Getter
@Setter
public class AladdinResponse {
    private String searchCategoryName;
    private List<Book> item;

    @Getter
    @Setter
    public static class Book {
        private String title;
        private String description;
        private String author;
        private String publisher;
        private String isbn13;
        private String cover;
        private String priceSales;
        private String stockStatus;


        public Book(){}

        public Book(String priceSales,String stockStatus){
            this.priceSales = priceSales;
            this.stockStatus = stockStatus;
        }
    }


}

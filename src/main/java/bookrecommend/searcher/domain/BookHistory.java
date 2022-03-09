package bookrecommend.searcher.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@Getter
@Entity

public class BookHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookId;
    private String title;
    private String date;
    private String author;
    private String publisher;
    private String image;
    private String isbn;

    @ManyToOne
    @JoinColumn(name="memberId")
    @JsonBackReference
    private Member member;


    protected BookHistory(){}
}



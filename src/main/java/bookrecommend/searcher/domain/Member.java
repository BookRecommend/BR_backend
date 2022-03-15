package bookrecommend.searcher.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Optional;

@Builder
@AllArgsConstructor
@Getter
@Entity

public class Member {
    @Id
    private String memberId;
    private String gender;
    private int age;
    private String region;
    private String subregion;

    @OneToMany(mappedBy = "member") // bookhistory의 member entity 참조.
    @JsonManagedReference
    private List<BookHistory> history;


    protected Member(){}

    public void updateInfo(String gender,int age, String region,String subregion){
        this.gender = gender;
        this.age = age;
        this.region = region;
        this.subregion= subregion;
    }
    public Optional<List<BookHistory>> getHistory(){
        return Optional.ofNullable(history);
    }

}

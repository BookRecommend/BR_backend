package bookrecommend.searcher.service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LibraryBookResponse {
    private String name;
    private String address;
    private String latitude;
    private String longitude;
    private String available;
}

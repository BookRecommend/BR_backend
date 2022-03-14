package bookrecommend.searcher.controller;

import bookrecommend.searcher.controller.DTO.BestSellerDto;
import bookrecommend.searcher.controller.DTO.PopularLoanDto;
import bookrecommend.searcher.controller.DTO.ResultDto;
import bookrecommend.searcher.controller.DTO.SearchDto;
import bookrecommend.searcher.domain.BookHistory;
import bookrecommend.searcher.service.*;
import bookrecommend.searcher.service.DTO.AladdinResponse;
import bookrecommend.searcher.service.DTO.LibraryResponse;
import bookrecommend.searcher.service.DTO.NaverResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@RestController


public class APIController {
    @Autowired
    private final ExternalAPIService externalAPIService;
    @Autowired
    private final MemberService memberService;

    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    public APIController(ExternalAPIService externalAPIService, MemberService memberService) {
        this.externalAPIService = externalAPIService;
        this.memberService = memberService;
    }

    @GetMapping("/search")
    public SearchDto.Response search(@RequestParam String query){

        NaverResponse naverResponse = externalAPIService.naverBookSearch(query).block();
        List<SearchDto.SrcResult> list = new ArrayList<>();

        for(NaverResponse.BookSearchResult ele : naverResponse.getItems()){
            list.add(SearchDto.SrcResult.builder()
                    .title(ele.getTitle())
                    .author(ele.getAuthor())
                    .isbn(ele.getIsbn().split(" ")[1])
                    .image(ele.getImage())
                    .description(ele.getDescription())
                    .year(ele.getPubdate().substring(0,4))
                    .publisher(ele.getPublisher())
                    .build()
            );
        }

        SearchDto.Info info =SearchDto.Info.builder()
                .result(list)
                .build();

        return new SearchDto.Response(info,200,"success");

    }

    @GetMapping(value={"/","/bestseller"})
    public BestSellerDto.Response getBestSeller(@RequestParam(required = false) String code){
        AladdinResponse list = externalAPIService.aladdinBestSeller(code);
        List<BestSellerDto.BestSeller> result = new ArrayList<>();
        int num = 0;

        for(AladdinResponse.Book ele : list.getItem()){
            num++;
            result.add(BestSellerDto.BestSeller.builder()
                    .rank(num)
                    .description(ele.getDescription())
                    .title(ele.getTitle())
                    .author(ele.getAuthor())
                    .image(ele.getCover())
                    .year(ele.getPubDate().substring(0,4))
                    .publisher(ele.getPublisher())
                    .isbn(ele.getIsbn13())
                    .build());
        }
        BestSellerDto.Info info = BestSellerDto.Info.builder()
                .category(list.getSearchCategoryName())
                .bookList(result)
                .build();

        return new BestSellerDto.Response(info,200,"success");
    }

    @GetMapping("/library")
    public PopularLoanDto.Response getPopularBook
            (@RequestParam(required = false)String startdate, @RequestParam(required = false)String enddate, @RequestParam(required = false)String age,@RequestParam(required = false)String code){
        LibraryResponse libraryResponse = externalAPIService.libraryPopularBook(startdate,enddate,age,code);
        List<PopularLoanDto.PopularLoan> result = new ArrayList<>();

        for(LibraryResponse.Doc ele : libraryResponse.getResponse().getDocs()){
            result.add(PopularLoanDto.PopularLoan.builder()
                    .rank(ele.getDoc().getRanking())
                    .title(ele.getDoc().getBookname())
                    .author(ele.getDoc().getAuthors())
                    .publisher(ele.getDoc().getPublisher())
                    .isbn(ele.getDoc().getIsbn13())
                    .year(ele.getDoc().getPublication_year())
                    .image(ele.getDoc().getBookImageURL())
                    .build());
        }

        PopularLoanDto.Info info =PopularLoanDto.Info.builder()
                .result(result)
                .build();

        return new PopularLoanDto.Response(info,200,"success");
    }

    @PostMapping("/result")
    public  ResultDto.Response getResult(@RequestBody ResultDto.Request req){
        try {
            BookHistory history =(req.getFrommypage()) ? BookHistory.builder().build() : memberService.saveHistory(req.getUsername(),req.getTitle(),req.getDate(), req.getAuthor(), req.getPublisher(), req.getIsbn(),req.getImage());
            //알라딘 서점 책 검색
            Mono<NaverResponse.BookSearchResult> naverResponse = (req.getIsExtraSearchNeeded()) ? externalAPIService.naverBookSearch(req.getIsbn())
                    .map(t-> t.getItems().get(0))
                    .defaultIfEmpty(new NaverResponse.BookSearchResult())
                    : Mono.just(new NaverResponse.BookSearchResult());

            Mono<AladdinResponse.Book> aladdinResponse = externalAPIService.aladdinSearch(req.getIsbn(), req.getTitle())
                                                    .map(t->t.getItem().get(0))
                                                    .defaultIfEmpty(new AladdinResponse.Book("unknown","unknown"));
            log.info(req.getUsername(),req.getIsbn());

            //주변 도서관 중 책 보유한 것만 선정
            Mono<List<ResultDto.Library>> libraryResponse =
                    externalAPIService.showBookStatus(req.getIsbn(), req.getRegion(), req.getSubregion())
                    .flatMap(t->Flux.just(new ResultDto.Library(t.getName(),t.getAddress(),t.getLatitude(),t.getLongitude(),t.getAvailable())))
                    .collectList();

            ResultDto.Info info = Mono.zip(aladdinResponse,libraryResponse, naverResponse)
                    .map(tuple -> ResultDto.Info.builder()
                            .price(tuple.getT1().getPriceSales())
                            .link(tuple.getT1().getLink())
                            .stock((tuple.getT1().getStockStatus().equals("")) ? "available" : "not available")
                            .libraries(tuple.getT2())
                            .bookId(history.getBookId())
                            .description(tuple.getT3().getDescription())
                            .build())
                    .block();

            return new ResultDto.Response(info, 200, "success");

        }catch(Exception e){
            return new ResultDto.Response(null, 400, e.getMessage());
        }
        }

}

package bookrecommend.searcher.service;

import bookrecommend.searcher.service.DTO.AladdinResponse;
import bookrecommend.searcher.service.DTO.LibraryBookResponse;
import bookrecommend.searcher.service.DTO.LibraryResponse;
import bookrecommend.searcher.service.DTO.NaverResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import javax.annotation.Resource;


public class ExternalAPIService {


    private final WebClient webClient;
    private final Environment env;


    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
    public ExternalAPIService(WebClient webClient, Environment env) {
        this.webClient = webClient;
        this.env = env;
    }

    public Mono<NaverResponse> naverBookSearch(String query){
         String clientId = env.getProperty("naverbooksearch.clientID");
         String clientSecret = env.getProperty("naverbooksearch.clientsecret");
         String display ="50";
         String text = "?display="+display+"&query="+query;

        if(text.charAt(1)=='&') text= "?"+text.substring(2);
        log.info(text);

        return  webClient.mutate()
                 .defaultHeader("X-Naver-Client-Id", clientId)
                 .defaultHeader("X-Naver-Client-Secret", clientSecret)
                 .baseUrl("https://openapi.naver.com/v1/search/book.json")
                 .build()
                 .get()
                 .uri(text)
                 .accept(MediaType.APPLICATION_JSON)
                 .retrieve()
                 .bodyToMono(NaverResponse.class);

//                 .bodyToMono(String.class)
//                 .block();
    }

    public AladdinResponse aladdinBestSeller(String categoryId){
         String ttbKey = env.getProperty("aladdinbestseller.ttb");
         if(categoryId==null) categoryId = "0";
         String MaxResults = "10";


         String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemList.aspx";
        return  webClient.mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri("?ttbkey="+ttbKey+"&QueryType=Bestseller&MaxResults="+MaxResults+"&start=1&SearchTarget=Book&output=JS&CategoryId={categoryId}&Version=20131101",categoryId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AladdinResponse.class).block();
    }



    public LibraryResponse libraryPopularBook(String startDt, String endDt, String age, String code){

         String baseUrl = "http://data4library.kr/api/loanItemSrch";
         String authKey = env.getProperty("library.authkey");
         String pageSize = "10";
         String uri ="?";

        uri+="authKey="+authKey+"&pageSize="+pageSize+"&format=json";
         if(startDt!=null&&endDt!=null) uri+="&startDt="+startDt+"&endDt="+endDt;
         if(age!=null)uri+="&age="+age;
         if(code!=null) uri+="&kdc="+code;

        return  webClient.mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON,MediaType.ALL)
                .retrieve()
                .bodyToMono(LibraryResponse.class).block();
    }

    public Mono<AladdinResponse> aladdinSearch(String isbn, String title){
        String ttbKey = env.getProperty("aladdinbestseller.ttb");
        String baseUrl = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx";
        return  webClient.mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri("?ttbkey="+ttbKey+"&itemIdType=ISBN13&ItemId="+isbn+"&start=1&output=JS&Version=20131101")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AladdinResponse.class);
    }

    @Cacheable(value = "libraryNearby", key ="#region.concat(#subregion)")
    private Mono<LibraryResponse> librarySearch(String region, String subregion){
//        log.info("cache missed on code :"+region+subregion);
        String baseUrl = "http://data4library.kr/api/libSrch";
        String authKey = env.getProperty("library.authkey");
        String uri = "?";
        if(region==null) region = "11";
        String dtl_region = (subregion==null)? "11010": region+subregion;

        uri+="authKey="+authKey+"&region="+region+"&dtl_region="+dtl_region+"&format=json";

        return  webClient.mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON,MediaType.ALL)
                .retrieve()
                .bodyToMono(LibraryResponse.class);
    }

    private Mono<LibraryResponse> libraryHasBook(String libCode, String isbn){
        String baseUrl = "http://data4library.kr/api/bookExist";
        String authKey = env.getProperty("library.authkey");
        String uri = "?";

        uri+="authKey="+authKey+"&libCode="+libCode+"&isbn13="+isbn+"&format=json";

        return  webClient.mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON,MediaType.ALL)
                .retrieve()
                .bodyToMono(LibraryResponse.class);
    }

    public Flux<LibraryBookResponse> showBookStatus(String isbn, String region, String subregion){
        Mono<LibraryResponse> libraryResponse = librarySearch(region,subregion);
        Flux<LibraryResponse.Lib> libs = libraryResponse.flatMapMany(t-> Flux.fromStream(t.getResponse().getLibs().stream()));
        Flux<LibraryResponse> result = libs.map(t->libraryHasBook(t.getLib().getLibCode(),isbn)).flatMap(x->x);
        Flux<LibraryBookResponse> libraries = Flux.zip(libs,result)//.log()
                .flatMap(tuple-> Flux.just(Tuples.of(tuple.getT1().getLib(),tuple.getT2().getResponse().getResult().getHasBook(), tuple.getT2().getResponse().getResult())))//.log()
                .filter(a ->a.getT2().equals("Y"))//.log()
                .flatMap(ele -> Flux.just(new LibraryBookResponse(ele.getT1().getLibName(),ele.getT1().getAddress(),ele.getT1().getLatitude(),ele.getT1().getLongitude(),ele.getT3().getLoanAvailable())));//.log();



        return libraries;
    }

//    @Scheduled(fixedRate = 100000)
//    @CacheEvict(value = "libraryNearby" ,allEntries = true)
//    public void clearCache(){
//        log.info("cache cleared");
//    }

}



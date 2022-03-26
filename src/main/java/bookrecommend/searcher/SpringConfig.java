package bookrecommend.searcher;

import bookrecommend.searcher.aop.TimeTraceAop;
import bookrecommend.searcher.repository.MemberRepository;
import bookrecommend.searcher.repository.MySQLHistoryRepository;
import bookrecommend.searcher.repository.MySQLMemberRepository;
import bookrecommend.searcher.service.ExternalAPIService;
import bookrecommend.searcher.service.MemberService;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@EnableCaching
@Configuration
//@PropertySource("classpath:user.properties")
public class SpringConfig implements WebMvcConfigurer{
    private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
//    private final DataSource dataSource;
    private final EntityManager em;
    private final Environment env;

    public SpringConfig(DataSource dataSource,EntityManager em ,Environment env){
//        this.dataSource = dataSource;
        this.em = em;
        this.env = env;
    }

    @Bean
    public MemberRepository memberRepository(){ return new MySQLMemberRepository(em);
    }

    @Bean
    public MySQLHistoryRepository bookRepository(){return new MySQLHistoryRepository(em);
    }

    @Bean
    public MemberService memberService(){ return new MemberService(memberRepository(),bookRepository());
    }

    @Bean
    public TimeTraceAop timeTraceAop(){return new TimeTraceAop();
    }

    @Bean
    public CacheManager cacheManager(){
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(List.of(new ConcurrentMapCache("libraryNearby",true)));
        return simpleCacheManager;
    }


    @Bean
    public WebClient webClient(){
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) //miliseconds
                .doOnConnected(
                        conn -> conn.addHandlerLast(new ReadTimeoutHandler(5))  //sec
                        .addHandlerLast(new WriteTimeoutHandler(60)) //sec
                        );

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2*1024*1024))
                .build();

        return WebClient.builder()
                .baseUrl("http://localhost:5011")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(
                        (req, next) -> next.exchange(
                                ClientRequest.from(req).header("from", "webclient").build()
                        )
                )
                .filter(
                        ExchangeFilterFunction.ofRequestProcessor(
                                clientRequest -> {
                                    log.info(">>>>>>>>>> REQUEST <<<<<<<<<<");
                                    log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
                                    clientRequest.headers().forEach(
                                            (name, values) -> values.forEach(value -> log.info("{} : {}", name, value))
                                    );
                                    return Mono.just(clientRequest);
                                }
                        )
                )
                .filter(
                        ExchangeFilterFunction.ofResponseProcessor(
                                clientResponse -> {
                                    log.info(">>>>>>>>>> RESPONSE <<<<<<<<<<");
                                    clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
                                    return Mono.just(clientResponse);
                                }
                        )
                )
                .exchangeStrategies(exchangeStrategies)
                .defaultHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.3")
                .defaultCookie("httpclient-type", "webclient")
                .build();
    }

    @Bean
    public ExternalAPIService externalAPIService(){return new ExternalAPIService(webClient(),env);}

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }

}

package bookrecommend.searcher;

import bookrecommend.searcher.service.ExternalAPIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SearcherApplicationTests {

	@Autowired
	ExternalAPIService externalAPIService;

	@Test
	void contextLoads() {
		long start = System.currentTimeMillis();
		System.out.println("start");
		externalAPIService.showBookStatus("9791165341909","11","010").blockFirst();
		long end1 = System.currentTimeMillis();
		long lap1 = end1 - start;
		System.out.println("time check :"+lap1);
		externalAPIService.showBookStatus("9788983928092","11","010").blockFirst();
		long end2 = System.currentTimeMillis();
		long lap2 = end2 -end1;
		System.out.println("time check2 :" + lap2);
		externalAPIService.showBookStatus("9791165341909","11","010").blockFirst();
		long end3 = System.currentTimeMillis();
		long lap3 = end3 -end2;
		long elapsed = end3 - start;
		System.out.println("time check2 :" + lap3);
		System.out.println("end of the test druation : "+ elapsed + "ms");
	}

}

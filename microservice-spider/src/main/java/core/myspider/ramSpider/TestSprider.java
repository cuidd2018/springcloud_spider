
package core.myspider.ramSpider;

import core.spiderCore.entities.CrawlDatums;
import core.spiderCore.entities.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestSprider extends RamCrawler {
     @Autowired private RamDBManager ramDBManager;

    public TestSprider() {
    }

    public void testSpiderStart() throws Exception {
        TestSprider crawler = new TestSprider();
        this.abstractDbManager = ramDBManager;
        crawler.addSeed("https://book.douban.com/tag/");
        crawler.setAutoParse(true);

        /**
         *可以设置每个线程visit的间隔，这里是毫秒
         *crawler.setVisitInterval(1000);
         *可以设置http请求重试的间隔，这里是毫秒
         *crawler.setRetryInterval(1000);
         */
        crawler.setThreads(30);
        crawler.start(3);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
    }
}

package spider.spiderCore.crawldb.Idbutil;

import spider.spiderCore.entities.CrawlDatum;

/**
 *入口注入
 */
public interface Injector {
      void inject(CrawlDatum datum) throws Exception;
}

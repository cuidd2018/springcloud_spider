package core.spiderCore.crawldb.Idbutil;

import core.spiderCore.entities.CrawlDatum;

/**
 *入口注入
 */
public interface Injector {
      void inject(CrawlDatum datum) throws Exception;
}

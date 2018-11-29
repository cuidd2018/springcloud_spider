package mainentercore.spiderCore.crawldb.Idbutil;

import mainentercore.spiderCore.entities.CrawlDatum;

/**
 *入口注入
 */
public interface Injector {
      void inject(CrawlDatum datum) throws Exception;
}

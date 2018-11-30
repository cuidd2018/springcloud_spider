package spider.spiderCore.http.IRequestor;


import spider.spiderCore.entities.CrawlDatum;
import spider.spiderCore.entities.Page;

/**
 *发送请求接口
 */
public interface Requester {
     Page getResponse(CrawlDatum crawlDatum) throws Exception;
}

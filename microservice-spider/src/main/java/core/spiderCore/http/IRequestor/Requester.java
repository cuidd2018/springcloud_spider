package core.spiderCore.http.IRequestor;


import core.spiderCore.entities.CrawlDatum;
import core.spiderCore.entities.Page;

/**
 *发送请求接口
 */
public interface Requester {
     Page getResponse(CrawlDatum crawlDatum) throws Exception;
}

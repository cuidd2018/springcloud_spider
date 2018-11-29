package mainentercore.spiderCore.http.IRequestor;


import mainentercore.spiderCore.entities.CrawlDatum;
import mainentercore.spiderCore.entities.Page;

/**
 *发送请求接口
 */
public interface Requester {
     Page getResponse(CrawlDatum crawlDatum) throws Exception;
}

package spider.myspider.spiderComponent;

import spider.spiderCore.entities.CrawlDatum;
import spider.spiderCore.entities.Page;
import spider.spiderCore.http.HttpRequest;
import spider.spiderCore.http.IRequestor.Requester;
import org.springframework.stereotype.Component;

@Component
public class MyRequester implements Requester {

    /**
     * @Title：${enclosing_method}
     * @Description: [实现 requester接口，调用自身方法获取网页]
     * 代理设置 proxy
     */
    @Override
    public Page getResponse(CrawlDatum crawlDatum) throws Exception {
        HttpRequest request = new HttpRequest(crawlDatum);
       /* Proxy proxy=new Proxy(Proxy.Type.HTTP, new InetSocketAddress("14.18.16.67",80));
//        HashMap<String ,Integer> proxy= site.getProxys();
//        for (Map.Entry<String, Integer> entry : proxy.entrySet()) {
//            System.out.println(entry.getKey());
//            System.out.println(entry.getValue());
//            proxys.add(entry.getKey(),entry.getValue());
//        }
//        request.setProxy(proxys.nextRandom());
        request.setProxy(proxy);*/
        return request.responsePage();
    }
}

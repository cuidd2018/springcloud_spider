package spider.myspider.ramSpider;

import spider.spiderCore.crawldb.AbstractGenerator;
import spider.spiderCore.crawldb.Idbutil.GeneratorFilter;
import spider.spiderCore.entities.CrawlDatum;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class RamGenerator extends AbstractGenerator<HashMap> {
    private static Logger LOG = Logger.getLogger(RamGenerator.class);
    public RamGenerator(GeneratorFilter generatorFilter) {
        setFilter(generatorFilter);
    }

    @Override
    public CrawlDatum nextWithoutFilter() throws Exception {
        //获取爬虫任务数据库
        HashMap crawl = getDataBase().getCrawlDB();
        //获取遍历器
        Iterator<Map.Entry> iterator = crawl.entrySet().iterator();
        if(iterator.hasNext()){
            Object key = iterator.next().getKey();
            CrawlDatum datum = (CrawlDatum) crawl.remove(key);
            return datum;
        }else{
            return null;
        }
    }

    @Override
    public void close() throws Exception {

    }
}

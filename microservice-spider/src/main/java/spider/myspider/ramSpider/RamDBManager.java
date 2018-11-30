package spider.myspider.ramSpider;

import spider.spiderCore.crawldb.AbstractDBManager;
import spider.spiderCore.entities.CrawlDatum;
import spider.spiderCore.entities.CrawlDatums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RamDBManager extends AbstractDBManager<HashMap> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDBManager.class);
    HashMap<String, CrawlDatum> fetchList = (HashMap<String, CrawlDatum>) getDataBase().getFetchDB();
    HashMap<String, CrawlDatum> crawlList = (HashMap<String, CrawlDatum>) getDataBase().getCrawlDB();
    HashMap<String, CrawlDatum> linkList = (HashMap<String, CrawlDatum>) getDataBase().getLinkDB();
    public RamDBManager(RamGenerator ramGenerator, RamDB ramDB) {
        super(ramGenerator, ramDB);
    }

    @Override
    public boolean isDBExists() {
        return true;
    }

    @Override
    public void clear() {
        getDataBase().clear();
    }

    @Override
    public void open() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {
        HashMap<String, CrawlDatum> list = (HashMap<String, CrawlDatum>) getDataBase().getCrawlDB();
        if (!force) {
            if (list.containsKey(datum.getStatus())) {
                return;
            }
        }
        datum.setStatus(0);
        list.put(String.valueOf(datum.getStatus()), datum);
    }

    @Override
    public void inject(CrawlDatums datums, boolean force) throws Exception {
        for (CrawlDatum datum : datums) {
            inject(datum, force);
        }
    }

    @Override
    public void merge() throws Exception {
        LOG.info("start merge");

        /*合并fetch库*/
        LOG.info("merge fetch database");
        for (Map.Entry<String, CrawlDatum> fetchEntry : fetchList.entrySet()) {
            crawlList.put(fetchEntry.getKey(), fetchEntry.getValue());
        }

        /*合并link库*/
        LOG.info("merge link database");
        for (String url : linkList.keySet()) {
            if (!crawlList.containsKey(url)) {
                crawlList.put(url, linkList.get(url));
            }
        }

        LOG.info("end merge");
        LOG.info("crawlDB size: " + String.valueOf(crawlList.size()));
        fetchList.clear();
        LOG.info("remove fetch database");
        linkList.clear();
        LOG.info("remove link database");

    }

    @Override
    public void initSegmentWriter() throws Exception {
    }

    @Override
    public synchronized void writeFetchSegment(CrawlDatum fetchDatum) throws Exception {

        fetchList.put(String.valueOf(fetchDatum.getStatus()), fetchDatum);
    }

    @Override
    public synchronized void writeParseSegment(CrawlDatums parseDatums) throws Exception {
        //LOG.info("写入解析后续任务");
        for (CrawlDatum datum : parseDatums) {
            linkList.put(datum.url(), datum);
        }
    }

    @Override
    public void closeSegmentWriter() throws Exception {
    }

}

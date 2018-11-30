package spider.spiderCore.fetcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spider.spiderCore.entities.CrawlDatum;
import spider.spiderCore.entities.CrawlDatums;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
* @author 一杯咖啡
* @desc 任务缓存管道
* @createTime
*/
public class FetchQueue {
    private static final Logger LOG = LoggerFactory.getLogger(FetchQueue.class);

    public AtomicInteger totalSize = new AtomicInteger(0);

    public final List<CrawlDatum> queue = Collections.synchronizedList(new LinkedList<CrawlDatum>());


    public void clearQueue() {
        queue.clear();
    }


    public int getSize() {
        return queue.size();
    }

    /**
     * desc:添加任务到queue中
     *
     * @Return: void
     **/
    public synchronized void addCrawlDatum(CrawlDatum item) {
        if (item == null) {
            return;
        }
        queue.add(item);
        totalSize.incrementAndGet();
    }

    /**
     * desc:从queue中提取任务
     **/
    public synchronized CrawlDatum getCrawlDatum() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.remove(0);
    }

    /**
     * desc:打印queue中的任务
     *
     **/
    public synchronized void dump() {
        for (int i = 0; i < queue.size(); i++) {
            CrawlDatum it = queue.get(i);
            LOG.info("  " + i + ". " + it.url());
        }
    }

}

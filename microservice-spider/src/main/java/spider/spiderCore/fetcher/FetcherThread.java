package spider.spiderCore.fetcher;

import spider.spiderCore.entities.CrawlDatum;
import spider.spiderCore.entities.CrawlDatums;
import spider.spiderCore.fetcher.IFetcherTools.Executor;
import spider.spiderCore.fetcher.IFetcherTools.NextFilter;
import spider.spiderCore.spiderConfig.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author 一杯咖啡
 * @desc 任务消费者
 * @createTime
 */
public class FetcherThread implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(FetcherThread.class);
    /** 组件从fetcher中获取*/
    private Executor executor;
    private NextFilter nextFilter;
    private FetchQueue fetchQueue;
    private QueueFeeder queueFeeder;
    private Fetcher fetcher;
    private Configuration configuration;

    /**
     * desc: 初始化执行线程
     **/
    public FetcherThread(Fetcher fetcher) {
        this.fetcher = fetcher;
        this.configuration = fetcher.getConfig();
        this.fetchQueue = fetcher.getFetchQueue();
        this.queueFeeder = fetcher.getQueueFeeder();
        this.executor = fetcher.getExecutor();
        this.nextFilter = fetcher.getNextFilter();
    }

    @Override
    public void run() {
        CrawlDatum datum = null;
        //判断调度器是否处于运行状态
        while (fetcher.isFetcherRuning()) {
            //从queue中取出任务
            datum = fetchQueue.getCrawlDatum();
            if (datum == null) {
                //判断任务队列是否有任务，如果没有，直接退出线程，只要 任务管道
                if (queueFeeder.isAlive() || fetchQueue.getSize() > 0) {
                    //pause(0, 500);
                    continue;
                } else {
                    break;
                }
            } else {
                CrawlDatums next = new CrawlDatums();
                try {
                    //调用函数执行分析当前页面，取出页面的urls 形成 next。
                    executor.execute(datum, next);
                    //过滤后续任务
                    if (nextFilter != null) {
                        CrawlDatums filteredNext = new CrawlDatums();
                        for (int i = 0; i < next.size(); i++) {
                            CrawlDatum filterResult = nextFilter.filter(next.get(i), datum);
                            if (filterResult != null) {
                                filteredNext.add(filterResult);
                            }
                        }
                        next = filteredNext;
                    }
                    LOG.info("done: " + datum.briefInfo());
                    //当前页面标记为 已爬取
                    datum.setStatus(CrawlDatum.STATUS_DB_SUCCESS);
                } catch (Exception ex) {
                    LOG.info("failed: " + datum.briefInfo(), ex);
                    datum.setStatus(CrawlDatum.STATUS_DB_FAILED);
                }
                datum.incrExecuteCount(1);
                datum.setExecuteTime(System.currentTimeMillis());
                try {
                    //写入当前任务到已抓取的任务库
                    fetcher.getAbstractDbManager().writeFetchSegment(datum);
                    if (datum.getStatus() == CrawlDatum.STATUS_DB_SUCCESS && !next.isEmpty()) {
                        //写入当前任务提取的符合条件的任务到 后续未抓取的任务库
                        fetcher.getAbstractDbManager().writeParseSegment(next);
                    }
                } catch (Exception ex) {
                    LOG.info("Exception when updating db", ex);
                }

                //当前页面执行完毕，等待时间
                long executeInterval = configuration.getExecuteInterval();
                if (executeInterval > 0) {
                    pause(0, executeInterval);
                }
            }
        }
    }

    /**
     * desc: 线程休眠
     **/
    public void pause(int second, long mills) {
        try {
            TimeUnit.SECONDS.sleep(second);
            TimeUnit.MILLISECONDS.sleep(mills);
        } catch (InterruptedException e) {
            LOG.error("spinWaiting thread sleep exception");
        }
    }
}

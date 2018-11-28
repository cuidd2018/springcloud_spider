package core.spiderCore.fetcher;


import core.spiderCore.crawldb.AbstractDBManager;
import core.spiderCore.crawldb.AbstractGenerator;
import core.spiderCore.crawldb.Idbutil.GeneratorFilter;
import core.spiderCore.entities.CrawlDatum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
* @author 一杯咖啡
* @desc 任务生产者
* @createTime
*/
public class QueueFeeder extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(QueueFeeder.class);

    private FetchQueue queue;
    private AbstractDBManager abstractDbManager;
    private AbstractGenerator abstractGenerator;
    private FetchItem fetchItem;
    private GeneratorFilter generatorFilter = null;
    //queeue 大小最大值设置
    private int queueMaxSize;
    //feeder 的状态
    public volatile boolean FeederRunning = true;

    /**
     * desc:初始化feeder
     * @Return:
     **/
    public QueueFeeder(Fetcher fetcher, Integer size) {
        this.queue = fetcher.getFetchQueue();
        this.abstractDbManager = fetcher.getAbstractDbManager();
        this.queueMaxSize = size;
    }
    /**
     * desc:关闭管道添加工具
     * @Return: void
     **/
    public void stopFeeder() {
        //停止数据库提取工具
        try {
            LOG.info("【-------------关闭数据库提取工具-------------】");
            closeGenerator();
        } catch (Exception e) {
            LOG.error("stoping abstractGenerator exception");
        }
        FeederRunning = false;
        while (this.isAlive()) {
            try {
                TimeUnit.SECONDS.sleep(1);
                LOG.info("【-------------停止任务生产者提取工具-------------】");
            } catch (InterruptedException ex) {
                LOG.error("关闭任务生产者线程休眠停止异常");
            }
        }
    }

    /**
     * desc:关闭任务生成工具
     *
     * @Return: void
     **/
    public void closeGenerator() throws Exception {
        if (abstractGenerator != null) {
            abstractGenerator.close();
            LOG.info("close abstractGenerator:" + abstractGenerator.getClass().getName() + " ......");
        }
    }
    /**
     * desc: feeder 线程运行
     * @Return:
     **/
    @Override
    public void run() {
        //获取任务生成工具 （从数据库中提取数据）
        abstractGenerator = abstractDbManager.getAbstractGenerator();
        LOG.info(abstractGenerator.toString());
        //abstractGenerator.setFilter(new StatusGeneratorFilter());
        //LOG.info("create abstractGenerator:" + abstractGenerator.getClass().getName());
        boolean hasMore = true;//判断任务来源中是否存在任务 redis
        FeederRunning = true;
        while (hasMore && FeederRunning) {
            //监听queue中数量，当queue中数量为1000时，线程等待，
            int feed = queueMaxSize - queue.getSize();
            if (feed <= 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                continue;
            }
            //如果queue中小于1000，往queue中添加新任务
            while (feed > 0 && hasMore && FeederRunning) {
                //任务生成器 如果下一个任务为空，返回空。判断dbmananger中是否有后续任务
                CrawlDatum datum = abstractGenerator.next();
                hasMore = (datum != null);
                if (hasMore) {
                    fetchItem = new FetchItem();
                    fetchItem.setDatum(datum);
                    queue.addFetchItem(fetchItem);
                    feed--;//一直填到queue为1000
                }
            }
        }
    }

    public AbstractGenerator getAbstractGenerator() {
        return abstractGenerator;
    }
}

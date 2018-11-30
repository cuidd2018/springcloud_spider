package spider.spiderCore.fetcher;


import spider.spiderCore.crawldb.AbstractDBManager;
import spider.spiderCore.crawldb.AbstractGenerator;
import spider.spiderCore.crawldb.Idbutil.GeneratorFilter;
import spider.spiderCore.entities.CrawlDatum;
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
    private GeneratorFilter generatorFilter = null;
    //queeue 大小最大值设置
    private int queueMaxSize;
    //feeder 的状态
    public volatile boolean feederRunning = true;

    /**
     * desc:初始化feeder
     **/
    public QueueFeeder(Fetcher fetcher, Integer size) {
        this.queue = fetcher.getFetchQueue();
        this.abstractDbManager = fetcher.getAbstractDbManager();
        this.queueMaxSize = size;
    }

    /**
     * desc:关闭管道添加工具
     **/
    public void stopFeeder() {
        //停止数据库提取工具
        try {
            LOG.info("【正在关闭数据库提取工具】");
            closeGenerator();
        } catch (Exception e) {
            LOG.error("【关闭数据库提取工具出错】");
        }
        feederRunning = false;
        while (this.isAlive()) {
            try {
                TimeUnit.SECONDS.sleep(1);
                LOG.info("【停止任务生产者】");
            } catch (InterruptedException ex) {
                LOG.error("【关闭任务生产者线程-前- 休眠出错】");
            }
        }
    }

    /**
     * desc:关闭任务生成工具
     **/
    public void closeGenerator() throws Exception {
        if (abstractGenerator != null) {
            abstractGenerator.close();
            LOG.info("close abstractGenerator:" + abstractGenerator.getClass().getName() + " ......");
        }
    }

    /**
     * desc: feeder 线程运行
     **/
    @Override
    public void run() {
        //获取任务生成工具 （从数据库中提取数据）
        abstractGenerator = abstractDbManager.getAbstractGenerator();
        LOG.info(abstractGenerator.toString());
        //abstractGenerator.setFilter(new StatusGeneratorFilter());
        //LOG.info("create abstractGenerator:" + abstractGenerator.getClass().getName());
        //判断任务来源中是否存在任务 redis
        boolean hasMore = true;
        feederRunning = true;

        while (hasMore && feederRunning) {
            //监听queue中数量，当queue中数量为1000时，线程等待，
            int feed = queueMaxSize - queue.getSize();
            if (feed <= 0) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ex) {
                }
                continue;
            }
            //如果queue中小于1000，往queue中添加新任务，未提取到任务 count 等待时间
            int count = 0;
            while (feed > 0 && hasMore && feederRunning) {
                //任务生成器 如果下一个任务为空，返回空。判断dbmananger中是否有后续任务
                CrawlDatum datum = abstractGenerator.next();
                if (datum != null) {
                    queue.addCrawlDatum(datum);
                    feed--;//一直填到queue为1000
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        count++;
                        if (count < 5) {
                            LOG.info("redis 中无后续任务，等待第 "+count+" 秒");
                        } else {
                            LOG.info("等待超时，关闭程序");
                            feederRunning = false;
                            hasMore = false;
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    public AbstractGenerator getAbstractGenerator() {
        return abstractGenerator;
    }
}

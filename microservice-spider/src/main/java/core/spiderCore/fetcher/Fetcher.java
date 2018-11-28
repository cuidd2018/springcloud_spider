package core.spiderCore.fetcher;

import core.spiderCore.crawldb.AbstractDBManager;
import core.spiderCore.fetcher.IFetcherTools.Executor;
import core.spiderCore.fetcher.IFetcherTools.NextFilter;
import core.spiderCore.spiderConfig.DefaultConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
* @author 一杯咖啡
* @desc 调度器，生产者，任务管道，消费者调度器
* @createTime
*/
public class Fetcher extends DefaultConfigImp {

    private static final Logger LOG = LoggerFactory.getLogger(Fetcher.class);

    /**
     * 核心组件
     * fetchQueue:任务管道
     * queueFeeder 任务生产者
     */
    private FetchQueue fetchQueue;
    private QueueFeeder queueFeeder;
    /**
     * 外部注入
     */
    private AbstractDBManager abstractDbManager;
    private Executor executor;
    private NextFilter nextFilter = null;
    /**
     * 线程状态属性
     * activeThreads 活动线程
     * startedThreads 已启动线程
     * spinWaiting 等待任务线程
     * lastRequestStart 请求持续时间
     */
    private AtomicInteger activeThreads;
    private AtomicInteger startedThreads;
    private AtomicInteger spinWaiting;
    private AtomicLong lastRequestStart;
    public static final int FETCH_SUCCESS = 1;
    public static final int FETCH_FAILED = 2;
    /**
     * 线程状态属性
     * threads 线程数量
     * fetcherRuning 调度器状态
     */
    private int threads = 20;
    private volatile boolean fetcherRuning;
    //private boolean isContentStored = false;

    //初始化fetcher
    public Fetcher(AbstractDBManager abstractDbManager, Executor executor, NextFilter nextFilter) {
        this.abstractDbManager = abstractDbManager;
        this.executor = executor;
        this.nextFilter = nextFilter;
    }

    /**
     * 抓取当前所有任务，会阻塞到爬取完成 开启 feeder 和 执行爬取线程。
     *
     * @throws IOException 异常
     */
    public Integer fetcherStart() throws Exception {
        if (executor == null) {
            LOG.info("Please Specify A Executor!");
            return 0;
        }
        //合并任务库
        abstractDbManager.merge();
        try {
            abstractDbManager.initSegmentWriter();
            LOG.info("init segmentWriter:" + abstractDbManager.getClass().getName());

            fetcherRuning = true;
            lastRequestStart = new AtomicLong(System.currentTimeMillis());
            activeThreads = new AtomicInteger(0);
            startedThreads = new AtomicInteger(0);
            spinWaiting = new AtomicInteger(0);
            //初始化任务管道
            fetchQueue = new FetchQueue();
            //开启从Dbmanager中抽取任务添加到fetchQueue中，generator作任务状态过滤，添加上限1000个
            queueFeeder = new QueueFeeder(this, 1000);
            queueFeeder.start();

            //初始化管道消费者 从queue中读取任务
            FetcherThread[] fetcherThreads = new FetcherThread[threads];
            for (int i = 0; i < threads; i++) {
                fetcherThreads[i] = new FetcherThread(this);
                fetcherThreads[i].start();
            }
            //主线程循环 每秒打印一次状态；fetcher 的状态，如果为false 则开始停止爬虫
            do {
                pause(1,0 );
                LOG.info("-activeThreads=" + activeThreads.get()
                        + ", spinWaiting=" + spinWaiting.get() + ", fetchQueue.size="
                        + fetchQueue.getSize());
                if (!queueFeeder.isAlive() && fetchQueue.getSize() < 5) {
                    fetchQueue.dump();
                }

                if ((System.currentTimeMillis() - lastRequestStart.get()) > getConfig().getThreadKiller()) {
                    LOG.info("Aborting with " + activeThreads + " hung threads.");
                    break;
                }
                /*pay addention*/
                //LOG.info("fetcher 运行状态："+fetcherRuning+"------------------------");
            } while (fetcherRuning && (startedThreads.get() != threads || activeThreads.get() > 0));

            //立即停止任务添加到管道
            queueFeeder.stopFeeder();
            //fetcherRuning = false;
            long waitThreadEndStartTime = System.currentTimeMillis();
            if (activeThreads.get() > 0) {
                LOG.info("wait for activeThreads to end");
            }
            /*判断活动线程数量 清理所有活动线程*/
            while (activeThreads.get() > 0) {
                LOG.info("-activeThreads=" + activeThreads.get());
                pause(0,500);
                //如果当前等待时间超过设置的线程停止时间，强制停止所有线程
                if (System.currentTimeMillis() - waitThreadEndStartTime > getConfig().getWaitThreadEndTime()) {
                    LOG.info("kill threads");
                    for (int i = 0; i < fetcherThreads.length; i++) {
                        if (fetcherThreads[i].isAlive()) {
                            try {
                               // fetcherRuning = false;
                                fetcherThreads[i].stop();
                                LOG.info("kill thread " + i);
                            } catch (Exception ex) {
                                LOG.info("Exception", ex);
                            }
                        }
                    }
                    break;
                }
            }
            LOG.info("clear all activeThread");
            //停止 任务执行线程后清空管道
            fetchQueue.clearQueue();//清空管道 redis 可以考虑重新将未抓取的url存回redis中
        } finally {
            if (queueFeeder != null) {
                queueFeeder.closeGenerator();
            }
            abstractDbManager.closeSegmentWriter();
            LOG.info("close segmentWriter:" + abstractDbManager.getClass().getName());
        }
        //返回生成的任务总数
        return queueFeeder.getAbstractGenerator().getTotalGenerate();
    }

    /**
     * 停止爬取
     */
    public void stopFetcher() {
        //停止任务添加工具
        LOG.info("【----------停止任务生产者----------】");
        queueFeeder.stopFeeder();
        //停止调度器
        fetcherRuning = false;
    }


    public FetchQueue getFetchQueue() {
        return fetchQueue;
    }

    public QueueFeeder getQueueFeeder() {
        return queueFeeder;
    }

    public AbstractDBManager getAbstractDbManager() {
        return abstractDbManager;
    }

    public void setAbstractDbManager(AbstractDBManager abstractDbManager) {
        this.abstractDbManager = abstractDbManager;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public NextFilter getNextFilter() {
        return nextFilter;
    }

    public void setNextFilter(NextFilter nextFilter) {
        this.nextFilter = nextFilter;
    }

    public AtomicInteger getActiveThreads() {
        return activeThreads;
    }

    public AtomicInteger getStartedThreads() {
        return startedThreads;
    }

    public AtomicInteger getSpinWaiting() {
        return spinWaiting;
    }

    public AtomicLong getLastRequestStart() {
        return lastRequestStart;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public boolean isFetcherRuning() {
        return fetcherRuning;
    }

    /**
     * desc: 线程休眠
     **/
    public void pause(int second,int mills){
        try {
            TimeUnit.SECONDS.sleep(second);
            TimeUnit.MILLISECONDS.sleep(mills);
        } catch (InterruptedException e) {
            LOG.error("Fetcher thread sleep exception");
        }
    }
}

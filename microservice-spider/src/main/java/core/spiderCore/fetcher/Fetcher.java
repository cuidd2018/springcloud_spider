package core.spiderCore.fetcher;

import core.spiderCore.crawldb.AbstractDBManager;
import core.spiderCore.fetcher.IFetcherTools.Executor;
import core.spiderCore.fetcher.IFetcherTools.NextFilter;
import core.spiderCore.spiderConfig.DefaultConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;
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
     * threads 线程数量
     * fetcherRuning 调度器状态
     */
    private int threads = 20;
    private volatile boolean fetcherRunning;
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
            LOG.info("未提供任务执行器");
            return 0;
        }
        //合并 入口和解析 任务库到 运行任务库
        abstractDbManager.merge();
        try {
            abstractDbManager.initSegmentWriter();
            LOG.info("初始化解析任务存储工具" + abstractDbManager.getClass().getName());
            fetcherRunning = true;
            //初始化任务管道
            fetchQueue = new FetchQueue();
            //开启从Dbmanager中抽取任务添加到fetchQueue中，generator作任务状态过滤，添加上限1000个
            queueFeeder = new QueueFeeder(this, 1000);
            queueFeeder.start();

            //创建线程池，允许核心线程超时关闭
            ThreadPoolExecutor threadsExecutor = new ThreadPoolExecutor(30, 45, 2, TimeUnit.SECONDS  , new LinkedBlockingQueue<>(10));
            threadsExecutor.allowCoreThreadTimeOut(true);
            //初始化消费者 从queue中读取任务
            for (int i = 0; i < threads; i++) {
                threadsExecutor.execute(new FetcherThread(this));
            }

            do {
                pause(1,0 );
                LOG.info("【线程池状态：\n"+threadsExecutor.toString()+" 】\n");
            }while (threadsExecutor.getActiveCount() > 0 && fetcherRunning);

            //立即停止任务添加到管道
            LOG.info("本地管道数量："+fetchQueue.getSize());
            this.stopFetcher();
            threadsExecutor.shutdown();
            LOG.info("线程池状态？？---"+threadsExecutor.toString());
            LOG.info("线程池关闭？？---"+threadsExecutor.isTerminated());
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
        fetcherRunning = false;
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

    public Executor getExecutor() {
        return executor;
    }

    public NextFilter getNextFilter() {
        return nextFilter;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public boolean isFetcherRuning() {
        return fetcherRunning;
    }

    /**
     * desc: 线程休眠
     **/
    public void pause(int second,int mills){
        try {
            TimeUnit.SECONDS.sleep(second);
            TimeUnit.MILLISECONDS.sleep(mills);
        } catch (InterruptedException e) {
            LOG.error("调度器休眠出错");
        }
    }
}

package spider.spiderCore.crawler;

import spider.spiderCore.crawldb.AbstractDBManager;
import spider.spiderCore.entities.CrawlDatum;
import spider.spiderCore.entities.CrawlDatums;
import spider.spiderCore.fetcher.Fetcher;
import spider.spiderCore.fetcher.IFetcherTools.Executor;
import spider.spiderCore.fetcher.IFetcherTools.NextFilter;
import spider.spiderCore.spiderConfig.DefaultConfigImp;
import spider.spiderCore.spiderConfig.configUtil.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * desc:单层爬虫
 **/
public class Crawler extends DefaultConfigImp {

    private static final Logger LOG = LoggerFactory.getLogger(Crawler.class);

    public Crawler() { }

    private int status;

    public final static int RUNNING = 1;
    public final static int STOPED = 2;
    protected boolean resumable = false;
    protected int threads = 50;


    protected CrawlDatums seeds = new CrawlDatums();
    protected CrawlDatums forcedSeeds = new CrawlDatums();
    protected Fetcher fetcher;

    protected Executor executor = null;
    protected NextFilter nextFilter = null;
    protected AbstractDBManager abstractDbManager;
    //protected GeneratorFilter generatorFilter = new StatusGeneratorFilter();
    protected void inject() throws Exception {
        abstractDbManager.inject(seeds);
    }

    protected void injectForcedSeeds() throws Exception {
        abstractDbManager.inject(forcedSeeds, true);
    }


    protected void registerOtherConfigurations(){
    }
    
    /**
     * 开始爬取，迭代次数为depth
     *
     * @param depth 迭代次数
     * @throws Exception 异常
     */
    public void start(int depth) throws Exception {
        LOG.info("配置信息："+this.configuration.toString());
        LOG.info(this.toString());
        //register dbmanager conf
        ConfigurationUtils.setTo(this, abstractDbManager, executor, nextFilter);
        registerOtherConfigurations();

        if (!resumable) {
            if (abstractDbManager.isDBExists()) {
                abstractDbManager.clear();
            }
            if (seeds.isEmpty() && forcedSeeds.isEmpty()) {
                LOG.error("error:Please add at least one seed");
                return;
            }
        }
        abstractDbManager.open();

        if (!seeds.isEmpty()) {
            inject();
        }
        if (!forcedSeeds.isEmpty()) {
            injectForcedSeeds();
        }
        status = RUNNING;
        for (int i = 0; i < depth; i++) {
            if (status == STOPED) {
                break;
            }
            LOG.info("start depth " + (i + 1));
            long startTime = System.currentTimeMillis();
            fetcher = new Fetcher(abstractDbManager,executor,nextFilter);
            //register fetcher conf
            ConfigurationUtils.setTo(this,fetcher);

            fetcher.setThreads(threads);
            int totalGenerate = fetcher.fetcherStart();

            long endTime = System.currentTimeMillis();
            long costTime = (endTime - startTime) / 1000;

            LOG.info("\ndepth " + (i + 1) +" finish: " +
                     "\n              this depth total urls: " + totalGenerate + "" +
                     "\n              this depth total time: " + costTime + " seconds");
            if (totalGenerate == 0) {
                break;
            }
        }
        abstractDbManager.close();
        afterStop();
    }

    public void afterStop(){ }

    /**
     * 停止爬虫
     */
    public void stop() {
        status = STOPED;
        fetcher.stopFetcher();
    }

    /**
     * 添加种子任务
     *
     * @param datum 种子任务
     * @param force 如果添加的种子是已爬取的任务，当force为true时，会强制注入种子，当force为false时，会忽略该种子
     */
    public void addSeed(CrawlDatum datum, boolean force) {
        addSeedAndReturn(datum,force);
    }

    /**
     * 等同于 addSeed(datum, false)
     *
     * @param datum 种子任务
     */
    public void addSeed(CrawlDatum datum) {
        addSeedAndReturn(datum);
    }
    /**
     * 添加种子集合
     *
     * @param datums 种子集合
     * @param force 如果添加的种子是已爬取的任务，当force为true时，会强制注入种子，当force为false时，会忽略该种子
     */
    public void addSeed(CrawlDatums datums, boolean force) {
        addSeedAndReturn(datums,force);
    }

    /**
     * 等同于 addSeed(datums,false)
     *
     * @param datums 种子任务集合
     */
    public void addSeed(CrawlDatums datums) {
        addSeedAndReturn(datums);
    }


    /**
     * 与addSeed(CrawlDatums datums, boolean force) 类似
     *
     * @param links 种子URL集合
     * @param force 是否强制注入
     */
    public void addSeed(Iterable<String> links, boolean force) {
        addSeedAndReturn(links,force);
    }


    /**
     * 与addSeed(CrawlDatums datums)类似
     *
     * @param links 种子URL集合
     */
    public void addSeed(Iterable<String> links) {
        addSeedAndReturn(links);
    }

    /**
     * 与addSeed(CrawlDatum datum, boolean force)类似
     *
     * @param url 种子URL
     * @param force 是否强制注入
     */
    public void addSeed(String url, boolean force) {
        addSeedAndReturn(url,force);
    }

    /**
     * 与addSeed(CrawlDatum datum)类似
     *
     * @param url 种子URL
     */
    public void addSeed(String url) {
        addSeedAndReturn(url);
    }

    public CrawlDatum addSeedAndReturn(CrawlDatum datum, boolean force) {
        if (force) {
            forcedSeeds.add(datum);
        } else {
            seeds.add(datum);
        }
        return datum;
    }

    public CrawlDatum addSeedAndReturn(CrawlDatum datum) {
        return addSeedAndReturn(datum,false);
    }

    public CrawlDatum addSeedAndReturn(String url, boolean force) {
        CrawlDatum datum = new CrawlDatum(url);
        return addSeedAndReturn(datum,force);
    }

    public CrawlDatum addSeedAndReturn(String url) {
        return addSeedAndReturn(url,false);
    }

    public CrawlDatums addSeedAndReturn(Iterable<String> links, boolean force) {
        CrawlDatums datums = new CrawlDatums(links);
        return addSeedAndReturn(datums,force);
    }

    public CrawlDatums addSeedAndReturn(Iterable<String> links) {
        return addSeedAndReturn(links,false);
    }

    public CrawlDatums addSeedAndReturn(CrawlDatums datums, boolean force) {
        if (force) {
            forcedSeeds.add(datums);
        } else {
            seeds.add(datums);
        }
        return datums;
    }

    public CrawlDatums addSeedAndReturn(CrawlDatums datums) {
        return addSeedAndReturn(datums,false);
    }

    /**
     * 返回是否断点爬取
     *
     * @return 是否断点爬取
     */
    public boolean isResumable() {
        return resumable;
    }

    /**
     * 设置是否断点爬取
     *
     * @param resumable 是否断点爬取
     */
    public void setResumable(boolean resumable) {
        this.resumable = resumable;
    }

    /**
     * 返回线程数
     *
     * @return 线程数
     */
    public int getThreads() {
        return threads;
    }

    /**
     * 设置线程数
     *
     * @param threads 线程数
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    /**
     * 获取每个爬取任务的最大执行次数
     *
     * @return 每个爬取任务的最大执行次数
     */
    public Executor getExecutor() {
        return executor;
    }

    /**
     * 设置执行器
     *
     * @param executor 执行器
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }




    public NextFilter getNextFilter() {
        return nextFilter;
    }

    public void setNextFilter(NextFilter nextFilter) {
        this.nextFilter = nextFilter;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setAbstractDbManager(AbstractDBManager abstractDbManager) {
        this.abstractDbManager = abstractDbManager;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Crawler Details:\n")
                .append("Crawler: ").append(getClass()).append("\n")
                .append("Executor: ").append(executor.getClass()).append("\n")
                .append("AbstractDBManager: ").append(abstractDbManager.getClass()).append("\n")
                .append("NextFilter: ");
        if (nextFilter == null) {
            sb.append("null");
        } else {
            sb.append(nextFilter.getClass());
        }
        return sb.toString();
    }
}

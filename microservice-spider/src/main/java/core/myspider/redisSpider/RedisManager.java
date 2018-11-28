package core.myspider.redisSpider;

import core.myspider.spiderTools.SerializeUtil;
import core.spiderCore.crawldb.AbstractDBManager;
import core.spiderCore.entities.CrawlDatum;
import core.spiderCore.entities.CrawlDatums;
import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * desc:连接操作redis类
 * @author 一杯咖啡
 * */
@Component
public class RedisManager extends AbstractDBManager<String> {
    private static final Logger LOG = Logger.getLogger(RedisManager.class);

    //private DataBase redisDb;
    private RedisTemplate redisTemplate;
    private SerializeUtil serializeUtil;

    public RedisManager(RedisDb redisDb, RedisGenerator generator) {
        super(generator, redisDb);
        this.serializeUtil = generator.getSerializeUtil();
        this.redisTemplate = generator.getRedisTemplate();
    }

    @Override
    public boolean isDBExists() {
        if (redisTemplate != null) {
            return true;
        }
        return false;
    }

    @Override
    public void clear() throws Exception {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.flushDb();
            LOG.info("启动清空以前的任务数据库");
            return "ok";
        });
    }

    @Override
    public void open() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    /**
     * desc: 任务强制注入
     **/
    @Override
    public void inject(CrawlDatum datum, boolean force) throws Exception {
        if (!isDBExists()) {
            throw new Exception("redis 数据库无效");
        } else {
            clear();
            String taskString = serializeUtil.serializeToString(datum);
            LOG.info("任务入口注入 ： " + datum.url());
            //注入任务到入口库
            String seeds = (String) getDataBase().getCrawlDB();
            //LOG.info("入口数据库 :" + seeds);
            redisTemplate.opsForList().rightPush(seeds, taskString);

        }
    }

    @Override
    public void inject(CrawlDatums datums, boolean force) throws Exception {
        for (CrawlDatum x : datums) {
            inject(x, force);
        }
    }
    /**
     * desc: 合并入口数据库
     **/
    @Override
    public void merge() throws Exception {
        String seeds = (String) getDataBase().getCrawlDB();
        String parse = (String) getDataBase().getFetchDB();
        while (redisTemplate.opsForList().size(seeds) > 0) {
            String seedStr = (String) redisTemplate.opsForList().leftPop(seeds);
            redisTemplate.opsForList().rightPush(parse, seedStr);
        }
    }
    /**
     * desc: 初始化写入数据库工具
     **/
    @Override
    public void initSegmentWriter() throws Exception {
     //   this.getAbstractGenerator().initGenerator( getConfig().getTopN(), getConfig().getMaxExecuteCount());
    }

    /**
     * desc: 写入已完成抓取的任务 可写入特定的数据库
     **/
    @Override
    public void writeFetchSegment(CrawlDatum fetchDatum) throws Exception {
        String taskString = serializeUtil.serializeToString(fetchDatum);
        // LOG.info(fetchDatum.url());
        redisTemplate.opsForList().rightPush((String) getDataBase().getLinkDB(), taskString);
    }

    /**
     * desc: 写入接下来的任务到任务数据库
     **/
    @Override
    public void writeParseSegment(CrawlDatums parseDatums) throws Exception {
        for (CrawlDatum task : parseDatums) {
            String nextTask = serializeUtil.serializeToString(task);
            //LOG.info(task.url());
            redisTemplate.opsForList().rightPush((String) getDataBase().getFetchDB(), nextTask);
        }
    }

    /**
     * desc: 关闭写入工具
     **/
    @Override
    public void closeSegmentWriter() throws Exception {

    }
}

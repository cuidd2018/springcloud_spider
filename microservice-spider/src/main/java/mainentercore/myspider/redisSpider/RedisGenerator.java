package mainentercore.myspider.redisSpider;


import mainentercore.myspider.spiderTools.SerializeUtil;
import mainentercore.spiderCore.crawldb.AbstractGenerator;
import mainentercore.spiderCore.crawldb.Idbutil.GeneratorFilter;
import mainentercore.spiderCore.entities.CrawlDatum;
import mainentercore.spiderCore.spiderConfig.DefaultConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 一杯咖啡
 */
@Component
public class RedisGenerator extends AbstractGenerator<String> {
    private static Logger LOG = LoggerFactory.getLogger(DefaultConfigImp.class);

    @Autowired
    private SerializeUtil serializeUtil;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public RedisGenerator(GeneratorFilter filter) {
        setFilter(filter);
    }

    /**
     * desc: 数据库提取任务
     **/
    @Override
    public CrawlDatum nextWithoutFilter() throws Exception {
        String datumString;
        CrawlDatum datum = null;
        String parse = getDataBase().getFetchDB();
        datumString = redisTemplate.opsForList().leftPop(parse);
        //LOG.info("解析数据库任务提取 : " + datumstr);
        if (datumString != null) {
            datum = (CrawlDatum) serializeUtil.deserializeToObject(datumString);
        }
        return datum;
    }

    @Override
    public void close() throws Exception {
    }

    public SerializeUtil getSerializeUtil() {
        return serializeUtil;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }
}

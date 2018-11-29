/*
package mainentercore.myspider.DbUtils;

import mainentercore.myspider.dao.SitesConfigDao;
import entity.SiteConfig;
import mainentercore.myspider.spiderTools.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

*/
/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${MysqlToRedis} </p>
 * <p>描述: [读取mysql 网站配置信息到redis队列] </p>
 **//*

@Component
public class ConfigFromMysqlToRedis {

    @Autowired private RedisTemplate redisTemplate;
    @Autowired private SitesConfigDao sitesConfigDao;
    @Autowired private SerializeUtil serializeUtil;

    public void MysqlWirteRedis() {
        */
/**
         * desc:主节点需要该功能从mysql数据库读到redis队列
         **//*

        List<SiteConfig> scs = sitesConfigDao.Read();
        String str;
        for (SiteConfig x : scs) {
            try {
                str = serializeUtil.serializeToString(x);
                redisTemplate.opsForList().leftPush("sites", str);
                //jedis.lpush("sites", str);
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
*/

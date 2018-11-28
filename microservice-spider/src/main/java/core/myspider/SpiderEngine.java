package core.myspider;


import core.myspider.DbUtils.ConfigFromMysqlToRedis;
import core.myspider.DbUtils.DataStoreTool;
import core.myspider.entity.SiteConfig;
import core.myspider.redisSpider.RedisManager;
import core.myspider.spiderComponent.MyParesContent;
import core.myspider.spiderComponent.MyRequester;
import core.myspider.spiderTools.ParesUtil;
import core.myspider.spiderTools.SerializeUtil;
import core.spiderCore.spiderConfig.configUtil.ConfigurationUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author 一杯咖啡
 */
@Component
public class SpiderEngine {
    private static final Logger LOG = Logger.getLogger(SpiderEngine.class.getSimpleName());
    /**
     * 数据存储组件
     **/
     @Autowired
     private RedisManager redisManager;
    // @Autowired
    //private RamDBManager ramDBManager;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ConfigFromMysqlToRedis configFromMysqlToRedis;
    /**
     * 爬虫调度执行组件
     **/
    @Autowired
    private ParesUtil paresUtil;
    @Autowired
    private MyRequester myRequester;
    @Autowired
    private DataStoreTool dataStoreTool;
    @Autowired
    private MyParesContent paresContent;
    @Autowired
    private SerializeUtil serializeUtil;

    /**
     * desc: 初始化爬虫
     **/
    public void initToRun() {
        LOG.info("加载mysql网站配置信息----------->>");
        configFromMysqlToRedis.MysqlWirteRedis();
        //读取redis队列任务，并开始抓取；阻塞直到能取出值
        String siteConfigString = "";
        try {
            while (true) {
                siteConfigString = (String) redisTemplate.opsForList().leftPop("sites");
                if ("".equals(siteConfigString) || null == siteConfigString) {
                    LOG.error("redis 网站配置数据为空");
                    pause(1, 0);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            LOG.error("读取redis队列失败" + e.getCause());
        }
        try {
            //获取序列化的字符串 生成siteConfig对象
            Object scObject = serializeUtil.deserializeToObject(siteConfigString);
            SiteConfig siteConfig = (SiteConfig) scObject;
            LOG.info("【" + siteConfig.getSiteName() + "】爬虫装载中------------->>>");
            /**
             * DataToDB 数据持久化组件 param(tableName)
             * paresUtil 网页解析组件 param(siteConfig,dataToDB)
             * visitor 网页解析器
             * mySpider 爬虫组合APP
             * abstractDBmanager 数据库管理组件
             */
            dataStoreTool.initStore(siteConfig.getTableName());
            paresUtil.initParesUitl(siteConfig, dataStoreTool);
            paresContent.MyParesContent(paresUtil);
            MySpider mySpider = new MySpider();
           // ConfigurationUtils.setTo(mySpider, ramDBManager.getAbstractGenerator());
            ConfigurationUtils.setTo(mySpider, paresContent,redisManager);
            mySpider.setAbstractDbManager(redisManager);
            mySpider.initMySpider(siteConfig, paresContent, myRequester, paresUtil);
            mySpider.getConfig().setTopN(10000);
            LOG.info(this.toString());

            //10秒自动关闭爬虫
           /* new Thread(() -> {
                pause(10, 0);
                mySpider.stop();
            }, "关闭线程").start();*/
            mySpider.startFetcher(mySpider);
        } catch (Exception e) {
            LOG.error("初始化爬虫异常: " + e.getCause() + ";messages:" + e.getMessage());
        }
    }

    /**
     * desc:线程休眠
     **/
    public void pause(Integer second, long mills) {
        try {
            LOG.warn("停止任务线程休眠" + second + "秒------------");
            TimeUnit.SECONDS.sleep(second);
            TimeUnit.MILLISECONDS.sleep(mills);
        } catch (InterruptedException e) {
            LOG.error("线程休眠异常");
        }
    }

    @Override
    public String toString() {
        return "\nMySpiderEngine{" +
                "\n  ramDBManager : " + redisManager.getClass().getName() +
                "\n  redisTemplate : " + redisTemplate.getClass().getName() +
                "\n  siteConfigCache : " + configFromMysqlToRedis.getClass().getName() +
                "\n  myRequester : " + myRequester.getClass().getName() +
                "\n  paresContent=" + paresContent.toString() +
                '}';
    }
}

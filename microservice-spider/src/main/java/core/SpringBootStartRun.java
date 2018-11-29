package core;


import core.myspider.SpiderEngine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author 一杯咖啡
 * @desc 爬虫启动类 （springboot 启动后，启动爬虫；检查环境）
 * @createTime ${YEAR}-${MONTH}-${DAY}-${TIME}
 */
@Configuration
public class SpringBootStartRun implements ApplicationRunner {
    private static final Logger LOG = Logger.getLogger(SpringBootStartRun.class);

    @Autowired
    private SpiderEngine spiderEngine;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DataSource dataSource;

    /**
     * desc: 初始化配置监视，出异常直接退出
     **/
    @PostConstruct
    public void show() {
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        LOG.info("环境监视：\n");
        //获取redis连接，连接失败则退出程序
        try {
            redisTemplate.getConnectionFactory().getConnection();
        } catch (Exception e) {
            LOG.error("redis 连接失败");
            System.exit(0);
        }
        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            LOG.error("数据库连接失败");
            System.exit(0);
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //new TestSprider().testSpiderStart();
        //  demoTypeCrawler.startSpider();
        spiderEngine.initToRun();
        //new RedisSpider(redisManager,true).startSpider();
    }
}

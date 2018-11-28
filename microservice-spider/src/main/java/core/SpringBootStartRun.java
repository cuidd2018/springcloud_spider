package core;


import core.myspider.SpiderEngine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.PostConstruct;

@Configuration
public class SpringBootStartRun implements ApplicationRunner {
    private static final Logger LOG = Logger.getLogger(SpringBootStartRun.class);
    @Autowired
    private Environment environment;
    @Autowired
    private SpiderEngine spiderEngine;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * desc: 初始化配置监视，出异常直接退出
     **/
    @PostConstruct
    public void show() {
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        LOG.info("redisTemplate" +redisTemplate.toString());
        LOG.info(" environment properties : " +environment.getProperty("user.dir"));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //new TestSprider().testSpiderStart();
      //  demoTypeCrawler.startSpider();
        spiderEngine.initToRun();
        //new RedisSpider(redisManager,true).startSpider();
    }
}

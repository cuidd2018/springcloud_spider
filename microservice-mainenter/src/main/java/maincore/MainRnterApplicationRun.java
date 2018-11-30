package maincore;

import maincore.dao.ConfigFromMysqlToRedis;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author 一杯咖啡
 * @desc 入口启动类
 * @createTime 2018-11-28-17:14
 */
@SpringBootApplication
@Configuration
public class MainRnterApplicationRun implements CommandLineRunner {
    private static final Logger LOG = Logger.getLogger(MainRnterApplicationRun.class);
    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    private ConfigFromMysqlToRedis configFromMysqlToRedis;
   @Autowired private RedisTemplate redisTemplate;
    public static void main(String[] args) {
        SpringApplication.run(MainRnterApplicationRun.class, args);
    }

    @PostConstruct
    public void monitor(){
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
    }
    @Override
    public void run(String... args) {
        configFromMysqlToRedis.MysqlWirteRedis();
        // 加载网站配置信息后调用
        this.sendMessage();
    }
    public void sendMessage(){
        MyMessage message = new MyMessage();
        for (int i = 0; i < 2; i++) {
            jmsTemplate.send("SiteConfig", message);
        }
        LOG.info("开始发送消息"+message);
        try {
            LOG.info("等待2秒");
            TimeUnit.SECONDS.sleep(2);
            System.exit(0);
        } catch (InterruptedException e) {

        }
    }

    //获取消息被如果redis中无配置信息
   // @JmsListener(destination = "SiteConfig")
   /* public void receivedMessage(String message){
        LOG.info("接收到消息"+message);
        if (MessageConstant.START.getName().equals(message)){
            configFromMysqlToRedis.MysqlWirteRedis();
        }
    }*/
}

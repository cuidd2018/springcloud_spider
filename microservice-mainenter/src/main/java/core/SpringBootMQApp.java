package core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-11-28-17:14
 */
@SpringBootApplication
public class SpringBootMQApp implements CommandLineRunner {

    @Autowired
    JmsTemplate jmsTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMQApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            jmsTemplate.send("my-destination", new Msg());
            TimeUnit.SECONDS.sleep(2);
        }
    }
}

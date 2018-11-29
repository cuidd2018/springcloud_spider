package core;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-11-28-17:15
 */
@Component
public class Listener {
    @JmsListener(destination = "my-destination")
    public void receivedMessage(String message) {
        System.out.println("接受到" + message);
    }
}

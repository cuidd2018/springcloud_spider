package mainentercore.myspider.activemqlistener;

import org.apache.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-11-29-15:15
 */
public class ActiveMqListener {
    private static final Logger LOG = Logger.getLogger(ActiveMqListener.class);
    @JmsListener(destination = "SiteConfig")
    public String getMessage(TextMessage message){
        try {
            return message.getText();
        } catch (JMSException e) {
            LOG.error("读取TextMessage 出错 ");
            return null;
        }
    }
}

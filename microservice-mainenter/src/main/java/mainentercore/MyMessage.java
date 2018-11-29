package mainentercore;

import entity.MessageConstant;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-11-28-17:15
 */
public class MyMessage implements MessageCreator {
    @Override
    public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(MessageConstant.START.getName());
    }
}

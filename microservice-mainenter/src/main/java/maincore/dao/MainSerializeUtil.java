package maincore.dao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <p>项目名称: ${序列化} </p>
 * <p>文件名称: ${SerializeUtil} </p>
 * <p>描述: [对象序列化和反序列化对象] </p>
 **/
@Component
public class MainSerializeUtil {
    private static final Logger LOG = Logger.getLogger(MainSerializeUtil.class);

    /**
     * @Title：${序列化}
     * @Description: [序列化对象为字符串]
     * @author <a href="mail to: 113985238@qq.com" rel="nofollow">whitenoise</a>
     */
    public String serializeToString(Object obj) throws Exception{
        //LOG.info("对象序列化，存入redis");
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        //此处只能是ISO-8859-1,但是不会影响中文使用
        String str = byteOut.toString("ISO-8859-1");
        return str;
    }

    /**
     * @Title：${反序列化}
     * @Description: [反序列化字符串为对象]
     * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
     */
    public Object deserializeToObject(String str) throws Exception{
        //LOG.info("对象反序列化，进入程序");
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Object obj =objIn.readObject();
        return obj;
    }
}

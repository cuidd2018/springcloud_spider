package core.myspider.spiderTools;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * desc: 解析url 记录数据总量工具
 **/
public class ParesCounter {
    /**
     *  TotalData 总数
     *  Valid 有效数
     *  Invalid 无效
     */
    private AtomicInteger TotalData = new AtomicInteger(0);
    private AtomicInteger Valid = new AtomicInteger(0);
    private AtomicInteger Invalid = new AtomicInteger(0);

    @Override
    public String toString() {
        return  "\n         DataCounter{" +
                "\n         处理的链接总数 = " + TotalData +
                "\n         有效的链接数 = " + Valid +
                "\n         无效的链接数 = " + Invalid +" }";

    }

    public  AtomicInteger getTotalData() {
        return TotalData;
    }

    public  AtomicInteger getValid() {
        return Valid;
    }

    public  AtomicInteger getInvalid() {
        return Invalid;
    }
}

package spider.spiderCore.pares;

import spider.spiderCore.entities.CrawlDatums;
import spider.spiderCore.entities.Page;

/**
 * 实现该接口
 * desc: visitor 使用该工具解析正文，提取自定义需要的内容
 * @author 一杯咖啡
 * */
public interface ParesContent {
    /**
     * @Description: [页面内容提取]
     * @param page 当前页面
     * @param next 提取的url
     */
    void paresContent(Page page, CrawlDatums next);
}

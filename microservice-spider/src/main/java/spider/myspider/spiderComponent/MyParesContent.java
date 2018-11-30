package spider.myspider.spiderComponent;

import spider.myspider.DbUtils.IStore.Store;
import spider.myspider.entity.MyNew;
import entity.ParseContentRules;
import spider.myspider.spiderTools.*;
import spider.spiderCore.entities.CrawlDatums;
import spider.spiderCore.entities.Page;
import spider.spiderCore.pares.ParesContent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author 一杯咖啡
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MyParesContent implements ParesContent {
    private static final Logger LOG = Logger.getLogger(ParesUtil.class);
    private ParseContentRules parseContentRules;
    private ParesUtil paresUtil;
    private ParesCounter paresCounter;
    private Store dataStoreTool;
    private RulesSplitUtil rulesSplitUtil;
    private Selectors selectors;
    private TimeFilter timeFilter;

    public void MyParesContent(ParesUtil paresUtil) {
        this.paresUtil = paresUtil;
        //使用解析工具
        this.parseContentRules = paresUtil.getParseContentRules();
        paresCounter = paresUtil.getParesCounter();
        dataStoreTool = paresUtil.getDataStoreTool();
        rulesSplitUtil = paresUtil.getRulesSplitUtil();
        selectors = paresUtil.getSelectors();
        timeFilter = paresUtil.getTimeFilter();
    }

    @Override
    public String toString() {
        return "MyParesContent{" +
                "paresUtil=" + paresUtil.toString() +
                '}';
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [页面正文提取]
     */
    @Override
    public void paresContent(Page page, CrawlDatums next) {
        //有效连接数+1
        paresCounter.getTotalData().incrementAndGet();
        String title = page.doc().title();
        if (title.trim().equals("")) {
            title = selectors.IdClassSelect(page, rulesSplitUtil.splitRule(parseContentRules.getTitle_rule()));
        }
        //获取正文
        String content = selectors.detaliSelect(page, rulesSplitUtil.splitRule(parseContentRules.getContent_rule()));
        if (!content.trim().equals("")) {
            //正文不为空 获取 作者，媒体，时间
            String media = selectors.detaliSelect(page, rulesSplitUtil.splitRule(parseContentRules.getMedia_rule()));
            String author = selectors.detaliSelect(page, rulesSplitUtil.splitRule(parseContentRules.getAnthor_rule()));
            String time = selectors.detaliSelect(page, rulesSplitUtil.splitRule(parseContentRules.getTime_rule()));
            //截取指定长度字符串
            time = rulesSplitUtil.SubStr(timeFilter.getTimeByReg(time));
            media = rulesSplitUtil.SubStr(media);
            author = rulesSplitUtil.SubStr(author);
            String url = page.url();
            try {
                MyNew myNew = GenerateNews(title, url, content, media, author, time);
                dataStoreTool.insert(myNew);
                paresCounter.getValid().incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //正文为空
            paresCounter.getInvalid().incrementAndGet();
            LOG.warn("已过滤正文为空的新闻");
        }
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [生成MyNew对象]
     */
    public MyNew GenerateNews(String title, String url, String content, String media, String author, String time) {
        MyNew myNew = new MyNew();
        myNew.setTitle(title);
        myNew.setURL(url);
        myNew.setContent(content);
        myNew.setMedia(media);
        myNew.setAnthor(author);
        myNew.setTime(time);
        return myNew;
    }
}

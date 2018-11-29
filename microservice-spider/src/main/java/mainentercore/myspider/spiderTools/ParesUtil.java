package mainentercore.myspider.spiderTools;

import mainentercore.myspider.DbUtils.DataStoreTool;
import mainentercore.myspider.DbUtils.IStore.Store;
import mainentercore.myspider.entity.MyNew;
import entity.ParseContentRules;
import entity.SiteConfig;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author 一杯咖啡
 * @Title：${enclosing_method}
 * @Description: [页面正文提取工具 集合]
 */
@Component
public class ParesUtil {
    private static final Logger LOG = Logger.getLogger(ParesUtil.class);
    private Selectors selectors = new Selectors();
    private RulesSplitUtil rulesSplitUtil = new RulesSplitUtil();
    private ParesCounter paresCounter = new ParesCounter();
    private TimeFilter timeFilter = new TimeFilter();
    /**
     * DataToDB 数据持久化工具
     * contentRules 页面正文提取规则 原始字符串
     * contRules 解析后的正文提取规则
     */
    private Store store;
    private ParseContentRules parseContentRules;
    private String[] contRules;

    public ParesUtil(){}
    /**
     * 初始化 自定义页面规则
     * @param siteConfig 网站匹配规则
     */
    public void initParesUitl(SiteConfig siteConfig, DataStoreTool store){
        this.parseContentRules = siteConfig.getParseContentRules();
        this.contRules = rulesSplitUtil.splitRule(siteConfig.getPageParse());
        this.store = store;
    }
    public Selectors getSelectors() {
        return selectors;
    }

    public RulesSplitUtil getRulesSplitUtil() {
        return rulesSplitUtil;
    }

    public ParesCounter getParesCounter() {
        return paresCounter;
    }

    public TimeFilter getTimeFilter() {
        return timeFilter;
    }

    public Store<MyNew> getDataStoreTool() {
        return store;
    }

    public ParseContentRules getParseContentRules() {
        return parseContentRules;
    }

    public String[] getContRules() {
        return contRules;
    }

    @Override
    public String toString() {
        return "ParesUitl{" +
                "\n selectors=" + selectors.getClass().getName() +
                "\n rulesSplitUtil=" + rulesSplitUtil.getClass().getName() +
                "\n paresCounter=" + paresCounter.getClass().getName() +
                "\n timeFilter=" + timeFilter.getClass().getName() +
                "\n dataStoreTool=" + store.getClass().getName() +
                '}';
    }
}

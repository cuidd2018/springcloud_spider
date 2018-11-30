package spider.myspider;


import entity.SiteConfig;
import spider.myspider.spiderTools.ParesUtil;
import spider.myspider.spiderTools.RulesSplitUtil;
import spider.spiderCore.crawler.AbstractAutoParseCrawler;
import spider.spiderCore.entities.CrawlDatums;
import spider.spiderCore.entities.Page;
import spider.spiderCore.http.IRequestor.Requester;
import spider.spiderCore.pares.ParesContent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
* @author 一杯咖啡
* @desc 爬虫初始化类
* @createTime  ${YEAR}-${MONTH}-${DAY}-${TIME}
*/
@Component
public class MySpider extends AbstractAutoParseCrawler {

    private static final Logger LOG = Logger.getLogger(MySpider.class);
    /**
     * siteConfig 网站配置信息
     * paresUtil 页面解析辅助工具
     * paresContent 页面解析组件
     **/
    private SiteConfig siteconfig;
    private ParesUtil paresUtil;
    private ParesContent paresContent;
    /**
     * urlRules url 解析正则表达式
     * seeds 入口 url
     * conPickRules 正文提取正则表达式
     */
    private String[] urlRules;
    private String[] seeds;
    private String[] conPickRules;

    public MySpider() {
        //设置任务上限
        //this.configuration.setTopN(600);
        //设置线程数
        this.setThreads(50);
    }

    /**
     * @param siteConfig   网站配置信息
     * @param paresContent 自定义页面解析器
     * @param requester    自定义请求工具 需实现requestor接口
     * desc :初始化爬虫组件
     */
    public void initMySpider(SiteConfig siteConfig, ParesContent paresContent, Requester requester, ParesUtil paresUtil) {
        this.siteconfig = siteConfig;
        this.paresContent = paresContent;
        this.requester = requester;
        this.paresUtil = paresUtil;

        RulesSplitUtil rulesSplitUtil = paresUtil.getRulesSplitUtil();
        urlRules = rulesSplitUtil.splitRule(siteconfig.getUrlPares());
        seeds = rulesSplitUtil.splitRule(siteconfig.getSeeds());
        conPickRules = rulesSplitUtil.splitRule(siteconfig.getPageParse());
        configSpider(siteConfig);
    }

    /**
     * @param siteConfig   网站配置信息
     * desc :初始化爬虫属性
     */
    public void configSpider(SiteConfig siteConfig) {
        //设置爬虫入口
        this.addMyRegx();
        //设置断点爬取
        this.setResumable(siteconfig.isRes());
        //设置自动解析url
        this.setAutoParse(siteConfig.isAutoParse());
    }

    /**
     * desc:规则注入
     */
    public void addMyRegx() {
        //注入规则
        for (String str : seeds) {
            LOG.info("入口：" + str);
            this.addSeed(str, true);
        }
        for (String n : conPickRules) {
            LOG.info("正文提取规则注入:" + n);
            this.addRegex(n);
        }
        for (String u : urlRules) {
            LOG.info("url提取规则注入:" + u);
            this.addRegex(u);
        }
    }

    /**
     * desc: 初始化完成，开始爬虫
     */
    public void startFetcher(MySpider spider) {
        try {
            spider.start(siteconfig.getDeepPath());
        } catch (Exception e) {
            LOG.error("开启爬虫失败");
        }
    }

    /**
     * desc: 符合正文提取规则。调用自定义解析页面
     **/
    @Override
    public void visit(Page page, CrawlDatums next) {
        //匹配正文筛选规则 url
        for (String conRegx : conPickRules) {
            if (page.url().matches(conRegx)) {
                paresContent.paresContent(page, next);
            }
        }
    }

    /**
     *  desc: 爬虫完成后执行
     */
    @Override
    public void afterStop() {
        LOG.info(paresUtil.getParesCounter().toString());
        LOG.info("等待10秒 继续下一任务--------------------------");
            //System.exit(0);
    }
}

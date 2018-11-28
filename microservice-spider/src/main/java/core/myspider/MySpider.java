package core.myspider;


import core.myspider.entity.SiteConfig;
import core.myspider.spiderTools.ParesUtil;
import core.myspider.spiderTools.RulesSplitUtil;
import core.spiderCore.crawler.AbstractAutoParseCrawler;
import core.spiderCore.entities.CrawlDatums;
import core.spiderCore.entities.Page;
import core.spiderCore.http.IRequestor.Requester;
import core.spiderCore.pares.ParesContent;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
* @author 一杯咖啡
* @desc 小型分布式爬虫,爬虫初始化组件
* @createTime  ${YEAR}-${MONTH}-${DAY}-${TIME}
*/
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
        try {
            TimeUnit.SECONDS.sleep(10);
            //LOG.info("又开始抓取拉——————————————————");
            //this.startFetcher(this);
            //从mysql 中加载配置到redis中
           /* mysqlToRedis.MysqlWirteRedis();
            String objstr;
            //阻塞直到能取出值
            while (true) {
                objstr = (String) redisTemplate.opsForList().leftPop("sites");
                //objstr = js.lpop("sites");
                if ("".equals(objstr) || objstr == null) {
                    LOG.error("redis 列表为空");
                    Thread.sleep(5000);
                } else {
                    break;
                }
            }*/
            // Object o = serializeUtil.deserializeToObject(objstr);
            //  SiteConfig sc = (SiteConfig) o;
            // LOG.info(sc.getSiteName() + " 装载中+++++");
            //MySpider mySpider = new MySpider();
            //  mySpider.init(sc);
            //this.StartFetcher(this);
            //mySpider.StartFetcher(mySpider);
        } catch (Exception e) {
            LOG.error("再启动爬虫失败");
        }
    }

}

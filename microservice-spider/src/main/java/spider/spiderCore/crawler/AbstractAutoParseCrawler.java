package spider.spiderCore.crawler;

import spider.spiderCore.entities.CrawlDatum;
import spider.spiderCore.entities.CrawlDatums;
import spider.spiderCore.entities.Links;
import spider.spiderCore.entities.Page;
import spider.spiderCore.entities.entityUtil.RegexRule;
import spider.spiderCore.fetcher.IFetcherTools.Executor;
import spider.spiderCore.fetcher.IFetcherTools.Visitor;
import spider.spiderCore.http.IRequestor.Requester;
import spider.spiderCore.spiderConfig.configUtil.ConfigurationUtils;
import org.jsoup.nodes.Document;

/**
 * 自动爬取解析爬虫
 */
public abstract class AbstractAutoParseCrawler extends Crawler implements Executor,Visitor {

    /**
     * 是否自动抽取符合正则的链接并加入后续任务
     */
    protected boolean autoParse = true;
    protected Visitor visitor;
    protected Requester requester;

    public AbstractAutoParseCrawler() {
        this.executor = this;
        this.visitor = this;
    }

    @Override
    protected void registerOtherConfigurations() {
        super.registerOtherConfigurations();
        //当前对象配置，付给当前对象配置
        ConfigurationUtils.setTo(this, requester);
        ConfigurationUtils.setTo(this, visitor);
    }


    /**
     * URL正则约束
     */
    protected RegexRule regexRule = new RegexRule();

    /**
     * 页面解析器（页面urls提取）
     * @param datum 任务对象
     * @param next 当前任务对象提取出来的接下来的任务
     */
    @Override
    public void execute(CrawlDatum datum, CrawlDatums next) throws Exception {
        Page page = requester.getResponse(datum);
        //抽取当前页面符合条件的urls
        if (autoParse && !regexRule.isEmpty()) {
            parseLink(page, next);
        }
        //用户自定义页面解析 重写visit方法
        visitor.visit(page, next);
        afterParse(page, next);
    }

    protected void afterParse(Page page, CrawlDatums next) {
        //单个页面正文抽取，任务urls抽取完成之后，执行该函数
    }

    /**
     * @Desc: [获取网页中符合 规则的url]
     */
    protected void parseLink(Page page, CrawlDatums next) {
        String contentType = page.contentType();
        if (contentType != null && contentType.contains("text/html")) {
            Document doc = page.doc();
            if (doc != null) {
                //从页面中取出需要的url 形成接下来的任务
                Links links = new Links().addByRegex(doc, regexRule, getConfig().getAutoDetectImg());
                next.add(links);
            }
        }
    }

    /**
     * 添加URL正则约束
     * @param urlRegex URL正则约束
     */
    public void addRegex(String urlRegex) { regexRule.addRule(urlRegex); }

    /**
     * 是否自动抽取符合正则的链接并加入后续任务
     * @param autoParse 是否自动抽取符合正则的链接并加入后续任务
     */
    public void setAutoParse(boolean autoParse) { this.autoParse = autoParse; }
    public boolean isAutoParse() {
        return autoParse;
    }
    /**
     * 正则规则
     * @param regexRule 正则规则
     */
    public void setRegexRule(RegexRule regexRule) { this.regexRule = regexRule; }
    public RegexRule getRegexRule() { return regexRule; }
    /**
     * 获取requester
     * @return requester
     */
    public Requester getRequester() {
        return requester;
    }
    public void setRequester(Requester requester) {
        this.requester = requester;
    }
}

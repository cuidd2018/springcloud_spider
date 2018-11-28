package core.spiderCore.entities;

import core.spiderCore.entities.entityUtil.CrawlDatumFormater;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 爬取任务的数据结构
 */
public class CrawlDatum implements Serializable {

    /**
     * STATUS_DB_UNEXECUTED 未爬取
     * STATUS_DB_FAILED 爬取失败
     * STATUS_DB_SUCCESS 爬取成功
     */
    public final static int STATUS_DB_UNEXECUTED = 0;
    public final static int STATUS_DB_FAILED = 1;
    public final static int STATUS_DB_SUCCESS = 5;

    private String url = null;
    private long executeTime = System.currentTimeMillis();
    //private int httpCode = -1;

    private int status = STATUS_DB_UNEXECUTED;
    private int executeCount = 0;
    /**
     * 在WebCollector 2.5之后，不再根据URL去重，而是根据key去重
     * 可以通过getKey()方法获得CrawlDatum的key,如果key为null,getKey()方法会返回URL
     * 因此如果不设置key，爬虫会将URL当做key作为去重标准
     */
    public CrawlDatum() {
    }

    public CrawlDatum(String url) {
        this.url = url;
    }

    /**
     * 判断当前Page的URL是否和输入正则匹配
     *
     * @param urlRegex
     * @return
     */
    public boolean matchUrl(String urlRegex) {
        return Pattern.matches(urlRegex, url());
    }

    //执行计算自增
    public int incrExecuteCount(int count) {
        executeCount += count;
        return executeCount;
    }

    public String url() {
        return url;
    }

    public CrawlDatum url(String url) {
        this.url = url;
        return this;
    }
    /**
     * @deprecated 已废弃，使用url()代替
     */
    @Deprecated
    public String getUrl() {
        return url();
    }

    /**
     * @deprecated 使用url(String url)代替
     */
    @Deprecated
    public CrawlDatum setUrl(String url) {
        return url(url);
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long fetchTime) {
        this.executeTime = fetchTime;
    }

    public int getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(int executeCount) {
        this.executeCount = executeCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String briefInfo() {
        return String.format("CrawlDatum: (URL: %s)", url());
    }

    @Override
    public String toString() {
        return CrawlDatumFormater.datumToString(this);
    }

}

package core.spiderCore.fetcher;

import core.spiderCore.entities.CrawlDatum;

/**
* @author 一杯咖啡
* @desc 任务数据实体
* @createTime
*/
public class FetchItem {

    private CrawlDatum datum;

    public CrawlDatum getDatum() {
        return datum;
    }

    public void setDatum(CrawlDatum datum) {
        this.datum = datum;
    }

    public FetchItem() {
    }
}

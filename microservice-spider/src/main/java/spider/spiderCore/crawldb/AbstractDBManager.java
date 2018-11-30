package spider.spiderCore.crawldb;

import spider.spiderCore.crawldb.Idbutil.DataBase;
import spider.spiderCore.crawldb.Idbutil.Injector;
import spider.spiderCore.crawldb.Idbutil.SegmentWriter;
import spider.spiderCore.entities.CrawlDatum;
import spider.spiderCore.entities.CrawlDatums;
import spider.spiderCore.spiderConfig.DefaultConfigImp;

/**
* @author 一杯咖啡
* @desc 抽象数据库管理，Injector 注入入口，SegmentWriter 写入解析结果
* @createTime  ${YEAR}-${MONTH}-${DAY}-${TIME}
*/
public abstract class AbstractDBManager<T> extends DefaultConfigImp implements Injector, SegmentWriter {

    private AbstractGenerator abstractGenerator;
    private DataBase<T> dataBase;

    public AbstractDBManager(AbstractGenerator<T> abstractGenerator,DataBase<T> dataBase){
        abstractGenerator.setDataBase(dataBase);
        this.abstractGenerator = abstractGenerator;
        this.dataBase = dataBase;
        }
    public abstract boolean isDBExists();

    public abstract void clear() throws Exception;

    public abstract void open() throws Exception;

    public abstract void close() throws Exception;

    public abstract void inject(CrawlDatum datum, boolean force) throws Exception;

    public abstract void inject(CrawlDatums datums, boolean force) throws Exception;

    public abstract void merge() throws Exception;

    @Override
    public void inject(CrawlDatum datum) throws Exception {
        inject(datum, false);
    }

//    public void inject(CrawlDatums datums, boolean force) throws Exception {
//        for (CrawlDatum datum : datums) {
//            inject(datum, force);
//        }
//    }

    public void inject(CrawlDatums datums) throws Exception {
        inject(datums, false);
    }

    public void inject(Iterable<String> links, boolean force) throws Exception {
        CrawlDatums datums = new CrawlDatums(links);
        inject(datums, force);
    }

    public void inject(Iterable<String> links) throws Exception {
        inject(links, false);
    }

    public void inject(String url, boolean force) throws Exception {
        CrawlDatum datum = new CrawlDatum(url);
        inject(datum, force);
    }

    public void inject(String url) throws Exception {
        CrawlDatum datum = new CrawlDatum(url);
        inject(datum);
    }

    public AbstractGenerator getAbstractGenerator() {
        return abstractGenerator;
    }
    public void setAbstractGenerator(AbstractGenerator abstractGenerator) {
        this.abstractGenerator = abstractGenerator;
    }
    public DataBase getDataBase() {
        return dataBase;
    }
    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }
}

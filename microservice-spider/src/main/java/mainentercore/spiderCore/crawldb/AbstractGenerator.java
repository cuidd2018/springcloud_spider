package mainentercore.spiderCore.crawldb;


import mainentercore.spiderCore.crawldb.Idbutil.DataBase;
import mainentercore.spiderCore.crawldb.Idbutil.GeneratorFilter;
import mainentercore.spiderCore.entities.CrawlDatum;
import mainentercore.spiderCore.spiderConfig.DefaultConfigImp;
import mainentercore.spiderCore.spiderConfig.configUtil.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务生成器
 */
public abstract class AbstractGenerator<T> extends DefaultConfigImp {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractGenerator.class.getSimpleName());

    private DataBase<T> dataBase;
    private GeneratorFilter filter = null;
    private int topN = 0;

    private int totalGenerate;

    public AbstractGenerator() {
        this.totalGenerate = 0;
//      this.maxExecuteCount = getConfig().getOrDefault(Configuration.KEY_MAX_EXECUTE_COUNT, Integer.MAX_VALUE);
    }

    /**
     * return null if there is no CrawlDatum to generate
     *
     * @return
     */
    public CrawlDatum next() {
        if (getConfig().getTopN() > 0 && totalGenerate >= getConfig().getTopN()) {
            return null;
        }
        CrawlDatum datum;
        while (true) {
            try {
                datum = nextWithoutFilter();
                if (datum == null) {
                    return datum;
                }
                if (filter == null || (datum = filter.filter(datum)) != null) {
                    if (datum.getExecuteCount() > getConfig().getMaxExecuteCount()) {
                        continue;
                    }
                    totalGenerate += 1;
                    return datum;
                }
            } catch (Exception e) {
                LOG.info("Exception when generating", e);
                return null;
            }
        }
    }

    public abstract CrawlDatum nextWithoutFilter() throws Exception;

    public abstract void close() throws Exception;

    public GeneratorFilter getFilter() {
        return filter;
    }

    public void setFilter(GeneratorFilter filter) {
        ConfigurationUtils.setTo(this, filter);
        this.filter = filter;
    }


    @Override
    public String toString() {
        return "AbstractGenerator{" +
                "dataBase=" + dataBase.getClass().getSimpleName() +
                ", filter=" + filter +
                ", topN=" + topN +
                ", totalGenerate=" + totalGenerate +
                '}';
    }
    public DataBase<T> getDataBase() {
        return dataBase;
    }
    public void setDataBase(DataBase<T> dataBase) {
        this.dataBase = dataBase;
    }
    public int getTotalGenerate() {
        return totalGenerate;
    }
}

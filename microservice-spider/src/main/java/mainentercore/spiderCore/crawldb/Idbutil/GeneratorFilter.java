package mainentercore.spiderCore.crawldb.Idbutil;

import mainentercore.spiderCore.entities.CrawlDatum;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${file_name} </p>
 * <p>描述: [爬虫任务过滤] </p>
 **/
public interface GeneratorFilter {

     CrawlDatum filter(CrawlDatum datum);
}

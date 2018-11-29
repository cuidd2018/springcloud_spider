package mainentercore.spiderCore.entities.entityUtil;

import mainentercore.spiderCore.entities.CrawlDatum;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 网站链接数据结构格式化
 */
public class CrawlDatumFormater {

    //日期格式化
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //判断链接是否已经执行，成功还是失败
    public static String datumToString(CrawlDatum datum) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nURL: ").append(datum.url())
                .append("\nSTATUS: ");

        switch (datum.getStatus()) {
            case CrawlDatum.STATUS_DB_SUCCESS:
                sb.append("success");
                break;
            case CrawlDatum.STATUS_DB_FAILED:
                sb.append("failed");
                break;
            case CrawlDatum.STATUS_DB_UNEXECUTED:
                sb.append("unexecuted");
                break;
        }

        //加入执行时间
        sb.append("\nExecuteTime:").append(sdf.format(new Date(datum.getExecuteTime())))
                .append("\nExecuteCount:").append(datum.getExecuteCount());

        //int metaIndex = 0;

       /* for(Entry<String, JsonElement> entry: datum.meta().entrySet()){
            sb.append("\nMETA").append("[").append(metaIndex++).append("]:(")
                    .append(entry.getKey()).append(",").append(entry.getValue()).append(")");
        }*/
        sb.append("\n");
        return sb.toString();
    }

   /* public static CrawlDatum jsonStrToDatum(String crawlDatumKey, String jsonStr) {
        JsonArray jsonArray = GsonUtils.parse(jsonStr).getAsJsonArray();

        CrawlDatum crawlDatum = new CrawlDatum();
        crawlDatum.key(crawlDatumKey);
        crawlDatum.url(jsonArray.get(0).getAsString());
        crawlDatum.setStatus(jsonArray.get(1).getAsInt());
        crawlDatum.setExecuteTime(jsonArray.get(2).getAsLong());
        crawlDatum.setExecuteCount(jsonArray.get(3).getAsInt());
        *//*if (jsonArray.size() == 5) {
            JsonObject metaJsonObject = jsonArray.get(4).getAsJsonObject();
            crawlDatum.meta(metaJsonObject);
        }*//*
        return crawlDatum;
    }*/
}

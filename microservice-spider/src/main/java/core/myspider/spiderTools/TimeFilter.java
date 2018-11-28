package core.myspider.spiderTools;

import core.spiderCore.entities.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${字符串中提取时间} </p>
 **/
public class TimeFilter {
    /**
     * desc:正则表达式提取正文时间
     **/
    public String getTimeByReg(String str) {
        List<String> regs = new ArrayList<>();
        regs.add("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        regs.add("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
        regs.add("\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}");
        regs.add("\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}");
        regs.add("\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        regs.add("\\d{4,}\\/(?:0?\\d|1[12])\\/(?:[012]?\\d|3[01]) (?:[01]?\\d|2[0-4]):(?:[0-5]?\\d|60)");
        for (String re : regs) {
            Pattern p = Pattern.compile(re);
            Matcher matcher = p.matcher(str);
            if (matcher.find()) {
                str = matcher.group(0);
                break;
            }
        }
        return str;
    }

    /**
     * desc:提取页面更新时间
     **/
    public String timeLastModify(Page page) {
        return null;
    }
}

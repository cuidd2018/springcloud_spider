package mainentercore.dao;
import entity.ParseContentRules;
import entity.SiteConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import java.util.List;
import java.util.logging.Logger;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${file_name} </p>
 * <p>描述: [mysql 网站配置数据操作] </p>
 * <p>创建时间: ${date} </p>
 * @author <a href="mail to: 1139835238@qq.com" rel="nofollow">whitenoise</a>
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 **/
@Component
public class SitesConfigDao {
    private static final Logger LOG = Logger.getLogger(SitesConfigDao.class.getSimpleName());

    @Autowired JdbcTemplate jdbcTemplate;

    public List<SiteConfig> Read() {
        String sql = "SELECT * FROM siteconfig ";
        String siteName = "";
        SqlRowSet results = null;
        results = jdbcTemplate.queryForRowSet(sql);
        if (results.next()) {
            siteName = results.getString(2);
        }else {
            LOG.warning("网站配置数据库为空");
        }
        results.beforeFirst();
        ParseContentRules CR = readContentRules(siteName);
        List<SiteConfig> sfList = ObjectRelation(results, CR);
        return sfList;
    }

    public ParseContentRules readContentRules(String siteName) {
        String sql = "SELECT * from contentRules WHERE siteName = '" + siteName+"'";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        ParseContentRules cs = null;
        if (result.next()) {
            result.beforeFirst();
            while (result.next()) {
                cs = new ParseContentRules();
                cs.setTitle_rule(result.getString(3));
                cs.setContent_rule(result.getString(4));
                cs.setTime_rule(result.getString(5));
                cs.setMedia_rule(result.getString(6));
                cs.setAnthor_rule(result.getString(7));
            }
            return cs;
        } else {
            LOG.warning("正文提取规则为空");
            return cs;
        }
    }

    public boolean insertSiteConfig(SiteConfig sc) {
        String sfsql = "INSERT INTO siteConfig(siteName,siteUrl,resume,contentPares,urlPares,seeds,deepPath,autoParse,tableName) VALUES (?,?,?,?,?,?,?,?,?)";
        int sfindex = jdbcTemplate.update(sfsql, sc.getSiteName(), sc.getSiteUrl(), sc.isRes(), sc.getPageParse(), sc.getUrlPares(), sc.getSeeds(), sc.getDeepPath(), sc.isAutoParse(), sc.getTableName());

        String sql = "insert into contentRules(siteName,title_rule,content_rule,time_rule,media_rule,author_rule) values(?,?,?,?,?,?)";
        ParseContentRules cr = sc.getParseContentRules();
        int crindex = jdbcTemplate.update(sql, sc.getSiteName(), cr.getTitle_rule(), cr.getContent_rule(), cr.getTime_rule(), cr.getMedia_rule(), cr.getAnthor_rule());

        if (sfindex != 0 && crindex != 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<SiteConfig> ObjectRelation(SqlRowSet resultSet, ParseContentRules parseContentRules) {
        SiteConfig siteconfig;
        List<SiteConfig> list = new ArrayList<>();
        if (resultSet.next()) {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                siteconfig = new SiteConfig();
                siteconfig.setSiteName(resultSet.getString(2));
                siteconfig.setSiteUrl(resultSet.getString(3));
                siteconfig.setRes(resultSet.getBoolean(4));
                siteconfig.setAutoParse(resultSet.getBoolean(8));
                siteconfig.setDeepPath(resultSet.getInt(7));
                siteconfig.setTableName(resultSet.getString(9));
                siteconfig.setUrlPares(resultSet.getString(10));
                siteconfig.setSeeds(resultSet.getString(6));
                siteconfig.setPageParse(resultSet.getString(5));
                siteconfig.setParseContentRules(parseContentRules);
                list.add(siteconfig);
            }
            return list;
        } else {
            return null;
        }
    }
}

package spider.myspider.DbUtils;


import spider.myspider.DbUtils.IStore.Store;
import spider.myspider.entity.MyNew;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataStoreTool implements Store<MyNew> {

     private static Logger LOG = Logger.getLogger(DataStoreTool.class);

     @Autowired private JdbcTemplate jdbcTemplate;
     private String tableName;

     public void initStore(String tableName){
         this.tableName = tableName;
         creatTable(tableName);
         LOG.info("DataStoreTool初始化--->>"+toString());
     }

    /**
     * desc:初始化mysql建表
     **/
    public void creatTable(String tableName) {
        try {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS " + tableName
                            + "(new_id int(11) NOT NULL AUTO_INCREMENT,"
                            + "title varchar(100)," +
                            "url varchar(200)," +
                            "content longtext," +
                            "time VARCHAR(50)," +
                            "media VARCHAR(20)," +
                            "author VARCHAR(20),"
                            + "PRIMARY KEY (new_id)"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;"
            );
            LOG.info("Tips : CreatTable - " + tableName + " Success");
        } catch (Exception e) {
            LOG.error("mysql未开启或JDBCHelper.createMysqlTemplate中参数配置不正确! "+e.getCause());
        }
    }

    /**
     * @Title：${enclosing_method}
     * @Description: [持久化数据到mysql数据库]
     */
    @Override
    public void insert(MyNew myNew) {
        String sql = "insert into " + tableName + "(title,url,content,time,media,author) values(?,?,?,?,?,?)";
        //title url content time media author
        int x = jdbcTemplate.update(sql, myNew.getTitle(), myNew.getURL(), myNew.getContent(), myNew.getTime(), myNew.getMedia(), myNew.getAnthor());
       /* if (x != 0) {
            LOG.info("存入数据成功");
        } else {
            LOG.info("存入数据失败");
        }*/
    }

    @Override
    public String toString() {
        return "DataStoreTool{" +
                "jdbcTemplate=" + jdbcTemplate +
                ", tableName='" + tableName + '\'' +
                '}';
    }
}

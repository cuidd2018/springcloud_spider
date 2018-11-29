package mainentercore.myspider.DbUtils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * <p>项目名称: ${小型分布式爬虫} </p>
 * <p>文件名称: ${C3poDataSourceUtil} </p>
 * <p>描述: [c3p0 数据库连接池] </p>
 **/
@Configuration
public class C3poDataSourceConfig {
    private static final Logger LOG = Logger.getLogger(C3poDataSourceConfig.class);

    @Profile(value = "online")
    @Bean(name = "dataSource")
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public DataSource getCommonDataSource() {
        Properties properties = new Properties();
        try {
            properties.load(C3poDataSourceConfig.class.getClassLoader().getResourceAsStream("c3p0.properties"));
            if (properties == null) {
                LOG.error("未获取到数据库配置文件");
            }
//            LOG.info("数据库配置文件：" + properties.toString());
//            c3p0数据库连接池
            ComboPooledDataSource  dataSources = new ComboPooledDataSource();
            dataSources.setDriverClass(properties.getProperty("jdbc.driverClass"));
            dataSources.setJdbcUrl(properties.getProperty("jdbc.url"));
            dataSources.setUser(properties.getProperty("jdbc.user"));
            dataSources.setPassword(properties.getProperty("jdbc.password"));
            dataSources.setInitialPoolSize(Integer.parseInt(properties.getProperty("jdbc.initialPoolSize")));
            dataSources.setAcquireIncrement(Integer.parseInt(properties.getProperty("jdbc.acquireIncrement")));
            dataSources.setAutoCommitOnClose(Boolean.parseBoolean(properties.getProperty("jdbc.autoCommitOnClose")));
            dataSources.setMaxIdleTime(Integer.parseInt(properties.getProperty("jdbc.maxIdleTime")));
            dataSources.setMaxPoolSize(Integer.parseInt(properties.getProperty("jdbc.maxPoolSize")));
            dataSources.setMinPoolSize(Integer.parseInt(properties.getProperty("jdbc.minPoolSize")));
            return dataSources;
        } catch (Exception e) {
            LOG.error("C3p0 连接池初始化失败:\n" + "Message:" + e.getMessage() + "\nCaused By:" + e.getCause());
            return null;
        }
    }
}


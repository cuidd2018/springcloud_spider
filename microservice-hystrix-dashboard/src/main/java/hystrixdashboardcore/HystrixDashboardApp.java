package hystrixdashboardcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @author 一杯咖啡
 * @desc
 * @createTime 2018-12-03-11:01
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableHystrixDashboard
@EnableEurekaClient
public class HystrixDashboardApp {
    public static void main(String[] args){
        SpringApplication.run(HystrixDashboardApp.class, args);
    }
}

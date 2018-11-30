package spider.spiderCore.spiderConfig;

public class Config {

    public static String DEFAULT_USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0";
    public static int MAX_RECEIVE_SIZE = 1024 * 1024 * 10;
    public static int THREAD_KILLER = 1000 * 60 * 2;
    public static int WAIT_THREAD_END_TIME = 1000 * 60;
    /*最大连续重定向次数*/
    public static int MAX_REDIRECT = 2;

    //连接超时 3s ;
    public static int TIMEOUT_CONNECT = 3000;
    //读取超时10秒
    public static int TIMEOUT_READ = 10000;
    //最大执行数10
    public static int MAX_EXECUTE_COUNT = 10;
    //    public static String DEFAULT_HTTP_METHOD = "GET";
    public static int TOP_N = 0;

    //执行间隔 0秒
    public static int EXECUTE_INTERVAL = 0;
    //自动发现图片
    public static boolean AUTO_DETECT_IMG = false;

}

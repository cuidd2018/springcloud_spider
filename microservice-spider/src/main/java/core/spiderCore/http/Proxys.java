package core.spiderCore.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Random;

/**
 * 请求 IP 代理类 存有列表
 */
public class Proxys extends ArrayList<Proxy> {
    public static final Logger LOG=LoggerFactory.getLogger(Proxys.class);

    public static Random random = new Random();

    /**
     * desc:随机获取ip
     * @Return: Proxy
     **/
    public Proxy nextRandom(){
        int r = random.nextInt(this.size());
        return this.get(r);
    }
    
    public void addEmpty(){
        Proxy nullProxy=null;
        this.add(nullProxy);
    }
    /**
     * desc:ip 端口 参数添加
     * @Return:
     **/
    public void add(String ip, int port) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
        this.add(proxy);
    }
    /**
     * desc:0.0.0.0：8080 字符串格式添加
     * @Return: void
     **/
    public void add(String proxyStr) throws Exception {
        try {
            String[] infos = proxyStr.split(":");
            String ip = infos[0];
            int port = Integer.valueOf(infos[1]);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            this.add(proxy);
        } catch (Exception ex) {
            LOG.info("Exception", ex);
        }

    }

    /**
     * desc: 从文件中读取代理ip
     * @Return: void
     **/
    public void addAllFromFile(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#")||line.isEmpty()) {
                continue;
            } else {
                this.add(line);
            }
        }
    }
}

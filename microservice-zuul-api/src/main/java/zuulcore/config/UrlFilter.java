package zuulcore.config;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 一杯咖啡
 * @desc url请求过滤，安全验证
 * @createTime 2018-12-09-20:56
 */
public class UrlFilter extends ZuulFilter {
    private static final Logger LOG = Logger.getLogger(UrlFilter.class);
    /**
     * desc:过滤类型  pre 调用之前执行；
     **/
    @Override
    public String filterType() {
        return "pre";
    }
    /**
     * desc: 过滤顺序 0 优先级最高
     **/
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * desc: 判断是否执行过滤
     **/
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * desc: 过滤逻辑
     **/
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String name = request.getParameter("ac");
        LOG.info(request.getRequestURL().toString()+"请求访问");
        if (null==name||"".equals(name)){
            LOG.error("ac 验证失败");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            ctx.setResponseBody("{result:ac is empty!}");
        }else {
            LOG.info("ac 验证通过");
        }
        return null;
    }
}

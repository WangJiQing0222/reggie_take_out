package com.takeOut.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.takeOut.reggie.common.BaseContext;
import com.takeOut.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 小飞侠NO.1
 * @startTime 18:37:09
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    /**
     * 未登录则必须登录，才能访问到其它页面，      使用的是过滤器，不是拦截器
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();

        //不需要处理的请求路径        本次请求的URI 在urls里面，则不要处理，放行
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };


        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);


        //3、如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4-1、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("后台用户已登录,用户id为：{}", request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            //仅仅让当前管理员登录使用，缺点：多个用户登录只有最后一个管理员id有效
            //可以使用HashMap，键为employee+empId，值为empId
            //还可以放在redis中缓存当前用户当前id
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        //4-2、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("客户已登录,客户id为：{}", request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
//            BaseContext.setCurrentId(userId);
            request.getSession().setAttribute("user", userId);

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        //5、如果未登录则返回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return  true;
            }
        }
        return false;
    }
}

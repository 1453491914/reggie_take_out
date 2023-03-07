//package com.example.reggie_take_out.filter;
//
//import com.alibaba.fastjson.JSON;
//import com.example.reggie_take_out.common.Result;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.AntPathMatcher;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author chengcheng
// * @version 1.0.0
// * @createTime 2023/2/22-16:30
// */
//
///**
// * 用于拦截未登陆用户，若用户未登陆则转到登陆页面
// */
//@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
//@Slf4j
//public class LoginCheckFilter implements Filter {
//
//    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        //1、获取本次请求的URI
//        String requestURI = request.getRequestURI();
//
//        //2、判断本次请求是否需要处理
//        boolean checkUri = CheckUri(requestURI);
//
//        //3、如果不需要处理，则直接放行
//        if (checkUri) {
//            log.info("本次清求不需要处理", requestURI);
//            filterChain.doFilter(request, response);
//            return;
//        }
//        //4、判断登录状态，如果已登录，则直接放行
//        if (request.getSession().getAttribute("employee") != null) {
//            log.info("用戸己登家，用戸id:", request.getSession().getAttribute("employee"));
//            filterChain.doFilter(request, response);
//            return;
//        }
//        //5、如果未登录则返回未登录结果
//        log.info("未登家，转到登陆页面");
//        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
//        return;
//    }
//
//    public boolean CheckUri(String uri) {
//        String[] urls = new String[]{"/employee/login",
//                "/employee/logout",
//                "/backend/**",
//                "/front/**"
//        };
//        for (String url : urls) {
//            if (PATH_MATCHER.match(url, uri)) {
//                return true;
//            }
//        }
//        return false;
//    }
//}

//package com.example.reggie_take_out.config;
//
//import interceptor.LoginCheckInterceptor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * @author chengcheng
// * @version 1.0.0
// * @createTime 2023/2/23-10:35
// */
//@Configuration
//public class LoginConfig implements WebMvcConfigurer {
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        InterceptorRegistration loginCheck = registry.addInterceptor(new LoginCheckInterceptor());
//        loginCheck.addPathPatterns("/backend/index.html");
//        //loginCheck.excludePathPatterns("/employee/login",
//        //        "/employee/logout","/api/**","/images/**","/backend/page/login/login.html","/js/**","plugins/**","styles/**","/favicon.ico");
//    }
//}

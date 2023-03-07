package com.example.reggie_take_out.config;

import com.example.reggie_take_out.common.JacksonObjectMapper;
import interceptor.FrontLoginCheckInterceptor;
import interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @author chengcheng
 * @version 1.0.0
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态的资源映射
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/static/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/static/front/");
    }

    /**
     * 扩展mvc框架的消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层用Jackson将Java对象转换成json
        converter.setObjectMapper(new JacksonObjectMapper());
        //添加消息转换器
        converters.add(0,converter);
    }

    /**
     * 添加拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration loginCheck = registry.addInterceptor(new LoginCheckInterceptor());
        loginCheck.addPathPatterns("/backend/index.html","employee/**");
        //loginCheck.excludePathPatterns("/employee/login",
        //        "/employee/logout","/api/**","/images/**","/backend/page/login/login.html","/js/**","plugins/**","styles/**","/favicon.ico");
        InterceptorRegistration frontLoginCheck = registry.addInterceptor(new FrontLoginCheckInterceptor());
        frontLoginCheck.addPathPatterns("/front/index.html","/front/page/**")
                .excludePathPatterns("/front/page/login.html");
    }


}

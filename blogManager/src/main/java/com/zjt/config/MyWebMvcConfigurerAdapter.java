package com.zjt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {


    /**
     * 以前要访问一个页面需要先创建个Controller控制类，在写方法跳转到页面
     * 在这里配置后就不需要那么麻烦了，直接访问http://localhost:8080/toLogin就跳转到login.html页面了
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("welcome");
        registry.addViewController("/tologin").setViewName("login");
        registry.addViewController("/welcome").setViewName("welcome");
        registry.addViewController("/error/errordeal").setViewName("error/errordealogin");
        registry.addViewController("/error/unauthorized").setViewName("error/unauthorized");
        registry.addViewController("/main").setViewName("main");
        super.addViewControllers(registry);
    }

}


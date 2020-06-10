package com.xingyutang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/api/**")
                .excludePathPatterns("/api/login",
                        "/api/appId",
                        "/api/testToken",
                        "/api/user",
                        "/api/weixinCallback",
                        "/api/wxJSConfig",
                        "/api/rongchuang/convention/index",
                        "/api/rongchuang/convention/suppliers",
                        "/api/rongchuang/convention/download",
                        "/api/lvcheng/**",
                        "/api/rongchuang/season/audio",
                        "/api/rongchuang/season/userAudio",
                        "/api/rongchuang/life/listAll",
                        "/api/rongchuang/life/export",
                        "/api/qinhe/culture/**",
                        "/api/ruihong/**",
                        "/api/foliday/**",
                        "/api/dahua/springCity/**");
    }
}

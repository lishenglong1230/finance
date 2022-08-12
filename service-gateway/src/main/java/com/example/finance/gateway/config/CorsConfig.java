package com.example.finance.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @Author: Lishenglong
 * @Date: 2022/8/10 17:40
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() { //跨域过滤器
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //是否允许携带cookie
        config.addAllowedOrigin("*"); //可接受的域，是一个具体域名或者*（代表任意域名）
        config.addAllowedHeader("*"); //允许携带的头
        config.addAllowedMethod("*"); //允许访问的方式

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}

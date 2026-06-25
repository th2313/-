package com.example.keshe.config; // 包名要和你选择的一致

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的前端域名，根据实际前端地址修改，这里假设前端是 http://localhost:5173
        config.addAllowedOriginPattern("http://localhost:5173");
        config.setAllowCredentials(true); // 允许携带凭证（如 Cookie）
        config.addAllowedMethod("*"); // 允许所有 HTTP 方法（GET、POST 等）
        config.addAllowedHeader("*"); // 允许所有请求头
        config.setMaxAge(3600L); // 预检请求的缓存时间（秒）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 对所有路径生效

        return new CorsFilter(source);
    }
}
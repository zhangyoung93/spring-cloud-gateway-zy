package com.zy.gateway.config;

import com.zy.gateway.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工具类转换Bean
 *
 * @author zy
 */
@Configuration
public class UtilConfig {

    /**
     * JWT bean
     *
     * @param filterProperties filterProperties
     * @return JwtUtil
     */
    @Bean
    public JwtUtil jwtUtil(FilterProperties filterProperties) {
        return new JwtUtil(filterProperties);
    }
}

package com.zy.gateway.config;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * 跨域资源共享配置
 *
 * @author zy
 */
@Configuration
public class CorsConfig {

    /**
     * 自定义跨域配置
     *
     * @param corsProperties corsProperties
     * @return CorsConfiguration
     */
    @Bean
    public CorsConfiguration corsConfiguration(CorsProperties corsProperties) {
        //允许跨域请求的源
        Set<String> origins = corsProperties.getOrigins();
        //允许跨域请求的方法
        Set<String> methods = corsProperties.getMethods();
        //允许跨域请求的请求头
        Set<String> headers = corsProperties.getHeaders();

        CorsConfiguration cors = new CorsConfiguration();
        //只要开启CORS配置，必须设置指定的源域名
        if (CollectionUtils.isEmpty(origins)) {
            throw new RuntimeException("cors origins must not be null");
        }
        for (String origin : origins) {
            try {
                //校验源地址合法性
                URI uri = new URI(origin);
                Assert.hasText(uri.getScheme(), "uri schema must not be empty");
                Assert.hasText(uri.getHost(), "uri host must not be empty");
            } catch (URISyntaxException e) {
                throw new RuntimeException("cors origin resolve fail,origin" + origin, e);
            }
            cors.addAllowedOriginPattern(origin);
        }

        if (CollectionUtils.isEmpty(methods)) {
            cors.addAllowedMethod("*");
        } else {
            for (String method : methods) {
                cors.addAllowedMethod(method);
            }
        }

        if (CollectionUtils.isEmpty(headers)) {
            cors.addAllowedHeader("*");
        } else {
            for (String header : headers) {
                cors.addAllowedHeader(header);
            }
        }

        //允许跨域请求携带cookies
        cors.setAllowCredentials(true);
        return cors;
    }

    /**
     * CORS配置注册WebFilter
     * 设置优先级在鉴权之前
     * <p>
     * 测试的时候，请求头加上Origin
     *
     * @param corsProperties corsProperties
     * @return CorsWebFilter
     */
    @Bean
    @Order(-3)
    public CorsWebFilter corsWebFilter(CorsProperties corsProperties) {
        CorsConfiguration corsConfiguration = corsConfiguration(corsProperties);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //为所有接口路径设置相同跨域配置
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}

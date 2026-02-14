package com.zy.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * 限流配置类
 *
 * @author zy
 */
@Configuration
public class RateLimitConfig {

    /**
     * 注册KeyResolver的实现类remoteAddrKeyResolver，按照请求来源IP解析
     *
     * @return KeyResolver
     */
    @Bean
    public KeyResolver remoteAddrKeyResolver() {

        return exchange -> {
            InetSocketAddress inetSocketAddress = exchange.getRequest().getRemoteAddress();
            if (inetSocketAddress != null) {
                String hostAddress = inetSocketAddress.getAddress().getHostAddress();
                //本地IPV6调用，转换为127.0.0.1
                if ("0:0:0:0:0:0:0:1".equals(hostAddress)) {
                    hostAddress = "127.0.0.1";
                }
                return Mono.just(hostAddress);
            }
            return Mono.empty();
        };
    }
}
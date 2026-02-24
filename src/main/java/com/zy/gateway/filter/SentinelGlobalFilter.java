package com.zy.gateway.filter;

import com.alibaba.csp.sentinel.Tracer;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Sentinel全局过滤器
 *
 * @author zy
 */
@Order(-2)
@Component
public class SentinelGlobalFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //sentinel熔断降级根据异常数判断时，需要手动将下游服务异常报告给sentinel统计。
        return chain.filter(exchange).doOnSuccess(unused -> {
            //捕获下游服务响应异常
            HttpStatusCode httpStatusCode = exchange.getResponse().getStatusCode();
            if (httpStatusCode != null && httpStatusCode.is5xxServerError()) {
                Tracer.trace(new RuntimeException("call remote service exception"));
            }
        }).onErrorResume(throwable -> {
            //捕获网关转发异常
            Tracer.trace(throwable);
            return Mono.error(throwable);
        });
    }
}

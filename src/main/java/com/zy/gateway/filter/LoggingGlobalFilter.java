package com.zy.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * 日志过滤器
 * 集成skyWalking
 *
 * @author zy
 */
@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
        String clientIp = getClientIp(request);

        //skyWalking的链路信息注入MDC，需要应用启动时引入skywalking-agent.jar才能获取
        String traceId = TraceContext.traceId();
        String segmentId = TraceContext.segmentId();

        //请求开始时间
        long startTime = System.currentTimeMillis();
        return chain.filter(exchange).doFinally(signalType -> {
            try {
                //请求结束时间
                long endTime = System.currentTimeMillis();
                int statusCode = 500;
                HttpStatusCode httpStatusCode = exchange.getResponse().getStatusCode();
                if (httpStatusCode != null) {
                    statusCode = httpStatusCode.value();
                }
                //分布式服务的全局链路ID
                MDC.put("traceId", traceId);
                //当前服务的链路ID
                MDC.put("segmentId", segmentId);
                //请求IP地址
                MDC.put("clientIp", clientIp);
                //请求路径
                MDC.put("path", path);
                //请求方法
                MDC.put("method", method);
                //响应状态码
                MDC.put("statusCode", String.valueOf(statusCode));
                //请求耗时
                MDC.put("duration", endTime - startTime + "ms");
                //通过logger输出MDC配置
                log.info("HTTP access log");
            } finally {
                //MDC基于ThreadLocal，输出到日志后需要清理
                MDC.clear();
            }
        });
    }

    private String getClientIp(ServerHttpRequest request) {
        String clientIp = "unknown";
        InetSocketAddress inetSocketAddress = request.getRemoteAddress();
        if (inetSocketAddress != null) {
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }
}

package com.zy.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zy.gateway.config.FilterProperties;
import com.zy.gateway.constant.ResponseEnum;
import com.zy.gateway.dto.BaseResponse;
import com.zy.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * 鉴权全局过滤器
 * 设置优先级高于默认过滤器
 *
 * @author zy
 */
@Slf4j
@Order(-2)
@Component
public class AuthGlobalFilter implements GlobalFilter {

    private final FilterProperties filterProperties;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    public AuthGlobalFilter(FilterProperties filterProperties, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.filterProperties = filterProperties;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //响应式模型获取HTTP请求
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        //不需要鉴权的路径，跳过
        Set<String> excludes = this.filterProperties.getAuth().getExcludes();
        if (CollectionUtils.isNotEmpty(excludes) && excludes.contains(path)) {
            return chain.filter(exchange);
        }

        //OPTION请求跳过鉴权
        if (HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod().name())) {
            return chain.filter(exchange);
        }

        //获取JWT
        String token = null;
        List<String> authorizationList = request.getHeaders().get("Authorization");
        if (CollectionUtils.isNotEmpty(authorizationList)) {
            String authorization = authorizationList.get(0);
            if (StringUtils.isNoneBlank(authorization) && authorization.startsWith("Bearer ")) {
                token = authorization.substring("Bearer ".length());
            }
        }

        //校验JWT
        boolean checkResult = this.jwtUtil.checkToken(token);
        if (StringUtils.isBlank(token) || !checkResult) {
            //TOKEN验证不通过，直接响应
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            BaseResponse<Object> baseResponse = BaseResponse.build(ResponseEnum.AUTH_FAIL.getCode(), ResponseEnum.AUTH_FAIL.getMsg() + "token验证不通过!", null);
            String body = "{\"code\":401,\"msg\":\"Unauthorized\"}";
            try {
                body = this.objectMapper.writeValueAsString(baseResponse);
            } catch (JsonProcessingException e) {
                log.error("响应异常！", e);
            }
            DataBuffer dataBuffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            //响应式模型，http响应体是DataBuffer类型
            return response.writeWith(Mono.just(dataBuffer));
        }

        //将用户ID添加到请求头，传递给下游服务
        String userId = this.jwtUtil.getUserId(token);
        //header属性命名建议小写
        ServerHttpRequest newRequest = request.mutate().header("user-id", userId).build();
        return chain.filter(exchange.mutate().request(newRequest).build());
    }
}

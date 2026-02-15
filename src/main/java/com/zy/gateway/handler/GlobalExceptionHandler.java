package com.zy.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zy.gateway.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局异常处理
 * 设置优先级高于默认的异常处理类
 *
 * @author zy
 */
@Slf4j
@Order(-2)
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        //已经提交的响应不处理
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        //未知异常统一返回500
        HttpStatus globalStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Internal server error!" + ex.getMessage();

        //具体异常信息要输出到日志
        log.error("Unexpected gateway exception!", ex);

        //封装统一的异常返回
        response.setStatusCode(globalStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        BaseResponse<Object> baseResponse = BaseResponse.build(globalStatus.value(), message, null);
        try {
            byte[] bytes = this.objectMapper.writeValueAsBytes(baseResponse);
            DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error("global exception response fail", e);
        }
        return response.setComplete();
    }
}

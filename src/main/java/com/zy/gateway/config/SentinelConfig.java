package com.zy.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.zy.gateway.dto.BaseResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Sentinel配置
 *
 * @author zy
 */
@Configuration
public class SentinelConfig {

    /**
     * 初始化回调处理类
     */
    @PostConstruct
    public void initBlockHandler() {
        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable) -> {
            BlockException blockException = (BlockException) throwable;
            String resource = blockException.getRule().getResource();
            int status = HttpStatus.TOO_MANY_REQUESTS.value();
            StringBuilder msg = new StringBuilder();
            if (throwable instanceof FlowException) {
                //限流异常
                msg.append("请求流量过高，请稍后再试。阈值=");
                GatewayFlowRule gatewayFlowRule = GatewayRuleManager.getRules().stream().filter(rule -> resource.equals(rule.getResource())).findFirst().orElse(null);
                if (gatewayFlowRule != null) {
                    msg.append(gatewayFlowRule.getCount());
                } else {
                    msg.append("-1");
                }
            } else if (throwable instanceof DegradeException) {
                //熔断异常
                status = HttpStatus.SERVICE_UNAVAILABLE.value();
                msg.append("服务熔断降级，请稍后再试。阈值=");
                DegradeRule degradeRule = DegradeRuleManager.getRules().stream().filter(rule -> resource.equals(rule.getResource())).findFirst().orElse(null);
                if (degradeRule != null) {
                    msg.append(degradeRule.getCount());
                } else {
                    msg.append("-1");
                }
            } else {
                msg.append(throwable.getMessage());
            }
            //将sentinel异常信息按标准格式输出
            BaseResponse<Object> baseResponse = BaseResponse.build(status, msg.toString(), null);
            return ServerResponse.status(status).contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(baseResponse));
        };
        //注册回调处理类
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }
}

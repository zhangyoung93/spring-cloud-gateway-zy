package com.zy.gateway.listener;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.zy.gateway.handler.RouteRefreshHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * Nacos配置监听器
 *
 * @author zy
 */
@Slf4j
@Component
public class NacosConfigListener {

    private final ConfigService configService;

    private final RouteRefreshHandler routeRefreshHandler;

    public NacosConfigListener(ConfigService configService, RouteRefreshHandler routeRefreshHandler) {
        this.configService = configService;
        this.routeRefreshHandler = routeRefreshHandler;
    }

    @PostConstruct
    public void addListener() throws NacosException {
        this.configService.addListener("gateway-routes", "DEFAULT_GROUP", new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                //监听nacos配置更新时修改路由规则
                routeRefreshHandler.updateRoutesRule(configInfo);
            }
        });
    }
}

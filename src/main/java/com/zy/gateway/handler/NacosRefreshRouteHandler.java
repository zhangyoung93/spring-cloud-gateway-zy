package com.zy.gateway.handler;

import com.alibaba.cloud.nacos.annotation.NacosConfigListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Nacos刷新路由处理类
 *
 * @author zy
 */
@Slf4j
@Component
public class NacosRefreshRouteHandler {

    private final RouteDefinitionWriter routeDefinitionWriter;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final ObjectMapper objectMapper;

    private final Map<String, Boolean> routeIdMap = new ConcurrentHashMap<>(64);

    public NacosRefreshRouteHandler(RouteDefinitionWriter routeDefinitionWriter, ApplicationEventPublisher applicationEventPublisher, ObjectMapper objectMapper) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.applicationEventPublisher = applicationEventPublisher;
        this.objectMapper = objectMapper;
    }

    /**
     * 监听Nacos路由配置修改行为
     *
     * @param jsonStr nacos配置文件内容
     */
    @NacosConfigListener(dataId = "gateway-routes", group = "DEFAULT_GROUP")
    public void onRouteConfigChange(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            log.warn("gateway-routes json is empty");
            return;
        }
        try {
            //配置内容转换成对象，顺便检查JSON语法
            List<RouteDefinition> routeDefinitionList = this.objectMapper.readValue(jsonStr, new TypeReference<>() {
            });
            //保存新路由
            Set<String> newRouteIdSet = new HashSet<>();
            for (RouteDefinition routeDefinition : routeDefinitionList) {
                this.routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
                newRouteIdSet.add(routeDefinition.getId());
            }
            //删除无效的旧路由
            for (String oldRouteId : this.routeIdMap.keySet()) {
                if (!newRouteIdSet.contains(oldRouteId)) {
                    this.routeDefinitionWriter.delete(Mono.just(oldRouteId)).subscribe();
                }
            }
            //更新缓存
            this.routeIdMap.clear();
            for (String routeId : newRouteIdSet) {
                this.routeIdMap.put(routeId, true);
            }
            //发布刷新路由事件
            this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
            log.info("gateway-routes refresh success");
        } catch (JsonProcessingException e) {
            log.error("gateway-routes refresh error", e);
        }
    }
}

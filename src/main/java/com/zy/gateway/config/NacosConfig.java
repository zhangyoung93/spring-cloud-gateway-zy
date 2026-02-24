package com.zy.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Nacos配置
 *
 * @author zy
 */
@Configuration
public class NacosConfig {

    private final NacosConfigProperties nacosConfigProperties;

    public NacosConfig(NacosConfigProperties nacosConfigProperties) {
        this.nacosConfigProperties = nacosConfigProperties;
    }

    /**
     * 注册ConfigService
     *
     * @return ConfigService
     * @throws NacosException e
     */
    @Bean
    public ConfigService configService() throws NacosException {
        return NacosFactory.createConfigService(this.nacosConfigProperties.assembleConfigServiceProperties());
    }
}

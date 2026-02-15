package com.zy.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 网关启动类 Netty
 *
 * @author zy
 */
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.zy.gateway.config")
public class SpringCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayApplication.class, args);
    }

}

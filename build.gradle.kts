plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.zy.gateway"
version = "0.0.1-SNAPSHOT"
description = "spring-cloud-gateway-zy"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val springCloudVersion = "2023.0.3"
val springCloudAlibabaVersion = "2023.0.1.0"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        mavenBom("com.alibaba.cloud:spring-cloud-alibaba-dependencies:$springCloudAlibabaVersion")
    }
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter")
    //网关选择异步模型
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    //负载均衡，用于发现服务
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    //springboot状态监控
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    //异步Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    //apache-commons
    implementation("org.apache.commons:commons-lang3:3.18.0")
    implementation("org.apache.commons:commons-collections4:4.5.0")
    //skyWalking链路追踪工具类
    implementation("org.apache.skywalking:apm-toolkit-trace:9.4.0")
    //Nacos
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-config")
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-nacos-discovery")
    //Alibaba-Sentinel
    implementation("com.alibaba.cloud:spring-cloud-starter-alibaba-sentinel")
    implementation("com.alibaba.cloud:spring-cloud-alibaba-sentinel-gateway")
    implementation("com.alibaba.csp:sentinel-datasource-nacos:1.8.9")
    //配置文件加密
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
    //日志格式化
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    //JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    compileOnly("org.projectlombok:lombok")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

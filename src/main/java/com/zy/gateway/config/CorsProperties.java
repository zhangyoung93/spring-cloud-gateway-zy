package com.zy.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * CORS属性
 *
 * @author zy
 */
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    private Set<String> origins;

    private Set<String> methods;

    private Set<String> headers;

    public Set<String> getOrigins() {
        return origins;
    }

    public void setOrigins(Set<String> origins) {
        this.origins = origins;
    }

    public Set<String> getMethods() {
        return methods;
    }

    public void setMethods(Set<String> methods) {
        this.methods = methods;
    }

    public Set<String> getHeaders() {
        return headers;
    }

    public void setHeaders(Set<String> headers) {
        this.headers = headers;
    }
}

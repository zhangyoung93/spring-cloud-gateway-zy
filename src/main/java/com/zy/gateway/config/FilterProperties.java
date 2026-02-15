package com.zy.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * 过滤器属性
 *
 * @author zy
 */
@ConfigurationProperties(prefix = "filter")
public class FilterProperties {

    private Auth auth;

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public static class Auth {

        private String jwtSecretKey;

        /**
         * 默认1小时
         */
        private Long jwtExpireTime = 3600000L;

        private Set<String> excludes;

        public String getJwtSecretKey() {
            return jwtSecretKey;
        }

        public void setJwtSecretKey(String jwtSecretKey) {
            this.jwtSecretKey = jwtSecretKey;
        }

        public Long getJwtExpireTime() {
            return jwtExpireTime;
        }

        public void setJwtExpireTime(Long jwtExpireTime) {
            this.jwtExpireTime = jwtExpireTime;
        }

        public Set<String> getExcludes() {
            return excludes;
        }

        public void setExcludes(Set<String> excludes) {
            this.excludes = excludes;
        }
    }
}

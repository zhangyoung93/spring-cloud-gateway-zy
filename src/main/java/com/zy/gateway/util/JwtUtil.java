package com.zy.gateway.util;

import com.zy.gateway.config.FilterProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Assert;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类
 *
 * @author zy
 */
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;

    private final FilterProperties filterProperties;

    public JwtUtil(FilterProperties filterProperties) {
        this.filterProperties = filterProperties;
        //初始化JWT密钥
        secretKey = Keys.hmacShaKeyFor(Base64Util.StringToBytes(filterProperties.getAuth().getJwtSecretKey()));
    }

    /**
     * 校验TOKEN
     *
     * @param token 去掉"Bearer "前缀的token
     * @return boolean
     */
    public boolean checkToken(String token) {
        try {
            Assert.hasText(token, "token must not be null");
            Jwts.parserBuilder()
                    .setSigningKey(this.secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("check jwt token fail", e);
        }
        return false;
    }

    /**
     * 从TOKEN获取userId
     *
     * @param token 去掉"Bearer "前缀的token
     * @return userId
     */
    public String getUserId(String token) {
        String userId = null;
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            userId = claims.getSubject();
            if (StringUtils.isBlank(userId)) {
                userId = claims.get("userId", String.class);
            }
        } catch (Exception e) {
            log.error("getUserIdFromToken fail", e);
        }
        return userId;
    }

    /**
     * 获取token过期时间
     *
     * @param token token 去掉"Bearer "前缀的token
     * @return Date
     */
    public Date getExpireDate(String token) {
        Date date = null;
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            date = claims.getExpiration();
        } catch (Exception e) {
            log.error("getExpireDate fail", e);
        }
        return date;
    }

    /**
     * 签发Token
     * <p>
     * JSON明文格式：
     * {
     * "header" : {
     * "alg" : "HS256"
     * },
     * "payload" : {
     * "sub" : "zy",
     * "userId" : "11111",
     * "iat" : 1771143473,
     * "exp" : 1771147074
     * },
     * "signature" : "uf8_ttkJcKuKrt63BLtQH-w_3uEoX_XZEfbSUSg7u8g"
     * }
     * <p>
     * 摘要字符串形式：eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ6eSIsInVzZXJJZCI6IjExMTExIiwiaWF0IjoxNzcxMTQzNDczLCJleHAiOjE3NzExNDcwNzR9.uf8_ttkJcKuKrt63BLtQH-w_3uEoX_XZEfbSUSg7u8g
     *
     * @param subject token主体
     * @param userId  token的自定义属性
     * @return String
     */
    public String signToken(String subject, String userId) {
        String token = null;
        try {
            Assert.hasText(subject, "subject must not be empty");
            Assert.hasText(userId, "userId must not be empty");
            Date nowDate = new Date();
            Date expireDate = new Date(System.currentTimeMillis() + this.filterProperties.getAuth().getJwtExpireTime());
            token = Jwts.builder()
                    //设置JWT主体，通常是userId
                    .setSubject(subject)
                    //自定义设置userId属性
                    .claim("userId", userId)
                    //设置token签发时间
                    .setIssuedAt(nowDate)
                    //设置token过期时间
                    .setExpiration(expireDate)
                    //设置token签发密钥
                    .signWith(this.secretKey)
                    //转换字符串
                    .compact();
            log.info("headerName=Authorization,headerValue={}", "Bearer " + token);
        } catch (Exception e) {
            log.error("sign token fail", e);
        }
        return token;
    }
}

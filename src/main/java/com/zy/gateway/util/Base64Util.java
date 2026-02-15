package com.zy.gateway.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Base64工具类
 *
 * @author zy
 */
public class Base64Util {

    /**
     * 生成随机32字节的Base64字符串
     *
     * @return String
     */
    public static String generateBase64Key() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64字符串转字节数组
     *
     * @param base64Str base64Str
     * @return byte[]
     */
    public static byte[] StringToBytes(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }
}

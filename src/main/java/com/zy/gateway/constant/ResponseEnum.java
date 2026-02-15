package com.zy.gateway.constant;

import lombok.Getter;

/**
 * 应答枚举
 *
 * @author zy
 */
@Getter
public enum ResponseEnum {
    /**
     * 编码 + 描述
     */
    SUCCESS(200, "处理成功！"),
    FAIL(500, "处理失败！"),
    AUTH_FAIL(401, "鉴权不通过！");

    private final Integer code;

    private final String msg;

    ResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

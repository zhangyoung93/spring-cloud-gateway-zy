package com.zy.gateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zy.gateway.constant.ResponseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 基础应答实体（泛型版）
 *
 * @param <T> 响应数据的具体类型
 * @author zy
 */
@Getter
@Setter
public class BaseResponse<T> {
    // 编码
    private Integer code;
    // 描述
    private String msg;
    // 数据
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    //时间戳
    private Long timestamp;

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                '}';
    }

    public static <T> BaseResponse<T> build(Integer code, String msg, T data) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setCode(code);
        baseResponse.setMsg(msg);
        baseResponse.setTimestamp(System.currentTimeMillis());
        if (data != null) {
            baseResponse.setData(data);
        }
        return baseResponse;
    }

    public static <T> BaseResponse<T> success(T data) {
        return build(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMsg(), data);
    }

    public static <T> BaseResponse<T> success() {
        return success(null);
    }

    public static <T> BaseResponse<T> fail(String msg, T data) {
        return build(ResponseEnum.FAIL.getCode(), msg, data);
    }

    public static <T> BaseResponse<T> fail(String msg) {
        return fail(msg, null);
    }
}
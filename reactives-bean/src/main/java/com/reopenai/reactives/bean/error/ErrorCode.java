package com.reopenai.reactives.bean.error;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by Allen Huang
 */
@JsonSerialize(using = ErrorCodeJsonSerializer.class)
public interface ErrorCode {

    String getCode();

    /**
     * 内置的一些错误码
     */
    @Getter
    @RequiredArgsConstructor
    enum Builtin implements ErrorCode {

        SUCCESS("200"),
        //---------------
        //  HTTP请求相关
        //---------------
        /**
         * 请求错误
         */
        BAD_REQUEST("400"),
        /**
         * 未认证
         */
        UNAUTHORIZED("401"),
        /**
         * 没有权限
         */
        FORBIDDEN("403"),
        /**
         * 资源不存在
         */
        NOT_FOUND("404"),
        /**
         * 请求方法错误
         */
        METHOD_NOT_ALLOWED("405"),
        /**
         * 无法解析ACCEPT
         */
        NOT_ACCEPTABLE("406"),
        /**
         * 请求的Media type错误
         */
        MEDIA_TYPE_NOT_ALLOWED("415"),
        /**
         * 请求太多被熔断
         */
        MANY_REQUEST("429"),
        /**
         * 未知异常
         */
        SERVER_ERROR("500"),
        /**
         * 未通过参数检查
         */
        FAILED_PARAMETER_CHECK("4001"),
        /**
         * 请求参数丢失
         */
        MISSING_REQUEST_PARAMETER("4002"),
        /**
         * 解析Token失败
         */
        PARSE_TOKEN_ERROR("4003"),
        /**
         * 非法的参数值
         */
        INVALID_PARAMETER("4006"),
        /**
         * TOTP签名错误
         */
        TOTP_CODE_ERROR("4007"),

        /**
         * 请求参数类型不匹配
         */
        PARAM_TYPE_MISMATCH("4008"),

        //---------------
        //  内部错误
        //---------------

        //---------------
        //  RPC相关
        //---------------
        /**
         * RPC接口参数错误.args=原因
         */
        RPC_ARGUMENT_TYPE_MISMATCH("5100"),
        /**
         * RPC接口参数校验未通过
         */
        RPC_ARGUMENT_VALIDATION("5101"),
        /**
         * RPC接口不存在
         */
        RPC_NOT_FOUND("5102"),
        /**
         * RPC请求方式错误
         */
        PRC_METHOD_NOT_SUPPORTED("5103"),
        /**
         * RPC请求方式错误
         */
        PRC_MEDIA_TYPE_NOT_SUPPORTED("5104"),
        /**
         * RPC缺少请求参数
         */
        PRC_MISSING_PARAMETER("5105"),
        /**
         * RPC参数错误
         */
        PRC_REQUEST_UNSATISFIED("5106"),
        /**
         * 请求参数错误
         */
        RPC_PARAMETER_MISTAKE("5107"),
        /**
         * RPC服务端执行异常
         */
        RPC_SERVER_ERROR("5108"),
        /**
         * RPC timeout异常
         */
        RPC_TIMEOUT("5109"),
        /**
         * RPC的目标对象状态不正常
         */
        RPC_UNAVAILABLE("5110");

        private final String code;

    }


    /**
     * 创建一个临时的错误码实例
     *
     * @param code 错误码
     * @return 错误码实例
     */
    static Temporary temporary(String code) {
        return new Temporary(code);
    }

    record Temporary(String code) implements ErrorCode {
        @Override
        public String getCode() {
            return this.code;
        }
    }

}

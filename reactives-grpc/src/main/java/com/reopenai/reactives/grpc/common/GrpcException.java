package com.reopenai.reactives.grpc.common;

import com.reopenai.reactives.bean.error.ErrorCode;
import com.reopenai.reactives.core.exception.BusinessException;

/**
 * Created by Allen Huang
 */
public class GrpcException extends BusinessException {

    public GrpcException(Throwable cause, ErrorCode errorCode, Object... args) {
        super(cause, errorCode, args);
    }

}

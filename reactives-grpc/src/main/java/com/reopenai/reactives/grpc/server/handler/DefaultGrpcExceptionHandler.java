package com.reopenai.reactives.grpc.server.handler;

import com.reopenai.reactives.bean.error.ErrorCode;
import com.reopenai.reactives.core.exception.BusinessException;
import com.reopenai.reactives.grpc.common.metadata.GrpcMetaDataKeys;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusException;
import org.springframework.core.Ordered;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

import java.util.StringJoiner;

/**
 * Created by Allen Huang
 */
public class DefaultGrpcExceptionHandler implements GrpcExceptionHandler, Ordered {

    @Override
    public StatusException handleException(Throwable exception) {
        Status status = Status.UNKNOWN;
        Metadata trailers = new Metadata();
        if (exception instanceof BusinessException err) {
            trailers.put(GrpcMetaDataKeys.ERROR_CODE, err.getErrorCode().getCode());
            trailers.put(GrpcMetaDataKeys.ERROR_MSG, err.getMessage());
            Object[] args = err.getArgs();
            if (args != null && args.length > 0) {
                StringJoiner joiner = new StringJoiner(",");
                for (Object arg : args) {
                    joiner.add(arg.toString());
                }
                trailers.put(GrpcMetaDataKeys.ERROR_PARAMS, joiner.toString());
            }
        } else {
            trailers.put(GrpcMetaDataKeys.ERROR_MSG, exception.getMessage());
            trailers.put(GrpcMetaDataKeys.ERROR_CODE, ErrorCode.Builtin.RPC_SERVER_ERROR.getCode());
        }
        return new StatusException(status, trailers);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}

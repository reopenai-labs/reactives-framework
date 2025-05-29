package com.reopenai.reactives.grpc.client.handler;

import cn.hutool.core.util.StrUtil;
import com.reopenai.reactives.bean.constants.EmptyConstants;
import com.reopenai.reactives.bean.error.ErrorCode;
import com.reopenai.reactives.grpc.common.GrpcException;
import com.reopenai.reactives.grpc.common.GrpcMethodDetail;
import com.reopenai.reactives.grpc.common.metadata.GrpcMetaDataKeys;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Allen Huang
 */
public class DefaultGrpcClientExceptionHandler implements GrpcClientExceptionHandler {

    @Override
    public GrpcException handle(GrpcMethodDetail methodDetail, Throwable cause) {
        if (cause instanceof StatusRuntimeException e) {
            return handleStatusRuntimeException(methodDetail, e);
        }
        return null;
    }

    private GrpcException handleStatusRuntimeException(GrpcMethodDetail methodDetail, StatusRuntimeException e) {
        StringBuilder builder = new StringBuilder();
        Status status = e.getStatus();
        builder.append("gRPC [endpoint=").append(methodDetail.getEndpoint()).append("]");
        builder.append("#[code=").append(status.getCode().value()).append("]");
        builder.append("#[status=").append(status.getCode().name()).append(']');
        builder.append("#[message=").append(e.getMessage()).append("]");
        Metadata trailers = e.getTrailers();
        if (trailers != null) {
            String errorCode = trailers.get(GrpcMetaDataKeys.ERROR_CODE);
            if (StrUtil.isNotBlank(errorCode)) {
                Object[] params = Optional.ofNullable(trailers.get(GrpcMetaDataKeys.ERROR_PARAMS))
                        .map(errorParams -> errorParams.split(","))
                        .orElse(EmptyConstants.EMPTY_STRING_ARRAY);
                String errMsg = trailers.get(GrpcMetaDataKeys.ERROR_MSG);
                builder.append("#[errorCode=[").append(errorCode).append("]");
                builder.append("#[errorParams=[").append(Arrays.toString(params)).append(']');
                builder.append("#[errMsg=[").append(errMsg == null ? status.getDescription() : errMsg).append("]");
                return new GrpcException(e, ErrorCode.temporary(errorCode), params);
            }
        }
        methodDetail.getLogger().error(builder.toString());
        return switch (status.getCode()) {
            case UNAVAILABLE -> new GrpcException(e, ErrorCode.Builtin.RPC_UNAVAILABLE);
            case NOT_FOUND -> new GrpcException(e, ErrorCode.Builtin.RPC_NOT_FOUND);
            case DEADLINE_EXCEEDED -> new GrpcException(e, ErrorCode.Builtin.RPC_TIMEOUT);
            case RESOURCE_EXHAUSTED -> new GrpcException(e, ErrorCode.Builtin.PRC_RESOURCE_EXHAUSTED);
            case INVALID_ARGUMENT -> new GrpcException(e, ErrorCode.Builtin.RPC_INVALID_ARGUMENT, e.getMessage());
            default -> new GrpcException(e, ErrorCode.Builtin.RPC_SERVER_ERROR);
        };
    }

}

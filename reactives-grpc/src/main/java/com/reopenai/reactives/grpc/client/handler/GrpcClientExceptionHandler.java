package com.reopenai.reactives.grpc.client.handler;

import com.reopenai.reactives.grpc.common.GrpcException;
import com.reopenai.reactives.grpc.common.GrpcMethodDetail;

/**
 * Created by Allen Huang
 */
public interface GrpcClientExceptionHandler {

    GrpcException handle(GrpcMethodDetail methodDetail, Throwable cause);

}

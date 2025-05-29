package com.reopenai.reactives.grpc.server.invoker;

import com.reopenai.reactives.grpc.common.GrpcMethodDetail;
import io.grpc.stub.ServerCalls;

/**
 * Created by Allen Huang
 */
public interface GrpcServerInvokeFactory {

    ServerCalls.UnaryMethod<byte[], byte[]> create(Object bean, GrpcMethodDetail methodDetail);

}

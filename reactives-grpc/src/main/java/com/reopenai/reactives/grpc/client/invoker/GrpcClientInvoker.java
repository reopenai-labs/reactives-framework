package com.reopenai.reactives.grpc.client.invoker;

/**
 * Created by Allen Huang
 */
public interface GrpcClientInvoker {

    Object invoke(Object[] arguments);

}

package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.grpc.common.GrpcMethodDetail;

/**
 * Created by Allen Huang
 */
public interface GrpcClientInvoker {

    Object invoke(Object[] arguments);

    GrpcMethodDetail getMethodDetail();

}

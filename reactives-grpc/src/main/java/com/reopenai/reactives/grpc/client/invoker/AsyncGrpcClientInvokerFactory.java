package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.Channel;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Allen Huang
 */
public class AsyncGrpcClientInvokerFactory extends BaseGrpcClientInvokerFactory {

    public AsyncGrpcClientInvokerFactory(List<RpcSerialization> serializations) {
        super(serializations);
    }

    @Override
    public GrpcClientInvoker create(Channel channel, GrpcMethodDetail methodDetail) {
        Class<?> returnType = methodDetail.getReturnClass();
        if (Future.class.isAssignableFrom(returnType)) {
            Method method = methodDetail.getMethod();
            RpcSerialization serialization = getSerialization(method);
            return new AsyncGrpcClientInvoker(channel, methodDetail, serialization);
        }
        return null;
    }

}

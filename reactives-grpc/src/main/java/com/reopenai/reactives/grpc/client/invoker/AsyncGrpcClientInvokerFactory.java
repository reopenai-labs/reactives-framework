package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;

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
    public GrpcClientInvoker create(Channel channel, Method method, MethodDescriptor<byte[], byte[]> methodDescriptor) {
        Class<?> returnType = method.getReturnType();
        if (Future.class.isAssignableFrom(returnType)) {
            RpcSerialization serialization = getSerialization(method);
            return new AsyncGrpcClientInvoker(channel, method, serialization, methodDescriptor);
        }
        return null;
    }

}

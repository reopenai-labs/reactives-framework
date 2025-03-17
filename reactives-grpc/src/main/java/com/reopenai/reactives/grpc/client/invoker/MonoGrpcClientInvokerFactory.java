package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Allen Huang
 */
public class MonoGrpcClientInvokerFactory extends BaseGrpcClientInvokerFactory {

    public MonoGrpcClientInvokerFactory(List<RpcSerialization> serializations) {
        super(serializations);
    }

    @Override
    public GrpcClientInvoker create(Channel channel, Method method, MethodDescriptor<byte[], byte[]> methodDescriptor) {
        Class<?> returnType = method.getReturnType();
        if (Mono.class.isAssignableFrom(returnType)) {
            RpcSerialization serialization = getSerialization(method);
            return new MonoGrpcClientInvoker(channel, method, serialization, methodDescriptor);
        }
        return null;
    }

}

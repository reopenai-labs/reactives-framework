package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.Channel;
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
    public GrpcClientInvoker create(Channel channel, GrpcMethodDetail methodDetail) {
        Class<?> returnType = methodDetail.getReturnClass();
        if (Mono.class.isAssignableFrom(returnType)) {
            Method method = methodDetail.getMethod();
            RpcSerialization serialization = getSerialization(method);
            return new MonoGrpcClientInvoker(channel, methodDetail, serialization);
        }
        return null;
    }

}

package com.reopenai.reactives.grpc.client.invoker;

import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * Created by Allen Huang
 */
public interface GrpcClientInvokerFactory extends Ordered {

    GrpcClientInvoker create(Channel channel, Method method, MethodDescriptor<byte[], byte[]> methodDescriptor);

    @Override
    default int getOrder() {
        return Integer.MIN_VALUE;
    }

}

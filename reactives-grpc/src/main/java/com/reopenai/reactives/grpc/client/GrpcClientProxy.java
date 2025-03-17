package com.reopenai.reactives.grpc.client;

import com.reopenai.reactives.grpc.client.invoker.GrpcClientInvoker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Allen Huang
 */
@RequiredArgsConstructor
public class GrpcClientProxy implements InvocationHandler {

    private final Logger logger;

    private final Map<Method, GrpcClientInvoker> invokers;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        if (Object.class.equals(declaringClass)) {
            return method.invoke(this, args);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[gRPC]requesting.args:{}", Arrays.asList(args));
        }
        GrpcClientInvoker invoker = invokers.get(method);
        if (invoker != null) {
            return invoker.invoke(args);
        }
        if (method.isDefault()) {
            return InvocationHandler.invokeDefault(proxy, method, args);
        }
        throw new IllegalStateException("Unexpected method invocation: " + method);
    }

}

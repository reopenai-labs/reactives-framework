package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.core.bench.BenchMarker;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Allen Huang
 */
public abstract class BaseGrpcClientInvoker implements GrpcClientInvoker {

    protected final Method method;

    protected final Channel channel;

    protected final Type returnType;

    protected final String benchFlag;

    protected final RpcSerialization rpcSerialization;

    protected final MethodDescriptor<byte[], byte[]> methodDescriptor;

    public BaseGrpcClientInvoker(Channel channel, Method method, RpcSerialization rpcSerialization,
                                 MethodDescriptor<byte[], byte[]> methodDescriptor) {
        this.method = method;
        this.channel = channel;
        this.rpcSerialization = rpcSerialization;
        this.methodDescriptor = methodDescriptor;
        this.benchFlag = BenchMarker.parseMethodFlag(method);
        this.returnType = resolveReturnType(method);
    }

    protected Type resolveReturnType(Method method) {
        ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
        return genericReturnType.getActualTypeArguments()[0];
    }

    protected ClientCall<byte[], byte[]> newCall() {
        return this.channel.newCall(this.methodDescriptor, CallOptions.DEFAULT);
    }

    protected byte[] serializerArguments(Object[] arguments) {
        Object params = arguments == null ? null : arguments.length == 0 ? null : arguments[0];
        return rpcSerialization.serializer(params);
    }

    protected Object deserializerResult(byte[] data) {
        return rpcSerialization.deserializer(data, returnType);
    }

}

package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;

import java.lang.reflect.Type;

/**
 * Created by Allen Huang
 */
public abstract class BaseGrpcClientInvoker implements GrpcClientInvoker {

    protected final GrpcMethodDetail methodDetail;

    protected final Channel channel;

    protected final RpcSerialization rpcSerialization;

    public BaseGrpcClientInvoker(Channel channel, GrpcMethodDetail methodDetail, RpcSerialization rpcSerialization) {
        this.methodDetail = methodDetail;
        this.channel = channel;
        this.rpcSerialization = rpcSerialization;
    }

    protected ClientCall<byte[], byte[]> newCall() {
        MethodDescriptor<byte[], byte[]> methodDescriptor = this.methodDetail.getMethodDescriptor();
        return this.channel.newCall(methodDescriptor, CallOptions.DEFAULT);
    }

    protected byte[] serializerArguments(Object[] arguments) {
        Object params = arguments == null ? null : arguments.length == 0 ? null : arguments[0];
        return rpcSerialization.serializer(params);
    }

    protected Object deserializerResult(byte[] data) {
        Type returnType = this.methodDetail.getReturnType();
        return rpcSerialization.deserializer(data, returnType);
    }

}

package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.core.bench.BenchMarker;
import com.reopenai.reactives.core.bench.BenchMarkers;
import com.reopenai.reactives.grpc.client.handler.GrpcClientExceptionHandler;
import com.reopenai.reactives.grpc.common.GrpcMethodDetail;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.stub.ClientCalls;
import lombok.RequiredArgsConstructor;

/**
 * Created by Allen Huang
 */
@RequiredArgsConstructor
public class BlockingGrpcClientInvoker extends BaseGrpcClientInvoker {

    protected final Channel channel;

    protected final GrpcMethodDetail methodDetail;

    protected final RpcSerialization rpcSerialization;

    protected final GrpcClientExceptionHandler grpcClientExceptionHandler;

    @Override
    public Object invoke(Object[] arguments) {
        BenchMarker benchMarker = BenchMarkers.current();
        try {
            benchMarker.mark(this.methodDetail.getBenchFlag());
            byte[] buff = serializerArguments(arguments);
            byte[] result = ClientCalls.blockingUnaryCall(channel, this.methodDetail.getMethodDescriptor(), CallOptions.DEFAULT, buff);
            return this.deserializerResult(result);
        } catch (Throwable e) {
            throw grpcClientExceptionHandler.handle(this.methodDetail, e);
        } finally {
            benchMarker.mark(this.methodDetail.getBenchFlag());
        }
    }

    @Override
    public GrpcMethodDetail getMethodDetail() {
        return this.methodDetail;
    }

    @Override
    public RpcSerialization getRpcSerialization() {
        return this.rpcSerialization;
    }

}

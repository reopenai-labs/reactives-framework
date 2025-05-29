package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.core.bench.BenchMarkers;
import com.reopenai.reactives.grpc.client.handler.GrpcClientExceptionHandler;
import com.reopenai.reactives.grpc.common.GrpcMethodDetail;
import com.reopenai.reactives.grpc.common.metadata.GrpcContextKeys;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.util.context.ContextView;

/**
 * Created by Allen Huang
 */
@RequiredArgsConstructor
public class MonoGrpcClientInvoker extends BaseGrpcClientInvoker {

    protected final Channel channel;

    protected final GrpcMethodDetail methodDetail;

    protected final RpcSerialization rpcSerialization;

    private final GrpcClientExceptionHandler grpcClientExceptionHandler;

    @Override
    public Mono<?> invoke(Object[] arguments) {
        byte[] argument = serializerArguments(arguments);
        return Mono.deferContextual(ctx ->
                Mono.<byte[]>create(sink -> {
                            StreamObserverAdapter adapter = new StreamObserverAdapter(sink);
                            currentContext(ctx).run(() -> {
                                ClientCall<byte[], byte[]> clientCall = this.newCall();
                                ClientCalls.asyncUnaryCall(clientCall, argument, adapter);
                            });
                        })
                        .onErrorMap(throwable -> grpcClientExceptionHandler.handle(this.methodDetail, throwable))
                        .transformDeferredContextual((mono, cv) -> BenchMarkers.markWithContext(mono, cv, methodDetail.getBenchFlag()))
                        .flatMap(this::deserialize)
        );
    }

    private Mono<Object> deserialize(byte[] buff) {
        Object o = this.deserializerResult(buff);
        return o == null ? Mono.empty() : Mono.just(o);
    }

    private Context currentContext(ContextView ctx) {
        Context context = Context.current();
        for (GrpcContextKeys key : GrpcContextKeys.values()) {
            Object value = ctx.getOrDefault(key.getAppKey(), null);
            if (value != null) {
                context = context.withValue(key.getContextKey(), key.getEncode().apply(value));
            }
        }
        return context;
    }

    protected ClientCall<byte[], byte[]> newCall() {
        MethodDescriptor<byte[], byte[]> methodDescriptor = this.methodDetail.getMethodDescriptor();
        return this.channel.newCall(methodDescriptor, CallOptions.DEFAULT);
    }

    @Override
    public RpcSerialization getRpcSerialization() {
        return this.rpcSerialization;
    }

    @Override
    public GrpcMethodDetail getMethodDetail() {
        return this.methodDetail;
    }


    private record StreamObserverAdapter(MonoSink<byte[]> sink) implements StreamObserver<byte[]> {

        @Override
        public void onNext(byte[] value) {
            sink.success(value);
        }

        @Override
        public void onError(Throwable t) {
            sink.error(t);
        }

        @Override
        public void onCompleted() {
            sink.success();
        }

    }

}

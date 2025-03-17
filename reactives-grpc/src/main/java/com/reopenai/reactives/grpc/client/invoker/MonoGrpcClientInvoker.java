package com.reopenai.reactives.grpc.client.invoker;

import com.reopenai.reactives.core.bench.BenchMarker;
import com.reopenai.reactives.grpc.common.metadata.GrpcKeys;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.Context;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import reactor.util.context.ContextView;

import java.lang.reflect.Method;

/**
 * Created by Allen Huang
 */
public class MonoGrpcClientInvoker extends BaseGrpcClientInvoker {

    public MonoGrpcClientInvoker(Channel channel, Method method, RpcSerialization serialization,
                                 MethodDescriptor<byte[], byte[]> methodDescriptor) {
        super(channel, method, serialization, methodDescriptor);
    }

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
                        .transformDeferredContextual((mono, cv) -> BenchMarker.markWithContext(mono, cv, benchFlag))
                        .flatMap(this::deserialize)
        );
    }

    private Mono<Object> deserialize(byte[] buff) {
        Object o = this.deserializerResult(buff);
        return o == null ? Mono.empty() : Mono.just(o);
    }

    private Context currentContext(ContextView ctx) {
        Context context = Context.current();
        for (GrpcKeys key : GrpcKeys.values()) {
            Object value = ctx.getOrDefault(key.getAppKey(), null);
            if (value != null) {
                context = context.withValue(key.getContextKey(), key.getEncode().apply(value));
            }
        }
        return context;
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

package com.reopenai.reactives.grpc.server.invoker;

import com.reopenai.reactives.bean.constants.EmptyConstants;
import com.reopenai.reactives.core.bench.BenchMarker;
import com.reopenai.reactives.grpc.common.GrpcMethodDetail;
import com.reopenai.reactives.grpc.common.metadata.GrpcContextKeys;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Allen Huang
 */
@RequiredArgsConstructor
public class ReactorGrpcServerInvoker implements ServerCalls.UnaryMethod<byte[], byte[]> {

    private final Object bean;

    private final GrpcMethodDetail methodDetail;

    private final RpcSerialization serialization;


    @Override
    public void invoke(byte[] bytes, StreamObserver<byte[]> streamObserver) {
        Map<Object, Object> context = buildContext();
        BenchMarker marker = new BenchMarker();
        context.put(BenchMarker.class, marker);
        StreamObserverAdapter adapter = new StreamObserverAdapter(streamObserver, marker, methodDetail);
        invokeWithArguments(bytes)
                .map(serialization::serializer)
                .doOnError(adapter::onError)
                .doOnSubscribe(adapter::doOnSubscribe)
                .contextWrite(Context.of(context))
                .contextCapture()
                .subscribe(adapter::onNext, adapter::onError, adapter::onCompleted);
    }

    private Map<Object, Object> buildContext() {
        Map<Object, Object> context = new HashMap<>();
        for (GrpcContextKeys key : GrpcContextKeys.values()) {
            String value = key.getContextKey().get();
            if (value != null) {
                context.put(key.getAppKey(), key.getDecode().apply(value));
            }
        }
        return context;
    }

    @RequiredArgsConstructor
    private static class StreamObserverAdapter implements StreamObserver<byte[]> {

        private final StreamObserver<byte[]> delegate;

        private final BenchMarker marker;

        private final GrpcMethodDetail methodDetail;

        private boolean published;

        @Override
        public void onNext(byte[] bytes) {
            delegate.onNext(bytes);
            published = true;
        }

        @Override
        public void onError(Throwable throwable) {
            delegate.onError(throwable);
        }

        @Override
        public void onCompleted() {
            if (!published) {
                delegate.onNext(EmptyConstants.EMPTY_BYTE_ARRAY);
            }
            delegate.onCompleted();
            marker.mark(methodDetail.getBenchFlag());
            methodDetail.getLogger().info("gRPC Call: {}", marker.getResult());
        }

        public void doOnSubscribe(Subscription subscription) {
            marker.mark(methodDetail.getBenchFlag());
        }

    }

    @SuppressWarnings("unchecked")
    private Mono<Object> invokeWithArguments(byte[] bytes) {
        Object[] arguments;
        Parameter parameter = methodDetail.getParameter();
        if (parameter == null) {
            arguments = EmptyConstants.EMPTY_OBJECT_ARRAY;
        } else if (bytes.length == 0) {
            arguments = new Object[]{null};
        } else {
            Object deserializer = serialization.deserializer(bytes, parameter.getParameterizedType());
            arguments = new Object[]{deserializer};
        }
        try {
            return (Mono<Object>) methodDetail.getMethod().invoke(bean, arguments);
        } catch (Throwable e) {
            return Mono.error(e);
        }
    }

}

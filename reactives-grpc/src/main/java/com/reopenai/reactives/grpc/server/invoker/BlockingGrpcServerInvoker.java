package com.reopenai.reactives.grpc.server.invoker;

import com.reopenai.reactives.bean.constants.EmptyConstants;
import com.reopenai.reactives.core.bench.BenchMarker;
import com.reopenai.reactives.core.bench.BenchMarkers;
import com.reopenai.reactives.grpc.common.GrpcMethodDetail;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Parameter;

/**
 * Created by Allen Huang
 */
@RequiredArgsConstructor
public class BlockingGrpcServerInvoker implements ServerCalls.UnaryMethod<byte[], byte[]> {

    private final Object bean;

    private final GrpcMethodDetail methodDetail;

    private final RpcSerialization serialization;


    @Override
    public void invoke(byte[] bytes, StreamObserver<byte[]> streamObserver) {
        BenchMarker benchMarker = BenchMarkers.current();
        benchMarker.mark("inter");
        try {
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
            Object result = methodDetail.getMethod().invoke(bean, arguments);
            byte[] serializer = serialization.serializer(result);
            streamObserver.onNext(serializer);
            streamObserver.onCompleted();
        } catch (Throwable e) {
            streamObserver.onError(e);
        } finally {
            benchMarker.mark("outer");
            methodDetail.getLogger().info("gRPC server {}", benchMarker.getResult());
        }
    }

}

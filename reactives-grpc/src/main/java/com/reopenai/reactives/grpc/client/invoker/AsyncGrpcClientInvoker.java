package com.reopenai.reactives.grpc.client.invoker;

import com.google.common.util.concurrent.ListenableFuture;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * Created by Allen Huang
 */
public class AsyncGrpcClientInvoker extends BaseGrpcClientInvoker {

    public AsyncGrpcClientInvoker(Channel channel, Method method, RpcSerialization rpcSerialization, MethodDescriptor<byte[], byte[]> methodDescriptor) {
        super(channel, method, rpcSerialization, methodDescriptor);
    }

    @Override
    public Object invoke(Object[] arguments) {
        byte[] argument = serializerArguments(arguments);
        ClientCall<byte[], byte[]> clientCall = this.newCall();
        ListenableFuture<byte[]> future = ClientCalls.futureUnaryCall(clientCall, argument);
        return new AsyncGrpcFuture(future, this::deserializerResult);
    }

    record AsyncGrpcFuture(ListenableFuture<byte[]> delegate,
                           Function<byte[], Object> decoder) implements Future<Object> {

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return delegate.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return delegate.isCancelled();
        }

        @Override
        public boolean isDone() {
            return delegate.isDone();
        }

        @Override
        public Object get() throws InterruptedException, ExecutionException {
            byte[] buff = delegate.get();
            return decoder.apply(buff);
        }

        @Override
        public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            byte[] buff = delegate.get(timeout, unit);
            return decoder.apply(buff);
        }
    }

}

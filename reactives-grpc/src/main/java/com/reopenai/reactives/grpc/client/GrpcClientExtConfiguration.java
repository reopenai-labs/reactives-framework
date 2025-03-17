package com.reopenai.reactives.grpc.client;

import com.reopenai.reactives.grpc.client.interceptor.ContextClientInterceptor;
import com.reopenai.reactives.grpc.client.invoker.AsyncGrpcClientInvokerFactory;
import com.reopenai.reactives.grpc.client.invoker.MonoGrpcClientInvokerFactory;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GlobalClientInterceptor;

import java.util.List;

/**
 * Created by Allen Huang
 */
@Configuration
public class GrpcClientExtConfiguration {

    @Bean
    public MonoGrpcClientInvokerFactory monoGrpcClientInvokerFactory(List<RpcSerialization> serializations) {
        return new MonoGrpcClientInvokerFactory(serializations);
    }

    @Bean
    public AsyncGrpcClientInvokerFactory asyncGrpcClientInvokerFactory(List<RpcSerialization> serializations) {
        return new AsyncGrpcClientInvokerFactory(serializations);
    }

    @Bean
    @GlobalClientInterceptor
    public ContextClientInterceptor contextClientInterceptor() {
        return new ContextClientInterceptor();
    }

}


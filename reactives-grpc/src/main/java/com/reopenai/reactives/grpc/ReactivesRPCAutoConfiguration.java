package com.reopenai.reactives.grpc;

import com.reopenai.reactives.grpc.client.GrpcClientRegister;
import com.reopenai.reactives.grpc.client.handler.DefaultGrpcClientExceptionHandler;
import com.reopenai.reactives.grpc.client.handler.GrpcClientExceptionHandler;
import com.reopenai.reactives.grpc.client.invoker.DefaultGrpcClientInvokerFactory;
import com.reopenai.reactives.grpc.client.invoker.GrpcClientInvokerFactory;
import com.reopenai.reactives.grpc.serialization.JsonSerialization;
import com.reopenai.reactives.grpc.serialization.ProtostuffSerialization;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import com.reopenai.reactives.grpc.server.CustomGrpcServiceDiscoverer;
import com.reopenai.reactives.grpc.server.handler.DefaultGrpcExceptionHandler;
import com.reopenai.reactives.grpc.server.invoker.DefaultGrpcServerInvokeFactory;
import com.reopenai.reactives.grpc.server.invoker.GrpcServerInvokeFactory;
import com.reopenai.reactives.grpc.server.test.EchoService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.grpc.autoconfigure.server.GrpcServerFactoryAutoConfiguration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.grpc.server.service.GrpcServiceConfigurer;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Allen Huang
 */
@AutoConfiguration
@Import(GrpcClientRegister.class)
@AutoConfigureBefore(GrpcServerFactoryAutoConfiguration.class)
public class ReactivesRPCAutoConfiguration {

    // 通用配置

    @Bean
    public EchoService echoService() {
        return new EchoService();
    }

    @Bean
    public RpcSerialization protostuffGrpcSerialization() {
        return new ProtostuffSerialization();
    }

    @Bean
    public RpcSerialization jsonGrpcSerialization() {
        return new JsonSerialization();
    }

    //---------------------
    //    客户端配置
    //---------------------

    @Bean
    @ConditionalOnMissingBean(GrpcClientInvokerFactory.class)
    public GrpcClientExceptionHandler grpcClientExceptionHandler() {
        return new DefaultGrpcClientExceptionHandler();
    }

    @Bean
    public DefaultGrpcClientInvokerFactory defaultGrpcClientInvokerFactory(List<RpcSerialization> serializations,
                                                                           GrpcClientExceptionHandler exceptionHandler) {
        Map<String, RpcSerialization> serializationMap = serializations
                .stream()
                .collect(Collectors.toMap(RpcSerialization::supportType, Function.identity()));
        return new DefaultGrpcClientInvokerFactory(serializationMap, exceptionHandler);
    }

    //---------------------
    //    服务端配置
    //---------------------

    @Bean
    @ConditionalOnProperty(value = "spring.grpc.server.enabled", havingValue = "true", matchIfMissing = true)
    public GrpcServerInvokeFactory grpcServerInvokeFactory(List<RpcSerialization> serializations) {
        return new DefaultGrpcServerInvokeFactory(serializations);
    }

    @Bean
    @ConditionalOnProperty(value = "spring.grpc.server.enabled", havingValue = "true", matchIfMissing = true)
    public CustomGrpcServiceDiscoverer grpcServiceDiscoverer(GrpcServiceConfigurer grpcServiceConfigurer,
                                                             ApplicationContext applicationContext,
                                                             GrpcServerInvokeFactory grpcServerInvokeFactory) {
        return new CustomGrpcServiceDiscoverer(grpcServiceConfigurer, applicationContext, grpcServerInvokeFactory);
    }

    @Bean
    @ConditionalOnProperty(value = "spring.grpc.server.enabled", havingValue = "true", matchIfMissing = true)
    public GrpcExceptionHandler defaultGrpcExceptionHandler() {
        return new DefaultGrpcExceptionHandler();
    }

}

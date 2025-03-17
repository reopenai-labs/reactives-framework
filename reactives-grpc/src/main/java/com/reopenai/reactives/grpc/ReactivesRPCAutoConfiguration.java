package com.reopenai.reactives.grpc;

import com.reopenai.reactives.grpc.client.GrpcClientRegister;
import com.reopenai.reactives.grpc.client.GrpcClientTargetProperties;
import com.reopenai.reactives.grpc.serialization.JsonSerialization;
import com.reopenai.reactives.grpc.serialization.ProtostuffSerialization;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import com.reopenai.reactives.grpc.server.CustomGrpcServiceDiscoverer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.grpc.server.service.GrpcServiceConfigurer;

import java.util.List;

/**
 * Created by Allen Huang
 */
@AutoConfiguration
@Import(GrpcClientRegister.class)
@EnableConfigurationProperties(GrpcClientTargetProperties.class)
public class ReactivesRPCAutoConfiguration {

    @Bean
    public RpcSerialization protostuffSerialization() {
        return new ProtostuffSerialization();
    }

    @Bean
    public RpcSerialization jsonSerialization() {
        return new JsonSerialization();
    }

    @Bean
    public CustomGrpcServiceDiscoverer grpcServiceDiscoverer(GrpcServiceConfigurer grpcServiceConfigurer,
                                                             ApplicationContext applicationContext,
                                                             List<RpcSerialization> rpcSerialization) {
        return new CustomGrpcServiceDiscoverer(grpcServiceConfigurer, applicationContext, rpcSerialization);
    }

}

package com.reopenai.reactives.grpc.server;

import com.reopenai.reactives.grpc.server.interceptor.ContextServerInterceptor;
import com.reopenai.reactives.grpc.server.test.EchoService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.autoconfigure.server.GrpcServerFactoryAutoConfiguration;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.service.GrpcService;

/**
 * Created by Allen Huang
 */
@AutoConfiguration
@AutoConfigureBefore(GrpcServerFactoryAutoConfiguration.class)
public class GrpcServerExtConfiguration {

    @Bean
    @GlobalServerInterceptor
    public ContextServerInterceptor contextServerInterceptor() {
        return new ContextServerInterceptor();
    }

    @GrpcService
    public EchoService echoService() {
        return new EchoService();
    }

}

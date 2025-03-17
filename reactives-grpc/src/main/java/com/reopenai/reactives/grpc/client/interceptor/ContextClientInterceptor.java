package com.reopenai.reactives.grpc.client.interceptor;

import com.reopenai.reactives.grpc.common.metadata.GrpcKeys;
import io.grpc.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.grpc.client.GlobalClientInterceptor;

/**
 * Created by Allen Huang
 */
@GlobalClientInterceptor
public class ContextClientInterceptor implements ClientInterceptor, EnvironmentAware {

    private String serviceName;

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<>(channel.newCall(method, callOptions)) {
            public void start(ClientCall.Listener<RespT> responseListener, io.grpc.Metadata headers) {
                headers.put(GrpcKeys.REFERER_SERVICE.getMetadataKey(), serviceName);
                for (GrpcKeys key : GrpcKeys.values()) {
                    String value = key.getContextKey().get();
                    if (value != null) {
                        headers.put(key.getMetadataKey(), value);
                    }
                }
                super.start(responseListener, headers);
            }
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.serviceName = environment.resolveRequiredPlaceholders("${spring.application.name:unknown}");
    }

}

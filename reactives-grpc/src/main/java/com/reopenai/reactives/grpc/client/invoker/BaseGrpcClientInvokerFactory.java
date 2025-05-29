package com.reopenai.reactives.grpc.client.invoker;

import cn.hutool.core.util.StrUtil;
import com.reopenai.reactives.grpc.client.annotation.GrpcStub;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import lombok.RequiredArgsConstructor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by Allen Huang
 */
@RequiredArgsConstructor
public abstract class BaseGrpcClientInvokerFactory implements GrpcClientInvokerFactory, EnvironmentAware {

    protected final List<RpcSerialization> serializations;

    protected Environment environment;

    protected RpcSerialization getSerialization(Method method) {
        String protocol = getProtocol(method);
        return getSerialization(protocol);
    }

    protected String getProtocol(Method method) {
        return Optional.ofNullable(method.getDeclaringClass().getAnnotation(GrpcStub.class))
                .map(GrpcStub::protocol)
                .filter(StrUtil::isNotBlank)
                .map(this.environment::resolvePlaceholders)
                .orElse(this.environment.resolveRequiredPlaceholders("${spring.grpc.global.protocol:PROTOSTUFF}"));
    }

    protected RpcSerialization getSerialization(String protocol) {
        protocol = protocol.toLowerCase(Locale.ROOT);
        for (RpcSerialization serialization : serializations) {
            if (serialization.supportType().equals(protocol)) {
                return serialization;
            }
        }
        throw new IllegalArgumentException("No serialization found for protocol " + protocol);
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

}

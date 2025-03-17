package com.reopenai.reactives.grpc.common;

import cn.hutool.core.util.StrUtil;
import com.reopenai.reactives.grpc.client.annotation.GrpcStub;
import com.reopenai.reactives.grpc.common.marshaller.ByteArrayMarshaller;
import io.grpc.MethodDescriptor;
import io.grpc.ServiceDescriptor;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * gRPC服务描述工具类
 *
 * @author Allen Huang
 */
public final class GrpcDefinitionUtil {

    /**
     * gRPC MethodDescriptor和Java Method映射关系的缓存
     */
    private static final Map<Method, MethodDescriptor<byte[], byte[]>> methodDescriptors = new HashMap<>(2);

    /**
     * 缓存服务的描述
     */
    private static final Map<Class<?>, ServiceDescriptor> serviceDescriptors = new HashMap<>(2);

    /**
     * 将一个Java方法解析成gRPC MethodDescriptor
     *
     * @param serviceName 方法的包名
     * @param method      目标方法名
     * @return MethodDescriptor实例
     */
    public static MethodDescriptor<byte[], byte[]> buildMethodDescriptor(String serviceName, Method method) {
        MethodDescriptor<byte[], byte[]> methodDescriptor = methodDescriptors.get(method);
        if (methodDescriptor == null) {
            synchronized (methodDescriptors) {
                methodDescriptor = methodDescriptors.get(method);
                if (methodDescriptor == null) {
                    methodDescriptor = MethodDescriptor.<byte[], byte[]>newBuilder()
                            .setType(MethodDescriptor.MethodType.UNARY)
                            .setFullMethodName(String.join("/", serviceName, method.getName()))
                            .setSampledToLocalTracing(true)
                            .setRequestMarshaller(ByteArrayMarshaller.INSTANCE)
                            .setResponseMarshaller(ByteArrayMarshaller.INSTANCE)
                            .build();
                    methodDescriptors.put(method, methodDescriptor);
                }
            }
        }
        return methodDescriptor;
    }

    /**
     * 获取一个服务的描述信息
     *
     * @param type 服务的类型
     * @return 服务的gRPC描述
     */
    public static ServiceDescriptor buildServiceDescriptor(Class<?> type, Environment environment) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException("RPC定义类必须是一个接口.");
        }
        ServiceDescriptor descriptor = serviceDescriptors.get(type);
        if (descriptor == null) {
            synchronized (serviceDescriptors) {
                descriptor = serviceDescriptors.get(type);
                if (descriptor == null) {
                    String serviceName = parseServiceName(type, environment);
                    ServiceDescriptor.Builder builder = ServiceDescriptor.newBuilder(serviceName);
                    for (Class<?> parent = type; parent != null; parent = parent.getSuperclass()) {
                        for (Method method : parent.getDeclaredMethods()) {
                            if (Modifier.isAbstract(method.getModifiers())) {
                                builder.addMethod(buildMethodDescriptor(serviceName, method));
                            }
                        }
                    }
                    descriptor = builder.build();
                    serviceDescriptors.put(type, descriptor);
                }
            }
        }
        return descriptor;
    }

    public static String parseServiceName(Class<?> type, Environment environment) {
        String packages = Optional.ofNullable(type.getAnnotation(GrpcStub.class))
                .map(GrpcStub::packages)
                .filter(StrUtil::isNotBlank)
                .map(environment::resolvePlaceholders)
                .orElseGet(type::getPackageName);
        return String.join(".", packages, type.getSimpleName());
    }

}

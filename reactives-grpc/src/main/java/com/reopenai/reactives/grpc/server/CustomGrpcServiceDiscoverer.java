package com.reopenai.reactives.grpc.server;

import com.reopenai.reactives.grpc.client.annotation.GrpcStub;
import com.reopenai.reactives.grpc.common.GrpcDefinitionUtil;
import com.reopenai.reactives.grpc.serialization.RpcSerialization;
import com.reopenai.reactives.grpc.server.invoker.ReactiveGrpcServerInvoker;
import com.reopenai.reactives.grpc.server.test.EchoServiceGrpc;
import io.grpc.*;
import io.grpc.stub.ServerCalls;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.grpc.internal.ApplicationContextBeanLookupUtils;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.service.DefaultGrpcServiceDiscoverer;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.grpc.server.service.GrpcServiceConfigurer;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Allen Huang
 */
public class CustomGrpcServiceDiscoverer extends DefaultGrpcServiceDiscoverer implements EnvironmentAware {

    private final ApplicationContext applicationContext;

    private final Map<String, RpcSerialization> rpcSerializationMap;

    private Environment environment;

    private List<ServerInterceptor> globalInterceptors;

    public CustomGrpcServiceDiscoverer(GrpcServiceConfigurer serviceConfigurer,
                                       ApplicationContext applicationContext,
                                       List<RpcSerialization> rpcSerialization) {
        super(serviceConfigurer, applicationContext);
        this.applicationContext = applicationContext;
        this.rpcSerializationMap = rpcSerialization.stream().collect(Collectors.toMap(RpcSerialization::supportType, Function.identity()));
    }

    @Override
    public List<ServerServiceDefinition> findServices() {
        List<ServerServiceDefinition> definitions = new ArrayList<>(super.findServices());
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(GrpcService.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object bean = entry.getValue();
            if (bean instanceof EchoServiceGrpc.EchoServiceImplBase) {
                continue;
            }
            Class<?> realClass = ClassUtils.getUserClass(bean);
            GrpcService grpcService = realClass.getAnnotation(GrpcService.class);
            Class<?>[] superInterfaces = ClassUtils.getAllInterfaces(bean);
            for (Class<?> superInterface : superInterfaces) {
                GrpcStub stub = superInterface.getAnnotation(GrpcStub.class);
                if (stub != null) {
                    String serviceName = GrpcDefinitionUtil.parseServiceName(superInterface, this.environment);
                    ServiceDescriptor serviceDescriptor = GrpcDefinitionUtil.buildServiceDescriptor(superInterface, this.environment);
                    ServerServiceDefinition.Builder builder = ServerServiceDefinition.builder(serviceDescriptor);
                    for (Method method : superInterface.getDeclaredMethods()) {
                        if (Modifier.isAbstract(method.getModifiers())) {
                            MethodDescriptor<byte[], byte[]> md = GrpcDefinitionUtil.buildMethodDescriptor(serviceName, method);
                            RpcSerialization rpcSerialization = Optional.ofNullable(this.rpcSerializationMap.get(stub.protocol()))
                                    .orElseThrow(() -> new IllegalStateException("不支持的序列化协议:" + stub.protocol() + "see: " + method.getDeclaringClass().getName() + method.getName()));
                            ReactiveGrpcServerInvoker reactiveGrpcServerInvoker = new ReactiveGrpcServerInvoker(bean, method, rpcSerialization);
                            ServerCallHandler<byte[], byte[]> handler = ServerCalls.asyncUnaryCall(reactiveGrpcServerInvoker);
                            builder.addMethod(md, handler);
                        }
                    }
                    definitions.add(bindInterceptors(builder.build(), grpcService));
                }
            }
        }
        return definitions;
    }

    private List<ServerInterceptor> findGlobalInterceptors() {
        if (this.globalInterceptors == null) {
            this.globalInterceptors = ApplicationContextBeanLookupUtils.getBeansWithAnnotation(this.applicationContext,
                    ServerInterceptor.class, GlobalServerInterceptor.class);
        }
        return this.globalInterceptors;
    }

    private ServerServiceDefinition bindInterceptors(ServerServiceDefinition serviceDefinition, GrpcService serviceConfig) {
        List<ServerInterceptor> interceptors = new ArrayList<>(findGlobalInterceptors());

        for (Class<? extends ServerInterceptor> interceptorClass : serviceConfig.interceptors()) {
            ServerInterceptor serverInterceptor;
            if (this.applicationContext.getBeanNamesForType(interceptorClass).length > 0) {
                serverInterceptor = this.applicationContext.getBean(interceptorClass);
            } else {
                try {
                    serverInterceptor = interceptorClass.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new BeanCreationException("Failed to create interceptor instance", e);
                }
            }
            interceptors.add(serverInterceptor);
        }
        for (String interceptorName : serviceConfig.interceptorNames()) {
            interceptors.add(this.applicationContext.getBean(interceptorName, ServerInterceptor.class));
        }
        return ServerInterceptors.interceptForward(serviceDefinition, interceptors);
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}

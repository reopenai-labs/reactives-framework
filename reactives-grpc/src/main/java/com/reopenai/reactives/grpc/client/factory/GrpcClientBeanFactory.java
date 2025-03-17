package com.reopenai.reactives.grpc.client.factory;

import com.reopenai.reactives.grpc.client.GrpcClientProxy;
import com.reopenai.reactives.grpc.client.GrpcClientTargetProperties;
import com.reopenai.reactives.grpc.client.annotation.GrpcStub;
import com.reopenai.reactives.grpc.client.invoker.GrpcClientInvoker;
import com.reopenai.reactives.grpc.client.invoker.GrpcClientInvokerFactory;
import com.reopenai.reactives.grpc.common.GrpcDefinitionUtil;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.env.Environment;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Created by Allen Huang
 */
@RequiredArgsConstructor
public class GrpcClientBeanFactory implements FactoryBean<Object>, ApplicationContextAware, EnvironmentAware {

    private final Class<?> targetType;

    private final ClassLoader classLoader;

    private Environment environment;

    private GrpcClientTargetProperties properties;

    private GrpcChannelFactory channelFactory;

    private Collection<GrpcClientInvokerFactory> invokerFactories;

    public GrpcClientBeanFactory(String beanClassName, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.targetType = ClassUtils.resolveClassName(beanClassName, classLoader);
    }

    @Override
    public Object getObject() throws Exception {
        Logger logger = LoggerFactory.getLogger(this.targetType);
        Class<?>[] proxiedInterfaces = ClassUtils.getAllInterfacesForClass(this.targetType);
        Map<Method, GrpcClientInvoker> invokers = this.createInvokers();
        GrpcClientProxy proxy = new GrpcClientProxy(logger, invokers);
        return Proxy.newProxyInstance(classLoader, proxiedInterfaces, proxy);
    }

    public Map<Method, GrpcClientInvoker> createInvokers() {
        GrpcStub description = this.targetType.getAnnotation(GrpcStub.class);
        String target = resolveTarget(description.service());
        String serviceName = GrpcDefinitionUtil.parseServiceName(this.targetType, this.environment);
        ManagedChannel channel = channelFactory.createChannel(target);

        Set<Method> methods = MethodIntrospector.selectMethods(this.targetType, this::matchMethod);
        Map<Method, GrpcClientInvoker> invokers = new HashMap<>(methods.size());
        for (Method method : methods) {
            MethodDescriptor<byte[], byte[]> md = GrpcDefinitionUtil.buildMethodDescriptor(serviceName, method);
            GrpcClientInvoker invoker = null;
            for (GrpcClientInvokerFactory invokerFactory : invokerFactories) {
                invoker = invokerFactory.create(channel, method, md);
                if (invoker != null) {
                    invokers.put(method, invoker);
                    break;
                }
            }
            if (invoker == null) {
                throw new BeanCreationException("gRPC Client当前不支持此方法: " + method.getDeclaringClass().getSimpleName() + "." + method.getName());
            }
        }
        return invokers;
    }

    private String resolveTarget(String serviceName) {
        serviceName = this.environment.resolvePlaceholders(serviceName);
        Map<String, String> specTargets = Optional.ofNullable(properties.getSpec())
                .orElse(Collections.emptyMap());
        String s = specTargets.get(serviceName);
        if (s != null) {
            return s;
        }
        Set<String> exclude = Optional.ofNullable(properties.getExcludeTemplates())
                .orElse(Collections.emptySet());
        if (exclude.contains(serviceName)) {
            return serviceName;
        }
        String arg = serviceName;
        return Optional.ofNullable(properties.getTemplate())
                .map(template -> String.format(template, arg))
                .orElse(serviceName);
    }

    protected boolean matchMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        if (Object.class.equals(declaringClass)) {
            return false;
        }
        Class<?> returnType = method.getReturnType();
        return Publisher.class.isAssignableFrom(returnType);
    }

    @Override
    public Class<?> getObjectType() {
        return targetType;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.channelFactory = applicationContext.getBean(GrpcChannelFactory.class);
        this.properties = applicationContext.getBean(GrpcClientTargetProperties.class);
        this.invokerFactories = applicationContext.getBeansOfType(GrpcClientInvokerFactory.class).values();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}

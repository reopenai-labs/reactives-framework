package com.reopenai.reactives.grpc.client;

import com.reopenai.reactives.core.runtime.RuntimeUtil;
import com.reopenai.reactives.grpc.client.annotation.GrpcStub;
import com.reopenai.reactives.grpc.client.factory.GrpcClientBeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * Created by Allen Huang
 */
public class GrpcClientRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private Environment environment;

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        ClassLoader classLoader = this.resourceLoader.getClassLoader();
        scanner.setResourceLoader(this.resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(GrpcStub.class));
        String mainPackage = RuntimeUtil.getMainPackage();
        int firstIndex = mainPackage.indexOf('.');
        int secondIndex = mainPackage.indexOf('.', firstIndex + 1);
        String packageName = mainPackage.substring(0, secondIndex);
        scanner.findCandidateComponents(packageName)
                .forEach(beanDefinition -> {
                    String className = beanDefinition.getBeanClassName();
                    AbstractBeanDefinition definition = BeanDefinitionBuilder
                            .genericBeanDefinition(GrpcClientBeanFactory.class)
                            .addConstructorArgValue(className)
                            .addConstructorArgValue(classLoader)
                            .getBeanDefinition();
                    String beanName = BeanDefinitionReaderUtils.generateBeanName(
                            new RootBeanDefinition(className), registry
                    ) + "FactoryBean";
                    registry.registerBeanDefinition(beanName, definition);
                });
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        String mainPackage = RuntimeUtil.getMainPackage();
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    String className = beanDefinition.getMetadata().getClassName();
                    if (className.startsWith(mainPackage)) {
                        return false;
                    }
                    return !beanDefinition.getMetadata().isAnnotation();
                }
                return false;
            }
        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}

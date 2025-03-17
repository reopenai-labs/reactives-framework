package com.reopenai.reactives.core.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reopenai.reactives.bean.enums.Language;
import com.reopenai.reactives.core.serialization.jackson.deserializer.LanguageDeserializer;
import com.reopenai.reactives.core.serialization.jackson.deserializer.LocalDateDeserializer;
import com.reopenai.reactives.core.serialization.jackson.deserializer.LocalDateTimeDeserializer;
import com.reopenai.reactives.core.serialization.jackson.deserializer.LocalTimeDeserializer;
import com.reopenai.reactives.core.serialization.jackson.serializer.*;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Jackson自动配置类
 *
 * @author Allen Huang
 */
@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
public class JacksonConfig implements CommandLineRunner, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                // 序列化配置
                .serializerByType(Number.class, new NumberSerializer())
                .serializerByType(Language.class, new LanguageSerializer())
                .serializerByType(LocalTime.class, new LocalTimeSerializer())
                .serializerByType(LocalDate.class, new LocalDateSerializer())
                .serializerByType(BigDecimal.class, new BigDecimalSerializer())
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer())
                // 反序列化配置
                .deserializerByType(Language.class, new LanguageDeserializer())
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer())
                .deserializerByType(LocalDate.class, new LocalDateDeserializer())
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer());
    }

    @Bean(JsonUtil.BEAN_NAME)
    public JsonUtil objectMapperLoader(ObjectMapper objectMapper) {
        return new JsonUtil(objectMapper);
    }

    @Override
    public void run(String... args) throws Exception {
        if (this.applicationContext instanceof AnnotationConfigServletWebServerApplicationContext context) {
            context.removeBeanDefinition(JsonUtil.BEAN_NAME);
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}

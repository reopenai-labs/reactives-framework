package com.reopenai.reactives.grpc.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

/**
 * Created by Allen Huang
 */
@Data
@ConfigurationProperties("spring.grpc.client.target")
public class GrpcClientTargetProperties {

    private String template;

    private Set<String> excludeTemplates;

    private Map<String, String> spec;

}

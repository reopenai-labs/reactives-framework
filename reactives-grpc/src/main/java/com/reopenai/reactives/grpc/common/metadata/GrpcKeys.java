package com.reopenai.reactives.grpc.common.metadata;

import io.grpc.Context;
import io.grpc.Metadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;
import java.util.function.Function;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

/**
 * Created by Allen Huang
 */
@Getter
@RequiredArgsConstructor
public enum GrpcKeys {

    REFERER_SERVICE(
            Metadata.Key.of("Referer-Service", ASCII_STRING_MARSHALLER),
            Context.key("Referer-Service"),
            "Referer-Service",
            Object::toString,
            Function.identity()
    ),

    LOCALE(
            Metadata.Key.of("Locale", ASCII_STRING_MARSHALLER),
            Context.key("Locale"),
            Locale.class,
            data -> data instanceof Locale locale ? locale.toLanguageTag() : null,
            Locale::forLanguageTag
    ),

    ;

    private final Metadata.Key<String> metadataKey;

    private final Context.Key<String> contextKey;

    private final Object appKey;

    private final Function<Object, String> encode;

    private final Function<String, ?> decode;

}

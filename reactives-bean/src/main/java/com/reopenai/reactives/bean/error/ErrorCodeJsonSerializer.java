package com.reopenai.reactives.bean.error;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by Allen Huang
 */
class ErrorCodeJsonSerializer extends JsonSerializer<ErrorCode> {

    @Override
    public void serialize(ErrorCode errorCode, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(errorCode.getCode());
    }

}
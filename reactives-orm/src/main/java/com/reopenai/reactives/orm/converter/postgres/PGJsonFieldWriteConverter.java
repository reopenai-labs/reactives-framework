package com.reopenai.reactives.orm.converter.postgres;

import com.reopenai.reactives.orm.builtin.JsonField;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * Created by Allen Huang
 */
@WritingConverter
public class PGJsonFieldWriteConverter implements Converter<JsonField, Json> {

    @Override
    public Json convert(JsonField source) {
        return Json.of(source.toString());
    }

}

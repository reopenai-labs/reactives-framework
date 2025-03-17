package com.reopenai.reactives.orm.config;

import java.util.List;

/**
 * Created by Allen Huang
 */
public interface ORMConverterCustomizer {

    List<Object> customize(List<Object> converters);

}

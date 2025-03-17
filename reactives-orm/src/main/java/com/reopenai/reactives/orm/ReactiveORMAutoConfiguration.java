package com.reopenai.reactives.orm;

import com.reopenai.reactives.bean.enums.XEnum;
import com.reopenai.reactives.core.bench.FluxBenchMarkerAdvisor;
import com.reopenai.reactives.core.bench.MonoBenchMarkerAdvisor;
import com.reopenai.reactives.orm.aop.OrmBenchMarkerClassFilter;
import com.reopenai.reactives.orm.config.ORMConverterCustomizer;
import com.reopenai.reactives.orm.converter.EnumTypeHandlerLoader;
import com.reopenai.reactives.orm.converter.postgres.PGJsonFieldReadConverter;
import com.reopenai.reactives.orm.converter.postgres.PGJsonFieldWriteConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen Huang
 */
@ComponentScan
@AutoConfiguration
public class ReactiveORMAutoConfiguration {

    @Bean
    public MonoBenchMarkerAdvisor monoBenchMakerAdvisor() {
        return new MonoBenchMarkerAdvisor(new OrmBenchMarkerClassFilter());
    }

    @Bean
    public FluxBenchMarkerAdvisor fluxBenchMakerAdvisor() {
        return new FluxBenchMarkerAdvisor(new OrmBenchMarkerClassFilter());
    }

    @Bean
    @ConditionalOnMissingBean
    public R2dbcCustomConversions r2dbcCustomConversions(DatabaseClient databaseClient, ObjectProvider<ORMConverterCustomizer> customizers) throws Exception {
        R2dbcDialect dialect = DialectResolver.getDialect(databaseClient.getConnectionFactory());
        List<Object> converters = new ArrayList<>(dialect.getConverters());
        converters.addAll(R2dbcCustomConversions.STORE_CONVERTERS);
        return new R2dbcCustomConversions(
                CustomConversions.StoreConversions.of(dialect.getSimpleTypeHolder(), converters),
                createConverters(customizers)
        );
    }

    private List<Object> createConverters(ObjectProvider<ORMConverterCustomizer> customizers) throws Exception {
        List<Object> converters = new ArrayList<>();
        converters.add(new PGJsonFieldReadConverter());
        converters.add(new PGJsonFieldWriteConverter());
        EnumTypeHandlerLoader.loadHandlers(XEnum.class.getPackageName(), converters);
        customizers.stream().forEach(converter -> converter.customize(converters));
        return converters;
    }

}

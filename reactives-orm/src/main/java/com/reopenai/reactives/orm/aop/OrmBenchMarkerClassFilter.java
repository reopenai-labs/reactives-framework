package com.reopenai.reactives.orm.aop;

import com.reopenai.reactives.core.bench.BenchMarkerClassFilter;
import com.reopenai.reactives.orm.repository.RepositoryExtension;
import org.springframework.stereotype.Repository;

/**
 * Created by Allen Huang
 */
public class OrmBenchMarkerClassFilter extends BenchMarkerClassFilter {

    @Override
    public boolean matches(Class<?> clazz) {
        return matchRepository(clazz) && super.matches(clazz);
    }

    public boolean matchRepository(Class<?> clazz) {
        return clazz.isAnnotationPresent(Repository.class) ||
                clazz == RepositoryExtension.class ||
                org.springframework.data.repository.Repository.class.isAssignableFrom(clazz);
    }
}

package com.reopenai.reactives.core.bench;

import com.reopenai.reactives.core.aop.MonoMethodMatcher;

import java.lang.reflect.Method;

/**
 * Created by Allen Huang
 */
public class MonoBenchMarkerMethodMatcher extends MonoMethodMatcher {

    public static final MonoBenchMarkerMethodMatcher INSTANCE = new MonoBenchMarkerMethodMatcher();

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return super.matches(method, targetClass)
                && !method.isAnnotationPresent(BenchMakerIgnore.class);
    }

}
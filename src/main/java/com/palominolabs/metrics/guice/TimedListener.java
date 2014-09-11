package com.palominolabs.metrics.guice;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;

/**
 * A listener which adds method interceptors to timed methods.
 */
public class TimedListener implements TypeListener {
    private final MetricRegistry metricRegistry;

    TimedListener(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public <T> void hear(TypeLiteral<T> literal,
                         TypeEncounter<T> encounter) {
        Class<? super T> klass = literal.getRawType();

        do {
            for (Method method : klass.getDeclaredMethods()) {
                final MethodInterceptor interceptor = TimedInterceptor.forMethod(metricRegistry,
                                                                                 klass, method);
                if (interceptor != null) {
                    encounter.bindInterceptor(Matchers.only(method), interceptor);
                }
            }
        } while ( (klass = klass.getSuperclass() ) != null);
    }
}

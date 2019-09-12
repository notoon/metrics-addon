/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.metrics.internal;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;

/**
 * A listener which adds method interceptors to methods that should be instrumented for exceptions
 */
class ExceptionMeteredListener implements TypeListener {
    private final MetricRegistry metricRegistry;

    ExceptionMeteredListener(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Override
    public <T> void hear(TypeLiteral<T> literal,
            TypeEncounter<T> encounter) {
        final Class<?> klass = literal.getRawType();
        for (Method method : klass.getDeclaredMethods()) {
            final MethodInterceptor interceptor = ExceptionMeteredInterceptor.forMethod(
                    metricRegistry,
                    klass,
                    method);

            if (interceptor != null) {
                encounter.bindInterceptor(Matchers.only(method), interceptor);
            }
        }
    }
}

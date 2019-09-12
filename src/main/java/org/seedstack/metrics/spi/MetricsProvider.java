/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.metrics.spi;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import io.nuun.kernel.api.annotations.Facet;

/**
 * Can be request by plugins to get early access to the {@link HealthCheckRegistry} and the {@link MetricRegistry}.
 */
@Facet
public interface MetricsProvider {
    /**
     * @return the global {@link HealthCheckRegistry}.
     */
    HealthCheckRegistry getHealthCheckRegistry();

    /**
     * @return the global {@link MetricRegistry}.
     */
    MetricRegistry getMetricRegistry();
}

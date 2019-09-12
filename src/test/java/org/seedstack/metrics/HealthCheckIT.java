/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.collect.Sets;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.testing.junit4.SeedITRunner;

@RunWith(SeedITRunner.class)
public class HealthCheckIT {
    @Inject
    HealthCheckRegistry healthCheckRegistry;

    @Test
    public void health_checks_are_correctly_registered() throws Exception {
        assertThat(healthCheckRegistry.runHealthChecks().keySet()).isEqualTo(Sets.newHashSet(
                "org.seedstack.metrics.fixtures.FailingHealthCheck",
                "SuccessfulHealthCheck",
                "org.seedstack.metrics.fixtures.InjectedHealthCheck"
        ));
    }

    @Test
    public void health_checks_are_injected() throws Exception {
        assertThat(healthCheckRegistry.runHealthCheck("org.seedstack.metrics.fixtures.InjectedHealthCheck").isHealthy())
                .isEqualTo(true);
    }
}

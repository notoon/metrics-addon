/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.metrics;

import com.google.inject.Injector;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seedstack.seed.testing.LaunchMode;
import org.seedstack.seed.testing.LaunchWith;
import org.seedstack.seed.testing.junit4.SeedITRunner;

@RunWith(SeedITRunner.class)
@LaunchWith(mode = LaunchMode.PER_TEST)
public class MetricsIT {
    @Inject
    private Injector injector;

    @Test
    public void instrumented_classes_can_be_injected_multiple_times() throws Exception {
        for (int i = 0; i < 10; i++) {
            injector.getInstance(InstrumentedManually.class);
            injector.getInstance(InstrumentedWithCachedGauge.class);
            injector.getInstance(InstrumentedWithCounted.class);
            injector.getInstance(InstrumentedWithExceptionMetered.class);
            injector.getInstance(InstrumentedWithGauge.class);
            injector.getInstance(InstrumentedWithMetered.class);
            injector.getInstance(InstrumentedWithTimed.class);
        }
    }
}

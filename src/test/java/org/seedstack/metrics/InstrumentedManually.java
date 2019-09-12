/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Metric;
import org.seedstack.seed.Bind;

@Bind
public class InstrumentedManually {
    @Metric
    private Histogram histogram = new Histogram(new ExponentiallyDecayingReservoir());

    @Metric
    private Meter meter;

    @Metric
    private Timer timer;

    @Metric
    private Counter counter;

    @Metric
    private Histogram histogram2;

    public Histogram getHistogram() {
        return histogram;
    }

    public Meter getMeter() {
        return meter;
    }

    public Timer getTimer() {
        return timer;
    }

    public Counter getCounter() {
        return counter;
    }

    public Histogram getHistogram2() {
        return histogram2;
    }
}

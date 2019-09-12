/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.metrics;

import com.codahale.metrics.annotation.CachedGauge;
import org.seedstack.seed.Bind;

@Bind
public class InstrumentedWithCachedGauge {
    @CachedGauge(name = "cached_gauge_things", timeout = 1000)
    public String doAThing() {
        return "poop";
    }

    @CachedGauge(timeout = 1000)
    public String doAnotherThing() {
        return "anotherThing";
    }

    @CachedGauge(name = "cached_gauge_absoluteName", absolute = true, timeout = 1000)
    public String doAThingWithAbsoluteName() {
        return "anotherThingWithAbsoluteName";
    }
}

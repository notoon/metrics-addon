/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.metrics.fixtures;

import com.codahale.metrics.health.HealthCheck;
import org.seedstack.seed.diagnostic.DiagnosticManager;

import javax.inject.Inject;

public class InjectedHealthCheck extends HealthCheck {
    @Inject
    private DiagnosticManager diagnosticManager;

    @Override
    protected Result check() throws Exception {
        return diagnosticManager == null ? Result.unhealthy("not injected") : Result.healthy();
    }
}

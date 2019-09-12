/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.metrics.internal;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.inject.Inject;
import org.fusesource.jansi.Ansi;
import org.seedstack.seed.command.CommandDefinition;
import org.seedstack.seed.command.Option;
import org.seedstack.seed.command.PrettyCommand;

/**
 * This command dumps the status of the application health checks.
 */
@CommandDefinition(scope = "metrics", name = "health", description = "Runs health checks and provide a complete result")
public class HealthCommand implements PrettyCommand<Map<String, HealthCheck.Result>> {
    @Option(name = "n", longName = "name", description = "Name of the health check to run", hasArgument = true)
    String name;

    @Inject
    private HealthCheckRegistry healthCheckRegistry;

    @Override
    public Map<String, HealthCheck.Result> execute(Object object) throws Exception {
        if (name != null) {
            return ImmutableMap.of(name, healthCheckRegistry.runHealthCheck(name));
        } else {
            return healthCheckRegistry.runHealthChecks();
        }
    }

    @Override
    public String prettify(Map<String, HealthCheck.Result> results) {
        Ansi val = Ansi.ansi();

        for (Map.Entry<String, HealthCheck.Result> entry : results.entrySet()) {
            if (entry.getValue().isHealthy()) {
                val.fgBright(Ansi.Color.GREEN).a("OK  ").reset();
            } else {
                val.fgBright(Ansi.Color.RED).a("FAIL").reset();
            }

            val.a("\t").a(entry.getKey());

            String message = entry.getValue().getMessage();
            if (message != null) {
                val.a("\t").a(message);
            }

            val.newline();
        }

        return val.toString();
    }
}

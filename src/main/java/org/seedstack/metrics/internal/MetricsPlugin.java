/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.metrics.internal;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.CpuProfileServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.codahale.metrics.servlets.PingServlet;
import com.codahale.metrics.servlets.ThreadDumpServlet;
import com.google.common.collect.Lists;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.Context;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import org.seedstack.metrics.MetricsConfig;
import org.seedstack.metrics.spi.MetricsProvider;
import org.seedstack.seed.core.SeedRuntime;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.seed.web.spi.FilterDefinition;
import org.seedstack.seed.web.spi.ListenerDefinition;
import org.seedstack.seed.web.spi.ServletDefinition;
import org.seedstack.seed.web.spi.WebProvider;
import org.seedstack.shed.reflect.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This plugin provides support for the Metrics monitoring library (https://dropwizard.github.io/metrics/).
 */
public class MetricsPlugin extends AbstractSeedPlugin implements WebProvider, MetricsProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsPlugin.class);
    private static boolean JAVA_SERVLET_PRESENT = Classes.optional("javax.servlet.ServletContext").isPresent();
    private static boolean METRICS_SERVLET_PRESENT = Classes.optional("com.codahale.metrics.servlets.MetricsServlet")
            .isPresent();
    private final Set<Class<? extends HealthCheck>> healthCheckClasses = new HashSet<>();
    private final Set<Class<? extends HttpServlet>> servletClasses = new HashSet<>();
    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
    private boolean servletsEnabled;
    private MetricsConfig config;
    @Inject
    private Map<String, HealthCheck> healthChecks;

    @Override
    public String name() {
        return "metrics";
    }

    @Override
    protected void setup(SeedRuntime seedRuntime) {
        if (JAVA_SERVLET_PRESENT) {
            ServletContext servletContext = seedRuntime.contextAs(ServletContext.class);
            if (servletContext != null) {
                servletContext.setAttribute(
                        "com.codahale.metrics.servlets.MetricsServlet.registry",
                        metricRegistry);
                servletContext.setAttribute(
                        "com.codahale.metrics.servlets.HealthCheckServlet.registry",
                        healthCheckRegistry);
                servletsEnabled = true;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public InitState initialize(InitContext initContext) {
        config = getConfiguration(MetricsConfig.class);
        Map<Class<?>, Collection<Class<?>>> scannedSubTypesByParentClass = initContext.scannedSubTypesByParentClass();

        for (Class<?> candidate : scannedSubTypesByParentClass.get(HealthCheck.class)) {
            if (HealthCheck.class.isAssignableFrom(candidate)) {
                healthCheckClasses.add((Class<? extends HealthCheck>) candidate);
                LOGGER.debug("Detected health check class {}", candidate.getCanonicalName());
            }
        }

        LOGGER.info("Detected {} health check class(es)", healthCheckClasses.size());

        return InitState.INITIALIZED;
    }

    @Override
    public void start(Context context) {
        for (Map.Entry<String, HealthCheck> healthCheckEntry : healthChecks.entrySet()) {
            healthCheckRegistry.register(healthCheckEntry.getKey(), healthCheckEntry.getValue());
        }
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder().subtypeOf(HealthCheck.class).build();
    }

    @Override
    public Object nativeUnitModule() {
        return new MetricsModule(metricRegistry, healthCheckRegistry, healthCheckClasses, servletClasses);
    }

    public HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    @Override
    public List<ServletDefinition> servlets() {
        ArrayList<ServletDefinition> servletDefinitions = Lists.newArrayList();
        if (servletsEnabled && config.servlets().isEnabled() && METRICS_SERVLET_PRESENT) {
            MetricsConfig.ServletsConfig servlets = config.servlets();
            if (servlets.isAdmin()) {
                ServletDefinition def = new ServletDefinition("seed-metrics", AdminServlet.class);
                def.addMappings(servlets.getPath() + "/*");

                Map<String, String> initParams = new HashMap<>();
                initParams.put(AdminServlet.CPU_PROFILE_URI_PARAM_KEY, servlets.cpu().getPath());
                initParams.put(AdminServlet.HEALTHCHECK_URI_PARAM_KEY, servlets.health().getPath());
                initParams.put(AdminServlet.METRICS_URI_PARAM_KEY, servlets.metrics().getPath());
                initParams.put(AdminServlet.PING_URI_PARAM_KEY, servlets.ping().getPath());
                initParams.put(AdminServlet.THREADS_URI_PARAM_KEY, servlets.threads().getPath());
                def.addInitParameters(initParams);

                servletDefinitions.add(def);
                servletClasses.add(AdminServlet.class);
                LOGGER.info("All Metrics servlets exposed on {}", servlets.getPath() + "/*");
            } else {
                if (servlets.cpu().isEnabled()) {
                    servletDefinitions.add(buildServletDefinition("seed-metrics-cpu",
                            CpuProfileServlet.class,
                            servlets.cpu()));
                    servletClasses.add(CpuProfileServlet.class);
                    LOGGER.info("Metrics cpu servlet exposed on {}", servlets.cpu().getFullPath());
                }
                if (servlets.health().isEnabled()) {
                    servletDefinitions.add(buildServletDefinition("seed-metrics-health",
                            HealthCheckServlet.class,
                            servlets.health()));
                    servletClasses.add(HealthCheckServlet.class);
                    LOGGER.info("Metrics healthcheck servlet exposed on {}", servlets.health().getFullPath());
                }
                if (servlets.metrics().isEnabled()) {
                    servletDefinitions.add(buildServletDefinition("seed-metrics-metrics",
                            MetricsServlet.class,
                            servlets.metrics()));
                    servletClasses.add(MetricsServlet.class);
                    LOGGER.info("Metrics servlet exposed on {}", servlets.metrics().getFullPath());
                }
                if (servlets.ping().isEnabled()) {
                    servletDefinitions.add(buildServletDefinition("seed-metrics-ping",
                            PingServlet.class,
                            servlets.ping()));
                    servletClasses.add(PingServlet.class);
                    LOGGER.info("Metrics ping servlet exposed on {}", servlets.ping().getFullPath());
                }
                if (servlets.threads().isEnabled()) {
                    servletDefinitions.add(buildServletDefinition("seed-metrics-threads",
                            ThreadDumpServlet.class,
                            servlets.threads()));
                    servletClasses.add(ThreadDumpServlet.class);
                    LOGGER.info("Metrics threads servlet exposed on {}", servlets.threads().getFullPath());
                }
            }
        }
        return servletDefinitions;
    }

    private ServletDefinition buildServletDefinition(String name, Class<? extends HttpServlet> servletClass,
            MetricsConfig.ServletConfig config) {
        ServletDefinition servletDefinition = new ServletDefinition(name, servletClass);
        servletDefinition.addMappings(config.getFullPath());
        servletClasses.add(servletClass);
        return servletDefinition;
    }

    @Override
    public List<FilterDefinition> filters() {
        return Lists.newArrayList();
    }

    @Override
    public List<ListenerDefinition> listeners() {
        return Lists.newArrayList();
    }
}

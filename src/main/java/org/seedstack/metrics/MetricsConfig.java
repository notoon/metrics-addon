/*
 * Copyright © 2013-2019, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.seedstack.metrics;

import com.codahale.metrics.servlets.AdminServlet;
import org.seedstack.coffig.Config;
import org.seedstack.coffig.SingleValue;

@Config("metrics")
public class MetricsConfig {
    private ServletsConfig servletsConfig = new ServletsConfig();

    public ServletsConfig servlets() {
        return servletsConfig;
    }

    @Config("servlets")
    public static class ServletsConfig {
        @SingleValue
        private boolean enabled = true;
        private String path = "/seed-metrics";
        private boolean admin = false;
        private ServletConfig cpu = new ServletConfig(this, AdminServlet.DEFAULT_CPU_PROFILE_URI, false);
        private ServletConfig health = new ServletConfig(this, AdminServlet.DEFAULT_HEALTHCHECK_URI, true);
        private ServletConfig metrics = new ServletConfig(this, AdminServlet.DEFAULT_METRICS_URI, true);
        private ServletConfig ping = new ServletConfig(this, AdminServlet.DEFAULT_PING_URI, true);
        private ServletConfig threads = new ServletConfig(this, AdminServlet.DEFAULT_THREADS_URI, false);

        public boolean isEnabled() {
            return enabled;
        }

        public ServletsConfig setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getPath() {
            return path;
        }

        public ServletsConfig setPath(String path) {
            this.path = path;
            return this;
        }

        public boolean isAdmin() {
            return admin;
        }

        public ServletsConfig setAdmin(boolean admin) {
            this.admin = admin;
            return this;
        }

        public ServletConfig cpu() {
            return cpu;
        }

        public ServletConfig health() {
            return health;
        }

        public ServletConfig metrics() {
            return metrics;
        }

        public ServletConfig ping() {
            return ping;
        }

        public ServletConfig threads() {
            return threads;
        }
    }

    public static class ServletConfig {
        private final ServletsConfig parent;
        @SingleValue
        private boolean enabled;
        private String path;

        public ServletConfig(ServletsConfig parent, String path, boolean enabled) {
            this.parent = parent;
            this.path = path;
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public ServletConfig setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getPath() {
            return path;
        }

        public ServletConfig setPath(String path) {
            this.path = path;
            return this;
        }

        public String getFullPath() {
            String parentPath = parent.getPath();
            if (parentPath.endsWith("/") && path.startsWith("/")) {
                return parentPath + path.substring(1);
            } else if (!parentPath.endsWith("/") && !path.startsWith("/") && !path.isEmpty()) {
                return parentPath + "/" + path;
            } else {
                return parentPath + path;
            }
        }
    }
}

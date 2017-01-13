/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.metrics.internal;

import org.seedstack.shed.exception.ErrorCode;

enum MetricsErrorCode implements ErrorCode {
    ERROR_ACCESSING_METRIC_FIELD,
    ERROR_EVALUATING_METRIC,
    HEALTH_CHECK_REGISTRY_NOT_FOUND,
    INVALID_METRIC_TYPE,
    METRICS_REGISTRY_NOT_FOUND
}

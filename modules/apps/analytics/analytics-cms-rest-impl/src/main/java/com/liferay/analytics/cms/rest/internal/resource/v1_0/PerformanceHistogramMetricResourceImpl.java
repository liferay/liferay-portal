/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceHistogramMetricResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/performance-histogram-metric.properties",
	scope = ServiceScope.PROTOTYPE,
	service = PerformanceHistogramMetricResource.class
)
public class PerformanceHistogramMetricResourceImpl
	extends BasePerformanceHistogramMetricResourceImpl {
}
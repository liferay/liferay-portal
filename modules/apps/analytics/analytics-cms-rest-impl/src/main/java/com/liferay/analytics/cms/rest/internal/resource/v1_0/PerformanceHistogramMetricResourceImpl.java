/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceHistogramMetric;
import com.liferay.analytics.cms.rest.internal.client.AnalyticsCloudClient;
import com.liferay.analytics.cms.rest.internal.depot.entry.util.DepotEntryUtil;
import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceHistogramMetricResource;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.util.AnalyticsSettingsManagerUtil;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.Http;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
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

	@Override
	public PerformanceHistogramMetric getPerformanceHistogramMetric(
			Long[] depotEntryIds, Integer rangeKey, String selectedMetric)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		AnalyticsSettingsManagerUtil.checkAnalyticsEnabled(
			_analyticsSettingsManager, contextCompany.getCompanyId());

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_http);

		return analyticsCloudClient.getPerformanceHistogramMetric(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			Arrays.asList(
				DepotEntryUtil.getGroupIds(
					DepotEntryUtil.getDepotEntries(
						contextCompany.getCompanyId(), depotEntryIds))),
			rangeKey, selectedMetric);
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Http _http;

}
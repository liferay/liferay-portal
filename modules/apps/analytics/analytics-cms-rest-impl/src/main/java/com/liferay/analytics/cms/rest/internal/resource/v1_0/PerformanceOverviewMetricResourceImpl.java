/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceOverviewMetric;
import com.liferay.analytics.cms.rest.internal.client.AnalyticsCloudClient;
import com.liferay.analytics.cms.rest.internal.depot.entry.util.DepotEntryUtil;
import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceOverviewMetricResource;
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
	properties = "OSGI-INF/liferay/rest/v1_0/performance-overview-metric.properties",
	scope = ServiceScope.PROTOTYPE,
	service = PerformanceOverviewMetricResource.class
)
public class PerformanceOverviewMetricResourceImpl
	extends BasePerformanceOverviewMetricResourceImpl {

	@Override
	public PerformanceOverviewMetric getPerformanceOverviewMetric(
			Long[] depotEntryIds, Integer rangeKey)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		AnalyticsSettingsManagerUtil.checkAnalyticsEnabled(
			_analyticsSettingsManager, contextCompany.getCompanyId());

		Long[] groupIds = DepotEntryUtil.getGroupIds(
			DepotEntryUtil.getDepotEntries(
				contextCompany.getCompanyId(), depotEntryIds));

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_http);

		return analyticsCloudClient.getPerformanceOverviewMetric(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			Arrays.asList(groupIds), rangeKey);
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Http _http;

}
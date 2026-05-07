/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.internal.resource.v1_0;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource;
import com.liferay.portal.kernel.util.Http;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.SiteHistogramMetric;
import com.liferay.site.dsr.analytics.rest.internal.client.AnalyticsCloudClient;
import com.liferay.site.dsr.analytics.rest.resource.v1_0.VisitorsSiteHistogramMetricResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gianmarco Brunialti
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/visitors-site-histogram-metric.properties",
	scope = ServiceScope.PROTOTYPE,
	service = VisitorsSiteHistogramMetricResource.class
)
public class VisitorsSiteHistogramMetricResourceImpl
	extends BaseVisitorsSiteHistogramMetricResourceImpl {

	@Override
	public SiteHistogramMetric getVisitorsSiteHistogramMetric(
			String[] groupIds, String interval, String rangeEnd,
			Integer rangeKey, String rangeStart)
		throws Exception {

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_channelResourceFactory, _http, contextUser);

		return analyticsCloudClient.getVisitorsSiteHistogramMetric(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			groupIds, interval, rangeEnd, rangeKey, rangeStart);
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private volatile ChannelResource.Factory _channelResourceFactory;

	@Reference
	private Http _http;

}
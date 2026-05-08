/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.internal.resource.v1_0;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.resource.v1_0.ChannelResource;
import com.liferay.portal.kernel.util.Http;
import com.liferay.site.dsr.analytics.rest.dto.v1_0.DocumentsMetric;
import com.liferay.site.dsr.analytics.rest.internal.client.AnalyticsCloudClient;
import com.liferay.site.dsr.analytics.rest.resource.v1_0.DocumentsMetricResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Gianmarco Brunialti
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/documents-metric.properties",
	scope = ServiceScope.PROTOTYPE, service = DocumentsMetricResource.class
)
public class DocumentsMetricResourceImpl
	extends BaseDocumentsMetricResourceImpl {

	@Override
	public DocumentsMetric getDocumentsMetric(
			String[] groupIds, String keywords, String rangeEnd,
			Integer rangeKey, String rangeStart, Integer size,
			String sortColumn, String sortType, Integer start)
		throws Exception {

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_channelResourceFactory, _http, contextUser);

		return analyticsCloudClient.getDocumentsMetric(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			groupIds, keywords, rangeEnd, rangeKey, rangeStart, size,
			sortColumn, sortType, start);
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private volatile ChannelResource.Factory _channelResourceFactory;

	@Reference
	private Http _http;

}

// LIFERAY-REST-BUILDER-HASH:1243811315
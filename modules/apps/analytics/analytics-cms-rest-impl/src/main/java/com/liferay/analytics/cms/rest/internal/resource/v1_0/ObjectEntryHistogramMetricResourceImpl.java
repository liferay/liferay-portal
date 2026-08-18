/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryHistogramMetric;
import com.liferay.analytics.cms.rest.internal.client.AnalyticsCloudClient;
import com.liferay.analytics.cms.rest.resource.v1_0.ObjectEntryHistogramMetricResource;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.util.AnalyticsSettingsManagerUtil;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.Http;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/object-entry-histogram-metric.properties",
	scope = ServiceScope.PROTOTYPE,
	service = ObjectEntryHistogramMetricResource.class
)
public class ObjectEntryHistogramMetricResourceImpl
	extends BaseObjectEntryHistogramMetricResourceImpl {

	@Override
	public ObjectEntryHistogramMetric getObjectEntryHistogramMetric(
			Long groupId, Long objectEntryId, Integer rangeKey,
			String[] selectedMetrics)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		AnalyticsSettingsManagerUtil.checkAnalyticsEnabled(
			_analyticsSettingsManager, contextCompany.getCompanyId());

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_http);

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
			objectEntryId);

		return analyticsCloudClient.getObjectEntryHistogramMetric(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			objectEntry.getExternalReferenceCode(), groupId, rangeKey,
			selectedMetrics);
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Http _http;

	@Reference
	private ObjectEntryService _objectEntryService;

}
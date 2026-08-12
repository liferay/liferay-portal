/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceMetric;
import com.liferay.analytics.cms.rest.internal.client.AnalyticsCloudClient;
import com.liferay.analytics.cms.rest.internal.depot.entry.util.DepotEntryUtil;
import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceMetricResource;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.util.AnalyticsSettingsManagerUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.InputStream;

import java.time.LocalDate;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/performance-metric.properties",
	scope = ServiceScope.PROTOTYPE, service = PerformanceMetricResource.class
)
public class PerformanceMetricResourceImpl
	extends BasePerformanceMetricResourceImpl {

	@Override
	public PerformanceMetric getPerformanceMetric(
			Long[] depotEntryIds, String groupBy, String metricType,
			Integer rangeKey)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		_validateMetricType(metricType);

		AnalyticsSettingsManagerUtil.checkAnalyticsEnabled(
			_analyticsSettingsManager, contextCompany.getCompanyId());

		Long[] groupIds = DepotEntryUtil.getGroupIds(
			DepotEntryUtil.getDepotEntries(
				contextCompany.getCompanyId(), depotEntryIds));

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_http);

		return analyticsCloudClient.getPerformanceMetric(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			Arrays.asList(groupIds), metricType, _getPath(groupBy), rangeKey);
	}

	@Override
	public Response getPerformanceMetricExport(
			Long[] depotEntryIds, String groupBy, String metricType,
			Integer rangeKey)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		_validateMetricType(metricType);

		AnalyticsSettingsManagerUtil.checkAnalyticsEnabled(
			_analyticsSettingsManager, contextCompany.getCompanyId());

		Long[] groupIds = DepotEntryUtil.getGroupIds(
			DepotEntryUtil.getDepotEntries(
				contextCompany.getCompanyId(), depotEntryIds));

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_http);

		InputStream inputStream = analyticsCloudClient.getInputStream(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			null, Arrays.asList(groupIds), null, metricType,
			_getPath(groupBy) + "/export", rangeKey, null);

		return Response.ok(
			(StreamingOutput)outputStream -> StreamUtil.transfer(
				inputStream, outputStream)
		).header(
			"Content-Disposition",
			StringBundler.concat(
				"attachment; filename=performance-metric-",
				StringUtil.toLowerCase(groupBy), "-", LocalDate.now(), ".csv")
		).build();
	}

	private String _getPath(String groupBy) {
		if (StringUtil.equalsIgnoreCase(groupBy, "categories")) {
			return "/categories";
		}

		if (StringUtil.equalsIgnoreCase(groupBy, "location")) {
			return "/geolocation";
		}

		throw new BadRequestException("Invalid group by: " + groupBy);
	}

	private void _validateMetricType(String metricType) {
		if (!StringUtil.equalsIgnoreCase(metricType, "downloadsMetric") &&
			!StringUtil.equalsIgnoreCase(metricType, "impressionsMetric") &&
			!StringUtil.equalsIgnoreCase(metricType, "readsMetric") &&
			!StringUtil.equalsIgnoreCase(metricType, "viewsMetric")) {

			throw new BadRequestException("Invalid metric type: " + metricType);
		}
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Http _http;

}
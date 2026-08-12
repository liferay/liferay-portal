/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.resource.v1_0;

import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceAssetConsumption;
import com.liferay.analytics.cms.rest.internal.client.AnalyticsCloudClient;
import com.liferay.analytics.cms.rest.internal.depot.entry.util.DepotEntryUtil;
import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceAssetConsumptionResource;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.analytics.settings.rest.util.AnalyticsSettingsManagerUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.BadRequestException;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/performance-asset-consumption.properties",
	scope = ServiceScope.PROTOTYPE,
	service = PerformanceAssetConsumptionResource.class
)
public class PerformanceAssetConsumptionResourceImpl
	extends BasePerformanceAssetConsumptionResourceImpl {

	@Override
	public PerformanceAssetConsumption getPerformanceAssetConsumption(
			Long categoryId, Long[] depotEntryIds, String groupBy,
			Integer rangeKey, Long structureId, Long tagId, Long vocabularyId,
			Pagination pagination)
		throws Exception {

		LicenseManagerUtil.checkFreeTier();

		_validateGroupBy(groupBy);

		AnalyticsSettingsManagerUtil.checkAnalyticsEnabled(
			_analyticsSettingsManager, contextCompany.getCompanyId());

		Long[] groupIds = DepotEntryUtil.getGroupIds(
			DepotEntryUtil.getDepotEntries(
				contextCompany.getCompanyId(), depotEntryIds));

		String objectType = null;

		if (structureId != null) {
			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.fetchObjectDefinition(
					structureId);

			if (objectDefinition != null) {
				objectType = objectDefinition.getName();
			}
		}

		AnalyticsCloudClient analyticsCloudClient = new AnalyticsCloudClient(
			_http, _objectDefinitionLocalService);

		return analyticsCloudClient.getPerformanceAssetConsumption(
			_analyticsSettingsManager.getAnalyticsConfiguration(
				contextCompany.getCompanyId()),
			categoryId, groupBy, Arrays.asList(groupIds),
			contextAcceptLanguage.getPreferredLocale(), "viewsMetric",
			objectType, pagination.getPage() - 1, rangeKey,
			pagination.getPageSize(), tagId, vocabularyId);
	}

	private void _validateGroupBy(String groupBy) {
		if (!StringUtil.equalsIgnoreCase(groupBy, "category") &&
			!StringUtil.equalsIgnoreCase(groupBy, "structure") &&
			!StringUtil.equalsIgnoreCase(groupBy, "tag") &&
			!StringUtil.equalsIgnoreCase(groupBy, "vocabulary")) {

			throw new BadRequestException("Invalid group by: " + groupBy);
		}
	}

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Http _http;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}
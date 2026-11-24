/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.rest.dto.v1_0.CampaignMetric;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.resource.v1_0.CampaignMetricResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Nilton Vieira
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/campaign-metric.properties",
	scope = ServiceScope.PROTOTYPE, service = CampaignMetricResource.class
)
public class CampaignMetricResourceImpl extends BaseCampaignMetricResourceImpl {

	@Override
	public Page<CampaignMetric> getWorkspaceGroupChannelCampaignMetricsPage(
			Long groupId, String channelId, String rangeEnd, String rangeKey,
			String rangeStart, String[] selectedMetrics)
		throws Exception {

		return Page.of(
			transform(
				_contactsEngineClient.getCampaignMetrics(
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					null, GetterUtil.getLong(channelId), rangeEnd,
					TimeRange.getRangeKey(rangeKey), rangeStart,
					StringUtil.merge(selectedMetrics, StringPool.COMMA)),
				campaignMetric -> _campaignMetricDTOConverter.toDTO(
					new FaroDTOConverterContext(
						contextAcceptLanguage.isAcceptAllLanguages(),
						campaignMetric.getMetricType(),
						contextAcceptLanguage.getPreferredLocale()),
					campaignMetric)));
	}

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.CampaignMetricDTOConverter)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.CampaignMetric,
		 CampaignMetric> _campaignMetricDTOConverter;

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.VisitFrequencyMetric;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelVisitFrequencyMetricResponse;
import com.liferay.osb.faro.rest.resource.v1_0.VisitFrequencyMetricResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Caio Pinheiro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/visit-frequency-metric.properties",
	scope = ServiceScope.PROTOTYPE, service = VisitFrequencyMetricResource.class
)
public class VisitFrequencyMetricResourceImpl
	extends BaseVisitFrequencyMetricResourceImpl {

	@Override
	public VisitFrequencyMetric getWorkspaceGroupChannelVisitFrequencyMetric(
			Long groupId, String channelId, String rangeEnd, String rangeKey,
			String rangeStart)
		throws Exception {

		GetWorkspaceGroupChannelVisitFrequencyMetricResponse
			getWorkspaceGroupChannelVisitFrequencyMetricResponse =
				_faroGraphQLClient.execute(
					GetWorkspaceGroupChannelVisitFrequencyMetricResponse.class,
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					"getWorkspaceGroupChannelVisitFrequencyMetric",
					HashMapBuilder.<String, Object>put(
						"channelId", channelId
					).put(
						"rangeEnd", rangeEnd
					).put(
						"rangeKey", TimeRange.getRangeKey(rangeKey)
					).put(
						"rangeStart", rangeStart
					).build());

		GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag
			visitFrequencyBag =
				getWorkspaceGroupChannelVisitFrequencyMetricResponse.
					getVisitFrequencyBag();

		if (visitFrequencyBag == null) {
			visitFrequencyBag =
				new GetWorkspaceGroupChannelVisitFrequencyMetricResponse.
					VisitFrequencyBag();
		}

		return _visitFrequencyMetricDTOConverter.toDTO(
			new FaroDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), null,
				contextAcceptLanguage.getPreferredLocale()),
			visitFrequencyBag);
	}

	@Reference
	private FaroGraphQLClient _faroGraphQLClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.VisitFrequencyMetricDTOConverter)"
	)
	private DTOConverter
		<GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag,
		 VisitFrequencyMetric> _visitFrequencyMetricDTOConverter;

}
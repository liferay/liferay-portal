/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.VisitorMetric;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelVisitorMetricResponse;
import com.liferay.osb.faro.rest.resource.v1_0.VisitorMetricResource;
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
	properties = "OSGI-INF/liferay/rest/v1_0/visitor-metric.properties",
	scope = ServiceScope.PROTOTYPE, service = VisitorMetricResource.class
)
public class VisitorMetricResourceImpl extends BaseVisitorMetricResourceImpl {

	@Override
	public VisitorMetric getWorkspaceGroupChannelVisitorMetric(
			Long groupId, String channelId, String rangeKey, String segmentId)
		throws Exception {

		GetWorkspaceGroupChannelVisitorMetricResponse
			getWorkspaceGroupChannelVisitorMetricResponse =
				_faroGraphQLClient.execute(
					GetWorkspaceGroupChannelVisitorMetricResponse.class,
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					"getWorkspaceGroupChannelVisitorMetric",
					HashMapBuilder.<String, Object>put(
						"channelId", channelId
					).put(
						"rangeKey", TimeRange.getRangeKey(rangeKey)
					).put(
						"segmentId", segmentId
					).build());

		GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric
			visitorMetric =
				getWorkspaceGroupChannelVisitorMetricResponse.
					getVisitorMetric();

		if (visitorMetric == null) {
			visitorMetric =
				new GetWorkspaceGroupChannelVisitorMetricResponse.
					VisitorMetric();
		}

		return _visitorMetricDTOConverter.toDTO(
			new FaroDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), null,
				contextAcceptLanguage.getPreferredLocale()),
			visitorMetric);
	}

	@Reference
	private FaroGraphQLClient _faroGraphQLClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.VisitorMetricDTOConverter)"
	)
	private DTOConverter
		<GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric,
		 VisitorMetric> _visitorMetricDTOConverter;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.rest.dto.v1_0.EventMetric;
import com.liferay.osb.faro.rest.internal.dto.v1_0.converter.FaroDTOConverterContext;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventMetricsResponse;
import com.liferay.osb.faro.rest.resource.v1_0.EventMetricResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/event-metric.properties",
	scope = ServiceScope.PROTOTYPE, service = EventMetricResource.class
)
public class EventMetricResourceImpl extends BaseEventMetricResourceImpl {

	@Override
	public EventMetric getWorkspaceGroupChannelAccountEventMetric(
			Long groupId, String channelId, String accountId, String interval,
			String rangeEnd, String rangeKey, String rangeStart, String search)
		throws Exception {

		return _getEventMetric(
			groupId, accountId, channelId, "", interval, rangeEnd, rangeKey,
			rangeStart, search);
	}

	@Override
	public EventMetric getWorkspaceGroupChannelIndividualEventMetric(
			Long groupId, String channelId, String individualId,
			String interval, String rangeEnd, String rangeKey,
			String rangeStart, String search)
		throws Exception {

		return _getEventMetric(
			groupId, null, channelId, individualId, interval, rangeEnd,
			rangeKey, rangeStart, search);
	}

	private EventMetric _getEventMetric(
			Long groupId, String accountId, String channelId, String entityId,
			String interval, String rangeEnd, String rangeKey,
			String rangeStart, String search)
		throws Exception {

		GetWorkspaceGroupChannelEventMetricsResponse
			getWorkspaceGroupChannelEventMetricsResponse =
				_faroGraphQLClient.execute(
					GetWorkspaceGroupChannelEventMetricsResponse.class,
					_faroProjectLocalService.getFaroProjectByGroupId(groupId),
					"getWorkspaceGroupChannelEventMetrics",
					HashMapBuilder.<String, Object>put(
						"accountId", accountId
					).put(
						"channelId", channelId
					).put(
						"entityId", entityId
					).put(
						"entityType", _ENTITY_TYPE_INDIVIDUAL
					).put(
						"interval", Interval.getGraphQLInterval(interval)
					).put(
						"keywords", search
					).put(
						"rangeEnd", rangeEnd
					).put(
						"rangeKey", TimeRange.getRangeKey(rangeKey)
					).put(
						"rangeStart", rangeStart
					).build());

		return _eventMetricDTOConverter.toDTO(
			new FaroDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), entityId,
				contextAcceptLanguage.getPreferredLocale()),
			getWorkspaceGroupChannelEventMetricsResponse.getEventMetric());
	}

	private static final String _ENTITY_TYPE_INDIVIDUAL = "INDIVIDUAL";

	@Reference(
		target = "(component.name=com.liferay.osb.faro.rest.internal.dto.v1_0.converter.EventMetricDTOConverter)"
	)
	private DTOConverter
		<GetWorkspaceGroupChannelEventMetricsResponse.EventMetric, EventMetric>
			_eventMetricDTOConverter;

	@Reference
	private FaroGraphQLClient _faroGraphQLClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

}
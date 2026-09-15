/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.EventMetric;
import com.liferay.osb.faro.rest.dto.v1_0.Metric;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventMetricsResponse;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventMetricsResponse$EventMetric",
	service = DTOConverter.class
)
public class EventMetricDTOConverter
	implements DTOConverter
		<GetWorkspaceGroupChannelEventMetricsResponse.EventMetric,
		 EventMetric> {

	@Override
	public String getContentType() {
		return EventMetric.class.getSimpleName();
	}

	@Override
	public EventMetric toDTO(
		DTOConverterContext dtoConverterContext,
		GetWorkspaceGroupChannelEventMetricsResponse.EventMetric eventMetric) {

		if (eventMetric == null) {
			return null;
		}

		return new EventMetric() {
			{
				setTotalEvents(
					() -> _metricDTOConverter.toDTO(
						dtoConverterContext,
						eventMetric.getTotalEventsMetric()));
				setTotalSessions(
					() -> _metricDTOConverter.toDTO(
						dtoConverterContext,
						eventMetric.getTotalSessionsMetric()));
			}
		};
	}

	@Reference(
		target = "(dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelEventMetricsResponse$Metric)"
	)
	private DTOConverter
		<GetWorkspaceGroupChannelEventMetricsResponse.Metric, Metric>
			_metricDTOConverter;

}
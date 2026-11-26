/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.VisitFrequencyMetric;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelVisitFrequencyMetricResponse;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Caio Pinheiro
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelVisitFrequencyMetricResponse$VisitFrequencyBag",
	service = DTOConverter.class
)
public class VisitFrequencyMetricDTOConverter
	implements DTOConverter
		<GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag,
		 VisitFrequencyMetric> {

	@Override
	public String getContentType() {
		return VisitFrequencyMetric.class.getSimpleName();
	}

	@Override
	public VisitFrequencyMetric toDTO(
		DTOConverterContext dtoConverterContext,
		GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag
			visitFrequencyBag) {

		if (visitFrequencyBag == null) {
			return null;
		}

		Map<String, Long> counts = _getCounts(
			visitFrequencyBag.getCompositions());

		return new VisitFrequencyMetric() {
			{
				setBiWeeklyVisitors(
					() -> GetterUtil.getLong(counts.get("bi-weekly")));
				setDailyVisitors(() -> GetterUtil.getLong(counts.get("daily")));
				setMonthlyVisitors(
					() -> GetterUtil.getLong(counts.get("monthly")));
				setOccasionalVisitors(
					() -> GetterUtil.getLong(counts.get("occasional")));
				setVisitors(
					() -> GetterUtil.getLong(
						visitFrequencyBag.getTotalCount()));
				setWeeklyVisitors(
					() -> GetterUtil.getLong(counts.get("weekly")));
			}
		};
	}

	private Map<String, Long> _getCounts(
		List<GetWorkspaceGroupChannelVisitFrequencyMetricResponse.Composition>
			compositions) {

		Map<String, Long> counts = new HashMap<>();

		if (compositions == null) {
			return counts;
		}

		for (GetWorkspaceGroupChannelVisitFrequencyMetricResponse.Composition
				composition : compositions) {

			counts.put(composition.getName(), composition.getCount());
		}

		return counts;
	}

}
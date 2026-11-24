/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.Trend;
import com.liferay.osb.faro.rest.dto.v1_0.CampaignMetric;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nilton Vieira
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.CampaignMetric",
	service = DTOConverter.class
)
public class CampaignMetricDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.CampaignMetric,
		 CampaignMetric> {

	@Override
	public String getContentType() {
		return CampaignMetric.class.getSimpleName();
	}

	@Override
	public CampaignMetric toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.CampaignMetric
			campaignMetric) {

		if (campaignMetric == null) {
			return null;
		}

		return new CampaignMetric() {
			{
				setMetricType(campaignMetric::getMetricType);
				setPreviousValue(campaignMetric::getPreviousValue);
				setTrendClassification(
					() -> {
						Trend trend = campaignMetric.getTrend();

						if (trend == null) {
							return null;
						}

						return trend.getTrendClassification();
					});
				setTrendPercentage(
					() -> {
						Trend trend = campaignMetric.getTrend();

						if (trend == null) {
							return null;
						}

						BigDecimal percentage = trend.getPercentage();

						if (percentage == null) {
							return null;
						}

						return percentage.doubleValue();
					});
				setValue(campaignMetric::getValue);
			}
		};
	}

}
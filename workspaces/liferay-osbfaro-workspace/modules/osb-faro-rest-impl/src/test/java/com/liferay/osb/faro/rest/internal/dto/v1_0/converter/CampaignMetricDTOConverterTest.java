/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.Trend;
import com.liferay.osb.faro.rest.dto.v1_0.CampaignMetric;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class CampaignMetricDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(_campaignMetricDTOConverter.toDTO(null, null));

		com.liferay.osb.faro.engine.client.model.CampaignMetric
			engineClientCampaignMetric = _getEngineClientCampaignMetric();

		Trend trend = new Trend();

		trend.setPercentage(BigDecimal.valueOf(RandomTestUtil.randomDouble()));
		trend.setTrendClassification(RandomTestUtil.randomString());

		engineClientCampaignMetric.setTrend(trend);

		CampaignMetric campaignMetric = _campaignMetricDTOConverter.toDTO(
			null, engineClientCampaignMetric);

		_assertCampaignMetric(engineClientCampaignMetric, campaignMetric);
		Assert.assertEquals(
			trend.getTrendClassification(),
			campaignMetric.getTrendClassification());

		BigDecimal percentage = trend.getPercentage();

		Assert.assertEquals(
			percentage.doubleValue(), campaignMetric.getTrendPercentage(), 0);

		engineClientCampaignMetric = _getEngineClientCampaignMetric();

		campaignMetric = _campaignMetricDTOConverter.toDTO(
			null, engineClientCampaignMetric);

		_assertCampaignMetric(engineClientCampaignMetric, campaignMetric);
		Assert.assertNull(campaignMetric.getTrendClassification());
		Assert.assertNull(campaignMetric.getTrendPercentage());
	}

	private void _assertCampaignMetric(
		com.liferay.osb.faro.engine.client.model.CampaignMetric
			engineClientCampaignMetric,
		CampaignMetric campaignMetric) {

		Assert.assertEquals(
			engineClientCampaignMetric.getMetricType(),
			campaignMetric.getMetricType());
		Assert.assertEquals(
			engineClientCampaignMetric.getPreviousValue(),
			campaignMetric.getPreviousValue());
		Assert.assertEquals(
			engineClientCampaignMetric.getValue(), campaignMetric.getValue());
	}

	private com.liferay.osb.faro.engine.client.model.CampaignMetric
		_getEngineClientCampaignMetric() {

		com.liferay.osb.faro.engine.client.model.CampaignMetric
			engineClientCampaignMetric =
				new com.liferay.osb.faro.engine.client.model.CampaignMetric();

		engineClientCampaignMetric.setMetricType(RandomTestUtil.randomString());
		engineClientCampaignMetric.setPreviousValue(
			RandomTestUtil.randomDouble());
		engineClientCampaignMetric.setValue(RandomTestUtil.randomDouble());

		return engineClientCampaignMetric;
	}

	private final CampaignMetricDTOConverter _campaignMetricDTOConverter =
		new CampaignMetricDTOConverter();

}
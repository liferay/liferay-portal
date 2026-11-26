/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.VisitorMetric;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelVisitorMetricResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Caio Pinheiro
 */
public class VisitorMetricDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(_visitorMetricDTOConverter.toDTO(null, null));

		_testToDTO();
		_testToDTOWithNullValues();
	}

	private GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric
		_createResponseVisitorMetric(Double totalSessionDuration) {

		GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric
			responseVisitorMetric =
				new GetWorkspaceGroupChannelVisitorMetricResponse.
					VisitorMetric();

		responseVisitorMetric.setAverageSessionDuration(
			RandomTestUtil.randomLong());
		responseVisitorMetric.setKnownVisitors(RandomTestUtil.randomLong());
		responseVisitorMetric.setTotalSessionDuration(totalSessionDuration);
		responseVisitorMetric.setVisitors(RandomTestUtil.randomLong());

		return responseVisitorMetric;
	}

	private void _testToDTO() {
		long totalSessionDuration =
			Integer.MAX_VALUE + (long)RandomTestUtil.randomInt();

		GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric
			responseVisitorMetric = _createResponseVisitorMetric(
				(double)totalSessionDuration);

		VisitorMetric visitorMetric = _visitorMetricDTOConverter.toDTO(
			null, responseVisitorMetric);

		Assert.assertEquals(
			responseVisitorMetric.getAverageSessionDuration(),
			visitorMetric.getAverageSessionDuration());
		Assert.assertEquals(
			responseVisitorMetric.getKnownVisitors(),
			visitorMetric.getKnownVisitors());
		Assert.assertEquals(
			Long.valueOf(totalSessionDuration),
			visitorMetric.getTotalSessionDuration());
		Assert.assertEquals(
			responseVisitorMetric.getVisitors(), visitorMetric.getVisitors());
	}

	private void _testToDTOWithNullValues() {
		VisitorMetric visitorMetric = _visitorMetricDTOConverter.toDTO(
			null,
			new GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric());

		Assert.assertEquals(
			Long.valueOf(0), visitorMetric.getAverageSessionDuration());
		Assert.assertEquals(Long.valueOf(0), visitorMetric.getKnownVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitorMetric.getTotalSessionDuration());
		Assert.assertEquals(Long.valueOf(0), visitorMetric.getVisitors());
	}

	private final VisitorMetricDTOConverter _visitorMetricDTOConverter =
		new VisitorMetricDTOConverter();

}
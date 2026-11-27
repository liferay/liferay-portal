/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.VisitFrequencyMetric;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelVisitFrequencyMetricResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Caio Pinheiro
 */
public class VisitFrequencyMetricDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(_visitFrequencyMetricDTOConverter.toDTO(null, null));

		_testToDTO();
		_testToDTOWithMissingBuckets();
		_testToDTOWithNullCompositions();
		_testToDTOWithUnknownBucket();
	}

	private void _assertZeroBuckets(VisitFrequencyMetric visitFrequencyMetric) {
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getBiWeeklyVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getDailyVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getMonthlyVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getOccasionalVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getWeeklyVisitors());
	}

	private GetWorkspaceGroupChannelVisitFrequencyMetricResponse.Composition
		_createComposition(long count, String name) {

		GetWorkspaceGroupChannelVisitFrequencyMetricResponse.Composition
			composition =
				new GetWorkspaceGroupChannelVisitFrequencyMetricResponse.
					Composition();

		composition.setCount(count);
		composition.setName(name);

		return composition;
	}

	private void _testToDTO() {
		GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag
			visitFrequencyBag =
				new GetWorkspaceGroupChannelVisitFrequencyMetricResponse.
					VisitFrequencyBag();

		long totalCount = RandomTestUtil.randomLong();

		visitFrequencyBag.setTotalCount(totalCount);

		long biWeeklyVisitors = RandomTestUtil.randomLong();
		long dailyVisitors = RandomTestUtil.randomLong();
		long monthlyVisitors = RandomTestUtil.randomLong();
		long occasionalVisitors = RandomTestUtil.randomLong();
		long weeklyVisitors = RandomTestUtil.randomLong();

		visitFrequencyBag.setCompositions(
			Arrays.asList(
				_createComposition(biWeeklyVisitors, "bi-weekly"),
				_createComposition(dailyVisitors, "daily"),
				_createComposition(monthlyVisitors, "monthly"),
				_createComposition(occasionalVisitors, "occasional"),
				_createComposition(weeklyVisitors, "weekly")));

		VisitFrequencyMetric visitFrequencyMetric =
			_visitFrequencyMetricDTOConverter.toDTO(null, visitFrequencyBag);

		Assert.assertEquals(
			Long.valueOf(biWeeklyVisitors),
			visitFrequencyMetric.getBiWeeklyVisitors());
		Assert.assertEquals(
			Long.valueOf(dailyVisitors),
			visitFrequencyMetric.getDailyVisitors());
		Assert.assertEquals(
			Long.valueOf(monthlyVisitors),
			visitFrequencyMetric.getMonthlyVisitors());
		Assert.assertEquals(
			Long.valueOf(occasionalVisitors),
			visitFrequencyMetric.getOccasionalVisitors());
		Assert.assertEquals(
			Long.valueOf(totalCount), visitFrequencyMetric.getVisitors());
		Assert.assertEquals(
			Long.valueOf(weeklyVisitors),
			visitFrequencyMetric.getWeeklyVisitors());
	}

	private void _testToDTOWithMissingBuckets() {
		GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag
			visitFrequencyBag =
				new GetWorkspaceGroupChannelVisitFrequencyMetricResponse.
					VisitFrequencyBag();

		long weeklyVisitors = RandomTestUtil.randomLong();

		visitFrequencyBag.setCompositions(
			Collections.singletonList(
				_createComposition(weeklyVisitors, "weekly")));
		visitFrequencyBag.setTotalCount(weeklyVisitors);

		VisitFrequencyMetric visitFrequencyMetric =
			_visitFrequencyMetricDTOConverter.toDTO(null, visitFrequencyBag);

		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getBiWeeklyVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getDailyVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getMonthlyVisitors());
		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getOccasionalVisitors());
		Assert.assertEquals(
			Long.valueOf(weeklyVisitors),
			visitFrequencyMetric.getWeeklyVisitors());
	}

	private void _testToDTOWithNullCompositions() {
		VisitFrequencyMetric visitFrequencyMetric =
			_visitFrequencyMetricDTOConverter.toDTO(
				null,
				new GetWorkspaceGroupChannelVisitFrequencyMetricResponse.
					VisitFrequencyBag());

		_assertZeroBuckets(visitFrequencyMetric);

		Assert.assertEquals(
			Long.valueOf(0), visitFrequencyMetric.getVisitors());
	}

	private void _testToDTOWithUnknownBucket() {
		GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag
			visitFrequencyBag =
				new GetWorkspaceGroupChannelVisitFrequencyMetricResponse.
					VisitFrequencyBag();

		visitFrequencyBag.setCompositions(
			Collections.singletonList(
				_createComposition(
					RandomTestUtil.randomLong(),
					RandomTestUtil.randomString())));

		_assertZeroBuckets(
			_visitFrequencyMetricDTOConverter.toDTO(null, visitFrequencyBag));
	}

	private final VisitFrequencyMetricDTOConverter
		_visitFrequencyMetricDTOConverter =
			new VisitFrequencyMetricDTOConverter();

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.machine.learning.internal.dispatch.executor;

import com.liferay.analytics.machine.learning.internal.recommendation.search.RecommendationIndexer;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Eudaldo Alonso
 */
public class
	AnalyticsDownloadMostViewedContentRecommendationDispatchTaskExecutorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceTrackerFactoryMockedStatic.close();
	}

	@Before
	public void setUp() {
		_analyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor.
			dispatchLogLocalService = _dispatchLogLocalService;

		ReflectionTestUtil.setFieldValue(
			_analyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor,
			"_recommendationIndexer", _recommendationIndexer);
	}

	@Test
	public void testGetResourceLastModifiedDate() {
		DispatchLog dispatchLog = Mockito.mock(DispatchLog.class);

		Date endDate = RandomTestUtil.nextDate();

		Mockito.when(
			dispatchLog.getEndDate()
		).thenReturn(
			endDate
		);

		long dispatchTriggerId = RandomTestUtil.randomLong();

		Mockito.when(
			_dispatchLogLocalService.fetchLatestDispatchLog(
				dispatchTriggerId, DispatchTaskStatus.SUCCESSFUL)
		).thenReturn(
			dispatchLog
		);

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_recommendationIndexer.isEmpty(companyId)
		).thenReturn(
			true
		);

		try (MockedStatic<FeatureFlagManagerUtil> mockedStatic =
				Mockito.mockStatic(FeatureFlagManagerUtil.class)) {

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(companyId, "LRAC-14771")
			).thenReturn(
				false
			);

			Assert.assertEquals(
				endDate,
				_analyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor.
					getResourceLastModifiedDate(companyId, dispatchTriggerId));

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(companyId, "LRAC-14771")
			).thenReturn(
				true
			);

			Assert.assertNull(
				_analyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor.
					getResourceLastModifiedDate(companyId, dispatchTriggerId));

			Mockito.when(
				_recommendationIndexer.isEmpty(companyId)
			).thenReturn(
				false
			);

			Assert.assertEquals(
				endDate,
				_analyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor.
					getResourceLastModifiedDate(companyId, dispatchTriggerId));
		}
	}

	private static final MockedStatic<ServiceTrackerFactory>
		_serviceTrackerFactoryMockedStatic = Mockito.mockStatic(
			ServiceTrackerFactory.class);

	private final
		AnalyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor
			_analyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor =
				new AnalyticsDownloadMostViewedContentRecommendationDispatchTaskExecutor();
	private final DispatchLogLocalService _dispatchLogLocalService =
		Mockito.mock(DispatchLogLocalService.class);
	private final RecommendationIndexer _recommendationIndexer = Mockito.mock(
		RecommendationIndexer.class);

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceOverviewMetric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.Trend;
import com.liferay.analytics.test.util.AnalyticsCloudHttpServer;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.net.HttpURLConnection;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class PerformanceOverviewMetricResourceTest
	extends BasePerformanceOverviewMetricResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));
	}

	@Override
	@Test
	public void testGetPerformanceOverviewMetric() throws Exception {
		_testGetPerformanceOverviewMetric();
		_testGetPerformanceOverviewMetricWithAnalyticsCloudNotConnected();
	}

	private void _assertMetric(
		Metric metric, String metricType, double previousValue, double value,
		Trend.Classification classification, double percentage) {

		Assert.assertEquals(metricType, metric.getMetricType());
		Assert.assertEquals(previousValue, metric.getPreviousValue(), 0);
		Assert.assertEquals(value, metric.getValue(), 0);

		Trend trend = metric.getTrend();

		Assert.assertEquals(
			classification.toString(),
			String.valueOf(trend.getClassification()));
		Assert.assertEquals(percentage, trend.getPercentage(), 0);
	}

	private void _testGetPerformanceOverviewMetric() throws Exception {
		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry" +
						"/performance-overview-metric",
					() -> JSONUtil.putAll(
						JSONUtil.put(
							"metricType", "downloadsMetric"
						).put(
							"previousValue", 4
						).put(
							"trend",
							JSONUtil.put(
								"percentage", 50
							).put(
								"trendClassification", "POSITIVE"
							)
						).put(
							"value", 6
						),
						JSONUtil.put(
							"metricType", "impressionsMetric"
						).put(
							"previousValue", 4
						).put(
							"trend",
							JSONUtil.put(
								"percentage", 25
							).put(
								"trendClassification", "NEGATIVE"
							)
						).put(
							"value", 3
						),
						JSONUtil.put(
							"metricType", "readsMetric"
						).put(
							"previousValue", 5
						).put(
							"trend",
							JSONUtil.put(
								"percentage", 0
							).put(
								"trendClassification", "NEUTRAL"
							)
						).put(
							"value", 5
						),
						JSONUtil.put(
							"metricType", "viewsMetric"
						).put(
							"previousValue", 1
						).put(
							"trend",
							JSONUtil.put(
								"percentage", 100
							).put(
								"trendClassification", "POSITIVE"
							)
						).put(
							"value", 2
						)
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			PerformanceOverviewMetric performanceOverviewMetric =
				performanceOverviewMetricResource.getPerformanceOverviewMetric(
					new Long[] {_depotEntry.getDepotEntryId()},
					RandomTestUtil.nextInt());

			_assertMetric(
				performanceOverviewMetric.getDownloadsMetric(),
				"downloadsMetric", 4, 6, Trend.Classification.POSITIVE, 50);
			_assertMetric(
				performanceOverviewMetric.getImpressionsMetric(),
				"impressionsMetric", 4, 3, Trend.Classification.NEGATIVE, 25);
			_assertMetric(
				performanceOverviewMetric.getReadsMetric(), "readsMetric", 5, 5,
				Trend.Classification.NEUTRAL, 0);
			_assertMetric(
				performanceOverviewMetric.getViewsMetric(), "viewsMetric", 1, 2,
				Trend.Classification.POSITIVE, 100);
		}
	}

	private void _testGetPerformanceOverviewMetricWithAnalyticsCloudNotConnected()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(), StringPool.BLANK);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				HttpURLConnection.HTTP_FORBIDDEN,
				performanceOverviewMetricResource.
					getPerformanceOverviewMetricHttpResponse(
						new Long[] {_depotEntry.getDepotEntryId()},
						RandomTestUtil.nextInt()));
		}
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.resource.v1_0.test;

import com.liferay.analytics.reports.rest.client.dto.v1_0.AssetMetric;
import com.liferay.analytics.reports.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.reports.rest.client.dto.v1_0.Trend;
import com.liferay.analytics.test.util.AnalyticsCloudHttpServer;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.net.HttpURLConnection;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Marcos Martins
 */
@RunWith(Arquillian.class)
public class AssetMetricResourceTest extends BaseAssetMetricResourceTestCase {

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

		UnicodeProperties unicodeProperties =
			testGroup.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			"analyticsChannelId", String.valueOf(RandomTestUtil.randomInt()));

		testGroup.setTypeSettingsProperties(unicodeProperties);

		testGroup = _groupLocalService.updateGroup(testGroup);
	}

	@Override
	@Test
	public void testGetGroupAssetMetric() throws Exception {
		_testGetGroupAssetMetric();
		_testGetGroupAssetMetricWithAnalyticsCloudNotConnected();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetGroupAssetMetric() throws Exception {
		super.testGraphQLGetGroupAssetMetric();
	}

	private void _testGetGroupAssetMetric() throws Exception {
		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/blog",
					() -> JSONUtil.put(
						"assetId", "1"
					).put(
						"assetType", "blog"
					).put(
						"defaultMetric",
						JSONUtil.put(
							"metricType", "VIEWS"
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
							"value", 1
						)
					).put(
						"selectedMetrics",
						JSONUtil.put(
							JSONUtil.put(
								"metricType", "VIEWS"
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
								"value", 1
							))
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			AssetMetric assetMetric = assetMetricResource.getGroupAssetMetric(
				testGroup.getGroupId(), "blog", "1", "ALL", 30,
				new String[] {"viewsMetric"});

			Assert.assertEquals("1", assetMetric.getAssetId());
			Assert.assertEquals("blog", assetMetric.getAssetType());

			Metric metric = assetMetric.getDefaultMetric();

			Assert.assertEquals(1, metric.getValue(), 0);
			Assert.assertEquals("VIEWS", metric.getMetricType());

			Trend trend = metric.getTrend();

			Assert.assertEquals(100, trend.getPercentage(), 0);
			Assert.assertEquals(
				Trend.TrendClassification.POSITIVE.toString(),
				String.valueOf(trend.getTrendClassification()));

			Metric[] selectedMetrics = assetMetric.getSelectedMetrics();

			Assert.assertEquals(
				Arrays.toString(selectedMetrics), 1, selectedMetrics.length);

			metric = selectedMetrics[0];

			Assert.assertEquals(1, metric.getValue(), 0);
			Assert.assertEquals("VIEWS", metric.getMetricType());

			trend = metric.getTrend();

			Assert.assertEquals(100, trend.getPercentage(), 0);
			Assert.assertEquals(
				Trend.TrendClassification.POSITIVE.toString(),
				String.valueOf(trend.getTrendClassification()));
		}
	}

	private void _testGetGroupAssetMetricWithAnalyticsCloudNotConnected()
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
				assetMetricResource.getGroupAssetMetricHttpResponse(
					testGroup.getGroupId(), "blog", "1", "ALL", 30,
					new String[] {"viewsMetric"}));
		}
	}

	@Inject
	private GroupLocalService _groupLocalService;

}
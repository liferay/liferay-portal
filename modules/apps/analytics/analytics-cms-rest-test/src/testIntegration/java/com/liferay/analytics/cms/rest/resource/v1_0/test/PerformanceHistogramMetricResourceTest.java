/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Histogram;
import com.liferay.analytics.cms.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceHistogramMetric;
import com.liferay.analytics.cms.rest.client.resource.v1_0.PerformanceHistogramMetricResource;
import com.liferay.analytics.cms.rest.resource.v1_0.test.util.DepotEntryTestUtil;
import com.liferay.analytics.test.util.AnalyticsCloudHttpServer;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class PerformanceHistogramMetricResourceTest
	extends BasePerformanceHistogramMetricResourceTestCase {

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

		_addDepotEntry();
		_addDepotEntry();
	}

	@Override
	@Test
	public void testGetPerformanceHistogramMetric() throws Exception {
		_testGetPerformanceHistogramMetric();
		_testGetPerformanceHistogramMetricWithAnalyticsCloudNotConnected();
		_testGetPerformanceHistogramMetricWithDepotEntryMemberUser();
	}

	private void _addDepotEntry() throws Exception {
		_depotEntries.add(
			DepotEntryTestUtil.addDepotEntry(testGroup.getGroupId()));
	}

	private void _testGetPerformanceHistogramMetric() throws Exception {
		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry" +
						"/performance-overview-metric/histogram",
					() -> JSONUtil.put(
						"histograms",
						JSONUtil.putAll(
							JSONUtil.put(
								"metricName", "downloadsMetric"
							).put(
								"metrics",
								JSONUtil.putAll(
									JSONUtil.put(
										"previousValue", 2.0
									).put(
										"previousValueKey", "2025-07-17T00:00"
									).put(
										"value", 1.0
									).put(
										"valueKey", "2025-07-24T00:00"
									),
									JSONUtil.put(
										"previousValue", 4.0
									).put(
										"previousValueKey", "2025-07-18T00:00"
									).put(
										"value", 5.0
									).put(
										"valueKey", "2025-07-25T00:00"
									))
							).put(
								"total", 7.0
							).put(
								"totalValue", 6.0
							))
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			PerformanceHistogramMetric performanceHistogramMetric =
				performanceHistogramMetricResource.
					getPerformanceHistogramMetric(
						TransformUtil.transformToArray(
							_depotEntries, DepotEntry::getDepotEntryId,
							Long.class),
						RandomTestUtil.nextInt(), "downloadsMetric");

			Histogram[] histograms = performanceHistogramMetric.getHistograms();

			Assert.assertEquals(
				Arrays.toString(histograms), 1, histograms.length);

			Histogram histogram = histograms[0];

			Assert.assertEquals("downloadsMetric", histogram.getMetricName());
			Assert.assertEquals(7, histogram.getTotal(), 0);
			Assert.assertEquals(6, histogram.getTotalValue(), 0);

			Metric[] metrics = histogram.getMetrics();

			Assert.assertEquals(Arrays.toString(metrics), 2, metrics.length);

			DepotEntryTestUtil.assertGroupIds(
				_depotEntries, analyticsCloudHttpServer.getLocation());
		}
	}

	private void _testGetPerformanceHistogramMetricWithAnalyticsCloudNotConnected()
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
				performanceHistogramMetricResource.
					getPerformanceHistogramMetricHttpResponse(
						TransformUtil.transformToArray(
							_depotEntries, DepotEntry::getDepotEntryId,
							Long.class),
						RandomTestUtil.nextInt(), "downloadsMetric"));
		}
	}

	private void _testGetPerformanceHistogramMetricWithDepotEntryMemberUser()
		throws Exception {

		com.liferay.analytics.cms.rest.resource.v1_0.
			PerformanceHistogramMetricResource
				performanceHistogramMetricResource =
					ReflectionTestUtil.getFieldValue(
						this, "_performanceHistogramMetricResource");

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry" +
						"/performance-overview-metric/histogram",
					() -> "{}");

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			DepotEntryTestUtil.withDepotEntryMemberUser(
				_depotEntries.get(0),
				() -> {
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer, null,
						depotEntryIds ->
							performanceHistogramMetricResource.
								getPerformanceHistogramMetric(
									depotEntryIds, RandomTestUtil.nextInt(),
									"downloadsMetric"));
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer,
						new DepotEntry[] {_depotEntries.get(0)},
						depotEntryIds ->
							performanceHistogramMetricResource.
								getPerformanceHistogramMetric(
									depotEntryIds, RandomTestUtil.nextInt(),
									"downloadsMetric"));
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer,
						_depotEntries.toArray(new DepotEntry[0]),
						depotEntryIds ->
							performanceHistogramMetricResource.
								getPerformanceHistogramMetric(
									depotEntryIds, RandomTestUtil.nextInt(),
									"downloadsMetric"));

					return null;
				});
		}
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

}
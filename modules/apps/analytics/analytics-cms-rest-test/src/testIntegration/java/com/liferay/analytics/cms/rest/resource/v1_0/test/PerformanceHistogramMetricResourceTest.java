/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.dto.v1_0.Histogram;
import com.liferay.analytics.cms.rest.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceHistogramMetric;
import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceHistogramMetricResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.ws.rs.ForbiddenException;

import java.util.Arrays;
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
	public void testGetPerformanceHistogramMetric() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId",
							RandomTestUtil.nextLong()
						).put(
							"liferayAnalyticsEnableAllGroupIds", true
						).put(
							"liferayAnalyticsFaroBackendSecuritySignature",
							RandomTestUtil.randomString()
						).put(
							"liferayAnalyticsFaroBackendURL",
							"http://" + RandomTestUtil.randomString()
						).build())) {

			ReflectionTestUtil.setFieldValue(
				_performanceHistogramMetricResource, "_http",
				new MockHttp(
					Collections.singletonMap(
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
											"previousValueKey",
											"2025-07-17T00:00"
										).put(
											"value", 1.0
										).put(
											"valueKey", "2025-07-24T00:00"
										),
										JSONUtil.put(
											"previousValue", 4.0
										).put(
											"previousValueKey",
											"2025-07-18T00:00"
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
						).toString())));

			PerformanceHistogramMetric performanceHistogramMetric =
				_performanceHistogramMetricResource.
					getPerformanceHistogramMetric(
						new Long[] {_depotEntry.getDepotEntryId()},
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
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_performanceHistogramMetricResource, "_http", _http);
		}
	}

	@Test
	public void testGetPerformanceHistogramMetricWithAnalyticsCloudNotConnected() {
		Assert.assertThrows(
			ForbiddenException.class,
			() ->
				_performanceHistogramMetricResource.
					getPerformanceHistogramMetric(
						new Long[] {_depotEntry.getDepotEntryId()},
						RandomTestUtil.nextInt(), "downloadsMetric"));
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private Http _http;

	@Inject
	private PerformanceHistogramMetricResource
		_performanceHistogramMetricResource;

}
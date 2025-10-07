/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.dto.v1_0.Histogram;
import com.liferay.analytics.cms.rest.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryHistogramMetric;
import com.liferay.analytics.cms.rest.resource.v1_0.ObjectEntryHistogramMetricResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class ObjectEntryHistogramMetricResourceTest
	extends BaseObjectEntryHistogramMetricResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testGetObjectEntryHistogramMetric() throws Exception {
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
				_objectEntryHistogramMetricResource, "_http",
				new MockHttp(
					Collections.singletonMap(
						"/api/1.0/asset-metric/objectEntry/overview/histogram",
						() -> JSONUtil.put(
							"histograms",
							JSONUtil.putAll(
								JSONUtil.put(
									"metricName", "downloadsMetric"
								).put(
									"metrics",
									JSONUtil.putAll(
										JSONUtil.put(
											"key", "2025-07-24T00:00"
										).put(
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
											"key", "2025-07-25T00:00"
										).put(
											"previousValue", 4.0
										).put(
											"previousValueKey",
											"2025-07-18T00:00"
										).put(
											"value", 5.0
										).put(
											"valueKey", "2025-07-25T00:00"
										),
										JSONUtil.put(
											"key", "2025-07-26T00:00"
										).put(
											"previousValue", 2.0
										).put(
											"previousValueKey",
											"2025-07-19T00:00"
										).put(
											"value", 2.0
										).put(
											"valueKey", "2025-07-26T00:00"
										),
										JSONUtil.put(
											"key", "2025-07-27T00:00"
										).put(
											"previousValue", 1.0
										).put(
											"previousValueKey",
											"2025-07-20T00:00"
										).put(
											"value", 0.0
										).put(
											"valueKey", "2025-07-27T00:00"
										),
										JSONUtil.put(
											"key", "2025-07-28T00:00"
										).put(
											"previousValue", 12.0
										).put(
											"previousValueKey",
											"2025-07-21T00:00"
										).put(
											"value", 13.0
										).put(
											"valueKey", "2025-07-28T00:00"
										),
										JSONUtil.put(
											"key", "2025-07-29T00:00"
										).put(
											"previousValue", 22.0
										).put(
											"previousValueKey",
											"2025-07-22T00:00"
										).put(
											"value", 14.0
										).put(
											"valueKey", "2025-07-29T00:00"
										),
										JSONUtil.put(
											"key", "2025-07-30T00:00"
										).put(
											"previousValue", 2.0
										).put(
											"previousValueKey",
											"2025-07-23T00:00"
										).put(
											"value", 15.0
										).put(
											"valueKey", "2025-07-30T00:00"
										))
								).put(
									"total", 7.0
								).put(
									"totalValue", 50.0
								))
						).toString())));

			ObjectEntryHistogramMetric objectEntryHistogramMetric =
				_objectEntryHistogramMetricResource.
					getObjectEntryHistogramMetric(
						"1", null, 7, new String[] {"downloadsMetric"});

			Histogram[] histograms = objectEntryHistogramMetric.getHistograms();

			Assert.assertEquals(
				Arrays.toString(histograms), 1, histograms.length);

			Histogram histogram = histograms[0];

			Assert.assertEquals("downloadsMetric", histogram.getMetricName());
			Assert.assertEquals(7, histogram.getTotal(), 0);
			Assert.assertEquals(50, histogram.getTotalValue(), 0);

			Metric[] metrics = histogram.getMetrics();

			Assert.assertEquals(Arrays.toString(metrics), 7, metrics.length);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_objectEntryHistogramMetricResource, "_http", _http);
		}
	}

	@Inject
	private Http _http;

	@Inject
	private ObjectEntryHistogramMetricResource
		_objectEntryHistogramMetricResource;

}
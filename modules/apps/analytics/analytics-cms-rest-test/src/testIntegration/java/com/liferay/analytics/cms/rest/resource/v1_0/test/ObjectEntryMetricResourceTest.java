/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.dto.v1_0.ObjectEntryMetric;
import com.liferay.analytics.cms.rest.dto.v1_0.Trend;
import com.liferay.analytics.cms.rest.resource.v1_0.ObjectEntryMetricResource;
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
import com.liferay.portal.kernel.util.StringUtil;
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
public class ObjectEntryMetricResourceTest
	extends BaseObjectEntryMetricResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testGetObjectEntryMetric() throws Exception {
		long dataSourceId = RandomTestUtil.nextLong();

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						AnalyticsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"liferayAnalyticsDataSourceId", dataSourceId
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
				_objectEntryMetricResource, "_http",
				new MockHttp(
					Collections.singletonMap(
						"/api/1.0/asset-metric/objectEntry/overview",
						() -> JSONUtil.put(
							"dataSourceId", String.valueOf(dataSourceId)
						).put(
							"defaultMetric",
							JSONUtil.put(
								"metricType", "IMPRESSIONS"
							).put(
								"previousValue", 1
							).put(
								"trend",
								JSONUtil.put(
									"percentage", 100
								).put(
									"trendClassification", "NEGATIVE"
								)
							).put(
								"value", 0
							)
						).put(
							"externalReferenceCode", "1"
						).put(
							"selectedMetrics",
							JSONUtil.putAll(
								JSONUtil.put(
									"metricType", "DOWNLOADS"
								).put(
									"previousValue", 1
								).put(
									"trend",
									JSONUtil.put(
										"percentage", 50
									).put(
										"trendClassification", "POSITIVE"
									)
								).put(
									"value", 2
								),
								JSONUtil.put(
									"metricType", "VIEWS"
								).put(
									"previousValue", 1
								).put(
									"trend",
									JSONUtil.put(
										"percentage", 0
									).put(
										"trendClassification", "NEUTRAL"
									)
								).put(
									"value", 1
								))
						).toString())));

			ObjectEntryMetric objectEntryMetric =
				_objectEntryMetricResource.getObjectEntryMetric(
					"1", null, 30,
					new String[] {"downloadsMetric", "viewsMetric"});

			Assert.assertEquals(
				String.valueOf(dataSourceId),
				objectEntryMetric.getDataSourceId());

			Metric metric = objectEntryMetric.getDefaultMetric();

			Assert.assertEquals("IMPRESSIONS", metric.getMetricType());
			Assert.assertEquals(1, metric.getPreviousValue(), 0);
			Assert.assertEquals(0, metric.getValue(), 0);

			Trend trend = metric.getTrend();

			Assert.assertEquals(
				Trend.Classification.NEGATIVE.toString(),
				String.valueOf(trend.getClassification()));
			Assert.assertEquals(100, trend.getPercentage(), 0);

			Assert.assertEquals(
				"1", objectEntryMetric.getExternalReferenceCode());

			Metric[] selectedMetrics = objectEntryMetric.getSelectedMetrics();

			Assert.assertEquals(
				Arrays.toString(selectedMetrics), 2, selectedMetrics.length);

			for (Metric selectedMetric : selectedMetrics) {
				if (StringUtil.equals(
						selectedMetric.getMetricType(), "DOWNLOADS")) {

					Assert.assertEquals(
						1, selectedMetric.getPreviousValue(), 0);

					trend = selectedMetric.getTrend();

					Assert.assertEquals(
						Trend.Classification.POSITIVE.toString(),
						String.valueOf(trend.getClassification()));
					Assert.assertEquals(50, trend.getPercentage(), 0);

					Assert.assertEquals(2, selectedMetric.getValue(), 0);
				}
				else if (StringUtil.equals(
							selectedMetric.getMetricType(), "VIEWS")) {

					Assert.assertEquals(
						1, selectedMetric.getPreviousValue(), 0);

					trend = selectedMetric.getTrend();

					Assert.assertEquals(
						Trend.Classification.NEUTRAL.toString(),
						String.valueOf(trend.getClassification()));
					Assert.assertEquals(0, trend.getPercentage(), 0);

					Assert.assertEquals(1, selectedMetric.getValue(), 0);
				}
			}
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_objectEntryMetricResource, "_http", _http);
		}
	}

	@Inject
	private Http _http;

	@Inject
	private ObjectEntryMetricResource _objectEntryMetricResource;

}
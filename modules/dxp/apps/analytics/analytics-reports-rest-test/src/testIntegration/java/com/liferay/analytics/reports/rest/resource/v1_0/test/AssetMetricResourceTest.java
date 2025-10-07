/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.resource.v1_0.test;

import com.liferay.analytics.reports.rest.client.dto.v1_0.Trend.TrendClassification;
import com.liferay.analytics.reports.rest.dto.v1_0.AssetMetric;
import com.liferay.analytics.reports.rest.dto.v1_0.Metric;
import com.liferay.analytics.reports.rest.dto.v1_0.Trend;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetMetricResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.Collections;

import org.junit.After;
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
		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		UnicodeProperties unicodeProperties = group.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			"analyticsChannelId", String.valueOf(RandomTestUtil.randomInt()));

		group.setTypeSettingsProperties(unicodeProperties);

		_group = _groupLocalService.updateGroup(group);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		UnicodeProperties unicodeProperties =
			_group.getTypeSettingsProperties();

		unicodeProperties.remove("analyticsChannelId");

		_group = _groupLocalService.updateGroup(_group);
	}

	@Override
	@Test
	public void testGetGroupAssetMetric() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
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
				_assetMetricResource, "_http",
				new MockHttp(
					Collections.singletonMap(
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
						).toString())));

			AssetMetric assetMetric = _assetMetricResource.getGroupAssetMetric(
				TestPropsValues.getGroupId(), "blog", "1", "ALL", 30,
				new String[] {"viewsMetric"});

			Assert.assertEquals("1", assetMetric.getAssetId());
			Assert.assertEquals("blog", assetMetric.getAssetType());

			Metric metric = assetMetric.getDefaultMetric();

			Assert.assertEquals(1, metric.getValue(), 0);
			Assert.assertEquals("VIEWS", metric.getMetricType());

			Trend trend = metric.getTrend();

			Assert.assertEquals(100, trend.getPercentage(), 0);
			Assert.assertEquals(
				TrendClassification.POSITIVE.toString(),
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
				TrendClassification.POSITIVE.toString(),
				String.valueOf(trend.getTrendClassification()));
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_assetMetricResource, "_http", _http);
		}
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetGroupAssetMetric() throws Exception {
		super.testGraphQLGetGroupAssetMetric();
	}

	@Inject
	private AssetMetricResource _assetMetricResource;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Http _http;

}
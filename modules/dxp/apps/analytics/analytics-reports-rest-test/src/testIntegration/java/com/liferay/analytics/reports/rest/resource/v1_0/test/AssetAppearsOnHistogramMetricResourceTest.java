/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.resource.v1_0.test;

import com.liferay.analytics.reports.rest.dto.v1_0.AppearsOnHistogram;
import com.liferay.analytics.reports.rest.dto.v1_0.AssetAppearsOnHistogram;
import com.liferay.analytics.reports.rest.dto.v1_0.AssetAppearsOnHistogramMetric;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetAppearsOnHistogramMetricResource;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.MockHttp;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
public class AssetAppearsOnHistogramMetricResourceTest
	extends BaseAssetAppearsOnHistogramMetricResourceTestCase {

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
		super.tearDown();

		UnicodeProperties unicodeProperties =
			_group.getTypeSettingsProperties();

		unicodeProperties.remove("analyticsChannelId");

		_group = _groupLocalService.updateGroup(_group);
	}

	@Override
	@Test
	public void testGetGroupAssetMetricAssetTypeAppearsOnHistogram()
		throws Exception {

		_testGetGroupAssetMetricAssetTypeAppearsOnHistogram();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetGroupAssetMetricAssetTypeAppearsOnHistogram()
		throws Exception {

		super.testGraphQLGetGroupAssetMetricAssetTypeAppearsOnHistogram();
	}

	private void _testGetGroupAssetMetricAssetTypeAppearsOnHistogram()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId())) {

			ReflectionTestUtil.setFieldValue(
				_assetAppearsOnHistogramMetricResource, "_http",
				new MockHttp(
					Collections.singletonMap(
						"/api/1.0/asset-metric/blog/appears-on/histogram",
						() -> JSONUtil.put(
							"assetAppearsOnHistograms",
							JSONUtil.putAll(
								JSONUtil.put(
									"appearsOnHistograms",
									JSONUtil.putAll(
										JSONUtil.put(
											"canonicalUrl", "https://test.com/1"
										).put(
											"pageTitle", "Title 1"
										).put(
											"totalValue", 3
										))
								).put(
									"metricName", "viewsMetric"
								))
						).toString())));

			AssetAppearsOnHistogramMetric assetAppearsOnHistogramMetric =
				_assetAppearsOnHistogramMetricResource.
					getGroupAssetMetricAssetTypeAppearsOnHistogram(
						TestPropsValues.getGroupId(), "blog", "1", "ALL", 30);

			AssetAppearsOnHistogram[] assetAppearsOnHistograms =
				assetAppearsOnHistogramMetric.getAssetAppearsOnHistograms();

			Assert.assertEquals(
				Arrays.toString(assetAppearsOnHistograms), 1,
				assetAppearsOnHistograms.length);

			AssetAppearsOnHistogram assetAppearsOnHistogram =
				assetAppearsOnHistograms[0];

			Assert.assertEquals(
				"viewsMetric", assetAppearsOnHistogram.getMetricName());

			AppearsOnHistogram[] appearsOnHistograms =
				assetAppearsOnHistogram.getAppearsOnHistograms();

			Assert.assertEquals(
				Arrays.toString(appearsOnHistograms), 1,
				appearsOnHistograms.length);

			AppearsOnHistogram appearsOnHistogram = appearsOnHistograms[0];

			Assert.assertEquals(
				"https://test.com/1", appearsOnHistogram.getCanonicalUrl());
			Assert.assertEquals("Title 1", appearsOnHistogram.getPageTitle());
			Assert.assertEquals(3, appearsOnHistogram.getTotalValue(), 0);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_assetAppearsOnHistogramMetricResource, "_http", _http);
		}
	}

	@Inject
	private AssetAppearsOnHistogramMetricResource
		_assetAppearsOnHistogramMetricResource;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Http _http;

}
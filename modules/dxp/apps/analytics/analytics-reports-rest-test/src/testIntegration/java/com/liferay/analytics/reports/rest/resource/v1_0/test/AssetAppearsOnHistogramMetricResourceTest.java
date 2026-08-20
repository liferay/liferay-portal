/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.resource.v1_0.test;

import com.liferay.analytics.reports.rest.client.dto.v1_0.AppearsOnHistogram;
import com.liferay.analytics.reports.rest.client.dto.v1_0.AssetAppearsOnHistogram;
import com.liferay.analytics.reports.rest.client.dto.v1_0.AssetAppearsOnHistogramMetric;
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

		UnicodeProperties unicodeProperties =
			testGroup.getTypeSettingsProperties();

		unicodeProperties.setProperty(
			"analyticsChannelId", String.valueOf(RandomTestUtil.randomInt()));

		testGroup.setTypeSettingsProperties(unicodeProperties);

		testGroup = _groupLocalService.updateGroup(testGroup);
	}

	@Override
	@Test
	public void testGetGroupAssetMetricAssetTypeAppearsOnHistogram()
		throws Exception {

		_testGetGroupAssetMetricAssetTypeAppearsOnHistogram();
		_testGetGroupAssetMetricAssetTypeAppearsOnHistogramWithAnalyticsCloudNotConnected();
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

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
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
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			AssetAppearsOnHistogramMetric assetAppearsOnHistogramMetric =
				assetAppearsOnHistogramMetricResource.
					getGroupAssetMetricAssetTypeAppearsOnHistogram(
						testGroup.getGroupId(), "blog", "1", "ALL", 30);

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
	}

	private void _testGetGroupAssetMetricAssetTypeAppearsOnHistogramWithAnalyticsCloudNotConnected()
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
				assetAppearsOnHistogramMetricResource.
					getGroupAssetMetricAssetTypeAppearsOnHistogramHttpResponse(
						testGroup.getGroupId(), "blog", "1", "ALL", 30));
		}
	}

	@Inject
	private GroupLocalService _groupLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.resource.v1_0.test;

import com.liferay.analytics.reports.rest.client.dto.v1_0.AssetDeviceMetric;
import com.liferay.analytics.reports.rest.client.dto.v1_0.DeviceMetric;
import com.liferay.analytics.reports.rest.client.dto.v1_0.Metric;
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
public class AssetDeviceMetricResourceTest
	extends BaseAssetDeviceMetricResourceTestCase {

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
	public void testGetGroupAssetMetricAssetTypeDevice() throws Exception {
		_testGetGroupAssetMetricAssetTypeDevice();
		_testGetGroupAssetMetricAssetTypeDeviceWithAnalyticsCloudNotConnected();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetGroupAssetMetricAssetTypeDevice()
		throws Exception {

		super.testGraphQLGetGroupAssetMetricAssetTypeDevice();
	}

	private void _testGetGroupAssetMetricAssetTypeDevice() throws Exception {
		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/blog/devices",
					() -> JSONUtil.put(
						"deviceMetrics",
						JSONUtil.putAll(
							JSONUtil.put(
								"metricName", "Desktop"
							).put(
								"metrics",
								JSONUtil.putAll(
									JSONUtil.put(
										"metricType", "VIEWS"
									).put(
										"value", 3
									))
							))
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			AssetDeviceMetric assetDeviceMetric =
				assetDeviceMetricResource.getGroupAssetMetricAssetTypeDevice(
					testGroup.getGroupId(), "blog", "1", "ALL", 30);

			DeviceMetric[] deviceMetrics = assetDeviceMetric.getDeviceMetrics();

			Assert.assertEquals(
				Arrays.toString(deviceMetrics), 1, deviceMetrics.length);

			DeviceMetric deviceMetric = deviceMetrics[0];

			Assert.assertEquals("Desktop", deviceMetric.getMetricName());

			Metric[] metrics = deviceMetric.getMetrics();

			Assert.assertEquals(Arrays.toString(metrics), 1, metrics.length);

			Metric metric = metrics[0];

			Assert.assertEquals("VIEWS", metric.getMetricType());
			Assert.assertEquals(3, metric.getValue(), 0);
		}
	}

	private void _testGetGroupAssetMetricAssetTypeDeviceWithAnalyticsCloudNotConnected()
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
				assetDeviceMetricResource.
					getGroupAssetMetricAssetTypeDeviceHttpResponse(
						testGroup.getGroupId(), "blog", "1", "ALL", 30));
		}
	}

	@Inject
	private GroupLocalService _groupLocalService;

}
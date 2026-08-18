/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.resource.v1_0.test;

import com.liferay.analytics.reports.rest.dto.v1_0.AssetDeviceMetric;
import com.liferay.analytics.reports.rest.dto.v1_0.DeviceMetric;
import com.liferay.analytics.reports.rest.dto.v1_0.Metric;
import com.liferay.analytics.reports.rest.resource.v1_0.AssetDeviceMetricResource;
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

import jakarta.ws.rs.ForbiddenException;

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
		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId())) {

			ReflectionTestUtil.setFieldValue(
				_assetDeviceMetricResource, "_http",
				new MockHttp(
					Collections.singletonMap(
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
						).toString())));

			AssetDeviceMetric assetDeviceMetric =
				_assetDeviceMetricResource.getGroupAssetMetricAssetTypeDevice(
					TestPropsValues.getGroupId(), "blog", "1", "ALL", 30);

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
		finally {
			ReflectionTestUtil.setFieldValue(
				_assetDeviceMetricResource, "_http", _http);
		}
	}

	private void _testGetGroupAssetMetricAssetTypeDeviceWithAnalyticsCloudNotConnected() {
		Assert.assertThrows(
			ForbiddenException.class,
			() -> _assetDeviceMetricResource.getGroupAssetMetricAssetTypeDevice(
				TestPropsValues.getGroupId(), "blog", "1", "ALL", 30));
	}

	@Inject
	private AssetDeviceMetricResource _assetDeviceMetricResource;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Http _http;

}
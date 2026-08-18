/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Histogram;
import com.liferay.analytics.cms.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceHistogramMetric;
import com.liferay.analytics.cms.rest.client.resource.v1_0.PerformanceHistogramMetricResource;
import com.liferay.analytics.test.util.AnalyticsCloudHttpServer;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
			_depotEntryLocalService.addDepotEntry(
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				DepotConstants.TYPE_ASSET_LIBRARY,
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId())));
	}

	private void _assertNoRequest(
			AnalyticsCloudHttpServer analyticsCloudHttpServer,
			UnsafeConsumer<Long[], Exception> unsafeConsumer)
		throws Exception {

		DepotEntry depotEntry1 = _depotEntries.get(0);
		DepotEntry depotEntry2 = _depotEntries.get(1);

		Long[][] depotEntryIdsArray = {
			null, {depotEntry1.getDepotEntryId()},
			{depotEntry1.getDepotEntryId(), depotEntry2.getDepotEntryId()}
		};

		for (Long[] depotEntryIds : depotEntryIdsArray) {
			unsafeConsumer.accept(depotEntryIds);

			Assert.assertNull(analyticsCloudHttpServer.getLocation());
		}
	}

	private PerformanceHistogramMetricResource
			_getPerformanceHistogramMetricResource()
		throws Exception {

		DepotEntry depotEntry = _depotEntries.get(0);

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(depotEntry.getGroupId());

		_userLocalService.updatePassword(
			user.getUserId(), password, password, false, true);

		_users.add(user);

		return PerformanceHistogramMetricResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
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

			PerformanceHistogramMetricResource
				performanceHistogramMetricResource =
					_getPerformanceHistogramMetricResource();

			_assertNoRequest(
				analyticsCloudHttpServer,
				depotEntryIds ->
					performanceHistogramMetricResource.
						getPerformanceHistogramMetric(
							depotEntryIds, RandomTestUtil.nextInt(),
							"downloadsMetric"));
		}
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private UserLocalService _userLocalService;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}
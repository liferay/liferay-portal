/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceOverviewMetric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.Trend;
import com.liferay.analytics.cms.rest.client.resource.v1_0.PerformanceOverviewMetricResource;
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
public class PerformanceOverviewMetricResourceTest
	extends BasePerformanceOverviewMetricResourceTestCase {

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
	public void testGetPerformanceOverviewMetric() throws Exception {
		_testGetPerformanceOverviewMetric();
		_testGetPerformanceOverviewMetricWithAnalyticsCloudNotConnected();
		_testGetPerformanceOverviewMetricWithDepotEntryMemberUser();
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

	private void _assertMetric(
		Metric metric, String metricType, double previousValue, double value,
		Trend.Classification classification, double percentage) {

		Assert.assertEquals(metricType, metric.getMetricType());
		Assert.assertEquals(previousValue, metric.getPreviousValue(), 0);
		Assert.assertEquals(value, metric.getValue(), 0);

		Trend trend = metric.getTrend();

		Assert.assertEquals(
			classification.toString(),
			String.valueOf(trend.getClassification()));
		Assert.assertEquals(percentage, trend.getPercentage(), 0);
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

	private PerformanceOverviewMetricResource
			_getPerformanceOverviewMetricResource()
		throws Exception {

		DepotEntry depotEntry = _depotEntries.get(0);

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(depotEntry.getGroupId());

		_userLocalService.updatePassword(
			user.getUserId(), password, password, false, true);

		_users.add(user);

		return PerformanceOverviewMetricResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	private void _testGetPerformanceOverviewMetric() throws Exception {
		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry" +
						"/performance-overview-metric",
					() -> JSONUtil.putAll(
						JSONUtil.put(
							"metricType", "downloadsMetric"
						).put(
							"previousValue", 4
						).put(
							"trend",
							JSONUtil.put(
								"percentage", 50
							).put(
								"trendClassification", "POSITIVE"
							)
						).put(
							"value", 6
						),
						JSONUtil.put(
							"metricType", "impressionsMetric"
						).put(
							"previousValue", 4
						).put(
							"trend",
							JSONUtil.put(
								"percentage", 25
							).put(
								"trendClassification", "NEGATIVE"
							)
						).put(
							"value", 3
						),
						JSONUtil.put(
							"metricType", "readsMetric"
						).put(
							"previousValue", 5
						).put(
							"trend",
							JSONUtil.put(
								"percentage", 0
							).put(
								"trendClassification", "NEUTRAL"
							)
						).put(
							"value", 5
						),
						JSONUtil.put(
							"metricType", "viewsMetric"
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
							"value", 2
						)
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			PerformanceOverviewMetric performanceOverviewMetric =
				performanceOverviewMetricResource.getPerformanceOverviewMetric(
					TransformUtil.transformToArray(
						_depotEntries, DepotEntry::getDepotEntryId, Long.class),
					RandomTestUtil.nextInt());

			_assertMetric(
				performanceOverviewMetric.getDownloadsMetric(),
				"downloadsMetric", 4, 6, Trend.Classification.POSITIVE, 50);
			_assertMetric(
				performanceOverviewMetric.getImpressionsMetric(),
				"impressionsMetric", 4, 3, Trend.Classification.NEGATIVE, 25);
			_assertMetric(
				performanceOverviewMetric.getReadsMetric(), "readsMetric", 5, 5,
				Trend.Classification.NEUTRAL, 0);
			_assertMetric(
				performanceOverviewMetric.getViewsMetric(), "viewsMetric", 1, 2,
				Trend.Classification.POSITIVE, 100);
		}
	}

	private void _testGetPerformanceOverviewMetricWithAnalyticsCloudNotConnected()
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
				performanceOverviewMetricResource.
					getPerformanceOverviewMetricHttpResponse(
						TransformUtil.transformToArray(
							_depotEntries, DepotEntry::getDepotEntryId,
							Long.class),
						RandomTestUtil.nextInt()));
		}
	}

	private void _testGetPerformanceOverviewMetricWithDepotEntryMemberUser()
		throws Exception {

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry" +
						"/performance-overview-metric",
					() -> "{}");

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			PerformanceOverviewMetricResource
				performanceOverviewMetricResource =
					_getPerformanceOverviewMetricResource();

			_assertNoRequest(
				analyticsCloudHttpServer,
				depotEntryIds ->
					performanceOverviewMetricResource.
						getPerformanceOverviewMetric(
							depotEntryIds, RandomTestUtil.nextInt()));
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
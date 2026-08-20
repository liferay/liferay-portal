/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.ObjectEntryMetric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.Trend;
import com.liferay.analytics.cms.rest.client.resource.v1_0.ObjectEntryMetricResource;
import com.liferay.analytics.test.util.AnalyticsCloudHttpServer;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.net.HttpURLConnection;

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
public class ObjectEntryMetricResourceTest
	extends BaseObjectEntryMetricResourceTestCase {

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
			DepotConstants.TYPE_SPACE,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(),
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", testCompany.getCompanyId()),
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build());
	}

	@Override
	@Test
	public void testGetObjectEntryMetric() throws Exception {
		_testGetObjectEntryMetric();
		_testGetObjectEntryMetricWithInvalidObjectEntryId();
		_testGetObjectEntryMetricWithUnsyncedGroup();
		_testGetObjectEntryMetricWithoutViewPermission();
	}

	private void _testGetObjectEntryMetric() throws Exception {
		String dataSourceId = RandomTestUtil.randomString();

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
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
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(), dataSourceId, true,
						analyticsCloudHttpServer.getURL())) {

			ObjectEntryMetric objectEntryMetric =
				objectEntryMetricResource.getObjectEntryMetric(
					null, _objectEntry.getObjectEntryId(),
					RandomTestUtil.nextInt(),
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
	}

	private void _testGetObjectEntryMetricWithInvalidObjectEntryId()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), false)) {

			assertHttpResponseStatusCode(
				HttpURLConnection.HTTP_NOT_FOUND,
				objectEntryMetricResource.getObjectEntryMetricHttpResponse(
					null, RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
					new String[] {"downloadsMetric", "viewsMetric"}));
		}
	}

	private void _testGetObjectEntryMetricWithoutViewPermission()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), false)) {

			String password = RandomTestUtil.randomString();

			User user = UserTestUtil.addUser(testCompany, password);

			ObjectEntryMetricResource objectEntryMetricResource =
				ObjectEntryMetricResource.builder(
				).authentication(
					user.getEmailAddress(), password
				).endpoint(
					testCompany.getVirtualHostname(),
					PortalUtil.getPortalServerPort(false), "http"
				).locale(
					LocaleUtil.getDefault()
				).build();

			assertHttpResponseStatusCode(
				HttpURLConnection.HTTP_NOT_FOUND,
				objectEntryMetricResource.getObjectEntryMetricHttpResponse(
					null, _objectEntry.getObjectEntryId(),
					RandomTestUtil.nextInt(),
					new String[] {"downloadsMetric", "viewsMetric"}));
		}
	}

	private void _testGetObjectEntryMetricWithUnsyncedGroup() throws Exception {
		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), false);
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				HttpURLConnection.HTTP_BAD_REQUEST,
				objectEntryMetricResource.getObjectEntryMetricHttpResponse(
					testGroup.getGroupId(), _objectEntry.getObjectEntryId(),
					RandomTestUtil.nextInt(),
					new String[] {"downloadsMetric", "viewsMetric"}));
		}
	}

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry;

}
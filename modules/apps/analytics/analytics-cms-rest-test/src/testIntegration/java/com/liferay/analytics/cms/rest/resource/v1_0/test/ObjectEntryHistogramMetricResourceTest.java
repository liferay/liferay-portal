/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Histogram;
import com.liferay.analytics.cms.rest.client.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.client.dto.v1_0.ObjectEntryHistogramMetric;
import com.liferay.analytics.cms.rest.client.resource.v1_0.ObjectEntryHistogramMetricResource;
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
public class ObjectEntryHistogramMetricResourceTest
	extends BaseObjectEntryHistogramMetricResourceTestCase {

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
	public void testGetObjectEntryHistogramMetric() throws Exception {
		_testGetObjectEntryHistogramMetric();
		_testGetObjectEntryHistogramMetricWithInvalidObjectEntryId();
		_testGetObjectEntryHistogramMetricWithUnsyncedGroup();
		_testGetObjectEntryHistogramMetricWithoutViewPermission();
	}

	private void _testGetObjectEntryHistogramMetric() throws Exception {
		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
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
										"previousValueKey", "2025-07-17T00:00"
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
										"previousValueKey", "2025-07-18T00:00"
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
										"previousValueKey", "2025-07-19T00:00"
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
										"previousValueKey", "2025-07-20T00:00"
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
										"previousValueKey", "2025-07-21T00:00"
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
										"previousValueKey", "2025-07-22T00:00"
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
										"previousValueKey", "2025-07-23T00:00"
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
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			ObjectEntryHistogramMetric objectEntryHistogramMetric =
				objectEntryHistogramMetricResource.
					getObjectEntryHistogramMetric(
						null, _objectEntry.getObjectEntryId(),
						RandomTestUtil.nextInt(),
						new String[] {"downloadsMetric"});

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
	}

	private void _testGetObjectEntryHistogramMetricWithInvalidObjectEntryId()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), false)) {

			assertHttpResponseStatusCode(
				HttpURLConnection.HTTP_NOT_FOUND,
				objectEntryHistogramMetricResource.
					getObjectEntryHistogramMetricHttpResponse(
						null, RandomTestUtil.nextLong(),
						RandomTestUtil.nextInt(),
						new String[] {"downloadsMetric"}));
		}
	}

	private void _testGetObjectEntryHistogramMetricWithoutViewPermission()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), false)) {

			String password = RandomTestUtil.randomString();

			User user = UserTestUtil.addUser(testCompany, password);

			ObjectEntryHistogramMetricResource
				objectEntryHistogramMetricResource =
					ObjectEntryHistogramMetricResource.builder(
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
				objectEntryHistogramMetricResource.
					getObjectEntryHistogramMetricHttpResponse(
						null, _objectEntry.getObjectEntryId(),
						RandomTestUtil.nextInt(),
						new String[] {"downloadsMetric"}));
		}
	}

	private void _testGetObjectEntryHistogramMetricWithUnsyncedGroup()
		throws Exception {

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
				objectEntryHistogramMetricResource.
					getObjectEntryHistogramMetricHttpResponse(
						testGroup.getGroupId(), _objectEntry.getObjectEntryId(),
						RandomTestUtil.nextInt(),
						new String[] {"downloadsMetric"}));
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
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.dto.v1_0.Metric;
import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceMetric;
import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceMetricResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.validation.ValidationException;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.ByteArrayOutputStream;

import java.time.LocalDate;

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
public class PerformanceMetricResourceTest
	extends BasePerformanceMetricResourceTestCase {

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
	public void testGetPerformanceMetric() throws Exception {
		_testGetPerformanceMetric(
			"categories", "viewsMetric",
			"/api/1.0/asset-metric/objectEntry/categories");
		_testGetPerformanceMetric(
			"location", "downloadsMetric",
			"/api/1.0/asset-metric/objectEntry/geolocation");
		_testGetPerformanceMetricWithAnalyticsCloudNotConnected();
		_testGetPerformanceMetricWithInvalidMetricType();
		_testGetPerformanceMetricWithNoData();
	}

	@Override
	@Test
	public void testGetPerformanceMetricExport() throws Exception {
		_testGetPerformanceMetricExport(
			"categories", "viewsMetric",
			"/api/1.0/asset-metric/objectEntry/categories/export");
		_testGetPerformanceMetricExport(
			"location", "downloadsMetric",
			"/api/1.0/asset-metric/objectEntry/geolocation/export");
		_testGetPerformanceMetricExportWithAnalyticsCloudNotConnected();
		_testGetPerformanceMetricExportWithInvalidMetricType();
	}

	private DepotEntry _addDepotEntry() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private void _assertMetric(Metric metric, double value, String valueKey) {
		Assert.assertEquals(value, metric.getValue(), 0);
		Assert.assertEquals(valueKey, metric.getValueKey());
	}

	private void _assertParameter(
		String expectedValue, String name, String url) {

		Assert.assertEquals(
			expectedValue,
			URLCodec.decodeURL(
				HttpComponentsUtil.getParameter(url, name, false)));
	}

	private PerformanceMetric _getPerformanceMetric(
			long dataSourceId, String metricsJSON, String metricType,
			String path, int rangeKey,
			UnsafeFunction<Long[], PerformanceMetric, Exception> unsafeFunction)
		throws Exception {

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

			RecordingMockHttp recordingMockHttp = new RecordingMockHttp(
				Collections.singletonMap(path, () -> metricsJSON));

			ReflectionTestUtil.setFieldValue(
				_performanceMetricResource, "_http", recordingMockHttp);

			PerformanceMetric performanceMetric = unsafeFunction.apply(
				TransformUtil.transformToArray(
					_depotEntries, DepotEntry::getDepotEntryId, Long.class));

			String location = recordingMockHttp.getLocation();

			_assertParameter(metricType, "assetSummaryMetricType", location);
			_assertParameter(
				String.valueOf(dataSourceId), "dataSourceId", location);
			_assertParameter(
				StringUtil.merge(
					TransformUtil.transformToArray(
						_depotEntries, DepotEntry::getGroupId, Long.class),
					StringPool.COMMA),
				"groupIds", location);
			_assertParameter(String.valueOf(rangeKey), "rangeKey", location);

			return performanceMetric;
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_performanceMetricResource, "_http", _http);
		}
	}

	private void _testGetPerformanceMetric(
			String groupBy, String metricType, String path)
		throws Exception {

		long dataSourceId = RandomTestUtil.nextLong();
		int rangeKey = RandomTestUtil.nextInt();
		int value1 = RandomTestUtil.nextInt();
		int value2 = RandomTestUtil.nextInt();
		String valueKey1 = RandomTestUtil.randomString();
		String valueKey2 = RandomTestUtil.randomString();

		PerformanceMetric performanceMetric = _getPerformanceMetric(
			dataSourceId,
			JSONUtil.put(
				"metricName", metricType
			).put(
				"metrics",
				JSONUtil.putAll(
					JSONUtil.put(
						"value", value1
					).put(
						"valueKey", valueKey1
					),
					JSONUtil.put(
						"value", value2
					).put(
						"valueKey", valueKey2
					))
			).toString(),
			metricType, path, rangeKey,
			depotEntryIds -> _performanceMetricResource.getPerformanceMetric(
				depotEntryIds, groupBy, metricType, rangeKey));

		Assert.assertEquals(metricType, performanceMetric.getMetricType());

		Metric[] metrics = performanceMetric.getMetrics();

		Assert.assertEquals(Arrays.toString(metrics), 2, metrics.length);

		_assertMetric(metrics[0], value1, valueKey1);
		_assertMetric(metrics[1], value2, valueKey2);
	}

	private void _testGetPerformanceMetricExport(
			String groupBy, String metricType, String path)
		throws Exception {

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

			String value = RandomTestUtil.randomString();

			RecordingMockHttp recordingMockHttp = new RecordingMockHttp(
				Collections.singletonMap(path, () -> value));

			ReflectionTestUtil.setFieldValue(
				_performanceMetricResource, "_http", recordingMockHttp);

			int rangeKey = RandomTestUtil.nextInt();

			Response response =
				_performanceMetricResource.getPerformanceMetricExport(
					TransformUtil.transformToArray(
						_depotEntries, DepotEntry::getDepotEntryId, Long.class),
					groupBy, metricType, rangeKey);

			Assert.assertEquals(
				StringBundler.concat(
					"attachment; filename=performance-metric-",
					StringUtil.toLowerCase(groupBy), "-", LocalDate.now(),
					".csv"),
				response.getHeaderString("Content-Disposition"));

			StreamingOutput streamingOutput =
				(StreamingOutput)response.getEntity();

			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			streamingOutput.write(byteArrayOutputStream);

			Assert.assertEquals(value, byteArrayOutputStream.toString());

			String location = recordingMockHttp.getLocation();

			_assertParameter(metricType, "assetSummaryMetricType", location);
			_assertParameter(
				String.valueOf(dataSourceId), "dataSourceId", location);
			_assertParameter(
				StringUtil.merge(
					TransformUtil.transformToArray(
						_depotEntries, DepotEntry::getGroupId, Long.class),
					StringPool.COMMA),
				"groupIds", location);
			_assertParameter(String.valueOf(rangeKey), "rangeKey", location);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_performanceMetricResource, "_http", _http);
		}
	}

	private void _testGetPerformanceMetricExportWithAnalyticsCloudNotConnected() {
		Assert.assertThrows(
			ForbiddenException.class,
			() -> _performanceMetricResource.getPerformanceMetricExport(
				TransformUtil.transformToArray(
					_depotEntries, DepotEntry::getDepotEntryId, Long.class),
				"categories", "viewsMetric", RandomTestUtil.nextInt()));
	}

	private void _testGetPerformanceMetricExportWithInvalidMetricType() {
		Assert.assertThrows(
			ValidationException.class,
			() -> _performanceMetricResource.getPerformanceMetricExport(
				TransformUtil.transformToArray(
					_depotEntries, DepotEntry::getDepotEntryId, Long.class),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.nextInt()));
	}

	private void _testGetPerformanceMetricWithAnalyticsCloudNotConnected() {
		Assert.assertThrows(
			ForbiddenException.class,
			() -> _performanceMetricResource.getPerformanceMetric(
				TransformUtil.transformToArray(
					_depotEntries, DepotEntry::getDepotEntryId, Long.class),
				"categories", "viewsMetric", RandomTestUtil.nextInt()));
	}

	private void _testGetPerformanceMetricWithInvalidMetricType() {
		Assert.assertThrows(
			ValidationException.class,
			() -> _performanceMetricResource.getPerformanceMetric(
				TransformUtil.transformToArray(
					_depotEntries, DepotEntry::getDepotEntryId, Long.class),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.nextInt()));
	}

	private void _testGetPerformanceMetricWithNoData() throws Exception {
		String metricType = "viewsMetric";
		int rangeKey = RandomTestUtil.nextInt();

		PerformanceMetric performanceMetric = _getPerformanceMetric(
			RandomTestUtil.nextLong(),
			JSONUtil.put(
				"metricName", metricType
			).put(
				"metrics", JSONUtil.putAll()
			).toString(),
			metricType, "/api/1.0/asset-metric/objectEntry/geolocation",
			rangeKey,
			depotEntryIds -> _performanceMetricResource.getPerformanceMetric(
				depotEntryIds, "location", metricType, rangeKey));

		Assert.assertEquals(metricType, performanceMetric.getMetricType());

		Metric[] metrics = performanceMetric.getMetrics();

		Assert.assertEquals(Arrays.toString(metrics), 0, metrics.length);
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private Http _http;

	@Inject
	private PerformanceMetricResource _performanceMetricResource;

}
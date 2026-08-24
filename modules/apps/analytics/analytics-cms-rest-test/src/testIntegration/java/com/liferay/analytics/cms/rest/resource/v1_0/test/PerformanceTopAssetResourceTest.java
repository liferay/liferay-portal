/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceTopAsset;
import com.liferay.analytics.cms.rest.client.dto.v1_0.Trend;
import com.liferay.analytics.cms.rest.client.http.HttpInvoker;
import com.liferay.analytics.cms.rest.client.pagination.Page;
import com.liferay.analytics.cms.rest.client.pagination.Pagination;
import com.liferay.analytics.cms.rest.client.resource.v1_0.PerformanceTopAssetResource;
import com.liferay.analytics.cms.rest.resource.v1_0.test.util.DepotEntryTestUtil;
import com.liferay.analytics.test.util.AnalyticsCloudHttpServer;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.net.HttpURLConnection;

import java.util.ArrayList;
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
public class PerformanceTopAssetResourceTest
	extends BasePerformanceTopAssetResourceTestCase {

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
	public void testGetPerformanceTopAssetExport() throws Exception {
		_testGetPerformanceTopAssetExportWithAnalyticsCloudNotConnected();
		_testGetPerformanceTopAssetExportResponse();
		_testGetPerformanceTopAssetExportURL();
		_testGetPerformanceTopAssetExportWithDepotEntryMemberUser();
	}

	@Override
	@Test
	public void testGetPerformanceTopAssetPage() throws Exception {
		_testGetPerformanceTopAssetPageWithAnalyticsCloudNotConnected();
		_testGetPerformanceTopAssetPageResponse();
		_testGetPerformanceTopAssetPageURL();
		_testGetPerformanceTopAssetPageWithDepotEntryMemberUser();
	}

	@Override
	@Test
	public void testGetPerformanceTopAssetPageWithPagination()
		throws Exception {

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/summaries", () -> "{}");

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			int page = RandomTestUtil.nextInt();
			int pageSize = RandomTestUtil.randomInt(1, 100);

			performanceTopAssetResource.getPerformanceTopAssetPage(
				null, RandomTestUtil.nextInt(), null, null,
				Pagination.of(page, pageSize), null);

			String location = analyticsCloudHttpServer.getLocation();

			_assertParameter(String.valueOf(page - 1), "page", location);
			_assertParameter(String.valueOf(pageSize), "size", location);
		}
	}

	private void _addDepotEntry() throws Exception {
		_depotEntries.add(
			DepotEntryTestUtil.addDepotEntry(testGroup.getGroupId()));
	}

	private void _assertParameter(
		String expectedValue, String name, String url) {

		Assert.assertEquals(
			expectedValue,
			URLCodec.decodeURL(
				HttpComponentsUtil.getParameter(url, name, false)));
	}

	private void _testGetPerformanceTopAssetExportResponse() throws Exception {
		String value = RandomTestUtil.randomString();

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/summaries/export",
					() -> value);

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			HttpInvoker.HttpResponse httpResponse =
				performanceTopAssetResource.
					getPerformanceTopAssetExportHttpResponse(
						null, RandomTestUtil.nextInt(), null, null, null);

			assertHttpResponseStatusCode(
				HttpURLConnection.HTTP_OK, httpResponse);

			Assert.assertEquals(value, httpResponse.getContent());
		}
	}

	private void _testGetPerformanceTopAssetExportURL() throws Exception {
		String dataSourceId = RandomTestUtil.randomString();

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/summaries/export",
					RandomTestUtil::randomString);

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(), dataSourceId, true,
						analyticsCloudHttpServer.getURL())) {

			String filterString = RandomTestUtil.randomString();
			int rangeKey = RandomTestUtil.nextInt();
			String search = RandomTestUtil.randomString();
			String sortFieldName1 = RandomTestUtil.randomString();
			String sortFieldName2 = RandomTestUtil.randomString();

			performanceTopAssetResource.getPerformanceTopAssetExport(
				TransformUtil.transformToArray(
					_depotEntries, DepotEntry::getDepotEntryId, Long.class),
				rangeKey, search, filterString,
				StringBundler.concat(
					sortFieldName1, ":desc,", sortFieldName2, ":asc"));

			String location = analyticsCloudHttpServer.getLocation();

			DepotEntryTestUtil.assertGroupIds(_depotEntries, location);

			_assertParameter(dataSourceId, "dataSourceId", location);
			_assertParameter(filterString, "filter", location);
			_assertParameter(search, "keywords", location);
			_assertParameter(String.valueOf(rangeKey), "rangeKey", location);
			_assertParameter(
				StringBundler.concat(
					sortFieldName1, ",desc,", sortFieldName2, ",asc"),
				"sort", location);
		}
	}

	private void _testGetPerformanceTopAssetExportWithAnalyticsCloudNotConnected()
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
				performanceTopAssetResource.
					getPerformanceTopAssetExportHttpResponse(
						null, RandomTestUtil.nextInt(),
						RandomTestUtil.randomString(), null, null));
		}
	}

	private void _testGetPerformanceTopAssetExportWithDepotEntryMemberUser()
		throws Exception {

		com.liferay.analytics.cms.rest.resource.v1_0.PerformanceTopAssetResource
			performanceTopAssetResource = ReflectionTestUtil.getFieldValue(
				this, "_performanceTopAssetResource");

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/summaries/export",
					() -> "{}");

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			DepotEntryTestUtil.withDepotEntryMemberUser(
				_depotEntries.get(0),
				() -> {
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer, null,
						depotEntryIds ->
							performanceTopAssetResource.
								getPerformanceTopAssetExport(
									depotEntryIds, RandomTestUtil.nextInt(),
									null, null, null));
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer,
						new DepotEntry[] {_depotEntries.get(0)},
						depotEntryIds ->
							performanceTopAssetResource.
								getPerformanceTopAssetExport(
									depotEntryIds, RandomTestUtil.nextInt(),
									null, null, null));
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer,
						_depotEntries.toArray(new DepotEntry[0]),
						depotEntryIds ->
							performanceTopAssetResource.
								getPerformanceTopAssetExport(
									depotEntryIds, RandomTestUtil.nextInt(),
									null, null, null));

					return null;
				});
		}
	}

	private void _testGetPerformanceTopAssetPageResponse() throws Exception {
		String assetId = RandomTestUtil.randomString();
		String assetTitle = RandomTestUtil.randomString();
		String assetType = RandomTestUtil.randomString();
		int downloads = RandomTestUtil.nextInt();
		double engagement = RandomTestUtil.nextDouble();
		double engagementTrend = RandomTestUtil.nextDouble();
		int impressions = RandomTestUtil.nextInt();
		int totalCount = RandomTestUtil.nextInt();
		int views = RandomTestUtil.nextInt();

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/summaries",
					() -> JSONUtil.put(
						"_embedded",
						JSONUtil.put(
							"assetSummaryMetrics",
							JSONUtil.putAll(
								JSONUtil.put(
									"assetId", assetId
								).put(
									"assetTitle", assetTitle
								).put(
									"assetType", assetType
								).put(
									"downloadsMetric",
									JSONUtil.put("value", downloads)
								).put(
									"engagementMetric",
									JSONUtil.put(
										"trend",
										JSONUtil.put(
											"percentage", engagementTrend
										).put(
											"trendClassification", "POSITIVE"
										)
									).put(
										"value", engagement
									)
								).put(
									"impressionsMetric",
									JSONUtil.put("value", impressions)
								).put(
									"viewsMetric", JSONUtil.put("value", views)
								)))
					).put(
						"page", JSONUtil.put("totalElements", totalCount)
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			int pageSize = RandomTestUtil.nextInt();

			Page<PerformanceTopAsset> page =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, RandomTestUtil.nextInt(), null, null,
					Pagination.of(1, pageSize), null);

			Assert.assertEquals(totalCount, page.getTotalCount());

			List<PerformanceTopAsset> performanceTopAssets =
				ListUtil.fromCollection(page.getItems());

			Assert.assertEquals(
				performanceTopAssets.toString(), 1,
				performanceTopAssets.size());

			PerformanceTopAsset performanceTopAsset = performanceTopAssets.get(
				0);

			Assert.assertEquals(
				downloads, performanceTopAsset.getDownloads(), 0);
			Assert.assertEquals(
				engagement, performanceTopAsset.getEngagement(), 0);
			Assert.assertEquals(
				assetId, performanceTopAsset.getExternalReferenceCode());
			Assert.assertEquals(
				impressions, performanceTopAsset.getImpressions(), 0);
			Assert.assertEquals(assetTitle, performanceTopAsset.getTitle());

			Trend trend = performanceTopAsset.getTrend();

			Assert.assertEquals(
				Trend.Classification.POSITIVE.toString(),
				String.valueOf(trend.getClassification()));
			Assert.assertEquals(engagementTrend, trend.getPercentage(), 0);

			Assert.assertEquals(assetType, performanceTopAsset.getType());
			Assert.assertEquals(views, performanceTopAsset.getViews(), 0);
		}
	}

	private void _testGetPerformanceTopAssetPageURL() throws Exception {
		String dataSourceId = RandomTestUtil.randomString();

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/summaries", () -> "{}");

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(), dataSourceId, true,
						analyticsCloudHttpServer.getURL())) {

			String filterString = RandomTestUtil.randomString();
			int page = RandomTestUtil.nextInt();
			int pageSize = RandomTestUtil.randomInt(1, 100);
			int rangeKey = RandomTestUtil.nextInt();
			String search = RandomTestUtil.randomString();
			String sortFieldName1 = RandomTestUtil.randomString();
			String sortFieldName2 = RandomTestUtil.randomString();

			performanceTopAssetResource.getPerformanceTopAssetPage(
				TransformUtil.transformToArray(
					_depotEntries, DepotEntry::getDepotEntryId, Long.class),
				rangeKey, search, filterString, Pagination.of(page, pageSize),
				StringBundler.concat(
					sortFieldName1, ":desc,", sortFieldName2, ":asc"));

			String location = analyticsCloudHttpServer.getLocation();

			DepotEntryTestUtil.assertGroupIds(_depotEntries, location);

			_assertParameter(dataSourceId, "dataSourceId", location);
			_assertParameter(filterString, "filter", location);
			_assertParameter(search, "keywords", location);
			_assertParameter(String.valueOf(page - 1), "page", location);
			_assertParameter(String.valueOf(rangeKey), "rangeKey", location);
			_assertParameter(String.valueOf(pageSize), "size", location);
			_assertParameter(
				StringBundler.concat(
					sortFieldName1, ",desc,", sortFieldName2, ",asc"),
				"sort", location);
		}
	}

	private void _testGetPerformanceTopAssetPageWithAnalyticsCloudNotConnected()
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
				performanceTopAssetResource.
					getPerformanceTopAssetPageHttpResponse(
						null, RandomTestUtil.nextInt(),
						RandomTestUtil.randomString(), null,
						Pagination.of(1, 10), null));
		}
	}

	private void _testGetPerformanceTopAssetPageWithDepotEntryMemberUser()
		throws Exception {

		com.liferay.analytics.cms.rest.resource.v1_0.PerformanceTopAssetResource
			performanceTopAssetResource = ReflectionTestUtil.getFieldValue(
				this, "_performanceTopAssetResource");

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/summaries", () -> "{}");

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			DepotEntryTestUtil.withDepotEntryMemberUser(
				_depotEntries.get(0),
				() -> {
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer, null,
						depotEntryIds ->
							performanceTopAssetResource.
								getPerformanceTopAssetPage(
									depotEntryIds, RandomTestUtil.nextInt(),
									null, null,
									com.liferay.portal.vulcan.pagination.
										Pagination.of(1, 10),
									null));
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer,
						new DepotEntry[] {_depotEntries.get(0)},
						depotEntryIds ->
							performanceTopAssetResource.
								getPerformanceTopAssetPage(
									depotEntryIds, RandomTestUtil.nextInt(),
									null, null,
									com.liferay.portal.vulcan.pagination.
										Pagination.of(1, 10),
									null));
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer,
						_depotEntries.toArray(new DepotEntry[0]),
						depotEntryIds ->
							performanceTopAssetResource.
								getPerformanceTopAssetPage(
									depotEntryIds, RandomTestUtil.nextInt(),
									null, null,
									com.liferay.portal.vulcan.pagination.
										Pagination.of(1, 10),
									null));

					return null;
				});
		}
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceTopAsset;
import com.liferay.analytics.cms.rest.dto.v1_0.Trend;
import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceTopAssetResource;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.core.UriInfo;

import java.io.ByteArrayOutputStream;

import java.time.LocalDate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
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

	@Override
	@Test
	public void testGetPerformanceTopAssetExport() throws Exception {
		_testGetPerformanceTopAssetExportWithAnalyticsCloudNotConnected();

		String dataSourceId = RandomTestUtil.randomString();

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(), dataSourceId)) {

			_testGetPerformanceTopAssetExportResponse();
			_testGetPerformanceTopAssetExportURL(dataSourceId);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_performanceTopAssetResource, "_http", _http);
		}
	}

	@Override
	@Test
	public void testGetPerformanceTopAssetPage() throws Exception {
		_testGetPerformanceTopAssetPageWithAnalyticsCloudNotConnected();

		String dataSourceId = RandomTestUtil.randomString();

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(), dataSourceId)) {

			_testGetPerformanceTopAssetPageResponse();
			_testGetPerformanceTopAssetPageURL(dataSourceId);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_performanceTopAssetResource, "_http", _http);
		}
	}

	@Override
	@Test
	public void testGetPerformanceTopAssetPageWithPagination()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString())) {

			RecordingMockHttp recordingMockHttp = _setUpRecordingMockHttp(
				"{}", "/api/1.0/asset-metric/objectEntry/summaries");

			_setUpUriInfo(null);

			int page = RandomTestUtil.nextInt();
			int pageSize = RandomTestUtil.nextInt();

			_performanceTopAssetResource.getPerformanceTopAssetPage(
				null, RandomTestUtil.nextInt(), null, null,
				Pagination.of(page, pageSize), null);

			String location = recordingMockHttp.getLocation();

			_assertParameter(String.valueOf(page - 1), "page", location);
			_assertParameter(String.valueOf(pageSize), "size", location);
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_performanceTopAssetResource, "_http", _http);
		}
	}

	private void _assertParameter(
		String expectedValue, String name, String url) {

		Assert.assertEquals(
			expectedValue,
			URLCodec.decodeURL(
				HttpComponentsUtil.getParameter(url, name, false)));
	}

	private RecordingMockHttp _setUpRecordingMockHttp(
		String json, String path) {

		RecordingMockHttp recordingMockHttp = new RecordingMockHttp(
			Collections.singletonMap(path, () -> json));

		ReflectionTestUtil.setFieldValue(
			_performanceTopAssetResource, "_http", recordingMockHttp);

		return recordingMockHttp;
	}

	private void _setUpUriInfo(String filterString) {
		MultivaluedMap<String, String> multivaluedMap =
			new MultivaluedHashMap<>();

		if (filterString != null) {
			multivaluedMap.putSingle("filter", filterString);
		}

		UriInfo uriInfo = (UriInfo)ProxyUtil.newProxyInstance(
			UriInfo.class.getClassLoader(), new Class<?>[] {UriInfo.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "getQueryParameters")) {
					return multivaluedMap;
				}

				return null;
			});

		ReflectionTestUtil.setFieldValue(
			_performanceTopAssetResource, "contextUriInfo", uriInfo);
	}

	private void _testGetPerformanceTopAssetExportResponse() throws Exception {
		String value = RandomTestUtil.randomString();

		_setUpRecordingMockHttp(
			value, "/api/1.0/asset-metric/objectEntry/summaries/export");

		_setUpUriInfo(null);

		Response response =
			_performanceTopAssetResource.getPerformanceTopAssetExport(
				null, RandomTestUtil.nextInt(), null, null, null);

		Assert.assertEquals(
			"attachment; filename=top-assets-" + LocalDate.now() + ".csv",
			response.getHeaderString("Content-Disposition"));

		StreamingOutput streamingOutput = (StreamingOutput)response.getEntity();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		streamingOutput.write(byteArrayOutputStream);

		Assert.assertEquals(value, byteArrayOutputStream.toString());
	}

	private void _testGetPerformanceTopAssetExportURL(String dataSourceId)
		throws Exception {

		RecordingMockHttp recordingMockHttp = _setUpRecordingMockHttp(
			RandomTestUtil.randomString(),
			"/api/1.0/asset-metric/objectEntry/summaries/export");

		String assetFilterString = RandomTestUtil.randomString();
		int rangeKey = RandomTestUtil.nextInt();
		String search = RandomTestUtil.randomString();
		Sort[] sorts = {
			new Sort(RandomTestUtil.randomString(), true),
			new Sort(RandomTestUtil.randomString(), false)
		};

		_setUpUriInfo(assetFilterString);

		_performanceTopAssetResource.getPerformanceTopAssetExport(
			null, rangeKey, search, null, sorts);

		String location = recordingMockHttp.getLocation();

		_assertParameter(dataSourceId, "dataSourceId", location);
		_assertParameter(assetFilterString, "filter", location);
		_assertParameter(search, "keywords", location);
		_assertParameter(String.valueOf(rangeKey), "rangeKey", location);

		StringBundler sb = new StringBundler();

		for (int i = 0; i < sorts.length; i++) {
			if (i > 0) {
				sb.append(StringPool.COMMA);
			}

			sb.append(sorts[i].getFieldName());
			sb.append(StringPool.COMMA);
			sb.append(sorts[i].isReverse() ? "desc" : "asc");
		}

		_assertParameter(sb.toString(), "sort", location);
	}

	private void _testGetPerformanceTopAssetExportWithAnalyticsCloudNotConnected() {
		Assert.assertThrows(
			ForbiddenException.class,
			() -> _performanceTopAssetResource.getPerformanceTopAssetExport(
				null, RandomTestUtil.nextInt(), RandomTestUtil.randomString(),
				null, null));
	}

	private void _testGetPerformanceTopAssetPageResponse() throws Exception {
		String assetId = RandomTestUtil.randomString();
		String assetTitle = RandomTestUtil.randomString();
		String assetType = RandomTestUtil.randomString();
		int downloads = RandomTestUtil.nextInt();
		double engagement = RandomTestUtil.nextDouble();
		double engagementTrend = RandomTestUtil.nextDouble();
		int impressions = RandomTestUtil.nextInt();
		int pageSize = RandomTestUtil.nextInt();
		int totalCount = RandomTestUtil.nextInt();
		int views = RandomTestUtil.nextInt();

		_setUpRecordingMockHttp(
			JSONUtil.put(
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
							"downloadsMetric", JSONUtil.put("value", downloads)
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
			).toString(),
			"/api/1.0/asset-metric/objectEntry/summaries");

		_setUpUriInfo(null);

		Page<PerformanceTopAsset> page =
			_performanceTopAssetResource.getPerformanceTopAssetPage(
				null, RandomTestUtil.nextInt(), null, null,
				Pagination.of(1, pageSize), null);

		Assert.assertEquals(totalCount, page.getTotalCount());

		List<PerformanceTopAsset> performanceTopAssets =
			(List<PerformanceTopAsset>)page.getItems();

		Assert.assertEquals(
			performanceTopAssets.toString(), 1, performanceTopAssets.size());

		PerformanceTopAsset performanceTopAsset = performanceTopAssets.get(0);

		Assert.assertEquals(downloads, performanceTopAsset.getDownloads(), 0);
		Assert.assertEquals(engagement, performanceTopAsset.getEngagement(), 0);
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

	private void _testGetPerformanceTopAssetPageURL(String dataSourceId)
		throws Exception {

		RecordingMockHttp recordingMockHttp = _setUpRecordingMockHttp(
			"{}", "/api/1.0/asset-metric/objectEntry/summaries");

		String assetFilterString = RandomTestUtil.randomString();
		int page = RandomTestUtil.nextInt();
		int pageSize = RandomTestUtil.nextInt();
		int rangeKey = RandomTestUtil.nextInt();
		String search = RandomTestUtil.randomString();
		Sort[] sorts = {
			new Sort(RandomTestUtil.randomString(), true),
			new Sort(RandomTestUtil.randomString(), false)
		};

		_setUpUriInfo(assetFilterString);

		_performanceTopAssetResource.getPerformanceTopAssetPage(
			null, rangeKey, search, null, Pagination.of(page, pageSize), sorts);

		String location = recordingMockHttp.getLocation();

		_assertParameter(dataSourceId, "dataSourceId", location);
		_assertParameter(assetFilterString, "filter", location);
		_assertParameter(search, "keywords", location);
		_assertParameter(String.valueOf(page - 1), "page", location);
		_assertParameter(String.valueOf(rangeKey), "rangeKey", location);
		_assertParameter(String.valueOf(pageSize), "size", location);

		StringBundler sb = new StringBundler();

		for (int i = 0; i < sorts.length; i++) {
			if (i > 0) {
				sb.append(StringPool.COMMA);
			}

			sb.append(sorts[i].getFieldName());
			sb.append(StringPool.COMMA);
			sb.append(sorts[i].isReverse() ? "desc" : "asc");
		}

		_assertParameter(sb.toString(), "sort", location);
	}

	private void _testGetPerformanceTopAssetPageWithAnalyticsCloudNotConnected() {
		Assert.assertThrows(
			ForbiddenException.class,
			() -> _performanceTopAssetResource.getPerformanceTopAssetPage(
				null, RandomTestUtil.nextInt(), RandomTestUtil.randomString(),
				null, Pagination.of(1, 10), null));
	}

	@Inject
	private Http _http;

	@Inject
	private PerformanceTopAssetResource _performanceTopAssetResource;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceAssetConsumption;
import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceAssetConsumptionItem;
import com.liferay.analytics.cms.rest.client.pagination.Pagination;
import com.liferay.analytics.cms.rest.client.resource.v1_0.PerformanceAssetConsumptionResource;
import com.liferay.analytics.cms.rest.resource.v1_0.test.util.DepotEntryTestUtil;
import com.liferay.analytics.test.util.AnalyticsCloudHttpServer;
import com.liferay.analytics.test.util.AnalyticsCompanyConfigurationTemporarySwapper;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.model.DepotEntry;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.Arrays;
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
public class PerformanceAssetConsumptionResourceTest
	extends BasePerformanceAssetConsumptionResourceTestCase {

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
	public void testGetPerformanceAssetConsumption() throws Exception {
		_testGetPerformanceAssetConsumptionGroupByStructure();
		_testGetPerformanceAssetConsumptionResponse();
		_testGetPerformanceAssetConsumptionURL();
		_testGetPerformanceAssetConsumptionWithDepotEntryMemberUser();
		_testGetPerformanceAssetConsumptionWithInvalidGroupBy();
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

	private void _testGetPerformanceAssetConsumptionGroupByStructure()
		throws Exception {

		ObjectDefinition basicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", testCompany.getCompanyId());
		ObjectDefinition blogObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BLOG", testCompany.getCompanyId());

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/asset-consumption",
					() -> JSONUtil.put(
						"metrics",
						JSONUtil.putAll(
							JSONUtil.put(
								"count", 30
							).put(
								"key", RandomTestUtil.randomString()
							).put(
								"title",
								basicWebContentObjectDefinition.getName()
							),
							JSONUtil.put(
								"count", 20
							).put(
								"key", RandomTestUtil.randomString()
							).put(
								"title", blogObjectDefinition.getName()
							))
					).put(
						"total", 2
					).put(
						"totalCount", 50
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			PerformanceAssetConsumption performanceAssetConsumption =
				performanceAssetConsumptionResource.
					getPerformanceAssetConsumption(
						null, null, "structure", RandomTestUtil.nextInt(), null,
						null, null, Pagination.of(1, 10));

			PerformanceAssetConsumptionItem[] performanceAssetConsumptionItems =
				performanceAssetConsumption.
					getPerformanceAssetConsumptionItems();

			Assert.assertEquals(
				Arrays.toString(performanceAssetConsumptionItems), 2,
				performanceAssetConsumptionItems.length);
			Assert.assertEquals(
				30L, (long)performanceAssetConsumptionItems[0].getCount());
			Assert.assertEquals(
				basicWebContentObjectDefinition.getExternalReferenceCode(),
				performanceAssetConsumptionItems[0].getKey());
			Assert.assertEquals(
				basicWebContentObjectDefinition.getLabel(
					LocaleUtil.getDefault()),
				performanceAssetConsumptionItems[0].getTitle());
			Assert.assertEquals(
				20L, (long)performanceAssetConsumptionItems[1].getCount());
			Assert.assertEquals(
				blogObjectDefinition.getExternalReferenceCode(),
				performanceAssetConsumptionItems[1].getKey());
			Assert.assertEquals(
				blogObjectDefinition.getLabel(LocaleUtil.getDefault()),
				performanceAssetConsumptionItems[1].getTitle());

			Assert.assertEquals(
				2L,
				(long)
					performanceAssetConsumption.
						getPerformanceAssetConsumptionItemsCount());
			Assert.assertEquals(
				50L, (long)performanceAssetConsumption.getTotalCount());
		}
	}

	private void _testGetPerformanceAssetConsumptionResponse()
		throws Exception {

		String key1 = RandomTestUtil.randomString();
		String key2 = RandomTestUtil.randomString();
		String title1 = RandomTestUtil.randomString();
		String title2 = RandomTestUtil.randomString();

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/asset-consumption",
					() -> JSONUtil.put(
						"metrics",
						JSONUtil.putAll(
							JSONUtil.put(
								"count", 10
							).put(
								"key", key1
							).put(
								"title", title1
							),
							JSONUtil.put(
								"count", 20
							).put(
								"key", key2
							).put(
								"title", title2
							))
					).put(
						"total", 2
					).put(
						"totalCount", 30
					).toString());

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(),
						RandomTestUtil.randomString(), true,
						analyticsCloudHttpServer.getURL())) {

			PerformanceAssetConsumption performanceAssetConsumption =
				performanceAssetConsumptionResource.
					getPerformanceAssetConsumption(
						null, null, "category", RandomTestUtil.nextInt(), null,
						null, null, Pagination.of(1, 10));

			PerformanceAssetConsumptionItem[] performanceAssetConsumptionItems =
				performanceAssetConsumption.
					getPerformanceAssetConsumptionItems();

			Assert.assertEquals(
				Arrays.toString(performanceAssetConsumptionItems), 2,
				performanceAssetConsumptionItems.length);
			Assert.assertEquals(
				10L, (long)performanceAssetConsumptionItems[0].getCount());
			Assert.assertEquals(
				key1, performanceAssetConsumptionItems[0].getKey());
			Assert.assertEquals(
				title1, performanceAssetConsumptionItems[0].getTitle());
			Assert.assertEquals(
				20L, (long)performanceAssetConsumptionItems[1].getCount());
			Assert.assertEquals(
				key2, performanceAssetConsumptionItems[1].getKey());
			Assert.assertEquals(
				title2, performanceAssetConsumptionItems[1].getTitle());

			Assert.assertEquals(
				2L,
				(long)
					performanceAssetConsumption.
						getPerformanceAssetConsumptionItemsCount());
			Assert.assertEquals(
				30L, (long)performanceAssetConsumption.getTotalCount());
		}
	}

	private void _testGetPerformanceAssetConsumptionURL() throws Exception {
		String dataSourceId = RandomTestUtil.randomString();

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/asset-consumption",
					() -> "{}");

			AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId(), dataSourceId, true,
						analyticsCloudHttpServer.getURL())) {

			ObjectDefinition objectDefinition =
				_objectDefinitionLocalService.
					getObjectDefinitionByExternalReferenceCode(
						"L_CMS_BLOG", testCompany.getCompanyId());

			long categoryId = RandomTestUtil.nextLong();
			int page = RandomTestUtil.nextInt();
			int rangeKey = RandomTestUtil.nextInt();
			int size = RandomTestUtil.randomInt(1, 100);
			long tagId = RandomTestUtil.nextLong();
			long vocabularyId = RandomTestUtil.nextLong();

			performanceAssetConsumptionResource.getPerformanceAssetConsumption(
				categoryId,
				TransformUtil.transformToArray(
					_depotEntries, DepotEntry::getDepotEntryId, Long.class),
				"tag", rangeKey, objectDefinition.getObjectDefinitionId(),
				tagId, vocabularyId, Pagination.of(page, size));

			String location = analyticsCloudHttpServer.getLocation();

			DepotEntryTestUtil.assertGroupIds(_depotEntries, location);

			_assertParameter("viewsMetric", "assetSummaryMetricType", location);
			_assertParameter(
				String.valueOf(categoryId), "categoryId", location);
			_assertParameter(dataSourceId, "dataSourceId", location);
			_assertParameter("tag", "groupBy", location);
			_assertParameter(
				objectDefinition.getName(), "objectType", location);
			_assertParameter(String.valueOf(page - 1), "page", location);
			_assertParameter(String.valueOf(rangeKey), "rangeKey", location);
			_assertParameter(String.valueOf(size), "size", location);
			_assertParameter(String.valueOf(tagId), "tagId", location);
			_assertParameter(
				String.valueOf(vocabularyId), "vocabularyId", location);
		}
	}

	private void _testGetPerformanceAssetConsumptionWithDepotEntryMemberUser()
		throws Exception {

		com.liferay.analytics.cms.rest.resource.v1_0.
			PerformanceAssetConsumptionResource
				performanceAssetConsumptionResource =
					ReflectionTestUtil.getFieldValue(
						this, "_performanceAssetConsumptionResource");

		try (AnalyticsCloudHttpServer analyticsCloudHttpServer =
				new AnalyticsCloudHttpServer(
					"/api/1.0/asset-metric/objectEntry/asset-consumption",
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
							performanceAssetConsumptionResource.
								getPerformanceAssetConsumption(
									null, depotEntryIds, "tag",
									RandomTestUtil.nextInt(), null, null, null,
									com.liferay.portal.vulcan.pagination.
										Pagination.of(1, 10)));
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer,
						new DepotEntry[] {_depotEntries.get(0)},
						depotEntryIds ->
							performanceAssetConsumptionResource.
								getPerformanceAssetConsumption(
									null, depotEntryIds, "tag",
									RandomTestUtil.nextInt(), null, null, null,
									com.liferay.portal.vulcan.pagination.
										Pagination.of(1, 10)));
					DepotEntryTestUtil.assertNoRequest(
						analyticsCloudHttpServer,
						_depotEntries.toArray(new DepotEntry[0]),
						depotEntryIds ->
							performanceAssetConsumptionResource.
								getPerformanceAssetConsumption(
									null, depotEntryIds, "tag",
									RandomTestUtil.nextInt(), null, null, null,
									com.liferay.portal.vulcan.pagination.
										Pagination.of(1, 10)));

					return null;
				});
		}
	}

	private void _testGetPerformanceAssetConsumptionWithInvalidGroupBy()
		throws Exception {

		try (AnalyticsCompanyConfigurationTemporarySwapper
				analyticsCompanyConfigurationTemporarySwapper =
					new AnalyticsCompanyConfigurationTemporarySwapper(
						testCompany.getCompanyId());
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				HttpURLConnection.HTTP_BAD_REQUEST,
				performanceAssetConsumptionResource.
					getPerformanceAssetConsumptionHttpResponse(
						null, null, RandomTestUtil.randomString(),
						RandomTestUtil.nextInt(), null, null, null,
						Pagination.of(1, 10)));
		}
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}
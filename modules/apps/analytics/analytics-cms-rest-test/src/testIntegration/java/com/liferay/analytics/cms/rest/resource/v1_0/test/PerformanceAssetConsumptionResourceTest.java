/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceAssetConsumption;
import com.liferay.analytics.cms.rest.dto.v1_0.PerformanceAssetConsumptionItem;
import com.liferay.analytics.cms.rest.resource.v1_0.PerformanceAssetConsumptionResource;
import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.function.transform.TransformUtil;
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
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import jakarta.ws.rs.BadRequestException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
		_performanceAssetConsumptionResource.setContextAcceptLanguage(
			new AcceptLanguage() {

				@Override
				public List<Locale> getLocales() {
					return Arrays.asList(LocaleUtil.getDefault());
				}

				@Override
				public String getPreferredLanguageId() {
					return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
				}

				@Override
				public Locale getPreferredLocale() {
					return LocaleUtil.getDefault();
				}

			});

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

			_testGetPerformanceAssetConsumptionGroupByStructure();
			_testGetPerformanceAssetConsumptionResponse();
			_testGetPerformanceAssetConsumptionURL(dataSourceId);
			_testGetPerformanceAssetConsumptionWithInvalidGroupBy();
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_performanceAssetConsumptionResource, "_http", _http);
		}
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

	private void _assertParameter(
		String expectedValue, String name, String url) {

		Assert.assertEquals(
			expectedValue,
			URLCodec.decodeURL(
				HttpComponentsUtil.getParameter(url, name, false)));
	}

	private RecordingMockHttp _setUpRecordingMockHttp(String json) {
		RecordingMockHttp recordingMockHttp = new RecordingMockHttp(
			Collections.singletonMap(
				"/api/1.0/asset-metric/objectEntry/asset-consumption",
				() -> json));

		ReflectionTestUtil.setFieldValue(
			_performanceAssetConsumptionResource, "_http", recordingMockHttp);

		return recordingMockHttp;
	}

	private void _testGetPerformanceAssetConsumptionGroupByStructure()
		throws Exception {

		CMSTestUtil.getOrAddGroup(
			PerformanceAssetConsumptionResourceTest.class);

		ObjectDefinition basicWebContentObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", testCompany.getCompanyId());
		ObjectDefinition blogObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BLOG", testCompany.getCompanyId());

		_setUpRecordingMockHttp(
			JSONUtil.put(
				"metrics",
				JSONUtil.putAll(
					JSONUtil.put(
						"count", 30
					).put(
						"key", RandomTestUtil.randomString()
					).put(
						"title", basicWebContentObjectDefinition.getName()
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

		PerformanceAssetConsumption performanceAssetConsumption =
			_performanceAssetConsumptionResource.getPerformanceAssetConsumption(
				null, null, "structure", RandomTestUtil.nextInt(), null, null,
				null, Pagination.of(1, 10));

		PerformanceAssetConsumptionItem[] performanceAssetConsumptionItems =
			performanceAssetConsumption.getPerformanceAssetConsumptionItems();

		Assert.assertEquals(
			Arrays.toString(performanceAssetConsumptionItems), 2,
			performanceAssetConsumptionItems.length);
		Assert.assertEquals(
			30L, (long)performanceAssetConsumptionItems[0].getCount());
		Assert.assertEquals(
			basicWebContentObjectDefinition.getExternalReferenceCode(),
			performanceAssetConsumptionItems[0].getKey());
		Assert.assertEquals(
			basicWebContentObjectDefinition.getLabel(LocaleUtil.getDefault()),
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

	private void _testGetPerformanceAssetConsumptionResponse()
		throws Exception {

		String key1 = RandomTestUtil.randomString();
		String key2 = RandomTestUtil.randomString();
		String title1 = RandomTestUtil.randomString();
		String title2 = RandomTestUtil.randomString();

		_setUpRecordingMockHttp(
			JSONUtil.put(
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

		PerformanceAssetConsumption performanceAssetConsumption =
			_performanceAssetConsumptionResource.getPerformanceAssetConsumption(
				null, null, "category", RandomTestUtil.nextInt(), null, null,
				null, Pagination.of(1, 10));

		PerformanceAssetConsumptionItem[] performanceAssetConsumptionItems =
			performanceAssetConsumption.getPerformanceAssetConsumptionItems();

		Assert.assertEquals(
			Arrays.toString(performanceAssetConsumptionItems), 2,
			performanceAssetConsumptionItems.length);
		Assert.assertEquals(
			10L, (long)performanceAssetConsumptionItems[0].getCount());
		Assert.assertEquals(key1, performanceAssetConsumptionItems[0].getKey());
		Assert.assertEquals(
			title1, performanceAssetConsumptionItems[0].getTitle());
		Assert.assertEquals(
			20L, (long)performanceAssetConsumptionItems[1].getCount());
		Assert.assertEquals(key2, performanceAssetConsumptionItems[1].getKey());
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

	private void _testGetPerformanceAssetConsumptionURL(long dataSourceId)
		throws Exception {

		RecordingMockHttp recordingMockHttp = _setUpRecordingMockHttp("{}");

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BLOG", testCompany.getCompanyId());

		long categoryId = RandomTestUtil.nextLong();
		int page = RandomTestUtil.nextInt();
		int rangeKey = RandomTestUtil.nextInt();
		int size = RandomTestUtil.nextInt();
		long tagId = RandomTestUtil.nextLong();
		long vocabularyId = RandomTestUtil.nextLong();

		_performanceAssetConsumptionResource.getPerformanceAssetConsumption(
			categoryId,
			TransformUtil.transformToArray(
				_depotEntries, DepotEntry::getDepotEntryId, Long.class),
			"tag", rangeKey, objectDefinition.getObjectDefinitionId(), tagId,
			vocabularyId, Pagination.of(page, size));

		String location = recordingMockHttp.getLocation();

		_assertParameter("viewsMetric", "assetSummaryMetricType", location);
		_assertParameter(String.valueOf(categoryId), "categoryId", location);
		_assertParameter(
			String.valueOf(dataSourceId), "dataSourceId", location);
		_assertParameter("tag", "groupBy", location);
		_assertParameter(
			StringUtil.merge(
				TransformUtil.transformToArray(
					_depotEntries, DepotEntry::getGroupId, Long.class),
				StringPool.COMMA),
			"groupIds", location);
		_assertParameter(objectDefinition.getName(), "objectType", location);
		_assertParameter(String.valueOf(page - 1), "page", location);
		_assertParameter(String.valueOf(rangeKey), "rangeKey", location);
		_assertParameter(String.valueOf(size), "size", location);
		_assertParameter(String.valueOf(tagId), "tagId", location);
		_assertParameter(
			String.valueOf(vocabularyId), "vocabularyId", location);
	}

	private void _testGetPerformanceAssetConsumptionWithInvalidGroupBy() {
		Assert.assertThrows(
			BadRequestException.class,
			() ->
				_performanceAssetConsumptionResource.
					getPerformanceAssetConsumption(
						null, null, RandomTestUtil.randomString(),
						RandomTestUtil.nextInt(), null, null, null,
						Pagination.of(1, 10)));
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private Http _http;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private PerformanceAssetConsumptionResource
		_performanceAssetConsumptionResource;

}
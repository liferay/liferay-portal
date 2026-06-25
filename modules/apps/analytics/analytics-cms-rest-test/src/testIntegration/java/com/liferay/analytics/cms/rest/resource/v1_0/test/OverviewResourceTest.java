/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Overview;
import com.liferay.analytics.cms.rest.client.dto.v1_0.Trend;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRel;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class OverviewResourceTest extends BaseOverviewResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	@Test
	public void testGetContentOverview() throws Exception {
		_setUpCMSContext();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_WEB_CONTENT", testCompany.getCompanyId());

		Map<String, Serializable> objectEntryValues =
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build();

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), objectDefinition, objectEntryValues);

		Trend positiveTrend = new Trend();

		positiveTrend.setClassification(Trend.Classification.POSITIVE);
		positiveTrend.setPercentage(100.0);

		Assert.assertEquals(
			new Overview() {
				{
					categoriesCount = 0L;
					tagsCount = 0L;
					totalCount = 1L;
					trend = positiveTrend;
					vocabulariesCount = 0L;
				}
			},
			overviewResource.getContentOverview(null, null, null, 7, null));

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), objectDefinition, objectEntryValues);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			objectDefinition.getClassName(), _objectEntry.getObjectEntryId());

		_assetVocabulary = _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), _depotEntry.getGroupId(), "Vocabulary",
			_serviceContext);

		_assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _depotEntry.getGroupId(), "Category 1",
			_assetVocabulary.getVocabularyId(), _serviceContext);

		_assetEntryAssetCategoryRel =
			_assetEntryAssetCategoryRelLocalService.
				addAssetEntryAssetCategoryRel(
					assetEntry.getEntryId(), _assetCategory1.getCategoryId());

		_assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _depotEntry.getGroupId(), "Category 2",
			_assetVocabulary.getVocabularyId(), _serviceContext);

		_assetEntryAssetCategoryRel =
			_assetEntryAssetCategoryRelLocalService.
				addAssetEntryAssetCategoryRel(
					assetEntry.getEntryId(), _assetCategory2.getCategoryId());

		Assert.assertEquals(
			new Overview() {
				{
					categoriesCount = 2L;
					tagsCount = 0L;
					totalCount = 2L;
					trend = positiveTrend;
					vocabulariesCount = 1L;
				}
			},
			overviewResource.getContentOverview(null, null, null, 7, null));

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), objectDefinition, objectEntryValues,
			RandomTestUtil.randomString());

		Assert.assertEquals(
			new Overview() {
				{
					categoriesCount = 2L;
					tagsCount = 1L;
					totalCount = 3L;
					trend = positiveTrend;
					vocabulariesCount = 1L;
				}
			},
			overviewResource.getContentOverview(null, null, null, 7, null));
	}

	@Override
	@Test
	public void testGetFileOverview() throws Exception {
		_setUpCMSContext();

		DLFolder dlFolder = DLTestUtil.addDLFolder(_depotEntry.getGroupId());
		byte[] bytes = FileUtil.getBytes(getClass(), "dependencies/test.pdf");

		_dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), dlFolder.getGroupId(),
			dlFolder.getRepositoryId(), dlFolder.getFolderId(),
			RandomTestUtil.randomString() + ".pdf",
			ContentTypes.APPLICATION_PDF, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(bytes), bytes.length, null, null,
			null,
			ServiceContextTestUtil.getServiceContext(dlFolder.getGroupId()));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", testCompany.getCompanyId());

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"file", String.valueOf(_dlFileEntry.getFileEntryId())
			).put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build());

		Trend positiveTrend = new Trend();

		positiveTrend.setClassification(Trend.Classification.POSITIVE);
		positiveTrend.setPercentage(100.0);

		Assert.assertEquals(
			new Overview() {
				{
					categoriesCount = 0L;
					tagsCount = 0L;
					totalCount = 1L;
					trend = positiveTrend;
					vocabulariesCount = 0L;
				}
			},
			overviewResource.getFileOverview(null, null, null, 7, null));
	}

	private void _setUpCMSContext() throws Exception {
		CMSTestUtil.getOrAddGroup(OverviewResourceTest.class);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testGroup.getGroupId(), TestPropsValues.getUserId());

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_SPACE, _serviceContext);
	}

	@DeleteAfterTestRun
	private AssetCategory _assetCategory1;

	@DeleteAfterTestRun
	private AssetCategory _assetCategory2;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@DeleteAfterTestRun
	private AssetEntryAssetCategoryRel _assetEntryAssetCategoryRel;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@DeleteAfterTestRun
	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private DLFileEntry _dlFileEntry;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry;

	private ServiceContext _serviceContext;

}
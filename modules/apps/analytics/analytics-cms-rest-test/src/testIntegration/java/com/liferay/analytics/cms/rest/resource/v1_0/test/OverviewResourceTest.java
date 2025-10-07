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
import com.liferay.batch.engine.unit.BatchEngineUnitProcessor;
import com.liferay.batch.engine.unit.BatchEngineUnitReader;
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
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Rachael Koestartyo
 */
@FeatureFlags(
	featureFlags = {
		@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-21926"),
		@FeatureFlag("LPD-31149"), @FeatureFlag("LPD-34594"),
		@FeatureFlag("LPS-179669")
	}
)
@RunWith(Arquillian.class)
public class OverviewResourceTest extends BaseOverviewResourceTestCase {

	@Override
	@Test
	public void testGetContentOverview() throws Exception {
		_setUpCMSContext();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_BASIC_WEB_CONTENT", testCompany.getCompanyId());

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), objectDefinition, Collections.emptyMap());

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
			_depotEntry.getGroupId(), objectDefinition, Collections.emptyMap());

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
			_depotEntry.getGroupId(), objectDefinition, Collections.emptyMap(),
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
		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

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
					"L_BASIC_DOCUMENT", testCompany.getCompanyId());

		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			_depotEntry.getGroupId(), objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"file", String.valueOf(_dlFileEntry.getFileEntryId())
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

	private void _deleteFile(Bundle bundle, String fileName) {
		File file = bundle.getDataFile(
			".com.liferay.site.initializer.cms.internal.batch." + fileName +
				".batch.engine.data.json.0.processed");

		if ((file != null) && file.exists()) {
			file.delete();
		}
	}

	private void _setUpCMSContext() throws Exception {
		Bundle testBundle = FrameworkUtil.getBundle(OverviewResourceTest.class);

		BundleContext bundleContext = testBundle.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			if (Objects.equals(
					bundle.getSymbolicName(),
					"com.liferay.site.initializer.cms")) {

				_deleteFile(bundle, "00.list.type.definition");
				_deleteFile(bundle, "01.object.folder");
				_deleteFile(bundle, "02.object.definition");

				CompletableFuture<Void> completableFuture =
					_batchEngineUnitProcessor.processBatchEngineUnits(
						_batchEngineUnitReader.getBatchEngineUnits(bundle));

				completableFuture.join();

				break;
			}
		}

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testGroup.getGroupId(), TestPropsValues.getUserId());

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY, _serviceContext);
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

	@Inject
	private BatchEngineUnitProcessor _batchEngineUnitProcessor;

	@Inject
	private BatchEngineUnitReader _batchEngineUnitReader;

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
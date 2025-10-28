/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test.util.lar;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportClassedModelUtil;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.portal.kernel.comment.CommentManagerUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedGroupedModel;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.IdentityServiceContextFunction;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.DateTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.kernel.zip.ZipReader;
import com.liferay.portal.kernel.zip.ZipReaderFactory;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;
import com.liferay.portal.test.rule.Inject;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.service.RatingsEntryLocalServiceUtil;
import com.liferay.ratings.test.util.RatingsTestUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Daniel Kocsis
 * @author Máté Thurzó
 */
public abstract class BaseStagedModelDataHandlerTestCase {

	@Before
	public void setUp() throws Exception {
		liveGroup = GroupTestUtil.addGroup();

		stagingGroup = GroupTestUtil.addGroup();

		stagingGroup.setLiveGroupId(liveGroup.getGroupId());

		stagingGroup = GroupLocalServiceUtil.updateGroup(stagingGroup);

		UserTestUtil.setUser(TestPropsValues.getUser());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));
	}

	@After
	public void tearDown() throws Exception {
		GroupLocalServiceUtil.deleteGroup(stagingGroup);
		GroupLocalServiceUtil.deleteGroup(liveGroup);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testCleanAssetCategoriesAndTags() throws Exception {
		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		AssetEntry assetEntry = fetchAssetEntry(stagedModel, stagingGroup);

		if (assetEntry == null) {
			return;
		}

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			stagingGroup.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			stagingGroup.getGroupId(), assetVocabulary.getVocabularyId());

		AssetTag assetTag = AssetTestUtil.addTag(stagingGroup.getGroupId());

		AssetEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			assetEntry.getCreateDate(), assetEntry.getModifiedDate(),
			assetEntry.getClassName(), assetEntry.getClassPK(),
			assetEntry.getClassUuid(), assetEntry.getClassTypeId(),
			new long[] {assetCategory.getCategoryId()},
			new String[] {assetTag.getName()}, assetEntry.isListable(),
			assetEntry.isVisible(), assetEntry.getStartDate(),
			assetEntry.getEndDate(), assetEntry.getPublishDate(),
			assetEntry.getExpirationDate(), assetEntry.getMimeType(),
			assetEntry.getTitle(), assetEntry.getDescription(),
			assetEntry.getSummary(), assetEntry.getUrl(),
			assetEntry.getLayoutUuid(), assetEntry.getHeight(),
			assetEntry.getWidth(), assetEntry.getPriority());

		exportImportStagedModel(stagedModel);

		StagedModel importedStagedModel = getStagedModel(
			stagedModel.getUuid(), liveGroup);

		Assert.assertNotNull(importedStagedModel);

		AssetEntry importedAssetEntry = fetchAssetEntry(
			importedStagedModel, liveGroup);

		Assert.assertNotNull(importedAssetEntry);

		List<AssetTag> assetTags = importedAssetEntry.getTags();

		Assert.assertFalse(assetTags.toString(), assetTags.isEmpty());

		List<AssetCategory> assetCategories =
			importedAssetEntry.getCategories();

		Assert.assertFalse(
			assetCategories.toString(), assetCategories.isEmpty());

		assetEntry = fetchAssetEntry(stagedModel, stagingGroup);

		AssetEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			assetEntry.getCreateDate(), assetEntry.getModifiedDate(),
			assetEntry.getClassName(), assetEntry.getClassPK(),
			assetEntry.getClassUuid(), assetEntry.getClassTypeId(), new long[0],
			new String[0], assetEntry.isListable(), assetEntry.isVisible(),
			assetEntry.getStartDate(), assetEntry.getEndDate(),
			assetEntry.getPublishDate(), assetEntry.getExpirationDate(),
			assetEntry.getMimeType(), assetEntry.getTitle(),
			assetEntry.getDescription(), assetEntry.getSummary(),
			assetEntry.getUrl(), assetEntry.getLayoutUuid(),
			assetEntry.getHeight(), assetEntry.getWidth(),
			assetEntry.getPriority());

		exportImportStagedModel(stagedModel);

		importedStagedModel = getStagedModel(stagedModel.getUuid(), liveGroup);

		Assert.assertNotNull(importedStagedModel);

		importedAssetEntry = fetchAssetEntry(importedStagedModel, liveGroup);

		Assert.assertNotNull(importedAssetEntry);

		assetTags = importedAssetEntry.getTags();

		Assert.assertTrue(assetTags.toString(), assetTags.isEmpty());

		assetCategories = importedAssetEntry.getCategories();

		Assert.assertTrue(
			assetCategories.toString(), assetCategories.isEmpty());
	}

	@Test
	public void testCleanStagedModelDataHandler() throws Exception {

		// Export

		initExport();

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		// Comments

		addComments(stagedModel);

		// Ratings

		addRatings(stagedModel);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		validateExport(
			portletDataContext, stagedModel, dependentStagedModelsMap);

		// Import

		initImport();

		deleteStagedModel(stagedModel, dependentStagedModelsMap, stagingGroup);

		// Reread the staged model for import from ZIP for true testing

		StagedModel exportedStagedModel = readExportedStagedModel(stagedModel);

		Assert.assertNotNull(exportedStagedModel);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedModel);

		StagedModel importedStagedModel = getStagedModel(
			exportedStagedModel.getUuid(), liveGroup);

		validateImportedStagedModel(exportedStagedModel, importedStagedModel);

		Assert.assertNotEquals(
			exportedStagedModel.getPrimaryKeyObj(),
			importedStagedModel.getPrimaryKeyObj());
	}

	@Test
	public void testExportImportWithDefaultData() throws Exception {

		// Export default data

		initExport();

		Map<String, List<StagedModel>> defaultDependentStagedModelsMap =
			addDefaultDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addDefaultStagedModel(
			stagingGroup, defaultDependentStagedModelsMap);

		if (stagedModel == null) {
			return;
		}

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		validateExport(
			portletDataContext, stagedModel, defaultDependentStagedModelsMap);

		// Add default data to live site

		Map<String, List<StagedModel>> secondDependentStagedModelsMap =
			addDefaultDependentStagedModelsMap(liveGroup);

		addDefaultStagedModel(liveGroup, secondDependentStagedModelsMap);

		// Import

		initImport();

		StagedModel exportedStagedModel = readExportedStagedModel(stagedModel);

		Assert.assertNotNull(exportedStagedModel);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedModel);

		// Import again for more robustness (i.e. filter name issues)

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedModel);

		StagedModel importedModel = getStagedModel(
			exportedStagedModel.getUuid(), liveGroup);

		Assert.assertNotNull(importedModel);
	}

	public void testLastPublishDate() throws Exception {
		if (!supportLastPublishDateUpdate()) {
			return;
		}

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<>();

		StagedGroupedModel stagedGroupedModel =
			(StagedGroupedModel)addStagedModel(
				stagingGroup, dependentStagedModelsMap);

		Assert.assertNull(stagedGroupedModel.getLastPublishDate());

		initExport();

		// Update last publish date

		Map<String, String[]> parameterMap =
			portletDataContext.getParameterMap();

		parameterMap.put(
			PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE,
			new String[] {Boolean.TRUE.toString()});

		try {
			ExportImportThreadLocal.setPortletStagingInProcess(true);

			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, stagedGroupedModel);
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}

		Assert.assertEquals(
			portletDataContext.getEndDate(),
			stagedGroupedModel.getLastPublishDate());

		// Do not update last publish date

		Date originalLastPublishDate = stagedGroupedModel.getLastPublishDate();

		parameterMap.put(
			PortletDataHandlerKeys.UPDATE_LAST_PUBLISH_DATE,
			new String[] {Boolean.TRUE.toString()});

		try {
			ExportImportThreadLocal.setPortletStagingInProcess(true);

			StagedModelDataHandlerUtil.exportStagedModel(
				portletDataContext, stagedGroupedModel);
		}
		finally {
			ExportImportThreadLocal.setPortletStagingInProcess(false);
		}

		Assert.assertEquals(
			originalLastPublishDate, stagedGroupedModel.getLastPublishDate());
	}

	@Test
	public void testStagedModelDataHandler() throws Exception {

		// Export

		initExport();

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		// Assets

		StagedModelAssets stagedModelAssets = updateAssetEntry(
			stagedModel, stagingGroup);

		// Comments

		addComments(stagedModel);

		// Ratings

		addRatings(stagedModel);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		validateExport(
			portletDataContext, stagedModel, dependentStagedModelsMap);

		// Import

		initImport();

		// Reread the staged model for import from ZIP for true testing

		StagedModel exportedStagedModel = readExportedStagedModel(stagedModel);

		Assert.assertNotNull(exportedStagedModel);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedModel);

		validateImport(
			stagedModel, stagedModelAssets, dependentStagedModelsMap,
			liveGroup);
	}

	@Test
	public void testVersioning() throws Exception {
		if (!isVersionableStagedModel()) {
			return;
		}

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		stagedModel = addVersion(stagedModel);

		exportImportStagedModel(stagedModel);

		StagedModel importedStagedModel = getStagedModel(
			stagedModel.getUuid(), liveGroup);

		Assert.assertNotNull(importedStagedModel);

		validateImportedStagedModel(stagedModel, importedStagedModel);
	}

	@Test
	public void testVersioning2() throws Exception {
		if (!isVersionableStagedModel()) {
			return;
		}

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		// Make sure the dates are different

		Thread.sleep(4000);

		exportImportStagedModel(stagedModel);

		StagedModel importedStagedModel = getStagedModel(
			stagedModel.getUuid(), liveGroup);

		validateImportedStagedModel(stagedModel, importedStagedModel);

		stagedModel = addVersion(stagedModel);

		exportImportStagedModel(stagedModel);

		importedStagedModel = getStagedModel(stagedModel.getUuid(), liveGroup);

		validateImportedStagedModel(stagedModel, importedStagedModel);
	}

	@Test
	public void testVersioningExportImportTwice() throws Exception {
		if (!isVersionableStagedModel()) {
			return;
		}

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			addDependentStagedModelsMap(stagingGroup);

		StagedModel stagedModel = addStagedModel(
			stagingGroup, dependentStagedModelsMap);

		stagedModel = addVersion(stagedModel);

		exportImportStagedModel(stagedModel);

		StagedModel importedStagedModel = getStagedModel(
			stagedModel.getUuid(), liveGroup);

		Assert.assertNotNull(importedStagedModel);

		validateImportedStagedModel(stagedModel, importedStagedModel);

		exportImportStagedModel(stagedModel);

		importedStagedModel = getStagedModel(stagedModel.getUuid(), liveGroup);

		Assert.assertNotNull(importedStagedModel);

		validateImportedStagedModel(stagedModel, importedStagedModel);
	}

	protected void addComments(StagedModel stagedModel) throws Exception {
		if (!isCommentableStagedModel()) {
			return;
		}

		User user = TestPropsValues.getUser();
		String className = ExportImportClassedModelUtil.getClassName(
			stagedModel);
		long classPK = ExportImportClassedModelUtil.getClassPK(stagedModel);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId(), user.getUserId());

		CommentManagerUtil.addComment(
			user.getUserId(), stagingGroup.getGroupId(), className, classPK,
			user.getFullName(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(50),
			new IdentityServiceContextFunction(serviceContext));
	}

	protected Map<String, List<StagedModel>> addDefaultDependentStagedModelsMap(
			Group group)
		throws Exception {

		return new HashMap<>();
	}

	protected StagedModel addDefaultStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		return null;
	}

	protected List<StagedModel> addDependentStagedModel(
		Map<String, List<StagedModel>> dependentStagedModelsMap, Class<?> clazz,
		StagedModel dependentStagedModel) {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			clazz.getSimpleName());

		if (dependentStagedModels == null) {
			dependentStagedModels = new ArrayList<>();

			dependentStagedModelsMap.put(
				clazz.getSimpleName(), dependentStagedModels);
		}

		dependentStagedModels.add(dependentStagedModel);

		return dependentStagedModels;
	}

	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		return new HashMap<>();
	}

	protected void addRatings(StagedModel stagedModel) throws Exception {
		RatingsTestUtil.addEntry(
			stagedModel.getModelClassName(),
			ExportImportClassedModelUtil.getClassPK(stagedModel));
	}

	protected abstract StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception;

	protected StagedModel addVersion(StagedModel stagedModel) throws Exception {
		return null;
	}

	protected void deleteStagedModel(
			StagedModel stagedModel,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		@SuppressWarnings("rawtypes")
		StagedModelDataHandler stagedModelDataHandler =
			StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
				ExportImportClassedModelUtil.getClassName(stagedModel));

		stagedModelDataHandler.deleteStagedModel(stagedModel);

		for (List<StagedModel> dependentStagedModels :
				dependentStagedModelsMap.values()) {

			for (StagedModel dependentStagedModel : dependentStagedModels) {
				stagedModelDataHandler =
					StagedModelDataHandlerRegistryUtil.
						getStagedModelDataHandler(
							ExportImportClassedModelUtil.getClassName(
								dependentStagedModel));

				stagedModelDataHandler.deleteStagedModel(dependentStagedModel);
			}
		}
	}

	protected void exportImportStagedModel(StagedModel stagedModel)
		throws Exception {

		exportStagedModel(stagedModel);

		importStagedModel(stagedModel);
	}

	protected void exportImportStagedModelFromLiveToStaging(
			StagedModel stagedModel)
		throws Exception {

		initExport(liveGroup);

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);

		initImport(liveGroup, stagingGroup);

		StagedModel exportedStagedModel = readExportedStagedModel(stagedModel);

		Assert.assertNotNull(exportedStagedModel);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedModel);
	}

	protected void exportStagedModel(StagedModel stagedModel) throws Exception {
		initExport();

		StagedModelDataHandlerUtil.exportStagedModel(
			portletDataContext, stagedModel);
	}

	protected AssetEntry fetchAssetEntry(StagedModel stagedModel, Group group)
		throws Exception {

		return AssetEntryLocalServiceUtil.fetchEntry(
			group.getGroupId(), stagedModel.getUuid());
	}

	protected Date getEndDate() {
		return new Date();
	}

	protected Map<String, String[]> getParameterMap() {
		return LinkedHashMapBuilder.put(
			PortletDataHandlerKeys.DATA_STRATEGY,
			new String[] {PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE}
		).put(
			PortletDataHandlerKeys.IGNORE_LAST_PUBLISH_DATE,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_DATA_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).build();
	}

	protected abstract StagedModel getStagedModel(String uuid, Group group)
		throws PortalException;

	protected abstract Class<? extends StagedModel> getStagedModelClass();

	protected Date getStartDate() {
		return new Date(System.currentTimeMillis() - Time.HOUR);
	}

	protected void importStagedModel(StagedModel stagedModel) throws Exception {
		initImport();

		StagedModel exportedStagedModel = readExportedStagedModel(stagedModel);

		Assert.assertNotNull(exportedStagedModel);

		StagedModelDataHandlerUtil.importStagedModel(
			portletDataContext, exportedStagedModel);
	}

	protected void initExport() throws Exception {
		initExport(stagingGroup);
	}

	protected void initExport(Group exportGroup) throws Exception {
		zipWriter = _zipWriterFactory.getZipWriter();

		portletDataContext =
			PortletDataContextFactoryUtil.createExportPortletDataContext(
				exportGroup.getCompanyId(), exportGroup.getGroupId(),
				getParameterMap(), getStartDate(), getEndDate(), zipWriter);

		portletDataContext.setExportImportProcessId(
			BaseStagedModelDataHandlerTestCase.class.getName());

		rootElement = SAXReaderUtil.createElement("root");

		portletDataContext.setExportDataRootElement(rootElement);

		missingReferencesElement = rootElement.addElement("missing-references");

		portletDataContext.setMissingReferencesElement(
			missingReferencesElement);
	}

	protected void initImport() throws Exception {
		initImport(stagingGroup, liveGroup);
	}

	protected void initImport(Group exportGroup, Group importGroup)
		throws Exception {

		userIdStrategy = new TestUserIdStrategy();

		zipReader = _zipReaderFactory.getZipReader(zipWriter.getFile());

		String xml = zipReader.getEntryAsString("/manifest.xml");

		if (xml == null) {
			Document document = SAXReaderUtil.createDocument();

			Element rootElement = document.addElement("root");

			rootElement.addElement("header");

			zipWriter.addEntry("/manifest.xml", document.asXML());

			zipReader = _zipReaderFactory.getZipReader(zipWriter.getFile());
		}

		portletDataContext =
			PortletDataContextFactoryUtil.createImportPortletDataContext(
				importGroup.getCompanyId(), importGroup.getGroupId(),
				getParameterMap(), userIdStrategy, zipReader);

		portletDataContext.setExportImportProcessId(
			BaseStagedModelDataHandlerTestCase.class.getName());
		portletDataContext.setImportDataRootElement(rootElement);

		Element missingReferencesElement = rootElement.element(
			"missing-references");

		if (missingReferencesElement == null) {
			missingReferencesElement = rootElement.addElement(
				"missing-references");
		}

		portletDataContext.setMissingReferencesElement(
			missingReferencesElement);

		Group sourceCompanyGroup = GroupLocalServiceUtil.getCompanyGroup(
			exportGroup.getCompanyId());

		portletDataContext.setSourceCompanyGroupId(
			sourceCompanyGroup.getGroupId());

		portletDataContext.setSourceCompanyId(exportGroup.getCompanyId());
		portletDataContext.setSourceGroupId(exportGroup.getGroupId());
	}

	protected boolean isAssetPrioritySupported() {
		return false;
	}

	protected boolean isCommentableStagedModel() {
		return false;
	}

	protected boolean isVersionableStagedModel() {
		return false;
	}

	protected StagedModel readExportedStagedModel(StagedModel stagedModel) {
		String stagedModelPath = ExportImportPathUtil.getModelPath(stagedModel);

		return (StagedModel)portletDataContext.getZipEntryAsObject(
			stagedModelPath);
	}

	protected boolean supportLastPublishDateUpdate() {
		return false;
	}

	protected StagedModelAssets updateAssetEntry(
			StagedModel stagedModel, Group group)
		throws Exception {

		AssetEntry assetEntry = fetchAssetEntry(stagedModel, group);

		if (assetEntry == null) {
			return null;
		}

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			stagingGroup.getGroupId());

		AssetCategory assetCategory = AssetTestUtil.addCategory(
			stagingGroup.getGroupId(), assetVocabulary.getVocabularyId());

		Company company = CompanyLocalServiceUtil.getCompany(
			stagedModel.getCompanyId());

		Group companyGroup = company.getGroup();

		AssetVocabulary companyAssetVocabulary = AssetTestUtil.addVocabulary(
			companyGroup.getGroupId());

		AssetCategory companyAssetCategory = AssetTestUtil.addCategory(
			companyGroup.getGroupId(),
			companyAssetVocabulary.getVocabularyId());

		AssetTag assetTag = AssetTestUtil.addTag(stagingGroup.getGroupId());

		double assetPriority = assetEntry.getPriority();

		if (isAssetPrioritySupported()) {
			assetPriority = RandomTestUtil.nextDouble();
		}

		assetEntry = AssetEntryLocalServiceUtil.updateEntry(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(),
			assetEntry.getCreateDate(), assetEntry.getModifiedDate(),
			assetEntry.getClassName(), assetEntry.getClassPK(),
			assetEntry.getClassUuid(), assetEntry.getClassTypeId(),
			new long[] {
				assetCategory.getCategoryId(),
				companyAssetCategory.getCategoryId()
			},
			new String[] {assetTag.getName()}, assetEntry.isListable(),
			assetEntry.isVisible(), assetEntry.getStartDate(),
			assetEntry.getEndDate(), assetEntry.getPublishDate(),
			assetEntry.getExpirationDate(), assetEntry.getMimeType(),
			assetEntry.getTitle(), assetEntry.getDescription(),
			assetEntry.getSummary(), assetEntry.getUrl(),
			assetEntry.getLayoutUuid(), assetEntry.getHeight(),
			assetEntry.getWidth(), assetPriority);

		return new StagedModelAssets(
			assetCategory, assetEntry, assetTag, assetVocabulary);
	}

	protected void validateAssets(
			StagedModel stagedModel, StagedModelAssets stagedModelAssets,
			Group group)
		throws Exception {

		if (stagedModelAssets == null) {
			return;
		}

		AssetEntry importedAssetEntry = fetchAssetEntry(stagedModel, group);

		if (isAssetPrioritySupported()) {
			AssetEntry assetEntry = stagedModelAssets.getAssetEntry();

			Assert.assertEquals(
				assetEntry.getPriority(), importedAssetEntry.getPriority(), 0D);
		}

		List<AssetCategory> importedAssetCategories =
			AssetCategoryLocalServiceUtil.getEntryCategories(
				importedAssetEntry.getEntryId());

		Assert.assertEquals(
			importedAssetCategories.toString(), 2,
			importedAssetCategories.size());

		AssetCategory stagedAssetCategory =
			stagedModelAssets.getAssetCategory();

		AssetCategory importedAssetCategory = null;

		Company company = CompanyLocalServiceUtil.getCompany(
			group.getCompanyId());

		long companyGroupId = company.getGroupId();

		for (AssetCategory assetCategory : importedAssetCategories) {
			long groupId = assetCategory.getGroupId();

			if (groupId != companyGroupId) {
				importedAssetCategory = assetCategory;

				break;
			}
		}

		Assert.assertEquals(
			stagedAssetCategory.getUuid(), importedAssetCategory.getUuid());

		List<AssetTag> importedAssetTags =
			AssetTagLocalServiceUtil.getEntryTags(
				importedAssetEntry.getEntryId());

		Assert.assertEquals(
			importedAssetTags.toString(), 1, importedAssetTags.size());

		AssetTag assetTag = stagedModelAssets.getAssetTag();
		AssetTag importedAssetTag = importedAssetTags.get(0);

		Assert.assertEquals(assetTag.getName(), importedAssetTag.getName());

		AssetVocabulary assetVocabulary =
			stagedModelAssets.getAssetVocabulary();
		AssetVocabulary importedAssetVocabulary =
			AssetVocabularyLocalServiceUtil.getVocabulary(
				importedAssetCategory.getVocabularyId());

		Assert.assertEquals(
			assetVocabulary.getUuid(), importedAssetVocabulary.getUuid());
	}

	protected void validateComments(
			StagedModel stagedModel, StagedModel importedStagedModel,
			Group group)
		throws Exception {

		if (!isCommentableStagedModel()) {
			return;
		}

		List<MBMessage> discussionMBMessages =
			MBMessageLocalServiceUtil.getMessages(
				ExportImportClassedModelUtil.getClassName(stagedModel),
				ExportImportClassedModelUtil.getClassPK(stagedModel),
				WorkflowConstants.STATUS_ANY);

		if (ListUtil.isEmpty(discussionMBMessages)) {
			return;
		}

		int importedDiscussionMBMessagesCount =
			MBMessageLocalServiceUtil.getDiscussionMessagesCount(
				ExportImportClassedModelUtil.getClassName(importedStagedModel),
				ExportImportClassedModelUtil.getClassPK(importedStagedModel),
				WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			discussionMBMessages.size(), importedDiscussionMBMessagesCount + 1);

		for (MBMessage discussionMBMessage : discussionMBMessages) {
			if (discussionMBMessage.isRoot()) {
				continue;
			}

			MBMessage importedDiscussionMBMessage =
				MBMessageLocalServiceUtil.fetchMBMessageByUuidAndGroupId(
					discussionMBMessage.getUuid(), group.getGroupId());

			Assert.assertNotNull(importedDiscussionMBMessage);
		}
	}

	protected void validateExport(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		Element rootElement = portletDataContext.getExportDataRootElement();

		List<Element> stagedModelGroupElements = new ArrayList<>();

		Class<?> stagedModelClass = getStagedModelClass();

		String stagedModelClassSimpleName = stagedModelClass.getSimpleName();

		stagedModelGroupElements.addAll(
			rootElement.elements(stagedModelClassSimpleName));

		for (String dependentStagedModelClassSimpleName :
				dependentStagedModelsMap.keySet()) {

			stagedModelGroupElements.addAll(
				rootElement.elements(dependentStagedModelClassSimpleName));
		}

		for (Element stagedModelGroupElement : stagedModelGroupElements) {
			String className = stagedModelGroupElement.getName();

			if (className.equals("missing-references")) {
				continue;
			}

			List<StagedModel> dependentStagedModels =
				dependentStagedModelsMap.get(className);

			if (dependentStagedModels == null) {
				dependentStagedModels = new ArrayList<>();
			}
			else {
				dependentStagedModels = ListUtil.copy(dependentStagedModels);
			}

			if (className.equals(stagedModelClassSimpleName)) {
				dependentStagedModels.add(stagedModel);
			}

			List<Element> elements = stagedModelGroupElement.elements();

			Assert.assertEquals(
				elements.toString(), dependentStagedModels.size(),
				elements.size());

			for (Element element : elements) {
				String path = element.attributeValue("path");

				Assert.assertNotNull(path);

				Iterator<StagedModel> iterator =
					dependentStagedModels.iterator();

				while (iterator.hasNext()) {
					StagedModel dependentStagedModel = iterator.next();

					String dependentStagedModelPath =
						ExportImportPathUtil.getModelPath(dependentStagedModel);

					if (path.equals(dependentStagedModelPath)) {
						iterator.remove();
					}
				}
			}

			Assert.assertTrue(
				"There is more than one element exported with the same path",
				dependentStagedModels.isEmpty());
		}
	}

	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {
	}

	protected void validateImport(
			StagedModel stagedModel, StagedModelAssets stagedModelAssets,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		StagedModel importedStagedModel = getStagedModel(
			stagedModel.getUuid(), group);

		Assert.assertNotNull(importedStagedModel);

		validateAssets(importedStagedModel, stagedModelAssets, group);
		validateComments(stagedModel, importedStagedModel, group);

		validateImport(dependentStagedModelsMap, group);
		validateImportedStagedModel(stagedModel, importedStagedModel);
		validateRatings(stagedModel, importedStagedModel);
	}

	protected void validateImportedStagedModel(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		DateTestUtil.assertEquals(
			stagedModel.getCreateDate(), importedStagedModel.getCreateDate());
		DateTestUtil.assertEquals(
			stagedModel.getModifiedDate(),
			importedStagedModel.getModifiedDate());

		Assert.assertEquals(
			stagedModel.getUuid(), importedStagedModel.getUuid());
	}

	protected void validateRatings(
			StagedModel stagedModel, StagedModel importedStagedModel)
		throws Exception {

		List<RatingsEntry> ratingsEntries =
			RatingsEntryLocalServiceUtil.getEntries(
				ExportImportClassedModelUtil.getClassName(stagedModel),
				ExportImportClassedModelUtil.getClassPK(stagedModel),
				WorkflowConstants.STATUS_ANY);

		List<RatingsEntry> importedRatingsEntries =
			RatingsEntryLocalServiceUtil.getEntries(
				ExportImportClassedModelUtil.getClassName(importedStagedModel),
				ExportImportClassedModelUtil.getClassPK(importedStagedModel),
				WorkflowConstants.STATUS_ANY);

		Assert.assertEquals(
			importedRatingsEntries.toString(), ratingsEntries.size(),
			importedRatingsEntries.size());

		for (RatingsEntry ratingsEntry : ratingsEntries) {
			Iterator<RatingsEntry> iterator = importedRatingsEntries.iterator();

			while (iterator.hasNext()) {
				RatingsEntry importedRatingsEntry = iterator.next();

				if (ratingsEntry.getScore() ==
						importedRatingsEntry.getScore()) {

					iterator.remove();

					break;
				}
			}
		}

		Assert.assertTrue(
			importedRatingsEntries.toString(),
			importedRatingsEntries.isEmpty());
	}

	@DeleteAfterTestRun
	protected Group liveGroup;

	protected Element missingReferencesElement;
	protected PortletDataContext portletDataContext;
	protected Element rootElement;
	protected Group stagingGroup;
	protected UserIdStrategy userIdStrategy;
	protected ZipReader zipReader;
	protected ZipWriter zipWriter;

	protected class StagedModelAssets implements Serializable {

		public StagedModelAssets(
			AssetCategory assetCategory, AssetEntry assetEntry,
			AssetTag assetTag, AssetVocabulary assetVocabulary) {

			_assetCategory = assetCategory;
			_assetEntry = assetEntry;
			_assetTag = assetTag;
			_assetVocabulary = assetVocabulary;
		}

		public AssetCategory getAssetCategory() {
			return _assetCategory;
		}

		public AssetEntry getAssetEntry() {
			return _assetEntry;
		}

		public AssetTag getAssetTag() {
			return _assetTag;
		}

		public AssetVocabulary getAssetVocabulary() {
			return _assetVocabulary;
		}

		public void setAssetCategory(AssetCategory assetCategory) {
			_assetCategory = assetCategory;
		}

		public void setAssetEntry(AssetEntry assetEntry) {
			_assetEntry = assetEntry;
		}

		public void setAssetTag(AssetTag assetTag) {
			_assetTag = assetTag;
		}

		public void setAssetVocabulary(AssetVocabulary assetVocabulary) {
			_assetVocabulary = assetVocabulary;
		}

		private AssetCategory _assetCategory;
		private AssetEntry _assetEntry;
		private AssetTag _assetTag;
		private AssetVocabulary _assetVocabulary;

	}

	protected class TestUserIdStrategy implements UserIdStrategy {

		public TestUserIdStrategy() {
			_userId = _initializeUserId();
		}

		public TestUserIdStrategy(User user) {
			_userId = user.getUserId();
		}

		@Override
		public long getUserId(String userUuid) {
			return _userId;
		}

		private long _initializeUserId() {
			try {
				return TestPropsValues.getUserId();
			}
			catch (Exception exception) {
				return 0;
			}
		}

		private final long _userId;

	}

	@Inject
	private ZipReaderFactory _zipReaderFactory;

	@Inject
	private ZipWriterFactory _zipWriterFactory;

}
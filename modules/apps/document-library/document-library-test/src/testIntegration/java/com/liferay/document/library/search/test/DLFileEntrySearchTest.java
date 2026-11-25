/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLTrashService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.dynamic.data.mapping.configuration.DDMIndexerConfiguration;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.search.TestOrderHelper;
import com.liferay.dynamic.data.mapping.util.DDM;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslator;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.util.BaseSearchTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.InputStream;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
@Sync
public class DLFileEntrySearchTest extends BaseSearchTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		ConfigurationTestUtil.saveConfiguration(
			DDMIndexerConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"enableLegacyDDMIndexFields", false
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			DDMIndexerConfiguration.class.getName());
	}

	@Override
	@Test
	public void testLocalizedSearch() throws Exception {
	}

	@Test
	public void testOrderByDDMBooleanField() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMBooleanField();
	}

	@Test
	public void testOrderByDDMBooleanFieldRepeatable() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMBooleanFieldRepeatable();
	}

	@Test
	public void testOrderByDDMIntegerField() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMIntegerField();
	}

	@Test
	public void testOrderByDDMIntegerFieldRepeatable() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMIntegerFieldRepeatable();
	}

	@Test
	public void testOrderByDDMNumberField() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMNumberField();
	}

	@Test
	public void testOrderByDDMNumberFieldRepeatable() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMNumberFieldRepeatable();
	}

	@Test
	public void testOrderByDDMRadioField() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMRadioField();
	}

	@Test
	public void testOrderByDDMRadioFieldKeyword() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMRadioFieldKeyword();
	}

	@Test
	public void testOrderByDDMTextField() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMTextField();
	}

	@Test
	public void testOrderByDDMTextFieldKeyword() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMTextFieldKeyword();
	}

	@Test
	public void testOrderByDDMTextFieldRepeatable() throws Exception {
		TestOrderHelper testOrderHelper = new DLFileEntrySearchTestOrderHelper(
			_ddmIndexer, group);

		testOrderHelper.testOrderByDDMTextFieldRepeatable();
	}

	@Override
	@Test
	public void testSearchAttachments() throws Exception {
	}

	@Override
	@Test
	public void testSearchByDDMStructureField() throws Exception {
	}

	@Test
	@TestInfo("LPS-130154")
	public void testSearchLargeContent() throws Exception {
		String keywords = RandomTestUtil.randomString();
		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			group.getGroupId());

		assertBaseModelsCount(0, keywords, searchContext);

		String content = StringBundler.concat(
			keywords, StringPool.NEW_LINE,
			StringUtil.randomString(PropsValues.DL_FILE_INDEXING_MAX_SIZE));

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			StringUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, content.getBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		assertBaseModelsCount(1, keywords, searchContext);
	}

	@Test
	public void testSearchTikaRawMetadata() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			group.getGroupId());

		int initialBaseModelsSearchCount = 0;

		assertBaseModelsCount(
			initialBaseModelsSearchCount, "Enterprise", searchContext);

		String fileName = "OSX_Test.docx";

		InputStream inputStream =
			DLFileEntrySearchTest.class.getResourceAsStream(
				"dependencies/" + fileName);

		File file = null;

		try {
			String mimeType = MimeTypesUtil.getContentType(file, fileName);

			file = FileUtil.createTempFile(inputStream);

			_dlAppLocalService.addFileEntry(
				null, serviceContext.getUserId(),
				serviceContext.getScopeGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName, mimeType,
				fileName, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				file, null, null, null, serviceContext);
		}
		finally {
			FileUtil.delete(file);
		}

		assertBaseModelsCount(
			initialBaseModelsSearchCount + 1, "Enterprise", searchContext);
	}

	@Test
	public void testSearchTreePath() throws Exception {
		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			"Document", StringPool.BLANK, StringUtil.randomString(),
			StringUtil.randomString(), new byte[0], null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), StringUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			folder.getFolderId(), StringUtil.randomString(),
			ContentTypes.APPLICATION_OCTET_STREAM, "Document", StringPool.BLANK,
			StringUtil.randomString(), StringUtil.randomString(), new byte[0],
			null, null, null,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			group.getGroupId());

		assertBaseModelsCount(2, "Document", searchContext);

		searchContext.setFolderIds(new long[] {folder.getFolderId()});

		assertBaseModelsCount(1, "Document", searchContext);
	}

	protected BaseModel<?> addBaseModel(
			String keywords, DDMStructure ddmStructure,
			ServiceContext serviceContext)
		throws Exception {

		return addBaseModel(
			new String[] {keywords}, ddmStructure, serviceContext);
	}

	protected BaseModel<?> addBaseModel(
			String[] keywords, DDMStructure ddmStructure,
			ServiceContext serviceContext)
		throws Exception {

		_ddmStructure = ddmStructure;

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchDataDefinitionFileEntryType(
				ddmStructure.getGroupId(), ddmStructure.getStructureId());

		if (dlFileEntryType == null) {
			dlFileEntryType = _dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(),
				serviceContext.getScopeGroupId(), ddmStructure.getStructureId(),
				null,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), "New File Entry Type"),
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), "New File Entry Type"),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				serviceContext);

			_dlFileEntryTypeLocalService.addDDMStructureLinks(
				dlFileEntryType.getFileEntryTypeId(),
				SetUtil.fromArray(ddmStructure.getStructureId()));
		}

		String content = "Content: Enterprise. Open Source. For Life.";

		DDMFormValues ddmFormValues = createDDMFormValues(
			_ddmBeanTranslator.translate(ddmStructure.getDDMForm()));

		for (String keyword : keywords) {
			ddmFormValues.addDDMFormFieldValue(
				createLocalizedDDMFormFieldValue(
					"name", StringUtil.trim(keyword)));
		}

		DLAppTestUtil.populateServiceContext(
			serviceContext, dlFileEntryType.getFileEntryTypeId());

		serviceContext.setAttribute(
			DDMFormValues.class.getName() + StringPool.POUND +
				ddmStructure.getStructureId(),
			ddmFormValues);

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, content.getBytes(), null, null, null,
			serviceContext);

		return (DLFileEntry)fileEntry.getModel();
	}

	@Override
	protected BaseModel<?> addBaseModelWithDDMStructure(
			BaseModel<?> parentBaseModel, String keywords,
			ServiceContext serviceContext)
		throws Exception {

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm("name");

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			serviceContext.getScopeGroupId(),
			DLFileEntryMetadata.class.getName(), ddmForm);

		return addBaseModel(keywords, ddmStructure, serviceContext);
	}

	@Override
	protected BaseModel<?> addBaseModelWithWorkflow(
			BaseModel<?> parentBaseModel, boolean approved, String keywords,
			ServiceContext serviceContext)
		throws Exception {

		DLFolder dlFolder = (DLFolder)parentBaseModel;

		long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

		if (dlFolder != null) {
			folderId = dlFolder.getFolderId();
		}

		FileEntry fileEntry = DLAppTestUtil.addFileEntryWithWorkflow(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			folderId, keywords + ".txt", keywords, approved, serviceContext);

		return (DLFileEntry)fileEntry.getModel();
	}

	protected DDMFormValues createDDMFormValues(
		com.liferay.dynamic.data.mapping.kernel.DDMForm ddmForm) {

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addAvailableLocale(LocaleUtil.US);
		ddmFormValues.setDefaultLocale(LocaleUtil.US);

		return ddmFormValues;
	}

	protected DDMFormFieldValue createLocalizedDDMFormFieldValue(
		String name, String value) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setName(name);

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(LocaleUtil.US, value);

		ddmFormFieldValue.setValue(localizedValue);

		return ddmFormFieldValue;
	}

	@Override
	protected void deleteBaseModel(long primaryKey) throws Exception {
		_dlFileEntryLocalService.deleteFileEntry(primaryKey);
	}

	@Override
	protected void expireBaseModelVersions(
			BaseModel<?> baseModel, boolean expireAllVersions,
			ServiceContext serviceContext)
		throws Exception {

		DLFileEntry dlFileEntry = (DLFileEntry)baseModel;

		if (expireAllVersions) {
			_dlAppService.deleteFileEntry(dlFileEntry.getFileEntryId());
		}
		else {
			_dlAppService.deleteFileVersion(
				dlFileEntry.getFileEntryId(), dlFileEntry.getVersion());
		}
	}

	@Override
	protected Class<?> getBaseModelClass() {
		return DLFileEntry.class;
	}

	@Override
	protected String getDDMStructureFieldName() {
		return _ddmIndexer.encodeName(
			_ddmStructure.getStructureId(), "name",
			LocaleUtil.getSiteDefault());
	}

	@Override
	protected BaseModel<?> getParentBaseModel(
			BaseModel<?> parentBaseModel, ServiceContext serviceContext)
		throws Exception {

		Folder folder = _dlAppService.addFolder(
			null, serviceContext.getScopeGroupId(),
			(Long)parentBaseModel.getPrimaryKeyObj(),
			RandomTestUtil.randomString(_FOLDER_NAME_MAX_LENGTH),
			RandomTestUtil.randomString(), serviceContext);

		return (DLFolder)folder.getModel();
	}

	@Override
	protected BaseModel<?> getParentBaseModel(
			Group group, ServiceContext serviceContext)
		throws Exception {

		Folder folder = _dlAppService.addFolder(
			null, serviceContext.getScopeGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(_FOLDER_NAME_MAX_LENGTH),
			RandomTestUtil.randomString(), serviceContext);

		return (DLFolder)folder.getModel();
	}

	@Override
	protected String getParentBaseModelClassName() {
		return DLFolderConstants.getClassName();
	}

	@Override
	protected String getSearchKeywords() {
		return "Title";
	}

	@Override
	protected boolean isExpirableAllVersions() {
		return true;
	}

	@Override
	protected void moveBaseModelToTrash(long primaryKey) throws Exception {
		_dlTrashService.moveFileEntryToTrash(primaryKey);
	}

	@Override
	protected void moveParentBaseModelToTrash(long primaryKey)
		throws Exception {

		_dlTrashService.moveFolderToTrash(primaryKey);
	}

	@Override
	protected Hits searchGroupEntries(long groupId, long creatorUserId)
		throws Exception {

		return _dlAppService.search(
			groupId, creatorUserId, WorkflowConstants.STATUS_APPROVED,
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@Override
	protected BaseModel<?> updateBaseModel(
			BaseModel<?> baseModel, String keywords,
			ServiceContext serviceContext)
		throws Exception {

		DLFileEntry dlFileEntry = (DLFileEntry)baseModel;

		FileEntry fileEntry = _dlAppService.updateFileEntry(
			dlFileEntry.getFileEntryId(), null, dlFileEntry.getMimeType(),
			keywords, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			DLVersionNumberIncrease.MAJOR, (byte[])null,
			dlFileEntry.getDisplayDate(), dlFileEntry.getExpirationDate(),
			dlFileEntry.getReviewDate(), serviceContext);

		return (DLFileEntry)fileEntry.getModel();
	}

	@Override
	protected void updateDDMStructure(ServiceContext serviceContext)
		throws Exception {

		DDMForm ddmForm = DDMStructureTestUtil.getSampleDDMForm("title");

		DDMFormLayout ddmFormLayout = _ddm.getDefaultDDMFormLayout(ddmForm);

		_ddmStructureLocalService.updateStructure(
			_ddmStructure.getUserId(), _ddmStructure.getStructureId(),
			_ddmStructure.getParentStructureId(), _ddmStructure.getNameMap(),
			_ddmStructure.getDescriptionMap(), ddmForm, ddmFormLayout,
			serviceContext);
	}

	protected class DLFileEntrySearchTestOrderHelper extends TestOrderHelper {

		protected DLFileEntrySearchTestOrderHelper(
				DDMIndexer ddmIndexer, Group group)
			throws Exception {

			super(ddmIndexer, group);
		}

		@Override
		protected DDMTemplate addDDMTemplate(DDMStructure ddmStructure)
			throws Exception {

			return null;
		}

		@Override
		protected BaseModel<?> addSearchableAssetEntry(
				String fieldValue, BaseModel<?> parentBaseModel,
				DDMStructure ddmStructure, DDMTemplate ddmTemplate,
				ServiceContext serviceContext)
			throws Exception {

			return addBaseModel(fieldValue, ddmStructure, serviceContext);
		}

		@Override
		protected BaseModel<?> addSearchableAssetEntryRepeatable(
				String[] fieldValues, BaseModel<?> parentBaseModel,
				DDMStructure ddmStructure, DDMTemplate ddmTemplate,
				ServiceContext serviceContext)
			throws Exception {

			return addBaseModel(fieldValues, ddmStructure, serviceContext);
		}

		@Override
		protected long getClassNameId() {
			return _portal.getClassNameId(DLFileEntryMetadata.class);
		}

		@Override
		protected String getSearchableAssetEntryClassName() {
			return getBaseModelClassName();
		}

		@Override
		protected BaseModel<?> getSearchableAssetEntryParentBaseModel(
				Group group, ServiceContext serviceContext)
			throws Exception {

			return getParentBaseModel(group, serviceContext);
		}

		@Override
		protected String getSearchableAssetEntryStructureClassName() {
			return DLFileEntryMetadata.class.getName();
		}

	}

	private static final int _FOLDER_NAME_MAX_LENGTH = 100;

	@Inject
	private DDM _ddm;

	@Inject
	private DDMBeanTranslator _ddmBeanTranslator;

	@Inject
	private DDMIndexer _ddmIndexer;

	private DDMStructure _ddmStructure;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject
	private DLTrashService _dlTrashService;

	@Inject
	private Portal _portal;

}
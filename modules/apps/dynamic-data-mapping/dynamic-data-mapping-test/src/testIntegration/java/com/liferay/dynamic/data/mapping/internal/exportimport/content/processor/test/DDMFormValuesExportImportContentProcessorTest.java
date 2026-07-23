/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.exportimport.content.processor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMTemplateTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslatorUtil;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataContextFactoryUtil;
import com.liferay.exportimport.test.util.TestReaderWriter;
import com.liferay.exportimport.test.util.TestUserIdStrategy;
import com.liferay.journal.article.dynamic.data.mapping.form.field.type.constants.JournalArticleDDMFormFieldTypeConstants;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.capabilities.ThumbnailCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.InputStream;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltan Csaszi
 */
@RunWith(Arquillian.class)
public class DDMFormValuesExportImportContentProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(_liveGroup);

		_stagingGroup = _liveGroup.getStagingGroup();

		TestReaderWriter testReaderWriter = new TestReaderWriter();

		_portletDataContextExport =
			PortletDataContextFactoryUtil.createExportPortletDataContext(
				_stagingGroup.getCompanyId(), _stagingGroup.getGroupId(),
				new HashMap<>(),
				new Date(System.currentTimeMillis() - Time.HOUR), new Date(),
				testReaderWriter);

		Document document = SAXReaderUtil.createDocument();

		Element manifestRootElement = document.addElement("root");

		manifestRootElement.addElement("header");

		testReaderWriter.addEntry("/manifest.xml", document.asXML());

		Element rootElement = SAXReaderUtil.createElement("root");

		_portletDataContextExport.setExportDataRootElement(rootElement);

		_portletDataContextImport =
			PortletDataContextFactoryUtil.createImportPortletDataContext(
				_liveGroup.getCompanyId(), _liveGroup.getGroupId(),
				new HashMap<>(), new TestUserIdStrategy(), testReaderWriter);

		_portletDataContextImport.setImportDataRootElement(rootElement);

		Element missingReferencesElement = rootElement.addElement(
			"missing-references");

		_portletDataContextExport.setMissingReferencesElement(
			missingReferencesElement);

		_portletDataContextImport.setMissingReferencesElement(
			missingReferencesElement);

		_portletDataContextImport.setSourceGroupId(_stagingGroup.getGroupId());

		rootElement.addElement("entry");
	}

	@After
	public void tearDown() throws Exception {
		_journalArticleLocalService.deleteArticles(_stagingGroup.getGroupId());

		_journalArticleLocalService.deleteArticles(_liveGroup.getGroupId());

		if (_ddmTemplate != null) {
			_ddmTemplateLocalService.deleteTemplate(_ddmTemplate);
		}

		if (_ddmStructure != null) {
			_ddmStructureLocalService.deleteStructure(_ddmStructure);
		}
	}

	@Test
	public void testReplaceDLExportImportContentReferences() throws Exception {
		_initDLReferences();

		DDMForm ddmForm = _formInstance.getDDMForm();

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		JSONObject jsonObject1 = JSONUtil.put(
			"classPK", _fileEntry.getFileEntryId()
		).put(
			"groupId", _fileEntry.getGroupId()
		).put(
			"title", _fileEntry.getTitle()
		).put(
			"type", "document"
		).put(
			"uuid", _fileEntry.getUuid()
		);

		for (DDMFormField ddmFormField : ddmFormFields) {
			ddmFormValues.addDDMFormFieldValue(
				DDMFormValuesTestUtil.createLocalizedDDMFormFieldValue(
					ddmFormField.getName(), jsonObject1.toString()));
		}

		DDMFormValues exportDDMFormValues =
			_exportImportContentProcessor.replaceExportContentReferences(
				_portletDataContextExport, _journalArticle, ddmFormValues, true,
				true);

		Map<Long, Long> groupIds =
			(Map<Long, Long>)_portletDataContextImport.getNewPrimaryKeysMap(
				Group.class);

		groupIds.put(_stagingGroup.getGroupId(), _liveGroup.getGroupId());

		Map<Long, Long> classPKs =
			(Map<Long, Long>)_portletDataContextImport.getNewPrimaryKeysMap(
				DLFileEntry.class);

		long fileEntryId = _fileEntry.getPrimaryKey();

		DLFileEntry newDLFileEntry = _dlFileEntryLocalService.copyFileEntry(
			TestPropsValues.getUserId(), _liveGroup.getGroupId(),
			_liveGroup.getGroupId(), fileEntryId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, null,
			new ServiceContext());

		newDLFileEntry.setUuid(_fileEntry.getUuid());

		_dlFileEntryLocalService.deleteFileEntry(fileEntryId);

		newDLFileEntry = _dlFileEntryLocalService.updateDLFileEntry(
			newDLFileEntry);

		classPKs.put(fileEntryId, newDLFileEntry.getPrimaryKey());

		_exportImportContentProcessor.replaceImportContentReferences(
			_portletDataContextImport, _journalArticle, exportDDMFormValues);

		List<DDMFormFieldValue> ddmFormFieldValues =
			exportDDMFormValues.getDDMFormFieldValues();

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		JSONObject jsonObject2 = JSONFactoryUtil.createJSONObject(
			value.getString(LocaleUtil.US));

		long newDLFileEntryId = newDLFileEntry.getFileEntryId();

		_dlFileEntryLocalService.deleteFileEntry(newDLFileEntry);

		Assert.assertEquals(newDLFileEntryId, jsonObject2.getLong("classPK"));
	}

	@Test
	public void testReplaceJournalExportImportContentReferences()
		throws Exception {

		_initJournalReferences();

		DDMFormValues exportDDMFormValues =
			_exportImportContentProcessor.replaceExportContentReferences(
				_portletDataContextExport, _fileEntry, _journalDDMFormValues,
				true, true);

		Map<Long, Long> groupIds =
			(Map<Long, Long>)_portletDataContextImport.getNewPrimaryKeysMap(
				Group.class);

		groupIds.put(_stagingGroup.getGroupId(), _liveGroup.getGroupId());

		Map<Long, Long> classPKs =
			(Map<Long, Long>)_portletDataContextImport.getNewPrimaryKeysMap(
				JournalArticle.class);

		long resourcePrimKey = _journalArticle.getResourcePrimKey();

		JournalArticle newJournalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _liveGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		newJournalArticle.setUuid(_journalArticle.getUuid());

		newJournalArticle = _journalArticleLocalService.updateJournalArticle(
			newJournalArticle);

		classPKs.put(resourcePrimKey, newJournalArticle.getResourcePrimKey());

		_exportImportContentProcessor.replaceImportContentReferences(
			_portletDataContextImport, _fileEntry, exportDDMFormValues);

		List<DDMFormFieldValue> ddmFormFieldValues =
			exportDDMFormValues.getDDMFormFieldValues();

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Value value = ddmFormFieldValue.getValue();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			value.getString(LocaleUtil.US));

		long newArticleResourcePrimKey = newJournalArticle.getResourcePrimKey();

		Assert.assertEquals(
			newArticleResourcePrimKey, jsonObject.getLong("classPK"));

		long fileEntryId = _fileEntry.getPrimaryKey();

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.getDLFileEntry(
			fileEntryId);

		DLFileEntryType dlFileEntryType = dlFileEntry.getDLFileEntryType();

		_dlFileEntryLocalService.deleteFileEntry(dlFileEntry);

		_dlFileEntryTypeLocalService.deleteFileEntryType(dlFileEntryType);
	}

	@Test
	public void testReplaceLayoutExportContentReferences() throws Exception {
		_journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout stagingLayout = LayoutTestUtil.addTypePortletLayout(
			_stagingGroup);

		_createDDMFormWithJournalField(
			_stagingGroup, _journalArticle, stagingLayout);

		DDMFormValues exportDDMFormValues =
			_exportImportContentProcessor.replaceExportContentReferences(
				_portletDataContextExport, _journalArticle,
				_journalDDMFormValues, true, true);

		List<DDMFormFieldValue> ddmFormFieldValues =
			exportDDMFormValues.getDDMFormFieldValues();

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(1);

		Value value = ddmFormFieldValue.getValue();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			value.getString(LocaleUtil.US));

		Assert.assertEquals(
			stagingLayout.getGroupId(), jsonObject.getLong("groupId"));
		Assert.assertEquals(
			stagingLayout.getUuid(), jsonObject.getString("id"));
		Assert.assertEquals(
			stagingLayout.getLayoutId(), jsonObject.getLong("layoutId"));
		Assert.assertEquals(
			stagingLayout.isPrivateLayout(),
			jsonObject.getBoolean("privateLayout"));

		List<Element> missingReferenceElements =
			_portletDataContextExport.getMissingReferencesElement(
			).elements();

		Assert.assertEquals(
			missingReferenceElements.toString(), 1,
			missingReferenceElements.size());

		Element missingReferenceElement = missingReferenceElements.get(0);

		Assert.assertEquals(
			PortletDataContext.REFERENCE_TYPE_LAZY,
			missingReferenceElement.attributeValue("type"));
	}

	@Test
	public void testReplaceLayoutImportContentReferencesWithMissingLayout()
		throws Exception {

		_journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_stagingGroup);

		_createDDMFormWithJournalField(_stagingGroup, _journalArticle, layout);

		DDMFormValues ddmFormValues =
			_exportImportContentProcessor.replaceExportContentReferences(
				_portletDataContextExport, _journalArticle,
				_journalDDMFormValues, true, true);

		List<DDMFormFieldValue> ddmFormFieldValues =
			ddmFormValues.getDDMFormFieldValues();

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(1);

		Value value = ddmFormFieldValue.getValue();

		String valueString = value.getString(LocaleUtil.US);

		_exportImportContentProcessor.replaceImportContentReferences(
			_portletDataContextImport, _journalArticle, ddmFormValues);

		Assert.assertEquals(valueString, value.getString(LocaleUtil.US));
	}

	private DDMForm _createDDMFormWithJournalField(
			Group group, JournalArticle journalArticle, Layout layout)
		throws Exception {

		_ddmStructure = DDMStructureTestUtil.addStructure(
			group.getGroupId(), DLFileEntryMetadata.class.getName());

		DDMForm journalDDMForm = new DDMForm();

		Set<Locale> availableLocales = new LinkedHashSet<>();

		availableLocales.add(LocaleUtil.US);

		journalDDMForm.setAvailableLocales(availableLocales);

		journalDDMForm.setDefaultLocale(LocaleUtil.US);

		List<DDMFormField> ddmFormFields = journalDDMForm.getDDMFormFields();

		DDMFormField journalArticleDDMFormField = new DDMFormField(
			RandomTestUtil.randomString(),
			JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE);

		journalArticleDDMFormField.setDataType(
			JournalArticleDDMFormFieldTypeConstants.JOURNAL_ARTICLE);
		journalArticleDDMFormField.setDDMForm(journalDDMForm);
		journalArticleDDMFormField.setFieldNamespace("ddm");
		journalArticleDDMFormField.setLocalizable(true);
		journalArticleDDMFormField.setShowLabel(true);

		ddmFormFields.add(journalArticleDDMFormField);

		if (layout != null) {
			DDMFormField linkToLayoutDDMFormField = new DDMFormField(
				RandomTestUtil.randomString(),
				DDMFormFieldTypeConstants.LINK_TO_LAYOUT);

			linkToLayoutDDMFormField.setDataType("string");
			linkToLayoutDDMFormField.setDDMForm(journalDDMForm);
			linkToLayoutDDMFormField.setFieldNamespace("ddm");
			linkToLayoutDDMFormField.setLocalizable(true);
			linkToLayoutDDMFormField.setShowLabel(true);

			ddmFormFields.add(linkToLayoutDDMFormField);
		}

		_ddmStructure.setClassNameId(
			ClassNameLocalServiceUtil.getClassNameId(
				DLFileEntryMetadata.class));
		_ddmStructure.setDDMForm(journalDDMForm);

		_ddmStructure = _ddmStructureLocalService.updateDDMStructure(
			_ddmStructure);

		_journalDDMFormValues = new DDMFormValues(journalDDMForm);

		_journalDDMFormValues.setAvailableLocales(availableLocales);
		_journalDDMFormValues.setDefaultLocale(LocaleUtil.US);

		JSONObject jsonObject = JSONUtil.put(
			"className", JournalArticle.class.getName()
		).put(
			"classPK", journalArticle.getResourcePrimKey()
		).put(
			"title", journalArticle.getTitle()
		);

		List<DDMFormField> journalDDMFormFields =
			journalDDMForm.getDDMFormFields();

		for (DDMFormField journalDDMFormField : journalDDMFormFields) {
			LocalizedValue value = new LocalizedValue();

			if (Objects.equals(
					journalDDMFormField.getType(),
					DDMFormFieldTypeConstants.LINK_TO_LAYOUT)) {

				value.addString(
					LocaleUtil.US,
					JSONUtil.put(
						"groupId", layout.getGroupId()
					).put(
						"id", layout.getUuid()
					).put(
						"layoutId", layout.getLayoutId()
					).put(
						"name", layout.getName(LocaleUtil.US)
					).put(
						"privateLayout", layout.isPrivateLayout()
					).put(
						"value", layout.getFriendlyURL(LocaleUtil.US)
					).toString());
			}
			else {
				value.addString(LocaleUtil.US, jsonObject.toString());
			}

			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setDDMFormValues(_journalDDMFormValues);
			ddmFormFieldValue.setName(journalDDMFormField.getName());
			ddmFormFieldValue.setValue(value);

			_journalDDMFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		return journalDDMForm;
	}

	private DDMFormInstance _createFormInstanceWithDocLib(
			Group group, String className)
		throws Exception {

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormTestUtil.addDocumentLibraryDDMFormField(
			ddmForm, "DocumentsAndMedia9t17");

		return DDMFormInstanceTestUtil.addDDMFormInstance(
			ddmForm, group, className, TestPropsValues.getUserId());
	}

	private void _initDLReferences() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId());

		_fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			serviceContext);

		ThumbnailCapability thumbnailCapability =
			_fileEntry.getRepositoryCapability(ThumbnailCapability.class);

		_fileEntry = thumbnailCapability.setLargeImageId(
			_fileEntry, _fileEntry.getFileEntryId());

		_formInstance = _createFormInstanceWithDocLib(
			_stagingGroup, JournalArticle.class.getName());

		DDMStructure structure = _formInstance.getStructure();

		long classNameId = ClassNameLocalServiceUtil.getClassNameId(
			JournalArticle.class);

		structure.setClassNameId(classNameId);

		structure.setDDMForm(_formInstance.getDDMForm());

		structure = _ddmStructureLocalService.updateDDMStructure(structure);

		_ddmTemplate = DDMTemplateTestUtil.addTemplate(
			_stagingGroup.getGroupId(), structure.getStructureId(),
			classNameId);

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		StringBundler sb = new StringBundler(16);

		sb.append("<?xml version=\"1.0\"?>  ");
		sb.append("<root available-locales=\"en_US\" ");
		sb.append("default-locale=\"en_US\"> \t");
		sb.append("<dynamic-element name=\"DocumentsAndMedia9t17\" ");
		sb.append("type=\"document_library\" index-type=\"keyword\" ");
		sb.append("instance-id=\"lvsi\"> \t\t");
		sb.append("<dynamic-content language-id=\"en_US\">");
		sb.append("<![CDATA[{\"classPK\":\"");
		sb.append(_fileEntry.getFileEntryId());
		sb.append("\",\"groupId\":\"");
		sb.append(_fileEntry.getGroupId());
		sb.append("\",\"title\":\"");
		sb.append(_fileEntry.getTitle());
		sb.append("\",\"type\":\"document\",\"uuid\":\"");
		sb.append(_fileEntry.getUuid());
		sb.append("\"}]]></dynamic-content> \t</dynamic-element> </root>");

		String content = sb.toString();

		_journalArticle = _journalArticleLocalService.addArticle(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, nameMap, nameMap,
			content, structure.getStructureId(), _ddmTemplate.getTemplateKey(),
			serviceContext);
	}

	private void _initJournalReferences() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId(), TestPropsValues.getUserId());

		_journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		InputStream inputStream = new UnsyncByteArrayInputStream(new byte[0]);
		long size = 0;
		File file = FileUtil.createTempFile(inputStream);

		_createDDMFormWithJournalField(_stagingGroup, _journalArticle, null);

		Map<String, com.liferay.dynamic.data.mapping.kernel.DDMFormValues>
			ddmFormValuesMap =
				HashMapBuilder.
					<String,
					 com.liferay.dynamic.data.mapping.kernel.DDMFormValues>put(
						_ddmStructure.getStructureKey(),
						DDMBeanTranslatorUtil.translate(_journalDDMFormValues)
					).build();

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
				_ddmStructure.getStructureId(), null,
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				serviceContext);

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			_stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			MimeTypesUtil.getExtensionContentType("txt"),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK,
			dlFileEntryType.getFileEntryTypeId(), ddmFormValuesMap, file,
			inputStream, size, null, null, null, serviceContext);

		_fileEntry = _dlAppLocalService.getFileEntry(
			dlFileEntry.getFileEntryId());
	}

	private DDMStructure _ddmStructure;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	private DDMTemplate _ddmTemplate;

	@Inject
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.dynamic.data.mapping.storage.DDMFormValues"
	)
	private ExportImportContentProcessor<DDMFormValues>
		_exportImportContentProcessor;

	private FileEntry _fileEntry;
	private DDMFormInstance _formInstance;
	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private DDMFormValues _journalDDMFormValues;

	@DeleteAfterTestRun
	private Group _liveGroup;

	private PortletDataContext _portletDataContextExport;
	private PortletDataContext _portletDataContextImport;
	private Group _stagingGroup;

}
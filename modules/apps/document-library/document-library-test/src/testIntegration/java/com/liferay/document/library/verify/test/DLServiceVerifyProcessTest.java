/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.verify.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLTrashServiceUtil;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.kernel.DDMForm;
import com.liferay.dynamic.data.mapping.kernel.DDMFormField;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.Folder;
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
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.verify.VerifyProcess;
import com.liferay.portal.verify.test.util.BaseVerifyProcessTestCase;

import java.io.ByteArrayInputStream;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 * @author Eudaldo Alonso
 * @author Sergio González
 */
@RunWith(Arquillian.class)
public class DLServiceVerifyProcessTest extends BaseVerifyProcessTestCase {

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

		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDLFileEntryTreePathWithDLFileEntryInTrash()
		throws Exception {

		Folder parentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		FileEntry fileEntry = addFileEntry(parentFolder.getFolderId());

		DLTrashServiceUtil.moveFileEntryToTrash(fileEntry.getFileEntryId());

		DLFolderLocalServiceUtil.deleteFolder(
			parentFolder.getFolderId(), false);

		doVerify();
	}

	@Test
	public void testDLFileEntryTreePathWithParentDLFolderInTrash()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Folder grandparentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		Folder parentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(), grandparentFolder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		addFileEntry(parentFolder.getFolderId());

		DLTrashServiceUtil.moveFolderToTrash(parentFolder.getFolderId());

		DLFolderLocalServiceUtil.deleteFolder(
			grandparentFolder.getFolderId(), false);

		doVerify();
	}

	@Test
	public void testDLFileEntryWithNoAssetEntryGetsAssetEntryAdded()
		throws Exception {

		DLFileEntry dlFileEntry = addDLFileEntry();

		long fileEntryId = dlFileEntry.getFileEntryId();

		AssetEntryLocalServiceUtil.deleteEntry(
			DLFileEntry.class.getName(), fileEntryId);

		Assert.assertNotNull(
			DLFileEntryLocalServiceUtil.fetchDLFileEntry(fileEntryId));
		Assert.assertNull(
			AssetEntryLocalServiceUtil.fetchEntry(
				DLFileEntry.class.getName(), fileEntryId));

		doVerify();

		Assert.assertNotNull(
			DLFileEntryLocalServiceUtil.fetchDLFileEntry(fileEntryId));
		Assert.assertNotNull(
			AssetEntryLocalServiceUtil.fetchEntry(
				DLFileEntry.class.getName(), fileEntryId));
	}

	@Test
	public void testDLFileShortcutTreePathWithDLFileShortcutInTrash()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Folder parentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		FileEntry fileEntry = addFileEntry(parentFolder.getFolderId());

		FileShortcut fileShortcut = DLAppLocalServiceUtil.addFileShortcut(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			parentFolder.getFolderId(), fileEntry.getFileEntryId(),
			serviceContext);

		DLTrashServiceUtil.moveFileShortcutToTrash(
			fileShortcut.getFileShortcutId());

		DLFolderLocalServiceUtil.deleteFolder(
			parentFolder.getFolderId(), false);

		doVerify();
	}

	@Test
	public void testDLFileShortcutTreePathWithParentDLFolderInTrash()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Folder grandparentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		Folder parentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(), grandparentFolder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		addFileEntry(parentFolder.getFolderId());

		DLTrashServiceUtil.moveFolderToTrash(parentFolder.getFolderId());

		DLFolderLocalServiceUtil.deleteFolder(
			grandparentFolder.getFolderId(), false);

		doVerify();
	}

	@Test
	public void testDLFolderTreePathWithDLFolderInTrash() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Folder parentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		Folder folder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(), parentFolder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		DLTrashServiceUtil.moveFolderToTrash(folder.getFolderId());

		DLFolderLocalServiceUtil.deleteFolder(
			parentFolder.getFolderId(), false);

		doVerify();
	}

	@Test
	public void testDLFolderTreePathWithParentDLFolderInTrash()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		Folder grandparentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		Folder parentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(), grandparentFolder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		DLAppServiceUtil.addFolder(
			null, _group.getGroupId(), parentFolder.getFolderId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		DLTrashServiceUtil.moveFolderToTrash(parentFolder.getFolderId());

		DLFolderLocalServiceUtil.deleteFolder(
			grandparentFolder.getFolderId(), false);

		doVerify();
	}

	@Test
	public void testDLFolderWithNoAssetEntryGetsAssetEntryAdded()
		throws Exception {

		DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		long folderId = dlFolder.getFolderId();

		AssetEntryLocalServiceUtil.deleteEntry(
			DLFolder.class.getName(), folderId);

		Assert.assertNotNull(DLFolderLocalServiceUtil.fetchDLFolder(folderId));
		Assert.assertNull(
			AssetEntryLocalServiceUtil.fetchEntry(
				DLFolder.class.getName(), folderId));

		doVerify();

		Assert.assertNotNull(DLFolderLocalServiceUtil.fetchDLFolder(folderId));
		Assert.assertNotNull(
			AssetEntryLocalServiceUtil.fetchEntry(
				DLFolder.class.getName(), folderId));
	}

	@Test
	public void testDeleteMismatchCompanyIdDLFileEntryMetadatas()
		throws Exception {

		DLFileEntry dlFileEntry = addDLFileEntry();

		DLFileEntryType dlFileEntryType = dlFileEntry.getDLFileEntryType();

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntryType);

		DDMStructure ddmStructure = ddmStructures.get(0);

		ddmStructure.setCompanyId(0);

		try {
			ddmStructure = DDMStructureLocalServiceUtil.updateDDMStructure(
				ddmStructure);

			DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

			DLFileEntryMetadata dlFileEntryMetadata =
				DLFileEntryMetadataLocalServiceUtil.fetchFileEntryMetadata(
					ddmStructure.getStructureId(),
					dlFileVersion.getFileVersionId());

			Assert.assertNotNull(dlFileEntryMetadata);

			doVerify();

			dlFileEntryMetadata =
				DLFileEntryMetadataLocalServiceUtil.fetchFileEntryMetadata(
					ddmStructure.getStructureId(),
					dlFileVersion.getFileVersionId());

			Assert.assertNull(dlFileEntryMetadata);
		}
		finally {
			ddmStructure.setCompanyId(dlFileEntryType.getCompanyId());

			DDMStructureLocalServiceUtil.updateDDMStructure(ddmStructure);
		}
	}

	@Test
	public void testDeleteNoStructuresDLFileEntryMetadatas() throws Exception {
		DLFileEntry dlFileEntry = addDLFileEntry();

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntry.getDLFileEntryType());

		DDMStructure ddmStructure = ddmStructures.get(0);

		DDMStructureLocalServiceUtil.deleteDDMStructure(
			ddmStructure.getStructureId());

		DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

		DLFileEntryMetadata dlFileEntryMetadata =
			DLFileEntryMetadataLocalServiceUtil.fetchFileEntryMetadata(
				ddmStructure.getStructureId(),
				dlFileVersion.getFileVersionId());

		Assert.assertNotNull(dlFileEntryMetadata);

		doVerify();

		dlFileEntryMetadata =
			DLFileEntryMetadataLocalServiceUtil.fetchFileEntryMetadata(
				ddmStructure.getStructureId(),
				dlFileVersion.getFileVersionId());

		Assert.assertNull(dlFileEntryMetadata);
	}

	protected DLFileEntry addDLFileEntry() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		byte[] bytes = FileUtil.getBytes(
			getClass(),
			"/com/liferay/document/library/service/test/dependencies" +
				"/ddmstructure.xml");

		DDMFormDeserializerDeserializeRequest.Builder builder =
			DDMFormDeserializerDeserializeRequest.Builder.newBuilder(
				new String(bytes));

		DDMFormDeserializerDeserializeResponse
			ddmFormDeserializerDeserializeResponse =
				_ddmFormDeserializer.deserialize(builder.build());

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			DLFileEntryMetadata.class.getName(),
			ddmFormDeserializerDeserializeResponse.getDDMForm());

		User user = TestPropsValues.getUser();

		serviceContext.setLanguageId(LocaleUtil.toLanguageId(user.getLocale()));

		DLFileEntryType dlFileEntryType =
			DLFileEntryTypeLocalServiceUtil.addFileEntryType(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				ddmStructure.getStructureId(), null,
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				serviceContext);

		Map<String, DDMFormValues> ddmFormValuesMap = getDDMFormValuesMap(
			ddmStructure.getStructureKey(), user.getLocale());

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
			TestDataConstants.TEST_BYTE_ARRAY);

		return DLFileEntryLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null,
			dlFileEntryType.getFileEntryTypeId(), ddmFormValuesMap, null,
			byteArrayInputStream, byteArrayInputStream.available(), null, null,
			null, serviceContext);
	}

	protected FileEntry addFileEntry(long folderId) throws Exception {
		return DLAppLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), folderId,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	protected Map<String, DDMFormValues> getDDMFormValuesMap(
		String ddmStructureKey, Locale currentLocale) {

		return HashMapBuilder.<String, DDMFormValues>put(
			ddmStructureKey,
			() -> {
				Set<Locale> availableLocales =
					DDMFormTestUtil.createAvailableLocales(currentLocale);

				DDMForm ddmForm = new DDMForm();

				ddmForm.setAvailableLocales(availableLocales);
				ddmForm.setDefaultLocale(currentLocale);

				DDMFormField ddmFormField = new DDMFormField(
					"date_an", DDMFormFieldType.DATE);

				ddmFormField.setDataType("date");

				ddmForm.addDDMFormField(ddmFormField);

				DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

				ddmFormValues.setAvailableLocales(availableLocales);
				ddmFormValues.setDefaultLocale(currentLocale);

				DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

				ddmFormFieldValue.setName("date_an");
				ddmFormFieldValue.setValue(new UnlocalizedValue(""));

				ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

				return ddmFormValues;
			}
		).build();
	}

	@Override
	protected VerifyProcess getVerifyProcess() {
		return _verifyProcess;
	}

	@Inject(filter = "ddm.form.deserializer.type=xsd")
	private DDMFormDeserializer _ddmFormDeserializer;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.document.library.internal.verify.DLServiceVerifyProcess",
		type = VerifyProcess.class
	)
	private VerifyProcess _verifyProcess;

}
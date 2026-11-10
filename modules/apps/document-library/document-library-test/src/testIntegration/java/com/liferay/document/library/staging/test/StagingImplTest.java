/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.staging.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLTrashService;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.external.shortcut.resolver.DLVideoExternalShortcutResolver;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.LocalizedValue;
import com.liferay.dynamic.data.mapping.kernel.Value;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslator;
import com.liferay.dynamic.data.mapping.util.DDMUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationParameterMapFactoryUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.kernel.staging.StagingConstants;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wulbern
 */
@RunWith(Arquillian.class)
public class StagingImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo(
		{
			"LPD-113633", "LPS-90557", "LPS-136822", "LPS-136824", "LPS-136828",
			"LPS-136831", "LPS-154396", "LPS-154416", "LPS-154417", "LPS-155794"
		}
	)
	public void testPublishLayouts() throws Exception {
		_testPublishDeletedFileEntryWithFileEntryType();
		_testPublishDeletedFileEntryWithFileShortcut();
		_testPublishFileEntryTypeChanges();
		_testPublishFileEntryURLTitleChanges();
		_testPublishFileEntryWithURLTitleAndDisableStaging();
		_testPublishFolderMovedToRecycleBin();
		_testPublishMetadataSets();
		_testPublishUpdatedFileEntryWithFileEntryType();
		_testPublishVideoExternalShortcut();
	}

	private FileEntry _addFileEntry(long folderId) throws Exception {
		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			folderId, RandomTestUtil.randomString() + ".txt",
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomBytes(), null, null,
			null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));
	}

	private DLFileEntryType _addFileEntryType(
			DDMStructure metadataSetDDMStructure)
		throws Exception {

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_stagingGroup.getGroupId(), DLFileEntryMetadata.class.getName());

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.addFileEntryType(
				null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
				ddmStructure.getStructureId(), null,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
				ServiceContextTestUtil.getServiceContext(
					_stagingGroup.getGroupId()));

		ddmStructure = _ddmStructureLocalService.getDDMStructure(
			ddmStructure.getStructureId());

		ddmStructure.setStructureKey(
			DLUtil.getDDMStructureKey(dlFileEntryType));

		_ddmStructureLocalService.updateDDMStructure(ddmStructure);

		_dlFileEntryTypeLocalService.addDDMStructureLinks(
			dlFileEntryType.getFileEntryTypeId(),
			Collections.singleton(metadataSetDDMStructure.getStructureId()));

		return dlFileEntryType;
	}

	private DDMStructure _addMetadataSetDDMStructure() throws Exception {
		return DDMStructureTestUtil.addStructure(
			_stagingGroup.getGroupId(), DLFileEntryMetadata.class.getName(),
			DDMStructureTestUtil.getSampleDDMForm("metadataSetField"));
	}

	private void _addStagingAttribute(
		ServiceContext serviceContext, String key, Object value) {

		serviceContext.setAttribute(
			StagingConstants.STAGED_PREFIX + key + StringPool.DOUBLE_DASH,
			GetterUtil.getString(value));
	}

	private void _assertDDMFormValues(FileEntry fileEntry, String value)
		throws Exception {

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		FileVersion fileVersion = fileEntry.getFileVersion();

		Map<String, DDMFormValues> ddmFormValuesMap =
			dlFileEntry.getDDMFormValuesMap(fileVersion.getFileVersionId());

		Assert.assertEquals(
			ddmFormValuesMap.toString(), 2, ddmFormValuesMap.size());

		for (DDMFormValues ddmFormValues : ddmFormValuesMap.values()) {
			List<DDMFormFieldValue> ddmFormFieldValues =
				ddmFormValues.getDDMFormFieldValues();

			Assert.assertFalse(ddmFormFieldValues.isEmpty());

			for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
				Value ddmFormFieldValueValue = ddmFormFieldValue.getValue();

				Assert.assertEquals(
					value, ddmFormFieldValueValue.getString(LocaleUtil.US));
			}
		}
	}

	private FileEntry _assertLiveURLTitle(
			FileEntry fileEntry, String mainURLTitle, String urlTitle)
		throws Exception {

		FileEntry liveFileEntry = _getLiveFileEntry(fileEntry);

		Assert.assertEquals(
			StringBundler.concat(
				PortalUtil.getPathContext(), "/documents/d",
				_liveGroup.getFriendlyURL(), StringPool.SLASH, mainURLTitle),
			_dlURLHelper.getPreviewURL(
				liveFileEntry, liveFileEntry.getFileVersion(), null,
				StringPool.BLANK, false, false));

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				_liveGroup.getGroupId(),
				PortalUtil.getClassNameId(FileEntry.class), urlTitle);

		Assert.assertEquals(
			liveFileEntry.getFileEntryId(), friendlyURLEntry.getClassPK());

		return liveFileEntry;
	}

	private DDMFormFieldValue _createDDMFormFieldValue(
		String name, String value) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setName(name);

		LocalizedValue localizedValue = new LocalizedValue(
			LocaleUtil.getSiteDefault());

		localizedValue.addString(LocaleUtil.getSiteDefault(), value);

		ddmFormFieldValue.setValue(localizedValue);

		return ddmFormFieldValue;
	}

	private void _enableLocalStaging() throws Exception {
		_liveGroup = GroupTestUtil.addGroup();

		_groups.add(_liveGroup);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_liveGroup.getGroupId());

		_addStagingAttribute(
			serviceContext, PortletDataHandlerKeys.DATA_STRATEGY_MIRROR, true);
		_addStagingAttribute(
			serviceContext,
			_staging.getStagedPortletId(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN),
			true);

		_stagingLocalService.enableLocalStaging(
			TestPropsValues.getUserId(), _liveGroup, false, false,
			serviceContext);

		_liveGroup = _groupLocalService.getGroup(_liveGroup.getGroupId());

		_stagingGroup = _liveGroup.getStagingGroup();
	}

	private DLFileEntryType _getDLFileEntryType(FileEntry fileEntry)
		throws Exception {

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		return _dlFileEntryTypeLocalService.getDLFileEntryType(
			dlFileEntry.getFileEntryTypeId());
	}

	private FileEntry _getLiveFileEntry(FileEntry fileEntry) throws Exception {
		return _dlAppLocalService.getFileEntryByUuidAndGroupId(
			fileEntry.getUuid(), _liveGroup.getGroupId());
	}

	private int _getLiveFoldersAndFileEntriesAndFileShortcutsCount()
		throws Exception {

		return _dlAppService.getFoldersAndFileEntriesAndFileShortcutsCount(
			_liveGroup.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			WorkflowConstants.STATUS_APPROVED, false);
	}

	private ServiceContext _getServiceContext(
			DLFileEntryType dlFileEntryType, String value)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId());

		serviceContext.setAttribute(
			"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

		for (DDMStructure ddmStructure :
				DLFileEntryTypeUtil.getDDMStructures(
					_dlFileEntryTypeLocalService.getFileEntryType(
						dlFileEntryType.getFileEntryTypeId()))) {

			DDMFormValues ddmFormValues = new DDMFormValues(
				_ddmBeanTranslator.translate(ddmStructure.getDDMForm()));

			ddmFormValues.addAvailableLocale(LocaleUtil.US);
			ddmFormValues.setDefaultLocale(LocaleUtil.US);

			for (String fieldName : ddmStructure.getFieldNames()) {
				DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

				ddmFormFieldValue.setInstanceId(StringUtil.randomString(8));
				ddmFormFieldValue.setName(fieldName);

				LocalizedValue localizedValue = new LocalizedValue(
					LocaleUtil.US);

				localizedValue.addString(LocaleUtil.US, value);

				ddmFormFieldValue.setValue(localizedValue);

				ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
			}

			serviceContext.setAttribute(
				DDMFormValues.class.getName() + StringPool.POUND +
					ddmStructure.getStructureId(),
				ddmFormValues);
		}

		return serviceContext;
	}

	private ServiceContext _getVideoExternalShortcutServiceContext(String url)
		throws Exception {

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_stagingGroup.getCompanyId());

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.getFileEntryType(
				companyGroup.getGroupId(), "DL_VIDEO_EXTERNAL_SHORTCUT");

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			dlFileEntryType);

		DDMStructure ddmStructure = ddmStructures.get(0);

		DDMFormValues ddmFormValues = new DDMFormValues(
			_ddmBeanTranslator.translate(ddmStructure.getDDMForm()));

		ddmFormValues.addAvailableLocale(LocaleUtil.getSiteDefault());
		ddmFormValues.addDDMFormFieldValue(
			_createDDMFormFieldValue("HTML", RandomTestUtil.randomString()));
		ddmFormValues.addDDMFormFieldValue(
			_createDDMFormFieldValue("URL", url));
		ddmFormValues.setDefaultLocale(LocaleUtil.getSiteDefault());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId());

		serviceContext.setAttribute(
			DDMFormValues.class.getName() + StringPool.POUND +
				ddmStructure.getStructureId(),
			ddmFormValues);
		serviceContext.setAttribute(
			"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

		return serviceContext;
	}

	private void _publish() throws Exception {
		_staging.publishLayouts(
			TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			_liveGroup.getGroupId(), false,
			ExportImportConfigurationParameterMapFactoryUtil.
				buildFullPublishParameterMap());
	}

	private void _testPublishDeletedFileEntryWithFileEntryType()
		throws Exception {

		_enableLocalStaging();

		DLFileEntryType dlFileEntryType = _addFileEntryType(
			_addMetadataSetDDMStructure());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomBytes(), null, null, null,
			_getServiceContext(dlFileEntryType, RandomTestUtil.randomString()));

		_publish();

		DLFileEntryType liveDLFileEntryType = _getDLFileEntryType(
			_getLiveFileEntry(fileEntry));

		Assert.assertEquals(
			dlFileEntryType.getUuid(), liveDLFileEntryType.getUuid());

		_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());

		_publish();

		Assert.assertNull(
			_dlFileEntryLocalService.fetchDLFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), _liveGroup.getGroupId()));
	}

	private void _testPublishDeletedFileEntryWithFileShortcut()
		throws Exception {

		_enableLocalStaging();

		FileEntry fileEntry = _addFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_dlAppLocalService.addFileShortcut(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));

		_publish();

		Assert.assertEquals(
			2, _getLiveFoldersAndFileEntriesAndFileShortcutsCount());

		_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());

		_publish();

		Assert.assertEquals(
			0, _getLiveFoldersAndFileEntriesAndFileShortcutsCount());
	}

	private void _testPublishFileEntryTypeChanges() throws Exception {
		_enableLocalStaging();

		FileEntry fileEntry = _addFileEntry(
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		DDMStructure metadataSetDDMStructure = _addMetadataSetDDMStructure();

		DLFileEntryType dlFileEntryType = _addFileEntryType(
			metadataSetDDMStructure);

		_publish();

		FileEntry liveFileEntry = _getLiveFileEntry(fileEntry);

		DLFileEntry liveDLFileEntry = (DLFileEntry)liveFileEntry.getModel();

		Assert.assertEquals(
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			liveDLFileEntry.getFileEntryTypeId());

		String dlFileEntryTypeName = RandomTestUtil.randomString();

		_dlFileEntryTypeLocalService.updateFileEntryType(
			dlFileEntryType.getFileEntryTypeId(),
			Collections.singletonMap(LocaleUtil.US, dlFileEntryTypeName),
			dlFileEntryType.getDescriptionMap());

		DDMForm ddmForm = metadataSetDDMStructure.getDDMForm();

		DDMForm sampleDDMForm = DDMStructureTestUtil.getSampleDDMForm(
			"newField");

		List<DDMFormField> ddmFormFields = sampleDDMForm.getDDMFormFields();

		ddmForm.addDDMFormField(ddmFormFields.get(0));

		_ddmStructureLocalService.updateStructure(
			TestPropsValues.getUserId(),
			metadataSetDDMStructure.getStructureId(), ddmForm,
			DDMUtil.getDefaultDDMFormLayout(ddmForm),
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));

		String value = RandomTestUtil.randomString();

		_dlAppLocalService.updateFileEntry(
			TestPropsValues.getUserId(), fileEntry.getFileEntryId(),
			fileEntry.getFileName(), fileEntry.getMimeType(),
			fileEntry.getTitle(), null, fileEntry.getDescription(),
			StringPool.BLANK, DLVersionNumberIncrease.MINOR, (byte[])null, null,
			null, null, _getServiceContext(dlFileEntryType, value));

		_publish();

		liveFileEntry = _getLiveFileEntry(fileEntry);

		DLFileEntryType liveDLFileEntryType = _getDLFileEntryType(
			liveFileEntry);

		Assert.assertEquals(
			dlFileEntryTypeName, liveDLFileEntryType.getName(LocaleUtil.US));
		Assert.assertEquals(
			dlFileEntryType.getUuid(), liveDLFileEntryType.getUuid());

		DDMStructure liveMetadataSetDDMStructure =
			_ddmStructureLocalService.getDDMStructureByUuidAndGroupId(
				metadataSetDDMStructure.getUuid(), _liveGroup.getGroupId());

		Assert.assertTrue(liveMetadataSetDDMStructure.hasField("newField"));

		_assertDDMFormValues(liveFileEntry, value);
	}

	private void _testPublishFileEntryURLTitleChanges() throws Exception {
		_enableLocalStaging();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			RandomTestUtil.randomString(), "urltitle", StringPool.BLANK,
			StringPool.BLANK,
			FileUtil.getBytes(getClass(), "dependencies/liferay.jpg"), null,
			null, null, serviceContext);

		_publish();

		_assertLiveURLTitle(fileEntry, "urltitle", "urltitle");

		_updateURLTitle(fileEntry, serviceContext, "modifiedurltitle");

		_publish();

		_assertLiveURLTitle(fileEntry, "modifiedurltitle", "urltitle");
	}

	private void _testPublishFileEntryWithURLTitleAndDisableStaging()
		throws Exception {

		_enableLocalStaging();

		byte[] bytes = FileUtil.getBytes(
			getClass(), "dependencies/liferay.jpg");
		String urlTitle = StringUtil.toLowerCase(RandomTestUtil.randomString());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			RandomTestUtil.randomString(), urlTitle, StringPool.BLANK,
			StringPool.BLANK, bytes, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));

		_publish();

		_stagingLocalService.disableStaging(
			_liveGroup,
			ServiceContextTestUtil.getServiceContext(_liveGroup.getGroupId()));

		Group group = _groupLocalService.getGroup(_liveGroup.getGroupId());

		Assert.assertFalse(group.hasStagingGroup());

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchFriendlyURLEntry(
				_liveGroup.getGroupId(),
				PortalUtil.getClassNameId(FileEntry.class), urlTitle);

		FileEntry liveFileEntry = _dlAppLocalService.getFileEntry(
			friendlyURLEntry.getClassPK());

		Assert.assertArrayEquals(
			bytes, FileUtil.getBytes(liveFileEntry.getContentStream()));
		Assert.assertEquals(fileEntry.getUuid(), liveFileEntry.getUuid());
	}

	private void _testPublishFolderMovedToRecycleBin() throws Exception {
		_enableLocalStaging();

		Folder folder = _dlAppLocalService.addFolder(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			ServiceContextTestUtil.getServiceContext(
				_stagingGroup.getGroupId()));

		_addFileEntry(folder.getFolderId());
		_addFileEntry(folder.getFolderId());

		_publish();

		Assert.assertEquals(
			1, _getLiveFoldersAndFileEntriesAndFileShortcutsCount());

		_dlTrashService.moveFolderToTrash(folder.getFolderId());

		_publish();

		Assert.assertEquals(
			0, _getLiveFoldersAndFileEntriesAndFileShortcutsCount());
	}

	private void _testPublishMetadataSets() throws Exception {
		_enableLocalStaging();

		DDMStructure ddmStructure1 = _addMetadataSetDDMStructure();
		DDMStructure ddmStructure2 = _addMetadataSetDDMStructure();

		_publish();

		for (DDMStructure ddmStructure :
				new DDMStructure[] {ddmStructure1, ddmStructure2}) {

			DDMStructure liveDDMStructure =
				_ddmStructureLocalService.fetchDDMStructureByUuidAndGroupId(
					ddmStructure.getUuid(), _liveGroup.getGroupId());

			Assert.assertEquals(
				ddmStructure.getName(LocaleUtil.US),
				liveDDMStructure.getName(LocaleUtil.US));
		}
	}

	private void _testPublishUpdatedFileEntryWithFileEntryType()
		throws Exception {

		_enableLocalStaging();

		DLFileEntryType dlFileEntryType = _addFileEntryType(
			_addMetadataSetDDMStructure());

		ServiceContext serviceContext = _getServiceContext(
			dlFileEntryType, RandomTestUtil.randomString());

		String assetTagName = StringUtil.toLowerCase(
			RandomTestUtil.randomString());

		serviceContext.setAssetTagNames(new String[] {assetTagName});

		String urlTitle = StringUtil.toLowerCase(RandomTestUtil.randomString());

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".jpg", ContentTypes.IMAGE_JPEG,
			RandomTestUtil.randomString(), urlTitle,
			RandomTestUtil.randomString(), StringPool.BLANK,
			FileUtil.getBytes(getClass(), "dependencies/liferay.jpg"), null,
			null, null, serviceContext);

		_publish();

		FileEntry liveFileEntry = _getLiveFileEntry(fileEntry);

		Assert.assertEquals(fileEntry.getTitle(), liveFileEntry.getTitle());

		byte[] bytes = FileUtil.getBytes(getClass(), "dependencies/sample.png");
		String description = RandomTestUtil.randomString();
		String fileName = RandomTestUtil.randomString() + ".png";
		String title = RandomTestUtil.randomString();

		String value = RandomTestUtil.randomString();

		serviceContext = _getServiceContext(dlFileEntryType, value);

		serviceContext.setAssetTagNames(new String[] {assetTagName});

		_dlAppLocalService.updateFileEntry(
			TestPropsValues.getUserId(), fileEntry.getFileEntryId(), fileName,
			ContentTypes.IMAGE_PNG, title, urlTitle, description,
			StringPool.BLANK, DLVersionNumberIncrease.MAJOR, bytes, null, null,
			null, serviceContext);

		_publish();

		liveFileEntry = _getLiveFileEntry(fileEntry);

		Assert.assertArrayEquals(
			bytes, FileUtil.getBytes(liveFileEntry.getContentStream()));
		Assert.assertEquals(description, liveFileEntry.getDescription());
		Assert.assertEquals(fileName, liveFileEntry.getFileName());
		Assert.assertEquals(
			ContentTypes.IMAGE_PNG, liveFileEntry.getMimeType());
		Assert.assertEquals(title, liveFileEntry.getTitle());
		Assert.assertEquals("2.0", liveFileEntry.getVersion());

		DLFileEntryType liveDLFileEntryType = _getDLFileEntryType(
			liveFileEntry);

		Assert.assertEquals(
			dlFileEntryType.getUuid(), liveDLFileEntryType.getUuid());

		_assertDDMFormValues(liveFileEntry, value);

		Assert.assertArrayEquals(
			new String[] {assetTagName},
			_assetTagLocalService.getTagNames(
				DLFileEntry.class.getName(), liveFileEntry.getFileEntryId()));

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
				PortalUtil.getClassNameId(FileEntry.class),
				liveFileEntry.getFileEntryId());

		Assert.assertEquals(urlTitle, friendlyURLEntry.getUrlTitle());
	}

	private void _testPublishVideoExternalShortcut() throws Exception {
		_enableLocalStaging();

		String url = "https://www.youtube.com/watch?v=HOdbzGCI5ME";

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _stagingGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			ContentTypes.APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML,
			RandomTestUtil.randomString(), "urltitle", StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			_getVideoExternalShortcutServiceContext(url));

		_publish();

		_assertLiveURLTitle(fileEntry, "urltitle", "urltitle");

		_updateURLTitle(
			fileEntry, _getVideoExternalShortcutServiceContext(url),
			"modifiedurltitle");

		_publish();

		FileEntry liveFileEntry = _assertLiveURLTitle(
			fileEntry, "modifiedurltitle", "urltitle");

		DLVideoExternalShortcut dlVideoExternalShortcut =
			_dlVideoExternalShortcutResolver.resolve(
				liveFileEntry.getFileVersion());

		Assert.assertEquals(url, dlVideoExternalShortcut.getURL());
	}

	private void _updateURLTitle(
			FileEntry fileEntry, ServiceContext serviceContext, String urlTitle)
		throws Exception {

		_dlAppLocalService.updateFileEntry(
			TestPropsValues.getUserId(), fileEntry.getFileEntryId(),
			fileEntry.getFileName(), fileEntry.getMimeType(),
			fileEntry.getTitle(), urlTitle, fileEntry.getDescription(),
			StringPool.BLANK, DLVersionNumberIncrease.MINOR, (byte[])null, null,
			null, null, serviceContext);
	}

	@Inject
	private AssetTagLocalService _assetTagLocalService;

	@Inject
	private DDMBeanTranslator _ddmBeanTranslator;

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
	private DLURLHelper _dlURLHelper;

	@Inject
	private DLVideoExternalShortcutResolver _dlVideoExternalShortcutResolver;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	private Group _liveGroup;

	@Inject
	private Staging _staging;

	private Group _stagingGroup;

	@Inject
	private StagingLocalService _stagingLocalService;

}
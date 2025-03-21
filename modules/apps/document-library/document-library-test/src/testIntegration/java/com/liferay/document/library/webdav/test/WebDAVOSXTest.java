/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.webdav.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.document.library.webdav.test.rule.WebDAVEnvironmentConfigClassTestRule;
import com.liferay.dynamic.data.mapping.kernel.DDMForm;
import com.liferay.dynamic.data.mapping.kernel.DDMFormField;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslatorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
 * <p>
 * Based on Microsoft Office 2008 for OS X.
 * </p>
 *
 * @author Alexander Chow
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class WebDAVOSXTest extends BaseWebDAVTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			WebDAVEnvironmentConfigClassTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Class<?> clazz = getClass();

		_testFileBytes = FileUtil.getBytes(clazz, _OFFICE_TEST_DOCX);
		_testMetaBytes = FileUtil.getBytes(clazz, _OFFICE_TEST_META_DOCX);
		_testDeltaBytes = FileUtil.getBytes(clazz, _OFFICE_TEST_DELTA_DOCX);

		servicePut(_TEST_FILE_NAME, _testFileBytes, getLock(_TEST_FILE_NAME));
	}

	@Test
	public void testChangeFileNameChangeWebDavDocumentFileName()
		throws Exception {

		FileEntry fileEntry = null;

		try {
			lock(HttpServletResponse.SC_OK, _TEST_FILE_NAME);

			assertCode(
				HttpServletResponse.SC_CREATED,
				servicePut(
					_TEST_FILE_NAME, _testFileBytes, getLock(_TEST_FILE_NAME)));

			unlock(_TEST_FILE_NAME);

			Folder folder = _dlAppLocalService.getFolder(
				TestPropsValues.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, getFolderName());

			fileEntry = _dlAppLocalService.getFileEntry(
				TestPropsValues.getGroupId(), folder.getFolderId(),
				_TEST_FILE_TITLE);

			long fileEntryId = fileEntry.getFileEntryId();

			_dlAppLocalService.updateFileEntry(
				TestPropsValues.getUserId(), fileEntryId, _TEST_FILE_NAME_2,
				ContentTypes.APPLICATION_TEXT, _TEST_FILE_TITLE,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				DLVersionNumberIncrease.MAJOR, _testFileBytes, null, null, null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

			servicePut(_TEST_FILE_NAME_2, _testDeltaBytes);

			fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			Assert.assertEquals(_TEST_FILE_TITLE, fileEntry.getTitle());
			Assert.assertEquals(_TEST_FILE_NAME_2, fileEntry.getFileName());

			assertCode(
				HttpServletResponse.SC_NOT_FOUND, serviceGet(_TEST_FILE_NAME));
			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_NAME_2));
			assertCode(HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_TITLE));
		}
		finally {
			if (fileEntry != null) {
				_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
			}
		}
	}

	@Test
	public void testChangeFileNameOSOnlyChangeFileName() throws Exception {
		FileEntry fileEntry = null;

		try {
			assertCode(
				HttpServletResponse.SC_CREATED,
				servicePut(
					_TEST_FILE_NAME_2, _testFileBytes,
					getLock(_TEST_FILE_NAME_2)));

			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_NAME_2));
			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_TITLE_2));

			Folder folder = _dlAppLocalService.getFolder(
				TestPropsValues.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, getFolderName());

			fileEntry = _dlAppLocalService.getFileEntry(
				TestPropsValues.getGroupId(), folder.getFolderId(),
				_TEST_FILE_TITLE_2);

			Assert.assertEquals(_TEST_FILE_TITLE_2, fileEntry.getTitle());
			Assert.assertEquals(_TEST_FILE_NAME_2, fileEntry.getFileName());

			assertCode(
				HttpServletResponse.SC_CREATED,
				serviceCopyOrMove(
					Method.MOVE, _TEST_FILE_NAME_2, _TEST_FILE_NAME_2_MOD));

			assertCode(
				HttpServletResponse.SC_NOT_FOUND,
				serviceGet(_TEST_FILE_NAME_2));
			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_NAME_2_MOD));
			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_TITLE_2));

			fileEntry = _dlAppLocalService.getFileEntry(
				TestPropsValues.getGroupId(), folder.getFolderId(),
				_TEST_FILE_TITLE_2);

			Assert.assertEquals(_TEST_FILE_TITLE_2, fileEntry.getTitle());
			Assert.assertEquals(_TEST_FILE_NAME_2_MOD, fileEntry.getFileName());
		}
		finally {
			if (fileEntry != null) {
				_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
			}
		}
	}

	@Test
	public void testChangeTitleFromRepositoryDoNotChangeFileName()
		throws Exception {

		FileEntry fileEntry = null;

		try {
			Folder folder = _dlAppLocalService.getFolder(
				TestPropsValues.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, getFolderName());

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId());

			fileEntry = _dlAppLocalService.addFileEntry(
				null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				folder.getFolderId(), _TEST_FILE_NAME_2,
				ContentTypes.APPLICATION_TEXT, _TEST_FILE_TITLE_2,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				_testFileBytes, null, null, null, serviceContext);

			lock(HttpServletResponse.SC_OK, _TEST_FILE_NAME_2);

			assertCode(
				HttpServletResponse.SC_CREATED,
				servicePut(
					_TEST_FILE_NAME_2, _testFileBytes,
					getLock(_TEST_FILE_NAME_2)));

			unlock(_TEST_FILE_NAME_2);

			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_NAME_2));
			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_TITLE_2));

			long fileEntryId = fileEntry.getFileEntryId();

			_dlAppLocalService.updateFileEntry(
				TestPropsValues.getUserId(), fileEntryId, _TEST_FILE_NAME_2,
				ContentTypes.APPLICATION_TEXT, _TEST_FILE_TITLE_2_MOD,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				DLVersionNumberIncrease.MAJOR, _testFileBytes, null, null, null,
				serviceContext);

			fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			Assert.assertEquals(_TEST_FILE_TITLE_2_MOD, fileEntry.getTitle());
			Assert.assertEquals(_TEST_FILE_NAME_2, fileEntry.getFileName());

			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_NAME_2));
			assertCode(
				HttpServletResponse.SC_NOT_FOUND,
				serviceGet(_TEST_FILE_TITLE_2));
			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_TITLE_2_MOD));
		}
		finally {
			if (fileEntry != null) {
				_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
			}
		}
	}

	@Test
	public void testChangeTitleFromWebDavDoNotChangeFileName()
		throws Exception {

		FileEntry fileEntry = null;

		try {
			assertCode(
				HttpServletResponse.SC_CREATED,
				servicePut(
					_TEST_FILE_NAME_2, _testFileBytes,
					getLock(_TEST_FILE_NAME_2)));

			Folder folder = _dlAppLocalService.getFolder(
				TestPropsValues.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, getFolderName());

			fileEntry = _dlAppLocalService.getFileEntry(
				TestPropsValues.getGroupId(), folder.getFolderId(),
				_TEST_FILE_TITLE_2);

			long fileEntryId = fileEntry.getFileEntryId();

			_dlAppLocalService.updateFileEntry(
				TestPropsValues.getUserId(), fileEntryId, _TEST_FILE_NAME_2,
				ContentTypes.APPLICATION_TEXT, _TEST_FILE_TITLE_2_MOD,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				DLVersionNumberIncrease.MAJOR, _testFileBytes, null, null, null,
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

			servicePut(_TEST_FILE_NAME_2, _testDeltaBytes);

			fileEntry = _dlAppLocalService.getFileEntry(fileEntryId);

			Assert.assertEquals(_TEST_FILE_TITLE_2_MOD, fileEntry.getTitle());
			Assert.assertEquals(_TEST_FILE_NAME_2, fileEntry.getFileName());

			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_NAME_2));
			assertCode(
				HttpServletResponse.SC_NOT_FOUND,
				serviceGet(_TEST_FILE_TITLE_2));
			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_TITLE_2_MOD));
		}
		finally {
			if (fileEntry != null) {
				_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
			}
		}
	}

	@Test
	public void testFileNameWithExtensionAndTitleWithoutExtension()
		throws Exception {

		FileEntry fileEntry = null;

		try {
			assertCode(
				HttpServletResponse.SC_CREATED,
				servicePut(
					_TEST_FILE_NAME_2, _testFileBytes,
					getLock(_TEST_FILE_NAME_2)));

			Folder folder = _dlAppLocalService.getFolder(
				TestPropsValues.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, getFolderName());

			fileEntry = _dlAppLocalService.getFileEntry(
				TestPropsValues.getGroupId(), folder.getFolderId(),
				_TEST_FILE_TITLE_2);

			Assert.assertEquals(_TEST_FILE_TITLE_2, fileEntry.getTitle());
			Assert.assertEquals(_TEST_FILE_NAME_2, fileEntry.getFileName());

			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_NAME_2));
			assertCode(
				HttpServletResponse.SC_OK, serviceGet(_TEST_FILE_TITLE_2));
		}
		finally {
			if (fileEntry != null) {
				_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
			}
		}
	}

	@Test
	public void testGetFileWithEscapedCharactersInFileName() throws Exception {
		FileEntry fileEntry = null;

		try {
			Group group = GroupLocalServiceUtil.getFriendlyURLGroup(
				PortalUtil.getDefaultCompanyId(), getGroupFriendlyURL());

			Folder folder = _dlAppLocalService.getFolder(
				group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				getFolderName());

			fileEntry = _dlAppLocalService.addFileEntry(
				null, TestPropsValues.getUserId(), group.getGroupId(),
				folder.getFolderId(), _TEST_FILE_NAME_ILLEGAL_CHARACTERS,
				ContentTypes.APPLICATION_MSWORD,
				_TEST_FILE_NAME_ILLEGAL_CHARACTERS, StringPool.BLANK,
				StringPool.BLANK, StringPool.BLANK, _testFileBytes, null, null,
				null,
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

			assertCode(
				HttpServletResponse.SC_OK,
				serviceGet(_TEST_FILE_NAME_ILLEGAL_CHARACTERS_ESCAPED));
		}
		finally {
			if (fileEntry != null) {
				_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
			}
		}
	}

	@Test
	public void testMSOffice1Create() throws Exception {
		Tuple tuple = null;

		for (int i = 0; i < 3; i++) {
			lock(HttpServletResponse.SC_OK, _TEST_FILE_NAME);
			unlock(_TEST_FILE_NAME);
		}

		lock(HttpServletResponse.SC_OK, _TEST_FILE_NAME);

		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut(
				_TEST_FILE_NAME, _testFileBytes, getLock(_TEST_FILE_NAME)));

		unlock(_TEST_FILE_NAME);

		for (int i = 0; i < 3; i++) {
			lock(HttpServletResponse.SC_OK, _TEST_FILE_NAME);

			tuple = serviceGet(_TEST_FILE_NAME);

			assertCode(HttpServletResponse.SC_OK, tuple);
			Assert.assertArrayEquals(_testFileBytes, getResponseBody(tuple));

			unlock(_TEST_FILE_NAME);
		}

		for (int i = 0; i < 2; i++) {
			assertCode(
				HttpServletResponse.SC_NOT_FOUND,
				servicePropFind(_TEST_META_NAME));
			assertCode(
				HttpServletResponse.SC_CREATED,
				servicePut(_TEST_META_NAME, _testMetaBytes));

			lock(HttpServletResponse.SC_OK, _TEST_META_NAME);

			assertCode(
				HttpServletResponse.SC_CREATED,
				servicePut(
					_TEST_META_NAME, _testMetaBytes, getLock(_TEST_META_NAME)));
			assertCode(
				WebDAVUtil.SC_MULTI_STATUS, servicePropFind(_TEST_META_NAME));

			tuple = serviceGet(_TEST_META_NAME);

			assertCode(HttpServletResponse.SC_OK, tuple);
			Assert.assertArrayEquals(_testMetaBytes, getResponseBody(tuple));

			unlock(_TEST_META_NAME);

			assertCode(
				HttpServletResponse.SC_NO_CONTENT,
				serviceDelete(_TEST_META_NAME));
		}

		for (int i = 0; i < 3; i++) {
			if (i == 1) {
				lock(HttpServletResponse.SC_OK, _TEST_META_NAME);

				tuple = serviceGet(_TEST_META_NAME);

				assertCode(HttpServletResponse.SC_OK, tuple);
				Assert.assertArrayEquals(
					_testMetaBytes, getResponseBody(tuple));
			}
			else {
				lock(HttpServletResponse.SC_CREATED, _TEST_META_NAME);

				tuple = serviceGet(_TEST_META_NAME);

				assertCode(HttpServletResponse.SC_OK, tuple);

				byte[] responseBody = getResponseBody(tuple);

				Assert.assertTrue(responseBody.length == 0);
			}

			unlock(_TEST_META_NAME);

			if (i == 0) {
				assertCode(
					HttpServletResponse.SC_CREATED,
					servicePut(
						_TEST_META_NAME, _testMetaBytes,
						getLock(_TEST_META_NAME)));

				tuple = serviceGet(_TEST_META_NAME);

				assertCode(HttpServletResponse.SC_OK, tuple);
				Assert.assertArrayEquals(
					_testMetaBytes, getResponseBody(tuple));

				assertCode(
					WebDAVUtil.SC_MULTI_STATUS,
					servicePropFind(_TEST_META_NAME));
			}
		}
	}

	@Test
	public void testMSOffice2Open() throws Exception {
		assertCode(
			WebDAVUtil.SC_MULTI_STATUS, servicePropFind(_TEST_FILE_NAME));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND, servicePropFind("MCF-Test.docx"));

		lock(HttpServletResponse.SC_OK, _TEST_FILE_NAME);

		Tuple tuple = serviceGet(_TEST_FILE_NAME);

		assertCode(HttpServletResponse.SC_OK, tuple);
		Assert.assertArrayEquals(_testFileBytes, getResponseBody(tuple));

		unlock(_TEST_FILE_NAME);
		lock(HttpServletResponse.SC_OK, _TEST_FILE_NAME);

		tuple = serviceGet(_TEST_FILE_NAME);

		assertCode(HttpServletResponse.SC_OK, tuple);
		Assert.assertArrayEquals(_testFileBytes, getResponseBody(tuple));
	}

	@Test
	public void testMSOffice3Modify() throws Exception {
		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind(_TEMP_FILE_NAME_1));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind("MCF-Word Work File D_1.tmp"));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind(_TEMP_FILE_NAME_1));
		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut(_TEMP_FILE_NAME_1, _testDeltaBytes));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind(_TEMP_META_NAME_1));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind(_TEMP_META_NAME_1));
		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut(_TEMP_META_NAME_1, _testMetaBytes));

		lock(HttpServletResponse.SC_OK, _TEMP_META_NAME_1);

		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut(
				_TEMP_META_NAME_1, _testMetaBytes, getLock(_TEMP_META_NAME_1)));
		assertCode(
			WebDAVUtil.SC_MULTI_STATUS, servicePropFind(_TEMP_FILE_NAME_1));

		unlock(_TEMP_META_NAME_1);
		lock(HttpServletResponse.SC_OK, _TEMP_FILE_NAME_1);
		unlock(_TEMP_FILE_NAME_1);
		lock(HttpServletResponse.SC_OK, _TEMP_FILE_NAME_1);

		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut(
				_TEMP_FILE_NAME_1, _testDeltaBytes,
				getLock(_TEMP_FILE_NAME_1)));
		assertCode(
			WebDAVUtil.SC_MULTI_STATUS, servicePropFind(_TEMP_FILE_NAME_1));

		unlock(_TEST_FILE_NAME);
		lock(HttpServletResponse.SC_OK, _TEST_FILE_NAME);

		Tuple tuple = serviceGet(_TEST_FILE_NAME);

		assertCode(HttpServletResponse.SC_OK, tuple);
		Assert.assertArrayEquals(_testFileBytes, getResponseBody(tuple));

		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind("Backup of Test.docx"));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind(_TEMP_FILE_NAME_2));

		unlock(_TEST_FILE_NAME);

		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind(_TEMP_FILE_NAME_2));
		assertCode(
			HttpServletResponse.SC_CREATED,
			serviceCopyOrMove(Method.MOVE, _TEST_FILE_NAME, _TEMP_FILE_NAME_2));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND, servicePropFind(_TEST_META_NAME));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind(_TEMP_META_NAME_2));

		for (int i = 0; i < 2; i++) {
			lock(HttpServletResponse.SC_OK, _TEMP_FILE_NAME_2);

			tuple = serviceGet(_TEMP_FILE_NAME_2);

			assertCode(HttpServletResponse.SC_OK, tuple);
			Assert.assertArrayEquals(_testFileBytes, getResponseBody(tuple));

			unlock(_TEMP_FILE_NAME_2);
		}

		for (int i = 0; i < 2; i++) {
			String orig = _TEMP_FILE_NAME_1;
			String dest = _TEST_FILE_NAME;

			if (i == 1) {
				orig = _TEMP_META_NAME_1;
				dest = _TEST_META_NAME;
			}

			assertCode(HttpServletResponse.SC_NOT_FOUND, servicePropFind(dest));

			if (i == 0) {
				assertCode(
					HttpServletResponse.SC_CREATED,
					serviceCopyOrMove(
						Method.MOVE, orig, _TEST_FILE_TITLE, getLock(orig)));

				moveLock(orig, dest);
			}
		}

		for (int i = 0; i < 2; i++) {
			lock(HttpServletResponse.SC_OK, _TEST_FILE_TITLE);

			tuple = serviceGet(_TEST_FILE_TITLE);

			assertCode(HttpServletResponse.SC_OK, tuple);
			Assert.assertArrayEquals(_testDeltaBytes, getResponseBody(tuple));

			unlock(_TEST_FILE_TITLE);
		}

		lock(HttpServletResponse.SC_CREATED, _TEST_META_NAME);

		tuple = serviceGet(_TEST_META_NAME);

		assertCode(HttpServletResponse.SC_OK, tuple);

		byte[] responseBody = getResponseBody(tuple);

		Assert.assertTrue(responseBody.length == 0);

		assertCode(
			HttpServletResponse.SC_CREATED,
			servicePut(
				_TEST_META_NAME, _testMetaBytes, getLock(_TEST_META_NAME)));
		assertCode(
			WebDAVUtil.SC_MULTI_STATUS, servicePropFind(_TEST_META_NAME));

		unlock(_TEST_META_NAME);
		unlock(_TEMP_FILE_NAME_2);

		assertCode(
			HttpServletResponse.SC_NO_CONTENT,
			serviceDelete(_TEMP_FILE_NAME_2));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind(_TEMP_META_NAME_2));
		assertCode(
			HttpServletResponse.SC_NOT_FOUND,
			servicePropFind(_TEMP_FILE_NAME_2));
	}

	@Test
	public void testPutFileWithDDMFormValuesKeepsDDMFormValues()
		throws Exception {

		FileEntry fileEntry = null;

		try {
			Group group = GroupLocalServiceUtil.getFriendlyURLGroup(
				PortalUtil.getDefaultCompanyId(), getGroupFriendlyURL());

			Folder folder = _dlAppLocalService.getFolder(
				TestPropsValues.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, getFolderName());

			DDMStructure ddmStructure = addDDMStructure(group);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId());

			DLFileEntryType dlFileEntryType =
				_dlFileEntryTypeLocalService.addFileEntryType(
					null, TestPropsValues.getUserId(),
					TestPropsValues.getGroupId(), ddmStructure.getStructureId(),
					null,
					Collections.singletonMap(
						LocaleUtil.US, RandomTestUtil.randomString()),
					Collections.singletonMap(
						LocaleUtil.US, RandomTestUtil.randomString()),
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
					serviceContext);

			serviceContext.setAttribute(
				"fileEntryTypeId", dlFileEntryType.getFileEntryTypeId());

			DDMFormValues expectedDDDMFormValues = getDDMFormValues();

			serviceContext.setAttribute(
				DDMFormValues.class.getName() + StringPool.POUND +
					ddmStructure.getStructureId(),
				expectedDDDMFormValues);

			fileEntry = _dlAppLocalService.addFileEntry(
				null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				folder.getFolderId(), _TEST_FILE_NAME_2,
				ContentTypes.APPLICATION_TEXT, _TEST_FILE_NAME_2,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				_testFileBytes, null, null, null, serviceContext);

			servicePut(_TEST_FILE_NAME_2, _testDeltaBytes);

			FileEntry finalFileEntry = _dlAppLocalService.getFileEntry(
				fileEntry.getFileEntryId());

			DLFileEntry finalDLFileEntryModel =
				(DLFileEntry)finalFileEntry.getModel();

			DLFileEntryType finalDLFileEntryModelDLFileEntryType =
				finalDLFileEntryModel.getDLFileEntryType();

			List<DDMStructure> ddmStructures =
				DLFileEntryTypeUtil.getDDMStructures(
					finalDLFileEntryModelDLFileEntryType);

			for (DDMStructure structure : ddmStructures) {
				String structureKey = structure.getStructureKey();

				if (structureKey.equals("TIKARAWMETADATA")) {
					continue;
				}

				FileVersion fileVersion = finalFileEntry.getLatestFileVersion();

				DLFileEntryMetadata fileEntryMetadata =
					_dlFileEntryMetadataLocalService.getFileEntryMetadata(
						structure.getStructureId(),
						fileVersion.getFileVersionId());

				com.liferay.dynamic.data.mapping.storage.DDMFormValues
					finalDDMFormValues =
						_ddmStorageEngineManager.getDDMFormValues(
							fileEntryMetadata.getDDMStorageId());

				Assert.assertTrue(
					expectedDDDMFormValues.equals(
						DDMBeanTranslatorUtil.translate(finalDDMFormValues)));
			}
		}
		finally {
			if (fileEntry != null) {
				_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
			}
		}
	}

	@Test
	public void testPutFileWithFileEntryTypeKeepsFileEntryType()
		throws Exception {

		FileEntry fileEntry = null;

		try {
			Group group = GroupLocalServiceUtil.getFriendlyURLGroup(
				PortalUtil.getDefaultCompanyId(), getGroupFriendlyURL());

			Folder folder = _dlAppLocalService.getFolder(
				TestPropsValues.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, getFolderName());

			DDMStructure ddmStructure = addDDMStructure(group);

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId());

			DLFileEntryType initialDLFileEntryType =
				_dlFileEntryTypeLocalService.addFileEntryType(
					null, TestPropsValues.getUserId(),
					TestPropsValues.getGroupId(), ddmStructure.getStructureId(),
					null,
					Collections.singletonMap(
						LocaleUtil.US, "New File Entry Type"),
					Collections.singletonMap(
						LocaleUtil.US, "New File Entry Type"),
					DLFileEntryTypeConstants.FILE_ENTRY_TYPE_SCOPE_DEFAULT,
					serviceContext);

			serviceContext.setAttribute(
				"fileEntryTypeId", initialDLFileEntryType.getFileEntryTypeId());

			DDMFormValues expectedDDDMFormValues = getDDMFormValues();

			serviceContext.setAttribute(
				DDMFormValues.class.getName() + StringPool.POUND +
					ddmStructure.getStructureId(),
				expectedDDDMFormValues);

			fileEntry = _dlAppLocalService.addFileEntry(
				null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				folder.getFolderId(), _TEST_FILE_NAME_2,
				ContentTypes.APPLICATION_TEXT, _TEST_FILE_NAME_2,
				StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
				_testFileBytes, null, null, null, serviceContext);

			servicePut(_TEST_FILE_NAME_2, _testDeltaBytes);

			FileEntry finalFileEntry = _dlAppLocalService.getFileEntry(
				fileEntry.getFileEntryId());

			DLFileEntry finalDLFileEntryModel =
				(DLFileEntry)finalFileEntry.getModel();

			DLFileEntryType finalFileEntryType =
				finalDLFileEntryModel.getDLFileEntryType();

			Assert.assertTrue(
				ListUtil.isNotEmpty(
					DLFileEntryTypeUtil.getDDMStructures(finalFileEntryType)));

			Assert.assertEquals(
				initialDLFileEntryType.getFileEntryTypeId(),
				finalFileEntryType.getFileEntryTypeId());
		}
		finally {
			if (fileEntry != null) {
				_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());
			}
		}
	}

	protected static DDMForm createDDMForm() {
		DDMForm ddmForm = new DDMForm();

		Set<Locale> availableLocales = new LinkedHashSet<>();

		availableLocales.add(LocaleUtil.US);

		ddmForm.setAvailableLocales(availableLocales);

		ddmForm.setDefaultLocale(LocaleUtil.US);

		return ddmForm;
	}

	protected DDMStructure addDDMStructure(Group group) throws Exception {
		DDMForm ddmForm = createDDMForm();

		DDMFormField ddmFormField = createLocalizableTextDDMFormField("Text");

		ddmFormField.setIndexType("text");

		ddmForm.addDDMFormField(ddmFormField);

		return DDMStructureTestUtil.addStructure(
			group.getGroupId(), DLFileEntryMetadata.class.getName(),
			DDMBeanTranslatorUtil.translate(ddmForm));
	}

	protected DDMFormField createLocalizableTextDDMFormField(String name) {
		DDMFormField ddmFormField = new DDMFormField(name, "separator");

		ddmFormField.setDataType("string");
		ddmFormField.setLocalizable(true);
		ddmFormField.setRepeatable(false);
		ddmFormField.setRequired(false);

		LocalizedValue localizedValue = ddmFormField.getLabel();

		localizedValue.addString(LocaleUtil.US, name);

		return ddmFormField;
	}

	protected DDMFormValues getDDMFormValues() {
		DDMForm ddmForm = createDDMForm();

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		DDMFormField ddmFormField = createLocalizableTextDDMFormField("Text");

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.setAvailableLocales(ddmForm.getAvailableLocales());
		ddmFormValues.setDefaultLocale(ddmForm.getDefaultLocale());

		return ddmFormValues;
	}

	protected String getLock(String fileName) {
		return _lockMap.get(fileName);
	}

	@Override
	protected String getUserAgent() {
		return _USER_AGENT;
	}

	protected void lock(int statusCode, String fileName) {
		Tuple tuple = serviceLock(fileName, null, 0);

		assertCode(statusCode, tuple);

		_lockMap.put(fileName, getLock(tuple));
	}

	protected void moveLock(String src, String dest) {
		String lock = _lockMap.remove(src);

		if (lock != null) {
			_lockMap.put(dest, lock);
		}
	}

	protected void unlock(String fileName) {
		String lock = _lockMap.remove(fileName);

		assertCode(
			HttpServletResponse.SC_NO_CONTENT, serviceUnlock(fileName, lock));
	}

	private static final String _OFFICE_TEST_DELTA_DOCX =
		"/com/liferay/document/library/dependencies/OSX_Test_Delta.docx";

	private static final String _OFFICE_TEST_DOCX =
		"/com/liferay/document/library/dependencies/OSX_Test.docx";

	private static final String _OFFICE_TEST_META_DOCX =
		"/com/liferay/document/library/dependencies/OSX_Test_Meta.docx";

	private static final String _TEMP_FILE_NAME_1 = "Word Work File D_1.tmp";

	private static final String _TEMP_FILE_NAME_2 = "Word Work File L_2.tmp";

	private static final String _TEMP_META_NAME_1 = "._Word Work File D_1.tmp";

	private static final String _TEMP_META_NAME_2 = "._Word Work File L_2.tmp";

	private static final String _TEST_FILE_NAME = "Test.docx";

	private static final String _TEST_FILE_NAME_2 = "Test2.docx";

	private static final String _TEST_FILE_NAME_2_MOD = "Test2Mod.docx";

	private static final String _TEST_FILE_NAME_ILLEGAL_CHARACTERS =
		"Test/0.docx";

	private static final String _TEST_FILE_NAME_ILLEGAL_CHARACTERS_ESCAPED =
		"Test" + PropsValues.DL_WEBDAV_SUBSTITUTION_CHAR + "0.docx";

	private static final String _TEST_FILE_TITLE = "Test";

	private static final String _TEST_FILE_TITLE_2 = "Test2";

	private static final String _TEST_FILE_TITLE_2_MOD = "Test2Mod";

	private static final String _TEST_META_NAME = "._Test.docx";

	private static final String _USER_AGENT =
		"WebDAVFS/1.8 (01808000) Darwin/10.3.0 (i386)";

	private static byte[] _testDeltaBytes;
	private static byte[] _testFileBytes;
	private static byte[] _testMetaBytes;

	@Inject
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Inject
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	private final Map<String, String> _lockMap = new HashMap<>();

}
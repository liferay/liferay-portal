/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileEntryTypeServiceUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalServiceUtil;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.dynamic.data.mapping.kernel.DDMForm;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslator;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoColumnLocalServiceUtil;
import com.liferay.expando.kernel.service.ExpandoTableLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Preston Crary
 */
@RunWith(Arquillian.class)
public class DLFileVersionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		setUpParentFolder();
		setUpResourcePermission();

		DDMStructure ddmStructure = DDMStructureTestUtil.addStructure(
			_group.getGroupId(), DLFileEntryMetadata.class.getName());

		_dlFileEntryType = DLFileEntryTypeServiceUtil.addFileEntryType(
			null, _group.getGroupId(), ddmStructure.getStructureId(), null,
			Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
			Collections.singletonMap(LocaleUtil.US, "New File Entry Type"),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		ExpandoTable expandoTable =
			ExpandoTableLocalServiceUtil.addDefaultTable(
				PortalUtil.getDefaultCompanyId(), DLFileEntry.class.getName());

		ExpandoColumnLocalServiceUtil.addColumn(
			expandoTable.getTableId(), _EXPANDO_ATTRIBUTE_NAME,
			ExpandoColumnConstants.STRING, StringPool.BLANK);

		_serviceContext = getServiceContext();

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, _group.getGroupId(), _parentFolder.getFolderId(),
			_SOURCE_FILE_NAME, ContentTypes.APPLICATION_OCTET_STREAM, _TITLE,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			_DATA_VERSION_1, null, null, null, _serviceContext);

		_fileVersion = DLFileVersionLocalServiceUtil.getFileVersion(
			fileEntry.getFileEntryId(), DLFileEntryConstants.VERSION_DEFAULT);
	}

	@After
	public void tearDown() throws Exception {
		ExpandoTable expandoTable =
			ExpandoTableLocalServiceUtil.getDefaultTable(
				PortalUtil.getDefaultCompanyId(), DLFileEntry.class.getName());

		ExpandoTableLocalServiceUtil.deleteTable(expandoTable);

		tearDownResourcePermission();
	}

	@Test
	public void testRevertVersion() throws Exception {
		DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_1, _fileVersion.getDisplayDate(),
			_fileVersion.getExpirationDate(), _fileVersion.getReviewDate(),
			_serviceContext);

		DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _UPDATE_VALUE, StringPool.BLANK,
			_fileVersion.getDescription(), _fileVersion.getChangeLog(),
			DLVersionNumberIncrease.MINOR, _DATA_VERSION_1,
			_fileVersion.getDisplayDate(), _fileVersion.getExpirationDate(),
			_fileVersion.getReviewDate(), _serviceContext);

		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_1, _fileVersion.getDisplayDate(),
			_fileVersion.getExpirationDate(), _fileVersion.getReviewDate(),
			_serviceContext);

		DLAppServiceUtil.revertFileEntry(
			fileEntry.getFileEntryId(), DLFileEntryConstants.VERSION_DEFAULT,
			_serviceContext);

		fileEntry = DLAppServiceUtil.getFileEntry(fileEntry.getFileEntryId());

		Assert.assertEquals("2.0", fileEntry.getVersion());
	}

	@Test
	public void testUpdateChecksum() throws Exception {
		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_2, _fileVersion.getDisplayDate(),
			_fileVersion.getExpirationDate(), _fileVersion.getReviewDate(),
			_serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	@Test
	public void testUpdateDescription() throws Exception {
		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _UPDATE_VALUE, _fileVersion.getChangeLog(),
			DLVersionNumberIncrease.MINOR, _DATA_VERSION_1,
			_fileVersion.getDisplayDate(), _fileVersion.getExpirationDate(),
			_fileVersion.getReviewDate(), _serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	@Test
	public void testUpdateDisplayDate() throws Exception {
		_setUp(
			_UPDATE_VALUE, _dlFileEntryType.getFileEntryTypeId(),
			StringPool.BLANK);

		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_1, new Date(), _fileVersion.getExpirationDate(),
			_fileVersion.getReviewDate(), _serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	@Test
	public void testUpdateExpando() throws Exception {
		_setUp(
			_UPDATE_VALUE, _dlFileEntryType.getFileEntryTypeId(),
			StringPool.BLANK);

		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_1, _fileVersion.getDisplayDate(),
			_fileVersion.getExpirationDate(), _fileVersion.getReviewDate(),
			_serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	@Test
	public void testUpdateExpirationDate() throws Exception {
		_setUp(
			_UPDATE_VALUE, _dlFileEntryType.getFileEntryTypeId(),
			StringPool.BLANK);

		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_1, _fileVersion.getDisplayDate(),
			new Date(System.currentTimeMillis() + Time.MINUTE),
			_fileVersion.getReviewDate(), _serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	@Test
	public void testUpdateFileEntryType() throws Exception {
		_setUp(
			StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			StringPool.BLANK);

		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_1, _fileVersion.getDisplayDate(),
			_fileVersion.getExpirationDate(), _fileVersion.getReviewDate(),
			_serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	@Test
	public void testUpdateMetadata() throws Exception {
		_setUp(
			StringPool.BLANK, _dlFileEntryType.getFileEntryTypeId(),
			_UPDATE_VALUE);

		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_1, _fileVersion.getDisplayDate(),
			_fileVersion.getExpirationDate(), _fileVersion.getReviewDate(),
			_serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	@Test
	public void testUpdateNothing() throws Exception {
		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_1, _fileVersion.getDisplayDate(),
			_fileVersion.getExpirationDate(), _fileVersion.getReviewDate(),
			_serviceContext);

		Assert.assertEquals("1.1", fileEntry.getVersion());
	}

	@Test
	public void testUpdateReviewDate() throws Exception {
		_setUp(
			_UPDATE_VALUE, _dlFileEntryType.getFileEntryTypeId(),
			StringPool.BLANK);

		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_1, _fileVersion.getDisplayDate(),
			_fileVersion.getExpirationDate(), new Date(), _serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	@Test
	public void testUpdateSize() throws Exception {
		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _fileVersion.getTitle(),
			StringPool.BLANK, _fileVersion.getDescription(),
			_fileVersion.getChangeLog(), DLVersionNumberIncrease.MINOR,
			_DATA_VERSION_3, _fileVersion.getDisplayDate(),
			_fileVersion.getExpirationDate(), _fileVersion.getReviewDate(),
			_serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	@Test
	public void testUpdateTitle() throws Exception {
		FileEntry fileEntry = DLAppServiceUtil.updateFileEntry(
			_fileVersion.getFileEntryId(), _SOURCE_FILE_NAME,
			_fileVersion.getMimeType(), _UPDATE_VALUE, StringPool.BLANK,
			_fileVersion.getDescription(), _fileVersion.getChangeLog(),
			DLVersionNumberIncrease.MINOR, _DATA_VERSION_1,
			_fileVersion.getDisplayDate(), _fileVersion.getExpirationDate(),
			_fileVersion.getReviewDate(), _serviceContext);

		Assert.assertNotEquals(
			DLFileEntryConstants.VERSION_DEFAULT, fileEntry.getVersion());
	}

	protected DDMFormFieldValue createDDMFormFieldValue(String name) {
		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setName(name);

		LocalizedValue localizedValue = new LocalizedValue(LocaleUtil.US);

		localizedValue.addString(LocaleUtil.US, StringPool.BLANK);

		ddmFormFieldValue.setValue(localizedValue);

		return ddmFormFieldValue;
	}

	protected DDMFormValues createDDMFormValues(DDMForm ddmForm) {
		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addAvailableLocale(LocaleUtil.US);
		ddmFormValues.setDefaultLocale(LocaleUtil.US);

		return ddmFormValues;
	}

	protected ServiceContext getServiceContext() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAttribute(
			"fileEntryTypeId", _dlFileEntryType.getFileEntryTypeId());

		Map<String, Serializable> expandoBridgeAttributes =
			serviceContext.getExpandoBridgeAttributes();

		expandoBridgeAttributes.put(_EXPANDO_ATTRIBUTE_NAME, StringPool.BLANK);

		serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			DLFileEntryTypeLocalServiceUtil.getFileEntryType(
				_dlFileEntryType.getFileEntryTypeId()));

		for (DDMStructure ddmStructure : ddmStructures) {
			DDMFormValues ddmFormValues = createDDMFormValues(
				_ddmBeanTranslator.translate(ddmStructure.getDDMForm()));

			for (String fieldName : ddmStructure.getFieldNames()) {
				DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(
					fieldName);

				ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
			}

			serviceContext.setAttribute(
				DDMFormValues.class.getName() + StringPool.POUND +
					ddmStructure.getStructureId(),
				ddmFormValues);
		}

		return serviceContext;
	}

	protected void setUpParentFolder() throws Exception {
		try {
			DLAppServiceUtil.deleteFolder(
				_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				"Test Folder");
		}
		catch (NoSuchFolderException noSuchFolderException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFolderException);
			}
		}

		_parentFolder = DLAppServiceUtil.addFolder(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, "Test Folder",
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	protected void setUpResourcePermission() throws Exception {
		RoleTestUtil.addResourcePermission(
			RoleConstants.GUEST, "com.liferay.document.library",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			ActionKeys.VIEW);
	}

	protected void tearDownResourcePermission() throws Exception {
		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, "com.liferay.document.library",
			ResourceConstants.SCOPE_GROUP, String.valueOf(_group.getGroupId()),
			ActionKeys.VIEW);
	}

	private void _setUp(String expando, long fileEntryTypeId, String metadata)
		throws Exception {

		_serviceContext = getServiceContext();

		Map<String, Serializable> expandoBridgeAttributes =
			_serviceContext.getExpandoBridgeAttributes();

		expandoBridgeAttributes.put(_EXPANDO_ATTRIBUTE_NAME, expando);

		_serviceContext.setExpandoBridgeAttributes(expandoBridgeAttributes);

		_serviceContext.setAttribute("fileEntryTypeId", fileEntryTypeId);

		if (fileEntryTypeId <= 0) {
			return;
		}

		List<DDMStructure> ddmStructures = DLFileEntryTypeUtil.getDDMStructures(
			DLFileEntryTypeLocalServiceUtil.getFileEntryType(fileEntryTypeId));

		for (DDMStructure ddmStructure : ddmStructures) {
			DDMFormValues ddmFormValues =
				(DDMFormValues)_serviceContext.getAttribute(
					DDMFormValues.class.getName() + StringPool.POUND +
						ddmStructure.getStructureId());

			for (DDMFormFieldValue ddmFormFieldValue :
					ddmFormValues.getDDMFormFieldValues()) {

				String fieldType = ddmStructure.getFieldType(
					ddmFormFieldValue.getName());

				if (!fieldType.equals("text")) {
					continue;
				}

				LocalizedValue localizedValue = new LocalizedValue(
					LocaleUtil.US);

				localizedValue.addString(LocaleUtil.US, metadata);

				ddmFormFieldValue.setValue(localizedValue);
			}

			_serviceContext.setAttribute(
				DDMFormValues.class.getName() + StringPool.POUND +
					ddmStructure.getStructureId(),
				ddmFormValues);
		}
	}

	private static final int _DATA_SIZE_1 = 512;

	private static final int _DATA_SIZE_2 = 1024;

	private static final byte[] _DATA_VERSION_1 = new byte[_DATA_SIZE_1];

	private static final byte[] _DATA_VERSION_2 = new byte[_DATA_SIZE_1];

	private static final byte[] _DATA_VERSION_3 = new byte[_DATA_SIZE_2];

	private static final String _EXPANDO_ATTRIBUTE_NAME = "Expando";

	private static final String _SOURCE_FILE_NAME = "SourceFileName.txt";

	private static final String _TITLE = "Title";

	private static final String _UPDATE_VALUE = "Update Value";

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileVersionTest.class);

	static {
		for (int i = 0; i < _DATA_SIZE_1; i++) {
			_DATA_VERSION_1[i] = (byte)i;
			_DATA_VERSION_2[i] = (byte)(i + 1);
		}

		for (int i = 0; i < _DATA_SIZE_2; i++) {
			_DATA_VERSION_3[i] = (byte)i;
		}
	}

	@Inject
	private DDMBeanTranslator _ddmBeanTranslator;

	@DeleteAfterTestRun
	private DLFileEntryType _dlFileEntryType;

	private DLFileVersion _fileVersion;

	@DeleteAfterTestRun
	private Group _group;

	private Folder _parentFolder;
	private ServiceContext _serviceContext;

}
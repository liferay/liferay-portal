/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalService;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.dynamic.data.mapping.constants.DDMStructureConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.data.cleanup.DLFileEntryDataCleanupPreupgradeProcess;

import java.sql.Connection;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author István András Dézsi
 */
@DataGuard(autoDelete = false, scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DLFileEntryDataCleanupPreupgradeProcessTest
	extends DLFileEntryDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@ClassRule
	@Rule
	public static final PermissionCheckerMethodTestRule
		permissionCheckerTestRule = PermissionCheckerMethodTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_classNames = _classNameLocalService.getClassNames(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		_systemEvents = _systemEventLocalService.getSystemEvents(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	@After
	public void tearDown() throws Exception {
		List<ClassName> classNames = ListUtil.remove(
			_classNameLocalService.getClassNames(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_classNames);

		for (ClassName className : classNames) {
			_classNameLocalService.deleteClassName(className);
		}

		List<SystemEvent> systemEvents = ListUtil.remove(
			_systemEventLocalService.getSystemEvents(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			_systemEvents);

		for (SystemEvent systemEvent : systemEvents) {
			_systemEventLocalService.deleteSystemEvent(systemEvent);
		}

		DataAccess.cleanUp(_connection);
	}

	@Test
	public void testUpgrade() throws Exception {
		long groupId = TestPropsValues.getGroupId();

		FileEntry fileEntry = _dlAppService.addFileEntry(
			null, groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.TEXT_PLAIN,
			StringUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, null, 0, null, null, null,
			ServiceContextTestUtil.getServiceContext(groupId));

		_dlFileShortcutLocalService.addFileShortcut(
			null, TestPropsValues.getUserId(), groupId, groupId,
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			fileEntry.getFileEntryId(),
			ServiceContextTestUtil.getServiceContext());

		FileVersion fileVersion = fileEntry.getFileVersion();

		_dlFileVersionPreviewLocalService.addDLFileVersionPreview(
			fileVersion.getFileEntryId(), fileVersion.getFileVersionId(),
			DLFileVersionPreviewConstants.STATUS_SUCCESS);

		DLFileEntryMetadata dlFileEntryMetadata =
			_dlFileEntryMetadataLocalService.createDLFileEntryMetadata(
				CounterLocalServiceUtil.increment());

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField =
			DDMFormTestUtil.createLocalizableTextDDMFormField(
				RandomTestUtil.randomString());

		DDMFormTestUtil.addDDMFormFields(ddmForm, ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMStructure ddmStructure = _ddmStructureLocalService.addStructure(
			null, TestPropsValues.getUserId(), groupId,
			DDMStructureConstants.DEFAULT_PARENT_STRUCTURE_ID,
			PortalUtil.getClassNameId(DLFileEntryMetadata.class),
			StringPool.BLANK,
			RandomTestUtil.randomLocaleStringMap(LocaleUtil.US),
			new HashMap<>(), StringPool.BLANK, StorageType.DEFAULT.toString(),
			ServiceContextTestUtil.getServiceContext());

		dlFileEntryMetadata.setDDMStorageId(
			_ddmStorageEngineManager.create(
				fileVersion.getCompanyId(), ddmStructure.getStructureId(),
				ddmFormValues, ServiceContextTestUtil.getServiceContext()));
		dlFileEntryMetadata.setDDMStructureId(ddmStructure.getStructureId());

		dlFileEntryMetadata.setFileEntryId(fileEntry.getFileEntryId());
		dlFileEntryMetadata.setFileVersionId(fileVersion.getFileVersionId());

		_dlFileEntryMetadataLocalService.addDLFileEntryMetadata(
			dlFileEntryMetadata);

		runSQL(
			"delete from DLFileEntry where fileEntryId = " +
				fileEntry.getFileEntryId());

		upgrade();

		DDMStructureVersion ddmStructureVersion =
			ddmStructure.getStructureVersion();

		_ddmFieldLocalService.deleteDDMFields(
			ddmStructureVersion.getStructureId());

		_ddmStructureLocalService.deleteStructure(ddmStructure);
	}

	@Test
	public void testUpgradeDeleteNullNameEntries() throws Exception {
		long fileEntryId1 = RandomTestUtil.nextLong();
		long fileEntryId2 = RandomTestUtil.nextLong();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DLFileEntryDataCleanupPreupgradeProcess.class.getName(),
				LoggerTestUtil.INFO)) {

			runSQL(
				StringBundler.concat(
					"insert into DLFileEntry (",
					"mvccVersion, ctCollectionId, fileEntryId, groupId) ",
					"values (0, 0, ", fileEntryId1, ", ",
					RandomTestUtil.nextLong(), ")"));
			runSQL(
				StringBundler.concat(
					"insert into DLFileEntry (",
					"mvccVersion, ctCollectionId, fileEntryId, groupId, name) ",
					"values (0, 0, ", fileEntryId2, ", ",
					RandomTestUtil.nextLong(), ", '')"));

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", _dbInspector.normalizeName("DLFileEntry"),
						", 1 row deleted because ",
						_dbInspector.normalizeName("fileEntryId"),
						StringPool.SPACE, fileEntryId1, StringPool.SPACE,
						_dbInspector.normalizeName("name"), " was null")));
			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", _dbInspector.normalizeName("DLFileEntry"),
						", 1 row deleted because ",
						_dbInspector.normalizeName("fileEntryId"),
						StringPool.SPACE, fileEntryId2, StringPool.SPACE,
						_dbInspector.normalizeName("name"), " was ",
						(DBManagerUtil.getDBType() == DBType.ORACLE) ? "null" :
							"empty")));
		}
		finally {
			runSQL(
				"delete from DLFileEntry where fileEntryId = " + fileEntryId1);
			runSQL(
				"delete from DLFileEntry where fileEntryId = " + fileEntryId2);
		}
	}

	private static List<ClassName> _classNames;
	private static List<SystemEvent> _systemEvents;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private Connection _connection;
	private DBInspector _dbInspector;

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Inject
	private DLFileShortcutLocalService _dlFileShortcutLocalService;

	@Inject
	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

}
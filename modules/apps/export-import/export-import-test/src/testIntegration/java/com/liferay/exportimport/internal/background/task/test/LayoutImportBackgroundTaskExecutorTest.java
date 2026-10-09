/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.background.task.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalService;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.staging.StagingGroupHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Szimko
 */
@RunWith(Arquillian.class)
public class LayoutImportBackgroundTaskExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetStatusCompletedWithErrors() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry[] objectEntries = _addObjectEntries(
			3, 0L, objectDefinition);

		File larFile = _exportLayouts(
			true, group.getGroupId(), false, new long[0], objectDefinition);

		_deleteObjectEntries(objectEntries);

		ObjectEntry objectEntry = objectEntries[1];

		Map<String, Serializable> values = objectEntry.getValues();

		_addObjectEntry(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, objectDefinition,
			values.get(_OBJECT_FIELD_NAME_TEXT));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.OFF)) {

			long backgroundTaskId =
				ExportImportLocalServiceUtil.importLayoutsInBackground(
					TestPropsValues.getUserId(),
					ExportImportConfigurationLocalServiceUtil.
						addExportImportConfiguration(
							TestPropsValues.getUserId(), group.getGroupId(),
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString(), 0,
							ExportImportConfigurationSettingsMapFactoryUtil.
								buildImportLayoutSettingsMap(
									TestPropsValues.getUser(),
									group.getGroupId(), false, new long[0],
									_getExportImportParameterMap(
										false, true,
										Arrays.asList(objectDefinition))),
							WorkflowConstants.STATUS_DRAFT,
							ServiceContextTestUtil.getServiceContext()),
					larFile);

			ExportImportTestUtil.retryAssert(
				1, TimeUnit.SECONDS, 5, TimeUnit.SECONDS,
				() -> {
					BackgroundTask backgroundTask =
						_backgroundTaskLocalService.getBackgroundTask(
							backgroundTaskId);

					Assert.assertEquals(
						BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
						backgroundTask.getStatus());
				});

			ServiceContextThreadLocal.popServiceContext();
		}
	}

	@Test
	public void testGetStatusCompletedWithErrorsWhenReportEntryTypeIsWarning()
		throws Exception {

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry[] objectEntries = _addObjectEntries(
			3, 0L, objectDefinition);

		File larFile = _exportLayouts(
			true, group.getGroupId(), false, new long[0], objectDefinition);

		_deleteObjectEntries(objectEntries);

		ExportImportConfiguration exportImportConfiguration =
			_addImportExportImportConfiguration(group, objectDefinition);

		_exportImportReportEntryLocalService.addExportImportReportEntry(
			group.getGroupId(), TestPropsValues.getCompanyId(), null,
			PortalUtil.getClassNameId(ExportImportConfiguration.class), 0,
			exportImportConfiguration.getExportImportConfigurationId(),
			ExportImportReportEntryConstants.TYPE_WARNING,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString());

		long backgroundTaskId =
			ExportImportLocalServiceUtil.importLayoutsInBackground(
				TestPropsValues.getUserId(), exportImportConfiguration,
				larFile);

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 5, TimeUnit.SECONDS,
			() -> {
				BackgroundTask backgroundTask =
					_backgroundTaskLocalService.getBackgroundTask(
						backgroundTaskId);

				Assert.assertEquals(
					BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
					backgroundTask.getStatus());
			});

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetStatusSuccessful() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext());

		UserTestUtil.setUser(TestPropsValues.getUser());

		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		ObjectDefinition objectDefinition = _addObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry[] objectEntries = _addObjectEntries(
			3, 0L, objectDefinition);

		File larFile = _exportLayouts(
			true, group.getGroupId(), false, new long[0], objectDefinition);

		_deleteObjectEntries(objectEntries);

		long backgroundTaskId =
			ExportImportLocalServiceUtil.importLayoutsInBackground(
				TestPropsValues.getUserId(),
				_addImportExportImportConfiguration(group, objectDefinition),
				larFile);

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 5, TimeUnit.SECONDS,
			() -> {
				BackgroundTask backgroundTask =
					_backgroundTaskLocalService.getBackgroundTask(
						backgroundTaskId);

				Assert.assertEquals(
					BackgroundTaskConstants.STATUS_SUCCESSFUL,
					backgroundTask.getStatus());
			});

		ServiceContextThreadLocal.popServiceContext();
	}

	private DLFileEntry _addDLFileEntry(byte[] content, long groupId)
		throws Exception {

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), groupId, 0,
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			new ByteArrayInputStream(content), 0, null, null, null,
			ServiceContextTestUtil.getServiceContext());

		return _dlFileEntryLocalService.getFileEntry(
			fileEntry.getFileEntryId());
	}

	private ExportImportConfiguration _addImportExportImportConfiguration(
			Group group, ObjectDefinition objectDefinition)
		throws Exception {

		return _exportImportConfigurationLocalService.
			addExportImportConfiguration(
				TestPropsValues.getUserId(), group.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0,
				ExportImportConfigurationSettingsMapFactoryUtil.
					buildImportLayoutSettingsMap(
						TestPropsValues.getUser(), group.getGroupId(), false,
						new long[0],
						_getExportImportParameterMap(
							false, true, Arrays.asList(objectDefinition))),
				WorkflowConstants.STATUS_DRAFT,
				ServiceContextTestUtil.getServiceContext());
	}

	private ObjectDefinition _addObjectDefinition(String scope)
		throws Exception {

		String objectDefinitionName = ObjectDefinitionTestUtil.getRandomName();

		return ObjectDefinitionTestUtil.publishObjectDefinition(
			objectDefinitionName,
			Arrays.asList(
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					RandomTestUtil.randomString(),
					_OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							"txt"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.VALUE_DOCS_AND_MEDIA
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							"100"
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					RandomTestUtil.randomString(),
					_OBJECT_FIELD_NAME_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							"txt"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.
								VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							"100"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_SHOW_FILES_IN_LIBRARY
						).value(
							Boolean.TRUE.toString()
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_STORAGE_DL_FOLDER_PATH
						).value(
							StringPool.SLASH + objectDefinitionName
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT,
					ObjectFieldConstants.DB_TYPE_LONG, true, false, null,
					RandomTestUtil.randomString(),
					_OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.
								NAME_ACCEPTED_FILE_EXTENSIONS
						).value(
							"txt"
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_FILE_SOURCE
						).value(
							ObjectFieldSettingConstants.
								VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
						).build(),
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
						).value(
							"100"
						).build()),
					false),
				ObjectFieldUtil.createObjectField(
					ObjectFieldConstants.BUSINESS_TYPE_TEXT,
					ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
					RandomTestUtil.randomString(), _OBJECT_FIELD_NAME_TEXT,
					Arrays.asList(
						new ObjectFieldSettingBuilder(
						).name(
							ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
						).value(
							Boolean.TRUE.toString()
						).build()),
					false)),
			scope);
	}

	private ObjectEntry[] _addObjectEntries(
			int count, long groupId, ObjectDefinition objectDefinition)
		throws Exception {

		ObjectEntry[] objectEntries = new ObjectEntry[count];

		for (int i = 0; i < count; i++) {
			objectEntries[i] = _addObjectEntry(
				groupId, objectDefinition, RandomTestUtil.randomString());
		}

		return objectEntries;
	}

	private ObjectEntry _addObjectEntry(
			long groupId, ObjectDefinition objectDefinition,
			Serializable objectFieldValue)
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		DLFileEntry dlFileEntry = _addDLFileEntry(
			DLTestUtil.randomTextFileBytes(), company.getGroupId());

		FileEntry tempFileEntry1 = _addTempFileEntry(
			DLTestUtil.randomTextFileBytes(), objectDefinition);
		FileEntry tempFileEntry2 = _addTempFileEntry(
			DLTestUtil.randomTextFileBytes(), objectDefinition);

		return _objectEntryLocalService.addObjectEntry(
			groupId, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA,
				dlFileEntry.getFileEntryId()
			).put(
				_OBJECT_FIELD_NAME_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA,
				tempFileEntry1.getFileEntryId()
			).put(
				_OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER,
				tempFileEntry2.getFileEntryId()
			).put(
				_OBJECT_FIELD_NAME_TEXT, objectFieldValue
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private FileEntry _addTempFileEntry(
			byte[] content, ObjectDefinition objectDefinition)
		throws Exception {

		return TempFileEntryUtil.addTempFileEntry(
			TestPropsValues.getGroupId(), TestPropsValues.getUserId(),
			objectDefinition.getPortletId(),
			TempFileEntryUtil.getTempFileName(
				RandomTestUtil.randomString() + ".txt"),
			FileUtil.createTempFile(content), ContentTypes.TEXT_PLAIN);
	}

	private void _deleteObjectEntries(ObjectEntry... objectEntries)
		throws Exception {

		for (ObjectEntry objectEntry : objectEntries) {
			_objectEntryLocalService.deleteObjectEntry(objectEntry);

			long fileEntryId = MapUtil.getLong(
				objectEntry.getValues(),
				_OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA);

			if (fileEntryId != 0) {
				_dlFileEntryLocalService.deleteFileEntry(fileEntryId);
			}

			fileEntryId = MapUtil.getLong(
				objectEntry.getValues(),
				_OBJECT_FIELD_NAME_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA);

			if (fileEntryId == 0) {
				continue;
			}

			_dlFileEntryLocalService.deleteFileEntry(fileEntryId);
		}
	}

	private File _exportLayouts(
			boolean deletions, long groupId,
			boolean includeLayoutSetLayoutsPortlet, boolean privateLayouts,
			long[] layoutIds, ObjectDefinition... objectDefinitions)
		throws Exception {

		return _exportImportLocalService.exportLayoutsAsFile(
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					ExportImportConfigurationSettingsMapFactoryUtil.
						buildExportLayoutSettingsMap(
							TestPropsValues.getUser(), groupId, privateLayouts,
							layoutIds,
							_getExportImportParameterMap(
								deletions, includeLayoutSetLayoutsPortlet,
								Arrays.asList(objectDefinitions)))));
	}

	private File _exportLayouts(
			boolean deletions, long groupId, boolean privateLayouts,
			long[] layoutIds, ObjectDefinition... objectDefinitions)
		throws Exception {

		return _exportLayouts(
			deletions, groupId, false, privateLayouts, layoutIds,
			objectDefinitions);
	}

	private Map<String, String[]> _getExportImportParameterMap(
		boolean deletions, boolean includeLayoutSetLayoutsPortlet,
		List<ObjectDefinition> objectDefinitions) {

		Map<String, String[]> parameterMap = HashMapBuilder.put(
			PortletDataHandlerKeys.DELETIONS,
			new String[] {Boolean.toString(deletions)}
		).put(
			PortletDataHandlerKeys.PERMISSIONS,
			new String[] {Boolean.FALSE.toString()}
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
			PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
			new String[] {Boolean.FALSE.toString()}
		).put(
			PortletDataHandlerKeys.PORTLET_SETUP_ALL,
			new String[] {Boolean.TRUE.toString()}
		).put(
			"PORTLET_DATA_com_liferay_layout_admin_web_portlet_" +
				"LayoutSetLayoutsPortlet",
			() -> {
				if (includeLayoutSetLayoutsPortlet) {
					return new String[] {Boolean.TRUE.toString()};
				}

				return null;
			}
		).build();

		objectDefinitions.forEach(
			objectDefinition -> parameterMap.put(
				PortletDataHandlerKeys.PORTLET_DATA + "_" +
					objectDefinition.getPortletId(),
				new String[] {Boolean.TRUE.toString()}));

		return parameterMap;
	}

	private static final String _OBJECT_FIELD_NAME_ATTACHMENT_DOCS_AND_MEDIA =
		"x" + RandomTestUtil.randomString();

	private static final String
		_OBJECT_FIELD_NAME_ATTACHMENT_SHOW_FILES_IN_DOCS_AND_MEDIA =
			"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_ATTACHMENT_USER_COMPUTER =
		"x" + RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_NAME_TEXT =
		"x" + RandomTestUtil.randomString();

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportLocalService _exportImportLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

}
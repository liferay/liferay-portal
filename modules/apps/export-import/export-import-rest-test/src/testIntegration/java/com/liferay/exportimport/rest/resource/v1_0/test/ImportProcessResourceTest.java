/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.lar.UserIdStrategy;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportLocalServiceUtil;
import com.liferay.exportimport.rest.client.dto.v1_0.ImportPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.ImportProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.ImportProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.client.dto.v1_0.RequestPortletDataHandler;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.ImportPreviewResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ImportProcessResource;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectDefinitionSettingLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.staging.StagingGroupHelper;

import java.io.File;
import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class ImportProcessResourceTest
	extends BaseImportProcessResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		_importPreviewResource = ImportPreviewResource.builder(
		).authentication(
			_adminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_importProcessResource = ImportProcessResource.builder(
		).authentication(
			_user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_userLocalService.deleteUser(_user);
	}

	@Override
	@Test
	public void testGetImportProcess() throws Exception {
		super.testGetImportProcess();

		_testGetImportProcessErrorMessageWhenStatusMessageIsNotJSON();
	}

	@Override
	@Test
	public void testGetImportProcessesPage() throws Exception {
		Page<ImportProcess> page = importProcessResource.getImportProcessesPage(
			null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		ImportProcess importProcess1 =
			testGetImportProcessesPage_addImportProcess(randomImportProcess());

		ImportProcess importProcess2 =
			testGetImportProcessesPage_addImportProcess(randomImportProcess());

		String portletId = RandomTestUtil.randomString();

		ImportProcess portletImportProcess = _addImportProcess(
			_getCompanyGroupId(), portletId,
			BackgroundTaskExecutorNames.
				PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR);

		page = importProcessResource.getImportProcessesPage(
			null, null, null, null, Pagination.of(1, (int)totalCount + 2),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(importProcess1, (List<ImportProcess>)page.getItems());
		assertContains(importProcess2, (List<ImportProcess>)page.getItems());
		assertValid(page, testGetImportProcessesPage_getExpectedActions());

		page = importProcessResource.getImportProcessesPage(
			null, portletId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(
			portletImportProcess, (List<ImportProcess>)page.getItems());

		page = importProcessResource.getImportProcessesPage(
			null, RandomTestUtil.randomString(), null, null,
			Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());

		importProcessResource.deleteImportProcess(importProcess1.getId());
		importProcessResource.deleteImportProcess(importProcess2.getId());
		importProcessResource.deleteImportProcess(portletImportProcess.getId());
	}

	@Override
	@Test
	public void testPostAssetLibraryImportProcess() throws Exception {
		String externalReferenceCode =
			testDepotEntryGroup.getExternalReferenceCode();

		assertHttpResponseStatusCode(
			403,
			_importProcessResource.postAssetLibraryImportProcessHttpResponse(
				externalReferenceCode, 0L, null, new ImportProcessRequest()));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_DEPOT);

		_testPostImportProcessWithObjectDefinition(
			() -> _exportLayoutAsFile(testDepotEntryGroup.getGroupId()),
			objectDefinition, testDepotEntryGroup.getGroupId(),
			file -> _importPreviewResource.postAssetLibraryImportPreview(
				externalReferenceCode, 0L, null, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest ->
				importProcessResource.postAssetLibraryImportProcess(
					externalReferenceCode, 0L, null, importProcessRequest));

		String portletId = objectDefinition.getPortletId();

		long plid = _addLayoutWithPortlet(testDepotEntryGroup, portletId);

		_testPostImportProcessWithObjectDefinition(
			() -> _exportPortletAsFile(
				testDepotEntryGroup.getGroupId(), plid, portletId),
			objectDefinition, testDepotEntryGroup.getGroupId(),
			file -> _importPreviewResource.postAssetLibraryImportPreview(
				externalReferenceCode, plid, portletId, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest ->
				importProcessResource.postAssetLibraryImportProcess(
					externalReferenceCode, plid, portletId,
					importProcessRequest));

		_testPostImportProcessWithoutPlid(
			importProcessRequest ->
				importProcessResource.postAssetLibraryImportProcessHttpResponse(
					externalReferenceCode, 0L, portletId,
					importProcessRequest));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Override
	@Test
	public void testPostImportProcess() throws Exception {
		assertHttpResponseStatusCode(
			403,
			_importProcessResource.postImportProcessHttpResponse(
				0L, null, new ImportProcessRequest()));

		Group companyGroup = _stagingGroupHelper.fetchCompanyGroup(
			testCompany.getCompanyId());

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		try {
			_testPostImportProcessWithObjectDefinition(
				() -> _exportLayoutAsFile(companyGroup.getGroupId()),
				objectDefinition, GroupConstants.DEFAULT_PARENT_GROUP_ID,
				file -> _importPreviewResource.postImportPreview(
					0L, null, null,
					HashMapBuilder.put(
						"file", file
					).build()),
				importProcessRequest -> importProcessResource.postImportProcess(
					0L, null, importProcessRequest));

			String portletId = objectDefinition.getPortletId();

			long plid = _addLayoutWithPortlet(testGroup, portletId);

			_testPostImportProcessWithObjectDefinition(
				() -> _exportPortletAsFile(
					companyGroup.getGroupId(), plid, portletId),
				objectDefinition, GroupConstants.DEFAULT_PARENT_GROUP_ID,
				file -> _importPreviewResource.postImportPreview(
					plid, portletId, null,
					HashMapBuilder.put(
						"file", file
					).build()),
				importProcessRequest -> importProcessResource.postImportProcess(
					plid, portletId, importProcessRequest));

			_testPostImportProcessWithoutPlid(
				importProcessRequest ->
					importProcessResource.postImportProcessHttpResponse(
						0L, portletId, importProcessRequest));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}

		_testPostImportProcessWithoutObjectDefinition(
			() -> _exportLayoutAsFile(companyGroup.getGroupId()),
			_publishObjectDefinition(ObjectDefinitionConstants.SCOPE_COMPANY),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			file -> _importPreviewResource.postImportPreview(
				0L, null, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest -> importProcessResource.postImportProcess(
				0L, null, importProcessRequest));

		_testPostImportProcessWithPreviewForOtherGroup(
			testGroup.getGroupId(),
			file -> _importPreviewResource.postSiteImportPreview(
				testGroup.getExternalReferenceCode(), 0L, null, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest ->
				importProcessResource.postImportProcessHttpResponse(
					0L, null, importProcessRequest));
		_testPostImportProcessWithSettings(
			companyGroup.getGroupId(),
			file -> _importPreviewResource.postImportPreview(
				0L, null, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest -> importProcessResource.postImportProcess(
				0L, null, importProcessRequest));
		_testPostImportProcessWithoutPreview(
			importProcessRequest ->
				importProcessResource.postImportProcessHttpResponse(
					0L, null, importProcessRequest));
	}

	@Override
	@Test
	public void testPostSiteImportProcess() throws Exception {
		String externalReferenceCode = testGroup.getExternalReferenceCode();

		assertHttpResponseStatusCode(
			403,
			_importProcessResource.postSiteImportProcessHttpResponse(
				externalReferenceCode, 0L, null, new ImportProcessRequest()));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		_testPostImportProcessWithObjectDefinition(
			() -> _exportLayoutAsFile(testGroup.getGroupId()), objectDefinition,
			testGroup.getGroupId(),
			file -> _importPreviewResource.postSiteImportPreview(
				externalReferenceCode, 0L, null, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest -> importProcessResource.postSiteImportProcess(
				externalReferenceCode, 0L, null, importProcessRequest));

		String portletId = objectDefinition.getPortletId();

		long plid = _addLayoutWithPortlet(testGroup, portletId);

		_testPostImportProcessWithObjectDefinition(
			() -> _exportPortletAsFile(testGroup.getGroupId(), plid, portletId),
			objectDefinition, testGroup.getGroupId(),
			file -> _importPreviewResource.postSiteImportPreview(
				externalReferenceCode, plid, portletId, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest -> importProcessResource.postSiteImportProcess(
				externalReferenceCode, plid, portletId, importProcessRequest));

		_testPostImportProcessWithoutPlid(
			importProcessRequest ->
				importProcessResource.postSiteImportProcessHttpResponse(
					externalReferenceCode, 0L, portletId,
					importProcessRequest));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_testPostImportProcessWithPreviewForOtherGroup(
			testDepotEntryGroup.getGroupId(),
			file -> _importPreviewResource.postAssetLibraryImportPreview(
				testDepotEntryGroup.getExternalReferenceCode(), 0L, null, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest ->
				importProcessResource.postSiteImportProcessHttpResponse(
					externalReferenceCode, 0L, null, importProcessRequest));
		_testPostImportProcessWithSettings(
			testGroup.getGroupId(),
			file -> _importPreviewResource.postSiteImportPreview(
				externalReferenceCode, 0L, null, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest -> importProcessResource.postSiteImportProcess(
				externalReferenceCode, 0L, null, importProcessRequest));
		_testPostImportProcessWithoutPreview(
			importProcessRequest ->
				importProcessResource.postSiteImportProcessHttpResponse(
					externalReferenceCode, 0L, null, importProcessRequest));
		_testPostImportProcessWithLayoutSet(
			file -> _importPreviewResource.postSiteImportPreview(
				externalReferenceCode, 0L, null, null,
				HashMapBuilder.put(
					"file", file
				).build()),
			importProcessRequest -> importProcessResource.postSiteImportProcess(
				externalReferenceCode, 0L, null, importProcessRequest));
	}

	@Override
	protected ImportProcess testBatchEngineDeleteImportTask_addImportProcess()
		throws Exception {

		return _addImportProcess(_getCompanyGroupId(), randomImportProcess());
	}

	@Override
	protected ImportProcess testDeleteImportProcess_addImportProcess()
		throws Exception {

		return _addImportProcess(_getCompanyGroupId(), randomImportProcess());
	}

	@Override
	protected ImportProcess testDeleteImportProcessBatch_addImportProcess()
		throws Exception {

		return _addImportProcess(_getCompanyGroupId(), randomImportProcess());
	}

	@Override
	protected ImportProcess
			testGetAssetLibraryImportProcessesPage_addImportProcess(
				String assetLibraryExternalReferenceCode,
				ImportProcess importProcess)
		throws Exception {

		return _addImportProcess(
			_getGroupId(assetLibraryExternalReferenceCode),
			randomImportProcess());
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetAssetLibraryImportProcessesPage_getExpectedActions(
				String assetLibraryExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected ImportProcess testGetImportProcess_addImportProcess()
		throws Exception {

		return _addImportProcess(_getCompanyGroupId(), randomImportProcess());
	}

	@Override
	protected ImportProcess testGetImportProcessesPage_addImportProcess(
			ImportProcess importProcess)
		throws Exception {

		return _addImportProcess(_getCompanyGroupId(), randomImportProcess());
	}

	@Override
	protected ProcessProgress testGetImportProcessProgress_addProcessProgress(
			long importProcessId, ProcessProgress processProgress)
		throws Exception {

		BackgroundTaskStatus backgroundTaskStatus =
			BackgroundTaskStatusRegistryUtil.registerBackgroundTaskStatus(
				importProcessId, null);

		backgroundTaskStatus.setAttribute(
			"allModelAdditionCountersTotal", 100L);
		backgroundTaskStatus.setAttribute(
			"currentModelAdditionCountersTotal", 50L);

		return new ProcessProgress() {
			{
				percentage = 50;
			}
		};
	}

	@Override
	protected ImportProcess testGetSiteImportProcessesPage_addImportProcess(
			String siteExternalReferenceCode, ImportProcess importProcess)
		throws Exception {

		return _addImportProcess(
			_getGroupId(siteExternalReferenceCode), randomImportProcess());
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteImportProcessesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	private ImportProcess _addImportProcess(
			long groupId, ImportProcess importProcess)
		throws Exception {

		return _addImportProcess(
			groupId, importProcess.getName(),
			BackgroundTaskExecutorNames.LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR);
	}

	private ImportProcess _addImportProcess(
			long groupId, String name, String taskExecutorClassName)
		throws Exception {

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.exportimport.internal.staging.StagingImpl",
				LoggerTestUtil.WARN);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.background.task.internal.messaging." +
					"BackgroundTaskMessageListener",
				LoggerTestUtil.WARN)) {

			BackgroundTask backgroundTask =
				_backgroundTaskLocalService.addBackgroundTask(
					TestPropsValues.getUserId(), groupId, name,
					taskExecutorClassName,
					HashMapBuilder.<String, Serializable>put(
						"exportImportConfigurationId",
						RandomTestUtil.randomLong()
					).build(),
					null);

			return new ImportProcess() {
				{
					setDateCreated(backgroundTask.getCreateDate());
					setDateModified(backgroundTask.getModifiedDate());
					setId(backgroundTask.getBackgroundTaskId());
					setName(backgroundTask.getName());
				}
			};
		}
	}

	private long _addLayoutWithPortlet(Group group, String portletId)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(layout, portletId);

		return layout.getPlid();
	}

	private void _deleteTempFileEntries(long groupId) throws Exception {
		String folderName = DigesterUtil.digestHex(
			DigesterUtil.SHA_256,
			"com.liferay.exportimport.rest.resource.v1_0." +
				"ImportPreviewResource");

		long userId = _adminUser.getUserId();

		for (String tempFileName :
				TempFileEntryUtil.getTempFileNames(
					groupId, userId, folderName)) {

			TempFileEntryUtil.deleteTempFileEntry(
				groupId, userId, folderName, tempFileName);
		}
	}

	private File _exportLayoutAsFile(long groupId) throws Exception {
		return _exportLayoutAsFile(groupId, false, null);
	}

	private File _exportLayoutAsFile(
			long groupId, boolean privateLayout, long[] layoutIds)
		throws Exception {

		Map<String, Serializable> parameterMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportLayoutSettingsMap(
					TestPropsValues.getUser(), groupId, privateLayout,
					layoutIds,
					HashMapBuilder.put(
						PortletDataHandlerKeys.PORTLET_DATA,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA_ALL,
						new String[] {Boolean.TRUE.toString()}
					).build());

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addExportImportConfiguration(
					TestPropsValues.getUserId(), groupId,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
					parameterMap, new ServiceContext());

		return ExportImportLocalServiceUtil.exportLayoutsAsFile(
			exportImportConfiguration);
	}

	private File _exportPortletAsFile(long groupId, long plid, String portletId)
		throws Exception {

		Map<String, Serializable> parameterMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportPortletSettingsMap(
					TestPropsValues.getUser(), plid, groupId, portletId,
					HashMapBuilder.put(
						PortletDataHandlerKeys.PORTLET_DATA,
						new String[] {Boolean.TRUE.toString()}
					).put(
						PortletDataHandlerKeys.PORTLET_DATA_ALL,
						new String[] {Boolean.TRUE.toString()}
					).build(),
					StringPool.BLANK);

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				addExportImportConfiguration(
					TestPropsValues.getUserId(), groupId,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
					parameterMap, new ServiceContext());

		return ExportImportLocalServiceUtil.exportPortletInfoAsFile(
			exportImportConfiguration);
	}

	private long _getCompanyGroupId() throws Exception {
		Group group = _stagingGroupHelper.fetchCompanyGroup(
			TestPropsValues.getCompanyId());

		return group.getGroupId();
	}

	private long _getGroupId(String externalReferenceCode) throws Exception {
		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			externalReferenceCode, TestPropsValues.getCompanyId());

		return group.getGroupId();
	}

	private void _importLayoutSet(
			UnsafeFunction<File, ImportPreview, Exception>
				postImportPreviewUnsafeFunction,
			UnsafeFunction<ImportProcessRequest, ImportProcess, Exception>
				postImportProcessUnsafeFunction,
			boolean privateLayout)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypeContentLayout(
			testGroup, privateLayout, false);

		File file = _exportLayoutAsFile(
			testGroup.getGroupId(), privateLayout,
			new long[] {layout.getLayoutId()});

		_layoutLocalService.deleteLayout(
			layout, ServiceContextTestUtil.getServiceContext());

		postImportPreviewUnsafeFunction.apply(file);

		ImportProcess importProcess = postImportProcessUnsafeFunction.apply(
			new ImportProcessRequest());

		assertValid(importProcess);

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 30, TimeUnit.SECONDS,
			() -> {
				BackgroundTask backgroundTask =
					_backgroundTaskLocalService.getBackgroundTask(
						importProcess.getId());

				Assert.assertEquals(
					BackgroundTaskConstants.STATUS_SUCCESSFUL,
					backgroundTask.getStatus());
			});

		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				layout.getUuid(), testGroup.getGroupId(), privateLayout));
	}

	private ObjectDefinition _publishObjectDefinition(String scope)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName(),
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING, true, true, null,
						RandomTestUtil.randomString(), "textField", false)),
				scope);

		if (Objects.equals(scope, ObjectDefinitionConstants.SCOPE_DEPOT)) {
			_objectDefinitionSettingLocalService.addObjectDefinitionSetting(
				objectDefinition.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS,
				StringPool.TRUE);
		}

		return objectDefinition;
	}

	@TestInfo("LPD-102315")
	private void _testGetImportProcessErrorMessageWhenStatusMessageIsNotJSON()
		throws Exception {

		ImportProcess importProcess = _addImportProcess(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			BackgroundTaskExecutorNames.LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR);

		_backgroundTaskLocalService.amendBackgroundTask(
			importProcess.getId(), null, null,
			BackgroundTaskConstants.STATUS_FAILED, _STATUS_MESSAGE,
			null);

		ImportProcess failedImportProcess =
			importProcessResource.getImportProcess(importProcess.getId());

		String errorMessage = failedImportProcess.getErrorMessage();

		Assert.assertNotEquals(_STATUS_MESSAGE, errorMessage);
		Assert.assertFalse(errorMessage, errorMessage.contains(".java:"));
		Assert.assertFalse(errorMessage, errorMessage.contains("\tat "));
		Assert.assertFalse(errorMessage, errorMessage.contains("java.lang."));
	}

	private void _testPostImportProcessWithLayoutSet(
			UnsafeFunction<File, ImportPreview, Exception>
				postImportPreviewUnsafeFunction,
			UnsafeFunction<ImportProcessRequest, ImportProcess, Exception>
				postImportProcessUnsafeFunction)
		throws Exception {

		_importLayoutSet(
			postImportPreviewUnsafeFunction, postImportProcessUnsafeFunction,
			false);
		_importLayoutSet(
			postImportPreviewUnsafeFunction, postImportProcessUnsafeFunction,
			true);
	}

	@TestInfo("LPD-45048")
	private void _testPostImportProcessWithObjectDefinition(
			UnsafeSupplier<File, Exception> exportFileUnsafeSupplier,
			ObjectDefinition objectDefinition, long objectEntryGroupId,
			UnsafeFunction<File, ImportPreview, Exception>
				postImportPreviewUnsafeFunction,
			UnsafeFunction<ImportProcessRequest, ImportProcess, Exception>
				postImportProcessUnsafeFunction)
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectEntryGroupId, objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"textField", RandomTestUtil.randomString()
			).build());

		File file = exportFileUnsafeSupplier.get();

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());

		postImportPreviewUnsafeFunction.apply(file);

		ImportProcessRequest importProcessRequest = new ImportProcessRequest();

		importProcessRequest.setRequestPortletDataHandlers(
			new RequestPortletDataHandler[] {
				new RequestPortletDataHandler() {
					{
						name =
							"PORTLET_DATA_" + objectDefinition.getPortletId();
					}
				}
			});

		ImportProcess importProcess = postImportProcessUnsafeFunction.apply(
			importProcessRequest);

		assertValid(importProcess);

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 30, TimeUnit.SECONDS,
			() -> {
				BackgroundTask backgroundTask =
					_backgroundTaskLocalService.getBackgroundTask(
						importProcess.getId());

				Assert.assertEquals(
					BackgroundTaskConstants.STATUS_SUCCESSFUL,
					backgroundTask.getStatus());
			});

		Assert.assertNotNull(
			_objectEntryLocalService.fetchObjectEntry(
				objectEntry.getExternalReferenceCode(), objectEntryGroupId,
				objectDefinition.getObjectDefinitionId()));
	}

	@TestInfo("LPD-76327")
	private void _testPostImportProcessWithoutObjectDefinition(
			UnsafeSupplier<File, Exception> exportFileUnsafeSupplier,
			ObjectDefinition objectDefinition, long objectEntryGroupId,
			UnsafeFunction<File, ImportPreview, Exception>
				postImportPreviewUnsafeFunction,
			UnsafeFunction<ImportProcessRequest, ImportProcess, Exception>
				postImportProcessUnsafeFunction)
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectEntryGroupId, objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"textField", RandomTestUtil.randomString()
			).build());

		File file = exportFileUnsafeSupplier.get();

		String portletId = objectDefinition.getPortletId();

		_objectEntryLocalService.deleteObjectEntry(
			objectEntry.getObjectEntryId());

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		postImportPreviewUnsafeFunction.apply(file);

		ImportProcessRequest importProcessRequest = new ImportProcessRequest();

		importProcessRequest.setRequestPortletDataHandlers(
			new RequestPortletDataHandler[] {
				new RequestPortletDataHandler() {
					{
						name = "PORTLET_DATA_" + portletId;
					}
				}
			});

		ImportProcess importProcess = postImportProcessUnsafeFunction.apply(
			importProcessRequest);

		assertValid(importProcess);

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 30, TimeUnit.SECONDS,
			() -> {
				BackgroundTask backgroundTask =
					_backgroundTaskLocalService.getBackgroundTask(
						importProcess.getId());

				Assert.assertEquals(
					BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
					backgroundTask.getStatus());
			});
	}

	private void _testPostImportProcessWithoutPlid(
			UnsafeFunction
				<ImportProcessRequest, HttpInvoker.HttpResponse, Exception>
					unsafeFunction)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400, unsafeFunction.apply(new ImportProcessRequest()));
		}
	}

	private void _testPostImportProcessWithoutPreview(
			UnsafeFunction
				<ImportProcessRequest, HttpInvoker.HttpResponse, Exception>
					unsafeFunction)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				404, unsafeFunction.apply(new ImportProcessRequest()));
		}
	}

	private void _testPostImportProcessWithPreviewForOtherGroup(
			long exportImportGroupId,
			UnsafeFunction<File, ImportPreview, Exception>
				postImportPreviewUnsafeFunction,
			UnsafeFunction
				<ImportProcessRequest, HttpInvoker.HttpResponse, Exception>
					postImportProcessUnsafeFunction)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			try {
				postImportPreviewUnsafeFunction.apply(
					_exportLayoutAsFile(exportImportGroupId));

				assertHttpResponseStatusCode(
					404,
					postImportProcessUnsafeFunction.apply(
						new ImportProcessRequest()));
			}
			finally {
				_deleteTempFileEntries(exportImportGroupId);
			}
		}
	}

	private void _testPostImportProcessWithSettings(
			long exportImportGroupId,
			UnsafeFunction<File, ImportPreview, Exception>
				postImportPreviewUnsafeFunction,
			UnsafeFunction<ImportProcessRequest, ImportProcess, Exception>
				postImportProcessUnsafeFunction)
		throws Exception {

		File file = _exportLayoutAsFile(exportImportGroupId);

		postImportPreviewUnsafeFunction.apply(file);

		ImportProcessRequest importProcessRequest = new ImportProcessRequest() {
			{
				dataStrategy = DataStrategy.COPY_AS_NEW;
				deletions = true;
				permissions = true;
				userIdStrategy = UserIdStrategy.ALWAYS_CURRENT_USER_ID;
			}
		};

		ImportProcess importProcess = postImportProcessUnsafeFunction.apply(
			importProcessRequest);

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				importProcess.getId());

		long exportImportConfigurationId = MapUtil.getLong(
			backgroundTask.getTaskContextMap(), "exportImportConfigurationId");

		ExportImportConfiguration exportImportConfiguration =
			ExportImportConfigurationLocalServiceUtil.
				getExportImportConfiguration(exportImportConfigurationId);

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		Assert.assertEquals(
			PortletDataHandlerKeys.DATA_STRATEGY_COPY_AS_NEW,
			MapUtil.getString(
				parameterMap, PortletDataHandlerKeys.DATA_STRATEGY));
		Assert.assertTrue(
			MapUtil.getBoolean(parameterMap, PortletDataHandlerKeys.DELETIONS));
		Assert.assertTrue(
			MapUtil.getBoolean(
				parameterMap, PortletDataHandlerKeys.PERMISSIONS));
		Assert.assertEquals(
			UserIdStrategy.ALWAYS_CURRENT_USER_ID,
			MapUtil.getString(
				parameterMap, PortletDataHandlerKeys.USER_ID_STRATEGY));
	}

	private static final String _STATUS_MESSAGE =
		"java.lang.NullPointerException\n\tat com.liferay.exportimport." +
			"internal.controller.LayoutImportController.importFile(" +
				"LayoutImportController.java:181)";

	private User _adminUser;

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	private ImportPreviewResource _importPreviewResource;
	private ImportProcessResource _importProcessResource;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}
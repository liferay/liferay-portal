/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.client.dto.v1_0.RequestPortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.RequestPortletDataHandlerControl;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportProcessResource;
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
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;
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

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class ExportProcessResourceTest
	extends BaseExportProcessResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_exportProcessResource = ExportProcessResource.builder(
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
	@TestInfo("LPS-88498")
	public void testGetExportProcessContent() throws Exception {
		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		ExportProcessRequest exportProcessRequest = new ExportProcessRequest();

		exportProcessRequest.setName(RandomTestUtil.randomString());
		exportProcessRequest.setRequestPortletDataHandlers(
			new RequestPortletDataHandler[] {
				new RequestPortletDataHandler() {
					{
						name =
							"PORTLET_DATA_" + objectDefinition.getPortletId();
					}
				}
			});

		ExportProcess exportProcess = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineExportTaskExecutorImpl",
				LoggerTestUtil.WARN)) {

			exportProcess = exportProcessResource.postSiteExportProcess(
				testGroup.getExternalReferenceCode(), 0L, null,
				exportProcessRequest);

			ExportProcess finalExportProcess = exportProcess;

			ExportImportTestUtil.retryAssert(
				1, TimeUnit.SECONDS, 30, TimeUnit.SECONDS,
				() -> {
					BackgroundTask backgroundTask =
						_backgroundTaskLocalService.getBackgroundTask(
							finalExportProcess.getId());

					Assert.assertEquals(
						BackgroundTaskConstants.STATUS_SUCCESSFUL,
						backgroundTask.getStatus());
				});
		}

		HttpInvoker.HttpResponse httpResponse =
			exportProcessResource.getExportProcessContentHttpResponse(
				exportProcess.getId());

		assertHttpResponseStatusCode(200, httpResponse);

		Assert.assertNotNull(httpResponse.getContent());

		assertHttpResponseStatusCode(
			404,
			_exportProcessResource.getExportProcessContentHttpResponse(
				exportProcess.getId()));

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				exportProcess.getId());

		List<FileEntry> fileEntries =
			backgroundTask.getAttachmentsFileEntries();

		FileEntry fileEntry = fileEntries.get(0);

		Assert.assertEquals(
			exportProcess.getName() + ".lar", fileEntry.getTitle());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Override
	@Test
	public void testGetExportProcessesPage() throws Exception {
		Page<ExportProcess> page = exportProcessResource.getExportProcessesPage(
			null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		ExportProcess exportProcess1 =
			testGetExportProcessesPage_addExportProcess(randomExportProcess());

		ExportProcess exportProcess2 =
			testGetExportProcessesPage_addExportProcess(randomExportProcess());

		String portletId = RandomTestUtil.randomString();

		ExportProcess portletExportProcess = _addExportProcess(
			_getCompanyGroupId(), portletId,
			BackgroundTaskExecutorNames.
				PORTLET_EXPORT_BACKGROUND_TASK_EXECUTOR);

		page = exportProcessResource.getExportProcessesPage(
			null, null, null, null, Pagination.of(1, (int)totalCount + 2),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(exportProcess1, (List<ExportProcess>)page.getItems());
		assertContains(exportProcess2, (List<ExportProcess>)page.getItems());
		assertValid(page, testGetExportProcessesPage_getExpectedActions());

		page = exportProcessResource.getExportProcessesPage(
			null, portletId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(
			portletExportProcess, (List<ExportProcess>)page.getItems());

		page = exportProcessResource.getExportProcessesPage(
			null, RandomTestUtil.randomString(), null, null,
			Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());

		exportProcessResource.deleteExportProcess(exportProcess1.getId());
		exportProcessResource.deleteExportProcess(exportProcess2.getId());
		exportProcessResource.deleteExportProcess(portletExportProcess.getId());
	}

	@Override
	@Test
	public void testPostAssetLibraryExportProcess() throws Exception {
		String externalReferenceCode =
			testDepotEntryGroup.getExternalReferenceCode();

		assertHttpResponseStatusCode(
			403,
			_exportProcessResource.postAssetLibraryExportProcessHttpResponse(
				externalReferenceCode, 0L, null,
				new ExportProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));

		_testPostExportProcessWithInvalidDateRange(
			exportProcessRequest ->
				exportProcessResource.postAssetLibraryExportProcessHttpResponse(
					externalReferenceCode, 0L, null, exportProcessRequest));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_DEPOT);

		ObjectEntry[] objectEntries = _addObjectEntries(
			objectDefinition, testDepotEntryGroup.getGroupId());

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest ->
				exportProcessResource.postAssetLibraryExportProcess(
					externalReferenceCode, 0L, null, exportProcessRequest),
			testDepotEntryGroup.getGroupId(), objectDefinition, objectEntries);

		String portletId = objectDefinition.getPortletId();

		long plid = _addLayoutWithPortlet(testDepotEntryGroup, portletId);

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest ->
				exportProcessResource.postAssetLibraryExportProcess(
					externalReferenceCode, plid, portletId,
					exportProcessRequest),
			testDepotEntryGroup.getGroupId(), objectDefinition, objectEntries);

		_testPostExportProcessWithoutPlid(
			exportProcessRequest ->
				exportProcessResource.postAssetLibraryExportProcessHttpResponse(
					externalReferenceCode, 0L, portletId,
					exportProcessRequest));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Override
	@Test
	public void testPostExportProcess() throws Exception {
		assertHttpResponseStatusCode(
			403,
			_exportProcessResource.postExportProcessHttpResponse(
				0L, null,
				new ExportProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));

		Group companyGroup = _stagingGroupHelper.fetchCompanyGroup(
			testCompany.getCompanyId());

		_testPostExportProcessWithInvalidDateRange(
			exportProcessRequest ->
				exportProcessResource.postExportProcessHttpResponse(
					0L, null, exportProcessRequest));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry[] objectEntries = _addObjectEntries(
			objectDefinition, GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest -> exportProcessResource.postExportProcess(
				0L, null, exportProcessRequest),
			companyGroup.getGroupId(), objectDefinition, objectEntries);
		_testPostExportProcessWithDateRange(
			companyGroup.getGroupId(), objectDefinition, objectEntries);
		_testPostExportProcessWithPermissions(
			companyGroup.getGroupId(), objectDefinition, objectEntries);

		_testPostExportProcessWithSameName(companyGroup.getGroupId());

		String portletId = objectDefinition.getPortletId();

		long plid = _addLayoutWithPortlet(testGroup, portletId);

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest -> exportProcessResource.postExportProcess(
				plid, portletId, exportProcessRequest),
			companyGroup.getGroupId(), objectDefinition, objectEntries);

		_testPostExportProcessWithoutPlid(
			exportProcessRequest ->
				exportProcessResource.postExportProcessHttpResponse(
					0L, portletId, exportProcessRequest));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Override
	@Test
	@TestInfo("LRQA-47649")
	public void testPostExportProcessRelaunch() throws Exception {
		super.testPostExportProcessRelaunch();

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		ExportProcessRequest exportProcessRequest = new ExportProcessRequest();

		exportProcessRequest.setName(RandomTestUtil.randomString());
		exportProcessRequest.setRequestPortletDataHandlers(
			new RequestPortletDataHandler[] {
				new RequestPortletDataHandler() {
					{
						name =
							"PORTLET_DATA_" + objectDefinition.getPortletId();
					}
				}
			});

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineExportTaskExecutorImpl",
				LoggerTestUtil.WARN)) {

			ExportProcess exportProcess =
				exportProcessResource.postSiteExportProcess(
					testGroup.getExternalReferenceCode(), 0L, null,
					exportProcessRequest);

			ExportImportTestUtil.assertBackgroundTaskSuccessful(
				exportProcess.getId());

			ExportProcess relaunchedExportProcess =
				exportProcessResource.postExportProcessRelaunch(
					exportProcess.getId());

			Assert.assertNotEquals(
				exportProcess.getId(), relaunchedExportProcess.getId());

			ExportImportTestUtil.assertBackgroundTaskSuccessful(
				relaunchedExportProcess.getId());

			BackgroundTask relaunchedBackgroundTask =
				_backgroundTaskLocalService.getBackgroundTask(
					relaunchedExportProcess.getId());

			Assert.assertFalse(
				ListUtil.isEmpty(
					relaunchedBackgroundTask.getAttachmentsFileEntries()));

			BackgroundTask backgroundTask =
				_backgroundTaskLocalService.getBackgroundTask(
					exportProcess.getId());

			Assert.assertEquals(
				BackgroundTaskConstants.STATUS_SUCCESSFUL,
				backgroundTask.getStatus());
		}

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@FeatureFlag("LPD-38869")
	@Override
	@Test
	public void testPostSiteExportProcess() throws Exception {
		String externalReferenceCode = testGroup.getExternalReferenceCode();

		assertHttpResponseStatusCode(
			403,
			_exportProcessResource.postSiteExportProcessHttpResponse(
				externalReferenceCode, 0L, null,
				new ExportProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));

		_testPostExportProcessWithInvalidDateRange(
			exportProcessRequest ->
				exportProcessResource.postSiteExportProcessHttpResponse(
					externalReferenceCode, 0L, null, exportProcessRequest));
		_testPostExportProcessWithLayoutSet(
			exportProcessRequest ->
				exportProcessResource.postSiteExportProcessHttpResponse(
					externalReferenceCode, 0L, null, exportProcessRequest),
			exportProcessRequest -> exportProcessResource.postSiteExportProcess(
				externalReferenceCode, 0L, null, exportProcessRequest));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		ObjectEntry[] objectEntries = _addObjectEntries(
			objectDefinition, testGroup.getGroupId());

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest -> exportProcessResource.postSiteExportProcess(
				externalReferenceCode, 0L, null, exportProcessRequest),
			testGroup.getGroupId(), objectDefinition, objectEntries);

		String portletId = objectDefinition.getPortletId();

		long plid = _addLayoutWithPortlet(testGroup, portletId);

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest -> exportProcessResource.postSiteExportProcess(
				externalReferenceCode, plid, portletId, exportProcessRequest),
			testGroup.getGroupId(), objectDefinition, objectEntries);

		_testPostExportProcessWithoutPlid(
			exportProcessRequest ->
				exportProcessResource.postSiteExportProcessHttpResponse(
					externalReferenceCode, 0L, portletId,
					exportProcessRequest));

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@Override
	protected ExportProcess testBatchEngineDeleteImportTask_addExportProcess()
		throws Exception {

		return _addExportProcess(randomExportProcess(), _getCompanyGroupId());
	}

	@Override
	protected ExportProcess testDeleteExportProcess_addExportProcess()
		throws Exception {

		return _addExportProcess(randomExportProcess(), _getCompanyGroupId());
	}

	@Override
	protected ExportProcess testDeleteExportProcessBatch_addExportProcess()
		throws Exception {

		return _addExportProcess(randomExportProcess(), _getCompanyGroupId());
	}

	@Override
	protected ExportProcess
			testGetAssetLibraryExportProcessesPage_addExportProcess(
				String assetLibraryExternalReferenceCode,
				ExportProcess exportProcess)
		throws Exception {

		return _addExportProcess(
			randomExportProcess(),
			_getGroupId(assetLibraryExternalReferenceCode));
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetAssetLibraryExportProcessesPage_getExpectedActions(
				String assetLibraryExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected ExportProcess testGetExportProcess_addExportProcess()
		throws Exception {

		return _addExportProcess(randomExportProcess(), _getCompanyGroupId());
	}

	@Override
	protected ExportProcess testGetExportProcessesPage_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		return _addExportProcess(randomExportProcess(), _getCompanyGroupId());
	}

	@Override
	protected ProcessProgress testGetExportProcessProgress_addProcessProgress(
			long exportProcessId, ProcessProgress processProgress)
		throws Exception {

		BackgroundTaskStatus backgroundTaskStatus =
			BackgroundTaskStatusRegistryUtil.registerBackgroundTaskStatus(
				exportProcessId, null);

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
	protected ExportProcess testGetSiteExportProcessesPage_addExportProcess(
			String siteExternalReferenceCode, ExportProcess exportProcess)
		throws Exception {

		return _addExportProcess(
			randomExportProcess(), _getGroupId(siteExternalReferenceCode));
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSiteExportProcessesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected ExportProcess testPostExportProcessRelaunch_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		return _addExportProcess(randomExportProcess(), _getCompanyGroupId());
	}

	private ExportProcess _addExportProcess(
			ExportProcess exportProcess, long groupId)
		throws Exception {

		return _addExportProcess(
			groupId, exportProcess.getName(),
			BackgroundTaskExecutorNames.LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR);
	}

	private ExportProcess _addExportProcess(
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

			return new ExportProcess() {
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

	private ObjectEntry[] _addObjectEntries(
			ObjectDefinition objectDefinition, long groupId)
		throws Exception {

		return new ObjectEntry[] {
			_addObjectEntry(objectDefinition, groupId),
			_addObjectEntry(objectDefinition, groupId)
		};
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, long groupId)
		throws Exception {

		return ObjectEntryTestUtil.addObjectEntry(
			groupId, objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"textField", RandomTestUtil.randomString()
			).build());
	}

	private <T> void _assertExportedExternalReferenceCodes(
			BackgroundTask backgroundTask, String fileNamePrefix, long groupId,
			T[] items, UnsafeFunction<T, String, Exception> unsafeFunction)
		throws Exception {

		List<FileEntry> fileEntries =
			backgroundTask.getAttachmentsFileEntries();

		Assert.assertEquals(fileEntries.toString(), 1, fileEntries.size());

		FileEntry larFileEntry = fileEntries.get(0);

		JSONAssert.assertEquals(
			JSONUtil.toJSONArray(
				items,
				item -> JSONUtil.put(
					"externalReferenceCode", unsafeFunction.apply(item))
			).toString(),
			String.valueOf(
				ExportImportTestUtil.getExportedJSONArray(
					fileNamePrefix, groupId, larFileEntry.getContentStream())),
			JSONCompareMode.LENIENT);
	}

	private void _assertExportedLayouts(
			String controlName,
			UnsafeFunction<ExportProcessRequest, ExportProcess, Exception>
				unsafeFunction,
			Layout... layouts)
		throws Exception {

		ExportProcess exportProcess = unsafeFunction.apply(
			new ExportProcessRequest() {
				{
					name = RandomTestUtil.randomString();

					setRequestPortletDataHandlers(
						new RequestPortletDataHandler[] {
							new RequestPortletDataHandler() {
								{
									name =
										"PORTLET_DATA_" + _LAYOUT_SET_LAYOUTS;

									setRequestPortletDataHandlerControls(
										new RequestPortletDataHandlerControl[] {
											new RequestPortletDataHandlerControl() {
												{
													name = controlName;
												}
											}
										});
								}
							}
						});
				}
			});

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 30, TimeUnit.SECONDS,
			() -> {
				BackgroundTask backgroundTask =
					_backgroundTaskLocalService.getBackgroundTask(
						exportProcess.getId());

				Assert.assertEquals(
					BackgroundTaskConstants.STATUS_SUCCESSFUL,
					backgroundTask.getStatus());
			});

		_assertExportedExternalReferenceCodes(
			_backgroundTaskLocalService.getBackgroundTask(
				exportProcess.getId()),
			"com.liferay.headless.admin.site.internal.resource.v1_0." +
				"SitePageResourceImpl",
			testGroup.getGroupId(), layouts, Layout::getExternalReferenceCode);
	}

	private long _getCompanyGroupId() throws Exception {
		Group group = _stagingGroupHelper.fetchCompanyGroup(
			testCompany.getCompanyId());

		return group.getGroupId();
	}

	private JSONArray _getExportedJSONArray(
			ExportProcess exportProcess, long groupId,
			ObjectDefinition objectDefinition)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				exportProcess.getId());

		List<FileEntry> fileEntries =
			backgroundTask.getAttachmentsFileEntries();

		Assert.assertEquals(fileEntries.toString(), 1, fileEntries.size());

		FileEntry larFileEntry = fileEntries.get(0);

		return ExportImportTestUtil.getExportedJSONArray(
			objectDefinition.getExternalReferenceCode(), groupId,
			larFileEntry.getContentStream());
	}

	private long _getGroupId(String externalReferenceCode) throws Exception {
		Group group = _groupLocalService.getGroupByExternalReferenceCode(
			externalReferenceCode, testCompany.getCompanyId());

		return group.getGroupId();
	}

	private ExportProcess _postExportProcess(
			UnsafeFunction<ExportProcessRequest, ExportProcess, Exception>
				unsafeFunction,
			ObjectDefinition objectDefinition,
			UnsafeConsumer<ExportProcessRequest, Exception> unsafeConsumer)
		throws Exception {

		ExportProcessRequest exportProcessRequest = new ExportProcessRequest();

		exportProcessRequest.setName(RandomTestUtil.randomString());
		exportProcessRequest.setRequestPortletDataHandlers(
			new RequestPortletDataHandler[] {
				new RequestPortletDataHandler() {
					{
						name =
							"PORTLET_DATA_" + objectDefinition.getPortletId();
					}
				}
			});

		unsafeConsumer.accept(exportProcessRequest);

		ExportProcess exportProcess = null;

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineExportTaskExecutorImpl",
				LoggerTestUtil.WARN)) {

			exportProcess = unsafeFunction.apply(exportProcessRequest);

			assertValid(exportProcess);

			ExportProcess finalExportProcess = exportProcess;

			ExportImportTestUtil.retryAssert(
				1, TimeUnit.SECONDS, 30, TimeUnit.SECONDS,
				() -> {
					BackgroundTask backgroundTask =
						_backgroundTaskLocalService.getBackgroundTask(
							finalExportProcess.getId());

					Assert.assertEquals(
						BackgroundTaskConstants.STATUS_SUCCESSFUL,
						backgroundTask.getStatus());
				});
		}

		return exportProcess;
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

	@TestInfo("LPD-90359")
	private void _testPostExportProcessWithDateRange(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry[] objectEntries)
		throws Exception {

		long time = System.currentTimeMillis();

		Assert.assertNull(
			_getExportedJSONArray(
				_postExportProcess(
					exportProcessRequest ->
						exportProcessResource.postExportProcess(
							0L, null, exportProcessRequest),
					objectDefinition,
					exportProcessRequest -> {
						exportProcessRequest.setEndDate(
							new Date(time - Time.DAY));
						exportProcessRequest.setStartDate(
							new Date(time - (2 * Time.DAY)));
					}),
				groupId, objectDefinition));

		ExportProcess exportProcess = _postExportProcess(
			exportProcessRequest -> exportProcessResource.postExportProcess(
				0L, null, exportProcessRequest),
			objectDefinition,
			exportProcessRequest -> {
				exportProcessRequest.setEndDate(new Date(time));
				exportProcessRequest.setStartDate(new Date(time - Time.HOUR));
			});

		_assertExportedExternalReferenceCodes(
			_backgroundTaskLocalService.getBackgroundTask(
				exportProcess.getId()),
			objectDefinition.getExternalReferenceCode(), groupId, objectEntries,
			ObjectEntry::getExternalReferenceCode);
	}

	private void _testPostExportProcessWithInvalidDateRange(
			UnsafeFunction
				<ExportProcessRequest, HttpInvoker.HttpResponse, Exception>
					unsafeFunction)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			ExportProcessRequest exportProcessRequest =
				new ExportProcessRequest();

			long time = System.currentTimeMillis();

			exportProcessRequest.setEndDate(new Date(time - Time.DAY));

			exportProcessRequest.setName(RandomTestUtil.randomString());
			exportProcessRequest.setStartDate(new Date(time));

			assertHttpResponseStatusCode(
				400, unsafeFunction.apply(exportProcessRequest));
		}
	}

	private void _testPostExportProcessWithLayoutSet(
			UnsafeFunction
				<ExportProcessRequest, HttpInvoker.HttpResponse, Exception>
					httpResponseUnsafeFunction,
			UnsafeFunction<ExportProcessRequest, ExportProcess, Exception>
				unsafeFunction)
		throws Exception {

		Layout privateLayout = LayoutTestUtil.addTypeContentLayout(
			testGroup, true, false);
		Layout publicLayout = LayoutTestUtil.addTypeContentLayout(
			testGroup, false, false);

		_assertExportedLayouts(
			"privateLayoutPages", unsafeFunction, privateLayout);
		_assertExportedLayouts(
			"publicLayoutPages", unsafeFunction, publicLayout);

		ExportProcessRequest exportProcessRequest = new ExportProcessRequest() {
			{
				name = RandomTestUtil.randomString();

				setRequestPortletDataHandlers(
					new RequestPortletDataHandler[] {
						new RequestPortletDataHandler() {
							{
								name = "PORTLET_DATA_" + _LAYOUT_SET_LAYOUTS;

								setRequestPortletDataHandlerControls(
									new RequestPortletDataHandlerControl[] {
										new RequestPortletDataHandlerControl() {
											{
												name = "publicLayoutPages";
											}
										},
										new RequestPortletDataHandlerControl() {
											{
												name = "privateLayoutPages";
											}
										}
									});
							}
						}
					});
			}
		};

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400, httpResponseUnsafeFunction.apply(exportProcessRequest));
		}
	}

	private void _testPostExportProcessWithObjectDefinition(
			UnsafeFunction<ExportProcessRequest, ExportProcess, Exception>
				unsafeFunction,
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry[] objectEntries)
		throws Exception {

		ExportProcess exportProcess = _postExportProcess(
			unsafeFunction, objectDefinition,
			exportProcessRequest -> {
			});

		_assertExportedExternalReferenceCodes(
			_backgroundTaskLocalService.getBackgroundTask(
				exportProcess.getId()),
			objectDefinition.getExternalReferenceCode(), groupId, objectEntries,
			ObjectEntry::getExternalReferenceCode);
	}

	private void _testPostExportProcessWithoutPlid(
			UnsafeFunction
				<ExportProcessRequest, HttpInvoker.HttpResponse, Exception>
					unsafeFunction)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			ExportProcessRequest exportProcessRequest =
				new ExportProcessRequest();

			exportProcessRequest.setName(RandomTestUtil.randomString());

			assertHttpResponseStatusCode(
				400, unsafeFunction.apply(exportProcessRequest));
		}
	}

	@TestInfo("LPD-90359")
	private void _testPostExportProcessWithPermissions(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry[] objectEntries)
		throws Exception {

		JSONArray jsonArray = _getExportedJSONArray(
			_postExportProcess(
				exportProcessRequest -> exportProcessResource.postExportProcess(
					0L, null, exportProcessRequest),
				objectDefinition,
				exportProcessRequest -> exportProcessRequest.setPermissions(
					true)),
			groupId, objectDefinition);

		Assert.assertEquals(objectEntries.length, jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Assert.assertTrue(
				jsonObject.toString(), jsonObject.has("permissions"));
		}
	}

	@TestInfo("LPD-90359")
	private void _testPostExportProcessWithSameName(long groupId)
		throws Exception {

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_addObjectEntry(
			objectDefinition, GroupConstants.DEFAULT_PARENT_GROUP_ID);

		String name = RandomTestUtil.randomString();

		ExportProcess exportProcess1 = _postExportProcess(
			exportProcessRequest -> exportProcessResource.postExportProcess(
				0L, null, exportProcessRequest),
			objectDefinition,
			exportProcessRequest -> exportProcessRequest.setName(name));

		_addObjectEntry(
			objectDefinition, GroupConstants.DEFAULT_PARENT_GROUP_ID);

		ExportProcess exportProcess2 = _postExportProcess(
			exportProcessRequest -> exportProcessResource.postExportProcess(
				0L, null, exportProcessRequest),
			objectDefinition,
			exportProcessRequest -> exportProcessRequest.setName(name));

		JSONArray jsonArray1 = _getExportedJSONArray(
			exportProcess1, groupId, objectDefinition);

		Assert.assertEquals(1, jsonArray1.length());

		JSONArray jsonArray2 = _getExportedJSONArray(
			exportProcess2, groupId, objectDefinition);

		Assert.assertEquals(2, jsonArray2.length());

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	private static final String _LAYOUT_SET_LAYOUTS =
		"com_liferay_layout_admin_web_portlet_LayoutSetLayoutsPortlet";

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	private ExportProcessResource _exportProcessResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectDefinitionSettingLocalService
		_objectDefinitionSettingLocalService;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}
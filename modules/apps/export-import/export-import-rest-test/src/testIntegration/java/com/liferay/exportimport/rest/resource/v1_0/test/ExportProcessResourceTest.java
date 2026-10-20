/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.lar.ExportImportGroup;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.rest.client.dto.v1_0.DateRangeDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.ExportProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.FromLastPublishDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.InstanceExportProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerBoolean;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.rest.client.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.client.dto.v1_0.Site;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportPreviewResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ExportProcessResource;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
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
import com.liferay.petra.function.transform.TransformUtil;
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
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import java.util.Arrays;
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
	public void testGetExportProcess() throws Exception {
		super.testGetExportProcess();

		_testGetExportProcessErrorMessageWhenStatusIsSuccessful();
		_testGetExportProcessErrorMessageWhenStatusMessageIsNotJSON();
	}

	@Override
	@Test
	@TestInfo("LPS-88498")
	public void testGetExportProcessContent() throws Exception {
		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		ExportProcessRequest exportProcessRequest = new ExportProcessRequest();

		exportProcessRequest.setName(RandomTestUtil.randomString());
		exportProcessRequest.setPortletDataHandlers(
			new PortletDataHandler[] {
				new PortletDataHandler() {
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
				testGroup.getExternalReferenceCode(), null, null,
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
				externalReferenceCode, null,
				new ExportProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));

		_testPostExportProcessWithInvalidDateRange(
			exportProcessRequest ->
				exportProcessResource.postAssetLibraryExportProcessHttpResponse(
					externalReferenceCode, null, exportProcessRequest));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_DEPOT);

		ObjectEntry[] objectEntries = _addObjectEntries(
			objectDefinition, testDepotEntryGroup.getGroupId());

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest ->
				exportProcessResource.postAssetLibraryExportProcess(
					externalReferenceCode, null, exportProcessRequest),
			testDepotEntryGroup.getGroupId(), objectDefinition, objectEntries);

		String portletId = objectDefinition.getPortletId();

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest ->
				exportProcessResource.postAssetLibraryExportProcess(
					externalReferenceCode, portletId, exportProcessRequest),
			testDepotEntryGroup.getGroupId(), objectDefinition, objectEntries);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);
	}

	@FeatureFlag("LPD-85946")
	@Override
	@Test
	public void testPostExportProcess() throws Exception {
		assertHttpResponseStatusCode(
			403,
			_exportProcessResource.postExportProcessHttpResponse(
				null,
				new InstanceExportProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));

		Group companyGroup = _stagingGroupHelper.fetchCompanyGroup(
			testCompany.getCompanyId());

		_testPostExportProcessWithInvalidDateRange(
			exportProcessRequest ->
				exportProcessResource.postExportProcessHttpResponse(
					null,
					_toInstanceExportProcessRequest(exportProcessRequest)));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_COMPANY);

		ObjectEntry[] objectEntries = _addObjectEntries(
			objectDefinition, GroupConstants.DEFAULT_PARENT_GROUP_ID);

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest -> exportProcessResource.postExportProcess(
				null, _toInstanceExportProcessRequest(exportProcessRequest)),
			companyGroup.getGroupId(), objectDefinition, objectEntries);
		_testPostExportProcessWithDateRange(
			companyGroup.getGroupId(), objectDefinition, objectEntries);
		_testPostExportProcessWithPermissions(
			companyGroup.getGroupId(), objectDefinition, objectEntries);

		_testPostExportProcessWithSameName(companyGroup.getGroupId());
		_testPostExportProcessWithSites();

		String portletId = objectDefinition.getPortletId();

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest -> exportProcessResource.postExportProcess(
				portletId,
				_toInstanceExportProcessRequest(exportProcessRequest)),
			companyGroup.getGroupId(), objectDefinition, objectEntries);

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
		exportProcessRequest.setPortletDataHandlers(
			new PortletDataHandler[] {
				new PortletDataHandler() {
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
					testGroup.getExternalReferenceCode(), null, null,
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
				externalReferenceCode, null, null,
				new ExportProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));

		_testPostExportProcessWithInvalidDateRange(
			exportProcessRequest ->
				exportProcessResource.postSiteExportProcessHttpResponse(
					externalReferenceCode, null, null, exportProcessRequest));
		_testPostExportProcessWithLayoutSet(
			exportProcessRequest ->
				exportProcessResource.postSiteExportProcessHttpResponse(
					externalReferenceCode, null, null, exportProcessRequest),
			exportProcessRequest -> exportProcessResource.postSiteExportProcess(
				externalReferenceCode, null, null, exportProcessRequest));
		_testPostExportProcessWithPortletDataHandlers(
			exportProcessRequest ->
				exportProcessResource.postSiteExportProcessHttpResponse(
					externalReferenceCode, null, null, exportProcessRequest),
			exportProcessRequest -> exportProcessResource.postSiteExportProcess(
				externalReferenceCode, null, null, exportProcessRequest));

		ObjectDefinition objectDefinition = _publishObjectDefinition(
			ObjectDefinitionConstants.SCOPE_SITE);

		ObjectEntry[] objectEntries = _addObjectEntries(
			objectDefinition, testGroup.getGroupId());

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest -> exportProcessResource.postSiteExportProcess(
				externalReferenceCode, null, null, exportProcessRequest),
			testGroup.getGroupId(), objectDefinition, objectEntries);

		String portletId = objectDefinition.getPortletId();

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest -> exportProcessResource.postSiteExportProcess(
				externalReferenceCode, portletId, null, exportProcessRequest),
			testGroup.getGroupId(), objectDefinition, objectEntries);

		Layout layout = _addLayoutWithPortlet(testGroup, portletId);

		_testPostExportProcessWithObjectDefinition(
			exportProcessRequest -> exportProcessResource.postSiteExportProcess(
				externalReferenceCode, portletId,
				layout.getExternalReferenceCode(), exportProcessRequest),
			testGroup.getGroupId(), objectDefinition, objectEntries);

		_testPostSiteExportProcessWithScope(
			externalReferenceCode, BlogsPortletKeys.BLOGS);
		_testPostSiteExportProcessWithSitePage(
			externalReferenceCode, portletId);

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition);

		_testPostSiteExportProcessWithLayoutSetPrototypeGroup();
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
		backgroundTaskStatus.setAttribute("phaseIndex", 1);
		backgroundTaskStatus.setAttribute("phasesTotal", 2);

		return new ProcessProgress() {
			{
				percentage = 75;
			}
		};
	}

	@Override
	protected ExportProcess testGetExportProcessesPage_addExportProcess(
			ExportProcess exportProcess)
		throws Exception {

		return _addExportProcess(randomExportProcess(), _getCompanyGroupId());
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

	private Layout _addLayoutWithPortlet(Group group, String portletId)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(layout, portletId);

		return layout;
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
			long groupId,
			PortletDataHandlerTreeSelection portletDataHandlerTreeSelection,
			UnsafeFunction<ExportProcessRequest, ExportProcess, Exception>
				unsafeFunction,
			String visibilityChoice, Layout... layouts)
		throws Exception {

		ExportProcess exportProcess = unsafeFunction.apply(
			_toExportProcessRequest(
				_toLayoutSetPortletDataHandler(
					portletDataHandlerTreeSelection, visibilityChoice)));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			exportProcess.getId());

		_assertExportedExternalReferenceCodes(
			_backgroundTaskLocalService.getBackgroundTask(
				exportProcess.getId()),
			"com.liferay.headless.admin.site.internal.resource.v1_0." +
				"SitePageResourceImpl",
			groupId, layouts, Layout::getExternalReferenceCode);
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

	private PortletDataHandler _getPortletDataHandler(
		PortletDataHandler[] portletDataHandlers, String name) {

		for (PortletDataHandler portletDataHandler : portletDataHandlers) {
			if (Objects.equals(portletDataHandler.getName(), name)) {
				return portletDataHandler;
			}
		}

		return null;
	}

	private ExportProcess _postExportProcess(
			UnsafeFunction<ExportProcessRequest, ExportProcess, Exception>
				unsafeFunction,
			ObjectDefinition objectDefinition,
			UnsafeConsumer<ExportProcessRequest, Exception> unsafeConsumer)
		throws Exception {

		ExportProcessRequest exportProcessRequest = new ExportProcessRequest();

		exportProcessRequest.setName(RandomTestUtil.randomString());
		exportProcessRequest.setPortletDataHandlers(
			new PortletDataHandler[] {
				new PortletDataHandler() {
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

	@TestInfo("LPD-102315")
	private void _testGetExportProcessErrorMessageWhenStatusIsSuccessful()
		throws Exception {

		ExportProcess exportProcess = _addExportProcess(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			BackgroundTaskExecutorNames.LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR);

		_backgroundTaskLocalService.amendBackgroundTask(
			exportProcess.getId(), null, null,
			BackgroundTaskConstants.STATUS_SUCCESSFUL, null, null);

		ExportProcess successfulExportProcess =
			exportProcessResource.getExportProcess(exportProcess.getId());

		Assert.assertNull(successfulExportProcess.getErrorMessage());
	}

	@TestInfo("LPD-102315")
	private void _testGetExportProcessErrorMessageWhenStatusMessageIsNotJSON()
		throws Exception {

		ExportProcess exportProcess = _addExportProcess(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			BackgroundTaskExecutorNames.LAYOUT_EXPORT_BACKGROUND_TASK_EXECUTOR);

		_backgroundTaskLocalService.amendBackgroundTask(
			exportProcess.getId(), null, null,
			BackgroundTaskConstants.STATUS_FAILED, _STATUS_MESSAGE, null);

		ExportProcess failedExportProcess =
			exportProcessResource.getExportProcess(exportProcess.getId());

		String errorMessage = failedExportProcess.getErrorMessage();

		Assert.assertNotEquals(_STATUS_MESSAGE, errorMessage);
		Assert.assertFalse(errorMessage, errorMessage.contains(".java:"));
		Assert.assertFalse(errorMessage, errorMessage.contains("\tat "));
		Assert.assertFalse(errorMessage, errorMessage.contains("java.lang."));
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
							null,
							_toInstanceExportProcessRequest(
								exportProcessRequest)),
					objectDefinition,
					exportProcessRequest -> exportProcessRequest.setDateFilter(
						_toDateRangeDateFilter(
							new Date(time - (2 * Time.DAY)),
							new Date(time - Time.DAY)))),
				groupId, objectDefinition));

		ExportProcess exportProcess = _postExportProcess(
			exportProcessRequest -> exportProcessResource.postExportProcess(
				null, _toInstanceExportProcessRequest(exportProcessRequest)),
			objectDefinition,
			exportProcessRequest -> exportProcessRequest.setDateFilter(
				_toDateRangeDateFilter(
					new Date(time - Time.HOUR), new Date(time))));

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

			exportProcessRequest.setDateFilter(
				_toDateRangeDateFilter(
					new Date(time), new Date(time - Time.DAY)));

			exportProcessRequest.setName(RandomTestUtil.randomString());

			assertHttpResponseStatusCode(
				400, unsafeFunction.apply(exportProcessRequest));

			exportProcessRequest.setDateFilter(
				new FromLastPublishDateFilter() {
					{
						type = Type.FROM_LAST_PUBLISH_DATE;
					}
				});

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
			testGroup.getGroupId(),
			_toPortletDataHandlerTreeSelection(
				null, null, new Layout[0], true, null),
			unsafeFunction, "private-pages", privateLayout);
		_assertExportedLayouts(
			testGroup.getGroupId(),
			_toPortletDataHandlerTreeSelection(
				null, null, new Layout[0], true, null),
			unsafeFunction, "public-pages", publicLayout);

		Layout parentLayout = LayoutTestUtil.addTypeContentLayout(
			testGroup, false, false);

		Layout childLayout = LayoutTestUtil.addTypeContentLayout(
			testGroup, parentLayout.getPlid());
		Layout excludedChildLayout = LayoutTestUtil.addTypeContentLayout(
			testGroup, parentLayout.getPlid());

		_assertExportedLayouts(
			testGroup.getGroupId(),
			_toPortletDataHandlerTreeSelection(
				null, null, new Layout[] {childLayout}, false, null),
			unsafeFunction, "public-pages", childLayout);
		_assertExportedLayouts(
			testGroup.getGroupId(),
			_toPortletDataHandlerTreeSelection(
				new Layout[] {excludedChildLayout}, null, null, false,
				new Layout[] {parentLayout}),
			unsafeFunction, "public-pages", childLayout, parentLayout);
		_assertExportedLayouts(
			testGroup.getGroupId(),
			_toPortletDataHandlerTreeSelection(
				new Layout[] {parentLayout}, null, null, false,
				new Layout[] {parentLayout}),
			unsafeFunction, "public-pages", childLayout, excludedChildLayout);
		_assertExportedLayouts(
			testGroup.getGroupId(),
			_toPortletDataHandlerTreeSelection(
				null, new Layout[] {parentLayout}, null, true, null),
			unsafeFunction, "public-pages", publicLayout);
		_assertExportedLayouts(
			testGroup.getGroupId(),
			_toPortletDataHandlerTreeSelection(
				null, new Layout[] {parentLayout}, new Layout[] {childLayout},
				true, null),
			unsafeFunction, "public-pages", childLayout, publicLayout);
		_assertExportedLayouts(
			testGroup.getGroupId(),
			_toPortletDataHandlerTreeSelection(
				new Layout[] {childLayout}, null, null, true, null),
			unsafeFunction, "public-pages", excludedChildLayout, parentLayout,
			publicLayout);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				httpResponseUnsafeFunction.apply(
					_toExportProcessRequest(
						_toLayoutSetPortletDataHandler(
							_toPortletDataHandlerTreeSelection(
								null, null, new Layout[] {privateLayout}, false,
								null),
							"public-pages"))));
			assertHttpResponseStatusCode(
				400,
				httpResponseUnsafeFunction.apply(
					_toExportProcessRequest(
						_toLayoutSetPortletDataHandler(
							_toPortletDataHandlerTreeSelection(
								null, null,
								new String[] {RandomTestUtil.randomString()},
								false, null),
							"public-pages"))));
			assertHttpResponseStatusCode(
				400,
				httpResponseUnsafeFunction.apply(
					_toExportProcessRequest(
						_toLayoutSetPortletDataHandler(
							null, RandomTestUtil.randomString()))));
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

	@TestInfo("LPD-90359")
	private void _testPostExportProcessWithPermissions(
			long groupId, ObjectDefinition objectDefinition,
			ObjectEntry[] objectEntries)
		throws Exception {

		JSONArray jsonArray = _getExportedJSONArray(
			_postExportProcess(
				exportProcessRequest -> exportProcessResource.postExportProcess(
					null,
					_toInstanceExportProcessRequest(exportProcessRequest)),
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

	private void _testPostExportProcessWithPortletDataHandlers(
			UnsafeFunction
				<ExportProcessRequest, HttpInvoker.HttpResponse, Exception>
					httpResponseUnsafeFunction,
			UnsafeFunction<ExportProcessRequest, ExportProcess, Exception>
				unsafeFunction)
		throws Exception {

		LayoutTestUtil.addTypeContentLayout(testGroup, false, false);

		User companyAdminUser = UserTestUtil.addCompanyAdminUser(testCompany);

		ExportPreviewResource exportPreviewResource =
			ExportPreviewResource.builder(
			).authentication(
				companyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		ExportPreview exportPreview =
			exportPreviewResource.getSiteExportPreview(
				testGroup.getExternalReferenceCode(), null, null, null, null,
				null, null);

		PortletDataHandler[] portletDataHandlers =
			exportPreview.getPortletDataHandlers();

		PortletDataHandler layoutSetPortletDataHandler = _getPortletDataHandler(
			portletDataHandlers,
			"PORTLET_DATA_" + LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS);

		PortletDataHandlerControl[] layoutSetPortletDataHandlerControls =
			layoutSetPortletDataHandler.getPortletDataHandlerControls();

		PortletDataHandlerChoice visibilityPortletDataHandlerChoice =
			(PortletDataHandlerChoice)layoutSetPortletDataHandlerControls[0];

		Assert.assertEquals(
			"_layout_set_visibility",
			visibilityPortletDataHandlerChoice.getName());

		PortletDataHandlerControl[] visibilityPortletDataHandlerControls =
			visibilityPortletDataHandlerChoice.getPortletDataHandlerControls();

		PortletDataHandlerControl pagesPortletDataHandlerControl =
			visibilityPortletDataHandlerControls[0];

		Assert.assertEquals(
			"_layout_set_pages", pagesPortletDataHandlerControl.getName());
		Assert.assertEquals(
			PortletDataHandlerControl.Type.TREE_SELECTION,
			pagesPortletDataHandlerControl.getType());

		PortletDataHandler lookAndFeelPortletDataHandler =
			_getPortletDataHandler(portletDataHandlers, "LOOK_AND_FEEL");

		PortletDataHandlerControl[] lookAndFeelPortletDataHandlerControls =
			lookAndFeelPortletDataHandler.getPortletDataHandlerControls();

		Assert.assertEquals(
			Arrays.toString(lookAndFeelPortletDataHandlerControls), 4,
			lookAndFeelPortletDataHandlerControls.length);

		ExportProcess exportProcess = unsafeFunction.apply(
			_toExportProcessRequest(portletDataHandlers));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			exportProcess.getId());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				httpResponseUnsafeFunction.apply(
					_toExportProcessRequest(
						new PortletDataHandler() {
							{
								name = RandomTestUtil.randomString();
							}
						})));
			assertHttpResponseStatusCode(
				400,
				httpResponseUnsafeFunction.apply(
					_toExportProcessRequest(
						new PortletDataHandler() {
							{
								name =
									"PORTLET_DATA_" +
										LayoutAdminPortletKeys.
											LAYOUT_SET_LAYOUTS;
								portletDataHandlerControls =
									new PortletDataHandlerControl[] {
										new PortletDataHandlerBoolean() {
											{
												name =
													RandomTestUtil.
														randomString();
												type =
													PortletDataHandlerControl.
														Type.BOOLEAN;
											}
										}
									};
							}
						})));
			assertHttpResponseStatusCode(
				400,
				httpResponseUnsafeFunction.apply(
					_toExportProcessRequest(
						new PortletDataHandler() {
							{
								name =
									"PORTLET_DATA_" +
										LayoutAdminPortletKeys.
											LAYOUT_SET_LAYOUTS;
								portletDataHandlerControls =
									new PortletDataHandlerControl[] {
										new PortletDataHandlerBoolean() {
											{
												name = "_layout_set_visibility";
												type =
													PortletDataHandlerControl.
														Type.BOOLEAN;
											}
										}
									};
							}
						})));
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
				null, _toInstanceExportProcessRequest(exportProcessRequest)),
			objectDefinition,
			exportProcessRequest -> exportProcessRequest.setName(name));

		_addObjectEntry(
			objectDefinition, GroupConstants.DEFAULT_PARENT_GROUP_ID);

		ExportProcess exportProcess2 = _postExportProcess(
			exportProcessRequest -> exportProcessResource.postExportProcess(
				null, _toInstanceExportProcessRequest(exportProcessRequest)),
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

	private void _testPostExportProcessWithSites() throws Exception {
		ExportProcess exportProcess = exportProcessResource.postExportProcess(
			null,
			new InstanceExportProcessRequest() {
				{
					name = RandomTestUtil.randomString();
					sites = new Site[] {
						new Site() {
							{
								externalReferenceCode =
									testGroup.getExternalReferenceCode();
							}
						}
					};
				}
			});

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			exportProcess.getId());

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				exportProcess.getId());

		List<FileEntry> fileEntries =
			backgroundTask.getAttachmentsFileEntries();

		Assert.assertEquals(fileEntries.toString(), 1, fileEntries.size());

		Assert.assertEquals(
			Collections.singletonList(testGroup.getExternalReferenceCode()),
			TransformUtil.transform(
				_exportImportHelper.getExportImportGroups(fileEntries.get(0)),
				ExportImportGroup::getExternalReferenceCode));
	}

	private void _testPostSiteExportProcessWithLayoutSetPrototypeGroup()
		throws Exception {

		_layoutSetPrototype = LayoutTestUtil.addLayoutSetPrototype(
			RandomTestUtil.randomString());

		Group group = _layoutSetPrototype.getGroup();

		LayoutTestUtil.addTypePortletLayout(group, true);

		List<Layout> layouts = _layoutLocalService.getLayouts(
			group.getGroupId(), true);

		_assertExportedLayouts(
			group.getGroupId(),
			_toPortletDataHandlerTreeSelection(
				null, null, new Layout[0], true, null),
			exportProcessRequest -> exportProcessResource.postSiteExportProcess(
				group.getExternalReferenceCode(), null, null,
				exportProcessRequest),
			"private-pages", layouts.toArray(new Layout[0]));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				exportProcessResource.postSiteExportProcessHttpResponse(
					group.getExternalReferenceCode(), null, null,
					_toExportProcessRequest(
						_toLayoutSetPortletDataHandler(null, "public-pages"))));
		}
	}

	private void _testPostSiteExportProcessWithScope(
			String externalReferenceCode, String portletId)
		throws Exception {

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		Group scopeGroup = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), testGroup.getGroupId(), layout);

		LayoutTestUtil.addPortletToLayout(
			layout, portletId,
			HashMapBuilder.put(
				"lfrScopeLayoutUuid", new String[] {layout.getUuid()}
			).put(
				"lfrScopeType", new String[] {"layout"}
			).build());

		ExportProcess exportProcess =
			exportProcessResource.postSiteExportProcess(
				externalReferenceCode, portletId,
				layout.getExternalReferenceCode(), _toExportProcessRequest());

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			exportProcess.getId());

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				exportProcess.getId());

		Assert.assertEquals(
			scopeGroup.getGroupId(), backgroundTask.getGroupId());

		Page<ExportProcess> page =
			exportProcessResource.getSiteExportProcessesPage(
				externalReferenceCode, null, portletId, null,
				layout.getExternalReferenceCode(), null, Pagination.of(1, 10),
				null);

		assertContains(exportProcess, (List<ExportProcess>)page.getItems());

		exportProcess = exportProcessResource.postSiteExportProcess(
			scopeGroup.getExternalReferenceCode(), portletId, null,
			_toExportProcessRequest());

		backgroundTask = _backgroundTaskLocalService.getBackgroundTask(
			exportProcess.getId());

		Assert.assertEquals(
			scopeGroup.getGroupId(), backgroundTask.getGroupId());

		assertHttpResponseStatusCode(
			404,
			exportProcessResource.postSiteExportProcessHttpResponse(
				scopeGroup.getExternalReferenceCode(), null, null,
				_toExportProcessRequest()));

		Layout globalLayout = LayoutTestUtil.addTypePortletLayout(testGroup);

		LayoutTestUtil.addPortletToLayout(
			globalLayout, portletId,
			HashMapBuilder.put(
				"lfrScopeType", new String[] {"company"}
			).build());

		exportProcess = exportProcessResource.postSiteExportProcess(
			externalReferenceCode, portletId,
			globalLayout.getExternalReferenceCode(), _toExportProcessRequest());

		backgroundTask = _backgroundTaskLocalService.getBackgroundTask(
			exportProcess.getId());

		Group companyGroup = _groupLocalService.getCompanyGroup(
			testCompany.getCompanyId());

		Assert.assertEquals(
			companyGroup.getGroupId(), backgroundTask.getGroupId());
	}

	private void _testPostSiteExportProcessWithSitePage(
			String externalReferenceCode, String portletId)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				exportProcessResource.postSiteExportProcessHttpResponse(
					externalReferenceCode, null, RandomTestUtil.randomString(),
					_toExportProcessRequest()));
			assertHttpResponseStatusCode(
				400,
				exportProcessResource.postSiteExportProcessHttpResponse(
					externalReferenceCode, portletId,
					RandomTestUtil.randomString(), _toExportProcessRequest()));
		}
	}

	private DateRangeDateFilter _toDateRangeDateFilter(
		Date dateRangeDateFilterStartDate, Date dateRangeDateFilterEndDate) {

		return new DateRangeDateFilter() {
			{
				endDate = dateRangeDateFilterEndDate;
				startDate = dateRangeDateFilterStartDate;
				type = Type.DATE_RANGE;
			}
		};
	}

	private ExportProcessRequest _toExportProcessRequest(
		PortletDataHandler... exportProcessRequestPortletDataHandlers) {

		return new ExportProcessRequest() {
			{
				name = RandomTestUtil.randomString();
				portletDataHandlers = exportProcessRequestPortletDataHandlers;
			}
		};
	}

	private String[] _toExternalReferenceCodes(Layout[] layouts) {
		if (layouts == null) {
			return null;
		}

		return TransformUtil.transform(
			layouts, Layout::getExternalReferenceCode, String.class);
	}

	private InstanceExportProcessRequest _toInstanceExportProcessRequest(
		ExportProcessRequest exportProcessRequest) {

		return InstanceExportProcessRequest.toDTO(
			exportProcessRequest.toString());
	}

	private PortletDataHandler _toLayoutSetPortletDataHandler(
		PortletDataHandlerTreeSelection pagesPortletDataHandlerTreeSelection,
		String visibilityChoice) {

		return new PortletDataHandler() {
			{
				name =
					"PORTLET_DATA_" + LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS;
				portletDataHandlerControls = new PortletDataHandlerControl[] {
					new PortletDataHandlerChoice() {
						{
							choice = visibilityChoice;
							name = "_layout_set_visibility";
							type = PortletDataHandlerControl.Type.CHOICE;

							if (pagesPortletDataHandlerTreeSelection != null) {
								portletDataHandlerControls =
									new PortletDataHandlerControl[] {
										pagesPortletDataHandlerTreeSelection
									};
							}
						}
					}
				};
			}
		};
	}

	private PortletDataHandlerTreeSelection _toPortletDataHandlerTreeSelection(
		Layout[] excludedItemLayouts, Layout[] excludedSubtreeLayouts,
		Layout[] itemLayouts, boolean selectAll, Layout[] subtreeLayouts) {

		return _toPortletDataHandlerTreeSelection(
			_toExternalReferenceCodes(excludedItemLayouts),
			_toExternalReferenceCodes(excludedSubtreeLayouts),
			_toExternalReferenceCodes(itemLayouts), selectAll,
			_toExternalReferenceCodes(subtreeLayouts));
	}

	private PortletDataHandlerTreeSelection _toPortletDataHandlerTreeSelection(
		String[]
			portletDataHandlerTreeSelectionExcludedItemExternalReferenceCodes,
		String[]
			portletDataHandlerTreeSelectionExcludedSubtreeExternalReferenceCodes,
		String[] portletDataHandlerTreeSelectionItemExternalReferenceCodes,
		boolean portletDataHandlerTreeSelectionSelectAll,
		String[] portletDataHandlerTreeSelectionSubtreeExternalReferenceCodes) {

		return new PortletDataHandlerTreeSelection() {
			{
				excludedItemExternalReferenceCodes =
					portletDataHandlerTreeSelectionExcludedItemExternalReferenceCodes;
				excludedSubtreeExternalReferenceCodes =
					portletDataHandlerTreeSelectionExcludedSubtreeExternalReferenceCodes;
				itemExternalReferenceCodes =
					portletDataHandlerTreeSelectionItemExternalReferenceCodes;
				name = "_layout_set_pages";
				selectAll = portletDataHandlerTreeSelectionSelectAll;
				selected = true;
				subtreeExternalReferenceCodes =
					portletDataHandlerTreeSelectionSubtreeExternalReferenceCodes;
				type = PortletDataHandlerControl.Type.TREE_SELECTION;
			}
		};
	}

	private static final String _STATUS_MESSAGE =
		"java.lang.NullPointerException\n\tat com.liferay.exportimport." +
			"internal.controller.LayoutExportController.doExport(" +
				"LayoutExportController.java:412)";

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private ExportImportHelper _exportImportHelper;

	private ExportProcessResource _exportProcessResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@DeleteAfterTestRun
	private LayoutSetPrototype _layoutSetPrototype;

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
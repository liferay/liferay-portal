/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.BlogsTestUtil;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.StagingLocalService;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.exportimport.report.constants.ExportImportReportEntryConstants;
import com.liferay.exportimport.report.service.ExportImportReportEntryLocalService;
import com.liferay.exportimport.rest.client.dto.v1_0.DateRangeDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.rest.client.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.RemoteConnection;
import com.liferay.exportimport.rest.client.dto.v1_0.ReportEntry;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.PublishPreviewResource;
import com.liferay.exportimport.rest.client.resource.v1_0.PublishProcessResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ReportEntryResource;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Raposo
 */
@RunWith(Arquillian.class)
public class PublishProcessResourceTest
	extends BasePublishProcessResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UserTestUtil.setUser(TestPropsValues.getUser());

		GroupTestUtil.enableLocalStaging(irrelevantDepotEntryGroup);
		GroupTestUtil.enableLocalStaging(irrelevantGroup);
		GroupTestUtil.enableLocalStaging(testDepotEntryGroup);
		GroupTestUtil.enableLocalStaging(testGroup);

		User adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		_publishPreviewResource = PublishPreviewResource.builder(
		).authentication(
			adminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_publishProcessResource = PublishProcessResource.builder(
		).authentication(
			_user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
		_reportEntryResource = ReportEntryResource.builder(
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
		_stagingLocalService.disableStaging(
			irrelevantDepotEntryGroup,
			ServiceContextTestUtil.getServiceContext(
				irrelevantDepotEntryGroup.getGroupId()));
		_stagingLocalService.disableStaging(
			testDepotEntryGroup,
			ServiceContextTestUtil.getServiceContext(
				testDepotEntryGroup.getGroupId()));

		super.tearDown();

		_userLocalService.deleteUser(_user);
	}

	@Override
	@Test
	public void testGetAssetLibraryPublishProcessesPage() throws Exception {
		Page<PublishProcess> page =
			publishProcessResource.getAssetLibraryPublishProcessesPage(
				testDepotEntryGroup.getExternalReferenceCode(), null, null,
				null, null, Pagination.of(1, 10), null);

		Assert.assertTrue(page.getTotalCount() >= 1);

		_deleteBackgroundTasks(irrelevantDepotEntryGroup, testDepotEntryGroup);

		super.testGetAssetLibraryPublishProcessesPage();
	}

	@Override
	@Test
	public void testGetPublishProcess() throws Exception {
		super.testGetPublishProcess();

		_testGetPublishProcessErrorMessageWhenStatusIsSuccessful();
		_testGetPublishProcessErrorMessageWhenStatusMessageIsNotJSON();
		_testGetPublishProcessWithViewStagingPermission();
	}

	@Override
	@Test
	public void testGetSitePublishProcessesPage() throws Exception {
		Page<PublishProcess> page =
			publishProcessResource.getSitePublishProcessesPage(
				testGroup.getExternalReferenceCode(), null, null, null, null,
				null, Pagination.of(1, 10), null);

		Assert.assertTrue(page.getTotalCount() >= 1);

		_deleteBackgroundTasks(irrelevantGroup, testGroup);

		super.testGetSitePublishProcessesPage();

		_testGetSitePublishProcessesPageWithPortlet();
	}

	@Override
	@Test
	public void testPostAssetLibraryPublishProcess() throws Exception {
		assertHttpResponseStatusCode(
			403,
			_publishProcessResource.postAssetLibraryPublishProcessHttpResponse(
				testDepotEntryGroup.getExternalReferenceCode(), null,
				new PublishProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));

		Group stagingGroup = _stagingGroupHelper.fetchLocalStagingGroup(
			testDepotEntryGroup);

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(
			stagingGroup.getGroupId());

		_addAssetLibraryPublishProcess(
			testDepotEntryGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString());

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), testDepotEntryGroup.getGroupId()));

		_testPostAssetLibraryPublishProcessWithPortlet();
	}

	@Override
	@Test
	public void testPostPublishProcessRelaunch() throws Exception {
		PublishProcess publishProcess = _addPublishProcess(
			testGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString());

		PublishProcess relaunchedPublishProcess =
			publishProcessResource.postPublishProcessRelaunch(
				publishProcess.getId());

		Assert.assertNotEquals(
			publishProcess.getId(), relaunchedPublishProcess.getId());

		_testPostPublishProcessRelaunchWithPortlet();
		_testPostPublishProcessRelaunchWithRemoteStaging();
	}

	@FeatureFlag("LPD-38869")
	@Override
	@Test
	public void testPostSitePublishProcess() throws Exception {
		assertHttpResponseStatusCode(
			403,
			_publishProcessResource.postSitePublishProcessHttpResponse(
				testGroup.getExternalReferenceCode(), null, null,
				new PublishProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));

		_testPostSitePublishProcessWithInvalidDateRange();
		_testPostSitePublishProcessWithoutStaging();
		_testPostSitePublishProcessWithLayoutSet();
		_testPostSitePublishProcessPerformsDirectBinaryImport();
		_testPostSitePublishProcessWithPortlet();
		_testPostSitePublishProcessWithPortletAndRemoteStaging();
		_testPostSitePublishProcessWithPortletAndReportEntry();
		_testPostSitePublishProcessWithPortletScopedToPage();
		_testPostSitePublishProcessWithRemoteConnection();
		_testPostSitePublishProcessWithRemoteStaging();
		_testPostSitePublishProcessWithRemoteStagingAndInvalidRemoteConnection();
		_testPostSitePublishProcessWithRemoteStagingAndMissingParentLayout();
		_testPostSitePublishProcessWithRemoteStagingAndPrivatePages();
		_testPostSitePublishProcessWithRemoteStagingAndRemoteConnection();
	}

	@Override
	protected PublishProcess testDeletePublishProcess_addPublishProcess()
		throws Exception {

		return _addPublishProcess(
			testGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString());
	}

	@Override
	protected PublishProcess
			testGetAssetLibraryPublishProcessesPage_addPublishProcess(
				String assetLibraryExternalReferenceCode,
				PublishProcess publishProcess)
		throws Exception {

		return _addAssetLibraryPublishProcess(
			assetLibraryExternalReferenceCode, publishProcess.getName());
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetAssetLibraryPublishProcessesPage_getExpectedActions(
				String assetLibraryExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	@Override
	protected PublishProcess testGetPublishProcess_addPublishProcess()
		throws Exception {

		return _addPublishProcess(
			testGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString());
	}

	@Override
	protected ProcessProgress testGetPublishProcessProgress_addProcessProgress(
			long publishProcessId, ProcessProgress processProgress)
		throws Exception {

		BackgroundTaskStatus backgroundTaskStatus =
			BackgroundTaskStatusRegistryUtil.registerBackgroundTaskStatus(
				publishProcessId, null);

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
	protected PublishProcess testGetSitePublishProcessesPage_addPublishProcess(
			String siteExternalReferenceCode, PublishProcess publishProcess)
		throws Exception {

		return _addPublishProcess(
			siteExternalReferenceCode, publishProcess.getName());
	}

	@Override
	protected Map<String, Map<String, String>>
			testGetSitePublishProcessesPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		return new HashMap<>();
	}

	private PublishProcess _addAssetLibraryPublishProcess(
			String assetLibraryExternalReferenceCode, String publishProcessName)
		throws Exception {

		PublishPreview publishPreview =
			_publishPreviewResource.getAssetLibraryPublishPreview(
				assetLibraryExternalReferenceCode, null, null, null, null,
				null);

		PublishProcess publishProcess =
			publishProcessResource.postAssetLibraryPublishProcess(
				assetLibraryExternalReferenceCode, null,
				new PublishProcessRequest() {
					{
						name = publishProcessName;
						portletDataHandlers =
							publishPreview.getPortletDataHandlers();
					}
				});

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			publishProcess.getId());

		return publishProcess;
	}

	private Layout _addLayoutWithPortlet(Group group) throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(group);

		LayoutTestUtil.addPortletToLayout(layout, BlogsPortletKeys.BLOGS);

		return layout;
	}

	private PublishProcess _addPortletPublishProcess(
			String portletId, String publishProcessName,
			String siteExternalReferenceCode,
			String sitePageExternalReferenceCode)
		throws Exception {

		PublishPreview publishPreview =
			_publishPreviewResource.getSitePublishPreview(
				siteExternalReferenceCode, null, null, null, portletId,
				sitePageExternalReferenceCode, null);

		PublishProcess publishProcess =
			publishProcessResource.postSitePublishProcess(
				siteExternalReferenceCode, portletId,
				sitePageExternalReferenceCode,
				new PublishProcessRequest() {
					{
						name = publishProcessName;
						portletDataHandlers =
							publishPreview.getPortletDataHandlers();
					}
				});

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			publishProcess.getId());

		return publishProcess;
	}

	private PublishProcess _addPublishProcess(
			String siteExternalReferenceCode, String publishProcessName)
		throws Exception {

		PublishProcess publishProcess =
			publishProcessResource.postSitePublishProcess(
				siteExternalReferenceCode, null, null,
				_toPublishProcessRequest(
					_toPortletDataHandlerTreeSelection(null, null, true, null),
					publishProcessName));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			publishProcess.getId());

		return publishProcess;
	}

	private void _assertPostSitePublishProcessBadRequest(
			Layout layout, String siteExternalReferenceCode)
		throws Exception {

		assertHttpResponseStatusCode(
			400,
			publishProcessResource.postSitePublishProcessHttpResponse(
				siteExternalReferenceCode, BlogsPortletKeys.BLOGS,
				layout.getExternalReferenceCode(),
				new PublishProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));
	}

	private void _assertPostSitePublishProcessProblem(
			Locale locale, PublishProcessRequest publishProcessRequest,
			String siteExternalReferenceCode, String title)
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		PublishProcessResource publishProcessResource =
			PublishProcessResource.builder(
			).authentication(
				user.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				locale
			).build();

		HttpInvoker.HttpResponse httpResponse =
			publishProcessResource.postSitePublishProcessHttpResponse(
				siteExternalReferenceCode, null, null, publishProcessRequest);

		assertHttpResponseStatusCode(400, httpResponse);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			httpResponse.getContent());

		Assert.assertEquals(title, jsonObject.getString("title"));
	}

	private void _assertTaskExecutorClassName(
			PublishProcess publishProcess, String taskExecutorClassName)
		throws Exception {

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				publishProcess.getId());

		Assert.assertEquals(
			taskExecutorClassName, backgroundTask.getTaskExecutorClassName());
	}

	private void _deleteBackgroundTasks(Group... groups) throws Exception {
		for (Group group : groups) {
			Group stagingGroup = _stagingGroupHelper.fetchLocalStagingGroup(
				group);

			_backgroundTaskLocalService.deleteGroupBackgroundTasks(
				group.getGroupId());
			_backgroundTaskLocalService.deleteGroupBackgroundTasks(
				stagingGroup.getGroupId());
		}
	}

	private Group _getStagingGroup(String siteExternalReferenceCode) {
		return _stagingGroupHelper.fetchLocalStagingGroup(
			_groupLocalService.fetchGroupByExternalReferenceCode(
				siteExternalReferenceCode, testCompany.getCompanyId()));
	}

	@TestInfo("LPD-102315")
	private void _testGetPublishProcessErrorMessageWhenStatusIsSuccessful()
		throws Exception {

		PublishProcess publishProcess = _addPublishProcess(
			testGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString());

		_backgroundTaskLocalService.amendBackgroundTask(
			publishProcess.getId(), null, null,
			BackgroundTaskConstants.STATUS_SUCCESSFUL, null, null);

		PublishProcess successfulPublishProcess =
			publishProcessResource.getPublishProcess(publishProcess.getId());

		Assert.assertNull(successfulPublishProcess.getErrorMessage());
	}

	@TestInfo("LPD-102315")
	private void _testGetPublishProcessErrorMessageWhenStatusMessageIsNotJSON()
		throws Exception {

		PublishProcess publishProcess = _addPublishProcess(
			testGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString());

		_backgroundTaskLocalService.amendBackgroundTask(
			publishProcess.getId(), null, null,
			BackgroundTaskConstants.STATUS_FAILED, _STATUS_MESSAGE, null);

		PublishProcess failedPublishProcess =
			publishProcessResource.getPublishProcess(publishProcess.getId());

		String errorMessage = failedPublishProcess.getErrorMessage();

		Assert.assertNotEquals(_STATUS_MESSAGE, errorMessage);
		Assert.assertFalse(errorMessage, errorMessage.contains(".java:"));
		Assert.assertFalse(errorMessage, errorMessage.contains("\tat "));
		Assert.assertFalse(errorMessage, errorMessage.contains("java.lang."));
	}

	private void _testGetPublishProcessWithViewStagingPermission()
		throws Exception {

		String password = RandomTestUtil.randomString();

		User user = UserTestUtil.addUser(testCompany, password);

		_userLocalService.addGroupUsers(
			testGroup.getGroupId(), new long[] {user.getUserId()});

		String externalReferenceCode = testGroup.getExternalReferenceCode();

		PublishProcess publishProcess = _addPublishProcess(
			externalReferenceCode, RandomTestUtil.randomString());

		PublishProcessResource publishProcessResource =
			PublishProcessResource.builder(
			).authentication(
				user.getEmailAddress(), password
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

		assertHttpResponseStatusCode(
			404,
			publishProcessResource.getPublishProcessHttpResponse(
				publishProcess.getId()));

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		RoleTestUtil.addResourcePermission(
			role, Group.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()), ActionKeys.VIEW_STAGING);

		assertHttpResponseStatusCode(
			200,
			publishProcessResource.getPublishProcessHttpResponse(
				publishProcess.getId()));
		assertHttpResponseStatusCode(
			200,
			publishProcessResource.getPublishProcessProgressHttpResponse(
				publishProcess.getId()));
		assertHttpResponseStatusCode(
			200,
			publishProcessResource.getSitePublishProcessesPageHttpResponse(
				externalReferenceCode, null, null, null, null, null,
				Pagination.of(1, 10), null));

		ReportEntryResource reportEntryResource = ReportEntryResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		assertHttpResponseStatusCode(
			200,
			reportEntryResource.getPublishProcessReportEntriesPageHttpResponse(
				publishProcess.getId(), null, null, Pagination.of(1, 10),
				null));

		assertHttpResponseStatusCode(
			403,
			publishProcessResource.deletePublishProcessHttpResponse(
				publishProcess.getId()));
		assertHttpResponseStatusCode(
			403,
			publishProcessResource.postPublishProcessRelaunchHttpResponse(
				publishProcess.getId()));
		assertHttpResponseStatusCode(
			403,
			publishProcessResource.postSitePublishProcessHttpResponse(
				externalReferenceCode, null, null,
				new PublishProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));
	}

	private void _testGetSitePublishProcessesPageWithPortlet()
		throws Exception {

		String externalReferenceCode = testGroup.getExternalReferenceCode();

		Layout layout = _addLayoutWithPortlet(
			_getStagingGroup(externalReferenceCode));

		PublishProcess publishProcess = _addPublishProcess(
			externalReferenceCode, RandomTestUtil.randomString());

		PublishProcess portletPublishProcess1 = _addPortletPublishProcess(
			BlogsPortletKeys.BLOGS, "A" + RandomTestUtil.randomString(),
			externalReferenceCode, layout.getExternalReferenceCode());
		PublishProcess portletPublishProcess2 = _addPortletPublishProcess(
			BlogsPortletKeys.BLOGS, "b" + RandomTestUtil.randomString(),
			externalReferenceCode, layout.getExternalReferenceCode());

		Page<PublishProcess> page =
			publishProcessResource.getSitePublishProcessesPage(
				externalReferenceCode, null, BlogsPortletKeys.BLOGS, null,
				layout.getExternalReferenceCode(), null, Pagination.of(1, 10),
				"name:asc");

		Assert.assertEquals(2, page.getTotalCount());

		assertEquals(
			Arrays.asList(portletPublishProcess1, portletPublishProcess2),
			(List<PublishProcess>)page.getItems());

		page = publishProcessResource.getSitePublishProcessesPage(
			externalReferenceCode, null, BlogsPortletKeys.BLOGS, null,
			layout.getExternalReferenceCode(), null, Pagination.of(1, 10),
			"name:desc");

		assertEquals(
			Arrays.asList(portletPublishProcess2, portletPublishProcess1),
			(List<PublishProcess>)page.getItems());

		page = publishProcessResource.getSitePublishProcessesPage(
			externalReferenceCode, null, BlogsPortletKeys.BLOGS,
			StringUtil.toLowerCase(portletPublishProcess1.getName()),
			layout.getExternalReferenceCode(), null, Pagination.of(1, 10),
			null);

		assertEquals(
			Collections.singletonList(portletPublishProcess1),
			(List<PublishProcess>)page.getItems());

		page = publishProcessResource.getSitePublishProcessesPage(
			externalReferenceCode, null, BlogsPortletKeys.BLOGS,
			BlogsPortletKeys.BLOGS, layout.getExternalReferenceCode(), null,
			Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());

		page = publishProcessResource.getSitePublishProcessesPage(
			externalReferenceCode, null, null, null, null, null,
			Pagination.of(1, 100), null);

		assertContains(publishProcess, (List<PublishProcess>)page.getItems());

		for (PublishProcess curPublishProcess : page.getItems()) {
			Assert.assertNotEquals(
				portletPublishProcess1.getId(), curPublishProcess.getId());
			Assert.assertNotEquals(
				portletPublishProcess2.getId(), curPublishProcess.getId());
		}

		page = publishProcessResource.getSitePublishProcessesPage(
			externalReferenceCode, null, BlogsPortletKeys.BLOGS, null,
			RandomTestUtil.randomString(), null, Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testPostAssetLibraryPublishProcessWithPortlet()
		throws Exception {

		Group stagingGroup = _stagingGroupHelper.fetchLocalStagingGroup(
			testDepotEntryGroup);

		FileEntry fileEntry = DLAppTestUtil.addFileEntry(
			stagingGroup.getGroupId());

		String externalReferenceCode =
			testDepotEntryGroup.getExternalReferenceCode();

		PublishPreview publishPreview =
			_publishPreviewResource.getAssetLibraryPublishPreview(
				externalReferenceCode, null, null, null,
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, null);

		PublishProcess publishProcess =
			publishProcessResource.postAssetLibraryPublishProcess(
				externalReferenceCode, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
				new PublishProcessRequest() {
					{
						name = RandomTestUtil.randomString();
						portletDataHandlers =
							publishPreview.getPortletDataHandlers();
					}
				});

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			publishProcess.getId());

		Assert.assertNotNull(
			_dlAppLocalService.getFileEntryByUuidAndGroupId(
				fileEntry.getUuid(), testDepotEntryGroup.getGroupId()));
		_assertTaskExecutorClassName(
			publishProcess,
			BackgroundTaskExecutorNames.
				PORTLET_STAGING_BACKGROUND_TASK_EXECUTOR);

		Page<PublishProcess> page =
			publishProcessResource.getAssetLibraryPublishProcessesPage(
				externalReferenceCode, null,
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, null, null,
				Pagination.of(1, 10), null);

		assertEquals(
			Collections.singletonList(publishProcess),
			(List<PublishProcess>)page.getItems());
	}

	private void _testPostPublishProcessRelaunchWithPortlet() throws Exception {
		String externalReferenceCode = testGroup.getExternalReferenceCode();

		Layout layout = _addLayoutWithPortlet(
			_getStagingGroup(externalReferenceCode));

		_addPublishProcess(
			externalReferenceCode, RandomTestUtil.randomString());

		PublishProcess publishProcess = _addPortletPublishProcess(
			BlogsPortletKeys.BLOGS, RandomTestUtil.randomString(),
			externalReferenceCode, layout.getExternalReferenceCode());

		PublishProcess relaunchedPublishProcess =
			publishProcessResource.postPublishProcessRelaunch(
				publishProcess.getId());

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			relaunchedPublishProcess.getId());

		Assert.assertEquals(
			publishProcess.getName(), relaunchedPublishProcess.getName());
		_assertTaskExecutorClassName(
			relaunchedPublishProcess,
			BackgroundTaskExecutorNames.
				PORTLET_STAGING_BACKGROUND_TASK_EXECUTOR);
	}

	private void _testPostPublishProcessRelaunchWithRemoteStaging()
		throws Exception {

		ExportImportTestUtil.withRemoteStaging(
			(stagingGroup, remoteLiveGroup) -> {
				Layout layout = LayoutTestUtil.addTypePortletLayout(
					stagingGroup);

				PublishProcess publishProcess = _addPublishProcess(
					stagingGroup.getExternalReferenceCode(),
					RandomTestUtil.randomString());

				_layoutLocalService.deleteLayout(
					_layoutLocalService.getLayoutByUuidAndGroupId(
						layout.getUuid(), remoteLiveGroup.getGroupId(), false));

				PublishProcess relaunchedPublishProcess =
					publishProcessResource.postPublishProcessRelaunch(
						publishProcess.getId());

				ExportImportTestUtil.assertBackgroundTaskSuccessful(
					relaunchedPublishProcess.getId());

				BackgroundTask backgroundTask =
					_backgroundTaskLocalService.getBackgroundTask(
						relaunchedPublishProcess.getId());

				Assert.assertEquals(
					BackgroundTaskExecutorNames.
						LAYOUT_REMOTE_STAGING_BACKGROUND_TASK_EXECUTOR,
					backgroundTask.getTaskExecutorClassName());

				Assert.assertNotNull(
					_layoutLocalService.fetchLayoutByUuidAndGroupId(
						layout.getUuid(), remoteLiveGroup.getGroupId(), false));
			});
	}

	private void _testPostSitePublishProcessPerformsDirectBinaryImport()
		throws Exception {

		PublishProcess publishProcess = _addPublishProcess(
			testGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString());

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				publishProcess.getId());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				MapUtil.getLong(
					backgroundTask.getTaskContextMap(),
					"exportImportConfigurationId"));

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		Assert.assertTrue(
			MapUtil.getBoolean(
				parameterMap,
				PortletDataHandlerKeys.PERFORM_DIRECT_BINARY_IMPORT));
	}

	private void _testPostSitePublishProcessWithInvalidDateRange()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			long time = System.currentTimeMillis();

			assertHttpResponseStatusCode(
				400,
				publishProcessResource.postSitePublishProcessHttpResponse(
					testGroup.getExternalReferenceCode(), null, null,
					new PublishProcessRequest() {
						{
							dateFilter = new DateRangeDateFilter() {
								{
									endDate = new Date(time + Time.HOUR);
									startDate = new Date(time - Time.HOUR);
									type = Type.DATE_RANGE;
								}
							};
							name = RandomTestUtil.randomString();
						}
					}));

			assertHttpResponseStatusCode(
				400,
				publishProcessResource.postSitePublishProcessHttpResponse(
					testGroup.getExternalReferenceCode(), null, null,
					new PublishProcessRequest() {
						{
							dateFilter = new DateRangeDateFilter() {
								{
									endDate = new Date(time - Time.HOUR);
									startDate = new Date(time);
									type = Type.DATE_RANGE;
								}
							};
							name = RandomTestUtil.randomString();
						}
					}));
		}
	}

	private void _testPostSitePublishProcessWithLayoutSet() throws Exception {
		Group stagingGroup = _getStagingGroup(
			testGroup.getExternalReferenceCode());

		LayoutTestUtil.addTypePortletLayout(stagingGroup);

		int layoutsCount = _layoutLocalService.getLayoutsCount(
			testGroup.getGroupId(), false);

		PublishProcess publishProcess =
			publishProcessResource.postSitePublishProcess(
				testGroup.getExternalReferenceCode(), null, null,
				new PublishProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				});

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			publishProcess.getId());

		Assert.assertEquals(
			layoutsCount,
			_layoutLocalService.getLayoutsCount(testGroup.getGroupId(), false));

		String publishProcessName = RandomTestUtil.randomString();

		publishProcess = _addPublishProcess(
			testGroup.getExternalReferenceCode(), publishProcessName);

		Assert.assertEquals(publishProcessName, publishProcess.getName());
		assertValid(publishProcess);

		Assert.assertEquals(
			layoutsCount + 1,
			_layoutLocalService.getLayoutsCount(testGroup.getGroupId(), false));

		Layout excludedLayout = LayoutTestUtil.addTypePortletLayout(
			stagingGroup);

		Layout parentLayout = LayoutTestUtil.addTypePortletLayout(stagingGroup);

		Layout selectedLayout = LayoutTestUtil.addTypePortletLayout(
			stagingGroup);

		publishProcess = publishProcessResource.postSitePublishProcess(
			testGroup.getExternalReferenceCode(), null, null,
			_toPublishProcessRequest(
				_toPortletDataHandlerTreeSelection(
					null, new Layout[] {selectedLayout}, false,
					new Layout[] {parentLayout}),
				RandomTestUtil.randomString()));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			publishProcess.getId());

		Assert.assertNull(
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				excludedLayout.getUuid(), testGroup.getGroupId(), false));
		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				parentLayout.getUuid(), testGroup.getGroupId(), false));
		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				selectedLayout.getUuid(), testGroup.getGroupId(), false));

		publishProcess = publishProcessResource.postSitePublishProcess(
			testGroup.getExternalReferenceCode(), null, null,
			_toPublishProcessRequest(
				_toPortletDataHandlerTreeSelection(
					new Layout[] {parentLayout}, null, true, null),
				RandomTestUtil.randomString()));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			publishProcess.getId());

		Assert.assertNotNull(
			_layoutLocalService.fetchLayoutByUuidAndGroupId(
				excludedLayout.getUuid(), testGroup.getGroupId(), false));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				publishProcessResource.postSitePublishProcessHttpResponse(
					testGroup.getExternalReferenceCode(), null, null,
					_toPublishProcessRequest(
						new PortletDataHandlerTreeSelection() {
							{
								itemExternalReferenceCodes = new String[] {
									RandomTestUtil.randomString()
								};
								name = "_layout_set_pages";
								selectAll = false;
								selected = true;
								type =
									PortletDataHandlerControl.Type.
										TREE_SELECTION;
							}
						},
						RandomTestUtil.randomString())));
		}
	}

	private void _testPostSitePublishProcessWithPortlet() throws Exception {
		String externalReferenceCode = testGroup.getExternalReferenceCode();

		Group stagingGroup = _getStagingGroup(externalReferenceCode);

		Layout layout = _addLayoutWithPortlet(stagingGroup);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				publishProcessResource.postSitePublishProcessHttpResponse(
					externalReferenceCode, BlogsPortletKeys.BLOGS,
					RandomTestUtil.randomString(),
					new PublishProcessRequest() {
						{
							name = RandomTestUtil.randomString();
						}
					}));
			assertHttpResponseStatusCode(
				400,
				publishProcessResource.postSitePublishProcessHttpResponse(
					externalReferenceCode, BlogsPortletKeys.BLOGS,
					layout.getExternalReferenceCode(),
					new PublishProcessRequest() {
						{
							name = RandomTestUtil.randomString();
						}
					}));

			Layout unpublishedPortletLayout =
				LayoutTestUtil.addTypePortletLayout(stagingGroup);

			_addPublishProcess(
				externalReferenceCode, RandomTestUtil.randomString());

			LayoutTestUtil.addPortletToLayout(
				unpublishedPortletLayout, BlogsPortletKeys.BLOGS);

			assertHttpResponseStatusCode(
				400,
				publishProcessResource.postSitePublishProcessHttpResponse(
					externalReferenceCode, BlogsPortletKeys.BLOGS,
					layout.getExternalReferenceCode(),
					new PublishProcessRequest() {
						{
							name = RandomTestUtil.randomString();
							remoteConnection = new RemoteConnection() {
								{
									siteId = RandomTestUtil.randomLong();
								}
							};
						}
					}));
			assertHttpResponseStatusCode(
				400,
				publishProcessResource.postSitePublishProcessHttpResponse(
					externalReferenceCode, BlogsPortletKeys.BLOGS,
					unpublishedPortletLayout.getExternalReferenceCode(),
					new PublishProcessRequest() {
						{
							name = RandomTestUtil.randomString();
						}
					}));
		}

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));

		String publishProcessName = RandomTestUtil.randomString();

		PublishProcess publishProcess = _addPortletPublishProcess(
			BlogsPortletKeys.BLOGS, publishProcessName, externalReferenceCode,
			layout.getExternalReferenceCode());

		Assert.assertNotNull(
			_blogsEntryLocalService.fetchBlogsEntryByUuidAndGroupId(
				blogsEntry.getUuid(), testGroup.getGroupId()));
		Assert.assertEquals(publishProcessName, publishProcess.getName());
		_assertTaskExecutorClassName(
			publishProcess,
			BackgroundTaskExecutorNames.
				PORTLET_STAGING_BACKGROUND_TASK_EXECUTOR);

		blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				stagingGroup.getGroupId()));

		_addPortletPublishProcess(
			BlogsPortletKeys.BLOGS_ADMIN, RandomTestUtil.randomString(),
			externalReferenceCode, null);

		Assert.assertNotNull(
			_blogsEntryLocalService.fetchBlogsEntryByUuidAndGroupId(
				blogsEntry.getUuid(), testGroup.getGroupId()));

		_userLocalService.addGroupUsers(
			testGroup.getGroupId(), new long[] {_user.getUserId()});

		PublishProcessRequest publishProcessRequest =
			new PublishProcessRequest() {
				{
					name = RandomTestUtil.randomString();
				}
			};

		assertHttpResponseStatusCode(
			403,
			_publishProcessResource.postSitePublishProcessHttpResponse(
				externalReferenceCode, BlogsPortletKeys.BLOGS,
				layout.getExternalReferenceCode(), publishProcessRequest));

		assertHttpResponseStatusCode(
			404,
			_publishProcessResource.getSitePublishProcessesPageHttpResponse(
				externalReferenceCode, null, BlogsPortletKeys.BLOGS, null,
				layout.getExternalReferenceCode(), null, Pagination.of(1, 10),
				null));
		assertHttpResponseStatusCode(
			404,
			_reportEntryResource.getPublishProcessReportEntriesPageHttpResponse(
				publishProcess.getId(), null, null, Pagination.of(1, 10),
				null));

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_userLocalService.addRoleUser(role.getRoleId(), _user.getUserId());

		RoleTestUtil.addResourcePermission(
			role, Group.class.getName(), ResourceConstants.SCOPE_GROUP,
			String.valueOf(testGroup.getGroupId()),
			ActionKeys.PUBLISH_PORTLET_INFO);

		assertHttpResponseStatusCode(
			200,
			_publishProcessResource.getSitePublishProcessesPageHttpResponse(
				externalReferenceCode, null, BlogsPortletKeys.BLOGS, null,
				layout.getExternalReferenceCode(), null, Pagination.of(1, 10),
				null));
		assertHttpResponseStatusCode(
			200,
			_publishProcessResource.postSitePublishProcessHttpResponse(
				externalReferenceCode, BlogsPortletKeys.BLOGS,
				layout.getExternalReferenceCode(), publishProcessRequest));
		assertHttpResponseStatusCode(
			200,
			_reportEntryResource.getPublishProcessReportEntriesPageHttpResponse(
				publishProcess.getId(), null, null, Pagination.of(1, 10),
				null));
		assertHttpResponseStatusCode(
			403,
			_publishProcessResource.postSitePublishProcessHttpResponse(
				externalReferenceCode, null, null, publishProcessRequest));
	}

	private void _testPostSitePublishProcessWithPortletAndRemoteStaging()
		throws Exception {

		ExportImportTestUtil.withRemoteStaging(
			(stagingGroup, remoteLiveGroup) -> {
				String externalReferenceCode =
					stagingGroup.getExternalReferenceCode();

				Layout layout = LayoutTestUtil.addTypePortletLayout(
					stagingGroup);

				_addPublishProcess(
					externalReferenceCode, RandomTestUtil.randomString());

				LayoutTestUtil.addPortletToLayout(
					layout, BlogsPortletKeys.BLOGS);

				Layout unpublishedLayout = _addLayoutWithPortlet(stagingGroup);

				try (LogCapture logCapture =
						LoggerTestUtil.configureLog4JLogger(
							"com.liferay.portal.vulcan.internal.jaxrs." +
								"exception.mapper." +
									"WebApplicationExceptionMapper",
							LoggerTestUtil.WARN)) {

					_assertPostSitePublishProcessBadRequest(
						layout, externalReferenceCode);
					_assertPostSitePublishProcessBadRequest(
						unpublishedLayout, externalReferenceCode);
				}
			});
	}

	private void _testPostSitePublishProcessWithPortletAndReportEntry()
		throws Exception {

		String externalReferenceCode = testGroup.getExternalReferenceCode();

		Layout layout = _addLayoutWithPortlet(
			_getStagingGroup(externalReferenceCode));

		_addPublishProcess(
			externalReferenceCode, RandomTestUtil.randomString());

		PublishProcess publishProcess = _addPortletPublishProcess(
			BlogsPortletKeys.BLOGS, RandomTestUtil.randomString(),
			externalReferenceCode, layout.getExternalReferenceCode());

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				publishProcess.getId());

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				MapUtil.getLong(
					backgroundTask.getTaskContextMap(),
					"exportImportConfigurationId"));

		ExportImportConfiguration reportedExportImportConfiguration =
			_exportImportConfigurationLocalService.
				addDraftExportImportConfiguration(
					TestPropsValues.getUserId(),
					ExportImportConfigurationConstants.
						TYPE_PUBLISH_PORTLET_LOCAL,
					exportImportConfiguration.getSettingsMap());

		_exportImportReportEntryLocalService.addExportImportReportEntry(
			testGroup.getGroupId(), testCompany.getCompanyId(), null,
			PortalUtil.getClassNameId(ExportImportConfiguration.class), 0,
			reportedExportImportConfiguration.getExportImportConfigurationId(),
			ExportImportReportEntryConstants.TYPE_ERROR,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					true, "LPD-96689")) {

			long backgroundTaskId = _staging.publishPortlet(
				TestPropsValues.getUserId(), reportedExportImportConfiguration);

			ExportImportTestUtil.retryAssert(
				1, TimeUnit.SECONDS, 30, TimeUnit.SECONDS,
				() -> {
					BackgroundTask reportedBackgroundTask =
						_backgroundTaskLocalService.getBackgroundTask(
							backgroundTaskId);

					Assert.assertEquals(
						BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS,
						reportedBackgroundTask.getStatus());
				});

			User adminUser = UserTestUtil.getAdminUser(
				testCompany.getCompanyId());

			ReportEntryResource reportEntryResource =
				ReportEntryResource.builder(
				).authentication(
					adminUser.getEmailAddress(),
					PropsValues.DEFAULT_ADMIN_PASSWORD
				).endpoint(
					testCompany.getVirtualHostname(),
					PortalUtil.getPortalServerPort(false), "http"
				).locale(
					LocaleUtil.getDefault()
				).build();

			Page<ReportEntry> page =
				reportEntryResource.getPublishProcessReportEntriesPage(
					backgroundTaskId, null, null, Pagination.of(1, 10), null);

			Assert.assertEquals(1, page.getTotalCount());
		}
	}

	private void _testPostSitePublishProcessWithPortletScopedToPage()
		throws Exception {

		String externalReferenceCode = testGroup.getExternalReferenceCode();

		Group stagingGroup = _getStagingGroup(externalReferenceCode);

		Layout layout = LayoutTestUtil.addTypePortletLayout(stagingGroup);

		Group scopeGroup = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), stagingGroup.getGroupId(), layout);

		LayoutTestUtil.addPortletToLayout(
			layout, BlogsPortletKeys.BLOGS,
			HashMapBuilder.put(
				"lfrScopeLayoutUuid", new String[] {layout.getUuid()}
			).put(
				"lfrScopeType", new String[] {"layout"}
			).build());

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				publishProcessResource.postSitePublishProcessHttpResponse(
					externalReferenceCode, BlogsPortletKeys.BLOGS,
					layout.getExternalReferenceCode(),
					new PublishProcessRequest() {
						{
							name = RandomTestUtil.randomString();
						}
					}));
		}

		_addPublishProcess(
			externalReferenceCode, RandomTestUtil.randomString());

		Group liveScopeGroup = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), testGroup.getGroupId(),
			_layoutLocalService.getLayoutByUuidAndGroupId(
				layout.getUuid(), testGroup.getGroupId(), false));

		scopeGroup.setLiveGroupId(liveScopeGroup.getGroupId());

		scopeGroup = _groupLocalService.updateGroup(scopeGroup);

		BlogsEntry blogsEntry = BlogsTestUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(scopeGroup.getGroupId()));

		PublishProcess publishProcess = _addPortletPublishProcess(
			BlogsPortletKeys.BLOGS, RandomTestUtil.randomString(),
			externalReferenceCode, layout.getExternalReferenceCode());

		BackgroundTask backgroundTask =
			_backgroundTaskLocalService.getBackgroundTask(
				publishProcess.getId());

		Assert.assertEquals(
			scopeGroup.getGroupId(), backgroundTask.getGroupId());

		Assert.assertNotNull(
			_blogsEntryLocalService.fetchBlogsEntryByUuidAndGroupId(
				blogsEntry.getUuid(), liveScopeGroup.getGroupId()));

		Page<PublishProcess> page =
			publishProcessResource.getSitePublishProcessesPage(
				externalReferenceCode, null, BlogsPortletKeys.BLOGS, null,
				layout.getExternalReferenceCode(), null, Pagination.of(1, 10),
				null);

		assertContains(publishProcess, (List<PublishProcess>)page.getItems());
	}

	private void _testPostSitePublishProcessWithRemoteConnection()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			assertHttpResponseStatusCode(
				400,
				publishProcessResource.postSitePublishProcessHttpResponse(
					testGroup.getExternalReferenceCode(), null, null,
					new PublishProcessRequest() {
						{
							name = RandomTestUtil.randomString();
							remoteConnection = new RemoteConnection() {
								{
									siteId = RandomTestUtil.randomLong();
								}
							};
						}
					}));
		}
	}

	private void _testPostSitePublishProcessWithRemoteStaging()
		throws Exception {

		ExportImportTestUtil.withRemoteStaging(
			(stagingGroup, remoteLiveGroup) -> {
				LayoutTestUtil.addTypePortletLayout(stagingGroup);

				int layoutsCount = _layoutLocalService.getLayoutsCount(
					remoteLiveGroup.getGroupId(), false);

				String publishProcessName = RandomTestUtil.randomString();

				PublishProcess publishProcess = _addPublishProcess(
					stagingGroup.getExternalReferenceCode(),
					publishProcessName);

				Assert.assertEquals(
					publishProcessName, publishProcess.getName());
				assertValid(publishProcess);

				Assert.assertEquals(
					layoutsCount + 1,
					_layoutLocalService.getLayoutsCount(
						remoteLiveGroup.getGroupId(), false));

				BackgroundTask backgroundTask =
					_backgroundTaskLocalService.getBackgroundTask(
						publishProcess.getId());

				Assert.assertEquals(
					BackgroundTaskExecutorNames.
						LAYOUT_REMOTE_STAGING_BACKGROUND_TASK_EXECUTOR,
					backgroundTask.getTaskExecutorClassName());

				ExportImportConfiguration exportImportConfiguration =
					_exportImportConfigurationLocalService.
						getExportImportConfiguration(
							MapUtil.getLong(
								backgroundTask.getTaskContextMap(),
								"exportImportConfigurationId"));

				Map<String, Serializable> settingsMap =
					exportImportConfiguration.getSettingsMap();

				Assert.assertFalse(
					MapUtil.getBoolean(
						(Map<String, String[]>)settingsMap.get("parameterMap"),
						PortletDataHandlerKeys.PERFORM_DIRECT_BINARY_IMPORT));

				Page<PublishProcess> page =
					publishProcessResource.getSitePublishProcessesPage(
						stagingGroup.getExternalReferenceCode(), null, null,
						null, null, null, Pagination.of(1, 10), null);

				Assert.assertEquals(1, page.getTotalCount());
			});
	}

	private void _testPostSitePublishProcessWithRemoteStagingAndInvalidRemoteConnection()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.service.impl.GroupLocalServiceImpl",
				LoggerTestUtil.ERROR)) {

			ExportImportTestUtil.withRemoteStaging(
				(stagingGroup, remoteLiveGroup) -> {
					PublishProcessRequest publishProcessRequest =
						new PublishProcessRequest() {
							{
								name = RandomTestUtil.randomString();
							}
						};

					publishProcessRequest.setRemoteConnection(
						new RemoteConnection() {
							{
								address = "connection-id-help[elasticsearch]";
							}
						});

					_assertPostSitePublishProcessProblem(
						LocaleUtil.SPAIN, publishProcessRequest,
						stagingGroup.getExternalReferenceCode(),
						LanguageUtil.format(
							LocaleUtil.SPAIN,
							"the-remote-address-x-is-not-valid",
							"connection-id-help[elasticsearch]", false));

					publishProcessRequest.setRemoteConnection(
						new RemoteConnection() {
							{
								port = 1;
							}
						});

					_assertPostSitePublishProcessProblem(
						LocaleUtil.US, publishProcessRequest,
						stagingGroup.getExternalReferenceCode(),
						StringBundler.concat(
							"Could not connect to address http://localhost:1",
							PortalUtil.getPathContext(),
							". Please verify that the specified port is ",
							"correct and that the remote server is configured ",
							"to accept requests from this server. If ",
							"everything is configured correctly, but you ",
							"still encounter this error, the administrator ",
							"has the option to forcibly disable remote ",
							"staging."));

					Page<PublishProcess> page =
						publishProcessResource.getSitePublishProcessesPage(
							stagingGroup.getExternalReferenceCode(), null, null,
							null, null, null, Pagination.of(1, 10), null);

					Assert.assertEquals(0, page.getTotalCount());
				});
		}
	}

	private void _testPostSitePublishProcessWithRemoteStagingAndMissingParentLayout()
		throws Exception {

		ExportImportTestUtil.withRemoteStaging(
			(stagingGroup, remoteLiveGroup) -> {
				Layout parentLayout = LayoutTestUtil.addTypePortletLayout(
					stagingGroup);

				Layout layout = LayoutTestUtil.addTypePortletLayout(
					stagingGroup, parentLayout.getPlid());

				try (SafeCloseable safeCloseable =
						FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
							true, "LPD-96689")) {

					PublishProcess publishProcess =
						publishProcessResource.postSitePublishProcess(
							stagingGroup.getExternalReferenceCode(), null, null,
							_toPublishProcessRequest(
								_toPortletDataHandlerTreeSelection(
									null, new Layout[] {layout}, false, null),
								RandomTestUtil.randomString()));

					ExportImportTestUtil.assertBackgroundTaskSuccessful(
						publishProcess.getId());
				}

				Layout remoteParentLayout =
					_layoutLocalService.getLayoutByExternalReferenceCode(
						parentLayout.getExternalReferenceCode(),
						remoteLiveGroup.getGroupId());

				Assert.assertTrue(remoteParentLayout.isHidden());
				Assert.assertTrue(remoteParentLayout.isTypeEmpty());

				Layout remoteLayout =
					_layoutLocalService.getLayoutByExternalReferenceCode(
						layout.getExternalReferenceCode(),
						remoteLiveGroup.getGroupId());

				Assert.assertEquals(
					remoteParentLayout.getLayoutId(),
					remoteLayout.getParentLayoutId());
			});
	}

	private void _testPostSitePublishProcessWithRemoteStagingAndPrivatePages()
		throws Exception {

		ExportImportTestUtil.withRemoteStaging(
			(stagingGroup, remoteLiveGroup) -> {
				LayoutTestUtil.addTypePortletLayout(stagingGroup, true);

				int privateLayoutsCount = _layoutLocalService.getLayoutsCount(
					remoteLiveGroup.getGroupId(), true);
				int publicLayoutsCount = _layoutLocalService.getLayoutsCount(
					remoteLiveGroup.getGroupId(), false);

				PublishProcess publishProcess =
					publishProcessResource.postSitePublishProcess(
						stagingGroup.getExternalReferenceCode(), null, null,
						_toPublishProcessRequest(
							_toPortletDataHandlerTreeSelection(
								null, null, true, null),
							RandomTestUtil.randomString(), "private-pages"));

				ExportImportTestUtil.assertBackgroundTaskSuccessful(
					publishProcess.getId());

				Assert.assertEquals(
					privateLayoutsCount + 1,
					_layoutLocalService.getLayoutsCount(
						remoteLiveGroup.getGroupId(), true));
				Assert.assertEquals(
					publicLayoutsCount,
					_layoutLocalService.getLayoutsCount(
						remoteLiveGroup.getGroupId(), false));
			});
	}

	private void _testPostSitePublishProcessWithRemoteStagingAndRemoteConnection()
		throws Exception {

		ExportImportTestUtil.withRemoteStaging(
			(stagingGroup, remoteLiveGroup) -> {
				Group targetGroup = GroupTestUtil.addGroup();

				try {
					LayoutTestUtil.addTypePortletLayout(stagingGroup);

					int remoteLiveGroupLayoutsCount =
						_layoutLocalService.getLayoutsCount(
							remoteLiveGroup.getGroupId(), false);
					int targetGroupLayoutsCount =
						_layoutLocalService.getLayoutsCount(
							targetGroup.getGroupId(), false);

					PublishProcessRequest publishProcessRequest =
						_toPublishProcessRequest(
							_toPortletDataHandlerTreeSelection(
								null, null, true, null),
							RandomTestUtil.randomString());

					publishProcessRequest.setRemoteConnection(
						new RemoteConnection() {
							{
								siteId = targetGroup.getGroupId();
							}
						});

					PublishProcess publishProcess =
						publishProcessResource.postSitePublishProcess(
							stagingGroup.getExternalReferenceCode(), null, null,
							publishProcessRequest);

					ExportImportTestUtil.assertBackgroundTaskSuccessful(
						publishProcess.getId());

					Assert.assertEquals(
						remoteLiveGroupLayoutsCount,
						_layoutLocalService.getLayoutsCount(
							remoteLiveGroup.getGroupId(), false));
					Assert.assertEquals(
						targetGroupLayoutsCount + 1,
						_layoutLocalService.getLayoutsCount(
							targetGroup.getGroupId(), false));
				}
				finally {
					GroupTestUtil.deleteGroup(targetGroup);
				}
			});
	}

	private void _testPostSitePublishProcessWithoutStaging() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			Group group = GroupTestUtil.addGroup();

			try {
				assertHttpResponseStatusCode(
					400,
					publishProcessResource.postSitePublishProcessHttpResponse(
						group.getExternalReferenceCode(), null, null,
						new PublishProcessRequest() {
							{
								name = RandomTestUtil.randomString();
							}
						}));
			}
			finally {
				GroupTestUtil.deleteGroup(group);
			}
		}
	}

	private String[] _toExternalReferenceCodes(Layout[] layouts) {
		if (layouts == null) {
			return null;
		}

		return TransformUtil.transform(
			layouts, Layout::getExternalReferenceCode, String.class);
	}

	private PortletDataHandlerTreeSelection _toPortletDataHandlerTreeSelection(
		Layout[] excludedSubtreeLayouts, Layout[] itemLayouts,
		boolean portletDataHandlerTreeSelectionSelectAll,
		Layout[] subtreeLayouts) {

		return new PortletDataHandlerTreeSelection() {
			{
				excludedSubtreeExternalReferenceCodes =
					_toExternalReferenceCodes(excludedSubtreeLayouts);
				itemExternalReferenceCodes = _toExternalReferenceCodes(
					itemLayouts);
				name = "_layout_set_pages";
				selectAll = portletDataHandlerTreeSelectionSelectAll;
				selected = true;
				subtreeExternalReferenceCodes = _toExternalReferenceCodes(
					subtreeLayouts);
				type = PortletDataHandlerControl.Type.TREE_SELECTION;
			}
		};
	}

	private PublishProcessRequest _toPublishProcessRequest(
		PortletDataHandlerTreeSelection pagesPortletDataHandlerTreeSelection,
		String publishProcessName) {

		return _toPublishProcessRequest(
			pagesPortletDataHandlerTreeSelection, publishProcessName,
			"public-pages");
	}

	private PublishProcessRequest _toPublishProcessRequest(
		PortletDataHandlerTreeSelection pagesPortletDataHandlerTreeSelection,
		String publishProcessName, String visibilityChoice) {

		return new PublishProcessRequest() {
			{
				name = publishProcessName;
				portletDataHandlers = new PortletDataHandler[] {
					new PortletDataHandler() {
						{
							name =
								"PORTLET_DATA_" +
									LayoutAdminPortletKeys.LAYOUT_SET_LAYOUTS;
							portletDataHandlerControls =
								new PortletDataHandlerControl[] {
									new PortletDataHandlerChoice() {
										{
											choice = visibilityChoice;
											name = "_layout_set_visibility";
											portletDataHandlerControls =
												new PortletDataHandlerControl[]
													 {
														pagesPortletDataHandlerTreeSelection
													};
											type =
												PortletDataHandlerControl.Type.
													CHOICE;
										}
									}
								};
						}
					}
				};
			}
		};
	}

	private static final String _STATUS_MESSAGE =
		"java.lang.NullPointerException\n\tat com.liferay.exportimport." +
			"internal.staging.StagingImpl.publishLayouts(" +
				"StagingImpl.java:2388)";

	@Inject
	private BackgroundTaskLocalService _backgroundTaskLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private ExportImportReportEntryLocalService
		_exportImportReportEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private PublishPreviewResource _publishPreviewResource;
	private PublishProcessResource _publishProcessResource;
	private ReportEntryResource _reportEntryResource;

	@Inject
	private Staging _staging;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	@Inject
	private StagingLocalService _stagingLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}
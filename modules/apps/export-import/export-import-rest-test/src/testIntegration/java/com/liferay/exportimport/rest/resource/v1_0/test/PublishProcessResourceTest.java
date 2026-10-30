/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.rest.client.dto.v1_0.DateRangeDateFilter;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.rest.client.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcessRequest;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.PublishProcessResource;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalService;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

		GroupTestUtil.enableLocalStaging(irrelevantGroup);
		GroupTestUtil.enableLocalStaging(testGroup);

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
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_userLocalService.deleteUser(_user);
	}

	@Override
	@Test
	public void testGetPublishProcess() throws Exception {
		super.testGetPublishProcess();

		_testGetPublishProcessErrorMessageWhenStatusIsSuccessful();
		_testGetPublishProcessErrorMessageWhenStatusMessageIsNotJSON();
	}

	@Override
	@Test
	public void testGetSitePublishProcessesPage() throws Exception {
		Page<PublishProcess> page =
			publishProcessResource.getSitePublishProcessesPage(
				testGroup.getExternalReferenceCode(), null, null, null,
				Pagination.of(1, 10), null);

		Assert.assertTrue(page.getTotalCount() >= 1);

		super.testGetSitePublishProcessesPage();
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
	}

	@FeatureFlag("LPD-38869")
	@Override
	@Test
	public void testPostSitePublishProcess() throws Exception {
		assertHttpResponseStatusCode(
			403,
			_publishProcessResource.postSitePublishProcessHttpResponse(
				testGroup.getExternalReferenceCode(),
				new PublishProcessRequest() {
					{
						name = RandomTestUtil.randomString();
					}
				}));

		_testPostSitePublishProcessWithInvalidDateRange();
		_testPostSitePublishProcessWithoutStaging();
		_testPostSitePublishProcessWithLayoutSet();
		_testPostSitePublishProcessPerformsDirectBinaryImport();
	}

	@Override
	protected PublishProcess testDeletePublishProcess_addPublishProcess()
		throws Exception {

		return _addPublishProcess(
			testGroup.getExternalReferenceCode(),
			RandomTestUtil.randomString());
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

	private PublishProcess _addPublishProcess(
			String siteExternalReferenceCode, String publishProcessName)
		throws Exception {

		PublishProcess publishProcess =
			publishProcessResource.postSitePublishProcess(
				siteExternalReferenceCode,
				_toPublishProcessRequest(
					_toPortletDataHandlerTreeSelection(null, null, true, null),
					publishProcessName));

		ExportImportTestUtil.assertBackgroundTaskSuccessful(
			publishProcess.getId());

		return publishProcess;
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
					testGroup.getExternalReferenceCode(),
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
					testGroup.getExternalReferenceCode(),
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
				testGroup.getExternalReferenceCode(),
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
			testGroup.getExternalReferenceCode(),
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
			testGroup.getExternalReferenceCode(),
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
					testGroup.getExternalReferenceCode(),
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
						group.getExternalReferenceCode(),
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
											choice = "public-pages";
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
	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private PublishProcessResource _publishProcessResource;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}
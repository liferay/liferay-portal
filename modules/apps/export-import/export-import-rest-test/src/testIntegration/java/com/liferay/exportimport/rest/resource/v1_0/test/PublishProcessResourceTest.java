/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.exportimport.changeset.constants.ChangesetPortletKeys;
import com.liferay.exportimport.kernel.lar.ExportImportDateUtil;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerBoolean;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.rest.client.dto.v1_0.ProcessProgress;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcess;
import com.liferay.exportimport.rest.client.dto.v1_0.PublishProcessRequest;
import com.liferay.exportimport.rest.client.dto.v1_0.ScheduledPublishProcess;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.PublishProcessResource;
import com.liferay.exportimport.rest.client.resource.v1_0.ScheduledPublishProcessResource;
import com.liferay.exportimport.test.util.ExportImportTestUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.staging.StagingGroupHelper;

import java.io.Serializable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
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

		GroupTestUtil.enableLocalStaging(irrelevantGroup);
		GroupTestUtil.enableLocalStaging(testGroup);

		String password = RandomTestUtil.randomString();

		_user = UserTestUtil.addUser(testCompany, password);

		_scheduledPublishProcessResource =
			ScheduledPublishProcessResource.builder(
			).authentication(
				_getAdminUserEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();

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
		_testPostSitePublishProcessWithInvalidCronExpression();
		_testPostSitePublishProcessWithOneTimeCronExpression();
		_testPostSitePublishProcessWithoutStaging();
		_testPostSitePublishProcessWithoutTimeZoneId();
		_testPostSitePublishProcessWithLayoutSet();
		_testPostSitePublishProcessWithDateRangeType();
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
					publishProcessName,
					_toPortletDataHandlerTreeSelection(
						true, null, null, null)));

		ExportImportTestUtil.retryAssert(
			1, TimeUnit.SECONDS, 30, TimeUnit.SECONDS,
			() -> {
				BackgroundTask backgroundTask =
					_backgroundTaskLocalService.getBackgroundTask(
						publishProcess.getId());

				Assert.assertEquals(
					BackgroundTaskConstants.STATUS_SUCCESSFUL,
					backgroundTask.getStatus());
			});

		return publishProcess;
	}

	private void _assertScheduledPublishProcess(
			String expectedRange,
			PublishProcessRequest.DateRangeType publishProcessDateRangeType,
			Date publishProcessStartDate, Date publishProcessEndDate,
			PortletDataHandler... publishProcessPortletDataHandlers)
		throws Exception {

		String publishProcessName = RandomTestUtil.randomString();

		publishProcessResource.postSitePublishProcess(
			testGroup.getExternalReferenceCode(),
			new PublishProcessRequest() {
				{
					cronExpression = _CRON_EXPRESSION;
					dateRangeType = publishProcessDateRangeType;
					endDate = publishProcessEndDate;
					name = publishProcessName;
					portletDataHandlers = publishProcessPortletDataHandlers;
					startDate = publishProcessStartDate;
					timeZoneId = "UTC";
				}
			});

		Page<ScheduledPublishProcess> page =
			_scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					testGroup.getExternalReferenceCode(), publishProcessName,
					Pagination.of(1, 1), null);

		ScheduledPublishProcess scheduledPublishProcess = page.fetchFirstItem();

		PublishProcessRequest publishProcessRequest =
			scheduledPublishProcess.getPublishProcessRequest();

		Assert.assertEquals(
			_toDateRangeType(expectedRange),
			publishProcessRequest.getDateRangeType());

		if (ArrayUtil.isNotEmpty(publishProcessPortletDataHandlers)) {
			PortletDataHandler portletDataHandler = null;

			for (PortletDataHandler curPortletDataHandler :
					publishProcessRequest.getPortletDataHandlers()) {

				if (!Objects.equals(
						curPortletDataHandler.getName(),
						PortletDataHandlerKeys.PORTLET_DATA +
							StringPool.UNDERLINE +
								BlogsPortletKeys.BLOGS_ADMIN)) {

					continue;
				}

				portletDataHandler = curPortletDataHandler;
			}

			Assert.assertNotNull(portletDataHandler);

			PortletDataHandlerControl[] portletDataHandlerControls =
				portletDataHandler.getPortletDataHandlerControls();

			PortletDataHandlerBoolean portletDataHandlerBoolean =
				(PortletDataHandlerBoolean)portletDataHandlerControls[0];

			Assert.assertEquals(
				Boolean.TRUE, portletDataHandlerBoolean.getSelected());
		}

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				scheduledPublishProcess.getId());

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		Assert.assertEquals(
			expectedRange, MapUtil.getString(parameterMap, "range"));

		if (expectedRange.equals(
				ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE)) {

			Assert.assertTrue(
				MapUtil.getBoolean(
					parameterMap,
					PortletDataHandlerKeys.PORTLET_DATA + StringPool.UNDERLINE +
						ChangesetPortletKeys.CHANGESET));

			Assert.assertFalse(
				MapUtil.getBoolean(
					parameterMap, PortletDataHandlerKeys.PORTLET_DATA_ALL));

			if (ArrayUtil.isEmpty(publishProcessPortletDataHandlers)) {
				Assert.assertFalse(
					parameterMap.containsKey(BlogsEntry.class.getName()));
			}
			else {
				Assert.assertTrue(
					MapUtil.getBoolean(
						parameterMap, BlogsEntry.class.getName()));
				Assert.assertFalse(
					parameterMap.containsKey(JournalArticle.class.getName()));
			}
		}

		if (expectedRange.equals(ExportImportDateUtil.RANGE_DATE_RANGE)) {
			Assert.assertEquals(
				publishProcessEndDate != null,
				parameterMap.containsKey("endDateSecond"));
			Assert.assertEquals(
				publishProcessEndDate != null,
				parameterMap.containsKey("endDateYear"));
			Assert.assertTrue(parameterMap.containsKey("startDateSecond"));
			Assert.assertTrue(parameterMap.containsKey("startDateYear"));
		}
		else if (expectedRange.equals(ExportImportDateUtil.RANGE_LAST)) {
			Assert.assertEquals("1", MapUtil.getString(parameterMap, "last"));
		}
	}

	private String _getAdminUserEmailAddress() throws Exception {
		User adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		return adminUser.getEmailAddress();
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

	private void _testPostSitePublishProcessWithDateRangeType()
		throws Exception {

		long time = System.currentTimeMillis();

		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_ALL, null, null, null);
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_LAST,
			PublishProcessRequest.DateRangeType.LAST,
			new Date(time - Time.HOUR), null);
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_DATE_RANGE, null,
			new Date(time - (2 * Time.HOUR)), new Date(time - Time.HOUR));
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_DATE_RANGE, null,
			new Date(time - (2 * Time.HOUR)), null);
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE,
			PublishProcessRequest.DateRangeType.FROM_LAST_PUBLISH_DATE, null,
			null);
		_assertScheduledPublishProcess(
			ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE,
			PublishProcessRequest.DateRangeType.FROM_LAST_PUBLISH_DATE, null,
			null,
			new PortletDataHandler() {
				{
					name =
						PortletDataHandlerKeys.PORTLET_DATA +
							StringPool.UNDERLINE + BlogsPortletKeys.BLOGS_ADMIN;
					portletDataHandlerControls =
						new PortletDataHandlerControl[] {
							new PortletDataHandlerBoolean() {
								{
									name =
										com.liferay.exportimport.kernel.lar.
											PortletDataHandlerControl.
												getNamespacedName(
													"blogs", "entries");
									selected = true;
									type =
										PortletDataHandlerControl.Type.BOOLEAN;
								}
							}
						};
				}
			});
	}

	private void _testPostSitePublishProcessWithInvalidCronExpression()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			for (String publishProcessCronExpression :
					new String[] {
						RandomTestUtil.randomString(), _CRON_EXPRESSION_PAST,
						_CRON_EXPRESSION_NEVER_FIRING
					}) {

				assertHttpResponseStatusCode(
					400,
					publishProcessResource.postSitePublishProcessHttpResponse(
						testGroup.getExternalReferenceCode(),
						new PublishProcessRequest() {
							{
								cronExpression = publishProcessCronExpression;
								name = RandomTestUtil.randomString();
							}
						}));
			}
		}
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
							endDate = new Date(time - Time.HOUR);
							name = RandomTestUtil.randomString();
							startDate = new Date(time);
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
				RandomTestUtil.randomString(),
				_toPortletDataHandlerTreeSelection(
					false, new Layout[] {selectedLayout},
					new Layout[] {parentLayout}, null)));

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
				RandomTestUtil.randomString(),
				_toPortletDataHandlerTreeSelection(
					true, null, null, new Layout[] {parentLayout})));

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
						RandomTestUtil.randomString(),
						new PortletDataHandlerTreeSelection() {
							{
								itemExternalReferenceCodes = new String[] {
									RandomTestUtil.randomString()
								};
								name = _CONTROL_NAME_PAGES;
								selectAll = false;
								selected = true;
								type =
									PortletDataHandlerControl.Type.
										TREE_SELECTION;
							}
						})));
		}
	}

	private void _testPostSitePublishProcessWithOneTimeCronExpression()
		throws Exception {

		Calendar calendar = CalendarFactoryUtil.getCalendar(
			TimeZoneUtil.getTimeZone("UTC"));

		calendar.clear();

		calendar.set(2099, Calendar.JANUARY, 1, 3, 0, 0);

		String publishProcessName = RandomTestUtil.randomString();

		publishProcessResource.postSitePublishProcess(
			testGroup.getExternalReferenceCode(),
			new PublishProcessRequest() {
				{
					cronExpression = _CRON_EXPRESSION_ONE_TIME;
					name = publishProcessName;
					scheduleStartDate = calendar.getTime();
					timeZoneId = "UTC";
				}
			});

		Page<ScheduledPublishProcess> page =
			_scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					testGroup.getExternalReferenceCode(), publishProcessName,
					Pagination.of(1, 1), null);

		ScheduledPublishProcess scheduledPublishProcess = page.fetchFirstItem();

		Assert.assertEquals(
			publishProcessName, scheduledPublishProcess.getName());
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

	private void _testPostSitePublishProcessWithoutTimeZoneId()
		throws Exception {

		String publishProcessName = RandomTestUtil.randomString();

		publishProcessResource.postSitePublishProcess(
			testGroup.getExternalReferenceCode(),
			new PublishProcessRequest() {
				{
					cronExpression = _CRON_EXPRESSION;
					name = publishProcessName;
				}
			});

		Page<ScheduledPublishProcess> page =
			_scheduledPublishProcessResource.
				getSiteScheduledPublishProcessesPage(
					testGroup.getExternalReferenceCode(), publishProcessName,
					Pagination.of(1, 1), null);

		ScheduledPublishProcess scheduledPublishProcess = page.fetchFirstItem();

		ExportImportConfiguration exportImportConfiguration =
			_exportImportConfigurationLocalService.getExportImportConfiguration(
				scheduledPublishProcess.getId());

		Map<String, Serializable> settingsMap =
			exportImportConfiguration.getSettingsMap();

		Map<String, String[]> parameterMap =
			(Map<String, String[]>)settingsMap.get("parameterMap");

		TimeZone timeZone = TimeZoneUtil.getDefault();

		Assert.assertEquals(
			timeZone.getID(), MapUtil.getString(parameterMap, "timeZoneId"));
	}

	private PublishProcessRequest.DateRangeType _toDateRangeType(String range) {
		if (range.equals(ExportImportDateUtil.RANGE_DATE_RANGE)) {
			return PublishProcessRequest.DateRangeType.DATE_RANGE;
		}

		if (range.equals(ExportImportDateUtil.RANGE_FROM_LAST_PUBLISH_DATE)) {
			return PublishProcessRequest.DateRangeType.FROM_LAST_PUBLISH_DATE;
		}

		if (range.equals(ExportImportDateUtil.RANGE_LAST)) {
			return PublishProcessRequest.DateRangeType.LAST;
		}

		return PublishProcessRequest.DateRangeType.ALL;
	}

	private String[] _toExternalReferenceCodes(Layout[] layouts) {
		if (layouts == null) {
			return null;
		}

		return TransformUtil.transform(
			layouts, Layout::getExternalReferenceCode, String.class);
	}

	private PortletDataHandlerTreeSelection _toPortletDataHandlerTreeSelection(
		boolean portletDataHandlerTreeSelectionSelectAll, Layout[] itemLayouts,
		Layout[] subtreeLayouts, Layout[] excludedSubtreeLayouts) {

		return new PortletDataHandlerTreeSelection() {
			{
				excludedSubtreeExternalReferenceCodes =
					_toExternalReferenceCodes(excludedSubtreeLayouts);
				itemExternalReferenceCodes = _toExternalReferenceCodes(
					itemLayouts);
				name = _CONTROL_NAME_PAGES;
				selectAll = portletDataHandlerTreeSelectionSelectAll;
				selected = true;
				subtreeExternalReferenceCodes = _toExternalReferenceCodes(
					subtreeLayouts);
				type = PortletDataHandlerControl.Type.TREE_SELECTION;
			}
		};
	}

	private PublishProcessRequest _toPublishProcessRequest(
		String publishProcessName,
		PortletDataHandlerTreeSelection pagesPortletDataHandlerTreeSelection) {

		return new PublishProcessRequest() {
			{
				name = publishProcessName;
				portletDataHandlers = new PortletDataHandler[] {
					new PortletDataHandler() {
						{
							name = _PORTLET_DATA_HANDLER_NAME_LAYOUT_SET;
							portletDataHandlerControls =
								new PortletDataHandlerControl[] {
									new PortletDataHandlerChoice() {
										{
											choice = _CHOICE_NAME_PUBLIC_PAGES;
											name = _CONTROL_NAME_VISIBILITY;
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

	private static final String _CHOICE_NAME_PUBLIC_PAGES = "public-pages";

	private static final String _CONTROL_NAME_PAGES = "_layout_set_pages";

	private static final String _CONTROL_NAME_VISIBILITY =
		"_layout_set_visibility";

	private static final String _CRON_EXPRESSION = "0 0 3 * * ?";

	private static final String _CRON_EXPRESSION_NEVER_FIRING =
		"0 0 12 31 2 ? *";

	private static final String _CRON_EXPRESSION_ONE_TIME = "0 0 3 1 1 ? 2099";

	private static final String _CRON_EXPRESSION_PAST = "0 30 9 22 8 ? 2020";

	private static final String _PORTLET_DATA_HANDLER_NAME_LAYOUT_SET =
		"PORTLET_DATA_com_liferay_layout_admin_web_portlet_" +
			"LayoutSetLayoutsPortlet";

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
	private ScheduledPublishProcessResource _scheduledPublishProcessResource;

	@Inject
	private StagingGroupHelper _stagingGroupHelper;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}
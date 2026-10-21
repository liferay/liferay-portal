/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.launch.set.publisher.LaunchSetPublisher;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletException;

import java.io.Serializable;

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

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jose Luis Navarro
 */
@RunWith(Arquillian.class)
public class UnscheduleLaunchMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "LaunchSet");

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId());

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_launchSetObjectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getSiteDefault()),
			HashMapBuilder.<String, Serializable>put(
				"name", RandomTestUtil.randomString()
			).build(),
			serviceContext);

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() throws Exception {
		_launchSetPublisher.unschedulePublish(
			_launchSetObjectEntry.getObjectEntryId());

		_objectEntryLocalService.deleteObjectEntry(_launchSetObjectEntry);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testProcessAction() throws Exception {
		JSONObject jsonObject = _processAction(TestPropsValues.getUser());

		Assert.assertEquals(_REDIRECT, jsonObject.getString("redirect"));

		_assertScheduled(null, WorkflowConstants.STATUS_DRAFT);

		Date date = new Date(System.currentTimeMillis() + Time.DAY);

		_launchSetPublisher.schedulePublish(
			date, _launchSetObjectEntry.getObjectEntryId(),
			TestPropsValues.getUserId());

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		try {
			jsonObject = _processAction(_user);

			Assert.assertEquals(
				_language.get(
					LocaleUtil.US, "you-do-not-have-the-required-permissions"),
				jsonObject.getString("error"));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}

		_assertScheduled(date, WorkflowConstants.STATUS_SCHEDULED);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.util.PortalImpl", LoggerTestUtil.WARN)) {

			String redirect =
				"http://" + RandomTestUtil.randomString() + ".com";

			jsonObject = _processAction(redirect, TestPropsValues.getUser());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Redirect URL " + redirect + " is not allowed",
				logEntry.getMessage());
		}

		Assert.assertTrue(Validator.isNull(jsonObject.getString("redirect")));

		_assertScheduled(null, WorkflowConstants.STATUS_DRAFT);

		_launchSetPublisher.schedulePublish(
			date, _launchSetObjectEntry.getObjectEntryId(),
			TestPropsValues.getUserId());

		jsonObject = _processAction(TestPropsValues.getUser());

		Assert.assertEquals(_REDIRECT, jsonObject.getString("redirect"));

		_assertScheduled(null, WorkflowConstants.STATUS_DRAFT);
	}

	private void _assertScheduled(Date expectedDate, int expectedStatus)
		throws Exception {

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				_launchSetObjectEntry.getObjectEntryId());

		Assert.assertEquals(expectedStatus, launchSetObjectEntry.getStatus());

		Map<String, Serializable> values = launchSetObjectEntry.getValues();

		Date scheduledDate = (Date)values.get("scheduledDate");

		SchedulerResponse schedulerResponse =
			_schedulerEngineHelper.getScheduledJob(
				StringBundler.concat(
					launchSetObjectEntry.getObjectEntryId(), StringPool.AT,
					launchSetObjectEntry.getCompanyId()),
				"liferay/launch_set_scheduled_publish", StorageType.PERSISTED);

		if (expectedDate == null) {
			Assert.assertNull(scheduledDate);
			Assert.assertNull(schedulerResponse);

			return;
		}

		Assert.assertEquals(
			expectedDate.getTime() / Time.SECOND,
			scheduledDate.getTime() / Time.SECOND);
		Assert.assertNotNull(schedulerResponse);
	}

	private ThemeDisplay _getThemeDisplay(User user) throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setScopeGroupId(TestPropsValues.getGroupId());
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private JSONObject _processAction(String redirect, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(user));
		mockLiferayPortletActionRequest.setParameter(
			"launchSetId",
			String.valueOf(_launchSetObjectEntry.getObjectEntryId()));
		mockLiferayPortletActionRequest.setParameter("redirect", redirect);

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		boolean thrown = false;

		try {
			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				mockLiferayPortletActionResponse);
		}
		catch (PortletException portletException) {
			thrown = true;
		}

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(
			jsonObject.toString(), jsonObject.has("error"), thrown);

		return jsonObject;
	}

	private JSONObject _processAction(User user) throws Exception {
		return _processAction(_REDIRECT, user);
	}

	private static final String _REDIRECT = "/" + RandomTestUtil.randomString();

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private Language _language;

	private ObjectEntry _launchSetObjectEntry;

	@Inject
	private LaunchSetPublisher _launchSetPublisher;

	@Inject(filter = "mvc.command.name=/launch/unschedule_launch")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private SchedulerEngineHelper _schedulerEngineHelper;

	@DeleteAfterTestRun
	private User _user;

}
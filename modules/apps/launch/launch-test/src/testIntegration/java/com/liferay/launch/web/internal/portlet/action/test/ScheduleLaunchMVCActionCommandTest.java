/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.launch.set.scheduler.LaunchSetScheduler;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
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
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Date;

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
public class ScheduleLaunchMVCActionCommandTest {

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
		_objectEntryLocalService.deleteObjectEntry(_launchSetObjectEntry);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testProcessAction() throws Exception {

		// Without permission

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(_user));

		try {
			JSONObject jsonObject = _processAction(
				System.currentTimeMillis() + Time.DAY, _user);

			Assert.assertEquals(
				_language.get(
					LocaleUtil.US, "you-do-not-have-the-required-permissions"),
				jsonObject.getString("error"));
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}

		_assertScheduledDate(null, WorkflowConstants.STATUS_DRAFT);

		// Date in the past

		JSONObject jsonObject = _processAction(
			System.currentTimeMillis() - Time.MINUTE,
			TestPropsValues.getUser());

		Assert.assertEquals(
			_language.get(
				LocaleUtil.US, "the-publish-time-must-be-in-the-future"),
			jsonObject.getString("validationError"));

		_assertScheduledDate(null, WorkflowConstants.STATUS_DRAFT);

		// External redirect

		jsonObject = _processAction(
			System.currentTimeMillis() + Time.DAY,
			"http://" + RandomTestUtil.randomString() + ".com",
			TestPropsValues.getUser());

		Assert.assertTrue(Validator.isNull(jsonObject.getString("redirect")));

		_launchSetScheduler.unschedulePublish(
			_launchSetObjectEntry.getObjectEntryId());

		// Future date

		long publishTime = System.currentTimeMillis() + Time.DAY;

		jsonObject = _processAction(publishTime, TestPropsValues.getUser());

		Assert.assertEquals(_REDIRECT, jsonObject.getString("redirect"));

		_assertScheduledDate(
			new Date(publishTime), WorkflowConstants.STATUS_SCHEDULED);

		// Already scheduled

		jsonObject = _processAction(
			System.currentTimeMillis() + Time.DAY, TestPropsValues.getUser());

		Assert.assertEquals(
			_language.get(LocaleUtil.US, "only-open-launches-can-be-scheduled"),
			jsonObject.getString("error"));

		_assertScheduledDate(
			new Date(publishTime), WorkflowConstants.STATUS_SCHEDULED);
	}

	private void _assertScheduledDate(Date expectedDate, int expectedStatus)
		throws Exception {

		ObjectEntry launchSetObjectEntry =
			_objectEntryLocalService.getObjectEntry(
				_launchSetObjectEntry.getObjectEntryId());

		Assert.assertEquals(expectedStatus, launchSetObjectEntry.getStatus());

		Assert.assertEquals(
			expectedDate,
			_launchSetScheduler.fetchScheduledDate(
				_launchSetObjectEntry.getObjectEntryId()));
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

	private JSONObject _processAction(
			long publishTime, String redirect, User user)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(user));
		mockLiferayPortletActionRequest.setParameter(
			"launchSetId",
			String.valueOf(_launchSetObjectEntry.getObjectEntryId()));
		mockLiferayPortletActionRequest.setParameter(
			"publishTime", String.valueOf(publishTime));
		mockLiferayPortletActionRequest.setParameter("redirect", redirect);

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		return _jsonFactory.createJSONObject(
			mockHttpServletResponse.getContentAsString());
	}

	private JSONObject _processAction(long publishTime, User user)
		throws Exception {

		return _processAction(publishTime, _REDIRECT, user);
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
	private LaunchSetScheduler _launchSetScheduler;

	@Inject(filter = "mvc.command.name=/launch/schedule_launch")
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private User _user;

}
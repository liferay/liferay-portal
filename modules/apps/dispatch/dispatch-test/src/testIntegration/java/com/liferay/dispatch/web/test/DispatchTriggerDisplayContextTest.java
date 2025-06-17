/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.executor.DispatchTaskExecutorRegistry;
import com.liferay.dispatch.internal.messaging.HiddenInUIDispatchTaskExecutor;
import com.liferay.dispatch.internal.messaging.SingleNodeClusterModeDispatchTaskExecutor;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Joe Duffy
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
@Sync
public class DispatchTriggerDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testCreationMenu() throws Exception {
		Set<String> dispatchTaskExecutorTypes =
			_dispatchTaskExecutorRegistry.getDispatchTaskExecutorTypes();

		Assert.assertTrue(
			dispatchTaskExecutorTypes.contains(
				HiddenInUIDispatchTaskExecutor.
					DISPATCH_TASK_EXECUTOR_TYPE_HIDDEN_IN_UI));

		Group group = GroupTestUtil.addGroup();

		Company company = _companyLocalService.getCompany(group.getCompanyId());

		User user = UserTestUtil.addUser(company);

		Object dispatchTriggerDisplayContext =
			_getDispatchTriggerDisplayContext(
				_getMockHttpServletRequest(
					company, LayoutTestUtil.addTypePortletLayout(group), user));

		Assert.assertNotNull(dispatchTriggerDisplayContext);

		ReflectionTestUtil.setFieldValue(
			(Object)ReflectionTestUtil.getFieldValue(
				dispatchTriggerDisplayContext, "dispatchRequestHelper"),
			"_currentURL", StringPool.BLANK);

		Map<?, ?> creationMenu = ReflectionTestUtil.invoke(
			dispatchTriggerDisplayContext, "getCreationMenu", new Class<?>[0],
			null);

		List<Map<?, ?>> dropdownItems = (List<Map<?, ?>>)creationMenu.get(
			"primaryItems");

		String dispatchTaskExecutorTypeHiddenInUILabel =
			ReflectionTestUtil.invoke(
				dispatchTriggerDisplayContext, "getDispatchTaskExecutorName",
				new Class<?>[] {String.class, Locale.class},
				HiddenInUIDispatchTaskExecutor.
					DISPATCH_TASK_EXECUTOR_TYPE_HIDDEN_IN_UI,
				LocaleUtil.US);

		for (Map<?, ?> dropdownItem : dropdownItems) {
			Assert.assertNotEquals(
				dispatchTaskExecutorTypeHiddenInUILabel,
				dropdownItem.get("label"));
		}
	}

	@Test
	public void testSingleNodeTaskExecutorTypes() throws Exception {
		Set<String> dispatchTaskExecutorTypes =
			_dispatchTaskExecutorRegistry.getDispatchTaskExecutorTypes();

		Assert.assertTrue(
			dispatchTaskExecutorTypes.contains(
				SingleNodeClusterModeDispatchTaskExecutor.
					DISPATCH_TASK_EXECUTOR_TYPE_SINGLE_NODE));

		User user = UserTestUtil.addUser();

		DispatchTrigger dispatchTrigger =
			_dispatchTriggerLocalService.addDispatchTrigger(
				"test-reference", user.getUserId(),
				SingleNodeClusterModeDispatchTaskExecutor.
					DISPATCH_TASK_EXECUTOR_TYPE_SINGLE_NODE,
				null, "test-trigger", false);

		Assert.assertEquals(
			DispatchTaskClusterMode.NOT_APPLICABLE,
			DispatchTaskClusterMode.valueOf(
				dispatchTrigger.getDispatchTaskClusterMode()));

		DispatchTaskClusterMode dispatchTaskClusterMode =
			DispatchTaskClusterMode.ALL_NODES;

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		calendar.setTime(new Date());

		Calendar endCalendar = (Calendar)calendar.clone();

		endCalendar.add(Calendar.DAY_OF_MONTH, 1);
		endCalendar.add(Calendar.HOUR, 1);

		Calendar startCalendar = (Calendar)calendar.clone();

		startCalendar.add(Calendar.DAY_OF_MONTH, -1);
		startCalendar.add(Calendar.HOUR, -1);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				_companyLocalService.getCompany(user.getCompanyId()),
				LayoutTestUtil.addTypePortletLayout(user.getGroupId()), user,
				"dispatchTriggerId",
				String.valueOf(dispatchTrigger.getDispatchTriggerId()),
				"active", "true", "cmd", "schedule", "cronExpression",
				"* 0/5 * ? * MON-SAT", "dispatchTaskClusterMode",
				String.valueOf(dispatchTaskClusterMode.getMode()),
				"endDateMonth", String.valueOf(endCalendar.get(Calendar.MONTH)),
				"endDateDay",
				String.valueOf(endCalendar.get(Calendar.DAY_OF_MONTH)),
				"endDateYear", String.valueOf(endCalendar.get(Calendar.YEAR)),
				"endDateHour", String.valueOf(endCalendar.get(Calendar.HOUR)),
				"endDateMinute",
				String.valueOf(endCalendar.get(Calendar.MINUTE)), "endDateAmPm",
				"1", "startDateMonth",
				String.valueOf(startCalendar.get(Calendar.MONTH)),
				"startDateDay",
				String.valueOf(startCalendar.get(Calendar.DAY_OF_MONTH)),
				"startDateYear",
				String.valueOf(startCalendar.get(Calendar.YEAR)),
				"startDateHour",
				String.valueOf(startCalendar.get(Calendar.HOUR)),
				"startDateMinute",
				String.valueOf(startCalendar.get(Calendar.MINUTE)),
				"startDateAmPm", "1");

		_mvcActionCommand.processAction(
			new MockLiferayPortletActionRequest(mockHttpServletRequest),
			new MockLiferayPortletActionResponse());

		dispatchTrigger = _dispatchTriggerLocalService.getDispatchTrigger(
			dispatchTrigger.getDispatchTriggerId());

		Assert.assertNotEquals(
			dispatchTaskClusterMode.getMode(),
			dispatchTrigger.getDispatchTaskClusterMode());

		Object dispatchTriggerDisplayContext =
			_getDispatchTriggerDisplayContext(mockHttpServletRequest);

		Assert.assertNotNull(dispatchTriggerDisplayContext);
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				dispatchTriggerDisplayContext, "isClusterModeSingle",
				new Class<?>[] {String.class},
				dispatchTrigger.getDispatchTaskExecutorType()));
	}

	private Object _getDispatchTriggerDisplayContext(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);
		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		mockLiferayPortletRenderRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			mockLiferayPortletRenderResponse);

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest, mockLiferayPortletRenderResponse);

		return mockLiferayPortletRenderRequest.getAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			Company company, Layout layout, User user, String... params)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(company, layout, user));

		if ((params != null) && ((params.length % 2) == 0)) {
			for (int i = 0; i < params.length; i = i + 2) {
				mockHttpServletRequest.addParameter(params[i], params[i + 1]);
			}
		}

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay(
			Company company, Layout layout, User user)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(company);
		themeDisplay.setLayout(layout);
		themeDisplay.setLocale(LocaleUtil.US);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(layout.getGroupId());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DispatchTaskExecutorRegistry _dispatchTaskExecutorRegistry;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject(
		filter = "component.name=com.liferay.dispatch.web.internal.portlet.action.EditDispatchTriggerMVCActionCommand"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject(
		filter = "component.name=com.liferay.dispatch.web.internal.portlet.action.ViewDispatchTriggerMVCRenderCommand"
	)
	private MVCRenderCommand _mvcRenderCommand;

}
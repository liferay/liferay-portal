/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dispatch.exception.NoSuchLogException;
import com.liferay.dispatch.executor.DispatchTaskStatus;
import com.liferay.dispatch.executor.internal.messaging.TestDispatchTaskExecutor;
import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.DispatchLogLocalService;
import com.liferay.dispatch.service.DispatchTriggerLocalService;
import com.liferay.dispatch.test.util.DispatchLogTestUtil;
import com.liferay.dispatch.test.util.DispatchTriggerTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Igor Beslic
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
@Sync
public class DispatchLogDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testDispatchLogDisplayContextExceptions() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Company company = _companyLocalService.getCompany(group.getCompanyId());

		User user = UserTestUtil.addUser(company);

		Object dispatchLogDisplayContext = _getDispatchLogDisplayContext(
			_getMockHttpServletRequest(
				company, LayoutTestUtil.addTypePortletLayout(group), user));

		Assert.assertNotNull(dispatchLogDisplayContext);

		Class<?> exceptionClass = Exception.class;

		try {
			ReflectionTestUtil.invoke(
				dispatchLogDisplayContext, "getDispatchLog", new Class<?>[0],
				null);
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Get dispatch log if HTTP servlet request misses dispatch log ID",
			NoSuchLogException.class, exceptionClass);

		try {
			exceptionClass = Exception.class;

			ReflectionTestUtil.invoke(
				dispatchLogDisplayContext, "getExecutionTimeMills",
				new Class<?>[0], null);
		}
		catch (Exception exception) {
			exceptionClass = exception.getClass();
		}

		Assert.assertEquals(
			"Get execution time if HTTP servlet request misses dispatch log ID",
			NoSuchLogException.class, exceptionClass);

		Assert.assertNull(
			ReflectionTestUtil.invoke(
				dispatchLogDisplayContext, "getDispatchTrigger",
				new Class<?>[0], null));
	}

	@Test
	public void testDispatchLogDisplayContextGetExecutionTimesMills()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		Company company = _companyLocalService.getCompany(group.getCompanyId());

		User user = UserTestUtil.addUser(company);

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(
				company, LayoutTestUtil.addTypePortletLayout(group), user);

		DispatchLog dispatchLog = _getDispatchLog(
			DispatchTaskStatus.IN_PROGRESS, user);

		mockHttpServletRequest.addParameter(
			"dispatchLogId", String.valueOf(dispatchLog.getDispatchLogId()));

		Object dispatchLogDisplayContext = _getDispatchLogDisplayContext(
			mockHttpServletRequest);

		DispatchLog contextDispatchLog = ReflectionTestUtil.invoke(
			dispatchLogDisplayContext, "getDispatchLog", new Class<?>[0], null);

		Assert.assertEquals(
			dispatchLog.getDispatchLogId(),
			contextDispatchLog.getDispatchLogId());

		long timeMillis = System.currentTimeMillis();

		Long executionTimeMills = ReflectionTestUtil.invoke(
			dispatchLogDisplayContext, "getExecutionTimeMills", new Class<?>[0],
			null);

		Assert.assertNotNull(executionTimeMills);

		Date startDate = contextDispatchLog.getStartDate();

		Assert.assertTrue(
			String.format(
				"Dispatch trigger execution time %d higher than or equal %d",
				executionTimeMills, timeMillis - startDate.getTime()),
			(timeMillis - startDate.getTime()) <= executionTimeMills);

		for (DispatchTaskStatus dispatchTaskStatus :
				DispatchTaskStatus.values()) {

			if (dispatchTaskStatus == DispatchTaskStatus.IN_PROGRESS) {
				continue;
			}

			dispatchLog = _getDispatchLog(dispatchTaskStatus, user);

			mockHttpServletRequest.removeParameter("dispatchLogId");

			mockHttpServletRequest.addParameter(
				"dispatchLogId",
				String.valueOf(dispatchLog.getDispatchLogId()));

			_assertExecutionTimeMills(
				_getDispatchLogDisplayContext(mockHttpServletRequest));
		}
	}

	private void _assertExecutionTimeMills(Object dispatchLogDisplayContext) {
		DispatchLog dispatchLog = ReflectionTestUtil.invoke(
			dispatchLogDisplayContext, "getDispatchLog", new Class<?>[0], null);

		Long executionTimeMills = ReflectionTestUtil.invoke(
			dispatchLogDisplayContext, "getExecutionTimeMills", new Class<?>[0],
			null);

		Date endDate = dispatchLog.getEndDate();

		Date startDate = dispatchLog.getStartDate();

		Assert.assertEquals(
			endDate.getTime() - startDate.getTime(),
			executionTimeMills.longValue());
	}

	private DispatchLog _getDispatchLog(
			DispatchTaskStatus dispatchTaskStatus, User user)
		throws Exception {

		DispatchTrigger dispatchTrigger =
			DispatchTriggerTestUtil.randomDispatchTrigger(
				user, TestDispatchTaskExecutor.DISPATCH_TASK_EXECUTOR_TYPE_TEST,
				RandomTestUtil.nextInt());

		dispatchTrigger = _dispatchTriggerLocalService.addDispatchTrigger(
			null, dispatchTrigger.getUserId(),
			dispatchTrigger.getDispatchTaskExecutorType(),
			dispatchTrigger.getDispatchTaskSettingsUnicodeProperties(),
			dispatchTrigger.getName(), dispatchTrigger.isSystem());

		DispatchLog expectedDispatchLog = DispatchLogTestUtil.randomDispatchLog(
			user, dispatchTaskStatus);

		DispatchLog dispatchLog = _dispatchLogLocalService.addDispatchLog(
			user.getUserId(), dispatchTrigger.getDispatchTriggerId(), null,
			null, null, expectedDispatchLog.getStartDate(),
			DispatchTaskStatus.IN_PROGRESS);

		if (dispatchTaskStatus == DispatchTaskStatus.IN_PROGRESS) {
			return dispatchLog;
		}

		return _dispatchLogLocalService.updateDispatchLog(
			dispatchLog.getDispatchLogId(), expectedDispatchLog.getEndDate(),
			expectedDispatchLog.getError(), expectedDispatchLog.getOutput(),
			DispatchTaskStatus.valueOf(expectedDispatchLog.getStatus()));
	}

	private Object _getDispatchLogDisplayContext(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		_mvcRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		return mockLiferayPortletRenderRequest.getAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT);
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
			Company company, Layout layout, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(company, layout, user));

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
	private DispatchLogLocalService _dispatchLogLocalService;

	@Inject
	private DispatchTriggerLocalService _dispatchTriggerLocalService;

	@Inject(
		filter = "component.name=com.liferay.dispatch.web.internal.portlet.action.ViewDispatchLogMVCRenderCommand"
	)
	private MVCRenderCommand _mvcRenderCommand;

}
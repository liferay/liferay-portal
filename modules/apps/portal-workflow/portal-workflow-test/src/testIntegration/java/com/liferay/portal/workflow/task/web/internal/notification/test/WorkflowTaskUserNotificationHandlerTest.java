/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.task.web.internal.notification.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.notifications.UserNotificationFeedEntry;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserNotificationEventTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.product.navigation.personal.menu.util.PersonalApplicationURLUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Leite
 */
@RunWith(Arquillian.class)
public class WorkflowTaskUserNotificationHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		_setBackURL(null, _CURRENT_URL, null);

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			_group.getGroupId(), BlogsEntry.class.getName(), 0, 0,
			"Single Approver", 1);

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(),
			new Date(System.currentTimeMillis() - Time.SECOND),
			_serviceContext);
	}

	@Test
	public void testGetLink() throws Exception {
		Group controlPanelGroup = GroupLocalServiceUtil.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CONTROL_PANEL);

		Layout controlPanelLayout = new VirtualLayout(
			LayoutLocalServiceUtil.fetchDefaultLayout(
				controlPanelGroup.getGroupId(), true),
			GroupLocalServiceUtil.getGroup(
				TestPropsValues.getCompanyId(), GroupConstants.GUEST));

		UserNotificationEvent userNotificationEvent =
			_getUserNotificationEvent();

		_assertLink(
			_CURRENT_URL, controlPanelLayout.getPlid(), controlPanelLayout,
			userNotificationEvent);

		Layout layout =
			PersonalApplicationURLUtil.
				getOrAddEmbeddedPersonalApplicationLayout(
					TestPropsValues.getUser(), _group, false);

		_assertLink(
			_CURRENT_URL, layout.getPlid(),
			LayoutTestUtil.addTypeContentLayout(_group), userNotificationEvent);
	}

	@Test
	public void testGetLinkBackURL() throws Exception {
		UserNotificationEvent userNotificationEvent =
			_getUserNotificationEvent();

		Layout layout =
			PersonalApplicationURLUtil.
				getOrAddEmbeddedPersonalApplicationLayout(
					TestPropsValues.getUser(), _group, false);

		_setBackURL(_BACK_URL, _CURRENT_URL, _REDIRECT);

		_assertLink(
			_BACK_URL, layout.getPlid(),
			LayoutTestUtil.addTypeContentLayout(_group), userNotificationEvent);

		_setBackURL(null, _CURRENT_URL, _REDIRECT);

		_assertLink(
			_REDIRECT, layout.getPlid(),
			LayoutTestUtil.addTypeContentLayout(_group), userNotificationEvent);

		_setBackURL(null, _CURRENT_URL, null);

		_assertLink(
			_CURRENT_URL, layout.getPlid(),
			LayoutTestUtil.addTypeContentLayout(_group), userNotificationEvent);

		_setBackURL(null, null, null);

		_assertLink(
			null, layout.getPlid(), LayoutTestUtil.addTypeContentLayout(_group),
			userNotificationEvent);
	}

	private void _assertLink(
			String backURL, long expectedPlid, Layout layout,
			UserNotificationEvent userNotificationEvent)
		throws Exception {

		UserNotificationFeedEntry userNotificationFeedEntry = _interpret(
			layout, userNotificationEvent);

		Assert.assertEquals(
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					_serviceContext.getRequest(), PortletKeys.MY_WORKFLOW_TASK,
					expectedPlid, PortletRequest.RENDER_PHASE)
			).setMVCPath(
				"/edit_workflow_task.jsp"
			).setBackURL(
				backURL
			).setParameter(
				"workflowTaskId",
				() -> {
					JSONObject jsonObject = _jsonFactory.createJSONObject(
						userNotificationEvent.getPayload());

					return jsonObject.get("workflowTaskId");
				}
			).setWindowState(
				WindowState.MAXIMIZED
			).buildString(),
			userNotificationFeedEntry.getLink());
	}

	private ThemeDisplay _getThemeDisplay(String currentURL, Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setURLCurrent(currentURL);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private UserNotificationEvent _getUserNotificationEvent() throws Exception {
		List<UserNotificationEvent> userNotificationEvents =
			UserNotificationEventTestUtil.getUserNotificationEvents(
				TestPropsValues.getUserId());

		for (UserNotificationEvent userNotificationEvent :
				userNotificationEvents) {

			if (!Objects.equals(
					PortletKeys.MY_WORKFLOW_TASK,
					userNotificationEvent.getType())) {

				continue;
			}

			return userNotificationEvent;
		}

		return null;
	}

	private UserNotificationFeedEntry _interpret(
			Layout layout, UserNotificationEvent userNotificationEvent)
		throws Exception {

		HttpServletRequest httpServletRequest = _serviceContext.getRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY,
			_getThemeDisplay(_serviceContext.getCurrentURL(), layout));

		return _userNotificationHandler.interpret(
			userNotificationEvent, _serviceContext);
	}

	private void _setBackURL(
		String backURL, String currentURL, String redirect) {

		_serviceContext.setCurrentURL(currentURL);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("backURL", backURL);
		mockHttpServletRequest.setParameter("redirect", redirect);

		_serviceContext.setRequest(mockHttpServletRequest);
	}

	private static final String _BACK_URL = "TestBackURL";

	private static final String _CURRENT_URL = "TestCurrentURL";

	private static final String _REDIRECT = "TestRedirect";

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JSONFactory _jsonFactory;

	private ServiceContext _serviceContext;

	@Inject(filter = "jakarta.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK)
	private UserNotificationHandler _userNotificationHandler;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}
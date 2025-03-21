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
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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

	@Test
	public void testGetLink() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			group.getGroupId(), BlogsEntry.class.getName(), 0, 0,
			"Single Approver", 1);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		_blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(),
			new Date(System.currentTimeMillis() - Time.SECOND), serviceContext);

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
			controlPanelLayout.getPlid(), group, controlPanelLayout,
			serviceContext, userNotificationEvent);

		Layout layout =
			PersonalApplicationURLUtil.
				getOrAddEmbeddedPersonalApplicationLayout(
					TestPropsValues.getUser(), group, false);

		_assertLink(
			layout.getPlid(), group, LayoutTestUtil.addTypeContentLayout(group),
			serviceContext, userNotificationEvent);
	}

	private void _assertLink(
			long expectedPlid, Group group, Layout layout,
			ServiceContext serviceContext,
			UserNotificationEvent userNotificationEvent)
		throws Exception {

		UserNotificationFeedEntry userNotificationFeedEntry = _interpret(
			group, layout, serviceContext, userNotificationEvent);

		Assert.assertEquals(
			PortletURLBuilder.create(
				PortletURLFactoryUtil.create(
					serviceContext.getRequest(), PortletKeys.MY_WORKFLOW_TASK,
					expectedPlid, PortletRequest.RENDER_PHASE)
			).setMVCPath(
				"/edit_workflow_task.jsp"
			).setBackURL(
				_CURRENT_URL
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

	private ThemeDisplay _getThemeDisplay(Group group, Layout layout)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.fetchCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setURLCurrent(_CURRENT_URL);
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private UserNotificationEvent _getUserNotificationEvent() throws Exception {
		List<UserNotificationEvent> userNotificationEvents =
			_userNotificationEventLocalService.getUserNotificationEvents(
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
			Group group, Layout layout, ServiceContext serviceContext,
			UserNotificationEvent userNotificationEvent)
		throws Exception {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay(group, layout));

		serviceContext.setRequest(httpServletRequest);

		return _userNotificationHandler.interpret(
			userNotificationEvent, serviceContext);
	}

	private static final String _CURRENT_URL = RandomTestUtil.randomString();

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

	@Inject(filter = "jakarta.portlet.name=" + PortletKeys.MY_WORKFLOW_TASK)
	private UserNotificationHandler _userNotificationHandler;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}
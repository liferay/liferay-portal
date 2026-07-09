/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.dsr.site.initializer.constants.DSRPortletKeys;
import com.liferay.site.dsr.site.initializer.constants.DSRTicketConstants;
import com.liferay.site.dsr.site.initializer.test.util.DSRTestUtil;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Stefano Motta
 */
@FeatureFlag("LPD-66359")
@RunWith(Arquillian.class)
public class InviteMemberMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		DSRTestUtil.getOrAddGroup();
	}

	@Test
	public void testRender() throws Exception {
		Group group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.GUEST);

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			_getMockLiferayPortletRenderRequest(
				group, TestPropsValues.getUser());

		MockLiferayPortletRenderResponse mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				mockLiferayPortletRenderResponse));

		HttpServletResponse httpServletResponse =
			mockLiferayPortletRenderResponse.getHttpServletResponse();

		Assert.assertEquals(302, httpServletResponse.getStatus());

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)httpServletResponse;

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertTrue(redirectedURL.contains(LoginPortletKeys.LOGIN));
		Assert.assertTrue(redirectedURL.contains("onboarding"));

		Ticket ticket = _ticketLocalService.addTicket(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			group.getGroupId(), DSRTicketConstants.TYPE_INVITE_MEMBER, null,
			JSONUtil.put(
				"emailAddress", RandomTestUtil.randomString() + "@liferay.com"
			).toString(),
			new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48)),
			new ServiceContext());

		mockLiferayPortletRenderRequest = _getMockLiferayPortletRenderRequest(
			group, null);

		mockLiferayPortletRenderRequest.addParameter(
			"ticketKey", ticket.getKey());

		mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			"/invite_member.jsp",
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				mockLiferayPortletRenderResponse));

		httpServletResponse =
			mockLiferayPortletRenderResponse.getHttpServletResponse();

		Assert.assertEquals(200, httpServletResponse.getStatus());

		User user = TestPropsValues.getUser();

		ticket = _ticketLocalService.addTicket(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			group.getGroupId(), DSRTicketConstants.TYPE_INVITE_MEMBER, null,
			JSONUtil.put(
				"emailAddress", user.getEmailAddress()
			).toString(),
			new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48)),
			new ServiceContext());

		mockLiferayPortletRenderRequest = _getMockLiferayPortletRenderRequest(
			group, null);

		mockLiferayPortletRenderRequest.addParameter(
			"ticketKey", ticket.getKey());

		mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				mockLiferayPortletRenderResponse));

		httpServletResponse =
			mockLiferayPortletRenderResponse.getHttpServletResponse();

		Assert.assertEquals(302, httpServletResponse.getStatus());

		mockHttpServletResponse = (MockHttpServletResponse)httpServletResponse;

		redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertTrue(redirectedURL.contains(LoginPortletKeys.LOGIN));
		Assert.assertTrue(redirectedURL.contains("onboarding"));

		mockLiferayPortletRenderRequest = _getMockLiferayPortletRenderRequest(
			group, user);

		mockLiferayPortletRenderRequest.addParameter(
			"ticketKey", ticket.getKey());

		mockLiferayPortletRenderResponse =
			new MockLiferayPortletRenderResponse();

		Assert.assertEquals(
			MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH,
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				mockLiferayPortletRenderResponse));

		httpServletResponse =
			mockLiferayPortletRenderResponse.getHttpServletResponse();

		Assert.assertEquals(302, httpServletResponse.getStatus());

		mockHttpServletResponse = (MockHttpServletResponse)httpServletResponse;

		Assert.assertEquals(
			"/web/guest", mockHttpServletResponse.getRedirectedUrl());

		ticket = _ticketLocalService.addTicket(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			group.getGroupId(), DSRTicketConstants.TYPE_INVITE_MEMBER, null,
			JSONUtil.put(
				"emailAddress", RandomTestUtil.randomString() + "@liferay.com"
			).toString(),
			new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48)),
			new ServiceContext());

		String ticketKey = ticket.getKey();

		_ticketLocalService.deleteTicket(ticket);

		mockLiferayPortletRenderRequest = _getMockLiferayPortletRenderRequest(
			group, null);

		mockLiferayPortletRenderRequest.addParameter("ticketKey", ticketKey);

		Assert.assertEquals(
			"/error.jsp",
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse()));

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletRenderRequest,
				NoSuchTicketException.class.getName()));

		ticket = _ticketLocalService.addTicket(
			TestPropsValues.getCompanyId(), Group.class.getName(),
			RandomTestUtil.nextLong(), DSRTicketConstants.TYPE_INVITE_MEMBER,
			null,
			JSONUtil.put(
				"emailAddress", RandomTestUtil.randomString() + "@liferay.com"
			).toString(),
			new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(48)),
			new ServiceContext());

		mockLiferayPortletRenderRequest = _getMockLiferayPortletRenderRequest(
			group, null);

		mockLiferayPortletRenderRequest.addParameter(
			"ticketKey", ticket.getKey());

		Assert.assertEquals(
			"/error.jsp",
			_mvcRenderCommand.render(
				mockLiferayPortletRenderRequest,
				new MockLiferayPortletRenderResponse()));

		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletRenderRequest,
				NoSuchGroupException.class.getName()));
	}

	private MockLiferayPortletRenderRequest _getMockLiferayPortletRenderRequest(
			Group group, User user)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest() {

				@Override
				public Portlet getPortlet() {
					return _portletLocalService.getPortletById(
						DSRPortletKeys.DSR_INVITE_MEMBER);
				}

				@Override
				public String getPortletName() {
					return DSRPortletKeys.DSR_INVITE_MEMBER;
				}

				{
					Portlet portlet = getPortlet();

					PortletApp portletApp = portlet.getPortletApp();

					portletApp.setSpecMajorVersion(2);
				}
			};

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(
				TestPropsValues.getCompanyId()));
		themeDisplay.setLayout(LayoutTestUtil.addTypePortletLayout(group));
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());

		if (user != null) {
			themeDisplay.setPermissionChecker(
				PermissionThreadLocal.getPermissionChecker());
			themeDisplay.setSignedIn(true);
			themeDisplay.setUser(user);
		}

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletRenderRequest;
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "mvc.command.name=/digital_sales_room/invite_member",
		type = MVCRenderCommand.class
	)
	private MVCRenderCommand _mvcRenderCommand;

	@Inject
	private PortletLocalService _portletLocalService;

	@Inject
	private TicketLocalService _ticketLocalService;

}
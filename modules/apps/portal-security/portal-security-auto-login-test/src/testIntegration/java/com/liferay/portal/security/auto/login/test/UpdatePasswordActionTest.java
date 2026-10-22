/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auto.login.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.action.UpdatePasswordAction;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Action;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;

import java.net.URL;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alvaro Saugar
 */
@RunWith(Arquillian.class)
public class UpdatePasswordActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testExecute() throws Exception {
		_testExecute();
		_testExecuteWithMFARedirectingToSignIn();
	}

	private MockHttpServletRequest _mockHttpServletRequest(
			Company company, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(
				ServletContextPool.get(StringPool.BLANK), HttpMethods.POST,
				StringPool.SLASH);

		mockHttpServletRequest.addParameter(Constants.CMD, "update");

		String password = RandomTestUtil.randomString();

		mockHttpServletRequest.addParameter("password1", password);
		mockHttpServletRequest.addParameter("password2", password);

		mockHttpServletRequest.addParameter("p_auth", "test");
		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, company.getCompanyId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(company);

		Layout layout = _layoutLocalService.getLayout(
			TestPropsValues.getPlid());

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setPathMain(RandomTestUtil.randomString());
		themeDisplay.setSiteGroupId(layout.getGroupId());
		themeDisplay.setURLSignIn(RandomTestUtil.randomString());
		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		mockHttpServletRequest.setCookies(
			new Cookie(CookiesConstants.NAME_COOKIE_SUPPORT, "true"));
		mockHttpServletRequest.setMethod("POST");

		Ticket ticket = _ticketLocalService.addDistinctTicket(
			user.getCompanyId(), User.class.getName(), user.getUserId(),
			TicketConstants.TYPE_PASSWORD, null,
			new Date(System.currentTimeMillis() + 3600000),
			new ServiceContext());

		_ticketId = String.valueOf(ticket.getTicketId());
		_ticketKey = ticket.getKey();

		mockHttpServletRequest.setParameter(
			"ticketId", String.valueOf(ticket.getTicketId()));
		mockHttpServletRequest.setParameter("ticketKey", ticket.getKey());

		ticket.setKey(PasswordEncryptorUtil.encrypt(ticket.getKey()));

		_ticketLocalService.updateTicket(ticket);

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(
			"LIFERAY_SHARED_AUTHENTICATION_TOKEN#CSRF", "test");

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextFactory.getInstance(mockHttpServletRequest));

		return mockHttpServletRequest;
	}

	private void _testExecute() throws Exception {
		Action action = new UpdatePasswordAction();

		User user = UserTestUtil.addUser();

		user.setLastLoginDate(null);

		user = _userLocalService.updateUser(user);

		Company company = _companyLocalService.getCompany(user.getCompanyId());

		MockHttpServletRequest mockHttpServletRequest = _mockHttpServletRequest(
			company, user);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		action.execute(null, mockHttpServletRequest, mockHttpServletResponse);

		HttpSession httpSession = mockHttpServletRequest.getSession();

		Assert.assertEquals(
			String.valueOf(user.getUserId()),
			httpSession.getAttribute("j_remoteuser"));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Assert.assertEquals(
			themeDisplay.getPathMain(),
			mockHttpServletResponse.getRedirectedUrl());

		user = _userLocalService.getUserByEmailAddress(
			company.getCompanyId(), user.getEmailAddress());

		Assert.assertNotNull(user.getLastLoginDate());

		URL url = new URL(
			StringBundler.concat(
				"http://localhost:", PortalUtil.getPortalServerPort(false),
				"/c/portal/update_password?languageId=", user.getLanguageId(),
				"&ticketId=", _ticketId, "&ticketKey=", _ticketKey));

		String content = URLUtil.toString(url);

		Assert.assertTrue(
			content.contains("Your password reset link is no longer valid"));
	}

	private void _testExecuteWithMFARedirectingToSignIn() throws Exception {
		Action action = new UpdatePasswordAction();

		User user = UserTestUtil.addUser();

		MockHttpServletRequest mockHttpServletRequest = _mockHttpServletRequest(
			_companyLocalService.getCompany(user.getCompanyId()), user);

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.MFA_ENABLED, Boolean.TRUE);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		action.execute(null, mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertNull(httpSession.getAttribute("j_remoteuser"));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Assert.assertEquals(
			themeDisplay.getURLSignIn(),
			mockHttpServletResponse.getRedirectedUrl());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private String _ticketId;
	private String _ticketKey;

	@Inject
	private TicketLocalService _ticketLocalService;

	@Inject
	private UserLocalService _userLocalService;

}
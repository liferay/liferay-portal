/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auto.login.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.action.UpdatePasswordAction;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
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
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Action;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
		Action action = new UpdatePasswordAction();

		User user = UserTestUtil.addUser();

		user.setLastLoginDate(null);

		user = _userLocalService.updateUser(user);

		Company company = _companyLocalService.getCompany(user.getCompanyId());

		action.execute(
			null, _mockHttpServletRequest(company, user),
			new MockHttpServletResponse());

		user = _userLocalService.getUserByEmailAddress(
			company.getCompanyId(), user.getEmailAddress());

		Assert.assertNotNull(user.getLastLoginDate());

		// Update password returns alert message when the ticket is no
		// longer valid

		URL url = new URL(
			StringBundler.concat(
				"http://localhost:8080/c/portal/update_password?languageId=",
				user.getLanguageId(), "&ticketId=", _ticketId, "&ticketKey=",
				_ticketKey));

		String content = URLUtil.toString(url);

		Assert.assertTrue(
			content.contains("Your password reset link is no longer valid"));
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

		Group group = layout.getGroup();

		themeDisplay.setSiteGroupId(group.getGroupId());

		themeDisplay.setUser(user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

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
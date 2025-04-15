/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auto.login.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.action.UpdatePasswordAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.struts.Action;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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
public class SetupAdminAutoLoginTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_safeCloseables.add(
			PropsValuesTestUtil.swapWithSafeCloseable(
				"DEFAULT_ADMIN_PASSWORD", ""));

		_company = CompanyTestUtil.addCompany();

		_originalCompanyId = CompanyThreadLocal.getCompanyId();

		_safeCloseables.add(
			CompanyThreadLocal.setCompanyIdWithSafeCloseable(
				_company.getCompanyId()));

		try {
			_emailAdressAdminUser =
				PropsValues.DEFAULT_ADMIN_EMAIL_ADDRESS_PREFIX + StringPool.AT +
					_company.getMx();

			_user = UserLocalServiceUtil.getUserByEmailAddress(
				_company.getCompanyId(), _emailAdressAdminUser);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Error getting user ", exception);
			}
		}
	}

	@AfterClass
	public static void tearDownClass() {
		for (SafeCloseable safeCloseable : _safeCloseables) {
			safeCloseable.close();
		}
	}

	@Test
	public void testAutologinToSetAdminPassword() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _company.getCompanyId());

		String[] credentials = _adminPasswordAutoLogin.login(
			mockHttpServletRequest, null);

		Assert.assertEquals(credentials[0], String.valueOf(_user.getUserId()));
		Assert.assertEquals(credentials[1], _user.getPassword());
		Assert.assertEquals("true", credentials[2]);
	}

	@Test
	public void testChangeUserConditionSetAdminPassword() throws Exception {
		Assert.assertEquals(
			_user.getReminderQueryAnswer(), WorkflowConstants.LABEL_PENDING);

		MockHttpServletRequest mockHttpServletRequest =
			_prepareHttpServletRequest();

		_updatePasswordAction.execute(
			null, mockHttpServletRequest, new MockHttpServletResponse());

		User setPasswordUser = UserLocalServiceUtil.getUserByEmailAddress(
			_company.getCompanyId(), _emailAdressAdminUser);

		Assert.assertEquals("", setPasswordUser.getReminderQueryAnswer());
	}

	private MockHttpServletRequest _prepareHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(
				ServletContextPool.get(StringPool.BLANK), HttpMethods.GET,
				StringPool.SLASH);

		mockHttpServletRequest.addParameter(Constants.CMD, "update");

		String password = RandomTestUtil.randomString();

		mockHttpServletRequest.addParameter("password1", password);
		mockHttpServletRequest.addParameter("password2", password);

		mockHttpServletRequest.addParameter("p_auth", "test");

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _company.getCompanyId());

		HttpSession httpSession = mockHttpServletRequest.getSession();

		httpSession.setAttribute(
			"LIFERAY_SHARED_AUTHENTICATION_TOKEN#CSRF", "test");

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);

		Layout layout = LayoutLocalServiceUtil.getLayout(
			PortalUtil.getControlPanelPlid(_company.getCompanyId()));

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		Group group = layout.getGroup();

		themeDisplay.setSiteGroupId(group.getGroupId());

		themeDisplay.setUser(_user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			mockHttpServletRequest);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		return mockHttpServletRequest;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SetupAdminAutoLoginTest.class);

	private static Company _company;
	private static String _emailAdressAdminUser;
	private static long _originalCompanyId;
	private static final List<SafeCloseable> _safeCloseables =
		new ArrayList<>();
	private static User _user;

	@Inject(
		filter = "component.name=com.liferay.portal.events.SetupAdminAutoLogin"
	)
	private AutoLogin _adminPasswordAutoLogin;

	private final Action _updatePasswordAction = new UpdatePasswordAction();

}
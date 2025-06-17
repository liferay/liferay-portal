/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletApp;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.Cookie;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Istvan Sajtos
 */
@FeatureFlag("LPD-6378")
@RunWith(Arquillian.class)
public class LoginMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testFailedLoginRedirectWithLayoutUtilityPageEntry()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"AUTH_TOKEN_CHECK_ENABLED", false)) {

			LayoutUtilityPageEntry layoutUtilityPageEntry =
				_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
					null, _serviceContext.getUserId(), _group.getGroupId(), 0,
					0, true, RandomTestUtil.randomString(),
					LayoutUtilityPageEntryConstants.TYPE_LOGIN, 0,
					_serviceContext);

			CustomMockLiferayPortletActionResponse
				customMockLiferayPortletActionResponse =
					new CustomMockLiferayPortletActionResponse();

			_mvcActionCommand.processAction(
				_getMockLiferayPortletActionRequest(),
				customMockLiferayPortletActionResponse);

			String redirectLocation =
				customMockLiferayPortletActionResponse.getRedirectLocation();

			Assert.assertTrue(
				redirectLocation.contains(_group.getFriendlyURL()));

			Layout layout = _layoutLocalService.getLayout(
				layoutUtilityPageEntry.getPlid());

			Assert.assertTrue(
				redirectLocation.contains(layout.getFriendlyURL()));

			_assertParameter(
				redirectLocation, "p_p_id", LoginPortletKeys.LOGIN);
			_assertParameter(redirectLocation, "p_p_lifecycle", "0");
			_assertParameter(redirectLocation, "p_p_state", "normal");
			_assertParameter(redirectLocation, "saveLastPath", "false");
		}
	}

	@Test
	public void testFailedLoginRedirectWithoutLayoutUtilityPageEntry()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"AUTH_TOKEN_CHECK_ENABLED", false)) {

			MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
				_getMockLiferayPortletActionRequest();

			CustomMockLiferayPortletActionResponse
				customMockLiferayPortletActionResponse =
					new CustomMockLiferayPortletActionResponse();

			_mvcActionCommand.processAction(
				mockLiferayPortletActionRequest,
				customMockLiferayPortletActionResponse);

			Layout layout =
				(Layout)mockLiferayPortletActionRequest.getAttribute(
					WebKeys.LAYOUT);

			String redirectLocation =
				customMockLiferayPortletActionResponse.getRedirectLocation();

			Assert.assertTrue(
				redirectLocation.contains(layout.getFriendlyURL()));
			_assertParameter(
				redirectLocation, "p_p_id", LoginPortletKeys.LOGIN);
			_assertParameter(redirectLocation, "p_p_lifecycle", "0");
			_assertParameter(redirectLocation, "p_p_state", "maximized");
			_assertParameter(redirectLocation, "saveLastPath", "false");
		}
	}

	private void _addCookieSupportCookie(
		MockHttpServletRequest mockHttpServletRequest) {

		mockHttpServletRequest.setCookies(
			new Cookie(CookiesConstants.NAME_COOKIE_SUPPORT, "true"));
	}

	private void _assertParameter(
		String url, String parameterName, String parameterValue) {

		Assert.assertTrue(url.contains(parameterName + "=" + parameterValue));
	}

	private LiferayPortletConfig _getLiferayPortletConfig() {
		Portlet portlet = _portletLocalService.getPortletById(
			LoginPortletKeys.LOGIN);

		return (LiferayPortletConfig)PortletConfigFactoryUtil.create(
			portlet, null);
	}

	private MockLiferayPortletActionRequest
			_getMockLiferayPortletActionRequest()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest() {

				@Override
				public Portlet getPortlet() {
					return _portletLocalService.getPortletById(
						LoginPortletKeys.LOGIN);
				}

				@Override
				public String getPortletName() {
					return LoginPortletKeys.LOGIN;
				}

				{
					Portlet portlet = getPortlet();

					PortletApp portletApp = portlet.getPortletApp();

					portletApp.setSpecMajorVersion(2);
				}
			};

		_addCookieSupportCookie(
			(MockHttpServletRequest)
				mockLiferayPortletActionRequest.getHttpServletRequest());

		mockLiferayPortletActionRequest.addParameter(
			"login", "test@liferay.com");
		mockLiferayPortletActionRequest.addParameter(
			"password", "wrongpassword");
		mockLiferayPortletActionRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG, _getLiferayPortletConfig());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		ThemeDisplay themeDisplay = _getThemeDisplay();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.LAYOUT, themeDisplay.getLayout());
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.fetchCompany(
				TestPropsValues.getCompanyId()));

		Layout layout = _layoutLocalService.getLayout(
			TestPropsValues.getPlid());

		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());

		themeDisplay.setPlid(TestPropsValues.getPlid());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(
			_userLocalService.getGuestUser(_group.getCompanyId()));

		return themeDisplay;
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject(
		filter = "mvc.command.name=/login/login", type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private PortletLocalService _portletLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private UserLocalService _userLocalService;

	private class CustomMockLiferayPortletActionResponse
		extends MockLiferayPortletActionResponse {

		public String getRedirectLocation() {
			return _redirectLocation;
		}

		@Override
		public void sendRedirect(String location) {
			_redirectLocation = location;
		}

		private String _redirectLocation;

	}

}
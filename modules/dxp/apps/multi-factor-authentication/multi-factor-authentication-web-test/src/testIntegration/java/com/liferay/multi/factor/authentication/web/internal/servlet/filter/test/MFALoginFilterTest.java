/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.web.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.multi.factor.authentication.spi.checker.headless.HeadlessMFAChecker;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class MFALoginFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_company = _companyLocalService.getCompany(_group.getCompanyId());

		_password = StringUtil.toLowerCase(RandomTestUtil.randomString());

		_user = UserTestUtil.addUser(_company);

		_user = _userLocalService.updatePassword(
			_user.getUserId(), _password, _password, false, true);
	}

	@Test
	public void testDoFilter() throws Exception {
		_testDoFilterDoesNotRedirectToVerify(_getMockHttpServletRequest());
		_testDoFilterDoesNotRedirectToVerifyWhenMFAIsSatisfied();
		_testDoFilterDoesNotRedirectToVerifyWhenPasswordIsIncorrect();
		_testDoFilterDoesNotRedirectToVerifyWithoutPostedCredentials();
		_testDoFilterRedirectsToVerifyWhenMFAIsNotSatisfied();
	}

	private MockHttpServletRequest _getMockHttpServletRequest()
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest("POST", "/c/portal/login");

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _company.getCompanyId());
		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());
		mockHttpServletRequest.setParameter(
			"p_auth", AuthTokenUtil.getToken(mockHttpServletRequest));
		mockHttpServletRequest.setParameter("login", _user.getEmailAddress());
		mockHttpServletRequest.setParameter("password", _password);

		return mockHttpServletRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setLayout(LayoutTestUtil.addTypeContentLayout(_group));
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRealUser(TestPropsValues.getUser());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		return themeDisplay;
	}

	private ServiceRegistration<HeadlessMFAChecker> _registerHeadlessMFAChecker(
		HeadlessMFAChecker headlessMFAChecker) {

		Bundle bundle = FrameworkUtil.getBundle(MFALoginFilterTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.registerService(
			HeadlessMFAChecker.class, headlessMFAChecker,
			MapUtil.singletonDictionary("companyId", _company.getCompanyId()));
	}

	private void _testDoFilterDoesNotRedirectToVerify(
			MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		RecordingFilterChain recordingFilterChain = new RecordingFilterChain();

		_mfaLoginFilter.doFilter(
			mockHttpServletRequest, new MockHttpServletResponse(),
			recordingFilterChain);

		Assert.assertTrue(recordingFilterChain._isDoFilterCalled());
	}

	private void _testDoFilterDoesNotRedirectToVerifyWhenMFAIsSatisfied()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						"com.liferay.multi.factor.authentication.email.otp." +
							"configuration.MFAEmailOTPConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			ServiceRegistration<HeadlessMFAChecker> serviceRegistration =
				_registerHeadlessMFAChecker(
					(httpServletRequest, userId) -> true);

			try {
				_testDoFilterDoesNotRedirectToVerify(
					_getMockHttpServletRequest());
			}
			finally {
				serviceRegistration.unregister();
			}
		}
	}

	private void _testDoFilterDoesNotRedirectToVerifyWhenPasswordIsIncorrect()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						"com.liferay.multi.factor.authentication.email.otp." +
							"configuration.MFAEmailOTPConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest();

			mockHttpServletRequest.setParameter(
				"password", RandomTestUtil.randomString());

			_testDoFilterDoesNotRedirectToVerify(mockHttpServletRequest);
		}
	}

	private void _testDoFilterDoesNotRedirectToVerifyWithoutPostedCredentials()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						"com.liferay.multi.factor.authentication.email.otp." +
							"configuration.MFAEmailOTPConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			MockHttpServletRequest mockHttpServletRequest =
				_getMockHttpServletRequest();

			mockHttpServletRequest.removeParameter("login");
			mockHttpServletRequest.removeParameter("password");

			_testDoFilterDoesNotRedirectToVerify(mockHttpServletRequest);

			mockHttpServletRequest = _getMockHttpServletRequest();

			mockHttpServletRequest.setQueryString(
				"password=" + RandomTestUtil.randomString());

			_testDoFilterDoesNotRedirectToVerify(mockHttpServletRequest);
		}
	}

	private void _testDoFilterRedirectsToVerifyWhenMFAIsNotSatisfied()
		throws Exception {

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						_company.getCompanyId(),
						"com.liferay.multi.factor.authentication.email.otp." +
							"configuration.MFAEmailOTPConfiguration",
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			ServiceRegistration<HeadlessMFAChecker> serviceRegistration =
				_registerHeadlessMFAChecker(
					(httpServletRequest, userId) -> false);

			try {
				MockHttpServletRequest mockHttpServletRequest =
					_getMockHttpServletRequest();
				MockHttpServletResponse mockHttpServletResponse =
					new MockHttpServletResponse();
				RecordingFilterChain recordingFilterChain =
					new RecordingFilterChain();

				_mfaLoginFilter.doFilter(
					mockHttpServletRequest, mockHttpServletResponse,
					recordingFilterChain);

				Assert.assertFalse(recordingFilterChain._isDoFilterCalled());

				String redirectedURL =
					mockHttpServletResponse.getRedirectedUrl();

				Assert.assertTrue(
					redirectedURL.contains(
						"com_liferay_multi_factor_authentication_web_portlet_" +
							"MFAVerifyPortlet"));

				HttpSession httpSession = mockHttpServletRequest.getSession();

				Assert.assertEquals(
					_user.getUserId(),
					GetterUtil.getLong(
						httpSession.getAttribute("MFA_USER_ID")));
				Assert.assertNotNull(
					httpSession.getAttribute("MFA_WEB_DIGEST"));
				Assert.assertNotNull(httpSession.getAttribute("MFA_WEB_KEY"));
			}
			finally {
				serviceRegistration.unregister();
			}
		}
	}

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.multi.factor.authentication.web.internal.servlet.filter.MFALoginFilter"
	)
	private Filter _mfaLoginFilter;

	private String _password;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	private class RecordingFilterChain implements FilterChain {

		@Override
		public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse) {

			_doFilterCalled = true;
		}

		private boolean _isDoFilterCalled() {
			return _doFilterCalled;
		}

		private boolean _doFilterCalled;

	}

}
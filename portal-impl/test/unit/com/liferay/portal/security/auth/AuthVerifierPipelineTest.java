/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifier;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierConfiguration;
import com.liferay.portal.kernel.security.auth.verifier.AuthVerifierResult;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.security.auth.registry.AuthVerifierRegistry;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Peter Fellwock
 */
public class AuthVerifierPipelineTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpAuthVerifier();
		_setUpAuthVerifierConfiguration();
		_setUpAuthVerifierRegistry();
		_setUpCompanyLocalServiceUtil();
		_setUpPortalUtil();
		_setUpUserLocalServiceUtil();
	}

	@After
	public void tearDown() {
		_authVerifierRegistryMockedStatic.close();
		_companyLocalServiceUtilMockedStatic.close();
		_userLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testVerifyRequest() throws PortalException {
		String contextPath = "";
		String includeURLs = StringBundler.concat(
			_BASE_URL, "/regular/*,", _BASE_URL, "/legacy*");

		String legacyRequestURI = contextPath + _BASE_URL + "/legacy/Hello";
		String regularRequestURI = contextPath + _BASE_URL + "/regular/Hello";

		AuthVerifierResult.State expectedState =
			AuthVerifierResult.State.SUCCESS;

		_assertAuthVerifierResult(
			contextPath, includeURLs, legacyRequestURI, expectedState);
		_assertAuthVerifierResult(
			contextPath, includeURLs, regularRequestURI, expectedState);
	}

	@Test
	public void testVerifyRequestWithContextPath() throws PortalException {
		String contextPath = "/abc";
		String includeURLs = StringBundler.concat(
			_BASE_URL, "/regular/*,", _BASE_URL, "/legacy*");

		String requestURI = contextPath + _BASE_URL + "/regular/Hello";

		AuthVerifierResult.State expectedState =
			AuthVerifierResult.State.SUCCESS;

		_assertAuthVerifierResult(
			contextPath, includeURLs, requestURI, expectedState);
	}

	@Test
	public void testVerifyRequestWithContextPathNotAffectedByPortalProxyPath()
		throws PortalException {

		String contextPath = "/abc";
		String includeURLs = StringBundler.concat(
			_BASE_URL, "/regular/*,", _BASE_URL, "/legacy*");

		String requestURI = contextPath + _BASE_URL + "/regular/Hello";

		AuthVerifierResult.State expectedState =
			AuthVerifierResult.State.SUCCESS;

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"PORTAL_PROXY_PATH", "/proxy")) {

			_setUpPortalUtil();

			_assertAuthVerifierResult(
				contextPath, includeURLs, requestURI, expectedState);
		}
	}

	@Test
	public void testVerifyRequestWithInactiveUser() throws PortalException {
		_user.setType(UserConstants.TYPE_REGULAR);
		_user.setStatus(WorkflowConstants.STATUS_INACTIVE);

		_assertAuthVerifierResultForUser(
			AuthVerifierResult.State.UNSUCCESSFUL, true);
		_assertAuthVerifierResultForUser(
			AuthVerifierResult.State.UNSUCCESSFUL, false);
	}

	@Test
	public void testVerifyRequestWithIncompleteSetupUser()
		throws PortalException {

		_user.setPasswordReset(false);
		_user.setEmailAddressVerified(false);
		_user.setType(UserConstants.TYPE_REGULAR);

		_assertAuthVerifierResultForUser(
			AuthVerifierResult.State.SUCCESS, true);
		_assertAuthVerifierResultForUser(
			AuthVerifierResult.State.UNSUCCESSFUL, false);

		_user.setPasswordReset(true);
		_user.setEmailAddressVerified(true);

		_assertAuthVerifierResultForUser(
			AuthVerifierResult.State.SUCCESS, true);
		_assertAuthVerifierResultForUser(
			AuthVerifierResult.State.UNSUCCESSFUL, false);
	}

	@Test
	public void testVerifyRequestWithNonmatchingRequestURI()
		throws PortalException {

		String contextPath = "";
		String includeURLs = StringBundler.concat(
			_BASE_URL, "/regular/*,", _BASE_URL, "/legacy*");

		String requestURI = contextPath + _BASE_URL + "/non/matching";

		AuthVerifierResult.State expectedState =
			AuthVerifierResult.State.UNSUCCESSFUL;

		_assertAuthVerifierResult(
			contextPath, includeURLs, requestURI, expectedState);
	}

	private void _assertAuthVerifierResult(
			String contextPath, String includeURLs, String requestURI,
			AuthVerifierResult.State expectedState)
		throws PortalException {

		AuthVerifierResult authVerifierResult = _verifyRequest(
			contextPath, false, includeURLs, requestURI);

		Assert.assertSame(expectedState, authVerifierResult.getState());
	}

	private void _assertAuthVerifierResultForUser(
			AuthVerifierResult.State expectedState, boolean impersonated)
		throws PortalException {

		AuthVerifierResult authVerifierResult = _verifyRequest(
			"", impersonated, _BASE_URL + "/regular/*",
			_BASE_URL + "/regular/Hello");

		Assert.assertSame(expectedState, authVerifierResult.getState());
	}

	private HttpServletRequest _getWrappedHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		return new HttpServletRequestWrapper(httpServletRequest) {

			@Override
			public HttpSession getSession() {
				return _httpSession;
			}

			@Override
			public HttpSession getSession(boolean create) {
				return _httpSession;
			}

			private final MockHttpSession _httpSession = new MockHttpSession();

		};
	}

	private void _setUpAuthVerifier() {
		AuthVerifierResult authVerifierResult = new AuthVerifierResult();

		authVerifierResult.setSettings(new HashMap<>());
		authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);

		_authVerifier = (AuthVerifier)ProxyUtil.newProxyInstance(
			AuthVerifier.class.getClassLoader(),
			new Class<?>[] {AuthVerifier.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "verify")) {
					return authVerifierResult;
				}

				return null;
			});
	}

	private void _setUpAuthVerifierConfiguration() {
		_authVerifierConfiguration = new AuthVerifierConfiguration();

		Class<? extends AuthVerifier> clazz = _authVerifier.getClass();

		_authVerifierConfiguration.setAuthVerifierClassName(clazz.getName());
	}

	private void _setUpAuthVerifierRegistry() {
		Mockito.when(
			AuthVerifierRegistry.getAuthVerifier(
				_authVerifierConfiguration.getAuthVerifierClassName())
		).thenReturn(
			_authVerifier
		);
	}

	private void _setUpCompanyLocalServiceUtil() throws Exception {
		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.isStrangersVerify()
		).thenReturn(
			true
		);

		Mockito.when(
			CompanyLocalServiceUtil.getCompany(Mockito.anyLong())
		).thenReturn(
			company
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(
			new PortalImpl() {

				@Override
				public long getCompanyId(
					HttpServletRequest httpServletRequest) {

					return 0;
				}

			});
	}

	private void _setUpUserLocalServiceUtil() throws Exception {
		_user = new UserImpl();

		_user.setUserId(_DO_AS_USER_ID);
		_user.setStatus(WorkflowConstants.STATUS_APPROVED);

		Mockito.when(
			UserLocalServiceUtil.fetchUser(Mockito.anyLong())
		).thenReturn(
			_user
		);

		Mockito.when(
			UserLocalServiceUtil.getGuestUserId(Mockito.anyLong())
		).thenReturn(
			_user.getUserId()
		);
	}

	private AuthVerifierResult _verifyRequest(
			String contextPath, boolean impersonated, String includeURLs,
			String requestURI)
		throws PortalException {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		ServiceRegistration<AuthVerifier> serviceRegistration =
			bundleContext.registerService(
				AuthVerifier.class, _authVerifier,
				MapUtil.singletonDictionary("urls.includes", includeURLs));

		try {
			Properties properties = new Properties();

			properties.put("urls.includes", includeURLs);

			_authVerifierConfiguration.setProperties(properties);

			AuthVerifierPipeline authVerifierPipeline =
				new AuthVerifierPipeline(
					Collections.singletonList(_authVerifierConfiguration),
					contextPath);

			AccessControlContext accessControlContext =
				new AccessControlContext();

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest(new MockServletContext());

			mockHttpServletRequest.setRequestURI(requestURI);

			if (impersonated) {
				mockHttpServletRequest.setAttribute(
					WebKeys.USER_ID, _DO_AS_USER_ID);

				HttpSession httpSession = mockHttpServletRequest.getSession();

				httpSession.setAttribute(WebKeys.USER_ID, _REAL_USER_ID);

				accessControlContext.setRequest(
					_getWrappedHttpServletRequest(mockHttpServletRequest));
			}
			else {
				accessControlContext.setRequest(mockHttpServletRequest);
			}

			return authVerifierPipeline.verifyRequest(accessControlContext);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	private static final String _BASE_URL = "/TestAuthVerifier";

	private static final long _DO_AS_USER_ID = RandomTestUtil.randomLong();

	private static final long _REAL_USER_ID = _DO_AS_USER_ID + 1;

	private AuthVerifier _authVerifier;
	private AuthVerifierConfiguration _authVerifierConfiguration;
	private final MockedStatic<AuthVerifierRegistry>
		_authVerifierRegistryMockedStatic = Mockito.mockStatic(
			AuthVerifierRegistry.class);
	private final MockedStatic<CompanyLocalServiceUtil>
		_companyLocalServiceUtilMockedStatic = Mockito.mockStatic(
			CompanyLocalServiceUtil.class);
	private User _user;
	private final MockedStatic<UserLocalServiceUtil>
		_userLocalServiceUtilMockedStatic = Mockito.mockStatic(
			UserLocalServiceUtil.class);

}
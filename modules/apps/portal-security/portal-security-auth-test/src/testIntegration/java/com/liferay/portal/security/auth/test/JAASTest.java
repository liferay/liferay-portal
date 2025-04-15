/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.jaas.JAASHelper;
import com.liferay.portal.servlet.filters.absoluteredirects.AbsoluteRedirectsResponse;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class JAASTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		_originalPortalJAASEnable = ReflectionTestUtil.getAndSetFieldValue(
			PropsValues.class, "PORTAL_JAAS_ENABLE", true);

		Configuration.setConfiguration(new JAASConfiguration());
	}

	@AfterClass
	public static void tearDownClass() {
		Configuration.setConfiguration(null);

		ReflectionTestUtil.setFieldValue(
			PropsValues.class, "PORTAL_JAAS_ENABLE", _originalPortalJAASEnable);
	}

	@Before
	public void setUp() throws Exception {
		_user = TestPropsValues.getUser();
	}

	@Test
	public void testGetUser() throws Exception {
		final IntegerWrapper counter = new IntegerWrapper();

		JAASHelper jaasHelper = JAASHelper.getInstance();

		JAASHelper.setInstance(
			new JAASHelper() {

				@Override
				protected long doGetJAASUserId(long companyId, String name)
					throws PortalException {

					try {
						return super.doGetJAASUserId(companyId, name);
					}
					finally {
						counter.increment();
					}
				}

			});

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"PORTAL_JAAS_AUTH_TYPE", "screenName")) {

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest(
					ServletContextPool.get(StringPool.BLANK), HttpMethods.GET,
					StringPool.SLASH);

			mockHttpServletRequest.setAttribute(
				WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
			mockHttpServletRequest.setRemoteUser(
				String.valueOf(_user.getScreenName()));

			try {
				User user = PortalUtil.getUser(mockHttpServletRequest);

				Assert.assertEquals(1, counter.getValue());
				Assert.assertEquals(_user.getUserId(), user.getUserId());

				user = PortalUtil.getUser(mockHttpServletRequest);

				Assert.assertEquals(1, counter.getValue());
				Assert.assertEquals(_user.getUserId(), user.getUserId());
			}
			finally {
				JAASHelper.setInstance(jaasHelper);
			}
		}
	}

	@Test
	public void testLoginEmailAddressWithScreenName() throws Exception {
		_testLoginFail(_user.getEmailAddress(), "screenName");
	}

	@Test
	public void testLoginEmailAddressWithUserId() throws Exception {
		_testLoginFail(_user.getEmailAddress(), "userId");
	}

	@Test
	public void testLoginScreenNameWithEmailAddress() throws Exception {
		_testLoginFail(_user.getScreenName(), "emailAddress");
	}

	@Test
	public void testLoginScreenNameWithLogin() throws Exception {
		_testLoginFail(_user.getScreenName(), "login");
	}

	@Test
	public void testLoginScreenNameWithUserId() throws Exception {
		_testLoginFail(_user.getScreenName(), "userId");
	}

	@Test
	public void testLoginUserIdWithEmailAddress() throws Exception {
		_testLoginFail(String.valueOf(_user.getUserId()), "emailAddress");
	}

	@Test
	public void testLoginUserIdWithLogin() throws Exception {
		_testLoginFail(String.valueOf(_user.getUserId()), "login");
	}

	@Test
	public void testLoginUserIdWithScreenName() throws Exception {
		_testLoginFail(String.valueOf(_user.getUserId()), "screenName");
	}

	@Test
	public void testProcessLoginEvents() throws Exception {
		Date lastLoginDate = _user.getLastLoginDate();

		ServletContext servletContext = ServletContextPool.get(
			StringPool.BLANK);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(
				servletContext, HttpMethods.GET, StringPool.SLASH);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletRequest.setRemoteUser(String.valueOf(_user.getUserId()));
		mockHttpServletRequest.setAttribute(
			AbsoluteRedirectsResponse.class.getName(), new Object());
		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		JAASAction preJAASAction = new JAASAction();
		JAASAction postJAASAction = new JAASAction();

		ServiceRegistration<?> serviceRegistration1 =
			bundleContext.registerService(
				LifecycleAction.class, preJAASAction,
				MapUtil.singletonDictionary("key", PropsKeys.LOGIN_EVENTS_PRE));
		ServiceRegistration<?> serviceRegistration2 =
			bundleContext.registerService(
				LifecycleAction.class, postJAASAction,
				MapUtil.singletonDictionary(
					"key", PropsKeys.LOGIN_EVENTS_POST));

		try {
			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher("/c");

			requestDispatcher.include(
				mockHttpServletRequest, mockHttpServletResponse);

			Assert.assertTrue(preJAASAction.isRan());
			Assert.assertTrue(postJAASAction.isRan());

			_user = _userLocalService.getUser(_user.getUserId());

			Assert.assertFalse(lastLoginDate.after(_user.getLastLoginDate()));
		}
		finally {
			serviceRegistration1.unregister();
			serviceRegistration2.unregister();
		}
	}

	private LoginContext _getLoginContext(String name, String password)
		throws Exception {

		return new LoginContext(
			"PortalRealm", new JAASCallbackHandler(name, password));
	}

	private void _testLoginFail(String name, String authType) throws Exception {
		LoginContext loginContext = _getLoginContext(name, _user.getPassword());

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"PORTAL_JAAS_AUTH_TYPE", authType)) {

			loginContext.login();

			Assert.fail();
		}
		catch (Exception exception) {
		}
	}

	private static Boolean _originalPortalJAASEnable;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	private static class JAASAction extends Action {

		public boolean isRan() {
			return _ran;
		}

		@Override
		public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			_ran = true;
		}

		private boolean _ran;

	}

	private static class JAASCallbackHandler implements CallbackHandler {

		public JAASCallbackHandler(String name, String password) {
			_name = name;
			_password = password;
		}

		@Override
		public void handle(Callback[] callbacks)
			throws UnsupportedCallbackException {

			for (Callback callback : callbacks) {
				if (callback instanceof NameCallback) {
					NameCallback nameCallback = (NameCallback)callback;

					nameCallback.setName(_name);
				}
				else if (callback instanceof PasswordCallback) {
					String password = GetterUtil.getString(_password);

					PasswordCallback passwordCallback =
						(PasswordCallback)callback;

					passwordCallback.setPassword(password.toCharArray());
				}
				else {
					throw new UnsupportedCallbackException(callback);
				}
			}
		}

		private final String _name;
		private final String _password;

	}

	private static class JAASConfiguration extends Configuration {

		@Override
		public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
			AppConfigurationEntry[] appConfigurationEntries =
				new AppConfigurationEntry[1];

			appConfigurationEntries[0] = new AppConfigurationEntry(
				"com.liferay.portal.kernel.security.jaas.PortalLoginModule",
				LoginModuleControlFlag.REQUIRED,
				HashMapBuilder.<String, Object>put(
					"debug", Boolean.TRUE
				).build());

			return appConfigurationEntries;
		}

	}

}
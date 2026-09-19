/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.AuthFailure;
import com.liferay.portal.kernel.security.auth.Authenticator;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.security.auth.AuthPipeline;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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

/**
 * @author Philip Jones
 */
@RunWith(Arquillian.class)
public class AuthPipelineTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(AuthPipelineTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_authFailureServiceRegistration = bundleContext.registerService(
			AuthFailure.class,
			(AuthFailure)ProxyUtil.newProxyInstance(
				AuthFailure.class.getClassLoader(),
				new Class<?>[] {AuthFailure.class},
				(proxy, method, args) -> {
					_calledAuthFailure = true;

					return null;
				}),
			HashMapDictionaryBuilder.<String, Object>put(
				"key",
				new String[] {
					PropsKeys.AUTH_FAILURE, PropsKeys.AUTH_MAX_FAILURES
				}
			).put(
				"service.ranking", Integer.MAX_VALUE
			).build());

		_authenticatorServiceRegistration = bundleContext.registerService(
			Authenticator.class,
			(Authenticator)ProxyUtil.newProxyInstance(
				Authenticator.class.getClassLoader(),
				new Class<?>[] {Authenticator.class},
				(proxy, method, args) -> {
					_calledAuthenticator = true;

					return Authenticator.SUCCESS;
				}),
			HashMapDictionaryBuilder.<String, Object>put(
				"key", PropsKeys.AUTH_PIPELINE_PRE
			).put(
				"service.ranking", Integer.MAX_VALUE
			).build());
	}

	@AfterClass
	public static void tearDownClass() {
		_authFailureServiceRegistration.unregister();
		_authenticatorServiceRegistration.unregister();
	}

	@Before
	public void setUp() {
		_calledAuthenticator = false;
		_calledAuthFailure = false;
	}

	@Test
	public void testAuthenticateByEmailAddress() throws AuthException {
		AuthPipeline.authenticateByEmailAddress(
			PropsKeys.AUTH_PIPELINE_PRE, 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null);

		Assert.assertTrue(_calledAuthenticator);
	}

	@Test
	public void testAuthenticateByScreenName() throws AuthException {
		AuthPipeline.authenticateByScreenName(
			PropsKeys.AUTH_PIPELINE_PRE, 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null);

		Assert.assertTrue(_calledAuthenticator);
	}

	@Test
	public void testAuthenticateByUserId() throws AuthException {
		AuthPipeline.authenticateByUserId(
			PropsKeys.AUTH_PIPELINE_PRE, 0, RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), null, null);

		Assert.assertTrue(_calledAuthenticator);
	}

	@Test
	public void testOnFailureByScreenName() {
		try {
			AuthPipeline.onFailureByScreenName(
				PropsKeys.AUTH_FAILURE, 0, RandomTestUtil.randomString(), null,
				null);
		}
		catch (AuthException authException) {
			if (_log.isDebugEnabled()) {
				_log.debug(authException);
			}
		}

		Assert.assertTrue(_calledAuthFailure);
	}

	@Test
	public void testOnFailureByUserId() {
		try {
			AuthPipeline.onFailureByUserId(
				PropsKeys.AUTH_FAILURE, 0, RandomTestUtil.randomLong(), null,
				null);
		}
		catch (AuthException authException) {
			if (_log.isDebugEnabled()) {
				_log.debug(authException);
			}
		}

		Assert.assertTrue(_calledAuthFailure);
	}

	@Test
	public void testOnMaxFailuresByEmailAddress() {
		try {
			AuthPipeline.onMaxFailuresByEmailAddress(
				PropsKeys.AUTH_MAX_FAILURES, 0, RandomTestUtil.randomString(),
				null, null);
		}
		catch (AuthException authException) {
			if (_log.isDebugEnabled()) {
				_log.debug(authException);
			}
		}

		Assert.assertTrue(_calledAuthFailure);
	}

	@Test
	public void testOnMaxFailuresByScreenName() {
		try {
			AuthPipeline.onMaxFailuresByScreenName(
				PropsKeys.AUTH_MAX_FAILURES, 0, RandomTestUtil.randomString(),
				null, null);
		}
		catch (AuthException authException) {
			if (_log.isDebugEnabled()) {
				_log.debug(authException);
			}
		}

		Assert.assertTrue(_calledAuthFailure);
	}

	@Test
	public void testOnMaxFailuresByUserId() {
		try {
			AuthPipeline.onMaxFailuresByUserId(
				PropsKeys.AUTH_MAX_FAILURES, 0, RandomTestUtil.randomLong(),
				null, null);
		}
		catch (AuthException authException) {
			if (_log.isDebugEnabled()) {
				_log.debug(authException);
			}
		}

		Assert.assertTrue(_calledAuthFailure);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuthPipelineTest.class);

	private static ServiceRegistration<AuthFailure>
		_authFailureServiceRegistration;
	private static ServiceRegistration<Authenticator>
		_authenticatorServiceRegistration;
	private static boolean _calledAuthFailure;
	private static boolean _calledAuthenticator;

}
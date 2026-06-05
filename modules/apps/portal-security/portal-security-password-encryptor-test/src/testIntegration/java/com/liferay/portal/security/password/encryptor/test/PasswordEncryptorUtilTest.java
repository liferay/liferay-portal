/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.password.encryptor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptorUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.InvocationTargetException;

import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.util.promise.Promise;

/**
 * @author Caio Farias
 */
@RunWith(Arquillian.class)
public class PasswordEncryptorUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testEncrypt() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Bundle bundle = _getBundle();

			bundle.stop();

			bundle.start();

			_assertIsComponentEnabled(
				_CLASS_NAME_BCRYPT_PASSWORD_ENCRYPTOR, false);
			_assertIsComponentEnabled(
				_CLASS_NAME_CRYPT_PASSWORD_ENCRYPTOR, false);

			Assert.assertThrows(
				SecurityException.class,
				() -> _testEncrypt(PasswordEncryptor.TYPE_BCRYPT));
			Assert.assertThrows(
				SecurityException.class,
				() -> _testEncrypt(PasswordEncryptor.TYPE_UFC_CRYPT));
		}
		finally {
			_enableComponent(_CLASS_NAME_BCRYPT_PASSWORD_ENCRYPTOR);
			_enableComponent(_CLASS_NAME_CRYPT_PASSWORD_ENCRYPTOR);
		}

		_assertIsComponentEnabled(_CLASS_NAME_BCRYPT_PASSWORD_ENCRYPTOR, true);
		_assertIsComponentEnabled(_CLASS_NAME_CRYPT_PASSWORD_ENCRYPTOR, true);

		_testEncrypt(PasswordEncryptor.TYPE_BCRYPT);
		_testEncrypt(PasswordEncryptor.TYPE_UFC_CRYPT);
	}

	private void _assertIsComponentEnabled(
		String componentName, boolean expected) {

		Assert.assertEquals(
			expected,
			_serviceComponentRuntime.isComponentEnabled(
				_serviceComponentRuntime.getComponentDescriptionDTO(
					_getBundle(), componentName)));
	}

	private void _enableComponent(String componentName)
		throws InterruptedException, InvocationTargetException {

		Promise<Void> promise = _serviceComponentRuntime.enableComponent(
			_serviceComponentRuntime.getComponentDescriptionDTO(
				_getBundle(), componentName));

		promise.getValue();
	}

	private Bundle _getBundle() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			if (Objects.equals(
					bundle.getSymbolicName(),
					"com.liferay.portal.security.password.encryptor.impl")) {

				return bundle;
			}
		}

		return null;
	}

	private void _testEncrypt(String algorithm) throws PwdEncryptorException {
		String password = RandomTestUtil.randomString();

		String encryptedPassword = PasswordEncryptorUtil.encrypt(
			algorithm, password, null);

		Assert.assertEquals(
			encryptedPassword,
			PasswordEncryptorUtil.encrypt(
				algorithm, password, encryptedPassword));
	}

	private static final String _CLASS_NAME_BCRYPT_PASSWORD_ENCRYPTOR =
		"com.liferay.portal.security.password.encryptor.internal." +
			"BCryptPasswordEncryptor";

	private static final String _CLASS_NAME_CRYPT_PASSWORD_ENCRYPTOR =
		"com.liferay.portal.security.password.encryptor.internal." +
			"CryptPasswordEncryptor";

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.lang.reflect.Method;

import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;

import java.util.Objects;

/**
 * Re-verifies the validated cryptographic provider on demand. BCFIPS and Amazon
 * Corretto are driven reflectively so portal-kernel keeps no compile-time
 * dependency on the provider jars.
 *
 * @author Lucas Miranda
 */
public class ReflectionFIPSSelfTestExecutor implements FIPSSelfTestExecutor {

	@Override
	public String execute() throws Exception {
		Provider[] providers = Security.getProviders();

		if (ArrayUtil.isEmpty(providers)) {
			throw new FIPSSelfTestException(
				null, "provider-presence", null,
				"There are no security providers");
		}

		Provider provider = providers[0];

		String name = provider.getName();

		if (Objects.equals(name, "AmazonCorrettoCryptoProvider")) {
			_reverifyAmazonCorretto(provider);
		}
		else if (Objects.equals(name, "BCFIPS")) {
			_reverifyBCFIPS(provider);
		}
		else {
			throw new FIPSSelfTestException(
				name, "provider-identity", null,
				"The security provider is not an allowed FIPS provider");
		}

		return name;
	}

	private void _reverifyAmazonCorretto(Provider provider) throws Exception {
		try {
			Class<?> providerClass = provider.getClass();

			Method assertHealthyMethod = ReflectionUtil.getDeclaredMethod(
				providerClass, "assertHealthy");

			assertHealthyMethod.invoke(provider);
		}
		catch (Exception exception) {
			throw new FIPSSelfTestException(
				"AmazonCorrettoCryptoProvider", "assertHealthy", null,
				_rootMessage(exception), exception);
		}
	}

	private void _reverifyBCFIPS(Provider provider) throws Exception {
		try {
			ClassLoader classLoader = provider.getClass(
			).getClassLoader();

			Class<?> cryptoServicesRegistrarClass = Class.forName(
				"org.bouncycastle.crypto.CryptoServicesRegistrar", true,
				classLoader);

			MessageDigest messageDigest = MessageDigest.getInstance(
				"SHA-256", provider);

			messageDigest.digest();

			Method method = ReflectionUtil.getDeclaredMethod(
				cryptoServicesRegistrarClass, "isInApprovedOnlyMode");

			if (!GetterUtil.getBoolean(method.invoke(null))) {
				throw new FIPSSelfTestException(
					"BCFIPS", "approved-only-mode", "NOT_APPROVED",
					"BCFIPS is not in approved only mode");
			}

			Class<?> fipsStatusClass = Class.forName(
				"org.bouncycastle.crypto.fips.FipsStatus", true, classLoader);

			method = ReflectionUtil.getDeclaredMethod(
				fipsStatusClass, "isReady");

			if (!GetterUtil.getBoolean(method.invoke(null))) {
				method = ReflectionUtil.getDeclaredMethod(
					fipsStatusClass, "getStatusMessage");

				throw new FIPSSelfTestException(
					"BCFIPS", "integrity-self-test",
					String.valueOf(method.invoke(null)),
					"BCFIPS integrity self test failed");
			}
		}
		catch (FIPSSelfTestException fipsSelfTestException) {
			throw fipsSelfTestException;
		}
		catch (Exception exception) {
			throw new FIPSSelfTestException(
				"BCFIPS", "self-test-invocation", null, _rootMessage(exception),
				exception);
		}
	}

	private String _rootMessage(Throwable throwable) {
		Throwable causeThrowable = throwable.getCause();

		if (causeThrowable == null) {
			causeThrowable = throwable;
		}

		String message = causeThrowable.getMessage();

		if (message == null) {
			return causeThrowable.toString();
		}

		return message;
	}

}
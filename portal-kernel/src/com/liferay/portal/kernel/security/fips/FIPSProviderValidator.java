/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;

import java.lang.reflect.Method;

import java.security.Provider;
import java.security.Security;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Caio Farias
 */
public class FIPSProviderValidator {

	public static void validate() {
		Provider[] providers = Security.getProviders();

		_validateFIPSProvider(providers);
		_validateProviders(providers);

		if (_log.isInfoEnabled()) {
			_log.info("FIPS provider validation finished successfully");
		}
	}

	private static void _validateFIPSProvider(Provider[] providers) {
		if (ArrayUtil.isEmpty(providers)) {
			throw new SecurityException("There are no providers registered");
		}

		Provider provider = providers[0];

		String name = provider.getName();

		if (!_allowedProviders.containsKey(name)) {
			throw new SecurityException(
				"The first provider must be an allowed FIPS provider");
		}

		try {
			if (Objects.equals(name, "AmazonCorrettoCryptoProvider")) {
				Class<?> providerClass = provider.getClass();

				Method assertHealthyMethod = ReflectionUtil.getDeclaredMethod(
					providerClass, "assertHealthy");

				assertHealthyMethod.invoke(provider);

				Method isExperimentalFipsMethod =
					ReflectionUtil.getDeclaredMethod(
						providerClass, "isExperimentalFips");
				Method isFipsMethod = ReflectionUtil.getDeclaredMethod(
					providerClass, "isFips");

				if (!GetterUtil.getBoolean(
						isExperimentalFipsMethod.invoke(provider)) ||
					GetterUtil.getBoolean(isFipsMethod.invoke(provider))) {

					return;
				}

				throw new SecurityException(
					"AmazonCorrettoCryptoProvider must be a nonexperimental " +
						"FIPS build");
			}
			else if (Objects.equals(name, "BCFIPS")) {
				Class<?> providerClass = provider.getClass();

				ClassLoader classLoader = providerClass.getClassLoader();

				Method isInApprovedOnlyModeMethod =
					ReflectionUtil.getDeclaredMethod(
						Class.forName(
							"org.bouncycastle.crypto.CryptoServicesRegistrar",
							true, classLoader),
						"isInApprovedOnlyMode");

				if (!GetterUtil.getBoolean(
						isInApprovedOnlyModeMethod.invoke(null))) {

					throw new SecurityException(
						"BCFIPS is not in approved only mode");
				}

				Class<?> fipsStatusClass = Class.forName(
					"org.bouncycastle.crypto.fips.FipsStatus", true,
					classLoader);

				Method isReadyMethod = ReflectionUtil.getDeclaredMethod(
					fipsStatusClass, "isReady");

				if (!GetterUtil.getBoolean(isReadyMethod.invoke(null))) {
					Method getStatusMessageMethod =
						ReflectionUtil.getDeclaredMethod(
							fipsStatusClass, "getStatusMessage");

					throw new SecurityException(
						"BCFIPS integrity self test failed: " +
							getStatusMessageMethod.invoke(null));
				}
			}
		}
		catch (SecurityException securityException) {
			throw securityException;
		}
		catch (Throwable throwable) {
			Throwable causeThrowable = throwable.getCause();

			if (causeThrowable == null) {
				causeThrowable = throwable;
			}

			String message = causeThrowable.getMessage();

			if (message == null) {
				message = causeThrowable.toString();
			}

			throw new SecurityException(
				"FIPS provider integrity failed: " + message, causeThrowable);
		}
	}

	private static void _validateProviders(Provider[] providers) {
		Provider fipsProvider = providers[0];

		List<String> allowedProviders = _allowedProviders.get(
			fipsProvider.getName());

		Provider[] notAllowedProviders = ArrayUtil.filter(
			providers,
			provider ->
				!provider.equals(fipsProvider) &&
				!allowedProviders.contains(provider.getName()));

		if (ArrayUtil.isEmpty(notAllowedProviders)) {
			if (_log.isInfoEnabled()) {
				_log.info("All registered providers are allowed for FIPS mode");
			}

			return;
		}

		throw new SecurityException(
			StringBundler.concat(
				"The providers ", Arrays.toString(notAllowedProviders),
				" are not allowed in FIPS mode for ", fipsProvider.getName()));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FIPSProviderValidator.class);

	private static final Map<String, List<String>> _allowedProviders = Map.of(
		"AmazonCorrettoCryptoProvider",
		List.of(
			"JdkLDAP", "JdkSASL", "SUN", "SunEC", "SunJCE", "SunJGSS",
			"SunJSSE", "SunRsaSign", "SunSASL", "XMLDSig"),
		"BCFIPS",
		List.of(
			"BCJSSE", "JdkLDAP", "JdkSASL", "SUN", "SunJCE", "SunJGSS",
			"SunSASL", "XMLDSig"));

}
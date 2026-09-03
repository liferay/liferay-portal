/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.pwd.PasswordEncryptor;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.lang.reflect.Method;

import java.net.URL;

import java.security.Provider;
import java.security.Security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Caio Farias
 */
public class FIPSModeValidator {

	public static Provider fetchProvider() {
		Provider[] providers = Security.getProviders();

		if (ArrayUtil.isEmpty(providers)) {
			return null;
		}

		return providers[0];
	}

	public static String[] getAllowedTLSCipherSuites(String[] tlsCipherSuites) {
		if (!PropsValues.FIPS_ENABLED) {
			return tlsCipherSuites;
		}

		return ArrayUtil.toStringArray(
			SetUtil.intersect(
				_allowedTLSCipherSuites, SetUtil.fromArray(tlsCipherSuites)));
	}

	public static String[] getAllowedTLSProtocols(String[] tlsProtocols) {
		if (!PropsValues.FIPS_ENABLED) {
			return tlsProtocols;
		}

		return ArrayUtil.toStringArray(
			SetUtil.intersect(
				_allowedTLSProtocols, SetUtil.fromArray(tlsProtocols)));
	}

	public static boolean isNotAllowedAlgorithm(String algorithm) {
		if (!PropsValues.FIPS_ENABLED) {
			return false;
		}

		if (Validator.isNull(algorithm)) {
			return true;
		}

		for (String allowedAlgorithm : _allowedAlgorithms) {
			if (StringUtil.startsWith(algorithm, allowedAlgorithm)) {
				return false;
			}
		}

		return true;
	}

	public static void validate() {
		Provider[] providers = Security.getProviders();

		_validateFIPSProvider(providers);
		_validateProviders(providers);

		_validateProperties();
	}

	public static void validateAlgorithm(String algorithm) {
		if (isNotAllowedAlgorithm(algorithm)) {
			throw new SecurityException(
				"Algorithm \"" + algorithm + "\" is not allowed in FIPS mode");
		}
	}

	public static void validateKey(String algorithm, int keySize) {
		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		validateAlgorithm(algorithm);

		if ((keySize != 0) && !_allowedKeySizes.contains(keySize)) {
			throw new SecurityException(
				"AES key must be 128, 192, or 256 bits");
		}
	}

	public static void validateSessionTimeout(int sessionTimeout) {
		if (!PropsValues.FIPS_ENABLED || (sessionTimeout <= 720)) {
			return;
		}

		throw new SecurityException(
			"Session timeout must not be greater than 12 hours in FIPS mode");
	}

	public static void validateURL(String url) {
		if (!PropsValues.FIPS_ENABLED ||
			(Validator.isNotNull(url) &&
			 StringUtil.startsWith(url, "ldaps://"))) {

			return;
		}

		throw new SecurityException(
			"URL protocol scheme is not allowed in FIPS mode");
	}

	private static List<String> _getPlaintextSecretProperties(
		Properties properties) {

		List<String> plaintextSecretProperties = new ArrayList<>();

		for (String key : PropsValues.ADMIN_OBFUSCATED_PROPERTIES) {
			String value = properties.getProperty(key);

			if (Validator.isNull(value)) {
				continue;
			}

			value = value.trim();

			if (value.startsWith("${") && value.endsWith("}")) {
				continue;
			}

			plaintextSecretProperties.add(key);
		}

		return plaintextSecretProperties;
	}

	private static void _validateFIPSProvider(Provider[] providers) {
		if (ArrayUtil.isEmpty(providers)) {
			throw new SecurityException("There are no security providers");
		}

		Provider provider = providers[0];

		String name = provider.getName();

		if (!_allowedProviderNames.containsKey(name)) {
			throw new SecurityException(
				"The first security provider must be an allowed FIPS provider");
		}

		try {
			if (Objects.equals(name, "AmazonCorrettoCryptoProvider")) {
				Class<?> providerClass = provider.getClass();

				Method assertHealthyMethod = ReflectionUtil.getDeclaredMethod(
					providerClass, "assertHealthy");

				assertHealthyMethod.invoke(provider);

				Method isExperimentalFIPSMethod =
					ReflectionUtil.getDeclaredMethod(
						providerClass, "isExperimentalFips");
				Method isFIPSMethod = ReflectionUtil.getDeclaredMethod(
					providerClass, "isFips");

				if (!GetterUtil.getBoolean(
						isExperimentalFIPSMethod.invoke(provider)) &&
					GetterUtil.getBoolean(isFIPSMethod.invoke(provider))) {

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

	private static void _validatePasswordsEncryptionAlgorithm(
		String algorithm) {

		String upperCaseAlgorithm = StringUtil.toUpperCase(
			GetterUtil.getString(algorithm));

		if (!upperCaseAlgorithm.startsWith(PasswordEncryptor.TYPE_PBKDF2) ||
			!upperCaseAlgorithm.contains("SHA256")) {

			throw new SecurityException(
				StringBundler.concat(
					"Algorithm \"", algorithm,
					"\" is not allowed in FIPS mode"));
		}

		int keySize = _PASSWORDS_ENCRYPTION_ALGORITHM_KEY_SIZE_MIN;
		int rounds = _PASSWORDS_ENCRYPTION_ALGORITHM_ROUNDS_MIN;

		Matcher matcher = _pbkdf2Pattern.matcher(upperCaseAlgorithm);

		if (matcher.matches()) {
			keySize = GetterUtil.getInteger(
				matcher.group(1), _PASSWORDS_ENCRYPTION_ALGORITHM_KEY_SIZE_MIN);
			rounds = GetterUtil.getInteger(
				matcher.group(2), _PASSWORDS_ENCRYPTION_ALGORITHM_ROUNDS_MIN);
		}

		if (keySize < _PASSWORDS_ENCRYPTION_ALGORITHM_KEY_SIZE_MIN) {
			throw new SecurityException(
				StringBundler.concat(
					"PBKDF2 output length ", keySize,
					" bits is below the minimum allowed value of ",
					_PASSWORDS_ENCRYPTION_ALGORITHM_KEY_SIZE_MIN, " bits"));
		}

		if (rounds < _PASSWORDS_ENCRYPTION_ALGORITHM_ROUNDS_MIN) {
			throw new SecurityException(
				StringBundler.concat(
					"PBKDF2 iteration count ", rounds,
					" is below the minimum allowed value of ",
					_PASSWORDS_ENCRYPTION_ALGORITHM_ROUNDS_MIN));
		}
	}

	private static void _validatePlaintextSecrets() {
		List<String> messages = new ArrayList<>();

		for (String source : PropsUtil.getLoadedSources()) {
			String fileName = source.substring(source.lastIndexOf('/') + 1);

			if (fileName.equals("portal.properties")) {
				continue;
			}

			Properties properties = null;

			try {
				properties = PropertiesUtil.load(new URL(source));
			}
			catch (IOException ioException) {
				continue;
			}

			for (String key : _getPlaintextSecretProperties(properties)) {
				messages.add(
					StringBundler.concat(
						"property \"", key, "\" in \"", fileName, "\""));
			}
		}

		if (!messages.isEmpty()) {
			throw new SecurityException(
				StringBundler.concat(
					"A plaintext value for ", StringUtil.merge(messages, ", "),
					" is not allowed in FIPS mode"));
		}
	}

	private static void _validateProperties() {
		if (GetterUtil.getBoolean(PropsUtil.get(PropsKeys.AUTH_MAC_ALLOW))) {
			validateAlgorithm(PropsUtil.get(PropsKeys.AUTH_MAC_ALGORITHM));
		}

		validateAlgorithm(
			PropsUtil.get(PropsKeys.COMPANY_ENCRYPTION_ALGORITHM));
		validateAlgorithm(PropsValues.TUNNELING_SERVLET_ENCRYPTION_ALGORITHM);
		_validatePasswordsEncryptionAlgorithm(
			PropsUtil.get(PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM));
		_validatePlaintextSecrets();
		validateSessionTimeout(PropsValues.SESSION_TIMEOUT);
	}

	private static void _validateProviders(Provider[] providers) {
		Provider provider = providers[0];

		List<String> allowedProviderNames = _allowedProviderNames.get(
			provider.getName());

		Provider[] notAllowedProviders = ArrayUtil.filter(
			providers,
			curProvider ->
				!curProvider.equals(provider) &&
				!allowedProviderNames.contains(curProvider.getName()));

		if (ArrayUtil.isEmpty(notAllowedProviders)) {
			return;
		}

		throw new SecurityException(
			StringBundler.concat(
				"The security providers ", Arrays.toString(notAllowedProviders),
				" are not allowed in FIPS mode for ", provider.getName()));
	}

	private static final int _PASSWORDS_ENCRYPTION_ALGORITHM_KEY_SIZE_MIN = 112;

	private static final int _PASSWORDS_ENCRYPTION_ALGORITHM_ROUNDS_MIN =
		1300000;

	private static final Set<String> _allowedAlgorithms = Set.of(
		"AES", "HmacSHA256", "HmacSHA384", "HmacSHA512", "PBKDF2WithHmacSHA256",
		"PBKDF2WithHmacSHA384", "PBKDF2WithHmacSHA512", "SHA-256", "SHA-384",
		"SHA-512");
	private static final Set<Integer> _allowedKeySizes = Set.of(128, 192, 256);
	private static final Map<String, List<String>> _allowedProviderNames =
		Map.of(
			"AmazonCorrettoCryptoProvider",
			List.of(
				"JdkLDAP", "JdkSASL", "SUN", "SunEC", "SunJCE", "SunJGSS",
				"SunJSSE", "SunRsaSign", "SunSASL", "XMLDSig"),
			"BCFIPS",
			List.of(
				"BCJSSE", "JdkLDAP", "JdkSASL", "SUN", "SunJCE", "SunJGSS",
				"SunSASL", "XMLDSig"));
	private static final Set<String> _allowedTLSCipherSuites = Set.of(
		"TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384",
		"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
		"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
		"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
		"TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
	private static final Set<String> _allowedTLSProtocols = Set.of(
		"TLSv1.2", "TLSv1.3");
	private static final Pattern _pbkdf2Pattern = Pattern.compile(
		"^[^/]*(?:/([0-9]+))?/([0-9]+)$");

}
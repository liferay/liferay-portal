/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.security.Provider;
import java.security.Security;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Caio Farias
 */
public class FIPSModeValidatorTest {

	@Test
	public void testGetAllowedTLSCipherSuites() {
		Assert.assertArrayEquals(
			new String[] {"TLS_RSA_WITH_AES_128_CBC_SHA"},
			FIPSModeValidator.getAllowedTLSCipherSuites(
				new String[] {"TLS_RSA_WITH_AES_128_CBC_SHA"}));

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Assert.assertArrayEquals(
				new String[] {"TLS_AES_128_GCM_SHA256"},
				FIPSModeValidator.getAllowedTLSCipherSuites(
					new String[] {
						"TLS_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA"
					}));
		}
	}

	@Test
	public void testGetAllowedTLSProtocols() {
		Assert.assertArrayEquals(
			new String[] {"TLSv1"},
			FIPSModeValidator.getAllowedTLSProtocols(new String[] {"TLSv1"}));

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			Assert.assertArrayEquals(
				new String[] {"TLSv1.3"},
				FIPSModeValidator.getAllowedTLSProtocols(
					new String[] {"TLSv1.3", "TLSv1"}));
		}
	}

	@Test
	public void testGetPlaintextSecretProperties() {
		String obfuscatedKey1 = RandomTestUtil.randomString();
		String obfuscatedKey2 = RandomTestUtil.randomString();
		String obfuscatedKey3 = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"ADMIN_OBFUSCATED_PROPERTIES",
					new String[] {
						obfuscatedKey1, obfuscatedKey2, obfuscatedKey3
					})) {

			Properties properties = new Properties();

			properties.setProperty(
				obfuscatedKey1, RandomTestUtil.randomString());
			properties.setProperty(
				obfuscatedKey2, "${" + RandomTestUtil.randomString() + "}");
			properties.setProperty(obfuscatedKey3, StringPool.BLANK);
			properties.setProperty(
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

			List<String> plaintextSecretProperties = ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_getPlaintextSecretProperties",
				new Class<?>[] {Properties.class}, properties);

			Assert.assertEquals(
				plaintextSecretProperties.toString(), 1,
				plaintextSecretProperties.size());

			Assert.assertTrue(
				plaintextSecretProperties.contains(obfuscatedKey1));
		}
	}

	@Test
	public void testValidateAlgorithm() {
		for (String algorithm : new String[] {"MD5", null}) {
			FIPSModeValidator.validateAlgorithm(algorithm);
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			for (String algorithm : new String[] {"AES", "SHA-256"}) {
				FIPSModeValidator.validateAlgorithm(algorithm);
			}

			for (String algorithm : new String[] {"MD5", "SHA-2", null}) {
				FIPSModeTestUtil.assertSecurityException(
					"is not allowed in FIPS mode",
					() -> FIPSModeValidator.validateAlgorithm(algorithm));
			}
		}
	}

	@Test
	public void testValidateAllowedValues() {
		String key = RandomTestUtil.randomString();

		for (String value :
				new String[] {
					"TLSv1.2", "TLSv1.2,", "TLSv1.2,TLSv1.3", "TLSv1.3"
				}) {

			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_validateAllowedValues",
				new Class<?>[] {Function.class, Map.class},
				(Function<String, String>)curKey -> value,
				Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));
		}

		for (String value :
				new String[] {
					"", ",TLSv1.2", "SSLv3,TLSv1.3", "TLSv1", "TLSv1.1",
					"TLSv1.1,TLSv1.2", "TLSv1.11", "TLSv1.2,SSLv2Hello",
					"tlsv1.2", null
				}) {

			_assertSecurityException(
				"FIPS mode requires the property \"" + key + "\"",
				"_validateAllowedValues",
				new Class<?>[] {Function.class, Map.class},
				(Function<String, String>)curKey -> value,
				Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));
		}
	}

	@Test
	public void testValidateFIPSProvider() {
		_assertSecurityException(
			"FIPS provider integrity failed:", "_validateFIPSProvider",
			new Class<?>[] {Provider[].class},
			(Object)new Provider[] {
				_createProvider("AmazonCorrettoCryptoProvider")
			});

		_assertSecurityException(
			"FIPS provider integrity failed:", "_validateFIPSProvider",
			new Class<?>[] {Provider[].class},
			(Object)new Provider[] {_createProvider("BCFIPS")});

		_assertSecurityException(
			"The first security provider must be an allowed FIPS provider",
			"_validateFIPSProvider", new Class<?>[] {Provider[].class},
			(Object)new Provider[] {
				_createProvider(RandomTestUtil.randomString())
			});

		_assertSecurityException(
			"There are no security providers", "_validateFIPSProvider",
			new Class<?>[] {Provider[].class}, (Object)new Provider[0]);
	}

	@Test
	public void testValidateKey() {
		FIPSModeValidator.validateKey("DES", 64);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			for (int keySize : new int[] {0, 128, 192, 256}) {
				FIPSModeValidator.validateKey("AES", keySize);
			}

			FIPSModeTestUtil.assertSecurityException(
				"Key size 64 is not allowed in FIPS mode",
				() -> FIPSModeValidator.validateKey("AES", 64));

			FIPSModeTestUtil.assertSecurityException(
				"is not allowed in FIPS mode",
				() -> FIPSModeValidator.validateKey("DES", 128));
		}
	}

	@Test
	public void testValidatePasswordsEncryptionAlgorithm() {
		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validatePasswordsEncryptionAlgorithm",
			new Class<?>[] {String.class}, "PBKDF2WithHmacSHA256/256/1300000");

		_assertSecurityException(
			"is not allowed in FIPS mode",
			"_validatePasswordsEncryptionAlgorithm",
			new Class<?>[] {String.class}, "PBKDF2WithHmacSHA1/160/1300000");

		_assertSecurityException(
			"is not allowed in FIPS mode",
			"_validatePasswordsEncryptionAlgorithm",
			new Class<?>[] {String.class}, "bcrypt/10");

		_assertSecurityException(
			"iteration count", "_validatePasswordsEncryptionAlgorithm",
			new Class<?>[] {String.class}, "PBKDF2WithHmacSHA256/256/600000");

		_assertSecurityException(
			"output length", "_validatePasswordsEncryptionAlgorithm",
			new Class<?>[] {String.class}, "PBKDF2WithHmacSHA256/64/1300000");
	}

	@Test
	public void testValidatePortalProperties() {
		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_ENCRYPTION_ALGORITHM", "AES", false);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable("FIPS_ENABLED", true);
			SafeCloseable safeCloseable3 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"PASSWORDS_ENCRYPTION_ALGORITHM",
					"PBKDF2WithHmacSHA256/256/1300000", false);
			SafeCloseable safeCloseable4 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"TUNNELING_SERVLET_ENCRYPTION_ALGORITHM", "AES")) {

			try (SafeCloseable safeCloseable5 =
					PropsValuesTestUtil.swapWithSafeCloseable(
						"TUNNEL_UTIL_VERIFY_SSL_HOSTNAME", "true", false)) {

				ReflectionTestUtil.invoke(
					FIPSModeValidator.class, "_validatePortalProperties",
					new Class<?>[0]);
			}

			try (SafeCloseable safeCloseable5 =
					PropsValuesTestUtil.swapWithSafeCloseable(
						"TUNNEL_UTIL_VERIFY_SSL_HOSTNAME", StringPool.BLANK,
						false)) {

				_assertSecurityException(
					"TLS verification must be enabled in FIPS mode",
					"_validatePortalProperties", new Class<?>[0]);
			}

			try (SafeCloseable safeCloseable5 =
					PropsValuesTestUtil.swapWithSafeCloseable(
						"TUNNEL_UTIL_VERIFY_SSL_HOSTNAME", "false", false)) {

				_assertSecurityException(
					"TLS verification must be enabled in FIPS mode",
					"_validatePortalProperties", new Class<?>[0]);
			}
		}
	}

	@Test
	public void testValidateProviders() {
		Map<String, List<String>> allowedProviderNames =
			ReflectionTestUtil.getFieldValue(
				FIPSModeValidator.class, "_allowedProviderNames");

		for (String allowedProviderName : allowedProviderNames.keySet()) {
			_assertSecurityException(
				"are not allowed in FIPS mode for", "_validateProviders",
				new Class<?>[] {Provider[].class},
				(Object)new Provider[] {
					_createProvider(allowedProviderName),
					_createProvider(RandomTestUtil.randomString())
				});
		}
	}

	@Test
	public void testValidateRequiredValues() {
		String key = RandomTestUtil.randomString();

		for (String[] values :
				new String[][] {
					{"PKIX", "PKIX"}, {"SSLv3, TLSv1", "TLSv1"},
					{"TLSv1.2,TLSv1.3", "TLSv1.2"}, {"TRUE", "true"},
					{"pkix", "PKIX"}, {"true", "true"}
				}) {

			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_validateRequiredValues",
				new Class<?>[] {Function.class, Map.class},
				(Function<String, String>)curKey -> values[0],
				Map.of(key, new String[] {values[1]}));
		}

		for (String[] values :
				new String[][] {
					{"SunPKIXFoo", "PKIX"}, {"TLSv1.1", "TLSv1"},
					{"untrue", "true"}, {null, "true"}
				}) {

			_assertSecurityException(
				"FIPS mode requires the property \"" + key + "\"",
				"_validateRequiredValues",
				new Class<?>[] {Function.class, Map.class},
				(Function<String, String>)curKey -> values[0],
				Map.of(key, new String[] {values[1]}));
		}

		System.setProperty(key, "true");

		try {
			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_validateRequiredValues",
				new Class<?>[] {Function.class, Map.class},
				(Function<String, String>)System::getProperty,
				Map.of(key, new String[] {"true"}));

			_assertSecurityException(
				"FIPS mode requires the property \"" + key + "\"",
				"_validateRequiredValues",
				new Class<?>[] {Function.class, Map.class},
				(Function<String, String>)Security::getProperty,
				Map.of(key, new String[] {"true"}));
		}
		finally {
			System.clearProperty(key);
		}
	}

	@Test
	public void testValidateTLSVerification() {
		FIPSModeValidator.validateTLSVerification(false);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			FIPSModeValidator.validateTLSVerification(true);

			FIPSModeTestUtil.assertSecurityException(
				"TLS verification must be enabled in FIPS mode",
				() -> FIPSModeValidator.validateTLSVerification(false));
		}
	}

	@Test
	public void testValidateURL() {
		for (String url :
				new String[] {
					"ldap://" + RandomTestUtil.randomString(),
					"ldaps://" + RandomTestUtil.randomString(), null
				}) {

			FIPSModeValidator.validateURL(url);
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			FIPSModeValidator.validateURL(
				"ldaps://" + RandomTestUtil.randomString());

			for (String url :
					new String[] {
						"", "ldap://" + RandomTestUtil.randomString(), null
					}) {

				FIPSModeTestUtil.assertSecurityException(
					"protocol scheme",
					() -> FIPSModeValidator.validateURL(url));
			}
		}
	}

	private void _assertSecurityException(
		String expectedMessage, String methodName, Class<?>[] parameterTypes,
		Object... arguments) {

		FIPSModeTestUtil.assertSecurityException(
			expectedMessage,
			() -> ReflectionTestUtil.invoke(
				FIPSModeValidator.class, methodName, parameterTypes,
				arguments));
	}

	private Provider _createProvider(String name) {
		return new Provider(
			name, RandomTestUtil.randomString(),
			RandomTestUtil.randomString()) {
		};
	}

}
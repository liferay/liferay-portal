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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

/**
 * @author Caio Farias
 */
public class FIPSModeValidatorTest {

	@Test
	public void testGetAllowedTLSCipherSuites() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", false)) {

			Assert.assertArrayEquals(
				new String[] {"TLS_RSA_WITH_AES_128_CBC_SHA"},
				FIPSModeValidator.getAllowedTLSCipherSuites(
					new String[] {"TLS_RSA_WITH_AES_128_CBC_SHA"}));
		}

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
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", false)) {

			Assert.assertArrayEquals(
				new String[] {"TLSv1"},
				FIPSModeValidator.getAllowedTLSProtocols(
					new String[] {"TLSv1"}));
		}

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

			Assert.assertTrue(
				plaintextSecretProperties.contains(obfuscatedKey1));
			Assert.assertEquals(
				plaintextSecretProperties.toString(), 1,
				plaintextSecretProperties.size());
		}
	}

	@Test
	public void testValidateAlgorithm() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", false)) {

			for (String algorithm : new String[] {"MD5", null}) {
				FIPSModeValidator.validateAlgorithm(algorithm);
			}
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			for (String algorithm : new String[] {"AES", "SHA-256"}) {
				FIPSModeValidator.validateAlgorithm(algorithm);
			}

			for (String algorithm : new String[] {"MD5", "SHA-2", null}) {
				_assertSecurityException(
					"is not allowed in FIPS mode",
					() -> FIPSModeValidator.validateAlgorithm(algorithm));
			}
		}
	}

	@Test
	public void testValidateFIPSProvider() {
		for (String name : List.of("AmazonCorrettoCryptoProvider", "BCFIPS")) {
			_assertSecurityException(
				"FIPS provider integrity failed:",
				() -> ReflectionTestUtil.invoke(
					FIPSModeValidator.class, "_validateFIPSProvider",
					new Class<?>[] {Provider[].class},
					(Object)new Provider[] {_createProvider(name)}));
		}

		_assertSecurityException(
			"The first security provider must be an allowed FIPS provider",
			() -> ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_validateFIPSProvider",
				new Class<?>[] {Provider[].class},
				(Object)new Provider[] {
					_createProvider(RandomTestUtil.randomString())
				}));
		_assertSecurityException(
			"There are no security providers",
			() -> ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_validateFIPSProvider",
				new Class<?>[] {Provider[].class}, (Object)new Provider[0]));
	}

	@Test
	public void testValidateKey() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", false)) {

			FIPSModeValidator.validateKey("DES", 64);
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			for (int keySize : List.of(128, 192, 256)) {
				FIPSModeValidator.validateKey("AES", keySize);
			}

			_assertSecurityException(
				"AES key must be 128, 192, or 256 bits",
				() -> FIPSModeValidator.validateKey("AES", 64));
			_assertSecurityException(
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
			() -> ReflectionTestUtil.invoke(
				FIPSModeValidator.class,
				"_validatePasswordsEncryptionAlgorithm",
				new Class<?>[] {String.class}, "bcrypt/10"));
		_assertSecurityException(
			"is not allowed in FIPS mode",
			() -> ReflectionTestUtil.invoke(
				FIPSModeValidator.class,
				"_validatePasswordsEncryptionAlgorithm",
				new Class<?>[] {String.class},
				"PBKDF2WithHmacSHA1/160/1300000"));
		_assertSecurityException(
			"iteration count",
			() -> ReflectionTestUtil.invoke(
				FIPSModeValidator.class,
				"_validatePasswordsEncryptionAlgorithm",
				new Class<?>[] {String.class},
				"PBKDF2WithHmacSHA256/256/600000"));
		_assertSecurityException(
			"output length",
			() -> ReflectionTestUtil.invoke(
				FIPSModeValidator.class,
				"_validatePasswordsEncryptionAlgorithm",
				new Class<?>[] {String.class},
				"PBKDF2WithHmacSHA256/64/1300000"));
	}

	@Test
	public void testValidateProviders() {
		Map<String, List<String>> allowedProviderNames =
			ReflectionTestUtil.getFieldValue(
				FIPSModeValidator.class, "_allowedProviderNames");

		for (String allowedProviderName : allowedProviderNames.keySet()) {
			_assertSecurityException(
				"are not allowed in FIPS mode for",
				() -> ReflectionTestUtil.invoke(
					FIPSModeValidator.class, "_validateProviders",
					new Class<?>[] {Provider[].class},
					(Object)new Provider[] {
						_createProvider(allowedProviderName),
						_createProvider(RandomTestUtil.randomString())
					}));
		}
	}

	@Test
	public void testValidateSessionTimeout() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", false)) {

			FIPSModeValidator.validateSessionTimeout(
				RandomTestUtil.randomInt());
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			FIPSModeValidator.validateSessionTimeout(720);

			_assertSecurityException(
				"Session timeout must not be greater than 12 hours in FIPS " +
					"mode",
				() -> FIPSModeValidator.validateSessionTimeout(721));
		}
	}

	@Test
	public void testValidateURL() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", false)) {

			for (String url :
					new String[] {
						"ldap://" + RandomTestUtil.randomString(),
						"ldaps://" + RandomTestUtil.randomString(), null
					}) {

				FIPSModeValidator.validateURL(url);
			}
		}

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			FIPSModeValidator.validateURL(
				"ldaps://" + RandomTestUtil.randomString());

			for (String url :
					new String[] {
						"ldap://" + RandomTestUtil.randomString(), "", null
					}) {

				_assertSecurityException(
					"protocol scheme",
					() -> FIPSModeValidator.validateURL(url));
			}
		}
	}

	private void _assertSecurityException(
		String expectedMessage, ThrowingRunnable throwingRunnable) {

		SecurityException securityException = Assert.assertThrows(
			SecurityException.class, throwingRunnable);

		String message = securityException.getMessage();

		Assert.assertTrue(message, message.contains(expectedMessage));
	}

	private Provider _createProvider(String name) {
		return new Provider(
			name, RandomTestUtil.randomString(),
			RandomTestUtil.randomString()) {
		};
	}

}
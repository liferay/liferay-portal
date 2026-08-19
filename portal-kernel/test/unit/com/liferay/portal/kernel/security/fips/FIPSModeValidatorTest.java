/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.fips;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.internal.security.fips.FIPSModeHelperUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.security.Provider;
import java.security.Security;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
	public void testIsNotAllowedProviderName() {
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_isNotAllowedProviderName",
				new Class<?>[] {String.class}, "AmazonCorrettoCryptoProvider"));
		Assert.assertFalse(
			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_isNotAllowedProviderName",
				new Class<?>[] {String.class}, "BCFIPS"));

		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_isNotAllowedProviderName",
				new Class<?>[] {String.class}, RandomTestUtil.randomString()));
		Assert.assertTrue(
			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_isNotAllowedProviderName",
				new Class<?>[] {String.class}, (Object)null));
	}

	@Test
	public void testValidateAlgorithm() {
		FIPSModeValidator.validateAlgorithm("MD5");
		FIPSModeValidator.validateAlgorithm(null);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			FIPSModeValidator.validateAlgorithm("AES");
			FIPSModeValidator.validateAlgorithm("SHA-256");

			FIPSModeTestUtil.assertSecurityException(
				"is not allowed in FIPS mode",
				() -> FIPSModeValidator.validateAlgorithm("MD5"));

			FIPSModeTestUtil.assertSecurityException(
				"is not allowed in FIPS mode",
				() -> FIPSModeValidator.validateAlgorithm("SHA-2"));

			FIPSModeTestUtil.assertSecurityException(
				"is not allowed in FIPS mode",
				() -> FIPSModeValidator.validateAlgorithm(null));
		}
	}

	@Test
	public void testValidateAllowedPropertyValues() {
		String key = RandomTestUtil.randomString();

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.2",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.2,",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.2,TLSv1.3",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.3",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> ",TLSv1.2",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "SSLv3,TLSv1.3",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.1",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.1,TLSv1.2",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.11",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.2,SSLv2Hello",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "tlsv1.2",
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateAllowedPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> null,
			Map.of(key, new String[] {"TLSv1.2", "TLSv1.3"}));
	}

	@Test
	public void testValidateClusterChannelAuthElement() throws Exception {
		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateClusterChannelAuthElement",
			new Class<?>[] {Element.class, String.class},
			_getElement(FIPSModeTestUtil.XML_AUTH, "AUTH"),
			RandomTestUtil.randomString());

		_assertSecurityException(
			"must authenticate cluster members with \"" +
				FIPSModeTestUtil.AUTH_CLASS_NAME + "\"",
			"_validateClusterChannelAuthElement",
			new Class<?>[] {Element.class, String.class},
			_getElement("<AUTH />", "AUTH"), RandomTestUtil.randomString());

		_assertSecurityException(
			"must authenticate cluster members with \"" +
				FIPSModeTestUtil.AUTH_CLASS_NAME + "\"",
			"_validateClusterChannelAuthElement",
			new Class<?>[] {Element.class, String.class},
			_getElement(
				StringUtil.replace(
					FIPSModeTestUtil.XML_AUTH, FIPSModeTestUtil.AUTH_CLASS_NAME,
					RandomTestUtil.randomString()),
				"AUTH"),
			RandomTestUtil.randomString());
	}

	@Test
	public void testValidateClusterChannelConfiguration() throws Exception {
		Path path = Files.createTempFile(null, ".xml");

		try {
			String channelPropertiesXML1 = StringBundler.concat(
				"<config>", FIPSModeTestUtil.XML_AUTH,
				FIPSModeTestUtil.XML_SYM_ENCRYPT, "</config>");

			Files.write(
				path, channelPropertiesXML1.getBytes(StandardCharsets.UTF_8));

			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_validateClusterChannelConfiguration",
				new Class<?>[] {String.class}, String.valueOf(path));

			String channelPropertiesXML2 = StringBundler.concat(
				"<config>", FIPSModeTestUtil.XML_AUTH,
				FIPSModeTestUtil.XML_ASYM_ENCRYPT, "</config>");

			Files.write(
				path, channelPropertiesXML2.getBytes(StandardCharsets.UTF_8));

			_assertSecurityException(
				"must encrypt intracluster traffic with \"SYM_ENCRYPT\" in " +
					"FIPS mode",
				"_validateClusterChannelConfiguration",
				new Class<?>[] {String.class}, String.valueOf(path));

			Files.write(
				path,
				"<config><FUTURE_ENCRYPT /></config>".getBytes(
					StandardCharsets.UTF_8));

			_assertSecurityException(
				"must encrypt intracluster traffic with \"SYM_ENCRYPT\" in " +
					"FIPS mode",
				"_validateClusterChannelConfiguration",
				new Class<?>[] {String.class}, String.valueOf(path));
		}
		finally {
			Files.delete(path);
		}
	}

	@Test
	public void testValidateClusterChannelSymEncryptElement() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			ReflectionTestUtil.invoke(
				FIPSModeValidator.class,
				"_validateClusterChannelSymEncryptElement",
				new Class<?>[] {Element.class},
				_getElement(FIPSModeTestUtil.XML_SYM_ENCRYPT, "SYM_ENCRYPT"));

			_assertSecurityException(
				"Initialization vector size 0 is not allowed in FIPS mode",
				"_validateClusterChannelSymEncryptElement",
				new Class<?>[] {Element.class},
				_getElement(
					StringUtil.removeSubstring(
						FIPSModeTestUtil.XML_SYM_ENCRYPT,
						"sym_iv_length=\"16\" "),
					"SYM_ENCRYPT"));

			_assertSecurityException(
				"Initialization vector size 12 is not allowed in FIPS mode",
				"_validateClusterChannelSymEncryptElement",
				new Class<?>[] {Element.class},
				_getElement(
					StringUtil.replace(
						FIPSModeTestUtil.XML_SYM_ENCRYPT,
						"sym_iv_length=\"16\"", "sym_iv_length=\"12\""),
					"SYM_ENCRYPT"));

			_assertSecurityException(
				"Key size 64 is not allowed in FIPS mode",
				"_validateClusterChannelSymEncryptElement",
				new Class<?>[] {Element.class},
				_getElement(
					StringUtil.replace(
						FIPSModeTestUtil.XML_SYM_ENCRYPT,
						"sym_keylength=\"128\"", "sym_keylength=\"64\""),
					"SYM_ENCRYPT"));

			String providerName = RandomTestUtil.randomString();

			_assertSecurityException(
				"Security provider \"" + providerName +
					"\" is not allowed in FIPS mode",
				"_validateClusterChannelSymEncryptElement",
				new Class<?>[] {Element.class},
				_getElement(
					StringUtil.replace(
						FIPSModeTestUtil.XML_SYM_ENCRYPT, "<SYM_ENCRYPT ",
						"<SYM_ENCRYPT provider=\"" + providerName + "\" "),
					"SYM_ENCRYPT"));

			_assertSecurityException(
				"Transformation \"\" is not allowed",
				"_validateClusterChannelSymEncryptElement",
				new Class<?>[] {Element.class},
				_getElement(
					StringUtil.removeSubstring(
						FIPSModeTestUtil.XML_SYM_ENCRYPT,
						"sym_algorithm=\"" +
							FIPSModeTestUtil.TRANSFORMATION_SYM + "\" "),
					"SYM_ENCRYPT"));

			_assertSecurityException(
				"Transformation \"AES/ECB/NoPadding\" is not allowed",
				"_validateClusterChannelSymEncryptElement",
				new Class<?>[] {Element.class},
				_getElement(
					StringUtil.replace(
						FIPSModeTestUtil.XML_SYM_ENCRYPT,
						"sym_algorithm=\"" +
							FIPSModeTestUtil.TRANSFORMATION_SYM + "\"",
						"sym_algorithm=\"AES/ECB/NoPadding\""),
					"SYM_ENCRYPT"));
		}
	}

	@Test
	public void testValidateClusterProperties() throws Exception {
		Path controlPath = Files.createTempFile(null, ".xml");
		Path transportPath = Files.createTempFile(null, ".xml");

		String transportKey =
			PropsKeys.CLUSTER_LINK_CHANNEL_PROPERTIES_TRANSPORT + ".0";

		String transportValue = PropsUtil.get(transportKey);

		PropsUtil.set(transportKey, String.valueOf(transportPath));

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"CLUSTER_LINK_AUTH_KEYSTORE_TYPE", "PKCS12", false);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"CLUSTER_LINK_CHANNEL_PROPERTIES_CONTROL",
					String.valueOf(controlPath), false);
			SafeCloseable safeCloseable3 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"CLUSTER_LINK_ENABLED", true);
			SafeCloseable safeCloseable4 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			String channelPropertiesXML = StringBundler.concat(
				"<config>", FIPSModeTestUtil.XML_AUTH,
				FIPSModeTestUtil.XML_SYM_ENCRYPT, "</config>");

			Files.write(
				controlPath,
				channelPropertiesXML.getBytes(StandardCharsets.UTF_8));
			Files.write(
				transportPath,
				channelPropertiesXML.getBytes(StandardCharsets.UTF_8));

			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_validateClusterProperties",
				new Class<?>[0]);

			_assertClusterPropertiesSecurityException(
				StringUtil.replace(
					channelPropertiesXML, "sym_keylength=\"128\"",
					"sym_keylength=\"64\""),
				"Key size 64 is not allowed in FIPS mode", transportPath);

			_assertClusterPropertiesSecurityException(
				StringBundler.concat(
					"<config>", FIPSModeTestUtil.XML_AUTH,
					FIPSModeTestUtil.XML_ASYM_ENCRYPT, "</config>"),
				"must encrypt intracluster traffic with \"SYM_ENCRYPT\" in " +
					"FIPS mode",
				transportPath);

			Files.write(
				transportPath,
				channelPropertiesXML.getBytes(StandardCharsets.UTF_8));

			_assertClusterPropertiesSecurityException(
				StringUtil.replace(
					channelPropertiesXML, FIPSModeTestUtil.AUTH_CLASS_NAME,
					RandomTestUtil.randomString()),
				String.valueOf(controlPath), controlPath);

			try (SafeCloseable safeCloseable5 =
					PropsValuesTestUtil.swapWithSafeCloseable(
						"CLUSTER_LINK_AUTH_KEYSTORE_TYPE",
						RandomTestUtil.randomString(), false)) {

				_assertSecurityException(
					"\"" + PropsKeys.CLUSTER_LINK_AUTH_KEYSTORE_TYPE +
						"\" to be set to only",
					"_validateClusterProperties", new Class<?>[0]);
			}
		}
		finally {
			Files.delete(controlPath);
			Files.delete(transportPath);

			PropsUtil.set(transportKey, transportValue);
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
	public void testValidateIVSize() {
		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateIVSize",
			new Class<?>[] {int.class}, 16);

		_assertSecurityException(
			"Initialization vector size 0 is not allowed in FIPS mode",
			"_validateIVSize", new Class<?>[] {int.class}, 0);

		_assertSecurityException(
			"Initialization vector size 12 is not allowed in FIPS mode",
			"_validateIVSize", new Class<?>[] {int.class}, 12);
	}

	@Test
	public void testValidateKey() {
		FIPSModeValidator.validateKey("DES", 64);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			FIPSModeValidator.validateKey("AES", 0);
			FIPSModeValidator.validateKey("AES", 128);
			FIPSModeValidator.validateKey("AES", 192);
			FIPSModeValidator.validateKey("AES", 256);

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
	public void testValidateRequiredPropertyValues() {
		String key = RandomTestUtil.randomString();

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "PKIX",
			Map.of(key, new String[] {"PKIX"}));

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "SSLv3, TLSv1",
			Map.of(key, new String[] {"TLSv1"}));

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.2,TLSv1.3",
			Map.of(key, new String[] {"TLSv1.2"}));

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TRUE",
			Map.of(key, new String[] {"true"}));

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "pkix",
			Map.of(key, new String[] {"PKIX"}));

		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "true",
			Map.of(key, new String[] {"true"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "SunPKIXFoo",
			Map.of(key, new String[] {"PKIX"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "TLSv1.1",
			Map.of(key, new String[] {"TLSv1"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> "untrue",
			Map.of(key, new String[] {"true"}));

		_assertSecurityException(
			"FIPS mode requires the property \"" + key + "\"",
			"_validateRequiredPropertyValues",
			new Class<?>[] {Function.class, Map.class},
			(Function<String, String>)curKey -> null,
			Map.of(key, new String[] {"true"}));

		System.setProperty(key, "true");

		try {
			ReflectionTestUtil.invoke(
				FIPSModeValidator.class, "_validateRequiredPropertyValues",
				new Class<?>[] {Function.class, Map.class},
				(Function<String, String>)System::getProperty,
				Map.of(key, new String[] {"true"}));

			_assertSecurityException(
				"FIPS mode requires the property \"" + key + "\"",
				"_validateRequiredPropertyValues",
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
	public void testValidateTransformation() {
		ReflectionTestUtil.invoke(
			FIPSModeValidator.class, "_validateTransformation",
			new Class<?>[] {String.class}, FIPSModeTestUtil.TRANSFORMATION_SYM);

		_assertSecurityException(
			"is not allowed in FIPS mode", "_validateTransformation",
			new Class<?>[] {String.class}, "AES");

		_assertSecurityException(
			"is not allowed in FIPS mode", "_validateTransformation",
			new Class<?>[] {String.class}, "RSA");

		_assertSecurityException(
			"is not allowed in FIPS mode", "_validateTransformation",
			new Class<?>[] {String.class},
			"RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

		_assertSecurityException(
			"is not allowed in FIPS mode", "_validateTransformation",
			new Class<?>[] {String.class}, (Object)null);
	}

	@Test
	public void testValidateURL() {
		FIPSModeValidator.validateURL(
			"ldap://" + RandomTestUtil.randomString());
		FIPSModeValidator.validateURL(
			"ldaps://" + RandomTestUtil.randomString());
		FIPSModeValidator.validateURL(null);

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			FIPSModeValidator.validateURL(
				"ldaps://" + RandomTestUtil.randomString());

			FIPSModeTestUtil.assertSecurityException(
				"protocol scheme", () -> FIPSModeValidator.validateURL(""));

			FIPSModeTestUtil.assertSecurityException(
				"protocol scheme",
				() -> FIPSModeValidator.validateURL(
					"ldap://" + RandomTestUtil.randomString()));

			FIPSModeTestUtil.assertSecurityException(
				"protocol scheme", () -> FIPSModeValidator.validateURL(null));
		}
	}

	private void _assertClusterPropertiesSecurityException(
			String channelPropertiesXML, String expectedMessage, Path path)
		throws Exception {

		Files.write(
			path, channelPropertiesXML.getBytes(StandardCharsets.UTF_8));

		_assertSecurityException(
			expectedMessage, "_validateClusterProperties", new Class<?>[0]);
	}

	private void _assertSecurityException(
		String expectedMessage, String methodName, Class<?>[] parameterTypes,
		Object... parameterValues) {

		FIPSModeTestUtil.assertSecurityException(
			expectedMessage,
			() -> ReflectionTestUtil.invoke(
				FIPSModeValidator.class, methodName, parameterTypes,
				parameterValues));
	}

	private Provider _createProvider(String name) {
		return new Provider(
			name, RandomTestUtil.randomString(),
			RandomTestUtil.randomString()) {
		};
	}

	private Element _getElement(String channelPropertiesXML, String tagName)
		throws Exception {

		Path path = Files.createTempFile(null, ".xml");

		try {
			Files.write(
				path, channelPropertiesXML.getBytes(StandardCharsets.UTF_8));

			Document document = FIPSModeHelperUtil.readDocument(
				String.valueOf(path));

			NodeList nodeList = document.getElementsByTagName(tagName);

			return (Element)nodeList.item(0);
		}
		finally {
			Files.delete(path);
		}
	}

}
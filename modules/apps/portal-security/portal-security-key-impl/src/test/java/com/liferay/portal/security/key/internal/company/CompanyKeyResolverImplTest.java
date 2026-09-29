/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.company;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.CompanyKeyResolverUtil;
import com.liferay.portal.kernel.exception.CompanyKeyException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.ServiceIndicator;
import com.liferay.portal.security.key.crypto.CryptoManager;
import com.liferay.portal.security.key.crypto.CryptoServiceResult;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.security.key.internal.profile.configuration.KeyManagerConfiguration;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.Key;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * @author Christopher Kian
 */
public class CompanyKeyResolverImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testActivate() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		CompanyKeyCacheEntry companyKeyCacheEntry = _createCompanyKeyCacheEntry(
			companyKeyResolverImpl);

		companyKeyResolverImpl.activate(
			HashMapBuilder.<String, Object>put(
				"companyKEKIdentifier", _KEK_IDENTIFIER
			).build());

		KeyManagerConfiguration keyManagerConfiguration =
			ReflectionTestUtil.getFieldValue(
				companyKeyResolverImpl, "_keyManagerConfiguration");

		Assert.assertEquals(
			_KEK_IDENTIFIER, keyManagerConfiguration.companyKEKIdentifier());

		Assert.assertNull(companyKeyCacheEntry.getKeyBytes());

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		Assert.assertTrue(companyKeyCacheEntries.isEmpty());

		companyKeyResolverImpl.deactivate();
	}

	@Test
	public void testDeactivate() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		CompanyKeyCacheEntry companyKeyCacheEntry = _createCompanyKeyCacheEntry(
			companyKeyResolverImpl);

		companyKeyResolverImpl.deactivate();

		Assert.assertNull(companyKeyCacheEntry.getKeyBytes());
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				companyKeyResolverImpl, "_keyManagerConfiguration"));

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		Assert.assertTrue(companyKeyCacheEntries.isEmpty());
	}

	@Test
	public void testIsEnabled() throws Exception {
		_testIsEnabled();
		_testIsEnabledInFIPSMode();
	}

	@Test
	public void testUnwrapKey() throws Exception {
		_testUnwrapKey();
		_testUnwrapKeyWithChangedWrappedKey();
		_testUnwrapKeyWithDecryptFailure();
		_testUnwrapKeyWithMalformedWrappedKey();
		_testUnwrapKeyWithMultipleCompanies();
		_testUnwrapKeyWithUnsupportedVersion();
	}

	@Test
	public void testWrapKey() throws Exception {
		_testWrapKey();
		_testWrapKeyWithEncryptFailure();
		_testWrapKeyWithUnregisteredKEKProvider();
		_testWrapKeyWithWildcardKEKProvider();
		_testWrapKeyWithoutKEKIdentifier();
		_testWrapKeyWithoutKEKProvider();
	}

	private void _assertUnwrapKeyFails(
		CompanyKeyResolverImpl companyKeyResolverImpl, String keyString) {

		try {
			companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, keyString);

			Assert.fail();
		}
		catch (CompanyKeyException companyKeyException) {
		}
	}

	private void _assertWrapKeyFails(
		CompanyKeyResolverImpl companyKeyResolverImpl) {

		try {
			companyKeyResolverImpl.wrapKey(_COMPANY_ID_1, _key1);

			Assert.fail();
		}
		catch (CompanyKeyException companyKeyException) {
		}
	}

	private CompanyKeyCacheEntry _createCompanyKeyCacheEntry(
			CompanyKeyResolverImpl companyKeyResolverImpl)
		throws Exception {

		_mockDecrypt(_CIPHERTEXT_1, _COMPANY_ID_1, _KEY_BYTES_1);

		companyKeyResolverImpl.unwrapKey(
			_COMPANY_ID_1, _toKeyString(_CIPHERTEXT_1));

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		return companyKeyCacheEntries.get(_COMPANY_ID_1);
	}

	private CompanyKeyResolverImpl _createCompanyKeyResolverImpl()
		throws Exception {

		CompanyKeyResolverImpl companyKeyResolverImpl =
			new CompanyKeyResolverImpl();

		_cryptoManager = Mockito.mock(CryptoManager.class);

		Mockito.when(
			_cryptoManager.getCryptoProviderIds(ArgumentMatchers.anyLong())
		).thenReturn(
			Collections.singletonList(_KEK_PROVIDER_ID)
		);

		ReflectionTestUtil.setFieldValue(
			companyKeyResolverImpl, "_cryptoManager", _cryptoManager);

		ReflectionTestUtil.setFieldValue(
			companyKeyResolverImpl, "_keyAlgorithm", _KEY_ALGORITHM);

		_keyManagerConfiguration = Mockito.mock(KeyManagerConfiguration.class);

		Mockito.when(
			_keyManagerConfiguration.companyKEKIdentifier()
		).thenReturn(
			_KEK_IDENTIFIER
		);

		ReflectionTestUtil.setFieldValue(
			companyKeyResolverImpl, "_keyManagerConfiguration",
			_keyManagerConfiguration);

		_keyManagerProfile = Mockito.mock(KeyManagerProfile.class);

		_mockCompanyKEKProviderId(_KEK_PROVIDER_ID);

		_keyManagerProfileRegistry = Mockito.mock(
			KeyManagerProfileRegistry.class);

		Mockito.when(
			_keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			_keyManagerProfile
		);

		ReflectionTestUtil.setFieldValue(
			companyKeyResolverImpl, "_keyManagerProfileRegistry",
			_keyManagerProfileRegistry);

		return companyKeyResolverImpl;
	}

	private Map<Long, CompanyKeyCacheEntry> _getCompanyKeyCacheEntries(
		CompanyKeyResolverImpl companyKeyResolverImpl) {

		return ReflectionTestUtil.getFieldValue(
			companyKeyResolverImpl, "_companyKeyCacheEntries");
	}

	private void _mockCompanyKEKIdentifier(String companyKEKIdentifier) {
		Mockito.when(
			_keyManagerConfiguration.companyKEKIdentifier()
		).thenReturn(
			companyKEKIdentifier
		);
	}

	private void _mockCompanyKEKProviderId(String companyKEKProviderId) {
		Mockito.when(
			_keyManagerProfile.getCompanyKEKProviderId()
		).thenReturn(
			companyKEKProviderId
		);
	}

	private byte[] _mockDecrypt(
			byte[] ciphertext, long companyId, byte[] keyBytes)
		throws Exception {

		byte[] providerKeyBytes = keyBytes.clone();

		Mockito.when(
			_cryptoManager.decrypt(
				AdditionalMatchers.aryEq(ciphertext),
				ArgumentMatchers.eq(companyId),
				ArgumentMatchers.eq(
					new KeyReference(
						_KEK_IDENTIFIER, _KEK_PROVIDER_ID,
						KeyReference.Type.CRYPTO)))
		).thenReturn(
			new CryptoServiceResult<>(_serviceIndicator, providerKeyBytes)
		).thenAnswer(
			invocationOnMock -> new CryptoServiceResult<>(
				_serviceIndicator, keyBytes.clone())
		);

		return providerKeyBytes;
	}

	private void _mockDecryptFailure() throws Exception {
		Mockito.when(
			_cryptoManager.decrypt(
				ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
				ArgumentMatchers.any())
		).thenThrow(
			new CryptoException(RandomTestUtil.randomString())
		);
	}

	private void _testIsEnabled() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", false)) {

			Assert.assertTrue(companyKeyResolverImpl.isEnabled(_COMPANY_ID_1));

			_mockCompanyKEKIdentifier(null);

			Assert.assertFalse(companyKeyResolverImpl.isEnabled(_COMPANY_ID_1));

			_mockCompanyKEKIdentifier(StringPool.BLANK);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					CompanyKeyResolverImpl.class.getName(),
					LoggerTestUtil.WARN)) {

				Assert.assertFalse(
					companyKeyResolverImpl.isEnabled(_COMPANY_ID_1));

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 0, logEntries.size());
			}

			_mockCompanyKEKIdentifier(StringPool.SPACE);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					CompanyKeyResolverImpl.class.getName(),
					LoggerTestUtil.WARN)) {

				Assert.assertFalse(
					companyKeyResolverImpl.isEnabled(_COMPANY_ID_1));

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					StringBundler.concat(
						"Company key wrapping is inactive because the KEK ",
						"identifier is set to an unusable value for company ",
						_COMPANY_ID_1),
					logEntry.getMessage());
			}

			ReflectionTestUtil.setFieldValue(
				companyKeyResolverImpl, "_keyManagerConfiguration", null);

			Assert.assertFalse(companyKeyResolverImpl.isEnabled(_COMPANY_ID_1));
		}
	}

	private void _testIsEnabledInFIPSMode() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			Assert.assertTrue(companyKeyResolverImpl.isEnabled(_COMPANY_ID_1));

			_mockCompanyKEKIdentifier(null);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					CompanyKeyResolverImpl.class.getName(),
					LoggerTestUtil.WARN)) {

				Assert.assertFalse(
					companyKeyResolverImpl.isEnabled(_COMPANY_ID_1));

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 1, logEntries.size());

				LogEntry logEntry = logEntries.get(0);

				Assert.assertEquals(
					StringBundler.concat(
						"The company key is stored in plaintext in FIPS mode ",
						"because the KEK identifier is not configured for ",
						"company ", _COMPANY_ID_1),
					logEntry.getMessage());
			}
		}
	}

	private void _testUnwrapKey() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		byte[] providerKeyBytes = _mockDecrypt(
			_CIPHERTEXT_1, _COMPANY_ID_1, _KEY_BYTES_1);

		String keyString = _toKeyString(_CIPHERTEXT_1);

		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, keyString));
		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, keyString));

		Mockito.verify(
			_cryptoManager, Mockito.times(1)
		).decrypt(
			ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
			ArgumentMatchers.any()
		);

		Assert.assertArrayEquals(
			new byte[_KEY_BYTES_1.length], providerKeyBytes);
	}

	private void _testUnwrapKeyWithChangedWrappedKey() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		byte[] changedCiphertext = _CIPHERTEXT_1.clone();

		changedCiphertext[0] = (byte)(changedCiphertext[0] + 1);

		_mockDecrypt(changedCiphertext, _COMPANY_ID_1, _KEY_BYTES_1);

		CompanyKeyCacheEntry companyKeyCacheEntry = _createCompanyKeyCacheEntry(
			companyKeyResolverImpl);

		Assert.assertEquals(
			_key1,
			companyKeyResolverImpl.unwrapKey(
				_COMPANY_ID_1, _toKeyString(changedCiphertext)));

		Mockito.verify(
			_cryptoManager, Mockito.times(2)
		).decrypt(
			ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
			ArgumentMatchers.any()
		);

		Assert.assertNull(companyKeyCacheEntry.getKeyBytes());
	}

	private void _testUnwrapKeyWithDecryptFailure() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		_createCompanyKeyCacheEntry(companyKeyResolverImpl);

		_mockDecryptFailure();

		String keyString = _toKeyString(_CIPHERTEXT_1);

		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, keyString));

		companyKeyResolverImpl = _createCompanyKeyResolverImpl();

		_mockDecryptFailure();

		_assertUnwrapKeyFails(companyKeyResolverImpl, keyString);
	}

	private void _testUnwrapKeyWithMalformedWrappedKey() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		_assertUnwrapKeyFails(
			companyKeyResolverImpl, RandomTestUtil.randomString());
		_assertUnwrapKeyFails(companyKeyResolverImpl, _WRAPPED_KEY_PREFIX);
		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			_WRAPPED_KEY_PREFIX + StringPool.CLOSE_CURLY_BRACE);

		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			_WRAPPED_KEY_PREFIX + _WRAPPED_KEY_VERSION + StringPool.COLON);

		String keyString = _toKeyString(_CIPHERTEXT_1);

		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringUtil.replaceFirst(
				keyString, CharPool.CLOSE_CURLY_BRACE, StringPool.BLANK));
		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringUtil.replaceFirst(
				keyString, CharPool.PIPE, StringPool.BLANK));
		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringUtil.replaceFirst(
				keyString, _KEK_IDENTIFIER, StringPool.BLANK));
		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringUtil.replaceFirst(
				keyString, _KEK_IDENTIFIER,
				_KEK_IDENTIFIER + StringPool.CLOSE_CURLY_BRACE));
		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringUtil.replaceFirst(
				keyString, _KEK_PROVIDER_ID, StringPool.BLANK));
		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringUtil.replaceFirst(
				keyString, _KEK_PROVIDER_ID,
				_KEK_PROVIDER_ID + StringPool.CLOSE_CURLY_BRACE));

		Base64.Encoder encoder = Base64.getEncoder();

		String ciphertextString = encoder.encodeToString(_CIPHERTEXT_1);

		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringUtil.replaceFirst(keyString, ciphertextString, "not base64"));
		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringUtil.replaceFirst(
				keyString, ciphertextString, StringPool.BLANK));
		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringUtil.replaceFirst(
				keyString, ciphertextString, StringPool.EQUAL));
	}

	private void _testUnwrapKeyWithMultipleCompanies() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		_mockDecrypt(_CIPHERTEXT_1, _COMPANY_ID_1, _KEY_BYTES_1);
		_mockDecrypt(_CIPHERTEXT_2, _COMPANY_ID_2, _KEY_BYTES_2);

		String keyString1 = _toKeyString(_CIPHERTEXT_1);
		String keyString2 = _toKeyString(_CIPHERTEXT_2);

		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, keyString1));
		Assert.assertEquals(
			_key2, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_2, keyString2));

		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, keyString1));
		Assert.assertEquals(
			_key2, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_2, keyString2));

		Mockito.verify(
			_cryptoManager, Mockito.times(2)
		).decrypt(
			ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
			ArgumentMatchers.any()
		);
	}

	private void _testUnwrapKeyWithUnsupportedVersion() throws Exception {
		String keyString = StringUtil.replaceFirst(
			_toKeyString(_CIPHERTEXT_1),
			_WRAPPED_KEY_PREFIX + _WRAPPED_KEY_VERSION,
			_WRAPPED_KEY_PREFIX + "v2");

		Assert.assertTrue(CompanyKeyResolverUtil.isWrappedKey(keyString));

		_assertUnwrapKeyFails(_createCompanyKeyResolverImpl(), keyString);
	}

	private void _testWrapKey() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		Mockito.when(
			_cryptoManager.encrypt(
				ArgumentMatchers.eq(_COMPANY_ID_1),
				ArgumentMatchers.eq(
					new KeyReference(
						_KEK_IDENTIFIER, _KEK_PROVIDER_ID,
						KeyReference.Type.CRYPTO)),
				ArgumentMatchers.any(byte[].class))
		).thenReturn(
			new CryptoServiceResult<>(_serviceIndicator, _CIPHERTEXT_1.clone())
		);

		String keyString = companyKeyResolverImpl.wrapKey(_COMPANY_ID_1, _key1);

		Assert.assertEquals(_toKeyString(_CIPHERTEXT_1), keyString);
		Assert.assertTrue(CompanyKeyResolverUtil.isWrappedKey(keyString));

		ArgumentCaptor<byte[]> argumentCaptor = ArgumentCaptor.forClass(
			byte[].class);

		Mockito.verify(
			_cryptoManager
		).encrypt(
			ArgumentMatchers.eq(_COMPANY_ID_1), ArgumentMatchers.any(),
			argumentCaptor.capture()
		);

		Assert.assertArrayEquals(
			new byte[_KEY_BYTES_1.length], argumentCaptor.getValue());

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		Assert.assertTrue(companyKeyCacheEntries.isEmpty());
	}

	private void _testWrapKeyWithEncryptFailure() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		Mockito.when(
			_cryptoManager.encrypt(
				ArgumentMatchers.anyLong(), ArgumentMatchers.any(),
				ArgumentMatchers.any())
		).thenThrow(
			new CryptoException(RandomTestUtil.randomString())
		);

		_assertWrapKeyFails(companyKeyResolverImpl);
	}

	private void _testWrapKeyWithUnregisteredKEKProvider() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		Mockito.when(
			_cryptoManager.getCryptoProviderIds(ArgumentMatchers.anyLong())
		).thenReturn(
			Collections.singletonList(RandomTestUtil.randomString())
		);

		_assertWrapKeyFails(companyKeyResolverImpl);

		Mockito.verify(
			_cryptoManager, Mockito.never()
		).encrypt(
			ArgumentMatchers.anyLong(), ArgumentMatchers.any(),
			ArgumentMatchers.any()
		);
	}

	private void _testWrapKeyWithWildcardKEKProvider() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		_mockCompanyKEKProviderId(StringPool.STAR);

		_assertWrapKeyFails(companyKeyResolverImpl);
	}

	private void _testWrapKeyWithoutKEKIdentifier() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		_mockCompanyKEKIdentifier(StringPool.BLANK);

		_assertWrapKeyFails(companyKeyResolverImpl);
	}

	private void _testWrapKeyWithoutKEKProvider() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl();

		Mockito.when(
			_keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			null
		);

		_assertWrapKeyFails(companyKeyResolverImpl);
	}

	private String _toKeyString(byte[] ciphertext) {
		Base64.Encoder encoder = Base64.getEncoder();

		return StringBundler.concat(
			_WRAPPED_KEY_PREFIX, _WRAPPED_KEY_VERSION, StringPool.COLON,
			_KEK_PROVIDER_ID, StringPool.COLON, _KEK_IDENTIFIER,
			StringPool.PIPE, encoder.encodeToString(ciphertext),
			StringPool.CLOSE_CURLY_BRACE);
	}

	private static final byte[] _CIPHERTEXT_1 = RandomTestUtil.randomBytes();

	private static final byte[] _CIPHERTEXT_2 = RandomTestUtil.randomBytes();

	private static final long _COMPANY_ID_1 = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID_2 = RandomTestUtil.randomLong();

	private static final String _KEK_IDENTIFIER = StringBundler.concat(
		"arn:aws:kms:", RandomTestUtil.randomString(), StringPool.COLON,
		RandomTestUtil.randomLong(), ":key/", RandomTestUtil.randomString());

	private static final String _KEK_PROVIDER_ID =
		RandomTestUtil.randomString();

	private static final String _KEY_ALGORITHM = "AES";

	private static final byte[] _KEY_BYTES_1 = RandomTestUtil.randomBytes();

	private static final byte[] _KEY_BYTES_2 = RandomTestUtil.randomBytes();

	private static final String _WRAPPED_KEY_PREFIX = "${wrappedKey:";

	private static final String _WRAPPED_KEY_VERSION = "v1";

	private CryptoManager _cryptoManager;
	private final Key _key1 = new SecretKeySpec(_KEY_BYTES_1, _KEY_ALGORITHM);
	private final Key _key2 = new SecretKeySpec(_KEY_BYTES_2, _KEY_ALGORITHM);
	private KeyManagerConfiguration _keyManagerConfiguration;
	private KeyManagerProfile _keyManagerProfile;
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;
	private final ServiceIndicator _serviceIndicator = new ServiceIndicator(
		true, RandomTestUtil.randomString());

}
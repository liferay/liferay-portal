/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.company;

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
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		CompanyKeyCacheEntry companyKeyCacheEntry = _createCompanyKeyCacheEntry(
			companyKeyResolverImpl);

		companyKeyResolverImpl.activate(
			HashMapBuilder.<String, Object>put(
				"companyKEKIdentifier", _KEK_IDENTIFIER
			).put(
				"companyKeyCacheTTL", 1
			).build());

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		KeyManagerConfiguration keyManagerConfiguration =
			ReflectionTestUtil.getFieldValue(
				companyKeyResolverImpl, "_keyManagerConfiguration");

		Assert.assertEquals(
			_KEK_IDENTIFIER, keyManagerConfiguration.companyKEKIdentifier());
		Assert.assertEquals(1, keyManagerConfiguration.companyKeyCacheTTL());

		Assert.assertNull(companyKeyCacheEntry.getKeyBytes());
		Assert.assertTrue(companyKeyCacheEntries.isEmpty());

		companyKeyResolverImpl.deactivate();
	}

	@Test
	public void testDeactivate() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		CompanyKeyCacheEntry companyKeyCacheEntry = _createCompanyKeyCacheEntry(
			companyKeyResolverImpl);

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		companyKeyResolverImpl.deactivate();

		Assert.assertNull(companyKeyCacheEntry.getKeyBytes());
		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				companyKeyResolverImpl, "_keyManagerConfiguration"));
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
		_testUnwrapKeyWithExpiredCacheEntry();
		_testUnwrapKeyWithExpiredCacheEntryForOtherCompany();
		_testUnwrapKeyWithMalformedWrappedKey();
		_testUnwrapKeyWithMultipleCompanies();
		_testUnwrapKeyWithUnsupportedVersion();
		_testUnwrapKeyWithoutCache();
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
		CompanyKeyResolverImpl companyKeyResolverImpl, String wrappedKey) {

		try {
			companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey);

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

		_mockDecrypt(_COMPANY_ID_1, _CIPHERTEXT_1, _KEY_BYTES_1);

		companyKeyResolverImpl.unwrapKey(
			_COMPANY_ID_1, _toWrappedKey(_CIPHERTEXT_1));

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		return companyKeyCacheEntries.get(_COMPANY_ID_1);
	}

	private CompanyKeyResolverImpl _createCompanyKeyResolverImpl(
			int companyKeyCacheTTL)
		throws Exception {

		CompanyKeyResolverImpl companyKeyResolverImpl =
			new CompanyKeyResolverImpl();

		_keyManagerConfiguration = Mockito.mock(KeyManagerConfiguration.class);

		Mockito.when(
			_keyManagerConfiguration.companyKEKIdentifier()
		).thenReturn(
			_KEK_IDENTIFIER
		);

		Mockito.when(
			_keyManagerConfiguration.companyKeyCacheTTL()
		).thenReturn(
			companyKeyCacheTTL
		);

		_keyManagerProfile = Mockito.mock(KeyManagerProfile.class);

		_mockCompanyKEKProviderId(_KEK_PROVIDER_ID);

		ReflectionTestUtil.setFieldValue(
			companyKeyResolverImpl, "_cryptoManager", _cryptoManager);

		ReflectionTestUtil.setFieldValue(
			companyKeyResolverImpl, "_keyAlgorithm", _KEY_ALGORITHM);
		ReflectionTestUtil.setFieldValue(
			companyKeyResolverImpl, "_keyManagerConfiguration",
			_keyManagerConfiguration);

		_keyManagerProfileRegistry = Mockito.mock(
			KeyManagerProfileRegistry.class);

		Mockito.when(
			_keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			_keyManagerProfile
		);

		_cryptoManager = Mockito.mock(CryptoManager.class);

		Mockito.when(
			_cryptoManager.getCryptoProviderIds(ArgumentMatchers.anyLong())
		).thenReturn(
			Collections.singletonList(_KEK_PROVIDER_ID)
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
			long companyId, byte[] ciphertext, byte[] keyBytes)
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
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

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
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

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
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		String wrappedKey = _toWrappedKey(_CIPHERTEXT_1);

		byte[] providerKeyBytes = _mockDecrypt(
			_COMPANY_ID_1, _CIPHERTEXT_1, _KEY_BYTES_1);

		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey));
		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey));

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
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		_mockDecrypt(_COMPANY_ID_1, _CIPHERTEXT_1, _KEY_BYTES_1);

		companyKeyResolverImpl.unwrapKey(
			_COMPANY_ID_1, _toWrappedKey(_CIPHERTEXT_1));

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		CompanyKeyCacheEntry companyKeyCacheEntry = companyKeyCacheEntries.get(
			_COMPANY_ID_1);

		byte[] changedCiphertext = _CIPHERTEXT_1.clone();

		changedCiphertext[0] = (byte)(changedCiphertext[0] + 1);

		_mockDecrypt(_COMPANY_ID_1, changedCiphertext, _KEY_BYTES_1);

		Assert.assertEquals(
			_key1,
			companyKeyResolverImpl.unwrapKey(
				_COMPANY_ID_1, _toWrappedKey(changedCiphertext)));

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
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		_createCompanyKeyCacheEntry(companyKeyResolverImpl);

		String wrappedKey = _toWrappedKey(_CIPHERTEXT_1);

		_mockDecryptFailure();

		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey));

		companyKeyResolverImpl = _createCompanyKeyResolverImpl(
			RandomTestUtil.randomInt(1, 1000));

		_mockDecryptFailure();

		_assertUnwrapKeyFails(companyKeyResolverImpl, wrappedKey);
	}

	private void _testUnwrapKeyWithExpiredCacheEntry() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		String wrappedKey = _toWrappedKey(_CIPHERTEXT_1);

		_mockDecrypt(_COMPANY_ID_1, _CIPHERTEXT_1, _KEY_BYTES_1);

		companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey);

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		CompanyKeyCacheEntry expiredCompanyKeyCacheEntry =
			new CompanyKeyCacheEntry(
				System.currentTimeMillis() - 1, _KEY_BYTES_1, wrappedKey);

		companyKeyCacheEntries.put(_COMPANY_ID_1, expiredCompanyKeyCacheEntry);

		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey));

		Mockito.verify(
			_cryptoManager, Mockito.times(2)
		).decrypt(
			ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
			ArgumentMatchers.any()
		);

		Assert.assertNull(expiredCompanyKeyCacheEntry.getKeyBytes());
	}

	private void _testUnwrapKeyWithExpiredCacheEntryForOtherCompany()
		throws Exception {

		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		CompanyKeyCacheEntry expiredCompanyKeyCacheEntry =
			new CompanyKeyCacheEntry(
				System.currentTimeMillis() - 1, _KEY_BYTES_1,
				_toWrappedKey(_CIPHERTEXT_1));

		companyKeyCacheEntries.put(_COMPANY_ID_1, expiredCompanyKeyCacheEntry);

		_mockDecrypt(_COMPANY_ID_2, _CIPHERTEXT_2, _KEY_BYTES_2);

		Assert.assertEquals(
			_key2,
			companyKeyResolverImpl.unwrapKey(
				_COMPANY_ID_2, _toWrappedKey(_CIPHERTEXT_2)));

		Assert.assertFalse(companyKeyCacheEntries.containsKey(_COMPANY_ID_1));
		Assert.assertNull(expiredCompanyKeyCacheEntry.getKeyBytes());
	}

	private void _testUnwrapKeyWithMalformedWrappedKey() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		_assertUnwrapKeyFails(
			companyKeyResolverImpl, RandomTestUtil.randomString());

		String body = StringBundler.concat(
			CompanyKeyResolverUtil.WRAPPED_KEY_PREFIX,
			CompanyKeyResolverUtil.WRAPPED_KEY_VERSION, StringPool.COLON,
			_KEK_PROVIDER_ID, StringPool.COLON, _KEK_IDENTIFIER);

		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			body + StringPool.PIPE + StringPool.CLOSE_CURLY_BRACE);
		_assertUnwrapKeyFails(
			companyKeyResolverImpl,
			StringBundler.concat(
				body, StringPool.PIPE, StringPool.EQUAL,
				StringPool.CLOSE_CURLY_BRACE));
		_assertUnwrapKeyFails(
			companyKeyResolverImpl, body + "|not valid base64}");
	}

	private void _testUnwrapKeyWithMultipleCompanies() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		String wrappedKey1 = _toWrappedKey(_CIPHERTEXT_1);
		String wrappedKey2 = _toWrappedKey(_CIPHERTEXT_2);

		_mockDecrypt(_COMPANY_ID_1, _CIPHERTEXT_1, _KEY_BYTES_1);
		_mockDecrypt(_COMPANY_ID_2, _CIPHERTEXT_2, _KEY_BYTES_2);

		Assert.assertEquals(
			_key1,
			companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey1));
		Assert.assertEquals(
			_key2,
			companyKeyResolverImpl.unwrapKey(_COMPANY_ID_2, wrappedKey2));

		Assert.assertEquals(
			_key1,
			companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey1));
		Assert.assertEquals(
			_key2,
			companyKeyResolverImpl.unwrapKey(_COMPANY_ID_2, wrappedKey2));

		Mockito.verify(
			_cryptoManager, Mockito.times(2)
		).decrypt(
			ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
			ArgumentMatchers.any()
		);
	}

	private void _testUnwrapKeyWithUnsupportedVersion() throws Exception {
		WrappedCompanyKey wrappedCompanyKey = new WrappedCompanyKey(
			_CIPHERTEXT_1,
			new KeyReference(
				_KEK_IDENTIFIER, _KEK_PROVIDER_ID, KeyReference.Type.CRYPTO));

		String wrappedKey = StringUtil.replaceFirst(
			wrappedCompanyKey.toWrappedKey(),
			CompanyKeyResolverUtil.WRAPPED_KEY_PREFIX +
				CompanyKeyResolverUtil.WRAPPED_KEY_VERSION,
			CompanyKeyResolverUtil.WRAPPED_KEY_PREFIX.concat("v2"));

		Assert.assertTrue(CompanyKeyResolverUtil.isWrappedKey(wrappedKey));

		_assertUnwrapKeyFails(
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000)),
			wrappedKey);
	}

	private void _testUnwrapKeyWithoutCache() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(0);

		String wrappedKey = _toWrappedKey(_CIPHERTEXT_1);

		_mockDecrypt(_COMPANY_ID_1, _CIPHERTEXT_1, _KEY_BYTES_1);

		companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey);
		companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey);

		Mockito.verify(
			_cryptoManager, Mockito.times(2)
		).decrypt(
			ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
			ArgumentMatchers.any()
		);

		Map<Long, CompanyKeyCacheEntry> companyKeyCacheEntries =
			_getCompanyKeyCacheEntries(companyKeyResolverImpl);

		Assert.assertTrue(companyKeyCacheEntries.isEmpty());
	}

	private void _testWrapKey() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

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

		String wrappedKey = companyKeyResolverImpl.wrapKey(
			_COMPANY_ID_1, _key1);

		WrappedCompanyKey wrappedCompanyKey = WrappedCompanyKey.parse(
			_COMPANY_ID_1, wrappedKey);

		KeyReference keyReference = wrappedCompanyKey.getKeyReference();

		Assert.assertArrayEquals(
			_CIPHERTEXT_1, wrappedCompanyKey.getCiphertext());

		Assert.assertEquals(_KEK_IDENTIFIER, keyReference.getIdentifier());
		Assert.assertEquals(_KEK_PROVIDER_ID, keyReference.getProviderId());

		ArgumentCaptor<byte[]> argumentCaptor = ArgumentCaptor.forClass(
			byte[].class);

		Mockito.verify(
			_cryptoManager
		).encrypt(
			ArgumentMatchers.eq(_COMPANY_ID_1), ArgumentMatchers.any(),
			argumentCaptor.capture()
		);

		Assert.assertArrayEquals(_KEY_BYTES_1, _key1.getEncoded());
		Assert.assertArrayEquals(
			new byte[_KEY_BYTES_1.length], argumentCaptor.getValue());

		Assert.assertEquals(
			_key1, companyKeyResolverImpl.unwrapKey(_COMPANY_ID_1, wrappedKey));
		Assert.assertTrue(CompanyKeyResolverUtil.isWrappedKey(wrappedKey));

		Mockito.verify(
			_cryptoManager, Mockito.never()
		).decrypt(
			ArgumentMatchers.any(), ArgumentMatchers.anyLong(),
			ArgumentMatchers.any()
		);
	}

	private void _testWrapKeyWithEncryptFailure() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

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
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

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
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		_mockCompanyKEKProviderId(StringPool.STAR);

		_assertWrapKeyFails(companyKeyResolverImpl);
	}

	private void _testWrapKeyWithoutKEKIdentifier() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		_mockCompanyKEKIdentifier(StringPool.BLANK);

		_assertWrapKeyFails(companyKeyResolverImpl);
	}

	private void _testWrapKeyWithoutKEKProvider() throws Exception {
		CompanyKeyResolverImpl companyKeyResolverImpl =
			_createCompanyKeyResolverImpl(RandomTestUtil.randomInt(1, 1000));

		Mockito.when(
			_keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			null
		);

		_assertWrapKeyFails(companyKeyResolverImpl);
	}

	private String _toWrappedKey(byte[] ciphertext) {
		WrappedCompanyKey wrappedCompanyKey = new WrappedCompanyKey(
			ciphertext,
			new KeyReference(
				_KEK_IDENTIFIER, _KEK_PROVIDER_ID, KeyReference.Type.CRYPTO));

		return wrappedCompanyKey.toWrappedKey();
	}

	private static final byte[] _CIPHERTEXT_1 = RandomTestUtil.randomBytes();

	private static final byte[] _CIPHERTEXT_2 = RandomTestUtil.randomBytes();

	private static final long _COMPANY_ID_1 = RandomTestUtil.randomLong();

	private static final long _COMPANY_ID_2 = RandomTestUtil.randomLong();

	private static final String _KEK_IDENTIFIER = RandomTestUtil.randomString();

	private static final String _KEK_PROVIDER_ID =
		RandomTestUtil.randomString();

	private static final String _KEY_ALGORITHM = "AES";

	private static final byte[] _KEY_BYTES_1 = RandomTestUtil.randomBytes();

	private static final byte[] _KEY_BYTES_2 = RandomTestUtil.randomBytes();

	private CryptoManager _cryptoManager;
	private final Key _key1 = new SecretKeySpec(_KEY_BYTES_1, _KEY_ALGORITHM);
	private final Key _key2 = new SecretKeySpec(_KEY_BYTES_2, _KEY_ALGORITHM);
	private KeyManagerConfiguration _keyManagerConfiguration;
	private KeyManagerProfile _keyManagerProfile;
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;
	private final ServiceIndicator _serviceIndicator = new ServiceIndicator(
		true, RandomTestUtil.randomString());

}
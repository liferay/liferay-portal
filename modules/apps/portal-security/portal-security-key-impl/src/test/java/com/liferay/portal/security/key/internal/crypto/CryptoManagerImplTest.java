/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.crypto;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.ServiceIndicator;
import com.liferay.portal.security.key.crypto.CryptoServiceResult;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.security.key.spi.crypto.CryptoProvider;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.security.Key;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Christopher Kian
 */
public class CryptoManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtil.setFieldValue(
			_cryptoManagerImpl, "_keyManagerProfileRegistry",
			_keyManagerProfileRegistry);
		ReflectionTestUtil.setFieldValue(
			_cryptoManagerImpl, "_serviceTrackerMap", _serviceTrackerMap);
	}

	@Test
	public void testDecrypt() throws Exception {
		byte[] plaintext = RandomTestUtil.randomBytes();

		Mockito.when(
			_cryptoProvider.decrypt(
				Mockito.any(byte[].class), Mockito.anyLong(),
				Mockito.anyString())
		).thenReturn(
			_getCryptoServiceResult(plaintext)
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		CryptoServiceResult<byte[]> cryptoServiceResult =
			_cryptoManagerImpl.decrypt(
				RandomTestUtil.randomBytes(), RandomTestUtil.randomLong(),
				_getKeyReference(cryptoProviderId));

		Assert.assertArrayEquals(plaintext, cryptoServiceResult.getValue());
	}

	@Test
	public void testDeleteKey() throws Exception {
		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		long companyId = RandomTestUtil.randomLong();
		String keyIdentifier = RandomTestUtil.randomString();

		_cryptoManagerImpl.deleteKey(
			companyId, _getKeyReference(cryptoProviderId, keyIdentifier));

		Mockito.verify(
			_cryptoProvider
		).deleteKey(
			companyId, keyIdentifier
		);
	}

	@Test
	public void testEncrypt() throws Exception {
		byte[] ciphertext = RandomTestUtil.randomBytes();

		Mockito.when(
			_cryptoProvider.encrypt(
				Mockito.anyLong(), Mockito.anyString(),
				Mockito.any(byte[].class))
		).thenReturn(
			_getCryptoServiceResult(ciphertext)
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		CryptoServiceResult<byte[]> cryptoServiceResult =
			_cryptoManagerImpl.encrypt(
				RandomTestUtil.randomLong(), _getKeyReference(cryptoProviderId),
				RandomTestUtil.randomBytes());

		Assert.assertArrayEquals(ciphertext, cryptoServiceResult.getValue());
	}

	@Test
	public void testEncryptResolvesProviderWildcard() throws Exception {
		_testEncryptResolvesProviderWildcard(CompanyConstants.SYSTEM);
		_testEncryptResolvesProviderWildcard(RandomTestUtil.randomLong());
	}

	@Test
	public void testEncryptThrowsCryptoException() {
		Mockito.when(
			_keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			null
		);

		Assert.assertThrows(
			CryptoException.class,
			() -> _cryptoManagerImpl.encrypt(
				RandomTestUtil.randomLong(), _getKeyReference(StringPool.STAR),
				RandomTestUtil.randomBytes()));

		String cryptoProviderId = RandomTestUtil.randomString();

		Assert.assertThrows(
			CryptoException.class,
			() -> _cryptoManagerImpl.encrypt(
				RandomTestUtil.randomLong(), _getKeyReference(cryptoProviderId),
				RandomTestUtil.randomBytes()));

		Mockito.when(
			_cryptoProvider.getProviderStatus()
		).thenReturn(
			ProviderStatus.ERROR
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		Assert.assertThrows(
			CryptoException.class,
			() -> _cryptoManagerImpl.encrypt(
				RandomTestUtil.randomLong(), _getKeyReference(cryptoProviderId),
				RandomTestUtil.randomBytes()));
	}

	@Test
	public void testExportKey() throws Exception {
		Key key = Mockito.mock(Key.class);

		Mockito.when(
			_cryptoProvider.exportKey(Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_getCryptoServiceResult(key)
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		CryptoServiceResult<Key> cryptoServiceResult =
			_cryptoManagerImpl.exportKey(
				RandomTestUtil.randomLong(),
				_getKeyReference(cryptoProviderId));

		Assert.assertSame(key, cryptoServiceResult.getValue());
	}

	@Test
	public void testGenerateAsymmetricKeyReference() throws Exception {
		String keyIdentifier = RandomTestUtil.randomString();

		Mockito.when(
			_cryptoProvider.generateAsymmetricKeyIdentifier(
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_getCryptoServiceResult(keyIdentifier)
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		CryptoServiceResult<KeyReference> cryptoServiceResult =
			_cryptoManagerImpl.generateAsymmetricKeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				_getKeyReference(cryptoProviderId));

		KeyReference keyReference = cryptoServiceResult.getValue();

		Assert.assertEquals(cryptoProviderId, keyReference.getProviderId());
		Assert.assertEquals(keyIdentifier, keyReference.getIdentifier());
	}

	@Test
	public void testGenerateSecretKeyReference() throws Exception {
		String keyIdentifier = RandomTestUtil.randomString();

		Mockito.when(
			_cryptoProvider.generateSecretKeyIdentifier(
				Mockito.anyString(), Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			_getCryptoServiceResult(keyIdentifier)
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		CryptoServiceResult<KeyReference> cryptoServiceResult =
			_cryptoManagerImpl.generateSecretKeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				_getKeyReference(cryptoProviderId));

		KeyReference keyReference = cryptoServiceResult.getValue();

		Assert.assertEquals(cryptoProviderId, keyReference.getProviderId());
		Assert.assertEquals(keyIdentifier, keyReference.getIdentifier());
	}

	@Test
	public void testGetCryptoProviderIds() {
		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_cryptoProvider.isAllowedCompany(companyId)
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		Mockito.when(
			_serviceTrackerMap.keySet()
		).thenReturn(
			Collections.singleton(cryptoProviderId)
		);

		Assert.assertEquals(
			Collections.singletonList(cryptoProviderId),
			_cryptoManagerImpl.getCryptoProviderIds(companyId));

		Mockito.when(
			_cryptoProvider.isAllowedCompany(companyId)
		).thenReturn(
			false
		);

		Assert.assertEquals(
			Collections.emptyList(),
			_cryptoManagerImpl.getCryptoProviderIds(companyId));
	}

	@Test
	public void testGetKeyReferences() throws Exception {
		String keyIdentifier = RandomTestUtil.randomString();

		Mockito.when(
			_cryptoProvider.getKeyIdentifiers(Mockito.anyLong())
		).thenReturn(
			Collections.singletonList(keyIdentifier)
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		List<KeyReference> keyReferences = _cryptoManagerImpl.getKeyReferences(
			RandomTestUtil.randomLong(), cryptoProviderId);

		Assert.assertEquals(keyReferences.toString(), 1, keyReferences.size());

		KeyReference keyReference = keyReferences.get(0);

		Assert.assertEquals(KeyReference.Type.CRYPTO, keyReference.getType());
		Assert.assertEquals(cryptoProviderId, keyReference.getProviderId());
		Assert.assertEquals(keyIdentifier, keyReference.getIdentifier());
	}

	@Test
	public void testImportSecretKey() throws Exception {
		String keyIdentifier = RandomTestUtil.randomString();

		Mockito.when(
			_cryptoProvider.importSecretKey(
				Mockito.anyString(), Mockito.anyLong(),
				Mockito.any(byte[].class), Mockito.anyString())
		).thenReturn(
			_getCryptoServiceResult(keyIdentifier)
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		byte[] keyBytes = RandomTestUtil.randomBytes();

		CryptoServiceResult<KeyReference> cryptoServiceResult =
			_cryptoManagerImpl.importSecretKey(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				keyBytes, _getKeyReference(cryptoProviderId));

		KeyReference keyReference = cryptoServiceResult.getValue();

		Assert.assertEquals(cryptoProviderId, keyReference.getProviderId());
		Assert.assertEquals(keyIdentifier, keyReference.getIdentifier());

		Assert.assertArrayEquals(new byte[keyBytes.length], keyBytes);
	}

	@Test
	public void testImportSecretKeyZerosKeyBytesEvenOnFailure()
		throws Exception {

		Mockito.when(
			_cryptoProvider.importSecretKey(
				Mockito.anyString(), Mockito.anyLong(),
				Mockito.any(byte[].class), Mockito.anyString())
		).thenThrow(
			new CryptoException()
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		byte[] keyBytes = RandomTestUtil.randomBytes();

		Assert.assertThrows(
			CryptoException.class,
			() -> _cryptoManagerImpl.importSecretKey(
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				keyBytes, _getKeyReference(cryptoProviderId)));

		Assert.assertArrayEquals(new byte[keyBytes.length], keyBytes);
	}

	@Test
	public void testUnwrap() throws Exception {
		Assert.assertThrows(
			CryptoException.class,
			() -> _cryptoManagerImpl.unwrap(
				RandomTestUtil.randomLong(),
				_getKeyReference(RandomTestUtil.randomString()),
				_getKeyReference(RandomTestUtil.randomString()),
				RandomTestUtil.randomString(), RandomTestUtil.randomBytes(),
				RandomTestUtil.randomInt()));

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String unwrappedKeyIdentifier = RandomTestUtil.randomString();

		Mockito.when(
			_cryptoProvider.unwrap(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any(byte[].class),
				Mockito.anyInt())
		).thenReturn(
			_getCryptoServiceResult(unwrappedKeyIdentifier)
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		CryptoServiceResult<KeyReference> cryptoServiceResult =
			_cryptoManagerImpl.unwrap(
				RandomTestUtil.randomLong(), _getKeyReference(cryptoProviderId),
				_getKeyReference(cryptoProviderId),
				RandomTestUtil.randomString(), RandomTestUtil.randomBytes(),
				RandomTestUtil.randomInt());

		KeyReference keyReference = cryptoServiceResult.getValue();

		Assert.assertEquals(cryptoProviderId, keyReference.getProviderId());
		Assert.assertEquals(
			unwrappedKeyIdentifier, keyReference.getIdentifier());
	}

	@Test
	public void testWrap() throws Exception {
		Assert.assertThrows(
			CryptoException.class,
			() -> _cryptoManagerImpl.wrap(
				RandomTestUtil.randomLong(),
				_getKeyReference(RandomTestUtil.randomString()),
				_getKeyReference(RandomTestUtil.randomString())));

		Mockito.when(
			_cryptoProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		byte[] wrappedKeyBytes = RandomTestUtil.randomBytes();

		Mockito.when(
			_cryptoProvider.wrap(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			_getCryptoServiceResult(wrappedKeyBytes)
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		CryptoServiceResult<byte[]> cryptoServiceResult =
			_cryptoManagerImpl.wrap(
				RandomTestUtil.randomLong(), _getKeyReference(cryptoProviderId),
				_getKeyReference(cryptoProviderId));

		Assert.assertSame(wrappedKeyBytes, cryptoServiceResult.getValue());
	}

	private <T> CryptoServiceResult<T> _getCryptoServiceResult(T value) {
		return new CryptoServiceResult<>(
			new ServiceIndicator(true, RandomTestUtil.randomString()), value);
	}

	private KeyReference _getKeyReference(String cryptoProviderId) {
		return _getKeyReference(
			cryptoProviderId, RandomTestUtil.randomString());
	}

	private KeyReference _getKeyReference(
		String cryptoProviderId, String keyIdentifier) {

		return new KeyReference(
			keyIdentifier, cryptoProviderId, KeyReference.Type.CRYPTO);
	}

	private void _testEncryptResolvesProviderWildcard(long companyId)
		throws Exception {

		String keyIdentifier = RandomTestUtil.randomString();

		Mockito.when(
			_cryptoProvider.encrypt(
				Mockito.eq(companyId), Mockito.eq(keyIdentifier),
				Mockito.any(byte[].class))
		).thenReturn(
			_getCryptoServiceResult(RandomTestUtil.randomBytes())
		);

		Mockito.when(
			_cryptoProvider.isAllowedCompany(companyId)
		).thenReturn(
			true
		);

		String cryptoProviderId = RandomTestUtil.randomString();

		if (companyId == CompanyConstants.SYSTEM) {
			Mockito.when(
				_keyManagerProfile.getSystemDEKProviderId()
			).thenReturn(
				cryptoProviderId
			);
		}
		else {
			Mockito.when(
				_keyManagerProfile.getCompanyDEKProviderId()
			).thenReturn(
				cryptoProviderId
			);
		}

		Mockito.when(
			_keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			_keyManagerProfile
		);

		Mockito.when(
			_serviceTrackerMap.getService(cryptoProviderId)
		).thenReturn(
			Collections.singletonList(_cryptoProvider)
		);

		_cryptoManagerImpl.encrypt(
			companyId, _getKeyReference(StringPool.STAR, keyIdentifier),
			RandomTestUtil.randomBytes());

		if (companyId == CompanyConstants.SYSTEM) {
			Mockito.verify(
				_keyManagerProfile
			).getSystemDEKProviderId();
		}
		else {
			Mockito.verify(
				_keyManagerProfile
			).getCompanyDEKProviderId();
		}
	}

	private final CryptoManagerImpl _cryptoManagerImpl =
		new CryptoManagerImpl();

	@Mock
	private CryptoProvider _cryptoProvider;

	@Mock
	private KeyManagerProfile _keyManagerProfile;

	@Mock
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;

	@Mock
	private ServiceTrackerMap<String, List<CryptoProvider>> _serviceTrackerMap;

}
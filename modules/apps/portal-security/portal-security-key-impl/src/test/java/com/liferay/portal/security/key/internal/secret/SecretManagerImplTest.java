/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.secret;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;
import com.liferay.portal.security.key.spi.secret.SecretProvider;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

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
public class SecretManagerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtil.setFieldValue(
			_secretManagerImpl, "_keyManagerProfileRegistry",
			_keyManagerProfileRegistry);
		ReflectionTestUtil.setFieldValue(
			_secretManagerImpl, "_serviceTrackerMap", _serviceTrackerMap);
	}

	@Test
	public void testDeleteSecret() {
		String secretProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(secretProviderId)
		).thenReturn(
			null
		);

		Assert.assertThrows(
			SecretException.class,
			() -> _secretManagerImpl.deleteSecret(
				RandomTestUtil.randomLong(), _keyReference(secretProviderId)));
	}

	@Test
	public void testGetKeyReferences() throws Exception {
		String secretProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_keyManagerProfile.getCompanySecretProviderId()
		).thenReturn(
			secretProviderId
		);

		Mockito.when(
			_keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			_keyManagerProfile
		);

		String secretIdentifier = RandomTestUtil.randomString();

		Mockito.when(
			_secretProvider.getSecretIdentifiers(Mockito.anyLong())
		).thenReturn(
			Collections.singletonList(secretIdentifier)
		);

		Mockito.when(
			_secretProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		Mockito.when(
			_serviceTrackerMap.getService(secretProviderId)
		).thenReturn(
			Collections.singletonList(_secretProvider)
		);

		List<KeyReference> keyReferences = _secretManagerImpl.getKeyReferences(
			RandomTestUtil.randomLong(), StringPool.STAR);

		Assert.assertEquals(keyReferences.toString(), 1, keyReferences.size());

		KeyReference keyReference = keyReferences.get(0);

		Assert.assertEquals(secretIdentifier, keyReference.getIdentifier());
		Assert.assertEquals(secretProviderId, keyReference.getProviderId());
		Assert.assertEquals(KeyReference.Type.SECRET, keyReference.getType());
	}

	@Test
	public void testGetSecret() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		String secretIdentifier = RandomTestUtil.randomString();
		String secretProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_secretProvider.getSecret(companyId, secretIdentifier)
		).thenReturn(
			new Secret(
				RandomTestUtil.randomBytes(),
				_keyReference(secretIdentifier, secretProviderId))
		);

		Mockito.when(
			_secretProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		Mockito.when(
			_serviceTrackerMap.getService(secretProviderId)
		).thenReturn(
			Collections.singletonList(_secretProvider)
		);

		_secretManagerImpl.getSecret(
			companyId, _keyReference(secretIdentifier, secretProviderId));

		Mockito.verify(
			_secretProvider
		).getSecret(
			companyId, secretIdentifier
		);

		Mockito.when(
			_secretProvider.getProviderStatus()
		).thenReturn(
			ProviderStatus.ERROR
		);

		Assert.assertThrows(
			SecretException.class,
			() -> _secretManagerImpl.getSecret(
				RandomTestUtil.randomLong(), _keyReference(secretProviderId)));
	}

	@Test
	public void testGetSecretProviderIds() {
		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_secretProvider.isAllowedCompany(companyId)
		).thenReturn(
			true
		);

		String secretProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(secretProviderId)
		).thenReturn(
			Collections.singletonList(_secretProvider)
		);

		Mockito.when(
			_serviceTrackerMap.keySet()
		).thenReturn(
			Collections.singleton(secretProviderId)
		);

		Assert.assertEquals(
			Collections.singletonList(secretProviderId),
			_secretManagerImpl.getSecretProviderIds(companyId));

		Mockito.when(
			_secretProvider.isAllowedCompany(companyId)
		).thenReturn(
			false
		);

		Assert.assertEquals(
			Collections.emptyList(),
			_secretManagerImpl.getSecretProviderIds(companyId));
	}

	@Test
	public void testPutSecret() throws Exception {
		Mockito.when(
			_secretProvider.isAllowedCompany(Mockito.anyLong())
		).thenReturn(
			true
		);

		String secretProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_serviceTrackerMap.getService(secretProviderId)
		).thenReturn(
			Collections.singletonList(_secretProvider)
		);

		long companyId = RandomTestUtil.randomLong();
		String secretIdentifier = RandomTestUtil.randomString();

		try (Secret secret = new Secret(
				_keyReference(secretIdentifier, secretProviderId),
				RandomTestUtil.randomString())) {

			_secretManagerImpl.putSecret(companyId, secret);
		}

		Mockito.verify(
			_secretProvider
		).putSecret(
			Mockito.eq(companyId), Mockito.any(Secret.class)
		);

		_testPutSecretResolvesProviderWildcard(CompanyConstants.SYSTEM);
		_testPutSecretResolvesProviderWildcard(RandomTestUtil.randomLong());
	}

	private KeyReference _keyReference(String secretProviderId) {
		return _keyReference(RandomTestUtil.randomString(), secretProviderId);
	}

	private KeyReference _keyReference(
		String secretIdentifier, String secretProviderId) {

		return new KeyReference(
			secretIdentifier, secretProviderId, KeyReference.Type.SECRET);
	}

	private void _testPutSecretResolvesProviderWildcard(long companyId)
		throws Exception {

		String secretProviderId = RandomTestUtil.randomString();

		if (companyId == CompanyConstants.SYSTEM) {
			Mockito.when(
				_keyManagerProfile.getSystemSecretProviderId()
			).thenReturn(
				secretProviderId
			);
		}
		else {
			Mockito.when(
				_keyManagerProfile.getCompanySecretProviderId()
			).thenReturn(
				secretProviderId
			);
		}

		Mockito.when(
			_keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			_keyManagerProfile
		);

		Mockito.when(
			_secretProvider.isAllowedCompany(companyId)
		).thenReturn(
			true
		);

		Mockito.when(
			_serviceTrackerMap.getService(secretProviderId)
		).thenReturn(
			Collections.singletonList(_secretProvider)
		);

		try (Secret secret = new Secret(
				_keyReference(RandomTestUtil.randomString(), StringPool.STAR),
				RandomTestUtil.randomString())) {

			_secretManagerImpl.putSecret(companyId, secret);
		}

		if (companyId == CompanyConstants.SYSTEM) {
			Mockito.verify(
				_keyManagerProfile
			).getSystemSecretProviderId();
		}
		else {
			Mockito.verify(
				_keyManagerProfile
			).getCompanySecretProviderId();
		}
	}

	@Mock
	private KeyManagerProfile _keyManagerProfile;

	@Mock
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;

	private final SecretManagerImpl _secretManagerImpl =
		new SecretManagerImpl();

	@Mock
	private SecretProvider _secretProvider;

	@Mock
	private ServiceTrackerMap<String, List<SecretProvider>> _serviceTrackerMap;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.secret;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.fips.FIPSAuditEvent;
import com.liferay.portal.kernel.security.fips.FIPSAuditUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Pedro Victor Silvestre
 */
public class SecretResolverImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		ReflectionTestUtil.setFieldValue(
			_secretResolverImpl, "_keyManagerProfileRegistry",
			_keyManagerProfileRegistry);
		ReflectionTestUtil.setFieldValue(
			_secretResolverImpl, "_portalCache", _portalCache);
		ReflectionTestUtil.setFieldValue(
			_secretResolverImpl, "_secretManager", _secretManager);
	}

	@Test
	public void testGetKey() {
		long companyId = RandomTestUtil.randomLong();
		String keyReferenceString = RandomTestUtil.randomString();

		Assert.assertEquals(
			companyId + StringPool.POUND + keyReferenceString,
			SecretResolverImpl.getKey(companyId, keyReferenceString));
	}

	@Test
	public void testResolve() throws Exception {
		_assertResolve(RandomTestUtil.randomString());
		_assertResolve(StringPool.STAR);
	}

	@Test
	public void testResolveWhenKeyReferenceIsCrypto() throws Exception {
		Assert.assertThrows(
			SecretException.class,
			() -> _secretResolverImpl.resolve(
				RandomTestUtil.randomLong(), "${keyRef:provider:identifier}"));

		Mockito.verifyNoInteractions(_secretManager);
	}

	@Test
	public void testResolveWhenKeyReferenceIsInvalid() throws Exception {
		Assert.assertThrows(
			SecretException.class,
			() -> _secretResolverImpl.resolve(
				RandomTestUtil.randomLong(), "${secretRef:provider}"));

		Mockito.verifyNoInteractions(_secretManager);
	}

	@Test
	public void testResolveWhenProviderIsSystem() throws Exception {
		String systemSecretProviderId = RandomTestUtil.randomString();

		Mockito.when(
			_keyManagerProfile.getSystemSecretProviderId()
		).thenReturn(
			systemSecretProviderId
		);

		Mockito.when(
			_keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			_keyManagerProfile
		);

		KeyReference keyReference = new KeyReference(
			RandomTestUtil.randomString(), systemSecretProviderId,
			KeyReference.Type.SECRET);
		String value = RandomTestUtil.randomString();

		Mockito.when(
			_secretManager.getSecret(CompanyConstants.SYSTEM, keyReference)
		).thenReturn(
			new Secret(keyReference, value)
		);

		Assert.assertEquals(
			value,
			_secretResolverImpl.resolve(
				RandomTestUtil.randomLong(),
				KeyReferenceUtil.toKeyReferenceString(keyReference)));

		Mockito.verify(
			_secretManager
		).getSecret(
			CompanyConstants.SYSTEM, keyReference
		);
	}

	@Test
	public void testResolveWhenSecretManagerFails() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		KeyReference keyReference = new KeyReference(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			KeyReference.Type.SECRET);
		SecretException secretException = new SecretException();

		Mockito.when(
			_secretManager.getSecret(companyId, keyReference)
		).thenThrow(
			secretException
		);

		Assert.assertSame(
			secretException,
			Assert.assertThrows(
				SecretException.class,
				() -> _secretResolverImpl.resolve(
					companyId,
					KeyReferenceUtil.toKeyReferenceString(keyReference))));
	}

	@Test
	public void testResolveWhenValueIsCached() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		KeyReference keyReference = new KeyReference(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			KeyReference.Type.SECRET);

		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			keyReference);

		String value = RandomTestUtil.randomString();

		Mockito.when(
			_portalCache.get(companyId + StringPool.POUND + keyReferenceString)
		).thenReturn(
			value
		);

		Assert.assertEquals(
			value, _secretResolverImpl.resolve(companyId, keyReferenceString));

		Mockito.verifyNoInteractions(_secretManager);
	}

	@Test
	public void testResolveWhenValueIsNotKeyReference() throws Exception {
		String value = RandomTestUtil.randomString();

		Assert.assertNull(
			_secretResolverImpl.resolve(RandomTestUtil.randomLong(), null));
		Assert.assertSame(
			value,
			_secretResolverImpl.resolve(RandomTestUtil.randomLong(), value));

		Mockito.verifyNoInteractions(_secretManager);
	}

	@Test
	public void testStore() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			_testStore();

			Mockito.clearInvocations(_secretManager);

			_testStoreWhenFIPSIsDisabled();
			_testStoreWhenIdentifierNamespaceIsUnsupported();
			_testStoreWhenKeyReferenceIsInvalid();
			_testStoreWhenSecretManagerFails();

			Mockito.reset(_secretManager);

			_testStoreWhenValueIsBlank();
			_testStoreWhenValueReferencesAnotherKey();
			_testStoreWhenValueReferencesAnotherNamespace();
			_testStoreWhenValueReferencesSameKeyInAnotherScope();
			_testStoreWhenValueReferencesSameSlot();
		}
	}

	private void _assertResolve(String providerId) throws Exception {
		long companyId = RandomTestUtil.randomLong();

		KeyReference keyReference = new KeyReference(
			RandomTestUtil.randomString(), providerId,
			KeyReference.Type.SECRET);
		String value = RandomTestUtil.randomString();

		Secret secret = new Secret(keyReference, value);

		Mockito.when(
			_secretManager.getSecret(companyId, keyReference)
		).thenReturn(
			secret
		);

		Assert.assertEquals(
			value,
			_secretResolverImpl.resolve(
				companyId,
				KeyReferenceUtil.toKeyReferenceString(keyReference)));
		Assert.assertTrue(secret.isDestroyed());

		Mockito.verify(
			_secretManager
		).getSecret(
			companyId, keyReference
		);

		Mockito.verify(
			_portalCache
		).put(
			companyId + StringPool.POUND +
				KeyReferenceUtil.toKeyReferenceString(keyReference),
			value, 600
		);
	}

	private void _assertStore(String identifier) throws Exception {
		long companyId = RandomTestUtil.randomLong();

		KeyReference keyReference = new KeyReference(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			KeyReference.Type.SECRET);

		AtomicReference<Secret> atomicReference = new AtomicReference<>();

		Mockito.when(
			_secretManager.putSecret(Mockito.eq(companyId), Mockito.any())
		).thenAnswer(
			invocationOnMock -> {
				atomicReference.set(invocationOnMock.getArgument(1));

				return keyReference;
			}
		);

		try (MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic =
				Mockito.mockStatic(FIPSAuditUtil.class)) {

			Assert.assertEquals(
				KeyReferenceUtil.toKeyReferenceString(keyReference),
				_secretResolverImpl.store(
					companyId, identifier, RandomTestUtil.randomString()));

			fipsAuditUtilMockedStatic.verifyNoInteractions();
		}

		Secret secret = atomicReference.get();

		KeyReference secretKeyReference = secret.getKeyReference();

		Assert.assertEquals(identifier, secretKeyReference.getIdentifier());
		Assert.assertEquals(
			StringPool.STAR, secretKeyReference.getProviderId());

		Assert.assertTrue(secret.isDestroyed());
	}

	private void _assertStoreFails(String eventType, String identifier)
		throws Exception {

		long companyId = RandomTestUtil.randomLong();
		SecretException secretException = new SecretException();

		Mockito.when(
			_secretManager.putSecret(Mockito.eq(companyId), Mockito.any())
		).thenThrow(
			secretException
		);

		try (MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic =
				Mockito.mockStatic(FIPSAuditUtil.class)) {

			Assert.assertSame(
				secretException,
				Assert.assertThrows(
					SecretException.class,
					() -> _secretResolverImpl.store(
						companyId, identifier, RandomTestUtil.randomString())));

			ArgumentCaptor<FIPSAuditEvent> argumentCaptor =
				ArgumentCaptor.forClass(FIPSAuditEvent.class);

			fipsAuditUtilMockedStatic.verify(
				() -> FIPSAuditUtil.write(argumentCaptor.capture()));

			FIPSAuditEvent fipsAuditEvent = argumentCaptor.getValue();

			Assert.assertEquals(eventType, fipsAuditEvent.getEventType());
			Assert.assertEquals(
				HashMapBuilder.<String, Object>put(
					"company-id", companyId
				).put(
					"identifier", identifier
				).build(),
				fipsAuditEvent.getFields());
		}
	}

	private void _assertStoreKeepsValue(
		String identifier, String referencedIdentifier) {

		String value = _toKeyReferenceString(referencedIdentifier);

		try (MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic =
				Mockito.mockStatic(FIPSAuditUtil.class)) {

			Assert.assertEquals(
				value,
				_secretResolverImpl.store(
					RandomTestUtil.randomLong(), identifier, value));

			fipsAuditUtilMockedStatic.verifyNoInteractions();
		}

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _assertStoreRejects(
		String eventType, String identifier, String referencedIdentifier) {

		long companyId = RandomTestUtil.randomLong();

		try (MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic =
				Mockito.mockStatic(FIPSAuditUtil.class)) {

			Assert.assertThrows(
				SecretException.class,
				() -> _secretResolverImpl.store(
					companyId, identifier,
					_toKeyReferenceString(referencedIdentifier)));

			ArgumentCaptor<FIPSAuditEvent> argumentCaptor =
				ArgumentCaptor.forClass(FIPSAuditEvent.class);

			fipsAuditUtilMockedStatic.verify(
				() -> FIPSAuditUtil.write(argumentCaptor.capture()));

			FIPSAuditEvent fipsAuditEvent = argumentCaptor.getValue();

			Assert.assertEquals(eventType, fipsAuditEvent.getEventType());
			Assert.assertEquals(
				HashMapBuilder.<String, Object>put(
					"company-id", companyId
				).put(
					"identifier", identifier
				).put(
					"rejected-identifier", referencedIdentifier
				).build(),
				fipsAuditEvent.getFields());
		}

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testStore() throws Exception {
		_assertStore(
			StringBundler.concat(
				"config/", RandomTestUtil.randomString(), StringPool.SLASH,
				RandomTestUtil.randomString()));
		_assertStore(
			StringBundler.concat(
				"preference/", RandomTestUtil.randomString(), StringPool.SLASH,
				RandomTestUtil.randomString()));
	}

	private void _testStoreWhenFIPSIsDisabled() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", false)) {

			String value = RandomTestUtil.randomString();

			Assert.assertEquals(
				value,
				_secretResolverImpl.store(
					RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
					value));

			Mockito.verifyNoInteractions(_secretManager);
		}
	}

	private void _testStoreWhenIdentifierNamespaceIsUnsupported() {
		try (MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic =
				Mockito.mockStatic(FIPSAuditUtil.class)) {

			Assert.assertThrows(
				IllegalArgumentException.class,
				() -> _secretResolverImpl.store(
					RandomTestUtil.randomLong(),
					StringBundler.concat(
						RandomTestUtil.randomString(), StringPool.SLASH,
						RandomTestUtil.randomString()),
					RandomTestUtil.randomString()));

			fipsAuditUtilMockedStatic.verifyNoInteractions();
		}

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testStoreWhenKeyReferenceIsInvalid() {
		try (MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic =
				Mockito.mockStatic(FIPSAuditUtil.class)) {

			Assert.assertThrows(
				SecretException.class,
				() -> _secretResolverImpl.store(
					RandomTestUtil.randomLong(),
					StringBundler.concat(
						"config/", RandomTestUtil.randomString(),
						StringPool.SLASH, RandomTestUtil.randomString()),
					"${secretRef:provider}"));

			fipsAuditUtilMockedStatic.verifyNoInteractions();
		}

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testStoreWhenSecretManagerFails() throws Exception {
		_assertStoreFails(
			"configuration-secret-store-failure",
			StringBundler.concat(
				"config/", RandomTestUtil.randomString(), StringPool.SLASH,
				RandomTestUtil.randomString()));
		_assertStoreFails(
			"preference-secret-store-failure",
			StringBundler.concat(
				"preference/", RandomTestUtil.randomString(), StringPool.SLASH,
				RandomTestUtil.randomString()));
	}

	private void _testStoreWhenValueIsBlank() {
		Assert.assertEquals(
			StringPool.BLANK,
			_secretResolverImpl.store(
				RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
				StringPool.BLANK));

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testStoreWhenValueReferencesAnotherKey() {
		String scope = RandomTestUtil.randomString();

		_assertStoreRejects(
			"configuration-secret-reference-rejected",
			StringBundler.concat(
				"config/", scope, StringPool.SLASH,
				RandomTestUtil.randomString()),
			StringBundler.concat(
				"config/", scope, StringPool.SLASH,
				RandomTestUtil.randomString()));
		_assertStoreRejects(
			"preference-secret-reference-rejected",
			StringBundler.concat(
				"preference/", scope, StringPool.SLASH,
				RandomTestUtil.randomString()),
			StringBundler.concat(
				"preference/", scope, StringPool.SLASH,
				RandomTestUtil.randomString()));
	}

	private void _testStoreWhenValueReferencesAnotherNamespace() {
		String key = RandomTestUtil.randomString();
		String scope = RandomTestUtil.randomString();

		_assertStoreRejects(
			"configuration-secret-reference-rejected",
			StringBundler.concat("config/", scope, StringPool.SLASH, key),
			StringBundler.concat("preference/", scope, StringPool.SLASH, key));
		_assertStoreRejects(
			"preference-secret-reference-rejected",
			StringBundler.concat("preference/", scope, StringPool.SLASH, key),
			StringBundler.concat("config/", scope, StringPool.SLASH, key));
	}

	private void _testStoreWhenValueReferencesSameKeyInAnotherScope() {
		String key = RandomTestUtil.randomString();

		_assertStoreRejects(
			"configuration-secret-reference-rejected",
			StringBundler.concat(
				"config/", RandomTestUtil.randomString(), StringPool.SLASH,
				key),
			StringBundler.concat(
				"config/", RandomTestUtil.randomString(), StringPool.SLASH,
				key));
		_assertStoreKeepsValue(
			StringBundler.concat(
				"preference/", RandomTestUtil.randomString(), StringPool.SLASH,
				key),
			StringBundler.concat(
				"preference/", RandomTestUtil.randomString(), StringPool.SLASH,
				key));
	}

	private void _testStoreWhenValueReferencesSameSlot() {
		String configurationIdentifier = StringBundler.concat(
			"config/", RandomTestUtil.randomString(), StringPool.SLASH,
			RandomTestUtil.randomString());

		_assertStoreKeepsValue(
			configurationIdentifier, configurationIdentifier);

		String preferenceIdentifier = StringBundler.concat(
			"preference/", RandomTestUtil.randomString(), StringPool.SLASH,
			RandomTestUtil.randomString());

		_assertStoreKeepsValue(preferenceIdentifier, preferenceIdentifier);
	}

	private String _toKeyReferenceString(String identifier) {
		return KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				identifier, RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));
	}

	@Mock
	private KeyManagerProfile _keyManagerProfile;

	@Mock
	private KeyManagerProfileRegistry _keyManagerProfileRegistry;

	@Mock
	private PortalCache<String, String> _portalCache;

	@Mock
	private SecretManager _secretManager;

	private final SecretResolverImpl _secretResolverImpl =
		new SecretResolverImpl();

}
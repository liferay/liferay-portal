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
import com.liferay.portal.security.key.secret.SecretContext;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

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

import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationAdmin;

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
	public void testResolveWithIdentifier() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		KeyReference keyReference = new KeyReference(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			KeyReference.Type.SECRET);

		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			keyReference);

		Assert.assertSame(
			keyReferenceString,
			_secretResolverImpl.resolve(
				companyId, RandomTestUtil.randomString(), keyReferenceString));

		String plainValue = RandomTestUtil.randomString();

		Assert.assertSame(
			plainValue,
			_secretResolverImpl.resolve(
				companyId, keyReference.getIdentifier(), plainValue));

		Assert.assertNull(
			_secretResolverImpl.resolve(
				companyId, keyReference.getIdentifier(), null));

		Mockito.verifyNoInteractions(_secretManager);

		String value = RandomTestUtil.randomString();

		Mockito.when(
			_secretManager.getSecret(companyId, keyReference)
		).thenReturn(
			new Secret(keyReference, value)
		);

		Assert.assertEquals(
			value,
			_secretResolverImpl.resolve(
				companyId, keyReference.getIdentifier(), keyReferenceString));
	}

	@Test
	public void testResolveWithSecretContext() throws Exception {
		_testResolveWithConfigurationSecretContext();
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

	@Test
	public void testStoreWithSecretContext() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			_testStoreWithSecretContext();
			_testStoreWithSecretContextWhenFIPSIsDisabled();
			_testStoreWithSecretContextWhenValueReferencesAnotherContext();
		}
	}

	private void _assertFIPSAuditEvent(
		long companyId, MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic,
		String identifier, String operation) {

		ArgumentCaptor<FIPSAuditEvent> argumentCaptor = ArgumentCaptor.forClass(
			FIPSAuditEvent.class);

		fipsAuditUtilMockedStatic.verify(
			() -> FIPSAuditUtil.write(argumentCaptor.capture()));

		FIPSAuditEvent fipsAuditEvent = argumentCaptor.getValue();

		Assert.assertEquals(
			"secret-reference-refused", fipsAuditEvent.getEventType());
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"company-id", companyId
			).put(
				"identifier", identifier
			).put(
				"operation", operation
			).build(),
			fipsAuditEvent.getFields());
	}

	private void _assertResolve(
			long companyId, String identifier, long scopeCompanyId,
			SecretContext secretContext)
		throws Exception {

		KeyReference keyReference = new KeyReference(
			identifier, RandomTestUtil.randomString(),
			KeyReference.Type.SECRET);
		String value = RandomTestUtil.randomString();

		Mockito.when(
			_secretManager.getSecret(scopeCompanyId, keyReference)
		).thenReturn(
			new Secret(keyReference, value)
		);

		Assert.assertEquals(
			value,
			_secretResolverImpl.resolve(
				companyId, secretContext,
				KeyReferenceUtil.toKeyReferenceString(keyReference)));
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

	private void _assertResolveRejects(
		long companyId, String identifier, SecretContext secretContext) {

		Mockito.clearInvocations(_secretManager);

		try (MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic =
				Mockito.mockStatic(FIPSAuditUtil.class)) {

			Assert.assertThrows(
				SecretException.class,
				() -> _secretResolverImpl.resolve(
					companyId, secretContext,
					_toKeyReferenceString(identifier)));

			_assertFIPSAuditEvent(
				companyId, fipsAuditUtilMockedStatic, identifier, "resolve");
		}

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _assertStore(
			long companyId, String identifier, SecretContext secretContext)
		throws Exception {

		KeyReference keyReference = new KeyReference(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			KeyReference.Type.SECRET);

		Mockito.when(
			_secretManager.putSecret(Mockito.eq(companyId), Mockito.any())
		).thenReturn(
			keyReference
		);

		Assert.assertEquals(
			KeyReferenceUtil.toKeyReferenceString(keyReference),
			_secretResolverImpl.store(
				companyId, secretContext, RandomTestUtil.randomString()));

		ArgumentCaptor<Secret> argumentCaptor = ArgumentCaptor.forClass(
			Secret.class);

		Mockito.verify(
			_secretManager
		).putSecret(
			Mockito.eq(companyId), argumentCaptor.capture()
		);

		Secret secret = argumentCaptor.getValue();

		KeyReference secretKeyReference = secret.getKeyReference();

		Assert.assertEquals(identifier, secretKeyReference.getIdentifier());
		Assert.assertEquals(
			StringPool.STAR, secretKeyReference.getProviderId());

		Assert.assertTrue(secret.isDestroyed());
	}

	private void _assertStore(String identifier) throws Exception {
		long companyId = RandomTestUtil.randomLong();

		KeyReference keyReference = new KeyReference(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			KeyReference.Type.SECRET);

		Mockito.when(
			_secretManager.putSecret(Mockito.eq(companyId), Mockito.any())
		).thenReturn(
			keyReference
		);

		Assert.assertEquals(
			KeyReferenceUtil.toKeyReferenceString(keyReference),
			_secretResolverImpl.store(
				companyId, identifier, RandomTestUtil.randomString()));

		ArgumentCaptor<Secret> argumentCaptor = ArgumentCaptor.forClass(
			Secret.class);

		Mockito.verify(
			_secretManager
		).putSecret(
			Mockito.eq(companyId), argumentCaptor.capture()
		);

		Secret secret = argumentCaptor.getValue();

		KeyReference secretKeyReference = secret.getKeyReference();

		Assert.assertEquals(identifier, secretKeyReference.getIdentifier());
		Assert.assertEquals(
			StringPool.STAR, secretKeyReference.getProviderId());

		Assert.assertTrue(secret.isDestroyed());
	}

	private void _assertStoreKeepsValue(
		String identifier, String keyReferenceIdentifier) {

		String value = _toKeyReferenceString(keyReferenceIdentifier);

		Assert.assertEquals(
			value,
			_secretResolverImpl.store(
				RandomTestUtil.randomLong(), identifier, value));

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _assertStoreRejects(
		String identifier, String keyReferenceIdentifier) {

		Assert.assertThrows(
			SecretException.class,
			() -> _secretResolverImpl.store(
				RandomTestUtil.randomLong(), identifier,
				_toKeyReferenceString(keyReferenceIdentifier)));

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testResolveWithConfigurationSecretContext() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		String key = RandomTestUtil.randomString();
		String pid = RandomTestUtil.randomString();

		SecretContext secretContext =
			SecretContext.createConfigurationSecretContext(
				key,
				HashMapBuilder.<String, Object>put(
					ConfigurationAdmin.SERVICE_FACTORYPID, pid + ".scoped"
				).put(
					Constants.SERVICE_PID,
					pid + ".scoped~" + RandomTestUtil.randomString()
				).put(
					"companyId", companyId
				).build());

		_assertResolve(
			companyId,
			StringBundler.concat(
				"config/", pid, "/company/", companyId, StringPool.SLASH, key),
			companyId, secretContext);
		_assertResolveRejects(
			companyId, StringBundler.concat("config/", pid, "/system/0/", key),
			secretContext);

		String name = RandomTestUtil.randomString();

		_assertResolve(
			companyId,
			StringBundler.concat(
				"config/", pid, StringPool.SLASH, name, "/company/", companyId,
				StringPool.SLASH, key),
			companyId,
			SecretContext.createConfigurationSecretContext(
				key,
				HashMapBuilder.<String, Object>put(
					ConfigurationAdmin.SERVICE_FACTORYPID, pid
				).put(
					Constants.SERVICE_PID, pid + "~" + name
				).put(
					"companyId", companyId
				).build()));

		long groupId = RandomTestUtil.randomLong();

		_assertResolve(
			companyId,
			StringBundler.concat(
				"config/", pid, "/group/", groupId, StringPool.SLASH, key),
			companyId,
			SecretContext.createConfigurationSecretContext(
				key,
				HashMapBuilder.<String, Object>put(
					ConfigurationAdmin.SERVICE_FACTORYPID, pid + ".scoped"
				).put(
					Constants.SERVICE_PID,
					pid + ".scoped~" + RandomTestUtil.randomString()
				).put(
					"companyId", companyId
				).put(
					"groupId", groupId
				).build()));

		String portletInstanceId = RandomTestUtil.randomString();

		_assertResolve(
			companyId,
			StringBundler.concat(
				"config/", pid, "/portlet-instance/", portletInstanceId,
				StringPool.SLASH, key),
			CompanyConstants.SYSTEM,
			SecretContext.createConfigurationSecretContext(
				key,
				HashMapBuilder.<String, Object>put(
					ConfigurationAdmin.SERVICE_FACTORYPID, pid + ".scoped"
				).put(
					Constants.SERVICE_PID,
					pid + ".scoped~" + RandomTestUtil.randomString()
				).put(
					"portletInstanceId", portletInstanceId
				).build()));

		_assertResolve(
			companyId, StringBundler.concat("config/", pid, "/system/0/", key),
			CompanyConstants.SYSTEM,
			SecretContext.createConfigurationSecretContext(
				key,
				HashMapBuilder.<String, Object>put(
					Constants.SERVICE_PID, pid
				).build()));
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
		Assert.assertThrows(
			IllegalArgumentException.class,
			() -> _secretResolverImpl.store(
				RandomTestUtil.randomLong(),
				StringBundler.concat(
					RandomTestUtil.randomString(), StringPool.SLASH,
					RandomTestUtil.randomString()),
				RandomTestUtil.randomString()));

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testStoreWhenKeyReferenceIsInvalid() {
		Assert.assertThrows(
			SecretException.class,
			() -> _secretResolverImpl.store(
				RandomTestUtil.randomLong(),
				StringBundler.concat(
					"config/", RandomTestUtil.randomString(), StringPool.SLASH,
					RandomTestUtil.randomString()),
				"${secretRef:provider}"));

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testStoreWhenSecretManagerFails() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		SecretException secretException = new SecretException();

		Mockito.when(
			_secretManager.putSecret(Mockito.eq(companyId), Mockito.any())
		).thenThrow(
			secretException
		);

		Assert.assertSame(
			secretException,
			Assert.assertThrows(
				SecretException.class,
				() -> _secretResolverImpl.store(
					companyId,
					StringBundler.concat(
						"config/", RandomTestUtil.randomString(),
						StringPool.SLASH, RandomTestUtil.randomString()),
					RandomTestUtil.randomString())));
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
			StringBundler.concat(
				"config/", scope, StringPool.SLASH,
				RandomTestUtil.randomString()),
			StringBundler.concat(
				"config/", scope, StringPool.SLASH,
				RandomTestUtil.randomString()));
		_assertStoreRejects(
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
			StringBundler.concat("config/", scope, StringPool.SLASH, key),
			StringBundler.concat("preference/", scope, StringPool.SLASH, key));
		_assertStoreRejects(
			StringBundler.concat("preference/", scope, StringPool.SLASH, key),
			StringBundler.concat("config/", scope, StringPool.SLASH, key));
	}

	private void _testStoreWhenValueReferencesSameKeyInAnotherScope() {
		String key = RandomTestUtil.randomString();

		_assertStoreRejects(
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

	private void _testStoreWithSecretContext() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		String key = RandomTestUtil.randomString();
		String pid = RandomTestUtil.randomString();

		_assertStore(
			companyId,
			StringBundler.concat(
				"config/", pid, "/company/", companyId, StringPool.SLASH, key),
			SecretContext.createConfigurationSecretContext(
				key,
				HashMapBuilder.<String, Object>put(
					Constants.SERVICE_PID, pid
				).put(
					"companyId", companyId
				).build()));
	}

	private void _testStoreWithSecretContextWhenFIPSIsDisabled()
		throws Exception {

		Mockito.clearInvocations(_secretManager);

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", false)) {

			String value = RandomTestUtil.randomString();

			Assert.assertEquals(
				value,
				_secretResolverImpl.store(
					RandomTestUtil.randomLong(),
					SecretContext.createConfigurationSecretContext(
						RandomTestUtil.randomString(), Collections.emptyMap()),
					value));
		}

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testStoreWithSecretContextWhenValueReferencesAnotherContext() {
		Mockito.clearInvocations(_secretManager);

		try (MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic =
				Mockito.mockStatic(FIPSAuditUtil.class)) {

			long companyId = RandomTestUtil.randomLong();
			String key = RandomTestUtil.randomString();
			String pid = RandomTestUtil.randomString();

			String identifier = StringBundler.concat(
				"config/", pid, "/company/", RandomTestUtil.randomLong(),
				StringPool.SLASH, key);
			SecretContext secretContext =
				SecretContext.createConfigurationSecretContext(
					key,
					HashMapBuilder.<String, Object>put(
						Constants.SERVICE_PID, pid
					).put(
						"companyId", companyId
					).build());

			Assert.assertThrows(
				SecretException.class,
				() -> _secretResolverImpl.store(
					companyId, secretContext,
					_toKeyReferenceString(identifier)));

			_assertFIPSAuditEvent(
				companyId, fipsAuditUtilMockedStatic, identifier, "store");
		}

		Mockito.verifyNoInteractions(_secretManager);
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
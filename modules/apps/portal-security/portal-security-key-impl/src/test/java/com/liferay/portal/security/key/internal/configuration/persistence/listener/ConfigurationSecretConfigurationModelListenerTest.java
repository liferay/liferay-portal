/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.configuration.persistence.listener;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeInformation;
import com.liferay.portal.configuration.metatype.definitions.ExtendedMetaTypeService;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Pedro Victor Silvestre
 */
public class ConfigurationSecretConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		_setUpKeyManagerProfileRegistry(_keyManagerProfile);

		Mockito.when(
			_bundleContext.getBundles()
		).thenReturn(
			new Bundle[] {_bundle}
		);

		Mockito.doReturn(
			SecretResolver.class
		).when(
			_bundle
		).loadClass(
			Mockito.anyString()
		);

		Mockito.when(
			_extendedMetaTypeService.getMetaTypeInformation(_bundle)
		).thenReturn(
			_extendedMetaTypeInformation
		);

		Mockito.when(
			_extendedMetaTypeInformation.getFactoryPids()
		).thenReturn(
			new String[0]
		);

		Mockito.when(
			_extendedMetaTypeInformation.getObjectClassDefinition(_PID, null)
		).thenReturn(
			_extendedObjectClassDefinition
		);

		Mockito.when(
			_extendedMetaTypeInformation.getPids()
		).thenReturn(
			new String[] {_PID}
		);

		ExtendedAttributeDefinition[] extendedAttributeDefinitions = {
			_createExtendedAttributeDefinition(
				"credential", AttributeDefinition.PASSWORD),
			_createExtendedAttributeDefinition(
				"host", AttributeDefinition.STRING)
		};

		Mockito.when(
			_extendedObjectClassDefinition.getAttributeDefinitions(
				ObjectClassDefinition.ALL)
		).thenReturn(
			extendedAttributeDefinitions
		);

		ReflectionTestUtil.setFieldValue(
			_configurationSecretConfigurationModelListener, "_bundleContext",
			_bundleContext);
		ReflectionTestUtil.setFieldValue(
			_configurationSecretConfigurationModelListener,
			"_extendedMetaTypeService", _extendedMetaTypeService);
		ReflectionTestUtil.setFieldValue(
			_configurationSecretConfigurationModelListener, "_secretManager",
			_secretManager);
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		_testOnBeforeSave();
		_testOnBeforeSaveWhenBundleCannotResolveKeyReference();
		_testOnBeforeSaveWhenConfigurationHasNoMetatype();
		_testOnBeforeSaveWhenKeyManagerProfileIsInactive();
		_testOnBeforeSaveWhenReferenceIsNotAConfiguration();
		_testOnBeforeSaveWhenReferenceNamesAnotherConfiguration();
		_testOnBeforeSaveWhenScopeIsCompany();
		_testOnBeforeSaveWhenScopeIsGroup();
		_testOnBeforeSaveWhenValueIsAlreadyVaulted();
		_testOnBeforeSaveWhenVaultIsUnavailable();
	}

	private ExtendedAttributeDefinition _createExtendedAttributeDefinition(
		String id, int type) {

		ExtendedAttributeDefinition extendedAttributeDefinition = Mockito.mock(
			ExtendedAttributeDefinition.class);

		Mockito.when(
			extendedAttributeDefinition.getID()
		).thenReturn(
			id
		);

		Mockito.when(
			extendedAttributeDefinition.getType()
		).thenReturn(
			type
		);

		return extendedAttributeDefinition;
	}

	private void _setUpKeyManagerProfileRegistry(
		KeyManagerProfile keyManagerProfile) {

		KeyManagerProfileRegistry keyManagerProfileRegistry = Mockito.mock(
			KeyManagerProfileRegistry.class);

		Mockito.when(
			keyManagerProfileRegistry.getActiveKeyManagerProfile()
		).thenReturn(
			keyManagerProfile
		);

		ReflectionTestUtil.setFieldValue(
			_configurationSecretConfigurationModelListener,
			"_keyManagerProfileRegistry", keyManagerProfileRegistry);
	}

	private void _testOnBeforeSave() throws Exception {
		setUp();

		String host = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).put(
				"host", host
			).build();

		KeyReference keyReference = new KeyReference(
			_IDENTIFIER, "provider", KeyReference.Type.SECRET);

		AtomicReference<Secret> atomicReference = new AtomicReference<>();

		Mockito.when(
			_secretManager.putSecret(
				Mockito.eq(CompanyConstants.SYSTEM), Mockito.any())
		).thenAnswer(
			invocationOnMock -> {
				atomicReference.set(invocationOnMock.getArgument(1));

				return keyReference;
			}
		);

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID, properties);

		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			keyReference);

		Assert.assertEquals(host, properties.get("host"));
		Assert.assertEquals(keyReferenceString, properties.get("credential"));

		Secret secret = atomicReference.get();

		KeyReference secretKeyReference = secret.getKeyReference();

		Assert.assertEquals(_IDENTIFIER, secretKeyReference.getIdentifier());
		Assert.assertEquals(
			StringPool.STAR, secretKeyReference.getProviderId());

		Assert.assertTrue(secret.isDestroyed());
	}

	private void _testOnBeforeSaveWhenBundleCannotResolveKeyReference()
		throws Exception {

		setUp();

		Mockito.doThrow(
			ClassNotFoundException.class
		).when(
			_bundle
		).loadClass(
			Mockito.anyString()
		);

		String value = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).build();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID, properties);

		Assert.assertEquals(value, properties.get("credential"));

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testOnBeforeSaveWhenConfigurationHasNoMetatype()
		throws Exception {

		setUp();

		String value = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).build();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(), properties);

		Assert.assertEquals(value, properties.get("credential"));

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testOnBeforeSaveWhenKeyManagerProfileIsInactive()
		throws Exception {

		setUp();

		_setUpKeyManagerProfileRegistry(null);

		String value = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).build();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID, properties);

		Assert.assertEquals(value, properties.get("credential"));

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testOnBeforeSaveWhenReferenceIsNotAConfiguration()
		throws Exception {

		setUp();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", "${secretRef:provider:oauth2/1234/clientSecret}"
			).build();

		Assert.assertThrows(
			ConfigurationModelListenerException.class,
			() -> _configurationSecretConfigurationModelListener.onBeforeSave(
				_PID, properties));
	}

	private void _testOnBeforeSaveWhenReferenceNamesAnotherConfiguration()
		throws Exception {

		setUp();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential",
				"${secretRef:provider:config/com.liferay.other/0/credential}"
			).build();

		Assert.assertThrows(
			ConfigurationModelListenerException.class,
			() -> _configurationSecretConfigurationModelListener.onBeforeSave(
				_PID, properties));
	}

	private void _testOnBeforeSaveWhenScopeIsCompany() throws Exception {
		setUp();

		long companyId = RandomTestUtil.randomLong();

		AtomicReference<Secret> atomicReference = new AtomicReference<>();

		Mockito.when(
			_secretManager.putSecret(Mockito.eq(companyId), Mockito.any())
		).thenAnswer(
			invocationOnMock -> {
				atomicReference.set(invocationOnMock.getArgument(1));

				return new KeyReference(
					StringBundler.concat(
						_IDENTIFIER_PREFIX, _PID, StringPool.SLASH, companyId,
						"/credential"),
					"provider", KeyReference.Type.SECRET);
			}
		);

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID + ".scoped~" + _FACTORY_SUFFIX,
			HashMapDictionaryBuilder.<String, Object>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, _PID + ".scoped"
			).put(
				"companyId", companyId
			).put(
				"credential", RandomTestUtil.randomString()
			).build());

		Secret secret = atomicReference.get();

		KeyReference keyReference = secret.getKeyReference();

		Assert.assertEquals(
			StringBundler.concat(
				_IDENTIFIER_PREFIX, _PID, ".scoped/", _FACTORY_SUFFIX,
				StringPool.SLASH, companyId, "/credential"),
			keyReference.getIdentifier());
	}

	private void _testOnBeforeSaveWhenScopeIsGroup() throws Exception {
		setUp();

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			_secretManager.putSecret(Mockito.eq(companyId), Mockito.any())
		).thenReturn(
			new KeyReference(_IDENTIFIER, "provider", KeyReference.Type.SECRET)
		);

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", companyId
			).put(
				"credential", RandomTestUtil.randomString()
			).put(
				"groupId", RandomTestUtil.randomLong()
			).build());

		Mockito.verify(
			_secretManager
		).putSecret(
			Mockito.eq(companyId), Mockito.any()
		);
	}

	private void _testOnBeforeSaveWhenValueIsAlreadyVaulted() throws Exception {
		setUp();

		String value = "${secretRef:provider:config/" + _PID + "/0/credential}";

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).build();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID, properties);

		Assert.assertEquals(value, properties.get("credential"));

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testOnBeforeSaveWhenVaultIsUnavailable() throws Exception {
		setUp();

		Mockito.when(
			_secretManager.putSecret(Mockito.anyLong(), Mockito.any())
		).thenThrow(
			new SecretException("Unable to put secret")
		);

		String value = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).build();

		Assert.assertThrows(
			ConfigurationModelListenerException.class,
			() -> _configurationSecretConfigurationModelListener.onBeforeSave(
				_PID, properties));

		Assert.assertEquals(value, properties.get("credential"));
	}

	private static final String _FACTORY_SUFFIX = RandomTestUtil.randomString();

	private static final String _IDENTIFIER =
		"config/com.liferay.test.Configuration/0/credential";

	private static final String _IDENTIFIER_PREFIX = "config/";

	private static final String _PID = "com.liferay.test.Configuration";

	@Mock
	private Bundle _bundle;

	@Mock
	private BundleContext _bundleContext;

	private final ConfigurationSecretConfigurationModelListener
		_configurationSecretConfigurationModelListener =
			new ConfigurationSecretConfigurationModelListener();

	@Mock
	private ExtendedMetaTypeInformation _extendedMetaTypeInformation;

	@Mock
	private ExtendedMetaTypeService _extendedMetaTypeService;

	@Mock
	private ExtendedObjectClassDefinition _extendedObjectClassDefinition;

	@Mock
	private KeyManagerProfile _keyManagerProfile;

	@Mock
	private SecretManager _secretManager;

}
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
import com.liferay.portal.kernel.security.fips.FIPSAuditEvent;
import com.liferay.portal.kernel.security.fips.FIPSAuditUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfile;
import com.liferay.portal.security.key.spi.profile.KeyManagerProfileRegistry;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;

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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
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
			"_configurationAdmin", _configurationAdmin);
		ReflectionTestUtil.setFieldValue(
			_configurationSecretConfigurationModelListener,
			"_extendedMetaTypeService", _extendedMetaTypeService);
		ReflectionTestUtil.setFieldValue(
			_configurationSecretConfigurationModelListener, "_secretManager",
			_secretManager);
		ReflectionTestUtil.setFieldValue(
			_configurationSecretConfigurationModelListener, "_secretResolver",
			_secretResolver);
	}

	@Test
	public void testOnBeforeDelete() throws Exception {
		_testOnBeforeDelete();
		_testOnBeforeDeleteWhenDeleteFails();

		Mockito.reset(_secretManager);

		_testOnBeforeDeleteWhenKeyManagerProfileIsInactive();

		_setUpKeyManagerProfileRegistry(_keyManagerProfile);

		_testOnBeforeDeleteWhenPropertiesAreNull();
		_testOnBeforeDeleteWhenReferenceIsNotAConfiguration();
	}

	@Test
	public void testOnBeforeSave() throws Exception {
		_testOnBeforeSave();

		Mockito.clearInvocations(_secretResolver);

		_testOnBeforeSaveWhenBundleIsInStaticRegion();

		Mockito.when(
			_bundle.getLocation()
		).thenReturn(
			null
		);

		_testOnBeforeSaveWhenConfigurationHasNoMetatype();
		_testOnBeforeSaveWhenKeyManagerProfileIsInactive();

		_setUpKeyManagerProfileRegistry(_keyManagerProfile);

		_testOnBeforeSaveWhenReferenceIsRejected();
		_testOnBeforeSaveWhenScopeIsCompany();
		_testOnBeforeSaveWhenScopeIsGroup();
		_testOnBeforeSaveWhenStoreFails();
	}

	private void _assertOnBeforeSaveFails(String value) throws Exception {
		SecretException secretException = new SecretException(
			RandomTestUtil.randomString());

		Mockito.when(
			_secretResolver.store(
				Mockito.anyLong(), Mockito.anyString(), Mockito.eq(value))
		).thenAnswer(
			invocationOnMock -> {
				throw secretException;
			}
		);

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).build();

		try (MockedStatic<FIPSAuditUtil> fipsAuditUtilMockedStatic =
				Mockito.mockStatic(FIPSAuditUtil.class)) {

			ConfigurationModelListenerException
				configurationModelListenerException = Assert.assertThrows(
					ConfigurationModelListenerException.class,
					() ->
						_configurationSecretConfigurationModelListener.
							onBeforeSave(_PID, properties));

			Assert.assertSame(
				secretException,
				configurationModelListenerException.getCause());

			ArgumentCaptor<FIPSAuditEvent> argumentCaptor =
				ArgumentCaptor.forClass(FIPSAuditEvent.class);

			fipsAuditUtilMockedStatic.verify(
				() -> FIPSAuditUtil.write(argumentCaptor.capture()));

			FIPSAuditEvent fipsAuditEvent = argumentCaptor.getValue();

			Assert.assertEquals(
				"configuration-secret-store-failure",
				fipsAuditEvent.getEventType());
			Assert.assertEquals(
				HashMapBuilder.<String, Object>put(
					"configuration-pid", _PID
				).put(
					"property-id", "credential"
				).build(),
				fipsAuditEvent.getFields());
		}

		Assert.assertEquals(value, properties.get("credential"));
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

	private void _testOnBeforeDelete() throws Exception {
		KeyReference keyReference = new KeyReference(
			"config/" + _PID + "/0/credential", "provider",
			KeyReference.Type.SECRET);

		Mockito.when(
			_configuration.getProperties()
		).thenReturn(
			HashMapDictionaryBuilder.<String, Object>put(
				"credential",
				KeyReferenceUtil.toKeyReferenceString(keyReference)
			).put(
				"host", RandomTestUtil.randomString()
			).build()
		);

		Mockito.when(
			_configurationAdmin.listConfigurations("(service.pid=" + _PID + ")")
		).thenReturn(
			new Configuration[] {_configuration}
		);

		_configurationSecretConfigurationModelListener.onBeforeDelete(_PID);

		Mockito.verify(
			_secretManager
		).deleteSecret(
			CompanyConstants.SYSTEM, keyReference
		);
	}

	private void _testOnBeforeDeleteWhenDeleteFails() throws Exception {
		KeyReference keyReference1 = new KeyReference(
			"config/" + _PID + "/0/credential", "provider",
			KeyReference.Type.SECRET);

		Mockito.doThrow(
			SecretException.class
		).when(
			_secretManager
		).deleteSecret(
			CompanyConstants.SYSTEM, keyReference1
		);

		KeyReference keyReference2 = new KeyReference(
			"config/" + _PID + "/0/host", "provider", KeyReference.Type.SECRET);

		Mockito.when(
			_configuration.getProperties()
		).thenReturn(
			HashMapDictionaryBuilder.<String, Object>put(
				"credential",
				KeyReferenceUtil.toKeyReferenceString(keyReference1)
			).put(
				"host", KeyReferenceUtil.toKeyReferenceString(keyReference2)
			).build()
		);

		Mockito.when(
			_configurationAdmin.listConfigurations("(service.pid=" + _PID + ")")
		).thenReturn(
			new Configuration[] {_configuration}
		);

		_configurationSecretConfigurationModelListener.onBeforeDelete(_PID);

		Mockito.verify(
			_secretManager
		).deleteSecret(
			CompanyConstants.SYSTEM, keyReference2
		);
	}

	private void _testOnBeforeDeleteWhenKeyManagerProfileIsInactive()
		throws Exception {

		_setUpKeyManagerProfileRegistry(null);

		_configurationSecretConfigurationModelListener.onBeforeDelete(_PID);

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testOnBeforeDeleteWhenPropertiesAreNull() throws Exception {
		Mockito.when(
			_configuration.getProperties()
		).thenReturn(
			null
		);

		Mockito.when(
			_configurationAdmin.listConfigurations("(service.pid=" + _PID + ")")
		).thenReturn(
			new Configuration[] {_configuration}
		);

		_configurationSecretConfigurationModelListener.onBeforeDelete(_PID);

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testOnBeforeDeleteWhenReferenceIsNotAConfiguration()
		throws Exception {

		Mockito.when(
			_configuration.getProperties()
		).thenReturn(
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", "${secretRef:provider:oauth2/1234/clientSecret}"
			).build()
		);

		Mockito.when(
			_configurationAdmin.listConfigurations("(service.pid=" + _PID + ")")
		).thenReturn(
			new Configuration[] {_configuration}
		);

		_configurationSecretConfigurationModelListener.onBeforeDelete(_PID);

		Mockito.verifyNoInteractions(_secretManager);
	}

	private void _testOnBeforeSave() throws Exception {
		String storedValue = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		Mockito.when(
			_secretResolver.store(
				CompanyConstants.SYSTEM, "config/" + _PID + "/0/credential",
				value)
		).thenReturn(
			storedValue
		);

		String host = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).put(
				"host", host
			).build();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID, properties);

		Assert.assertEquals(storedValue, properties.get("credential"));
		Assert.assertEquals(host, properties.get("host"));
	}

	private void _testOnBeforeSaveWhenBundleIsInStaticRegion()
		throws Exception {

		Mockito.when(
			_bundle.getLocation()
		).thenReturn(
			"file:/opt/liferay/osgi/static/com.liferay.example.jar" +
				"?protocol=jar&static=true"
		);

		String value = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).build();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID, properties);

		Assert.assertEquals(value, properties.get("credential"));

		Mockito.verifyNoInteractions(_secretResolver);
	}

	private void _testOnBeforeSaveWhenConfigurationHasNoMetatype()
		throws Exception {

		String value = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).build();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			RandomTestUtil.randomString(), properties);

		Assert.assertEquals(value, properties.get("credential"));

		Mockito.verifyNoInteractions(_secretResolver);
	}

	private void _testOnBeforeSaveWhenKeyManagerProfileIsInactive()
		throws Exception {

		_setUpKeyManagerProfileRegistry(null);

		String value = RandomTestUtil.randomString();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"credential", value
			).build();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID, properties);

		Assert.assertEquals(value, properties.get("credential"));

		Mockito.verifyNoInteractions(_secretResolver);
	}

	private void _testOnBeforeSaveWhenReferenceIsRejected() throws Exception {
		_assertOnBeforeSaveFails(
			"${secretRef:provider:config/com.liferay.other/0/credential}");
	}

	private void _testOnBeforeSaveWhenScopeIsCompany() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		String value = RandomTestUtil.randomString();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID + ".scoped~" + _FACTORY_SUFFIX,
			HashMapDictionaryBuilder.<String, Object>put(
				ConfigurationAdmin.SERVICE_FACTORYPID, _PID + ".scoped"
			).put(
				"companyId", companyId
			).put(
				"credential", value
			).build());

		Mockito.verify(
			_secretResolver
		).store(
			companyId,
			StringBundler.concat(
				"config/", _PID, ".scoped/", _FACTORY_SUFFIX, StringPool.SLASH,
				companyId, "/credential"),
			value
		);
	}

	private void _testOnBeforeSaveWhenScopeIsGroup() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		String value = RandomTestUtil.randomString();

		_configurationSecretConfigurationModelListener.onBeforeSave(
			_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", companyId
			).put(
				"credential", value
			).put(
				"groupId", RandomTestUtil.randomLong()
			).build());

		Mockito.verify(
			_secretResolver
		).store(
			companyId,
			StringBundler.concat(
				"config/", _PID, StringPool.SLASH, companyId, "/credential"),
			value
		);
	}

	private void _testOnBeforeSaveWhenStoreFails() throws Exception {
		_assertOnBeforeSaveFails(RandomTestUtil.randomString());
	}

	private static final String _FACTORY_SUFFIX = RandomTestUtil.randomString();

	private static final String _PID = "com.liferay.test.Configuration";

	@Mock
	private Bundle _bundle;

	@Mock
	private BundleContext _bundleContext;

	@Mock
	private Configuration _configuration;

	@Mock
	private ConfigurationAdmin _configurationAdmin;

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

	@Mock
	private SecretResolver _secretResolver;

}
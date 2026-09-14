/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.SecretManager;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.secret.SecretProvider;
import com.liferay.portal.security.key.test.util.TestSecretProvider;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class SecretResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_secretProviderServiceRegistration = _bundleContext.registerService(
			SecretProvider.class, new TestSecretProvider(_SECRET_PROVIDER_ID),
			HashMapDictionaryBuilder.<String, Object>put(
				"secret.provider.id", _SECRET_PROVIDER_ID
			).build());

		ConfigurationTestUtil.saveConfiguration(
			_KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"companySecretProviderId", _SECRET_PROVIDER_ID
			).put(
				"systemSecretProviderId", _SECRET_PROVIDER_ID
			).build());
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			_KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID);

		if (_secretProviderServiceRegistration != null) {
			_secretProviderServiceRegistration.unregister();
		}
	}

	@Test
	public void testResolve() throws Exception {
		long companyId = TestPropsValues.getCompanyId();
		String identifier = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		KeyReference keyReference = _putSecret(companyId, identifier, value);

		Assert.assertEquals(identifier, keyReference.getIdentifier());
		Assert.assertEquals(_SECRET_PROVIDER_ID, keyReference.getProviderId());
		Assert.assertEquals(KeyReference.Type.SECRET, keyReference.getType());

		Assert.assertEquals(
			value,
			_secretResolver.resolve(
				companyId,
				KeyReferenceUtil.toKeyReferenceString(keyReference)));
	}

	@Test
	public void testResolveUnderAnotherCompany() throws Exception {
		KeyReference keyReference = _putSecret(
			TestPropsValues.getCompanyId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		Assert.assertThrows(
			SecretException.class,
			() -> _secretResolver.resolve(
				CompanyConstants.SYSTEM,
				KeyReferenceUtil.toKeyReferenceString(keyReference)));
	}

	private KeyReference _putSecret(
			long companyId, String identifier, String value)
		throws Exception {

		try (Secret secret = new Secret(
				new KeyReference(
					identifier, StringPool.STAR, KeyReference.Type.SECRET),
				value)) {

			return _secretManager.putSecret(companyId, secret);
		}
	}

	private static final String _KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID =
		"com.liferay.portal.security.key.internal.profile.configuration." +
			"KeyManagerCustomProfileConfiguration";

	private static final String _SECRET_PROVIDER_ID = "test-key-secret";

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	@Inject
	private SecretManager _secretManager;

	private ServiceRegistration<SecretProvider>
		_secretProviderServiceRegistration;

	@Inject
	private SecretResolver _secretResolver;

}
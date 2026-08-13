/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.constants.GrantType;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.Secret;
import com.liferay.portal.security.key.secret.exception.SecretException;
import com.liferay.portal.security.key.spi.ProviderStatus;
import com.liferay.portal.security.key.spi.secret.SecretProvider;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class OAuth2ApplicationLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_serviceRegistration = _bundleContext.registerService(
			SecretProvider.class, new TestSecretProvider(),
			HashMapDictionaryBuilder.<String, Object>put(
				"secret.provider.id", _PROVIDER_ID
			).build());

		ConfigurationTestUtil.saveConfiguration(
			_KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"companySecretProviderId", _PROVIDER_ID
			).put(
				"systemSecretProviderId", _PROVIDER_ID
			).build());

		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(
			_KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID);

		_serviceRegistration.unregister();
	}

	@Test
	public void testResolveClientSecret() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			String clientSecret = RandomTestUtil.randomString();

			_oAuth2Application = _addOAuth2Application(clientSecret);

			Assert.assertEquals(
				clientSecret,
				_oAuth2ApplicationLocalService.resolveClientSecret(
					_oAuth2Application));

			String storedClientSecret = _oAuth2Application.getClientSecret();

			Assert.assertTrue(
				storedClientSecret,
				KeyReferenceUtil.isKeyReference(storedClientSecret));
		}
	}

	@Test
	public void testResolveClientSecretWhenDisabled() throws Exception {
		String clientSecret = RandomTestUtil.randomString();

		_oAuth2Application = _addOAuth2Application(clientSecret);

		Assert.assertEquals(clientSecret, _oAuth2Application.getClientSecret());
		Assert.assertEquals(
			clientSecret,
			_oAuth2ApplicationLocalService.resolveClientSecret(
				_oAuth2Application));
	}

	private OAuth2Application _addOAuth2Application(String clientSecret)
		throws Exception {

		return _oAuth2ApplicationLocalService.addOAuth2Application(
			TestPropsValues.getCompanyId(), _user.getUserId(),
			_user.getFullName(),
			ListUtil.fromArray(GrantType.CLIENT_CREDENTIALS),
			"client_secret_post", _user.getUserId(), null, 0, clientSecret,
			null, null, null, 0, null, RandomTestUtil.randomString(), null,
			null, false, null, false, new ServiceContext());
	}

	private static final String _KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID =
		"com.liferay.portal.security.key.internal.profile.configuration." +
			"KeyManagerCustomProfileConfiguration";

	private static final String _PROVIDER_ID = "test-oauth2-secret";

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	@DeleteAfterTestRun
	private OAuth2Application _oAuth2Application;

	@Inject
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	private ServiceRegistration<SecretProvider> _serviceRegistration;

	@DeleteAfterTestRun
	private User _user;

	private static class TestSecretProvider implements SecretProvider {

		@Override
		public void deleteSecret(long companyId, String secretIdentifier) {
			_secrets.remove(_key(companyId, secretIdentifier));
		}

		@Override
		public ProviderStatus getProviderStatus() {
			return ProviderStatus.OPERATIONAL;
		}

		@Override
		public Secret getSecret(long companyId, String secretIdentifier)
			throws SecretException {

			String value = _secrets.get(_key(companyId, secretIdentifier));

			if (value == null) {
				throw new SecretException(
					"No secret found for identifier " + secretIdentifier);
			}

			return new Secret(
				new KeyReference(
					secretIdentifier, _PROVIDER_ID, KeyReference.Type.SECRET),
				value);
		}

		@Override
		public List<String> getSecretIdentifiers(long companyId) {
			List<String> secretIdentifiers = new ArrayList<>();

			String prefix = companyId + StringPool.SLASH;

			for (String key : _secrets.keySet()) {
				if (key.startsWith(prefix)) {
					secretIdentifiers.add(key.substring(prefix.length()));
				}
			}

			return secretIdentifiers;
		}

		@Override
		public boolean isAllowedCompany(long companyId) {
			return true;
		}

		@Override
		public void putSecret(long companyId, Secret secret) {
			KeyReference keyReference = secret.getKeyReference();

			_secrets.put(
				_key(companyId, keyReference.getIdentifier()),
				new String(secret.getChars()));
		}

		private String _key(long companyId, String secretIdentifier) {
			return companyId + StringPool.SLASH + secretIdentifier;
		}

		private final Map<String, String> _secrets = new ConcurrentHashMap<>();

	}

}
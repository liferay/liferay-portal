/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.multi.factor.authentication.timebased.otp.model.MFATimeBasedOTPEntry;
import com.liferay.multi.factor.authentication.timebased.otp.service.MFATimeBasedOTPEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
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
 * @author Pedro Victor Silvestre
 */
@RunWith(Arquillian.class)
public class MFATimeBasedOTPEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_serviceRegistration = _bundleContext.registerService(
			SecretProvider.class, _testSecretProvider,
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
		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
			_mfaTimeBasedOTPEntryLocalService.fetchMFATimeBasedOTPEntryByUserId(
				_user.getUserId());

		if (mfaTimeBasedOTPEntry != null) {
			_mfaTimeBasedOTPEntryLocalService.deleteMFATimeBasedOTPEntry(
				mfaTimeBasedOTPEntry);
		}

		ConfigurationTestUtil.deleteConfiguration(
			_KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID);

		_serviceRegistration.unregister();
	}

	@Test
	public void testAddTimeBasedOTPEntryWhenPutSecretFails() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			_testSecretProvider.setPutSecretFailure(true);

			try {
				_mfaTimeBasedOTPEntryLocalService.addTimeBasedOTPEntry(
					_user.getUserId(), RandomTestUtil.randomString());

				Assert.fail();
			}
			catch (SecretException secretException) {
				Assert.assertEquals(
					_PUT_SECRET_FAILURE_MESSAGE, secretException.getMessage());
			}

			Assert.assertNull(
				_mfaTimeBasedOTPEntryLocalService.
					fetchMFATimeBasedOTPEntryByUserId(_user.getUserId()));
		}
	}

	@Test
	public void testAddTimeBasedOTPEntryWhenSharedSecretIsKeyReference()
		throws Exception {

		try {
			_mfaTimeBasedOTPEntryLocalService.addTimeBasedOTPEntry(
				_user.getUserId(),
				KeyReferenceUtil.toKeyReferenceString(
					new KeyReference(
						RandomTestUtil.randomString(), StringPool.STAR,
						KeyReference.Type.SECRET)));

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertEquals(
				"Shared secret cannot begin with a reserved key reference " +
					"prefix",
				portalException.getMessage());
		}
	}

	@Test
	public void testDeleteMFATimeBasedOTPEntry() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
				_mfaTimeBasedOTPEntryLocalService.addTimeBasedOTPEntry(
					_user.getUserId(), RandomTestUtil.randomString());

			long companyId = mfaTimeBasedOTPEntry.getCompanyId();
			String secretIdentifier = _getSecretIdentifier(
				mfaTimeBasedOTPEntry);

			Assert.assertTrue(
				_testSecretProvider.getSecretIdentifiers(
					companyId
				).contains(
					secretIdentifier
				));

			_mfaTimeBasedOTPEntryLocalService.deleteMFATimeBasedOTPEntry(
				mfaTimeBasedOTPEntry);

			Assert.assertFalse(
				_testSecretProvider.getSecretIdentifiers(
					companyId
				).contains(
					secretIdentifier
				));
		}
	}

	@Test
	public void testDeleteMFATimeBasedOTPEntryByPrimaryKey() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
				_mfaTimeBasedOTPEntryLocalService.addTimeBasedOTPEntry(
					_user.getUserId(), RandomTestUtil.randomString());

			long companyId = mfaTimeBasedOTPEntry.getCompanyId();
			String secretIdentifier = _getSecretIdentifier(
				mfaTimeBasedOTPEntry);

			_mfaTimeBasedOTPEntryLocalService.deleteMFATimeBasedOTPEntry(
				mfaTimeBasedOTPEntry.getMfaTimeBasedOTPEntryId());

			Assert.assertFalse(
				_testSecretProvider.getSecretIdentifiers(
					companyId
				).contains(
					secretIdentifier
				));
		}
	}

	@Test
	public void testGetPlaintextSharedSecret() throws Exception {
		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			String sharedSecret = RandomTestUtil.randomString();

			MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
				_mfaTimeBasedOTPEntryLocalService.addTimeBasedOTPEntry(
					_user.getUserId(), sharedSecret);

			Assert.assertEquals(
				sharedSecret,
				_mfaTimeBasedOTPEntryLocalService.getPlaintextSharedSecret(
					mfaTimeBasedOTPEntry));
			Assert.assertTrue(
				KeyReferenceUtil.isKeyReference(
					mfaTimeBasedOTPEntry.getSharedSecret()));
		}
	}

	@Test
	public void testGetPlaintextSharedSecretWhenFIPSIsDisabled()
		throws Exception {

		String sharedSecret = RandomTestUtil.randomString();

		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
			_mfaTimeBasedOTPEntryLocalService.addTimeBasedOTPEntry(
				_user.getUserId(), sharedSecret);

		Assert.assertEquals(
			sharedSecret, mfaTimeBasedOTPEntry.getSharedSecret());
		Assert.assertEquals(
			sharedSecret,
			_mfaTimeBasedOTPEntryLocalService.getPlaintextSharedSecret(
				mfaTimeBasedOTPEntry));
	}

	@Test
	public void testGetPlaintextSharedSecretWhenSharedSecretIsPlaintext()
		throws Exception {

		String sharedSecret = RandomTestUtil.randomString();

		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry =
			_mfaTimeBasedOTPEntryLocalService.addTimeBasedOTPEntry(
				_user.getUserId(), sharedSecret);

		try (AutoCloseable autoCloseable =
				ReflectionTestUtil.setFieldValueWithAutoCloseable(
					PropsValues.class, "FIPS_ENABLED", true)) {

			Assert.assertEquals(
				sharedSecret,
				_mfaTimeBasedOTPEntryLocalService.getPlaintextSharedSecret(
					mfaTimeBasedOTPEntry));
		}
	}

	private String _getSecretIdentifier(
		MFATimeBasedOTPEntry mfaTimeBasedOTPEntry) {

		KeyReference keyReference = KeyReferenceUtil.toKeyReference(
			mfaTimeBasedOTPEntry.getSharedSecret());

		return keyReference.getIdentifier();
	}

	private static final String _KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID =
		"com.liferay.portal.security.key.internal.profile.configuration." +
			"KeyManagerCustomProfileConfiguration";

	private static final String _PROVIDER_ID = "test-mfa-timebased-otp-secret";

	private static final String _PUT_SECRET_FAILURE_MESSAGE =
		"Unable to put secret";

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	@Inject
	private MFATimeBasedOTPEntryLocalService _mfaTimeBasedOTPEntryLocalService;

	private ServiceRegistration<SecretProvider> _serviceRegistration;
	private final TestSecretProvider _testSecretProvider =
		new TestSecretProvider();

	@DeleteAfterTestRun
	private User _user;

	private static class TestSecretProvider implements SecretProvider {

		@Override
		public void deleteSecret(long companyId, String secretIdentifier) {
			_secrets.remove(_getKey(companyId, secretIdentifier));
		}

		@Override
		public ProviderStatus getProviderStatus() {
			return ProviderStatus.OPERATIONAL;
		}

		@Override
		public Secret getSecret(long companyId, String secretIdentifier)
			throws SecretException {

			String value = _secrets.get(_getKey(companyId, secretIdentifier));

			if (value == null) {
				throw new SecretException(
					"No secret was found for identifier \"" + secretIdentifier +
						"\"");
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
		public void putSecret(long companyId, Secret secret)
			throws SecretException {

			if (_putSecretFailure) {
				throw new SecretException(_PUT_SECRET_FAILURE_MESSAGE);
			}

			KeyReference keyReference = secret.getKeyReference();

			_secrets.put(
				_getKey(companyId, keyReference.getIdentifier()),
				new String(secret.getChars()));
		}

		public void setPutSecretFailure(boolean putSecretFailure) {
			_putSecretFailure = putSecretFailure;
		}

		private String _getKey(long companyId, String secretIdentifier) {
			return companyId + StringPool.SLASH + secretIdentifier;
		}

		private boolean _putSecretFailure;
		private final Map<String, String> _secrets = new ConcurrentHashMap<>();

	}

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.encryptor.CompanyKeyResolverUtil;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyInfo;
import com.liferay.portal.kernel.service.CompanyInfoLocalServiceUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.key.spi.crypto.CryptoProvider;
import com.liferay.portal.security.key.test.util.TestCompanyCryptoProvider;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.security.Key;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class CompanyKeyResolverUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_keyManagerConfigurationTemporarySwapper =
			new ConfigurationTemporarySwapper(
				_KEY_MANAGER_CONFIGURATION_PID,
				_getKeyManagerConfigurationProperties(
					TestCompanyCryptoProvider.KEY_IDENTIFIER));
		_keyManagerCustomProfileConfigurationTemporarySwapper =
			new ConfigurationTemporarySwapper(
				_KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID,
				_getKeyManagerCustomProfileConfigurationProperties(
					TestCompanyCryptoProvider.PROVIDER_ID));

		Bundle bundle = FrameworkUtil.getBundle(
			CompanyKeyResolverUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_testCompanyCryptoProvider = new TestCompanyCryptoProvider();

		_serviceRegistration = bundleContext.registerService(
			CryptoProvider.class, _testCompanyCryptoProvider,
			HashMapDictionaryBuilder.<String, Object>put(
				"crypto.provider.id", TestCompanyCryptoProvider.PROVIDER_ID
			).build());
	}

	@After
	public void tearDown() throws Exception {
		_keyManagerConfigurationTemporarySwapper.close();
		_keyManagerCustomProfileConfigurationTemporarySwapper.close();

		_serviceRegistration.unregister();
	}

	@Test
	public void testRotateKey() throws Exception {
		Company company = _addCompany();

		String keyString = company.getKey();

		Assert.assertFalse(_isFIPSKey(keyString));
		Assert.assertTrue(CompanyKeyResolverUtil.isWrappedKey(keyString));

		_saveKeyManagerConfiguration(StringPool.BLANK);

		Assert.assertEquals(keyString, _checkCompanyKey(company));

		Company legacyCompany = _addCompany();

		String legacyKeyString = legacyCompany.getKey();

		Assert.assertFalse(_isFIPSKey(legacyKeyString));
		Assert.assertFalse(
			CompanyKeyResolverUtil.isWrappedKey(legacyKeyString));

		_saveKeyManagerConfiguration(TestCompanyCryptoProvider.KEY_IDENTIFIER);
		_saveKeyManagerCustomProfileConfiguration(
			RandomTestUtil.randomString());

		Assert.assertEquals(legacyKeyString, _checkCompanyKey(legacyCompany));

		_saveKeyManagerCustomProfileConfiguration(
			TestCompanyCryptoProvider.PROVIDER_ID);

		String rotatedKeyString = _checkCompanyKey(company);

		Assert.assertEquals(rotatedKeyString, _checkCompanyKey(company));
		Assert.assertTrue(_isFIPSKey(rotatedKeyString));
		Assert.assertTrue(
			CompanyKeyResolverUtil.isWrappedKey(rotatedKeyString));

		Key unwrappedKey = CompanyKeyResolverUtil.unwrapKey(
			company.getCompanyId(), rotatedKeyString);

		byte[] encodedBytes = unwrappedKey.getEncoded();

		Assert.assertEquals(
			Arrays.toString(encodedBytes), 32, encodedBytes.length);

		_saveKeyManagerConfiguration(StringPool.BLANK);

		String rotatedLegacyKeyString = _checkCompanyKey(legacyCompany);

		Assert.assertTrue(_isFIPSKey(rotatedLegacyKeyString));
		Assert.assertFalse(
			CompanyKeyResolverUtil.isWrappedKey(rotatedLegacyKeyString));

		unwrappedKey = CompanyKeyResolverUtil.unwrapKey(
			legacyCompany.getCompanyId(), rotatedLegacyKeyString);

		encodedBytes = unwrappedKey.getEncoded();

		Assert.assertEquals(
			Arrays.toString(encodedBytes), 32, encodedBytes.length);
	}

	@Test
	public void testUnwrapKey() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		Key key = EncryptorUtil.generateKey();

		String keyString = _wrapKey(companyId, key);

		_saveKeyManagerConfiguration(TestCompanyCryptoProvider.KEY_IDENTIFIER);

		int decryptCount = _testCompanyCryptoProvider.getDecryptCount();
		Key unwrappedKey = CompanyKeyResolverUtil.unwrapKey(
			companyId, keyString);

		Assert.assertEquals(
			decryptCount + 1, _testCompanyCryptoProvider.getDecryptCount());
		Assert.assertEquals(key, unwrappedKey);

		unwrappedKey = CompanyKeyResolverUtil.unwrapKey(companyId, keyString);

		Assert.assertEquals(
			decryptCount + 1, _testCompanyCryptoProvider.getDecryptCount());
		Assert.assertEquals(key, unwrappedKey);

		key = EncryptorUtil.generateKey();

		unwrappedKey = CompanyKeyResolverUtil.unwrapKey(
			companyId, EncryptorUtil.serializeKey(key));

		Assert.assertEquals(
			decryptCount + 1, _testCompanyCryptoProvider.getDecryptCount());
		Assert.assertEquals(key, unwrappedKey);
	}

	@Test
	public void testWrapKey() throws Exception {
		Company company = _addCompany();

		Assert.assertTrue(
			CompanyKeyResolverUtil.isWrappedKey(company.getKey()));

		Key key = company.getKeyObj();

		Assert.assertNotNull(key);

		String plaintext = RandomTestUtil.randomString();

		Assert.assertEquals(
			plaintext,
			EncryptorUtil.decrypt(key, EncryptorUtil.encrypt(key, plaintext)));

		EntityCacheUtil.clearCache();

		Company persistedCompany = CompanyLocalServiceUtil.getCompany(
			company.getCompanyId());

		Assert.assertTrue(
			CompanyKeyResolverUtil.isWrappedKey(persistedCompany.getKey()));
		Assert.assertEquals(key, persistedCompany.getKeyObj());

		_saveKeyManagerConfiguration(StringPool.BLANK);

		Company legacyCompany = _addCompany();

		String legacyKeyString = legacyCompany.getKey();

		Assert.assertFalse(
			CompanyKeyResolverUtil.isWrappedKey(legacyKeyString));
		Assert.assertEquals(
			EncryptorUtil.deserializeKey(legacyKeyString),
			legacyCompany.getKeyObj());
	}

	private Company _addCompany() throws Exception {
		Company company = CompanyLocalServiceUtil.createCompany(
			CounterLocalServiceUtil.increment());

		company.setWebId(RandomTestUtil.randomString());

		company = CompanyLocalServiceUtil.updateCompany(company);

		_companies.add(company);

		CompanyLocalServiceUtil.checkCompanyKey(company.getCompanyId());

		return CompanyLocalServiceUtil.getCompany(company.getCompanyId());
	}

	private String _checkCompanyKey(Company company) throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			CompanyLocalServiceUtil.checkCompanyKey(company.getCompanyId());
		}

		EntityCacheUtil.clearCache();

		CompanyInfo companyInfo = CompanyInfoLocalServiceUtil.fetchCompany(
			company.getCompanyId());

		return companyInfo.getKey();
	}

	private Dictionary<String, Object> _getKeyManagerConfigurationProperties(
		String companyKEKIdentifier) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"activeProfileId", "custom"
		).put(
			"companyKEKIdentifier", companyKEKIdentifier
		).build();
	}

	private Dictionary<String, Object>
		_getKeyManagerCustomProfileConfigurationProperties(
			String companyKEKProviderId) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"companyKEKProviderId", companyKEKProviderId
		).build();
	}

	private boolean _isFIPSKey(String keyString) {
		return ReflectionTestUtil.invoke(
			CompanyKeyResolverUtil.class, "_isFIPSKey",
			new Class<?>[] {String.class}, keyString);
	}

	private void _saveKeyManagerConfiguration(String companyKEKIdentifier)
		throws Exception {

		ConfigurationTestUtil.saveConfiguration(
			_KEY_MANAGER_CONFIGURATION_PID,
			_getKeyManagerConfigurationProperties(companyKEKIdentifier));
	}

	private void _saveKeyManagerCustomProfileConfiguration(
			String companyKEKProviderId)
		throws Exception {

		ConfigurationTestUtil.saveConfiguration(
			_KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID,
			_getKeyManagerCustomProfileConfigurationProperties(
				companyKEKProviderId));
	}

	private String _wrapKey(long companyId, Key key) {
		return ReflectionTestUtil.invoke(
			CompanyKeyResolverUtil.class, "_wrapKey",
			new Class<?>[] {long.class, Key.class}, companyId, key);
	}

	private static final String _KEY_MANAGER_CONFIGURATION_PID =
		"com.liferay.portal.security.key.internal.profile.configuration." +
			"KeyManagerConfiguration";

	private static final String _KEY_MANAGER_CUSTOM_PROFILE_CONFIGURATION_PID =
		"com.liferay.portal.security.key.internal.profile.configuration." +
			"KeyManagerCustomProfileConfiguration";

	@DeleteAfterTestRun
	private final List<Company> _companies = new ArrayList<>();

	private ConfigurationTemporarySwapper
		_keyManagerConfigurationTemporarySwapper;
	private ConfigurationTemporarySwapper
		_keyManagerCustomProfileConfigurationTemporarySwapper;
	private ServiceRegistration<CryptoProvider> _serviceRegistration;
	private TestCompanyCryptoProvider _testCompanyCryptoProvider;

}
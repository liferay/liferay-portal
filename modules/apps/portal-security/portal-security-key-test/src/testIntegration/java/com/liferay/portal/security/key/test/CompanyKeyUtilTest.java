/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.encryptor.CompanyKeyUtil;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.key.spi.crypto.CryptoProvider;
import com.liferay.portal.security.key.test.util.TestCompanyCryptoProvider;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.security.Key;

import java.util.Dictionary;

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
public class CompanyKeyUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_testCompanyCryptoProvider = new TestCompanyCryptoProvider();

		Bundle bundle = FrameworkUtil.getBundle(CompanyKeyUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			CryptoProvider.class, _testCompanyCryptoProvider,
			HashMapDictionaryBuilder.<String, Object>put(
				"crypto.provider.id", TestCompanyCryptoProvider.PROVIDER_ID
			).build());

		_keyManagerCustomProfileConfigurationTemporarySwapper =
			new ConfigurationTemporarySwapper(
				"com.liferay.portal.security.key.internal.profile." +
					"configuration.KeyManagerCustomProfileConfiguration",
				HashMapDictionaryBuilder.<String, Object>put(
					"companyKEKProviderId",
					TestCompanyCryptoProvider.PROVIDER_ID
				).build());

		_keyManagerConfigurationTemporarySwapper =
			new ConfigurationTemporarySwapper(
				_KEY_MANAGER_CONFIGURATION_PID,
				_getKeyManagerConfigurationProperties(
					TestCompanyCryptoProvider.KEY_IDENTIFIER));
	}

	@After
	public void tearDown() throws Exception {
		_keyManagerConfigurationTemporarySwapper.close();

		_keyManagerCustomProfileConfigurationTemporarySwapper.close();

		_serviceRegistration.unregister();
	}

	@Test
	public void testUnwrapKey() throws Exception {
		long companyId = RandomTestUtil.randomLong();
		Key key = EncryptorUtil.generateKey();

		String wrappedKey = CompanyKeyUtil.wrapKey(companyId, key);

		_saveKeyManagerConfiguration(TestCompanyCryptoProvider.KEY_IDENTIFIER);

		int decryptCount = _testCompanyCryptoProvider.getDecryptCount();

		Key unwrappedKey = CompanyKeyUtil.unwrapKey(companyId, wrappedKey);

		Assert.assertEquals(
			decryptCount + 1, _testCompanyCryptoProvider.getDecryptCount());
		Assert.assertEquals(key, unwrappedKey);

		unwrappedKey = CompanyKeyUtil.unwrapKey(companyId, wrappedKey);

		Assert.assertEquals(
			decryptCount + 1, _testCompanyCryptoProvider.getDecryptCount());
		Assert.assertEquals(key, unwrappedKey);

		key = EncryptorUtil.generateKey();

		unwrappedKey = CompanyKeyUtil.unwrapKey(
			companyId, EncryptorUtil.serializeKey(key));

		Assert.assertEquals(
			decryptCount + 1, _testCompanyCryptoProvider.getDecryptCount());
		Assert.assertEquals(key, unwrappedKey);
	}

	@Test
	public void testWrapKey() throws Exception {
		_company = CompanyTestUtil.addCompany();

		Assert.assertTrue(CompanyKeyUtil.isWrappedKey(_company.getKey()));

		Key key = _company.getKeyObj();

		Assert.assertNotNull(key);

		String plaintext = RandomTestUtil.randomString();

		Assert.assertEquals(
			plaintext,
			EncryptorUtil.decrypt(key, EncryptorUtil.encrypt(key, plaintext)));

		EntityCacheUtil.clearCache();

		Company persistedCompany = CompanyLocalServiceUtil.getCompany(
			_company.getCompanyId());

		Assert.assertEquals(key, persistedCompany.getKeyObj());
		Assert.assertTrue(
			CompanyKeyUtil.isWrappedKey(persistedCompany.getKey()));

		_saveKeyManagerConfiguration(StringPool.BLANK);

		_legacyCompany = CompanyTestUtil.addCompany();

		String serializedKey = _legacyCompany.getKey();

		Assert.assertFalse(CompanyKeyUtil.isWrappedKey(serializedKey));
		Assert.assertEquals(
			EncryptorUtil.deserializeKey(serializedKey),
			_legacyCompany.getKeyObj());
	}

	private Dictionary<String, Object> _getKeyManagerConfigurationProperties(
		String companyKEKIdentifier) {

		return HashMapDictionaryBuilder.<String, Object>put(
			"activeProfileId", "custom"
		).put(
			"companyKEKIdentifier", companyKEKIdentifier
		).build();
	}

	private void _saveKeyManagerConfiguration(String companyKEKIdentifier)
		throws Exception {

		ConfigurationTestUtil.saveConfiguration(
			_KEY_MANAGER_CONFIGURATION_PID,
			_getKeyManagerConfigurationProperties(companyKEKIdentifier));
	}

	private static final String _KEY_MANAGER_CONFIGURATION_PID =
		"com.liferay.portal.security.key.internal.profile.configuration." +
			"KeyManagerConfiguration";

	@DeleteAfterTestRun
	private Company _company;

	private ConfigurationTemporarySwapper
		_keyManagerConfigurationTemporarySwapper;
	private ConfigurationTemporarySwapper
		_keyManagerCustomProfileConfigurationTemporarySwapper;

	@DeleteAfterTestRun
	private Company _legacyCompany;

	private ServiceRegistration<CryptoProvider> _serviceRegistration;
	private TestCompanyCryptoProvider _testCompanyCryptoProvider;

}
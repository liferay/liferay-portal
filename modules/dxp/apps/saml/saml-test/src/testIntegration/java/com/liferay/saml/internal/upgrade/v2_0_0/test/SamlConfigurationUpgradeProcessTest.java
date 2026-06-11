/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.upgrade.v2_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.saml.runtime.certificate.CertificateEntityId;
import com.liferay.saml.runtime.certificate.CertificateTool;
import com.liferay.saml.runtime.configuration.SamlConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Manuele Castro
 * @author Rafael Praxedes
 */
@RunWith(Arquillian.class)
public class SamlConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		_deleteDLKeyStores();
		_deleteFileSystemKeyStores();

		if (_configuration != null) {
			ConfigurationTestUtil.deleteConfiguration(_configuration);
		}

		if (_pid != null) {
			ConfigurationTestUtil.deleteConfiguration(_pid);
		}
	}

	@Test
	public void testUpgrade() throws Exception {
		String entityId = RandomTestUtil.randomString();

		String encryptionAlias = entityId + "-encryption";

		String encryptionCredentialPassword = RandomTestUtil.randomString();

		String credentialPassword = RandomTestUtil.randomString();

		String keyStorePassword = RandomTestUtil.randomString();

		byte[] bytes = _createJKSKeyStoreBytes(
			HashMapBuilder.put(
				encryptionAlias, encryptionCredentialPassword
			).put(
				entityId, credentialPassword
			).build(),
			keyStorePassword);

		long companyId = TestPropsValues.getCompanyId();

		_store.addFile(
			companyId, CompanyConstants.SYSTEM, _JKS_DL_KEYSTORE_PATH,
			Store.VERSION_DEFAULT, new ByteArrayInputStream(bytes));

		String liferayHome = PropsUtil.get(PropsKeys.LIFERAY_HOME);

		File jksFile = new File(liferayHome + "/data/keystore.jks");

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				jksFile)) {

			fileOutputStream.write(bytes);
		}

		_pid = ConfigurationTestUtil.createFactoryConfiguration(
			SamlProviderConfiguration.class.getName(),
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", RandomTestUtil.randomLong()
			).put(
				"saml.entity.id", entityId
			).put(
				"saml.keystore.credential.password", credentialPassword
			).put(
				"saml.keystore.encryption.credential.password",
				encryptionCredentialPassword
			).build());

		_updateSamlConfiguration(keyStorePassword);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_assertSamlConfiguration(
			keyStorePassword, "${liferay.home}/data/keystore.p12", "PKCS12");

		Assert.assertTrue(
			_store.hasFile(
				companyId, CompanyConstants.SYSTEM, _PKCS12_DL_KEYSTORE_PATH,
				Store.VERSION_DEFAULT));

		KeyStore keyStore = KeyStore.getInstance("PKCS12");

		try (InputStream inputStream = _store.getFileAsStream(
				companyId, CompanyConstants.SYSTEM, _PKCS12_DL_KEYSTORE_PATH,
				Store.VERSION_DEFAULT)) {

			keyStore.load(inputStream, keyStorePassword.toCharArray());

			Assert.assertTrue(keyStore.containsAlias(encryptionAlias));
			Assert.assertTrue(keyStore.containsAlias(entityId));
		}

		File pkcs12File = new File(liferayHome + "/data/keystore.p12");

		Assert.assertTrue(pkcs12File.exists());

		try (FileInputStream fileInputStream = new FileInputStream(
				pkcs12File)) {

			keyStore.load(fileInputStream, keyStorePassword.toCharArray());

			Assert.assertTrue(keyStore.containsAlias(encryptionAlias));
			Assert.assertTrue(keyStore.containsAlias(entityId));
		}
	}

	private void _assertSamlConfiguration(
			String keyStorePassword, String keyStorePath, String keyStoreType)
		throws Exception {

		_configuration = _configurationAdmin.getConfiguration(
			SamlConfiguration.class.getName(), StringPool.QUESTION);

		Dictionary<String, Object> properties = _configuration.getProperties();

		if (Validator.isNotNull(keyStorePassword)) {
			Assert.assertEquals(
				keyStorePassword, properties.get("saml.keystore.password"));
		}

		if (Validator.isNotNull(keyStorePath)) {
			Assert.assertEquals(
				keyStorePath, properties.get("saml.keystore.path"));
		}

		if (Validator.isNotNull(keyStoreType)) {
			Assert.assertEquals(
				keyStoreType, properties.get("saml.keystore.type"));
		}
	}

	private byte[] _createJKSKeyStoreBytes(
			Map<String, String> keyPasswords, String keyStorePassword)
		throws Exception {

		KeyStore keyStore = KeyStore.getInstance("JKS");

		keyStore.load(null, null);

		for (Map.Entry<String, String> entry : keyPasswords.entrySet()) {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
				"RSA");

			keyPairGenerator.initialize(2048);

			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			CertificateEntityId certificateEntityId = new CertificateEntityId(
				RandomTestUtil.randomString(), null, null, null, null, null);

			Calendar startDate = Calendar.getInstance();

			Calendar endDate = (Calendar)startDate.clone();

			endDate.add(Calendar.DAY_OF_YEAR, 365);

			X509Certificate x509Certificate =
				_certificateTool.generateCertificate(
					keyPair, certificateEntityId, certificateEntityId,
					startDate.getTime(), endDate.getTime(), "SHA256withRSA");

			String keyPassword = entry.getValue();

			keyStore.setKeyEntry(
				entry.getKey(), keyPair.getPrivate(), keyPassword.toCharArray(),
				new X509Certificate[] {x509Certificate});
		}

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		keyStore.store(byteArrayOutputStream, keyStorePassword.toCharArray());

		return byteArrayOutputStream.toByteArray();
	}

	private void _deleteDLKeyStores() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		if (_store.hasFile(
				companyId, CompanyConstants.SYSTEM, _JKS_DL_KEYSTORE_PATH,
				Store.VERSION_DEFAULT)) {

			_store.deleteDirectory(
				companyId, CompanyConstants.SYSTEM, _JKS_DL_KEYSTORE_PATH);
		}

		if (_store.hasFile(
				companyId, CompanyConstants.SYSTEM, _PKCS12_DL_KEYSTORE_PATH,
				Store.VERSION_DEFAULT)) {

			_store.deleteDirectory(
				companyId, CompanyConstants.SYSTEM, _PKCS12_DL_KEYSTORE_PATH);
		}
	}

	private void _deleteFileSystemKeyStores() {
		String liferayHome = PropsUtil.get(PropsKeys.LIFERAY_HOME);

		File jksFile = new File(liferayHome + "/data/keystore.jks");

		if (jksFile.exists()) {
			jksFile.delete();
		}

		File pkcs12File = new File(liferayHome + "/data/keystore.p12");

		if (pkcs12File.exists()) {
			pkcs12File.delete();
		}
	}

	private void _updateSamlConfiguration(String keyStorePassword)
		throws Exception {

		_configuration = _configurationAdmin.getConfiguration(
			SamlConfiguration.class.getName(), StringPool.QUESTION);

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("saml.keystore.password", keyStorePassword);

		String keyStorePath = "${liferay.home}/data/keystore.jks";

		properties.put("saml.keystore.path", keyStorePath);

		String keyStoreType = "jks";

		properties.put("saml.keystore.type", keyStoreType);

		_configuration.update(properties);

		_assertSamlConfiguration(keyStorePassword, keyStorePath, keyStoreType);
	}

	private static final String _CLASS_NAME =
		"com.liferay.saml.internal.upgrade.v2_0_0." +
			"SamlConfigurationUpgradeProcess";

	private static final String _JKS_DL_KEYSTORE_PATH = "saml/keystore.jks";

	private static final String _PKCS12_DL_KEYSTORE_PATH = "saml/keystore.p12";

	@Inject
	private CertificateTool _certificateTool;

	private Configuration _configuration;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private String _pid;

	@Inject(
		filter = "(&(objectClass=com.liferay.document.library.kernel.store.Store)(default=true))"
	)
	private Store _store;

	@Inject(
		filter = "(&(component.name=com.liferay.saml.internal.upgrade.registry.SamlImplUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}
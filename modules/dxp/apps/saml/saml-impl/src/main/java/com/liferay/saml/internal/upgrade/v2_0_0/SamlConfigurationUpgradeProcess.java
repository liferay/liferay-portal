/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.upgrade.v2_0_0;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.runtime.configuration.SamlConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.security.KeyStore;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Manuele Castro
 * @author Rafael Praxedes
 */
public class SamlConfigurationUpgradeProcess extends UpgradeProcess {

	public SamlConfigurationUpgradeProcess(
		CompanyLocalService companyLocalService,
		ConfigurationAdmin configurationAdmin,
		ConfigurationProvider configurationProvider, Store store) {

		_companyLocalService = companyLocalService;
		_configurationAdmin = configurationAdmin;
		_configurationProvider = configurationProvider;
		_store = store;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeConfiguration();
		_upgradeDLKeyStores();
		_upgradeFileSystemKeyStore();
	}

	private KeyStore _convertJKSToPKCS12(
			InputStream inputStream, char[] keyStorePassword)
		throws Exception {

		KeyStore jksKeyStore = KeyStore.getInstance("JKS");

		jksKeyStore.load(inputStream, keyStorePassword);

		KeyStore pkcs12KeyStore = KeyStore.getInstance("PKCS12");

		pkcs12KeyStore.load(null, null);

		Enumeration<String> aliasesEnumeration = jksKeyStore.aliases();

		while (aliasesEnumeration.hasMoreElements()) {
			String alias = aliasesEnumeration.nextElement();

			if (jksKeyStore.isCertificateEntry(alias)) {
				pkcs12KeyStore.setCertificateEntry(
					alias, jksKeyStore.getCertificate(alias));
			}
			else if (jksKeyStore.isKeyEntry(alias)) {
				char[] keyEntryPassword = null;

				try {
					keyEntryPassword = _getKeyEntryPassword(alias);

					KeyStore.Entry entry = jksKeyStore.getEntry(
						alias,
						new KeyStore.PasswordProtection(keyEntryPassword));

					pkcs12KeyStore.setEntry(
						alias, entry,
						new KeyStore.PasswordProtection(keyEntryPassword));
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Skipping inactive key: " + alias, exception);
					}
				}
				finally {
					if (ArrayUtil.isNotEmpty(keyEntryPassword)) {
						Arrays.fill(keyEntryPassword, '\0');
					}
				}
			}
		}

		return pkcs12KeyStore;
	}

	private char[] _getKeyEntryPassword(String entityId) throws Exception {
		String passwordPropertyKey = "saml.keystore.credential.password";

		if (entityId.endsWith("-encryption")) {
			entityId = entityId.substring(
				0, entityId.lastIndexOf("-encryption"));

			passwordPropertyKey =
				"saml.keystore.encryption.credential.password";
		}

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(&(companyId=*)(service.pid=",
				SamlProviderConfiguration.class.getName(), "*))"));

		if (configurations == null) {
			throw new IllegalStateException(
				"There is no SAML configuration associated with key: " +
					entityId);
		}

		String password = null;

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			if ((properties != null) &&
				StringUtil.equalsIgnoreCase(
					entityId,
					GetterUtil.getString(properties.get("saml.entity.id")))) {

				password = GetterUtil.getString(
					properties.get(passwordPropertyKey));

				break;
			}
		}

		if (Validator.isNull(password)) {
			throw new IllegalStateException(
				"No password match was found for key: " + entityId);
		}

		return password.toCharArray();
	}

	private char[] _getKeyStorePassword() throws Exception {
		SamlConfiguration samlConfiguration =
			_configurationProvider.getSystemConfiguration(
				SamlConfiguration.class);

		String keyStorePassword = samlConfiguration.keyStorePassword();

		if (Validator.isNull(keyStorePassword)) {
			return new char[0];
		}

		return keyStorePassword.toCharArray();
	}

	private String _getKeyStorePath(boolean jks) throws Exception {
		SamlConfiguration samlConfiguration =
			_configurationProvider.getSystemConfiguration(
				SamlConfiguration.class);

		String keyStorePath = samlConfiguration.keyStorePath();

		if (Validator.isNull(keyStorePath) ||
			(!keyStorePath.endsWith(".jks") &&
			 !keyStorePath.endsWith(".p12"))) {

			return null;
		}

		if (!jks && keyStorePath.endsWith(".jks")) {
			keyStorePath = StringUtil.replaceLast(keyStorePath, ".jks", ".p12");
		}
		else if (jks && keyStorePath.endsWith(".p12")) {
			keyStorePath = StringUtil.replaceLast(keyStorePath, ".p12", ".jks");
		}

		return StringUtil.replace(
			keyStorePath, "${liferay.home}",
			PropsUtil.get(PropsKeys.LIFERAY_HOME));
	}

	private void _saveDLKeyStore(
			long companyId, KeyStore keyStore, char[] password)
		throws Exception {

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		keyStore.store(byteArrayOutputStream, password);

		if (_store.hasFile(
				companyId, CompanyConstants.SYSTEM, _PKCS12_DL_KEYSTORE_PATH,
				Store.VERSION_DEFAULT)) {

			_store.deleteDirectory(
				companyId, CompanyConstants.SYSTEM, _PKCS12_DL_KEYSTORE_PATH);
		}

		_store.addFile(
			companyId, CompanyConstants.SYSTEM, _PKCS12_DL_KEYSTORE_PATH,
			Store.VERSION_DEFAULT,
			new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
	}

	private void _upgradeConfiguration() throws Exception {
		Configuration configuration = _configurationAdmin.getConfiguration(
			SamlConfiguration.class.getName(), StringPool.QUESTION);

		Dictionary<String, Object> properties = configuration.getProperties();

		if (properties == null) {
			return;
		}

		String keyStoreType = GetterUtil.getString(
			properties.get("saml.keystore.type"));

		if (Validator.isNull(keyStoreType) ||
			StringUtil.equalsIgnoreCase(keyStoreType, "jks")) {

			properties.put("saml.keystore.type", "PKCS12");

			String keyStorePath = GetterUtil.getString(
				properties.get("saml.keystore.path"));

			if (keyStorePath.endsWith(".jks")) {
				properties.put(
					"saml.keystore.path",
					StringUtil.replaceLast(keyStorePath, ".jks", ".p12"));
			}

			configuration.update(properties);
		}
	}

	private void _upgradeDLKeyStores() {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				char[] keyStorePassword = null;

				try {
					boolean hasJKSKeyStore = _store.hasFile(
						companyId, CompanyConstants.SYSTEM,
						_JKS_DL_KEYSTORE_PATH, Store.VERSION_DEFAULT);

					boolean hasPKCS12KeyStore = _store.hasFile(
						companyId, CompanyConstants.SYSTEM,
						_PKCS12_DL_KEYSTORE_PATH, Store.VERSION_DEFAULT);

					if (!hasJKSKeyStore || hasPKCS12KeyStore) {
						return;
					}

					keyStorePassword = _getKeyStorePassword();

					try (InputStream inputStream = _store.getFileAsStream(
							companyId, CompanyConstants.SYSTEM,
							_JKS_DL_KEYSTORE_PATH, Store.VERSION_DEFAULT)) {

						_saveDLKeyStore(
							companyId,
							_convertJKSToPKCS12(inputStream, keyStorePassword),
							keyStorePassword);
					}
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to migrate DL SAML keystore for company " +
								companyId,
							exception);
					}
				}
				finally {
					if (keyStorePassword != null) {
						Arrays.fill(keyStorePassword, '\0');
					}
				}
			});
	}

	private void _upgradeFileSystemKeyStore() throws Exception {
		String jksFileSystemKeyStorePath = _getKeyStorePath(true);
		String pkcs12FileSystemKeyStorePath = _getKeyStorePath(false);

		if ((jksFileSystemKeyStorePath == null) ||
			(pkcs12FileSystemKeyStorePath == null)) {

			return;
		}

		File jksFile = new File(jksFileSystemKeyStorePath);
		File pkcs12File = new File(pkcs12FileSystemKeyStorePath);

		char[] keyStorePassword = null;

		try {
			if (!jksFile.exists() || pkcs12File.exists()) {
				return;
			}

			keyStorePassword = _getKeyStorePassword();

			try (FileInputStream fileInputStream = new FileInputStream(
					jksFile)) {

				File parentDir = pkcs12File.getParentFile();

				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}

				KeyStore pkcs12KeyStore = _convertJKSToPKCS12(
					fileInputStream, keyStorePassword);

				try (FileOutputStream fileOutputStream = new FileOutputStream(
						pkcs12File)) {

					pkcs12KeyStore.store(fileOutputStream, keyStorePassword);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to migrate filesystem SAML keystore", exception);
			}
		}
		finally {
			if (keyStorePassword != null) {
				Arrays.fill(keyStorePassword, '\0');
			}
		}
	}

	private static final String _JKS_DL_KEYSTORE_PATH = "saml/keystore.jks";

	private static final String _PKCS12_DL_KEYSTORE_PATH = "saml/keystore.p12";

	private static final Log _log = LogFactoryUtil.getLog(
		SamlConfigurationUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationProvider _configurationProvider;
	private final Store _store;

}
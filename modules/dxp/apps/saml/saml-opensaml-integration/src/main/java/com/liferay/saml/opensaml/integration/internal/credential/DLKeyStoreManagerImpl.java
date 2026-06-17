/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.credential;

import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.saml.runtime.credential.KeyStoreManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.security.KeyStore;
import java.security.KeyStoreException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(
	configurationPid = "com.liferay.saml.runtime.configuration.SamlConfiguration",
	service = KeyStoreManager.class
)
public class DLKeyStoreManagerImpl extends BaseKeyStoreManagerImpl {

	@Override
	public KeyStore getKeyStore() throws KeyStoreException {
		KeyStore keyStore = KeyStore.getInstance(getSamlKeyStoreType());

		try (InputStream inputStream = _store.getFileAsStream(
				getCompanyId(), CompanyConstants.SYSTEM, _SAML_KEYSTORE_PATH,
				Store.VERSION_DEFAULT)) {

			String samlKeyStorePassword = getSamlKeyStorePassword();

			keyStore.load(inputStream, samlKeyStorePassword.toCharArray());
		}
		catch (NoSuchFileException noSuchFileException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(noSuchFileException);
			}

			try {
				keyStore.load(null, null);
			}
			catch (Exception exception) {
				String message = "Unable to load blank keystore";

				if (_log.isDebugEnabled()) {
					_log.debug(message, exception);
				}
				else {
					_log.error(message);
				}
			}
		}
		catch (Exception exception) {
			throw new KeyStoreException(
				StringBundler.concat(
					"Unable to load keystore ", getCompanyId(), "/",
					_SAML_KEYSTORE_PATH, ": ", exception.getMessage()),
				exception);
		}

		return keyStore;
	}

	@Override
	public void saveKeyStore(KeyStore keyStore) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		String samlKeyStorePassword = getSamlKeyStorePassword();

		keyStore.store(
			byteArrayOutputStream, samlKeyStorePassword.toCharArray());

		if (_store.hasFile(
				getCompanyId(), CompanyConstants.SYSTEM, _SAML_KEYSTORE_PATH,
				Store.VERSION_DEFAULT)) {

			_store.deleteDirectory(
				getCompanyId(), CompanyConstants.SYSTEM, _SAML_KEYSTORE_PATH);
		}

		_store.addFile(
			getCompanyId(), CompanyConstants.SYSTEM, _SAML_KEYSTORE_PATH,
			Store.VERSION_DEFAULT,
			new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws Exception {
		updateConfigurations(properties);
	}

	private static final String _SAML_KEYSTORE_PATH = "saml/keystore.p12";

	private static final Log _log = LogFactoryUtil.getLog(
		DLKeyStoreManagerImpl.class);

	@Reference(target = "(default=true)")
	private Store _store;

}
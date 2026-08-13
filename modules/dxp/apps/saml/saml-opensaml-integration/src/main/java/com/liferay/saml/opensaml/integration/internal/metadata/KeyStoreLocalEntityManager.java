/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.metadata;

import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.opensaml.integration.internal.util.KeyStoreUtil;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.runtime.SamlException;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.credential.KeyStoreManager;
import com.liferay.saml.runtime.exception.CredentialAuthException;
import com.liferay.saml.runtime.exception.CredentialException;
import com.liferay.saml.runtime.exception.EntityIdException;
import com.liferay.saml.runtime.metadata.LocalEntityManager;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import java.util.Arrays;
import java.util.List;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.apache.xml.security.utils.Base64;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.x509.X509Credential;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(
	configurationPid = "com.liferay.saml.runtime.configuration.SamlKeyStoreManagerConfiguration",
	service = LocalEntityManager.class
)
public class KeyStoreLocalEntityManager implements LocalEntityManager {

	@Override
	public void authenticateLocalEntityCertificate(
			String certificateKeyPassword, CertificateUsage certificateUsage,
			String entityId)
		throws CredentialAuthException, CredentialException {

		KeyStore.Entry entry = null;

		if (certificateUsage == CertificateUsage.ENCRYPTION) {
			entry = KeyStoreUtil.getKeyStoreEntry(
				KeyStoreUtil.getAlias(entityId, UsageType.ENCRYPTION),
				certificateKeyPassword, _keyStoreManager);
		}
		else {
			entry = KeyStoreUtil.getKeyStoreEntry(
				KeyStoreUtil.getAlias(entityId, UsageType.SIGNING),
				certificateKeyPassword, _keyStoreManager);
		}

		if (entry == null) {
			throw new CredentialException("Certificate not found");
		}
	}

	@Override
	public void deleteLocalEntityCertificate(CertificateUsage certificateUsage)
		throws KeyStoreException {

		KeyStore keyStore = _keyStoreManager.getKeyStore();

		keyStore.deleteEntry(
			KeyStoreUtil.getAlias(
				getLocalEntityId(), _getUsageType(certificateUsage)));

		try {
			_keyStoreManager.saveKeyStore(keyStore);
		}
		catch (Exception exception) {
			throw new KeyStoreException(exception);
		}
	}

	@Override
	public String getEncodedLocalEntityCertificate(
			CertificateUsage certificateUsage)
		throws SamlException {

		try {
			X509Certificate x509Certificate = getLocalEntityCertificate(
				certificateUsage);

			if (x509Certificate == null) {
				return null;
			}

			return Base64.encode(x509Certificate.getEncoded(), 76);
		}
		catch (CertificateEncodingException certificateEncodingException) {
			throw new SamlException(certificateEncodingException);
		}
	}

	@Override
	public X509Certificate getLocalEntityCertificate(
			CertificateUsage certificateUsage)
		throws SamlException {

		UsageType usageType = _getUsageType(certificateUsage);

		if (usageType == null) {
			return null;
		}

		String entityId = getLocalEntityId();

		if (Validator.isBlank(entityId)) {
			throw new SamlException(
				new EntityIdException("An Entity ID must be configured"));
		}

		UsageCriterion usageCriterion = new UsageCriterion(usageType);

		try {
			X509Credential x509Credential =
				(X509Credential)_credentialResolver.resolveSingle(
					new CriteriaSet(
						new EntityIdCriterion(entityId), usageCriterion));

			if (x509Credential == null) {
				return null;
			}

			return x509Credential.getEntityCertificate();
		}
		catch (ResolverException resolverException) {
			throw new SamlException(resolverException);
		}
	}

	@Override
	public String getLocalEntityId() {
		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		return samlProviderConfiguration.entityId();
	}

	@Override
	public boolean hasDefaultIdpRole() {
		List<SamlSpIdpConnection> samlSpIdpConnections =
			_samlSpIdpConnectionLocalService.getSamlSpIdpConnections(
				CompanyThreadLocal.getCompanyId());

		return !samlSpIdpConnections.isEmpty();
	}

	@Override
	public void storeLocalEntityCertificate(
			PrivateKey privateKey, String certificateKeyPassword,
			X509Certificate x509Certificate, CertificateUsage certificateUsage)
		throws Exception {

		KeyStore keyStore = _keyStoreManager.getKeyStore();

		char[] certificateKeyPasswordChars =
			certificateKeyPassword.toCharArray();

		KeyStore.PasswordProtection keyStorePasswordProtection =
			new KeyStore.PasswordProtection(certificateKeyPasswordChars);

		try {
			keyStore.setEntry(
				KeyStoreUtil.getAlias(
					getLocalEntityId(), _getUsageType(certificateUsage)),
				new KeyStore.PrivateKeyEntry(
					privateKey, new Certificate[] {x509Certificate}),
				keyStorePasswordProtection);
		}
		finally {
			Arrays.fill(certificateKeyPasswordChars, '\0');

			KeyStoreUtil.destroyPasswordProtection(keyStorePasswordProtection);
		}

		_keyStoreManager.saveKeyStore(keyStore);
	}

	private UsageType _getUsageType(CertificateUsage certificateUsage) {
		UsageType usageType = null;

		if (certificateUsage == CertificateUsage.ENCRYPTION) {
			usageType = UsageType.ENCRYPTION;
		}
		else if (certificateUsage == CertificateUsage.SIGNING) {
			usageType = UsageType.SIGNING;
		}

		return usageType;
	}

	@Reference
	private CredentialResolver _credentialResolver;

	@Reference(name = "KeyStoreManager", target = "(default=true)")
	private KeyStoreManager _keyStoreManager;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

	@Reference
	private SamlSpIdpConnectionLocalService _samlSpIdpConnectionLocalService;

}
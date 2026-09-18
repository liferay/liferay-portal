/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.credential;

import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.saml.opensaml.integration.internal.util.KeyStoreUtil;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.credential.KeyStoreManager;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import java.util.Arrays;
import java.util.Collections;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.credential.impl.AbstractCredentialResolver;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.x509.BasicX509Credential;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.saml.runtime.configuration.SamlKeyStoreManagerConfiguration",
	service = CredentialResolver.class
)
public class KeyStoreCredentialResolver extends AbstractCredentialResolver {

	@Override
	public Iterable<Credential> resolve(CriteriaSet criteriaSet)
		throws SecurityException {

		_checkCriteriaRequirements(criteriaSet);

		EntityIdCriterion entityIDCriterion = criteriaSet.get(
			EntityIdCriterion.class);

		String entityId = entityIDCriterion.getEntityId();

		SamlProviderConfiguration samlProviderConfiguration =
			_samlProviderConfigurationHelper.getSamlProviderConfiguration();

		UsageCriterion usageCriterion = criteriaSet.get(UsageCriterion.class);

		UsageType usageType = UsageType.UNSPECIFIED;

		if (usageCriterion != null) {
			usageType = usageCriterion.getUsage();
		}

		String keyStoreCredentialPassword = null;

		if (entityId.equals(samlProviderConfiguration.entityId())) {
			if (usageType == UsageType.ENCRYPTION) {
				keyStoreCredentialPassword =
					samlProviderConfiguration.
						keyStoreEncryptionCredentialPassword();
			}
			else {
				keyStoreCredentialPassword = SecretResolverUtil.resolve(
					CompanyThreadLocal.getCompanyId(),
					samlProviderConfiguration.keyStoreCredentialPassword());
			}
		}

		KeyStore.Entry entry = KeyStoreUtil.getKeyStoreEntry(
			KeyStoreUtil.getAlias(entityId, usageType),
			keyStoreCredentialPassword, _keyStoreManager);

		if (entry == null) {
			return Collections.emptySet();
		}

		Credential credential = _buildCredential(entry, entityId, usageType);

		return Collections.singleton(credential);
	}

	private Credential _buildCredential(
		KeyStore.Entry entry, String entityId, UsageType usage) {

		if (entry instanceof KeyStore.PrivateKeyEntry) {
			return _processPrivateKeyEntry(
				(KeyStore.PrivateKeyEntry)entry, entityId, usage);
		}
		else if (entry instanceof KeyStore.SecretKeyEntry) {
			return _processSecretKeyEntry(
				(KeyStore.SecretKeyEntry)entry, entityId, usage);
		}
		else if (entry instanceof KeyStore.TrustedCertificateEntry) {
			return _processTrustedCertificateEntry(
				(KeyStore.TrustedCertificateEntry)entry, entityId, usage);
		}

		return null;
	}

	private void _checkCriteriaRequirements(CriteriaSet criteriaSet) {
		EntityIdCriterion entityIdCriterion = criteriaSet.get(
			EntityIdCriterion.class);

		if (entityIdCriterion == null) {
			throw new IllegalArgumentException(
				"No entity ID criterion was available in criteria set");
		}
	}

	private Credential _processPrivateKeyEntry(
		KeyStore.PrivateKeyEntry privateKeyEntry, String entityId,
		UsageType usageType) {

		BasicX509Credential basicX509Credential = new BasicX509Credential(
			(X509Certificate)privateKeyEntry.getCertificate());

		basicX509Credential.setEntityCertificateChain(
			Arrays.asList(
				(X509Certificate[])privateKeyEntry.getCertificateChain()));
		basicX509Credential.setEntityId(entityId);
		basicX509Credential.setPrivateKey(privateKeyEntry.getPrivateKey());
		basicX509Credential.setUsageType(usageType);

		return basicX509Credential;
	}

	private Credential _processSecretKeyEntry(
		KeyStore.SecretKeyEntry secretKeyEntry, String entityId,
		UsageType usageType) {

		BasicCredential basicCredential = new BasicCredential(
			secretKeyEntry.getSecretKey());

		basicCredential.setEntityId(entityId);
		basicCredential.setUsageType(usageType);

		return basicCredential;
	}

	private Credential _processTrustedCertificateEntry(
		KeyStore.TrustedCertificateEntry trustedCertificateEntry,
		String entityId, UsageType usageType) {

		X509Certificate x509Certificate =
			(X509Certificate)trustedCertificateEntry.getTrustedCertificate();

		BasicX509Credential basicX509Credential = new BasicX509Credential(
			x509Certificate);

		basicX509Credential.setEntityCertificateChain(
			Arrays.asList(x509Certificate));

		basicX509Credential.setEntityId(entityId);
		basicX509Credential.setUsageType(usageType);

		return basicX509Credential;
	}

	@Reference(name = "KeyStoreManager", target = "(default=true)")
	private KeyStoreManager _keyStoreManager;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}
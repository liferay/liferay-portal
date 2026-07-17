/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.bootstrap;

import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.saml.opensaml.integration.internal.util.ConfigurationServiceBootstrapUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.impl.BasicDecryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureValidationConfiguration;
import org.opensaml.xmlsec.impl.BasicWhitelistBlacklistConfiguration;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Carlos Sierra Andrés
 * @author Jorge García Jiménez
 */
@Component(service = {})
public class SecurityConfigurationBootstrap {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		BasicDecryptionConfiguration basicDecryptionConfiguration =
			DefaultSecurityConfigurationBootstrap.
				buildDefaultDecryptionConfiguration();
		BasicEncryptionConfiguration basicEncryptionConfiguration =
			DefaultSecurityConfigurationBootstrap.
				buildDefaultEncryptionConfiguration();
		BasicSignatureSigningConfiguration basicSignatureSigningConfiguration =
			DefaultSecurityConfigurationBootstrap.
				buildDefaultSignatureSigningConfiguration();
		BasicSignatureValidationConfiguration
			basicSignatureValidationConfiguration =
				DefaultSecurityConfigurationBootstrap.
					buildDefaultSignatureValidationConfiguration();

		String[] blacklistedAlgorithms = new String[0];

		Object blacklistedAlgorithmsObject = properties.get(
			"blacklisted.algorithms");

		if (blacklistedAlgorithmsObject instanceof String[]) {
			blacklistedAlgorithms = (String[])blacklistedAlgorithmsObject;
		}

		String[] fipsDisallowedAlgorithms = new String[0];

		if (PropsValues.FIPS_ENABLED) {
			fipsDisallowedAlgorithms = _FIPS_DISALLOWED_ALGORITHMS;
		}

		_blacklist(basicDecryptionConfiguration, blacklistedAlgorithms);
		_blacklist(basicEncryptionConfiguration, blacklistedAlgorithms);
		_blacklist(
			basicSignatureSigningConfiguration, blacklistedAlgorithms,
			fipsDisallowedAlgorithms);
		_blacklist(
			basicSignatureValidationConfiguration, blacklistedAlgorithms,
			fipsDisallowedAlgorithms);

		ConfigurationServiceBootstrapUtil.register(
			DecryptionConfiguration.class, basicDecryptionConfiguration);
		ConfigurationServiceBootstrapUtil.register(
			EncryptionConfiguration.class, basicEncryptionConfiguration);
		ConfigurationServiceBootstrapUtil.register(
			SignatureSigningConfiguration.class,
			basicSignatureSigningConfiguration);
		ConfigurationServiceBootstrapUtil.register(
			SignatureValidationConfiguration.class,
			basicSignatureValidationConfiguration);
	}

	private void _blacklist(
		BasicWhitelistBlacklistConfiguration
			basicWhitelistBlacklistConfiguration,
		String[]... stringsArrays) {

		Collection<String> blacklistedAlgorithms = new HashSet<>(
			basicWhitelistBlacklistConfiguration.getBlacklistedAlgorithms());

		for (String[] strings : stringsArrays) {
			Collections.addAll(blacklistedAlgorithms, strings);
		}

		basicWhitelistBlacklistConfiguration.setBlacklistedAlgorithms(
			blacklistedAlgorithms);
	}

	private static final String[] _FIPS_DISALLOWED_ALGORITHMS = {
		SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5,
		SignatureConstants.ALGO_ID_DIGEST_SHA1,
		SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5,
		SignatureConstants.ALGO_ID_MAC_HMAC_SHA1,
		SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1,
		SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1,
		SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5,
		SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1
	};

}
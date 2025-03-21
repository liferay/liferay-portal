/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.metadata;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.saml.runtime.SamlException;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;
import com.liferay.saml.runtime.metadata.LocalEntityManager;

import jakarta.servlet.http.HttpServletRequest;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;

/**
 * @author Mika Koivisto
 */
public class MetadataManagerUtil {

	public static EntityDescriptor getEntityDescriptor(
			HttpServletRequest httpServletRequest,
			SamlProviderConfigurationHelper samlProviderConfigurationHelper,
			CredentialResolver credentialResolver,
			LocalEntityManager localEntityManager)
		throws SamlException {

		Credential encryptionCredential = null;

		try {
			encryptionCredential = _getEncryptionCredential(
				credentialResolver, localEntityManager);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get encryption credential: " +
						exception.getMessage(),
					exception);
			}
		}

		try {
			String portalURL = PortalUtil.getPortalURL(
				httpServletRequest,
				_isSSLRequired(samlProviderConfigurationHelper) ||
				PortalUtil.isSecure(httpServletRequest));
			String localEntityId = localEntityManager.getLocalEntityId();

			if (samlProviderConfigurationHelper.isRoleIdp()) {
				return MetadataGeneratorUtil.buildIdpEntityDescriptor(
					portalURL, localEntityId,
					_isWantAuthnRequestSigned(samlProviderConfigurationHelper),
					_isSignMetadata(samlProviderConfigurationHelper),
					_getSigningCredential(
						credentialResolver, localEntityManager),
					encryptionCredential);
			}
			else if (samlProviderConfigurationHelper.isRoleSp()) {
				return MetadataGeneratorUtil.buildSpEntityDescriptor(
					portalURL, localEntityId,
					_isSignAuthnRequest(samlProviderConfigurationHelper),
					_isSignMetadata(samlProviderConfigurationHelper),
					_isWantAssertionsSigned(samlProviderConfigurationHelper),
					_getSigningCredential(
						credentialResolver, localEntityManager),
					encryptionCredential);
			}

			return null;
		}
		catch (Exception exception) {
			throw new SamlException(exception);
		}
	}

	private static Credential _getEncryptionCredential(
			CredentialResolver credentialResolver,
			LocalEntityManager localEntityManager)
		throws SamlException {

		try {
			String entityId = localEntityManager.getLocalEntityId();

			if (Validator.isNull(entityId)) {
				return null;
			}

			return credentialResolver.resolveSingle(
				new CriteriaSet(
					new EntityIdCriterion(entityId),
					new UsageCriterion(UsageType.ENCRYPTION)));
		}
		catch (ResolverException resolverException) {
			throw new SamlException(resolverException);
		}
	}

	private static SamlProviderConfiguration _getSamlProviderConfiguration(
		SamlProviderConfigurationHelper samlProviderConfigurationHelper) {

		return samlProviderConfigurationHelper.getSamlProviderConfiguration();
	}

	private static Credential _getSigningCredential(
			CredentialResolver credentialResolver,
			LocalEntityManager localEntityManager)
		throws SamlException {

		try {
			String entityId = localEntityManager.getLocalEntityId();

			if (Validator.isNull(entityId)) {
				return null;
			}

			return credentialResolver.resolveSingle(
				new CriteriaSet(
					new EntityIdCriterion(entityId),
					new UsageCriterion(UsageType.SIGNING)));
		}
		catch (ResolverException resolverException) {
			throw new SamlException(resolverException);
		}
	}

	private static boolean _isSignAuthnRequest(
		SamlProviderConfigurationHelper samlProviderConfigurationHelper) {

		return _getSamlProviderConfiguration(
			samlProviderConfigurationHelper
		).signAuthnRequest();
	}

	private static boolean _isSignMetadata(
		SamlProviderConfigurationHelper samlProviderConfigurationHelper) {

		return _getSamlProviderConfiguration(
			samlProviderConfigurationHelper
		).signMetadata();
	}

	private static boolean _isSSLRequired(
		SamlProviderConfigurationHelper samlProviderConfigurationHelper) {

		return _getSamlProviderConfiguration(
			samlProviderConfigurationHelper
		).sslRequired();
	}

	private static boolean _isWantAssertionsSigned(
		SamlProviderConfigurationHelper samlProviderConfigurationHelper) {

		return _getSamlProviderConfiguration(
			samlProviderConfigurationHelper
		).assertionSignatureRequired();
	}

	private static boolean _isWantAuthnRequestSigned(
		SamlProviderConfigurationHelper samlProviderConfigurationHelper) {

		return _getSamlProviderConfiguration(
			samlProviderConfigurationHelper
		).authnRequestSignatureRequired();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MetadataManagerUtil.class);

}
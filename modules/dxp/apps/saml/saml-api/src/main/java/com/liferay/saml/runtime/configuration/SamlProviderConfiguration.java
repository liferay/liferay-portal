/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.runtime.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.saml.constants.SamlProviderConfigurationKeys;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Mika Koivisto
 */
@ExtendedObjectClassDefinition(
	category = "sso", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.saml.runtime.configuration.SamlProviderConfiguration",
	localization = "content/Language", name = "saml-provider-configuration-name"
)
@ProviderType
public interface SamlProviderConfiguration {

	@Meta.AD(deflt = "0", name = "company-id", required = false)
	public long companyId();

	@Meta.AD(
		deflt = "liferay",
		description = "saml-keystore-credential-password-description",
		id = "saml.keystore.credential.password",
		name = "saml-keystore-credential-password", required = false
	)
	public String keyStoreCredentialPassword();

	@Meta.AD(
		deflt = "liferay",
		description = "saml-keystore-encryption-credential-password-description",
		id = "saml.keystore.encryption.credential.password",
		name = "saml-keystore-encryption-credential-password", required = false
	)
	public String keyStoreEncryptionCredentialPassword();

	@Meta.AD(
		description = "saml-sp-assertion-signature-required-description",
		id = "saml.sp.assertion.signature.required",
		name = "saml-sp-assertion-signature-required", required = false
	)
	public boolean assertionSignatureRequired();

	@Meta.AD(
		deflt = "true",
		description = "saml-idp-authn-request-signature-required-description",
		id = "saml.idp.authn.request.signature.required",
		name = "saml-idp-authn-request-signature-required", required = false
	)
	public boolean authnRequestSignatureRequired();

	@Meta.AD(
		deflt = "true",
		description = "saml-idp-authn-request-signing-allows-dynamic-acs-url-description",
		id = "saml.idp.authn.request.signing.allows.dynamic.acs.url",
		name = "saml-idp-authn-request-signing-allows-dynamic-acs-url",
		required = false
	)
	public boolean authnRequestSigningAllowsDynamicACSURL();

	@Meta.AD(
		deflt = "3000", description = "saml-sp-clock-skew-description",
		id = "saml.sp.clock.skew", name = "clock-skew", required = false
	)
	public long clockSkew();

	@Meta.AD(
		deflt = "1800", description = "saml-idp-assertion-lifetime-description",
		id = "saml.idp.assertion.lifetime",
		name = "saml-idp-assertion-lifetime", required = false
	)
	public int defaultAssertionLifetime();

	@Meta.AD(id = "saml.enabled", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(
		description = "saml-entity-id-description", id = "saml.entity.id",
		name = "saml-entity-id", required = false
	)
	public String entityId();

	@Meta.AD(
		description = "saml-sp-ldap-import-enabled-description",
		id = "saml.sp.ldap.import.enabled",
		name = "saml-sp-ldap-import-enabled", required = false
	)
	public boolean ldapImportEnabled();

	@Meta.AD(
		deflt = SamlProviderConfigurationKeys.SAML_ROLE_SP, id = "saml.role",
		name = "saml-role",
		optionLabels = {
			"identity-broker", "identity-provider", "service-provider"
		},
		optionValues = {
			SamlProviderConfigurationKeys.SAML_ROLE_IB,
			SamlProviderConfigurationKeys.SAML_ROLE_IDP,
			SamlProviderConfigurationKeys.SAML_ROLE_SP
		},
		required = false
	)
	public String role();

	@Meta.AD(
		description = "saml-idp-session-maximum-age-description",
		id = "saml.idp.session.maximum.age",
		name = "saml-idp-session-maximum-age", required = false
	)
	public long sessionMaximumAge();

	@Meta.AD(
		description = "saml-idp-session-timeout-description",
		id = "saml.idp.session.timeout", name = "saml-idp-session-timeout",
		required = false
	)
	public long sessionTimeout();

	@Meta.AD(
		deflt = "true", description = "saml-sp-sign-authn-request-description",
		id = "saml.sp.sign.authn.request", name = "saml-sp-sign-authn-request",
		required = false
	)
	public boolean signAuthnRequest();

	@Meta.AD(
		deflt = "true", description = "saml-sign-metadata-description",
		id = "saml.sign.metadata", name = "saml-sign-metadata", required = false
	)
	public boolean signMetadata();

	@Meta.AD(
		description = "saml-ssl-required-description", id = "saml.ssl.required",
		name = "saml-ssl-required", required = false
	)
	public boolean sslRequired();

	/**
	 * If no SAML IdP is matched then show the login portlet
	 */
	@Meta.AD(
		deflt = "true",
		description = "saml-sp-allow-showing-the-login-portlet-description",
		id = "saml.sp.allow.showing.the.login.portlet",
		name = "saml-sp-allow-showing-the-login-portlet", required = false
	)
	public boolean allowShowingTheLoginPortlet();

}
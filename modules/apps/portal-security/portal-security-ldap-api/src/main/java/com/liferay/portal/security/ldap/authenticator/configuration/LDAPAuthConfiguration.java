/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.authenticator.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.security.ldap.configuration.CompanyScopedConfiguration;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(
	category = "ldap", scope = ExtendedObjectClassDefinition.Scope.COMPANY,
	visibilityControllerKey = "ldap-auth"
)
@Meta.OCD(
	id = "com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration",
	localization = "content/Language", name = "ldap-auth-configuration-name"
)
public interface LDAPAuthConfiguration extends CompanyScopedConfiguration {

	@Meta.AD(deflt = "0", name = "company-id", required = false)
	@Override
	public long companyId();

	@Meta.AD(deflt = "false", name = "enabled", required = false)
	public boolean enabled();

	@Meta.AD(deflt = "false", name = "required", required = false)
	public boolean required();

	@Meta.AD(
		deflt = "false", description = "password-policy-enabled-help",
		name = "password-policy-enabled", required = false
	)
	public boolean passwordPolicyEnabled();

	@Meta.AD(
		deflt = "bind", description = "method-help", name = "method",
		optionLabels = {"bind", "password-compare"},
		optionValues = {"bind", "password-compare"}, required = false
	)
	public String method();

	@Meta.AD(
		deflt = "NONE", description = "password-encryption-algorithm-help",
		name = "password-encryption-algorithm",
		optionValues = {
			"BCRYPT", "MD2", "MD5", "NONE", "SHA", "SHA-256", "SHA-384", "SSHA",
			"UFC-CRYPT"
		},
		required = false
	)
	public String passwordEncryptionAlgorithm();

}
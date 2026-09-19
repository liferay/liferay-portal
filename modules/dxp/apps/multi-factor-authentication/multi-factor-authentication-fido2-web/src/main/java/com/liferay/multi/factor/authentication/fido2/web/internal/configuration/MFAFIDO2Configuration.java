/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.web.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Arthur Chan
 * @review
 */
@ExtendedObjectClassDefinition(
	category = "multi-factor-authentication",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY,
	visibilityControllerKey = "multi-factor-authentication"
)
@Meta.OCD(
	id = "com.liferay.multi.factor.authentication.fido2.web.internal.configuration.MFAFIDO2Configuration",
	localization = "content/Language", name = "mfa-fido2-configuration-name"
)
public interface MFAFIDO2Configuration {

	/**
	 * If <code>true</code>, the origin matching rule is relaxed to allow any
	 * port number.
	 */
	@Meta.AD(
		deflt = "false", description = "allow-origin-port-description",
		name = "allow-origin-port", required = false
	)
	public boolean allowOriginPort();

	/**
	 * If <code>true</code>, the origin matching rule is relaxed to allow any
	 * subdomain, of any depth, of the values of RelyingPartyBuilder#origins(
	 * Set) origins.
	 */
	@Meta.AD(
		deflt = "false", description = "allow-origin-subdomain-description",
		name = "allow-origin-subdomain", required = false
	)
	public boolean allowOriginSubdomain();

	/**
	 * Number of allowed credentials(authenticators) per user.
	 */
	@Meta.AD(
		deflt = "1", description = "allowed-credentials-per-user-description",
		name = "allowed-credentials-per-user", required = false
	)
	public int allowedCredentialsPerUser();

	@Meta.AD(
		deflt = "false", description = "mfa-fido2-enabled-description",
		name = "enabled", required = false
	)
	public boolean enabled();

	@Meta.AD(
		deflt = "200", description = "order-description",
		id = "service.ranking", name = "order", required = false
	)
	public int order();

	/**
	 * The allowed origins that returned authenticator responses will be
	 * compared against. The default is the set containing only the string
	 * <code>"https://" + {@link #getIdentity()}.getId()</code>.
	 */
	@Meta.AD(
		deflt = "https://localhost", description = "origins-description",
		name = "origins", required = false
	)
	public String[] origins();

	/**
	 * The RelyingParty ID must be equal to the origin's effective domain,
	 * or a registrable domain suffix of the origin's effective domain.
	 * For example an origin of https://login.example.com:1337
	 * can only have rpID as one of the following:
	 * login.example.com or example.com
	 * This is done in order to match the behavior of pervasively deployed
	 * ambient credentials (e.g., cookies,  [RFC6265])
	 */
	@Meta.AD(
		deflt = "localhost", description = "relying-party-id-description",
		name = "relying-party-id", required = false
	)
	public String relyingPartyId();

	@Meta.AD(
		deflt = "relying Party", description = "relying-party-name-description",
		name = "relying-party-name", required = false
	)
	public String relyingPartyName();

}
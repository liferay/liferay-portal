/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Alvaro Saugar
 */
@ExtendedObjectClassDefinition(
	generateUI = false, scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration",
	localization = "content/Language",
	name = "oauth-client-company-configuration-name"
)
@ProviderType
public interface OAuthClientCompanyConfiguration {

	@Meta.AD(
		description = "oauth-client-as-hosts-allowed-help",
		name = "oauth-client-as-hosts-allowed", required = false
	)
	public String[] authServerHostsAllowed();

	@Meta.AD(
		deflt = "false",
		description = "oauth-client-as-local-network-access-enabled-help",
		name = "oauth-client-as-local-network-access-enabled", required = false
	)
	public boolean authServerLocalNetworkAccessEnabled();

}
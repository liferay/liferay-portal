/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.scope.liferay;

import java.util.Collection;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Lists scope aliases and matching {@link LiferayOAuth2Scope}s based on a
 * portal instance configuration of the OAuth2 framework. Scope aliases can
 * match multiple {@link LiferayOAuth2Scope}s from OAuth2 frameworks in
 * different portal instances.
 *
 * @author Carlos Sierra Andrés
 */
@ProviderType
public interface ScopeLocator {

	public LiferayOAuth2Scope getLiferayOAuth2Scope(
		long companyId, String applicationName, String scope);

	/**
	 * Returns all the application exported scopes.
	 *
	 * @param  companyId the ID of the portal instance containing the scopes
	 * @return the matching scopes
	 * @review
	 */
	public Collection<LiferayOAuth2Scope> getLiferayOAuth2Scopes(
		long companyId);

	/**
	 * Returns the application exported scopes matching the given portal
	 * instance's scopes alias.
	 *
	 * @param  companyId the ID of the portal instance containing the scopes
	 * @param  scopesAlias the alias mapped to scopes
	 * @return the matching scopes
	 */
	public Collection<LiferayOAuth2Scope> getLiferayOAuth2Scopes(
		long companyId, String scopesAlias);

	/**
	 * Returns the application exported scopes matching the given portal
	 * instance's scopes alias, filtered by application name.
	 *
	 * @param  companyId the ID of the portal instance containing the scopes
	 * @param  scopesAlias the alias mapped to scopes
	 * @param  applicationName the application containing the scopes
	 * @return the matching scopes, filtered by application name
	 */
	public Collection<LiferayOAuth2Scope> getLiferayOAuth2Scopes(
		long companyId, String scopesAlias, String applicationName);

	/**
	 * Returns the scope aliases available for the given portal instance.
	 *
	 * @param  companyId the ID of the portal instance containing the scope
	 *         aliases
	 * @return the non-<code>null</code> scope aliases
	 */
	public Collection<String> getScopeAliases(long companyId);

	/**
	 * Returns the scope aliases available for the given portal instance,
	 * filtered by application name.
	 *
	 * @param  companyId the ID of the portal instance containing the scope
	 *         aliases
	 * @param  applicationName the application exporting the scopes
	 * @return the non-<code>null</code> scope aliases, filtered by application
	 *         name
	 */
	public Collection<String> getScopeAliases(
		long companyId, String applicationName);

	public void stopWaitingForScopeAliases(Runnable runnable);

	public Set<String> waitForScopeAliases(
		long companyId, Set<String> scopeAliases, Runnable runnable);

}
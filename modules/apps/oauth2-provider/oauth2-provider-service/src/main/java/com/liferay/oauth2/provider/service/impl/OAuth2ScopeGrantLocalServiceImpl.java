/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service.impl;

import com.liferay.oauth2.provider.exception.DuplicateOAuth2ScopeGrantException;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.model.OAuth2ScopeGrant;
import com.liferay.oauth2.provider.scope.liferay.LiferayOAuth2Scope;
import com.liferay.oauth2.provider.service.base.OAuth2ScopeGrantLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.oauth2.provider.model.OAuth2ScopeGrant",
	service = AopService.class
)
public class OAuth2ScopeGrantLocalServiceImpl
	extends OAuth2ScopeGrantLocalServiceBaseImpl {

	@Override
	public OAuth2ScopeGrant createOAuth2ScopeGrant(
			long companyId, long oAuth2ApplicationScopeAliasesId,
			String applicationName, String bundleSymbolicName, String scope)
		throws DuplicateOAuth2ScopeGrantException {

		throw new UnsupportedOperationException();
	}

	@Override
	public OAuth2ScopeGrant createOAuth2ScopeGrant(
			long companyId, long oAuth2ApplicationScopeAliasesId,
			String applicationName, String bundleSymbolicName, String scope,
			List<String> scopeAliases)
		throws DuplicateOAuth2ScopeGrantException {

		OAuth2ScopeGrant oAuth2ScopeGrant =
			oAuth2ScopeGrantPersistence.fetchByC_O_A_B_S(
				companyId, oAuth2ApplicationScopeAliasesId, applicationName,
				bundleSymbolicName, scope);

		if (oAuth2ScopeGrant != null) {
			throw new DuplicateOAuth2ScopeGrantException();
		}

		long oAuth2ScopeGrantId = counterLocalService.increment(
			OAuth2ScopeGrant.class.getName());

		oAuth2ScopeGrant = oAuth2ScopeGrantPersistence.create(
			oAuth2ScopeGrantId);

		oAuth2ScopeGrant.setCompanyId(companyId);
		oAuth2ScopeGrant.setOAuth2ApplicationScopeAliasesId(
			oAuth2ApplicationScopeAliasesId);
		oAuth2ScopeGrant.setApplicationName(applicationName);
		oAuth2ScopeGrant.setBundleSymbolicName(bundleSymbolicName);
		oAuth2ScopeGrant.setScope(scope);

		oAuth2ScopeGrant.setScopeAliasesList(scopeAliases);

		return oAuth2ScopeGrantPersistence.update(oAuth2ScopeGrant);
	}

	@Override
	public Collection<LiferayOAuth2Scope> getFilteredLiferayOAuth2Scopes(
		long oAuth2ApplicationScopeAliasesId,
		Collection<LiferayOAuth2Scope> liferayOAuth2Scopes) {

		Collection<LiferayOAuth2Scope> filteredLiferayOAuth2Scopes =
			new ArrayList<>(liferayOAuth2Scopes.size());

		List<OAuth2ScopeGrant> oAuth2ScopeGrants =
			oAuth2ScopeGrantPersistence.findByOAuth2ApplicationScopeAliasesId(
				oAuth2ApplicationScopeAliasesId);

		for (LiferayOAuth2Scope liferayOAuth2Scope : liferayOAuth2Scopes) {
			for (OAuth2ScopeGrant oAuth2ScopeGrant : oAuth2ScopeGrants) {
				if (_isMatch(oAuth2ScopeGrant, liferayOAuth2Scope)) {
					filteredLiferayOAuth2Scopes.add(liferayOAuth2Scope);

					break;
				}
			}
		}

		return filteredLiferayOAuth2Scopes;
	}

	@Override
	public Collection<OAuth2ScopeGrant> getOAuth2ScopeGrants(
		long oAuth2ApplicationScopeAliasesId, int start, int end,
		OrderByComparator<OAuth2ScopeGrant> orderByComparator) {

		return oAuth2ScopeGrantPersistence.
			findByOAuth2ApplicationScopeAliasesId(
				oAuth2ApplicationScopeAliasesId, start, end, orderByComparator);
	}

	@Override
	public Collection<OAuth2ScopeGrant> getOAuth2ScopeGrants(
		long companyId, String applicationName, String bundleSymbolicName,
		String accessTokenContent) {

		List<OAuth2ScopeGrant> oAuth2ScopeGrants = new ArrayList<>();

		for (OAuth2Authorization oAuth2Authorization :
				oAuth2AuthorizationPersistence.findByC_ATCH(
					companyId, accessTokenContent.hashCode())) {

			String currentAccessTokenContent =
				oAuth2Authorization.getAccessTokenContent();

			if (!MessageDigest.isEqual(
					accessTokenContent.getBytes(StandardCharsets.UTF_8),
					currentAccessTokenContent.getBytes(
						StandardCharsets.UTF_8))) {

				continue;
			}

			for (OAuth2ScopeGrant oAuth2ScopeGrant :
					oAuth2ScopeGrantPersistence.
						getOAuth2AuthorizationOAuth2ScopeGrants(
							oAuth2Authorization.getPrimaryKey())) {

				if (Objects.equals(
						applicationName,
						oAuth2ScopeGrant.getApplicationName()) &&
					Objects.equals(
						bundleSymbolicName,
						oAuth2ScopeGrant.getBundleSymbolicName())) {

					oAuth2ScopeGrants.add(oAuth2ScopeGrant);
				}
			}
		}

		return oAuth2ScopeGrants;
	}

	@Override
	public Collection<OAuth2ScopeGrant> grantLiferayOAuth2Scopes(
			long oAuth2AuthorizationId,
			Collection<LiferayOAuth2Scope> liferayOAuth2Scopes)
		throws PortalException {

		if (liferayOAuth2Scopes.isEmpty()) {
			return Collections.emptyList();
		}

		OAuth2Authorization oAuth2Authorization =
			oAuth2AuthorizationPersistence.findByPrimaryKey(
				oAuth2AuthorizationId);

		List<OAuth2ScopeGrant> oAuth2ScopeGrants =
			oAuth2ScopeGrantPersistence.findByOAuth2ApplicationScopeAliasesId(
				oAuth2Authorization.getOAuth2ApplicationScopeAliasesId());

		List<OAuth2ScopeGrant> resultOAuth2ScopeGrants = new ArrayList<>(
			oAuth2ScopeGrants.size());

		for (LiferayOAuth2Scope liferayOAuth2Scope : liferayOAuth2Scopes) {
			for (OAuth2ScopeGrant oAuth2ScopeGrant : oAuth2ScopeGrants) {
				if (_isMatch(oAuth2ScopeGrant, liferayOAuth2Scope)) {
					resultOAuth2ScopeGrants.add(oAuth2ScopeGrant);

					break;
				}
			}
		}

		addOAuth2AuthorizationOAuth2ScopeGrants(
			oAuth2AuthorizationId, resultOAuth2ScopeGrants);

		return resultOAuth2ScopeGrants;
	}

	private boolean _isMatch(
		OAuth2ScopeGrant oAuth2ScopeGrant,
		LiferayOAuth2Scope liferayOAuth2Scope) {

		if (!Objects.equals(
				oAuth2ScopeGrant.getApplicationName(),
				liferayOAuth2Scope.getApplicationName()) ||
			!Objects.equals(
				oAuth2ScopeGrant.getScope(), liferayOAuth2Scope.getScope())) {

			return false;
		}

		Bundle bundle = liferayOAuth2Scope.getBundle();

		String bundleSymbolicName = bundle.getSymbolicName();

		return Objects.equals(
			oAuth2ScopeGrant.getBundleSymbolicName(), bundleSymbolicName);
	}

}
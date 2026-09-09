/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.internal.resource.v1_0;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.ai.hub.cell.rest.dto.v1_0.AuthorizationToken;
import com.liferay.ai.hub.cell.rest.internal.web.cache.AIHubCellAccessTokenWebCacheItem;
import com.liferay.ai.hub.cell.rest.internal.web.cache.AIHubCellUserTokenWebCacheItem;
import com.liferay.ai.hub.cell.rest.resource.v1_0.AuthorizationTokenResource;
import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.auth.PrincipalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Feliphe Marinho
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/authorization-token.properties",
	scope = ServiceScope.PROTOTYPE, service = AuthorizationTokenResource.class
)
public class AuthorizationTokenResourceImpl
	extends BaseAuthorizationTokenResourceImpl {

	@Override
	public AuthorizationToken postAuthorizationToken() throws Exception {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		if (contextUser.isGuestUser()) {
			throw new PrincipalException.MustBeAuthenticated(
				contextUser.getUserId());
		}

		AIHubCellConfiguration aiHubCellConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AIHubCellConfiguration.class, contextCompany.getCompanyId());

		JSONObject jsonObject = AIHubCellAccessTokenWebCacheItem.get(
			aiHubCellConfiguration, contextCompany.getCompanyId());

		if (jsonObject == null) {
			throw new PortalException("Unable to get an access token");
		}

		return new AuthorizationToken() {
			{
				setAccessToken(() -> jsonObject.getString("access_token"));
				setScope(() -> jsonObject.getString("scope"));
				setServiceURL(aiHubCellConfiguration::serviceURL);
				setUserToken(
					() -> {
						OAuth2Application oAuth2Application =
							_oAuth2ApplicationLocalService.
								getOAuth2ApplicationByExternalReferenceCode(
									"AI-HUB-CELL",
									contextCompany.getCompanyId());

						return AIHubCellUserTokenWebCacheItem.get(
							_localOAuthClient, oAuth2Application,
							contextUser.getUserId());
					});
			}
		};
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private LocalOAuthClient _localOAuthClient;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}
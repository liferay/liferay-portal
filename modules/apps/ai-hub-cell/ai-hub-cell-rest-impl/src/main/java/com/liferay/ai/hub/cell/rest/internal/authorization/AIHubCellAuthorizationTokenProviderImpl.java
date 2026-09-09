/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.internal.authorization;

import com.liferay.ai.hub.cell.authorization.AIHubCellAuthorizationTokenProvider;
import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.ai.hub.cell.rest.internal.web.cache.AIHubCellAccessTokenWebCacheItem;
import com.liferay.ai.hub.cell.rest.internal.web.cache.AIHubCellUserTokenWebCacheItem;
import com.liferay.oauth.client.LocalOAuthClient;
import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.oauth2.provider.service.OAuth2ApplicationLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Uen
 */
@Component(service = AIHubCellAuthorizationTokenProvider.class)
public class AIHubCellAuthorizationTokenProviderImpl
	implements AIHubCellAuthorizationTokenProvider {

	@Override
	public JSONObject getAuthorizationTokenJSONObject(
			long companyId, long userId)
		throws PortalException {

		AIHubCellConfiguration aiHubCellConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AIHubCellConfiguration.class, companyId);

		JSONObject jsonObject = AIHubCellAccessTokenWebCacheItem.get(
			aiHubCellConfiguration, companyId);

		if (jsonObject == null) {
			throw new PortalException("Unable to get an access token");
		}

		return JSONUtil.put(
			"accessToken", jsonObject.getString("access_token")
		).put(
			"scope", jsonObject.getString("scope")
		).put(
			"serviceURL", aiHubCellConfiguration.serviceURL()
		).put(
			"userToken",
			() -> {
				OAuth2Application oAuth2Application =
					_oAuth2ApplicationLocalService.
						getOAuth2ApplicationByExternalReferenceCode(
							"AI-HUB-CELL", companyId);

				return AIHubCellUserTokenWebCacheItem.get(
					_localOAuthClient, oAuth2Application, userId);
			}
		);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private LocalOAuthClient _localOAuthClient;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

}

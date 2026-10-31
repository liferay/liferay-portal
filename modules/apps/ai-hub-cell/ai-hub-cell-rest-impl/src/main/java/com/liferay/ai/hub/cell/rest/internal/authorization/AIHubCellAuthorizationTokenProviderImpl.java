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
import com.liferay.oauth2.provider.service.OAuth2ApplicationScopeAliasesLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.StringUtil;

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

		Company company = _companyLocalService.getCompany(companyId);

		if (StringUtil.equalsIgnoreCase(
				company.getVirtualHostname(),
				HttpComponentsUtil.getDomain(
					aiHubCellConfiguration.serviceURL()))) {

			OAuth2Application oAuth2Application =
				_oAuth2ApplicationLocalService.
					getOAuth2ApplicationByExternalReferenceCode(
						"AI-HUB-CELL", companyId);

			String userToken = AIHubCellUserTokenWebCacheItem.get(
				_localOAuthClient, oAuth2Application, userId);

			if (userToken == null) {
				throw new PortalException("Unable to get an access token");
			}

			return JSONUtil.put(
				"accessToken", userToken
			).put(
				"scope",
				StringUtil.merge(
					_oAuth2ApplicationScopeAliasesLocalService.
						getScopeAliasesList(
							oAuth2Application.
								getOAuth2ApplicationScopeAliasesId()),
					StringPool.SPACE)
			).put(
				"serviceURL", aiHubCellConfiguration.serviceURL()
			).put(
				"userToken", userToken
			);
		}

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
	private CompanyLocalService _companyLocalService;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private LocalOAuthClient _localOAuthClient;

	@Reference
	private OAuth2ApplicationLocalService _oAuth2ApplicationLocalService;

	@Reference
	private OAuth2ApplicationScopeAliasesLocalService
		_oAuth2ApplicationScopeAliasesLocalService;

}
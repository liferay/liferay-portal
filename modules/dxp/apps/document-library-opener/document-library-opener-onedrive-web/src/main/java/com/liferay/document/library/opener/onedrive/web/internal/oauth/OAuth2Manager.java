/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.oauth;

import com.github.scribejava.apis.MicrosoftAzureActiveDirectory20Api;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.ServiceBuilderOAuth20;
import com.github.scribejava.core.oauth.OAuth20Service;

import com.liferay.document.library.opener.onedrive.web.internal.configuration.DLOneDriveCompanyConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.key.secret.SecretResolver;

import java.io.IOException;

import java.util.concurrent.ExecutionException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = OAuth2Manager.class)
public class OAuth2Manager {

	public AccessToken createAccessToken(
			long companyId, long userId, String code, String portalURL)
		throws Exception {

		try (OAuth20Service oAuth20Service = _createOAuth20Service(
				companyId, _getRedirectURI(portalURL))) {

			AccessToken accessToken = new AccessToken(
				oAuth20Service.getAccessToken(code));

			AccessTokenStoreUtil.add(companyId, userId, accessToken);

			return accessToken;
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	public AccessToken getAccessToken(long companyId, long userId)
		throws PortalException {

		AccessToken accessToken = AccessTokenStoreUtil.getAccessToken(
			companyId, userId);

		if (accessToken == null) {
			return null;
		}

		if (!accessToken.isValid()) {
			return _refreshOAuth2AccessToken(companyId, userId, accessToken);
		}

		return accessToken;
	}

	public String getAuthorizationURL(
			long companyId, String portalURL, String state)
		throws PortalException {

		try (OAuth20Service oAuth20Service = _createOAuth20Service(
				companyId, _getRedirectURI(portalURL))) {

			return oAuth20Service.getAuthorizationUrl(state);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	public boolean hasAccessToken(long companyId, long userId)
		throws PortalException {

		AccessToken accessToken = getAccessToken(companyId, userId);

		if (accessToken == null) {
			return false;
		}

		return true;
	}

	public void revokeOAuth2AccessToken(long companyId, long userId) {
		AccessToken accessToken = AccessTokenStoreUtil.getAccessToken(
			companyId, userId);

		if (accessToken == null) {
			return;
		}

		AccessTokenStoreUtil.delete(companyId, userId);
	}

	private OAuth20Service _createOAuth20Service(
			long companyId, String redirectURL)
		throws PortalException {

		DLOneDriveCompanyConfiguration dlOneDriveCompanyConfiguration =
			_getDLOneDriveCompanyConfiguration(companyId);

		ServiceBuilderOAuth20 serviceBuilderOAuth20 = new ServiceBuilder(
			dlOneDriveCompanyConfiguration.clientId()
		).apiSecret(
			_secretResolver.resolve(
				companyId, dlOneDriveCompanyConfiguration.clientSecret())
		).callback(
			redirectURL
		).withScope(
			"https://graph.microsoft.com/.default"
		).apiKey(
			dlOneDriveCompanyConfiguration.clientId()
		);

		try (OAuth20Service oAuth20Service = serviceBuilderOAuth20.build(
				MicrosoftAzureActiveDirectory20Api.custom(
					dlOneDriveCompanyConfiguration.tenant()))) {

			return oAuth20Service;
		}
		catch (Exception exception) {
			throw new PortalException(
				"Unable to create OAuth20Service", exception);
		}
	}

	private DLOneDriveCompanyConfiguration _getDLOneDriveCompanyConfiguration(
			long companyId)
		throws ConfigurationException {

		return _configurationProvider.getCompanyConfiguration(
			DLOneDriveCompanyConfiguration.class, companyId);
	}

	private String _getRedirectURI(String portalURL) {
		return StringBundler.concat(
			portalURL, _portal.getPathContext(), Portal.PATH_MODULE,
			"/document_library/onedrive/oauth2");
	}

	private AccessToken _refreshOAuth2AccessToken(
			long companyId, long userId, AccessToken accessToken)
		throws PortalException {

		if (accessToken.getRefreshToken() == null) {
			return null;
		}

		try (OAuth20Service oAuth20Service = _createOAuth20Service(
				companyId, null)) {

			AccessToken newAccessToken = new AccessToken(
				oAuth20Service.refreshAccessToken(
					accessToken.getRefreshToken()));

			AccessTokenStoreUtil.add(companyId, userId, newAccessToken);

			return newAccessToken;
		}
		catch (ExecutionException | InterruptedException | IOException
					exception) {

			throw new PortalException(exception);
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private SecretResolver _secretResolver;

}
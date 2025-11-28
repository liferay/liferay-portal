/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal;

import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectServiceException;

import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.net.URL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(service = AuthorizationServerMetadataResolver.class)
public class AuthorizationServerMetadataResolver {

	public OIDCProviderMetadata resolveOIDCProviderMetadata(
			String authServerWellKnownURI, int metadataCacheInSeconds,
			long oAuthClientEntryId)
		throws Exception {

		if (authServerWellKnownURI.endsWith("local")) {
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataLocalService.
					getOAuthClientASLocalMetadata(authServerWellKnownURI);

			return OIDCProviderMetadata.parse(
				oAuthClientASLocalMetadata.getMetadataJSONOIC());
		}

		OIDCProviderMetadata oidcProviderMetadata =
			_oidcProviderMetadataPortalCache.get(oAuthClientEntryId);

		if (oidcProviderMetadata != null) {
			return oidcProviderMetadata;
		}

		HTTPRequest httpRequest = new HTTPRequest(
			HTTPRequest.Method.GET, new URL(authServerWellKnownURI));

		HTTPResponse httpResponse = httpRequest.send();

		if (httpResponse.getStatusCode() != HTTPResponse.SC_OK) {
			throw new OpenIdConnectServiceException.ProviderException(
				httpResponse.getStatusMessage());
		}

		oidcProviderMetadata = OIDCProviderMetadata.parse(
			httpResponse.getContent());

		_oidcProviderMetadataPortalCache.put(
			oAuthClientEntryId, oidcProviderMetadata, metadataCacheInSeconds);

		return oidcProviderMetadata;
	}

	@Reference
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	private final PortalCache<Long, OIDCProviderMetadata>
		_oidcProviderMetadataPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM,
			AuthorizationServerMetadataResolver.class.getName());

}
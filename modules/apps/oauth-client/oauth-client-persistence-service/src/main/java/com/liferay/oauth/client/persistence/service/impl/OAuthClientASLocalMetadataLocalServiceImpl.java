/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientASLocalMetadataException;
import com.liferay.oauth.client.persistence.exception.OAuthClientASLocalMetadataJSONException;
import com.liferay.oauth.client.persistence.exception.OAuthClientASLocalMetadataLocalWellKnownURIException;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.base.OAuthClientASLocalMetadataLocalServiceBaseImpl;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Base64;

import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.net.URI;
import java.net.URISyntaxException;

import java.security.MessageDigest;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = "model.class.name=com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata",
	service = AopService.class
)
public class OAuthClientASLocalMetadataLocalServiceImpl
	extends OAuthClientASLocalMetadataLocalServiceBaseImpl {

	@Override
	public OAuthClientASLocalMetadata addOAuthClientASLocalMetadata(
			long userId, Boolean enabled, String issuerString, String jwksUri,
			String[] supportedGrantTypes, String[] supportedScopes,
			String tokenEndpointString)
		throws PortalException {

		// 		AuthorizationServerMetadata authorizationServerMetadata =

		//			_parseAuthorizationServerMetadata(metadataJSON, wellKnownURISuffix);

		String localWellKnownURIOIC = _generateLocalWellKnownURI(
			issuerString, tokenEndpointString, "openid-configuration");

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataPersistence.fetchByLocalWellKnownURIOIC(
				localWellKnownURIOIC);

		if (oAuthClientASLocalMetadata != null) {
			throw new DuplicateOAuthClientASLocalMetadataException();
		}

		String metadataJSONOIC = buildOpenIdConfigurationJSON(
			issuerString, jwksUri, supportedScopes, supportedGrantTypes,
			tokenEndpointString);

		String metadataJSONOAS = buildAuthorizationServerJSON(
			issuerString, jwksUri, supportedScopes, supportedGrantTypes,
			tokenEndpointString);

		User user = _userLocalService.getUser(userId);

		oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataPersistence.create(
				counterLocalService.increment());

		oAuthClientASLocalMetadata.setCompanyId(user.getCompanyId());
		oAuthClientASLocalMetadata.setUserId(user.getUserId());
		oAuthClientASLocalMetadata.setUserName(user.getFullName());
		oAuthClientASLocalMetadata.setIssuer(issuerString);
		oAuthClientASLocalMetadata.setLocalWellKnownEnabled(enabled);
		oAuthClientASLocalMetadata.setLocalWellKnownURIOAS(
			_generateLocalWellKnownURI(
				issuerString, null, "oauth-authorization-server"));
		oAuthClientASLocalMetadata.setLocalWellKnownURIOIC(
			localWellKnownURIOIC);
		oAuthClientASLocalMetadata.setMetadataJSONOAS(metadataJSONOAS);
		oAuthClientASLocalMetadata.setMetadataJSONOIC(metadataJSONOIC);

		oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataPersistence.update(
				oAuthClientASLocalMetadata);

		_resourceLocalService.addResources(
			oAuthClientASLocalMetadata.getCompanyId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			oAuthClientASLocalMetadata.getUserId(),
			OAuthClientASLocalMetadata.class.getName(),
			oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId(), false,
			false, false);

		return oAuthClientASLocalMetadata;
	}

	@Override
	public OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId)
		throws PortalException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataPersistence.findByPrimaryKey(
				oAuthClientASLocalMetadataId);

		return deleteOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	public OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata)
		throws PortalException {

		oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataPersistence.remove(
				oAuthClientASLocalMetadata);

		_resourceLocalService.deleteResource(
			oAuthClientASLocalMetadata.getCompanyId(),
			OAuthClientASLocalMetadata.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId());

		return oAuthClientASLocalMetadata;
	}

	@Override
	public OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			String localWellKnownURI)
		throws PortalException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataPersistence.findByLocalWellKnownURIOIC(
				localWellKnownURI);

		return deleteOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	public OAuthClientASLocalMetadata fetchOAuthClientASLocalMetadata(
		String localWellKnownURI) {

		return oAuthClientASLocalMetadataPersistence.
			fetchByLocalWellKnownURIOIC(localWellKnownURI);
	}

	@Override
	public List<OAuthClientASLocalMetadata>
		getCompanyOAuthClientASLocalMetadata(long companyId) {

		return oAuthClientASLocalMetadataPersistence.findByCompanyId(companyId);
	}

	@Override
	public List<OAuthClientASLocalMetadata>
		getCompanyOAuthClientASLocalMetadata(
			long companyId, int start, int end) {

		return oAuthClientASLocalMetadataPersistence.findByCompanyId(
			companyId, start, end);
	}

	@Override
	public OAuthClientASLocalMetadata getOAuthClientASLocalMetadata(
			String localWellKnownURI)
		throws PortalException {

		return oAuthClientASLocalMetadataPersistence.findByLocalWellKnownURIOIC(
			localWellKnownURI);
	}

	@Override
	public List<OAuthClientASLocalMetadata> getUserOAuthClientASLocalMetadata(
		long userId) {

		return oAuthClientASLocalMetadataPersistence.findByUserId(userId);
	}

	@Override
	public List<OAuthClientASLocalMetadata> getUserOAuthClientASLocalMetadata(
		long userId, int start, int end) {

		return oAuthClientASLocalMetadataPersistence.findByUserId(
			userId, start, end);
	}

	@Override
	public OAuthClientASLocalMetadata updateOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId, String metadataJSON,
			String wellKnownURISuffix)
		throws PortalException {

		AuthorizationServerMetadata authorizationServerMetadata =
			_parseAuthorizationServerMetadata(metadataJSON, wellKnownURISuffix);

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId);

		AuthorizationServerMetadata currentAuthorizationServerMetadata =
			_parseAuthorizationServerMetadata(
				oAuthClientASLocalMetadata.getMetadataJSONOIC(),
				wellKnownURISuffix);

		oAuthClientASLocalMetadata.setMetadataJSONOIC(metadataJSON);

		String currentIssuer = String.valueOf(
			currentAuthorizationServerMetadata.getIssuer());
		String currentLocalWellKnownURI =
			oAuthClientASLocalMetadata.getLocalWellKnownURIOIC();

		if (!currentIssuer.equals(
				String.valueOf(authorizationServerMetadata.getIssuer())) ||
			!currentLocalWellKnownURI.contains(wellKnownURISuffix)) {

			oAuthClientASLocalMetadata.setLocalWellKnownURIOIC(
				_generateLocalWellKnownURI(
					String.valueOf(authorizationServerMetadata.getIssuer()),
					String.valueOf(
						authorizationServerMetadata.getTokenEndpointURI()),
					wellKnownURISuffix));
		}

		return oAuthClientASLocalMetadataPersistence.update(
			oAuthClientASLocalMetadata);
	}

	private String _generateLocalWellKnownURI(
			String issuer, String tokenEndpoint, String wellKnownURISuffix)
		throws PortalException {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			URI issuerURI = URI.create(issuer);

			if (wellKnownURISuffix.equals("openid-configuration")) {
				return StringBundler.concat(
					issuerURI.getScheme(), "://", issuerURI.getAuthority(),
					"/.well-known/", wellKnownURISuffix, issuerURI.getPath(),
					'/',
					Base64.encodeToURL(
						messageDigest.digest(tokenEndpoint.getBytes())),
					"/local");
			}

			return StringBundler.concat(
				issuerURI.getScheme(), "://", issuerURI.getAuthority(),
				"/o/.well-known/", wellKnownURISuffix);
		}
		catch (Exception exception) {
			throw new OAuthClientASLocalMetadataLocalWellKnownURIException(
				exception);
		}
	}

	private AuthorizationServerMetadata _parseAuthorizationServerMetadata(
			String metadataJSON, String wellKnownURISuffix)
		throws PortalException {

		try {
			if (wellKnownURISuffix.equals("openid-configuration")) {
				return OIDCProviderMetadata.parse(metadataJSON);
			}

			return AuthorizationServerMetadata.parse(metadataJSON);
		}
		catch (Exception exception) {
			throw new OAuthClientASLocalMetadataJSONException(
				exception.getMessage(), exception);
		}
	}

	// ---------------------------------------------------------
	// Construcción del JSON de oauth-authorization-server
	// ---------------------------------------------------------

	private String buildAuthorizationServerJSON(
			String issuerStr, String jwksUri, String[] supportedScopes,
			String[] supportedGrantTypes, String tokenEndpoint)
		throws PortalException {

		try {
			Issuer issuer = new Issuer(issuerStr);

			AuthorizationServerMetadata metadata =
				new AuthorizationServerMetadata(issuer);

			metadata.setJWKSetURI(new URI(jwksUri));
			metadata.setTokenEndpointURI(new URI(tokenEndpoint));

			// scopes

			Scope scope = new Scope();

			for (String s : supportedScopes) {
				scope.add(s);
			}

			metadata.setScopes(scope);

			// grant types

			GrantType[] grantTypes = new GrantType[supportedGrantTypes.length];

			for (int i = 0; i < supportedGrantTypes.length; i++) {
				grantTypes[i] = toGrantType(supportedGrantTypes[i]);
			}

			metadata.setGrantTypes(Arrays.asList(grantTypes));

			// auth methods típicos

			metadata.setTokenEndpointAuthMethods(
				Arrays.asList(
					ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
					ClientAuthenticationMethod.CLIENT_SECRET_POST));

			return metadata.toJSONObject(
			).toJSONString();
		}
		catch (URISyntaxException e) {
			throw new PortalException(
				"Error construyendo el JSON de AuthorizationServerMetadata", e);
		}
	}

	private String buildOpenIdConfigurationJSON(
			String issuerStr, String jwksUri, String[] supportedScopes,
			String[] supportedGrantTypes, String tokenEndpoint)
		throws PortalException {

		try {
			Issuer issuer = new Issuer(issuerStr);

			// OIDC requiere subject types, normalmente solo PUBLIC

			OIDCProviderMetadata metadata = new OIDCProviderMetadata(
				issuer, Arrays.asList(SubjectType.PUBLIC), new URI(jwksUri));

			metadata.setTokenEndpointURI(new URI(tokenEndpoint));

			// scopes

			Scope scope = new Scope();

			for (String s : supportedScopes) {
				scope.add(s);
			}

			metadata.setScopes(scope);

			// grant types

			GrantType[] grantTypes = new GrantType[supportedGrantTypes.length];

			for (int i = 0; i < supportedGrantTypes.length; i++) {
				grantTypes[i] = toGrantType(supportedGrantTypes[i]);
			}

			metadata.setGrantTypes(Arrays.asList(grantTypes));

			return metadata.toJSONObject(
			).toJSONString();
		}
		catch (URISyntaxException e) {
			throw new PortalException(
				"Error construyendo el JSON de OIDCProviderMetadata", e);
		}
	}

	private GrantType toGrantType(String grantType) {
		switch (grantType) {
			case "authorization_code":
				return GrantType.AUTHORIZATION_CODE;
			case "client_credentials":
				return GrantType.CLIENT_CREDENTIALS;
			case "password":
				return GrantType.PASSWORD;
			case "refresh_token":
				return GrantType.REFRESH_TOKEN;
			default:

				// Para extensiones tipo device_code, jwt-bearer, etc.

				return new GrantType(grantType);
		}
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
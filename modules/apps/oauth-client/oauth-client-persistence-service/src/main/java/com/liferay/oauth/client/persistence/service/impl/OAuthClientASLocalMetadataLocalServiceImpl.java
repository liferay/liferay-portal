/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientASLocalMetadataException;
import com.liferay.oauth.client.persistence.exception.OAuthClientASLocalMetadataIssuerException;
import com.liferay.oauth.client.persistence.exception.OAuthClientASLocalMetadataLocalWellKnownURIException;
import com.liferay.oauth.client.persistence.exception.OAuthClientASLocalMetadataMetadataJSONException;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.base.OAuthClientASLocalMetadataLocalServiceBaseImpl;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.security.MessageDigest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
			long userId, String metadataJSON, String wellKnownURISuffix)
		throws PortalException {

		AuthorizationServerMetadata authorizationServerMetadata =
			_parseAuthorizationServerMetadata(metadataJSON, wellKnownURISuffix);

		return addOAuthClientASLocalMetadata(
			null, userId,
			String.valueOf(
				authorizationServerMetadata.getAuthorizationEndpointURI()),
			String.valueOf(authorizationServerMetadata.getIssuer()),
			String.valueOf(authorizationServerMetadata.getJWKSetURI()), false,
			String.valueOf(
				authorizationServerMetadata.getRegistrationEndpointURI()),
			StringUtil.split(
				StringUtil.merge(authorizationServerMetadata.getGrantTypes()),
				StringPool.COMMA),
			StringUtil.split(
				StringUtil.merge(authorizationServerMetadata.getScopes()),
				StringPool.COMMA),
			StringUtil.split(
				_getSubjectTypes(authorizationServerMetadata),
				StringPool.COMMA),
			String.valueOf(authorizationServerMetadata.getTokenEndpointURI()),
			_getUserInfoEndpointURI(authorizationServerMetadata));
	}

	public OAuthClientASLocalMetadata addOAuthClientASLocalMetadata(
			String externalReferenceCode, long userId,
			String authorizationEndpoint, String issuer, String jwksURI,
			boolean localWellKnownEnabled, String registrationEndpoint,
			String[] supportedGrantTypes, String[] supportedScopes,
			String[] supportedSubjectTypes, String tokenEndpoint,
			String userInfoEndpoint)
		throws PortalException {

		User user = _userLocalService.getUser(userId);

		String localWellKnownURI = _generateLocalWellKnownURI(
			issuer, tokenEndpoint, "openid-configuration");

		_validate(
			null, user.getCompanyId(), authorizationEndpoint, issuer, jwksURI,
			localWellKnownURI, registrationEndpoint, tokenEndpoint,
			userInfoEndpoint);

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataPersistence.create(
				counterLocalService.increment());

		oAuthClientASLocalMetadata.setExternalReferenceCode(
			externalReferenceCode);
		oAuthClientASLocalMetadata.setCompanyId(user.getCompanyId());
		oAuthClientASLocalMetadata.setUserId(user.getUserId());
		oAuthClientASLocalMetadata.setUserName(user.getFullName());
		oAuthClientASLocalMetadata.setIssuer(issuer);
		oAuthClientASLocalMetadata.setLocalWellKnownEnabled(
			localWellKnownEnabled);
		oAuthClientASLocalMetadata.setLocalWellKnownURI(localWellKnownURI);
		oAuthClientASLocalMetadata.setMetadataJSON(
			_generateMetadataJSON(
				authorizationEndpoint, issuer, jwksURI, supportedGrantTypes,
				supportedScopes, supportedSubjectTypes, tokenEndpoint,
				userInfoEndpoint));
		oAuthClientASLocalMetadata.setOAuthASLocalWellKnownURI(
			_generateLocalWellKnownURI(
				issuer, null, "oauth-authorization-server"));
		oAuthClientASLocalMetadata.setOAuthASMetadataJSON(
			_generateAuthorizationServerMetadataJSON(
				authorizationEndpoint, issuer, jwksURI, registrationEndpoint,
				supportedScopes, supportedGrantTypes, tokenEndpoint));

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
			long companyId, String localWellKnownURI)
		throws PortalException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataPersistence.findByC_LWKURI(
				companyId, localWellKnownURI);

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
	public OAuthClientASLocalMetadata fetchOAuthClientASLocalMetadata(
		long companyId, boolean localWellKnownEnabled,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return oAuthClientASLocalMetadataPersistence.fetchByC_L_First(
			companyId, localWellKnownEnabled, orderByComparator);
	}

	@Override
	public OAuthClientASLocalMetadata fetchOAuthClientASLocalMetadata(
		long companyId, String issuer) {

		return oAuthClientASLocalMetadataPersistence.fetchByC_I(
			companyId, issuer);
	}

	@Override
	public OAuthClientASLocalMetadata
		fetchOAuthClientASLocalMetadataByLocalWellKnownURI(
			long companyId, String localWellKnownURI) {

		return oAuthClientASLocalMetadataPersistence.fetchByC_LWKURI(
			companyId, localWellKnownURI);
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
			long companyId, String localWellKnownURI)
		throws PortalException {

		return oAuthClientASLocalMetadataPersistence.findByC_LWKURI(
			companyId, localWellKnownURI);
	}

	@Override
	public int getOAuthClientASLocalMetadatasCount(long companyId) {
		return oAuthClientASLocalMetadataPersistence.countByCompanyId(
			companyId);
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

		return updateOAuthClientASLocalMetadata(
			oAuthClientASLocalMetadataId,
			String.valueOf(
				authorizationServerMetadata.getAuthorizationEndpointURI()),
			String.valueOf(authorizationServerMetadata.getIssuer()),
			String.valueOf(authorizationServerMetadata.getJWKSetURI()), false,
			String.valueOf(
				authorizationServerMetadata.getRegistrationEndpointURI()),
			StringUtil.split(
				StringUtil.merge(authorizationServerMetadata.getGrantTypes()),
				StringPool.COMMA),
			StringUtil.split(
				StringUtil.merge(authorizationServerMetadata.getScopes()),
				StringPool.COMMA),
			StringUtil.split(
				_getSubjectTypes(authorizationServerMetadata),
				StringPool.COMMA),
			String.valueOf(authorizationServerMetadata.getTokenEndpointURI()),
			_getUserInfoEndpointURI(authorizationServerMetadata));
	}

	public OAuthClientASLocalMetadata updateOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId, String authorizationEndpoint,
			String issuer, String jwksURI, boolean localWellKnownEnabled,
			String registrationEndpoint, String[] supportedGrantTypes,
			String[] supportedScopes, String[] supportedSubjectTypes,
			String tokenEndpoint, String userInfoEndpoint)
		throws PortalException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId);

		String localWellKnownURI =
			oAuthClientASLocalMetadata.getLocalWellKnownURI();

		if (!issuer.equals(oAuthClientASLocalMetadata.getIssuer()) ||
			localWellKnownURI.contains("openid-configuration")) {

			_validate(
				oAuthClientASLocalMetadata,
				oAuthClientASLocalMetadata.getCompanyId(),
				authorizationEndpoint, issuer, jwksURI, localWellKnownURI,
				registrationEndpoint, tokenEndpoint, userInfoEndpoint);

			oAuthClientASLocalMetadata.setIssuer(issuer);
			oAuthClientASLocalMetadata.setLocalWellKnownEnabled(
				localWellKnownEnabled);
			oAuthClientASLocalMetadata.setLocalWellKnownURI(
				_generateLocalWellKnownURI(
					issuer, tokenEndpoint, "openid-configuration"));
			oAuthClientASLocalMetadata.setMetadataJSON(
				_generateMetadataJSON(
					authorizationEndpoint, issuer, jwksURI, supportedGrantTypes,
					supportedScopes, supportedSubjectTypes, tokenEndpoint,
					userInfoEndpoint));
			oAuthClientASLocalMetadata.setOAuthASLocalWellKnownURI(
				_generateLocalWellKnownURI(
					issuer, null, "oauth-authorization-server"));
			oAuthClientASLocalMetadata.setOAuthASMetadataJSON(
				_generateAuthorizationServerMetadataJSON(
					authorizationEndpoint, issuer, jwksURI,
					registrationEndpoint, supportedScopes, supportedGrantTypes,
					tokenEndpoint));

			oAuthClientASLocalMetadata =
				oAuthClientASLocalMetadataPersistence.update(
					oAuthClientASLocalMetadata);
		}

		return oAuthClientASLocalMetadata;
	}

	private String _generateAuthorizationServerMetadataJSON(
			String authorizationEndpoint, String issuer, String jwksURI,
			String registrationEndpoint, String[] supportedScopes,
			String[] supportedGrantTypes, String tokenEndpoint)
		throws PortalException {

		try {
			AuthorizationServerMetadata authorizationServerMetadata =
				new AuthorizationServerMetadata(new Issuer(issuer));

			authorizationServerMetadata.setAuthorizationEndpointURI(
				new URI(authorizationEndpoint));
			authorizationServerMetadata.setCodeChallengeMethods(
				Collections.singletonList(CodeChallengeMethod.S256));
			authorizationServerMetadata.setGrantTypes(
				TransformUtil.transformToList(
					supportedGrantTypes, GrantType::parse));
			authorizationServerMetadata.setJWKSetURI(new URI(jwksURI));
			authorizationServerMetadata.setRegistrationEndpointURI(
				new URI(registrationEndpoint));
			authorizationServerMetadata.setResponseTypes(
				Collections.singletonList(new ResponseType("code")));
			authorizationServerMetadata.setScopes(new Scope(supportedScopes));
			authorizationServerMetadata.setTokenEndpointURI(
				new URI(tokenEndpoint));

			return String.valueOf(authorizationServerMetadata.toJSONObject());
		}
		catch (Exception exception) {
			throw new OAuthClientASLocalMetadataMetadataJSONException(
				exception.getMessage(), exception);
		}
	}

	private String _generateLocalWellKnownURI(
			String issuer, String tokenEndpoint, String wellKnownURISuffix)
		throws PortalException {

		try {
			URI issuerURI = URI.create(issuer);

			if (wellKnownURISuffix.equals("openid-configuration")) {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");

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

	private String _generateMetadataJSON(
			String authorizationEndpoint, String issuer, String jwksURI,
			String[] supportedGrantTypes, String[] supportedScopes,
			String[] supportedSubjectTypes, String tokenEndpoint,
			String userInfoEndpoint)
		throws PortalException {

		try {
			OIDCProviderMetadata oidcProviderMetadata =
				new OIDCProviderMetadata(
					new Issuer(issuer),
					TransformUtil.transformToList(
						supportedSubjectTypes, SubjectType::parse),
					new URI(jwksURI));

			oidcProviderMetadata.setAuthorizationEndpointURI(
				new URI(authorizationEndpoint));
			oidcProviderMetadata.setCodeChallengeMethods(
				Collections.singletonList(CodeChallengeMethod.S256));
			oidcProviderMetadata.setGrantTypes(
				TransformUtil.transformToList(
					supportedGrantTypes, GrantType::parse));
			oidcProviderMetadata.setResponseTypes(
				Collections.singletonList(new ResponseType("code")));
			oidcProviderMetadata.setScopes(new Scope(supportedScopes));
			oidcProviderMetadata.setTokenEndpointURI(new URI(tokenEndpoint));
			oidcProviderMetadata.setUserInfoEndpointURI(
				new URI(userInfoEndpoint));

			return String.valueOf(oidcProviderMetadata.toJSONObject());
		}
		catch (Exception exception) {
			throw new OAuthClientASLocalMetadataMetadataJSONException(
				exception.getMessage(), exception);
		}
	}

	private String _getSubjectTypes(
		AuthorizationServerMetadata authorizationServerMetadata) {

		if (authorizationServerMetadata instanceof
				OIDCProviderMetadata oidcProviderMetadata) {

			return StringUtil.merge(oidcProviderMetadata.getSubjectTypes());
		}

		return StringPool.BLANK;
	}

	private String _getUserInfoEndpointURI(
		AuthorizationServerMetadata authorizationServerMetadata) {

		if (authorizationServerMetadata instanceof
				OIDCProviderMetadata oidcProviderMetadata) {

			return String.valueOf(
				oidcProviderMetadata.getUserInfoEndpointURI());
		}

		return StringPool.BLANK;
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
			throw new OAuthClientASLocalMetadataMetadataJSONException(
				exception.getMessage(), exception);
		}
	}

	private void _validate(
			OAuthClientASLocalMetadata oldOAuthClientASLocalMetadata,
			long companyId, String authorizationEndpoint, String issuer,
			String jwksURI, String localWellKnownURI,
			String registrationEndpoint, String tokenEndpoint,
			String userInfoEndpoint)
		throws PortalException {

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-63415")) {
			_validateURL(authorizationEndpoint);

			if (Validator.isNull(issuer)) {
				throw new OAuthClientASLocalMetadataIssuerException();
			}

			_validateURL(issuer);
			_validateURL(jwksURI);
			_validateURL(registrationEndpoint);
			_validateURL(tokenEndpoint);
			_validateURL(userInfoEndpoint);
		}

		if (oldOAuthClientASLocalMetadata == null) {
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
				oAuthClientASLocalMetadataPersistence.fetchByC_LWKURI(
					companyId, localWellKnownURI);

			if (oAuthClientASLocalMetadata != null) {
				throw new DuplicateOAuthClientASLocalMetadataException();
			}
		}

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataPersistence.fetchByC_I(companyId, issuer);

		if ((oAuthClientASLocalMetadata != null) &&
			!Objects.equals(
				oldOAuthClientASLocalMetadata, oAuthClientASLocalMetadata)) {

			throw new DuplicateOAuthClientASLocalMetadataException();
		}
	}

	private void _validateURL(String urlString) throws PortalException {
		if (Validator.isNull(urlString)) {
			return;
		}

		try {
			URL url = new URL(urlString);

			if (!Http.HTTPS.equalsIgnoreCase(url.getProtocol())) {
				throw new OAuthClientASLocalMetadataLocalWellKnownURIException(
					urlString);
			}
		}
		catch (MalformedURLException malformedURLException) {
			throw new OAuthClientASLocalMetadataLocalWellKnownURIException(
				urlString, malformedURLException);
		}
	}

	@Reference
	private ResourceLocalService _resourceLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
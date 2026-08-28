/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.upgrade.v1_6_2;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.net.URI;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Jorge García Jiménez
 */
public class OAuthClientASLocalMetadataUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery(
				StringBundler.concat(
					"select oAuthClientASLocalMetadataId, issuer, ",
					"metadataJSON, oAuthASMetadataJSON from ",
					"OAuthClientASLocalMetadata"));

			PreparedStatement preparedStatement =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update OAuthClientASLocalMetadata set " +
						"oAuthASLocalWellKnownURI = ?, oAuthASMetadataJSON = " +
							"? where oAuthClientASLocalMetadataId = ?")) {

			while (resultSet.next()) {
				String issuer = resultSet.getString("issuer");
				long oAuthClientASLocalMetadataId = resultSet.getLong(
					"oAuthClientASLocalMetadataId");

				preparedStatement.setString(
					1,
					_generateOAuthASLocalWellKnownURI(
						issuer, oAuthClientASLocalMetadataId));

				String oAuthASMetadataJSON = _generateOAuthASMetadataJSON(
					issuer, resultSet.getString("metadataJSON"),
					oAuthClientASLocalMetadataId);

				if (oAuthASMetadataJSON == null) {
					oAuthASMetadataJSON = GetterUtil.getString(
						resultSet.getString("oAuthASMetadataJSON"));
				}

				preparedStatement.setString(2, oAuthASMetadataJSON);

				preparedStatement.setLong(3, oAuthClientASLocalMetadataId);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

	private URI _generateIntrospectionEndpointURI(URI tokenEndpointURI) {
		if (tokenEndpointURI == null) {
			return null;
		}

		String tokenEndpointString = _removeTrailingSlash(
			String.valueOf(tokenEndpointURI));

		if (!tokenEndpointString.endsWith("/token")) {
			return null;
		}

		return URI.create(
			StringUtil.replaceLast(
				tokenEndpointString, "/token", "/introspect"));
	}

	private String _generateOAuthASLocalWellKnownURI(
		String issuer, long oAuthClientASLocalMetadataId) {

		if (Validator.isNull(issuer)) {
			return _generatePlaceholderOAuthASLocalWellKnownURI(
				oAuthClientASLocalMetadataId, "its issuer is null");
		}

		URI issuerURI = null;

		try {
			issuerURI = URI.create(issuer);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}

			return _generatePlaceholderOAuthASLocalWellKnownURI(
				oAuthClientASLocalMetadataId, "its issuer is an invalid URI");
		}

		if (Validator.isNull(issuerURI.getRawAuthority()) ||
			Validator.isNull(issuerURI.getScheme())) {

			return _generatePlaceholderOAuthASLocalWellKnownURI(
				oAuthClientASLocalMetadataId,
				"its issuer has no scheme or authority");
		}

		String oAuthASLocalWellKnownURI = StringBundler.concat(
			issuerURI.getScheme(), "://", issuerURI.getRawAuthority(),
			_OAUTH_AS_LOCAL_WELL_KNOWN_PATH, issuerURI.getRawPath());

		if (oAuthASLocalWellKnownURI.length() >
				_OAUTH_AS_LOCAL_WELL_KNOWN_URI_MAX_LENGTH) {

			return _generatePlaceholderOAuthASLocalWellKnownURI(
				oAuthClientASLocalMetadataId,
				"the generated URI is longer than " +
					_OAUTH_AS_LOCAL_WELL_KNOWN_URI_MAX_LENGTH + " characters");
		}

		return oAuthASLocalWellKnownURI;
	}

	private String _generateOAuthASMetadataJSON(
		String issuer, String metadataJSON, long oAuthClientASLocalMetadataId) {

		if (Validator.isNull(metadataJSON)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to generate OAuth 2 authorization server ",
						"metadata for OAuth 2 client authorization server ",
						"local metadata ", oAuthClientASLocalMetadataId,
						" because its metadata is null"));
			}

			return null;
		}

		try {
			AuthorizationServerMetadata authorizationServerMetadata =
				new AuthorizationServerMetadata(new Issuer(issuer));

			OIDCProviderMetadata oidcProviderMetadata =
				OIDCProviderMetadata.parse(metadataJSON);

			authorizationServerMetadata.setAuthorizationEndpointURI(
				oidcProviderMetadata.getAuthorizationEndpointURI());

			authorizationServerMetadata.setCodeChallengeMethods(
				Collections.singletonList(CodeChallengeMethod.S256));
			authorizationServerMetadata.setGrantTypes(
				oidcProviderMetadata.getGrantTypes());

			URI tokenEndpointURI = oidcProviderMetadata.getTokenEndpointURI();

			URI introspectionEndpointURI = _generateIntrospectionEndpointURI(
				tokenEndpointURI);

			if (introspectionEndpointURI != null) {
				authorizationServerMetadata.setIntrospectionEndpointURI(
					introspectionEndpointURI);
			}

			authorizationServerMetadata.setJWKSetURI(
				oidcProviderMetadata.getJWKSetURI());
			authorizationServerMetadata.setRegistrationEndpointURI(
				oidcProviderMetadata.getRegistrationEndpointURI());
			authorizationServerMetadata.setResponseTypes(
				Collections.singletonList(new ResponseType("code")));
			authorizationServerMetadata.setScopes(
				oidcProviderMetadata.getScopes());
			authorizationServerMetadata.setTokenEndpointAuthMethods(
				Arrays.asList(
					ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
					ClientAuthenticationMethod.CLIENT_SECRET_POST,
					ClientAuthenticationMethod.NONE));
			authorizationServerMetadata.setTokenEndpointURI(tokenEndpointURI);

			return String.valueOf(authorizationServerMetadata.toJSONObject());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to generate OAuth 2 authorization server ",
						"metadata for OAuth 2 client authorization server ",
						"local metadata ", oAuthClientASLocalMetadataId),
					exception);
			}

			return null;
		}
	}

	private String _generatePlaceholderOAuthASLocalWellKnownURI(
		long oAuthClientASLocalMetadataId, String reason) {

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Unable to generate an OAuth 2 authorization server local ",
					"well known URI for OAuth 2 client authorization server ",
					"local metadata ", oAuthClientASLocalMetadataId,
					" because ", reason,
					", so a placeholder is stored instead"));
		}

		return StringBundler.concat(
			_OAUTH_AS_LOCAL_WELL_KNOWN_PATH, StringPool.SLASH,
			oAuthClientASLocalMetadataId);
	}

	private String _removeTrailingSlash(String urlString) {
		if (!urlString.endsWith(StringPool.SLASH)) {
			return urlString;
		}

		return urlString.substring(0, urlString.length() - 1);
	}

	private static final String _OAUTH_AS_LOCAL_WELL_KNOWN_PATH =
		"/.well-known/oauth-authorization-server";

	private static final int _OAUTH_AS_LOCAL_WELL_KNOWN_URI_MAX_LENGTH = 256;

	private static final Log _log = LogFactoryUtil.getLog(
		OAuthClientASLocalMetadataUpgradeProcess.class);

}
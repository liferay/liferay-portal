/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action;

import com.liferay.oauth.client.admin.web.internal.constants.OAuthClientWebKeys;
import com.liferay.oauth.client.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.net.URI;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 * @author Alvaro Saugar
 */
@Component(
	property = {
		"jakarta.portlet.name=" + OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN,
		"mvc.command.name=/oauth_client_admin/update_oauth_client_as_local_metadata"
	},
	service = MVCRenderCommand.class
)
public class UpdateOAuthClientASLocalMetadataMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		try {
			_render(renderRequest);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
		catch (ParseException parseException) {
			throw new RuntimeException(parseException);
		}

		if (!FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-63415")) {

			return "/admin/update_oauth_client_as_local_metadata_old.jsp";
		}

		return "/admin/update_oauth_client_as_local_metadata.jsp";
	}

	private void _render(RenderRequest renderRequest)
		throws ParseException, PortalException {

		long oAuthClientASLocalMetadataId = ParamUtil.getLong(
			renderRequest, "oAuthClientASLocalMetadataId");

		if (!(oAuthClientASLocalMetadataId > 0)) {
			return;
		}

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataService.fetchOAuthClientASLocalMetadata(
				oAuthClientASLocalMetadataId);

		renderRequest.setAttribute(
			OAuthClientASLocalMetadata.class.getName(),
			oAuthClientASLocalMetadata);

		AuthorizationServerMetadata authorizationServerMetadata =
			AuthorizationServerMetadata.parse(
				oAuthClientASLocalMetadata.getOAuthASMetadataJSON());

		URI authorizationEndpointURI =
			authorizationServerMetadata.getAuthorizationEndpointURI();

		if (authorizationEndpointURI != null) {
			renderRequest.setAttribute(
				OAuthClientWebKeys.AUTHORIZATION_ENDPOINT,
				authorizationEndpointURI.toString());
		}

		if (authorizationServerMetadata.getGrantTypes() != null) {
			renderRequest.setAttribute(
				OAuthClientWebKeys.SUPPORTED_GRANT_TYPES,
				StringUtil.merge(authorizationServerMetadata.getGrantTypes()));
		}

		URI jwksURI = authorizationServerMetadata.getJWKSetURI();

		if (jwksURI != null) {
			renderRequest.setAttribute(
				OAuthClientWebKeys.JWKS_URI, jwksURI.toString());
		}

		URI registrationEndpointURI =
			authorizationServerMetadata.getRegistrationEndpointURI();

		if (registrationEndpointURI != null) {
			renderRequest.setAttribute(
				OAuthClientWebKeys.REGISTRATION_ENDPOINT,
				registrationEndpointURI.toString());
		}

		Scope supportedScopes = authorizationServerMetadata.getScopes();

		if (supportedScopes != null) {
			renderRequest.setAttribute(
				OAuthClientWebKeys.SUPPORTED_SCOPES,
				StringUtil.merge(supportedScopes));
		}

		OIDCProviderMetadata oidcProviderMetadata = OIDCProviderMetadata.parse(
			oAuthClientASLocalMetadata.getMetadataJSON());

		if (oidcProviderMetadata.getSubjectTypes() != null) {
			renderRequest.setAttribute(
				OAuthClientWebKeys.SUPPORTED_SUBJECT_TYPES,
				StringUtil.merge(oidcProviderMetadata.getSubjectTypes()));
		}

		URI tokenEndpointURI =
			authorizationServerMetadata.getTokenEndpointURI();

		if (tokenEndpointURI != null) {
			renderRequest.setAttribute(
				OAuthClientWebKeys.TOKEN_ENDPOINT, tokenEndpointURI.toString());
		}

		URI userInfoEndpointURI = oidcProviderMetadata.getUserInfoEndpointURI();

		if (userInfoEndpointURI != null) {
			renderRequest.setAttribute(
				OAuthClientWebKeys.USER_INFO_ENDPOINT,
				userInfoEndpointURI.toString());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateOAuthClientASLocalMetadataMVCRenderCommand.class);

	@Reference
	private OAuthClientASLocalMetadataService
		_oAuthClientASLocalMetadataService;

}
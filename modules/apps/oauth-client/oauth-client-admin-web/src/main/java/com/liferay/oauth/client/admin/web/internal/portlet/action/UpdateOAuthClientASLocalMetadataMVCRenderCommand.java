/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action;

import com.liferay.oauth.client.admin.web.internal.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
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
			String issuer = ParamUtil.getString(renderRequest, "issuer");

			if (Validator.isNotNull(issuer)) {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)renderRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
					_oAuthClientASLocalMetadataService.
						getIssuerAuthClientASLocalMetadata(
							themeDisplay.getCompanyId(), issuer);

				String metadataJSON =
					oAuthClientASLocalMetadata.getMetadataJSONOIC();

				OIDCProviderMetadata authorizationServerMetadata =
					OIDCProviderMetadata.parse(metadataJSON);

				renderRequest.setAttribute(
					OAuthClientASLocalMetadata.class.getName(),
					oAuthClientASLocalMetadata);

				if (authorizationServerMetadata.getAuthorizationEndpointURI() !=
						null) {

					renderRequest.setAttribute(
						"authorization_endpoint",
						authorizationServerMetadata.getAuthorizationEndpointURI(
						).toString());
				}

				if (authorizationServerMetadata.getJWKSetURI() != null) {
					renderRequest.setAttribute(
						"jwks_uri",
						authorizationServerMetadata.getJWKSetURI(
						).toString());
				}

				if (authorizationServerMetadata.getGrantTypes() != null) {
					renderRequest.setAttribute(
						"supported-grant-types",
						StringUtil.merge(
							authorizationServerMetadata.getGrantTypes()));
				}

				if (authorizationServerMetadata.getScopes() != null) {
					renderRequest.setAttribute(
						"supported-scopes",
						authorizationServerMetadata.getScopes(
						).toString());
				}

				if (authorizationServerMetadata.getSubjectTypes() != null) {
					renderRequest.setAttribute(
						"supported_subject_types",
						StringUtil.merge(
							authorizationServerMetadata.getSubjectTypes()));
				}

				if (authorizationServerMetadata.getTokenEndpointURI() != null) {
					renderRequest.setAttribute(
						"token_endpoint",
						authorizationServerMetadata.getTokenEndpointURI(
						).toString());
				}

				if (authorizationServerMetadata.getUserInfoEndpointURI() !=
						null) {

					renderRequest.setAttribute(
						"userinfo_endpoint",
						authorizationServerMetadata.getUserInfoEndpointURI(
						).toString());
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
		catch (ParseException parseException) {
			throw new RuntimeException(parseException);
		}

		return "/admin/update_oauth_client_as_local_metadata.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateOAuthClientASLocalMetadataMVCRenderCommand.class);

	@Reference
	private OAuthClientASLocalMetadataService
		_oAuthClientASLocalMetadataService;

}
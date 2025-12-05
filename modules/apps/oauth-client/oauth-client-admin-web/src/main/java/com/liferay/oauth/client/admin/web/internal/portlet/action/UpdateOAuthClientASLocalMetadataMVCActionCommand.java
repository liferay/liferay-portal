/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action;

import com.liferay.oauth.client.admin.web.internal.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

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
	service = MVCActionCommand.class
)
public class UpdateOAuthClientASLocalMetadataMVCActionCommand
	implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		try {
			Boolean enabledLocalWellKnownURIOAS = ParamUtil.getBoolean(
				actionRequest, "enabled");

			String authorizationEndpoint = ParamUtil.getString(
				actionRequest, "authorization_endpoint");

			String issuer = ParamUtil.getString(actionRequest, "issuer");

			String jwksUri = ParamUtil.getString(actionRequest, "jwks_uri");
			String supportedScopes = ParamUtil.getString(
				actionRequest, "supported-scopes");

			String supportedGrantTypes = ParamUtil.getString(
				actionRequest, "supported-grant-types");
			String supportedSubjectTypes = ParamUtil.getString(
				actionRequest, "supported_subject_types");
			String tokenEndpoint = ParamUtil.getString(
				actionRequest, "token_endpoint");
			String userInfoEndpoint = ParamUtil.getString(
				actionRequest, "userinfo_endpoint");

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataService.
					getIssuerAuthClientASLocalMetadata(
						themeDisplay.getCompanyId(), issuer);

			if (oAuthClientASLocalMetadata == null) {
				_oAuthClientASLocalMetadataService.
					addOAuthClientASLocalMetadata(
						themeDisplay.getUserId(), authorizationEndpoint,
						enabledLocalWellKnownURIOAS, issuer, jwksUri,
						StringUtil.split(supportedGrantTypes, StringPool.COMMA),
						StringUtil.split(supportedScopes, StringPool.COMMA),
						StringUtil.split(
							supportedSubjectTypes, StringPool.COMMA),
						tokenEndpoint, userInfoEndpoint);
			}
			else {
				_oAuthClientASLocalMetadataService.
					updateOAuthClientASLocalMetadata(
						oAuthClientASLocalMetadata.
							getOAuthClientASLocalMetadataId(),
						authorizationEndpoint, enabledLocalWellKnownURIOAS,
						issuer, jwksUri,
						StringUtil.split(supportedGrantTypes, StringPool.COMMA),
						StringUtil.split(supportedScopes, StringPool.COMMA),
						StringUtil.split(
							supportedSubjectTypes, StringPool.COMMA),
						tokenEndpoint, userInfoEndpoint);
			}

			return true;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			Class<?> clazz = portalException.getClass();

			SessionErrors.add(actionRequest, clazz.getName(), portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateOAuthClientASLocalMetadataMVCActionCommand.class);

	@Reference
	private OAuthClientASLocalMetadataService
		_oAuthClientASLocalMetadataService;

}
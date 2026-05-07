/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action;

import com.liferay.oauth.client.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientPRLocalMetadataService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN,
		"mvc.command.name=/oauth_client_admin/update_oauth_client_pr_local_metadata"
	},
	service = MVCRenderCommand.class
)
public class UpdateOAuthClientPRLocalMetadataMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		try {
			long oAuthClientPRLocalMetadataId = ParamUtil.getLong(
				renderRequest, "oAuthClientPRLocalMetadataId");

			if (oAuthClientPRLocalMetadataId > 0) {
				OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
					_oAuthClientPRLocalMetadataService.
						fetchOAuthClientPRLocalMetadata(
							oAuthClientPRLocalMetadataId);

				renderRequest.setAttribute(
					OAuthClientPRLocalMetadata.class.getName(),
					oAuthClientPRLocalMetadata);

				if (oAuthClientPRLocalMetadata != null) {
					_unpack(renderRequest, oAuthClientPRLocalMetadata);
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return "/admin/update_oauth_client_pr_local_metadata.jsp";
	}

	private void _unpack(
		RenderRequest renderRequest,
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		String metadataJSON = oAuthClientPRLocalMetadata.getMetadataJSON();

		if (Validator.isNull(metadataJSON)) {
			return;
		}

		try {
			JSONObject metadataJSONObject = _jsonFactory.createJSONObject(
				metadataJSON);

			renderRequest.setAttribute(
				"authorizationServers",
				StringUtil.merge(
					JSONUtil.toStringArray(
						metadataJSONObject.getJSONArray(
							"authorization_servers"))));
			renderRequest.setAttribute(
				"bearerMethodsSupported",
				StringUtil.merge(
					JSONUtil.toStringArray(
						metadataJSONObject.getJSONArray(
							"bearer_methods_supported"))));
			renderRequest.setAttribute(
				"resourceName", metadataJSONObject.getString("resource_name"));
			renderRequest.setAttribute(
				"scopesSupported",
				StringUtil.merge(
					JSONUtil.toStringArray(
						metadataJSONObject.getJSONArray("scopes_supported"))));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateOAuthClientPRLocalMetadataMVCRenderCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuthClientPRLocalMetadataService
		_oAuthClientPRLocalMetadataService;

}
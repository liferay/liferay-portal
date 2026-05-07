/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action;

import com.liferay.oauth.client.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientPRLocalMetadataService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

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
	service = MVCActionCommand.class
)
public class UpdateOAuthClientPRLocalMetadataMVCActionCommand
	implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		try {
			long oAuthClientPRLocalMetadataId = ParamUtil.getLong(
				actionRequest, "oAuthClientPRLocalMetadataId");

			String[] authorizationServers = StringUtil.split(
				ParamUtil.getString(actionRequest, "authorizationServers"),
				StringPool.COMMA);
			String[] bearerMethodsSupported = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "bearerMethodsSupported", "header"),
				StringPool.COMMA);
			boolean localWellKnownEnabled = ParamUtil.getBoolean(
				actionRequest, "localWellKnownEnabled");
			String resource = ParamUtil.getString(actionRequest, "resource");
			String resourceName = ParamUtil.getString(
				actionRequest, "resourceName");
			String[] scopesSupported = StringUtil.split(
				ParamUtil.getString(actionRequest, "scopesSupported"),
				StringPool.COMMA);

			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
				_oAuthClientPRLocalMetadataService.
					fetchOAuthClientPRLocalMetadata(
						oAuthClientPRLocalMetadataId);

			if (oAuthClientPRLocalMetadata == null) {
				_oAuthClientPRLocalMetadataService.
					addOAuthClientPRLocalMetadata(
						null, authorizationServers, bearerMethodsSupported,
						localWellKnownEnabled, resource, resourceName,
						scopesSupported);
			}
			else {
				_oAuthClientPRLocalMetadataService.
					updateOAuthClientPRLocalMetadata(
						oAuthClientPRLocalMetadata.
							getOAuthClientPRLocalMetadataId(),
						authorizationServers, bearerMethodsSupported,
						localWellKnownEnabled, resource, resourceName,
						scopesSupported);
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
		UpdateOAuthClientPRLocalMetadataMVCActionCommand.class);

	@Reference
	private OAuthClientPRLocalMetadataService
		_oAuthClientPRLocalMetadataService;

}
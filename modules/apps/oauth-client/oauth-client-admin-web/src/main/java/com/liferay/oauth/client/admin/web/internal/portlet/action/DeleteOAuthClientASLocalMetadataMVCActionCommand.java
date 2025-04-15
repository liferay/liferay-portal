/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action;

import com.liferay.oauth.client.admin.web.internal.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;

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
		"mvc.command.name=/oauth_client_admin/delete_oauth_client_as_local_metadata"
	},
	service = MVCActionCommand.class
)
public class DeleteOAuthClientASLocalMetadataMVCActionCommand
	implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		try {
			String localWellKnownURI = ParamUtil.getString(
				actionRequest, "localWellKnownURI");

			_oAuthClientASLocalMetadataService.deleteOAuthClientASLocalMetadata(
				localWellKnownURI);
		}
		catch (PortalException portalException) {
			if (_log.isInfoEnabled()) {
				_log.info(portalException);
			}

			SessionErrors.add(actionRequest, portalException.getClass());
		}

		actionResponse.setRenderParameter(
			"navigation", "oauth-client-as-local-metadata");

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteOAuthClientASLocalMetadataMVCActionCommand.class);

	@Reference
	private OAuthClientASLocalMetadataService
		_oAuthClientASLocalMetadataService;

}
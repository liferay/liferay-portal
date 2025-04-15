/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.connected.app.web.internal.portlet.action;

import com.liferay.connected.app.ConnectedApp;
import com.liferay.connected.app.ConnectedAppManager;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ACCOUNT,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/connected_app/revoke_connected_app"
	},
	service = MVCActionCommand.class
)
public class RevokeConnectedAppMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		User user = _portal.getSelectedUser(actionRequest);

		String connectedAppKey = ParamUtil.getString(
			actionRequest, "connectedAppKey");

		ConnectedApp connectedApp = _connectedAppManager.getConnectedApp(
			user, connectedAppKey);

		if (connectedApp != null) {
			connectedApp.revoke();
		}
	}

	@Reference
	private ConnectedAppManager _connectedAppManager;

	@Reference
	private Portal _portal;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.portlet.action;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.constants.UserAssociatedDataPortletKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author William Newbury
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UserAssociatedDataPortletKeys.USER_ASSOCIATED_DATA,
		"mvc.command.name=/user_associated_data/delete_user"
	},
	service = MVCActionCommand.class
)
public class DeleteUserMVCActionCommand extends BaseUADMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_userLocalService.deleteUser(getSelectedUserId(actionRequest));

		MultiSessionMessages.add(
			actionRequest, "requestProcessed",
			_language.get(
				_portalImpl.getHttpServletRequest(actionRequest),
				"user-successfully-deleted"));

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			actionRequest, UsersAdminPortletKeys.USERS_ADMIN,
			PortletRequest.RENDER_PHASE);

		sendRedirect(
			actionRequest, actionResponse, liferayPortletURL.toString());
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portalImpl;

	@Reference
	private UserLocalService _userLocalService;

}
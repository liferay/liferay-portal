/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.portlet.action;

import com.liferay.account.admin.web.internal.display.context.InvitedAccountUserDisplayContext;
import com.liferay.account.admin.web.internal.portlet.action.util.TicketUtil;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AccountPortletKeys.ACCOUNT_USERS_REGISTRATION,
		"mvc.command.name=/account_admin/create_account_user",
		"portlet.add.default.resource.check.whitelist.mvc.action=true"
	},
	service = MVCRenderCommand.class
)
public class CreateAccountUserMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (themeDisplay.isSignedIn()) {
			SessionErrors.add(renderRequest, PrincipalException.class);

			return "/account_user_registration/error.jsp";
		}

		Ticket ticket = TicketUtil.getTicket(
			renderRequest, _ticketLocalService);

		if (ticket == null) {
			SessionErrors.add(renderRequest, NoSuchTicketException.class);

			return "/account_user_registration/error.jsp";
		}

		InvitedAccountUserDisplayContext invitedAccountUserDisplayContext =
			new InvitedAccountUserDisplayContext();

		invitedAccountUserDisplayContext.setTicketKey(ticket.getKey());

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				ticket.getExtraInfo());

			invitedAccountUserDisplayContext.setEmailAddress(
				jsonObject.getString("emailAddress"));
		}
		catch (JSONException jsonException) {
			throw new PortletException(jsonException);
		}

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, invitedAccountUserDisplayContext);

		return "/account_user_registration/create_account_user.jsp";
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private TicketLocalService _ticketLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.portlet.action;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.dsr.site.initializer.constants.DSRPortletKeys;
import com.liferay.site.dsr.site.initializer.constants.DSRTicketConstants;
import com.liferay.site.dsr.site.initializer.internal.display.context.InviteMemberDisplayContext;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DSRPortletKeys.DSR_INVITE_MEMBER,
		"mvc.command.name=/dsr/invite_member",
		"portlet.add.default.resource.check.whitelist.mvc.action=true"
	},
	service = MVCRenderCommand.class
)
public class InviteMemberMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			Ticket ticket = _getTicket(renderRequest);

			if (ticket == null) {
				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);
				Group group = themeDisplay.getSiteGroup();

				httpServletResponse.sendRedirect(
					PortletURLBuilder.create(
						PortletURLFactoryUtil.create(
							_portal.getHttpServletRequest(renderRequest),
							LoginPortletKeys.LOGIN,
							PortalUtil.getPlidFromPortletId(
								group.getGroupId(), LoginPortletKeys.LOGIN),
							PortletRequest.RENDER_PHASE)
					).setRedirect(
						group.getDisplayURL(themeDisplay) + "/onboarding"
					).buildString());

				return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
			}

			if (themeDisplay.isSignedIn()) {
				HttpServletResponse httpServletResponse =
					_portal.getHttpServletResponse(renderResponse);
				Group group = _groupService.getGroup(ticket.getClassPK());

				httpServletResponse.sendRedirect(
					group.getDisplayURL(themeDisplay));

				return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
			}

			JSONObject jsonObject = _jsonFactory.createJSONObject(
				ticket.getExtraInfo());

			String emailAddress = jsonObject.getString("emailAddress");

			User user = _userLocalService.fetchUserByEmailAddress(
				ticket.getCompanyId(), emailAddress);

			if (user == null) {
				InviteMemberDisplayContext
					inviteDigitalSalesRoomRoomUserDisplayContext =
						new InviteMemberDisplayContext(
							emailAddress,
							_groupLocalService.getGroup(ticket.getClassPK()),
							_portal.getHttpServletRequest(renderRequest),
							ticket.getClassPK(), themeDisplay, ticket.getKey());

				renderRequest.setAttribute(
					WebKeys.PORTLET_DISPLAY_CONTEXT,
					inviteDigitalSalesRoomRoomUserDisplayContext);

				return "/invite_member.jsp";
			}

			Group group = _groupLocalService.getGroup(ticket.getClassPK());
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			httpServletResponse.sendRedirect(
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						_portal.getHttpServletRequest(renderRequest),
						LoginPortletKeys.LOGIN,
						PortalUtil.getPlidFromPortletId(
							group.getGroupId(), LoginPortletKeys.LOGIN),
						PortletRequest.RENDER_PHASE)
				).setRedirect(
					group.getDisplayURL(themeDisplay) + "/onboarding"
				).buildString());

			return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private Ticket _getTicket(RenderRequest renderRequest) {
		String ticketKey = ParamUtil.getString(renderRequest, "ticketKey");

		if (Validator.isNull(ticketKey)) {
			return null;
		}

		Ticket ticket = _ticketLocalService.fetchTicket(ticketKey);

		if ((ticket == null) ||
			(ticket.getType() != DSRTicketConstants.TYPE_INVITE_MEMBER)) {

			return null;
		}

		if (!ticket.isExpired()) {
			return ticket;
		}

		_ticketLocalService.deleteTicket(ticket);

		return null;
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchTicketException;
import com.liferay.portal.kernel.exception.RoleAssignmentException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.site.dsr.site.initializer.constants.DSRPortletKeys;
import com.liferay.site.dsr.site.initializer.constants.DSRTicketConstants;
import com.liferay.site.dsr.site.initializer.internal.display.context.InviteMemberDisplayContext;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DSRPortletKeys.DSR_INVITE_MEMBER,
		"mvc.command.name=/dsr/invite_member"
	},
	service = MVCActionCommand.class
)
public class InviteMemberMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		Ticket ticket = _getTicket(actionRequest);

		if (ticket == null) {
			SessionErrors.add(actionRequest, NoSuchTicketException.class);

			actionResponse.setRenderParameter("mvcPath", "/error.jsp");

			return;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			ticket.getExtraInfo());

		InviteMemberDisplayContext
			inviteDigitalSalesRoomRoomUserDisplayContext =
				new InviteMemberDisplayContext(
					jsonObject.getString("emailAddress"),
					_groupLocalService.getGroup(ticket.getClassPK()),
					_portal.getHttpServletRequest(actionRequest),
					ticket.getClassPK(),
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY),
					ticket.getKey());

		actionRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			inviteDigitalSalesRoomRoomUserDisplayContext);

		User user = _addUser(
			actionRequest, ticket.getClassPK(),
			jsonObject.getString("emailAddress"),
			jsonObject.getString("roleKey"));

		if (user.getStatus() == WorkflowConstants.STATUS_APPROVED) {
			SessionMessages.add(
				_portal.getHttpServletRequest(actionRequest), "userAdded",
				user.getEmailAddress());
		}
		else {
			SessionMessages.add(
				_portal.getHttpServletRequest(actionRequest), "userPending",
				user.getEmailAddress());
		}

		_ticketLocalService.deleteTicket(ticket);

		sendRedirect(actionRequest, actionResponse);
	}

	private User _addUser(
			ActionRequest actionRequest, long digitalSalesRoomId,
			String emailAddress, String roleKey)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		boolean autoPassword = true;
		String password1 = null;
		String password2 = null;

		if (PrefsPropsUtil.getBoolean(
				themeDisplay.getCompanyId(),
				PropsKeys.LOGIN_CREATE_ACCOUNT_ALLOW_CUSTOM_PASSWORD,
				PropsValues.LOGIN_CREATE_ACCOUNT_ALLOW_CUSTOM_PASSWORD)) {

			autoPassword = false;

			password1 = ParamUtil.getString(actionRequest, "password1");
			password2 = ParamUtil.getString(actionRequest, "password2");
		}

		User user = _userService.addUser(
			themeDisplay.getCompanyId(), autoPassword, password1, password2,
			false, ParamUtil.getString(actionRequest, "screenName"),
			emailAddress,
			LocaleUtil.fromLanguageId(
				ParamUtil.getString(actionRequest, "languageId")),
			ParamUtil.getString(actionRequest, "firstName"),
			ParamUtil.getString(actionRequest, "middleName"),
			ParamUtil.getString(actionRequest, "lastName"),
			ParamUtil.getLong(actionRequest, "prefixListTypeId"),
			ParamUtil.getLong(actionRequest, "suffixListTypeId"), true, 0, 1,
			1970, ParamUtil.getString(actionRequest, "jobTitle"), null, null,
			null, null, false,
			ServiceContextFactory.getInstance(
				User.class.getName(), actionRequest));

		_userLocalService.addGroupUser(digitalSalesRoomId, user.getUserId());

		if (Validator.isNotNull(roleKey)) {
			Role role = _roleLocalService.fetchRole(
				themeDisplay.getCompanyId(), roleKey);

			if (role.getType() != RoleConstants.TYPE_SITE) {
				throw new RoleAssignmentException(
					StringBundler.concat(
						"Role type ",
						RoleConstants.getTypeLabel(role.getType()), " is not ",
						RoleConstants.getTypeLabel(RoleConstants.TYPE_SITE)));
			}

			_userGroupRoleLocalService.addUserGroupRoles(
				user.getUserId(), digitalSalesRoomId,
				new long[] {role.getRoleId()});
		}

		LiveUsers.joinGroup(
			themeDisplay.getCompanyId(), digitalSalesRoomId, user.getUserId());

		return user;
	}

	private Ticket _getTicket(ActionRequest actionRequest) {
		String ticketKey = ParamUtil.getString(actionRequest, "ticketKey");

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
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private TicketLocalService _ticketLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

}
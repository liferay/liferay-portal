/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.ContactNameException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.RequiredRoleException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.membershippolicy.MembershipPolicyException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portlet.usersadmin.util.UsersAdminUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ACCOUNT,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.SERVICE_ACCOUNTS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/update_user_roles"
	},
	service = MVCActionCommand.class
)
public class UpdateUserRolesMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			User user = _portal.getSelectedUser(actionRequest);

			Contact contact = user.getContact();

			Calendar birthdayCal = CalendarFactoryUtil.getCalendar();

			birthdayCal.setTime(user.getBirthday());

			long[] roleIds = UsersAdminUtil.getRoleIds(actionRequest);

			_validate(user, roleIds);

			List<UserGroupRole> userGroupRoles = null;

			String addGroupRolesGroupIds = actionRequest.getParameter(
				"addGroupRolesGroupIds");
			String addGroupRolesRoleIds = actionRequest.getParameter(
				"addGroupRolesRoleIds");
			String deleteGroupRolesGroupIds = actionRequest.getParameter(
				"deleteGroupRolesGroupIds");
			String deleteGroupRolesRoleIds = actionRequest.getParameter(
				"deleteGroupRolesRoleIds");

			if ((addGroupRolesGroupIds != null) ||
				(addGroupRolesRoleIds != null) ||
				(deleteGroupRolesGroupIds != null) ||
				(deleteGroupRolesRoleIds != null)) {

				userGroupRoles = UsersAdminUtil.getUserGroupRoles(
					actionRequest);
			}

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				User.class.getName(), actionRequest);

			serviceContext.setAssetCategoryIds(null);
			serviceContext.setAssetTagNames(null);

			user = _userService.updateUser(
				user.getUserId(), user.getPassword(), null, null,
				user.isPasswordReset(), null, null, user.getScreenName(),
				user.getEmailAddress(), user.getLanguageId(),
				user.getTimeZoneId(), user.getGreeting(), user.getComments(),
				user.getFirstName(), user.getMiddleName(), user.getLastName(),
				contact.getPrefixListTypeId(), contact.getSuffixListTypeId(),
				user.isMale(), birthdayCal.get(Calendar.MONTH),
				birthdayCal.get(Calendar.DATE), birthdayCal.get(Calendar.YEAR),
				contact.getSmsSn(), contact.getFacebookSn(),
				contact.getJabberSn(), contact.getSkypeSn(),
				contact.getTwitterSn(), user.getJobTitle(), null,
				user.getOrganizationIds(), roleIds, userGroupRoles,
				user.getUserGroupIds(), serviceContext);

			User currentUser = _userService.getCurrentUser();

			if (currentUser.getUserId() == user.getUserId()) {
				String redirect = _getRedirect(actionRequest, currentUser);

				if (Validator.isNotNull(redirect)) {
					sendRedirect(actionRequest, actionResponse, redirect);
				}
			}
		}
		catch (Exception exception) {
			if (exception instanceof ContactNameException ||
				exception instanceof NoSuchUserException ||
				exception instanceof PrincipalException ||
				exception instanceof
					RequiredRoleException.MustNotRemoveLastAdministator ||
				exception instanceof UserEmailAddressException ||
				exception instanceof UserScreenNameException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");
			}
			else if (exception instanceof MembershipPolicyException) {
				SessionErrors.add(
					actionRequest, exception.getClass(), exception);

				actionResponse.setRenderParameter("mvcPath", "/edit_user.jsp");
			}
			else {
				throw exception;
			}
		}
	}

	private String _getRedirect(ActionRequest actionRequest, User currentUser)
		throws Exception {

		PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
			JavaConstants.JAKARTA_PORTLET_CONFIG);

		String portletName = portletConfig.getPortletName();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(currentUser);

		if (!PortletPermissionUtil.contains(
				permissionChecker, portletName,
				ActionKeys.ACCESS_IN_CONTROL_PANEL)) {

			return _portal.getHomeURL(
				_portal.getHttpServletRequest(actionRequest));
		}

		if (portletName.equals(UsersAdminPortletKeys.MY_ORGANIZATIONS)) {
			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(actionRequest);

			String backURL = null;
			long organizationId = 0;
			String portletNamespace = _portal.getPortletNamespace(
				UsersAdminPortletKeys.MY_ORGANIZATIONS);
			String redirect = ParamUtil.getString(
				httpServletRequest, "redirect");

			if (Validator.isNotNull(redirect)) {
				Map<String, String[]> parameterMap =
					HttpComponentsUtil.getParameterMap(redirect);

				backURL = parameterMap.get(portletNamespace + "backURL")[0];
			}

			if (Validator.isNotNull(backURL)) {
				Map<String, String[]> parameterMap =
					HttpComponentsUtil.getParameterMap(backURL);

				organizationId = GetterUtil.getLong(
					parameterMap.get(portletNamespace + "organizationId")[0]);
			}

			if ((organizationId > 0) &&
				!OrganizationPermissionUtil.contains(
					permissionChecker, organizationId, ActionKeys.VIEW)) {

				PortletURL portletURL = _portal.getControlPanelPortletURL(
					httpServletRequest, portletName,
					PortletRequest.RENDER_PHASE);

				return portletURL.toString();
			}
		}

		return StringPool.BLANK;
	}

	private void _validate(User user, long[] roleIds) throws Exception {

		// This is a unique case where we should throw an exception in the
		// portlet action. The service implementation already guards against
		// removing the last administrator, but it does so by quietly readding
		// the administrator role to the roleIds array. We're already safe in
		// regards to data integrity. However, the goal is to provide the user
		// feedback as to why the administrator role was not removed. Putting
		// this check in UserServiceImpl is useless because UsersAdmin readds
		// the role.

		Role administratorRole = _roleLocalService.getRole(
			user.getCompanyId(), RoleConstants.ADMINISTRATOR);

		long[] administratorUserIds = _userLocalService.getRoleUserIds(
			administratorRole.getRoleId());

		if ((administratorUserIds.length == 1) &&
			ArrayUtil.contains(administratorUserIds, user.getUserId()) &&
			!ArrayUtil.contains(roleIds, administratorRole.getRoleId())) {

			throw new RequiredRoleException.MustNotRemoveLastAdministator();
		}
	}

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

}
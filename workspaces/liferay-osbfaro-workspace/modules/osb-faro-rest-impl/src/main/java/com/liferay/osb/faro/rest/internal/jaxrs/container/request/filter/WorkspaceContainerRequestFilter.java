/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.request.filter;

import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.annotation.Priority;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Nilton Vieira
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Faro.Rest)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Faro.Rest.Workspace.Container.Request.Filter"
	},
	scope = ServiceScope.PROTOTYPE, service = ContainerRequestFilter.class
)
@Priority(Priorities.AUTHORIZATION)
public class WorkspaceContainerRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		UriInfo uriInfo = containerRequestContext.getUriInfo();

		MultivaluedMap<String, String> pathParameters =
			uriInfo.getPathParameters();

		String groupIdString = pathParameters.getFirst("groupId");

		if (Validator.isNull(groupIdString)) {
			return;
		}

		long groupId = GetterUtil.getLong(groupIdString);

		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(groupId);

		if (faroProject == null) {
			_abort(
				containerRequestContext, Response.Status.NOT_FOUND,
				StringBundler.concat(
					"No workspace was found with group ID ", groupIdString,
					". Call getWorkspacesPage to list the workspaces you can ",
					"access."));

			return;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) || _isGuestUser(permissionChecker)) {
			_abort(
				containerRequestContext, Response.Status.UNAUTHORIZED,
				"You are not authenticated");

			return;
		}

		if (!permissionChecker.isOmniadmin()) {
			if (!faroProject.isAllowedIPAddress(
					_httpServletRequest.getRemoteAddr())) {

				_abort(
					containerRequestContext, Response.Status.FORBIDDEN,
					"Your IP address is not authorized to access this " +
						"workspace");

				return;
			}

			if (!_isWorkspaceMember(groupId, permissionChecker.getUserId())) {
				_abort(
					containerRequestContext, Response.Status.FORBIDDEN,
					StringBundler.concat(
						"You do not have access to the workspace with group ",
						"ID ", groupIdString, ". Call getWorkspacesPage to ",
						"list the workspaces you can access."));

				return;
			}
		}

		String channelId = pathParameters.getFirst("channelId");

		if (Validator.isNull(channelId)) {
			MultivaluedMap<String, String> queryParameters =
				uriInfo.getQueryParameters();

			channelId = queryParameters.getFirst("channelId");
		}

		if (Validator.isNull(channelId)) {
			return;
		}

		FaroChannel faroChannel = _faroChannelLocalService.fetchFaroChannel(
			channelId, groupId);

		if (faroChannel == null) {
			_abort(
				containerRequestContext, Response.Status.NOT_FOUND,
				StringBundler.concat(
					"No property was found with channel ID ", channelId,
					" in the workspace with group ID ", groupIdString,
					". Call getWorkspaceGroupChannelsPage to list the ",
					"properties you can access."));

			return;
		}

		if (!_hasChannelPermission(faroChannel, permissionChecker)) {
			_abort(
				containerRequestContext, Response.Status.FORBIDDEN,
				StringBundler.concat(
					"You do not have access to the property with channel ID ",
					channelId, ". Call getWorkspaceGroupChannelsPage to list ",
					"the properties you can access."));
		}
	}

	private void _abort(
		ContainerRequestContext containerRequestContext, Response.Status status,
		String title) {

		containerRequestContext.abortWith(
			Response.status(
				status
			).entity(
				new Problem(status, title)
			).type(
				MediaType.APPLICATION_JSON
			).build());
	}

	private boolean _hasChannelPermission(
		FaroChannel faroChannel, PermissionChecker permissionChecker) {

		if (faroChannel.getPermissionType() ==
				FaroChannelConstants.PERMISSION_ALL_USERS) {

			return true;
		}

		return ListUtil.isNotEmpty(
			_userGroupRoleLocalService.getUserGroupRoles(
				permissionChecker.getUserId(), faroChannel.getGroupId()));
	}

	private boolean _isGuestUser(PermissionChecker permissionChecker) {
		User user = permissionChecker.getUser();

		if ((user == null) || user.isGuestUser()) {
			return true;
		}

		return false;
	}

	private boolean _isWorkspaceMember(long groupId, long userId) {
		FaroUser faroUser = _faroUserLocalService.fetchFaroUser(
			groupId, userId);

		if ((faroUser == null) ||
			(faroUser.getStatus() != FaroUserConstants.STATUS_APPROVED)) {

			return false;
		}

		Role role = _roleLocalService.fetchRole(faroUser.getRoleId());

		if (role == null) {
			return false;
		}

		return ArrayUtil.contains(_WORKSPACE_ROLE_NAMES, role.getName());
	}

	private static final String[] _WORKSPACE_ROLE_NAMES = {
		RoleConstants.SITE_ADMINISTRATOR, RoleConstants.SITE_MEMBER,
		RoleConstants.SITE_OWNER
	};

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private FaroUserLocalService _faroUserLocalService;

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}
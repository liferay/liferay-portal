/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.request.filter;

import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.engine.client.model.ErrorResponse;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroProjectLocalServiceUtil;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.osb.faro.web.internal.annotations.TokenAuthentication;
import com.liferay.osb.faro.web.internal.annotations.Unauthenticated;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

/**
 * @author Matthew Kong
 */
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		Method method = _resourceInfo.getResourceMethod();

		if (method.isAnnotationPresent(TokenAuthentication.class) ||
			method.isAnnotationPresent(Unauthenticated.class)) {

			return;
		}

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		User user = permissionChecker.getUser();

		if (user.isDefaultUser()) {
			containerRequestContext.abortWith(
				_getResponse(
					Response.Status.UNAUTHORIZED, "You are not authenticated"));

			return;
		}

		FaroUserLocalServiceUtil.acceptInvitations(user.getUserId(), null);

		if (permissionChecker.isOmniadmin()) {
			return;
		}

		long groupId = _getGroupId(containerRequestContext.getUriInfo());

		if (groupId > 0) {
			FaroProject faroProject =
				FaroProjectLocalServiceUtil.fetchFaroProjectByGroupId(groupId);

			if ((faroProject != null) &&
				!faroProject.isAllowedIPAddress(
					_httpServletRequest.getRemoteAddr())) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						String.format(
							"The IP %s is not authorized to access this " +
								"resource",
							_httpServletRequest.getRemoteAddr()));
				}

				containerRequestContext.abortWith(
					_getResponse(
						Response.Status.FORBIDDEN,
						"Your IP address is not authorized to access this " +
							"resource"));

				return;
			}
		}

		RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);

		if ((rolesAllowed == null) ||
			_hasPermission(groupId, rolesAllowed.value(), user.getUserId())) {

			return;
		}

		containerRequestContext.abortWith(
			_getResponse(
				Response.Status.FORBIDDEN,
				"You do not have the required permissions"));
	}

	private long _getGroupId(UriInfo uriInfo) {
		MultivaluedMap<String, String> params = uriInfo.getPathParameters();

		String groupKey = params.getFirst("groupId");

		Group group = GroupLocalServiceUtil.fetchFriendlyURLGroup(
			PortalUtil.getDefaultCompanyId(), StringPool.SLASH + groupKey);

		if (group != null) {
			return group.getGroupId();
		}

		return GetterUtil.getLong(groupKey);
	}

	private Response _getResponse(
		Response.StatusType statusType, String message) {

		Response.ResponseBuilder responseBuilder = Response.status(statusType);

		responseBuilder.type(MediaType.APPLICATION_JSON_TYPE);

		ErrorResponse errorResponse = new ErrorResponse();

		errorResponse.setError(statusType.getReasonPhrase());
		errorResponse.setStatus(statusType.getStatusCode());
		errorResponse.setMessage(message);

		responseBuilder.entity(errorResponse);

		return responseBuilder.build();
	}

	private boolean _hasPermission(
		long groupId, String[] allowedRoleNames, long userId) {

		FaroUser faroUser = FaroUserLocalServiceUtil.fetchFaroUser(
			groupId, userId);

		if ((faroUser == null) ||
			(faroUser.getStatus() != FaroUserConstants.STATUS_APPROVED)) {

			return false;
		}

		Role role = RoleLocalServiceUtil.fetchRole(faroUser.getRoleId());

		if (role == null) {
			return false;
		}

		for (String allowedRoleName : allowedRoleNames) {
			if ((allowedRoleName.equals(RoleConstants.SITE_ADMINISTRATOR) &&
				 _isSiteAdministrator(role.getName())) ||
				(allowedRoleName.equals(RoleConstants.SITE_MEMBER) &&
				 _isSiteMember(role.getName())) ||
				(allowedRoleName.equals(RoleConstants.SITE_OWNER) &&
				 _isSiteOwner(role.getName()))) {

				return true;
			}
		}

		return false;
	}

	private boolean _isSiteAdministrator(String roleName) {
		if (_isSiteOwner(roleName) ||
			StringUtil.equals(roleName, RoleConstants.SITE_ADMINISTRATOR)) {

			return true;
		}

		return false;
	}

	private boolean _isSiteMember(String roleName) {
		if (_isSiteAdministrator(roleName) || _isSiteOwner(roleName) ||
			StringUtil.equals(roleName, RoleConstants.SITE_MEMBER)) {

			return true;
		}

		return false;
	}

	private boolean _isSiteOwner(String roleName) {
		return StringUtil.equals(roleName, RoleConstants.SITE_OWNER);
	}

	private static final Log _log = LogFactoryUtil.getLog(SecurityFilter.class);

	@Context
	private HttpServletRequest _httpServletRequest;

	@Context
	private ResourceInfo _resourceInfo;

}
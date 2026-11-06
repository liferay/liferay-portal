/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.request.filter;

import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.internal.jaxrs.util.ProblemUtil;
import com.liferay.osb.faro.rest.internal.security.permission.WorkspacePermission;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.annotation.Priority;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
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

		String groupId = pathParameters.getFirst("groupId");

		if (Validator.isNull(groupId)) {
			return;
		}

		FaroProject faroProject =
			_faroProjectLocalService.fetchFaroProjectByGroupId(
				GetterUtil.getLong(groupId));

		if (faroProject == null) {
			_abort(
				containerRequestContext, Response.Status.NOT_FOUND,
				"No workspace was found with group ID " + groupId);

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
					"IP address not allowed to access the workspace");

				return;
			}

			if (!_workspacePermission.isWorkspaceMember(
					faroProject.getGroupId(), permissionChecker)) {

				_abort(
					containerRequestContext, Response.Status.FORBIDDEN,
					StringBundler.concat(
						"Unable to access the workspace with group ID ",
						groupId, ". Call \"getWorkspacesPage\" to list the ",
						"workspaces you can use."));

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
			channelId, faroProject.getGroupId());

		if (faroChannel == null) {
			_abort(
				containerRequestContext, Response.Status.NOT_FOUND,
				"No property was found with channel ID " + channelId +
					" in the workspace with group ID " + groupId);

			return;
		}

		if (!_workspacePermission.hasChannelPermission(
				faroChannel, permissionChecker)) {

			_abort(
				containerRequestContext, Response.Status.FORBIDDEN,
				StringBundler.concat(
					"Unable to access the property with channel ID ", channelId,
					". Call \"getWorkspaceGroupChannelsPage\" to list the ",
					"properties you can use."));
		}
	}

	private void _abort(
		ContainerRequestContext containerRequestContext, Response.Status status,
		String title) {

		containerRequestContext.abortWith(
			ProblemUtil.toResponse(status, title));
	}

	private boolean _isGuestUser(PermissionChecker permissionChecker) {
		User user = permissionChecker.getUser();

		if ((user == null) || user.isGuestUser()) {
			return true;
		}

		return false;
	}

	@Reference
	private FaroChannelLocalService _faroChannelLocalService;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private WorkspacePermission _workspacePermission;

}
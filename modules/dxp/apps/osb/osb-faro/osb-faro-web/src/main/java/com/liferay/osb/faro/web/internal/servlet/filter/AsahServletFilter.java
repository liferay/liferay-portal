/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.servlet.filter;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.util.FaroPermissionChecker;
import com.liferay.osb.faro.web.internal.util.FaroProjectThreadLocal;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"osgi.http.whiteboard.filter.name=com.liferay.osb.faro.web.internal.servlet.filter.AsahServletFilter",
		"osgi.http.whiteboard.filter.pattern=/cerebro/graphql/*",
		"osgi.http.whiteboard.filter.pattern=/proxy/download/*"
	},
	service = Filter.class
)
public class AsahServletFilter extends BaseFilter {

	protected FaroProject fetchFaroProject(String groupKey) {
		long groupId = 0;

		Group group = _groupLocalService.fetchFriendlyURLGroup(
			_portal.getDefaultCompanyId(), StringPool.SLASH + groupKey);

		if (group == null) {
			groupId = GetterUtil.getLong(groupKey);
		}
		else {
			groupId = group.getGroupId();
		}

		return _faroProjectLocalService.fetchFaroProjectByGroupId(groupId);
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	protected PermissionChecker getPermissionChecker(User user)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker != null) {
			return permissionChecker;
		}

		permissionChecker = PermissionCheckerFactoryUtil.create(user);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		return permissionChecker;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		User user = _portal.getUser(httpServletRequest);

		if ((user == null) || user.isDefaultUser()) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unauthenticated access attempt with remote address " +
						httpServletRequest.getRemoteAddr());
			}

			httpServletResponse.sendError(
				HttpServletResponse.SC_UNAUTHORIZED,
				"You are not authenticated");

			return;
		}

		String projectGroupId = ParamUtil.getString(
			httpServletRequest, "projectGroupId");

		FaroProject faroProject = fetchFaroProject(projectGroupId);

		if (faroProject == null) {
			_log.error(
				"Unable to retrieve faro project for group ID " +
					projectGroupId);

			httpServletResponse.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				"Unable to fetch referred Analytics Project information");

			return;
		}

		PermissionChecker permissionChecker = getPermissionChecker(user);

		if (permissionChecker.isOmniadmin()) {
			_processFilter(
				httpServletRequest, httpServletResponse, filterChain, user,
				faroProject);

			return;
		}

		if (!faroProject.isAllowedIPAddress(
				httpServletRequest.getRemoteHost())) {

			httpServletResponse.sendError(
				HttpServletResponse.SC_FORBIDDEN,
				"Your IP address is not authorized to access this resource");

			return;
		}

		if (FaroPermissionChecker.isGroupMember(
				faroProject.getGroupId(), permissionChecker)) {

			_processFilter(
				httpServletRequest, httpServletResponse, filterChain, user,
				faroProject);

			return;
		}

		httpServletResponse.sendError(
			HttpServletResponse.SC_FORBIDDEN,
			"You do not have the required permissions");
	}

	private void _processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain,
			User user, FaroProject faroProject)
		throws Exception {

		FaroProjectThreadLocal.setFaroProject(faroProject);

		PrincipalThreadLocal.setName(user.getUserId());

		// Process filter

		Class<?> clazz = getClass();

		processFilter(
			clazz.getName(), httpServletRequest, httpServletResponse,
			filterChain);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AsahServletFilter.class);

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}
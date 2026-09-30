/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.servlet.filter;

import com.liferay.digital.sales.room.util.DSRRoomUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matyas Wollner
 */
@Component(
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=DSR Archived Room Filter", "url-pattern=/c/*",
		"url-pattern=/group/*", "url-pattern=/web/*"
	},
	service = Filter.class
)
public class ArchivedRoomFilter extends BaseFilter {

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		Group group = _fetchGroup(httpServletRequest);

		if ((group != null) && !group.isActive() &&
			_groupLocalService.isMaintenanceMode(group) &&
			DSRRoomUtil.isArchived(group)) {

			PermissionChecker permissionChecker = _getPermissionChecker(
				httpServletRequest);

			if ((permissionChecker != null) &&
				!permissionChecker.isGroupAdmin(group.getGroupId())) {

				httpServletResponse.sendRedirect(
					_portal.getPortalURL(httpServletRequest));

				return;
			}
		}

		processFilter(
			ArchivedRoomFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);
	}

	private Group _fetchGroup(HttpServletRequest httpServletRequest) {
		long plid = ParamUtil.getLong(httpServletRequest, "p_l_id");

		if (plid > 0) {
			Layout layout = _layoutLocalService.fetchLayout(plid);

			if (layout == null) {
				return null;
			}

			return _groupLocalService.fetchGroup(layout.getGroupId());
		}

		String requestURI = httpServletRequest.getRequestURI();

		String contextPath = httpServletRequest.getContextPath();

		if (Validator.isNotNull(contextPath) &&
			requestURI.startsWith(contextPath)) {

			requestURI = requestURI.substring(contextPath.length());
		}

		int[] groupFriendlyURLIndex = _portal.getGroupFriendlyURLIndex(
			requestURI);

		if (groupFriendlyURLIndex == null) {
			return null;
		}

		return _groupLocalService.fetchFriendlyURLGroup(
			CompanyThreadLocal.getCompanyId(),
			requestURI.substring(
				groupFriendlyURLIndex[0], groupFriendlyURLIndex[1]));
	}

	private PermissionChecker _getPermissionChecker(
			HttpServletRequest httpServletRequest)
		throws Exception {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker != null) {
			return permissionChecker;
		}

		User user = _portal.getUser(httpServletRequest);

		if (user == null) {
			user = _userLocalService.fetchGuestUser(
				CompanyThreadLocal.getCompanyId());
		}

		if (user == null) {
			return null;
		}

		return _permissionCheckerFactory.create(user);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ArchivedRoomFilter.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}
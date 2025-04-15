/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.web.internal.role.type.contributor.util.RoleTypeContributorRetrieverUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

/**
 * @author Drew Brokke
 */
public class RoleChecker extends EmptyOnClickRowChecker {

	public RoleChecker(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		super(portletResponse);

		_portletRequest = portletRequest;
	}

	@Override
	public boolean isDisabled(Object object) {
		Role role = (Role)object;

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			if (role.isSystem() ||
				!RolePermissionUtil.contains(
					permissionChecker, role.getRoleId(), ActionKeys.DELETE)) {

				return true;
			}

			RoleTypeContributor roleTypeContributor =
				RoleTypeContributorRetrieverUtil.getCurrentRoleTypeContributor(
					_portletRequest);

			if ((roleTypeContributor != null) &&
				(!roleTypeContributor.isAllowDelete(role) ||
				 roleTypeContributor.isAutomaticallyAssigned(role))) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(RoleChecker.class);

	private final PortletRequest _portletRequest;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.search;

import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.membershippolicy.OrganizationMembershipPolicyUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Roberto Díaz
 */
public class OrganizationRoleUserChecker extends RowChecker {

	public OrganizationRoleUserChecker(
		RenderResponse renderResponse, Organization organization, Role role) {

		super(renderResponse);

		_organization = organization;
		_role = role;
	}

	@Override
	public boolean isChecked(Object object) {
		User user = (User)object;

		try {
			return UserGroupRoleLocalServiceUtil.hasUserGroupRole(
				user.getUserId(), _organization.getGroupId(),
				_role.getRoleId());
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}
	}

	@Override
	public boolean isDisabled(Object object) {
		User user = (User)object;

		try {
			if (isChecked(user)) {
				if (OrganizationMembershipPolicyUtil.isRoleProtected(
						PermissionThreadLocal.getPermissionChecker(),
						user.getUserId(), _organization.getOrganizationId(),
						_role.getRoleId()) ||
					OrganizationMembershipPolicyUtil.isRoleRequired(
						user.getUserId(), _organization.getOrganizationId(),
						_role.getRoleId())) {

					return true;
				}
			}
			else {
				if (!OrganizationMembershipPolicyUtil.isRoleAllowed(
						user.getUserId(), _organization.getOrganizationId(),
						_role.getRoleId())) {

					return true;
				}
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return super.isDisabled(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationRoleUserChecker.class);

	private final Organization _organization;
	private final Role _role;

}
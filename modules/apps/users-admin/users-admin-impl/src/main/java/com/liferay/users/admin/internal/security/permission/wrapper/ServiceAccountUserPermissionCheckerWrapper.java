/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.security.permission.wrapper;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapper;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Pei-Jung Lan
 */
public class ServiceAccountUserPermissionCheckerWrapper
	extends PermissionCheckerWrapper {

	public ServiceAccountUserPermissionCheckerWrapper(
		PermissionChecker permissionChecker,
		UserLocalService userLocalService) {

		super(permissionChecker);

		_permissionChecker = permissionChecker;
		_userLocalService = userLocalService;
	}

	@Override
	public boolean hasPermission(
		Group group, String name, long primKey, String actionId) {

		if (!_hasPermission(name, primKey)) {
			return false;
		}

		return permissionChecker.hasPermission(group, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		Group group, String name, String primKey, String actionId) {

		if (!_hasPermission(name, GetterUtil.getLong(primKey))) {
			return false;
		}

		return permissionChecker.hasPermission(group, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, long primKey, String actionId) {

		if (!_hasPermission(name, primKey)) {
			return false;
		}

		return permissionChecker.hasPermission(
			groupId, name, primKey, actionId);
	}

	@Override
	public boolean hasPermission(
		long groupId, String name, String primKey, String actionId) {

		if (!_hasPermission(name, GetterUtil.getLong(primKey))) {
			return false;
		}

		return permissionChecker.hasPermission(
			groupId, name, primKey, actionId);
	}

	private boolean _hasPermission(String name, long primKey) {
		if (StringUtil.equals(name, User.class.getName())) {
			User user = _userLocalService.fetchUser(primKey);

			if ((user != null) && user.isServiceAccountUser() &&
				!_permissionChecker.isCompanyAdmin()) {

				return false;
			}
		}

		return true;
	}

	private final PermissionChecker _permissionChecker;
	private final UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.UserGroupRoleServiceWrapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lianne Louie
 */
@Component(service = ServiceWrapper.class)
public class DepotUserGroupRoleServiceWrapper
	extends UserGroupRoleServiceWrapper {

	@Override
	public void addUserGroupRoles(long userId, long groupId, long[] roleIds)
		throws PortalException {

		DepotRoleSubtypeValidator.validate(groupId, roleIds);

		super.addUserGroupRoles(userId, groupId, roleIds);
	}

	@Override
	public void addUserGroupRoles(long[] userIds, long groupId, long roleId)
		throws PortalException {

		DepotRoleSubtypeValidator.validate(groupId, new long[] {roleId});

		super.addUserGroupRoles(userIds, groupId, roleId);
	}

	@Override
	public void updateUserGroupRoles(
			long userId, long groupId, long[] addedRoleIds,
			long[] deletedRoleIds)
		throws PortalException {

		DepotRoleSubtypeValidator.validate(groupId, addedRoleIds);

		super.updateUserGroupRoles(
			userId, groupId, addedRoleIds, deletedRoleIds);
	}

}
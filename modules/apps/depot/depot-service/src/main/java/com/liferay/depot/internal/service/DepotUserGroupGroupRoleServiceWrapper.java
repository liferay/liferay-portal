/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.service;

import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.UserGroupGroupRoleServiceWrapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lianne Louie
 */
@Component(service = ServiceWrapper.class)
public class DepotUserGroupGroupRoleServiceWrapper
	extends UserGroupGroupRoleServiceWrapper {

	@Override
	public void addUserGroupGroupRoles(
			long userGroupId, long groupId, long[] roleIds)
		throws PortalException {

		DepotRoleUtil.validate(groupId, roleIds);

		super.addUserGroupGroupRoles(userGroupId, groupId, roleIds);
	}

	@Override
	public void addUserGroupGroupRoles(
			long[] userGroupIds, long groupId, long roleId)
		throws PortalException {

		DepotRoleUtil.validate(groupId, new long[] {roleId});

		super.addUserGroupGroupRoles(userGroupIds, groupId, roleId);
	}

}
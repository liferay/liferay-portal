/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.tools.service.builder.test.model.PermissionCheckFinderEntry;
import com.liferay.portal.tools.service.builder.test.service.base.PermissionCheckFinderEntryLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class PermissionCheckFinderEntryLocalServiceImpl
	extends PermissionCheckFinderEntryLocalServiceBaseImpl {

	@Override
	public PermissionCheckFinderEntry addPermissionCheckFinderEntry(
			long companyId, long groupId, int integer, String name, String type,
			long userId)
		throws PortalException {

		// PermissionCheckFinderEntry

		PermissionCheckFinderEntry permissionCheckFinderEntry =
			permissionCheckFinderEntryPersistence.create(
				counterLocalService.increment());

		permissionCheckFinderEntry.setGroupId(groupId);
		permissionCheckFinderEntry.setCompanyId(companyId);
		permissionCheckFinderEntry.setUserId(userId);
		permissionCheckFinderEntry.setInteger(integer);
		permissionCheckFinderEntry.setName(name);
		permissionCheckFinderEntry.setType(type);

		permissionCheckFinderEntry =
			permissionCheckFinderEntryPersistence.update(
				permissionCheckFinderEntry);

		// Resources

		_resourceLocalService.addResources(
			companyId, groupId, userId,
			PermissionCheckFinderEntry.class.getName(),
			permissionCheckFinderEntry.getPrimaryKey(), false, true, false);

		return permissionCheckFinderEntry;
	}

	@Override
	public List<PermissionCheckFinderEntry> filterFindByGroupId(long groupId) {
		return permissionCheckFinderEntryPersistence.filterFindByGroupId(
			groupId);
	}

	@Override
	public List<PermissionCheckFinderEntry> filterFindByGroupId(
		long[] groupIds) {

		return permissionCheckFinderEntryPersistence.filterFindByGroupId(
			groupIds);
	}

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

}
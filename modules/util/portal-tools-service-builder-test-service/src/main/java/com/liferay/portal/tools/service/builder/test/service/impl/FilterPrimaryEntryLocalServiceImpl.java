/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.service.ResourceLocalService;
import com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry;
import com.liferay.portal.tools.service.builder.test.service.base.FilterPrimaryEntryLocalServiceBaseImpl;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class FilterPrimaryEntryLocalServiceImpl
	extends FilterPrimaryEntryLocalServiceBaseImpl {

	@Override
	public FilterPrimaryEntry addFilterPrimaryEntry(
			long companyId, long groupId, long resourcePrimKey, long userId)
		throws PortalException {

		FilterPrimaryEntry filterPrimaryEntry =
			filterPrimaryEntryPersistence.create(
				counterLocalService.increment());

		filterPrimaryEntry.setGroupId(groupId);
		filterPrimaryEntry.setCompanyId(companyId);
		filterPrimaryEntry.setUserId(userId);
		filterPrimaryEntry.setResourcePrimKey(resourcePrimKey);

		filterPrimaryEntry = filterPrimaryEntryPersistence.update(
			filterPrimaryEntry);

		_resourceLocalService.addResources(
			companyId, groupId, userId, FilterPrimaryEntry.class.getName(),
			resourcePrimKey, false, true, false);

		return filterPrimaryEntry;
	}

	@Override
	public FilterPrimaryEntry deleteFilterPrimaryEntry(
			FilterPrimaryEntry filterPrimaryEntry)
		throws PortalException {

		filterPrimaryEntry = filterPrimaryEntryPersistence.remove(
			filterPrimaryEntry);

		_resourceLocalService.deleteResource(
			filterPrimaryEntry.getCompanyId(),
			FilterPrimaryEntry.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			filterPrimaryEntry.getResourcePrimKey());

		return filterPrimaryEntry;
	}

	@Override
	public int filterCountByGroupId(long groupId) {
		return filterPrimaryEntryPersistence.filterCountByGroupId(groupId);
	}

	@Override
	public List<FilterPrimaryEntry> filterFindByGroupId(long groupId) {
		return filterPrimaryEntryPersistence.filterFindByGroupId(groupId);
	}

	@BeanReference(type = ResourceLocalService.class)
	private ResourceLocalService _resourceLocalService;

}
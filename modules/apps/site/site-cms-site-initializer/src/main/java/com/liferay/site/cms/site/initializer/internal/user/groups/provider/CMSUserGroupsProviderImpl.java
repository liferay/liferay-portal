/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.user.groups.provider;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.comparator.UserGroupNameComparator;
import com.liferay.site.cms.site.initializer.user.groups.provider.CMSUserGroupsProvider;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Larissa Ribeiro
 */
@Component(service = CMSUserGroupsProvider.class)
public class CMSUserGroupsProviderImpl implements CMSUserGroupsProvider {

	@Override
	public List<UserGroup> getUserGroups(String keywords, int start, int end) {
		LinkedHashMap<String, Object> userGroupParams = _getUserGroupParams();

		if (userGroupParams == null) {
			return Collections.emptyList();
		}

		return _userGroupLocalService.search(
			CompanyThreadLocal.getCompanyId(), keywords, userGroupParams, start,
			end, UserGroupNameComparator.getInstance(true));
	}

	@Override
	public int getUserGroupsCount(String keywords) {
		LinkedHashMap<String, Object> userGroupParams = _getUserGroupParams();

		if (userGroupParams == null) {
			return 0;
		}

		return _userGroupLocalService.searchCount(
			CompanyThreadLocal.getCompanyId(), keywords, userGroupParams);
	}

	private Long[] _getGroupIds() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		List<Long> groupIds = TransformUtil.transform(
			_depotEntryLocalService.getDepotEntryGroupIds(
				CompanyThreadLocal.getCompanyId(), DepotConstants.TYPE_SPACE),
			groupId -> {
				if (permissionChecker.isGroupAdmin(groupId) ||
					_groupLocalService.hasUserGroup(
						PrincipalThreadLocal.getUserId(), groupId)) {

					return groupId;
				}

				return null;
			});

		return groupIds.toArray(new Long[0]);
	}

	private LinkedHashMap<String, Object> _getUserGroupParams() {
		Long[] groupIds = _getGroupIds();

		if (ArrayUtil.isEmpty(groupIds)) {
			return null;
		}

		return LinkedHashMapBuilder.<String, Object>put(
			"userGroupsGroups", groupIds
		).build();
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserGroupLocalService _userGroupLocalService;

}
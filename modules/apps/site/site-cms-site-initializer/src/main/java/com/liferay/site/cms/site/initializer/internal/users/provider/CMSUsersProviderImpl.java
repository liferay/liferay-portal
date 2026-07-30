/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.users.provider;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.users.provider.CMSUsersProvider;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Leite
 */
@Component(service = CMSUsersProvider.class)
public class CMSUsersProviderImpl implements CMSUsersProvider {

	@Override
	public List<User> getUsers(String keywords, int start, int end) {
		LinkedHashMap<String, Object> userParams = _getUserParams();

		if (userParams == null) {
			return Collections.emptyList();
		}

		return _userLocalService.search(
			CompanyThreadLocal.getCompanyId(), keywords,
			WorkflowConstants.STATUS_APPROVED, userParams, start, end,
			UserFirstNameComparator.getInstance(true));
	}

	@Override
	public int getUsersCount(String keywords) {
		LinkedHashMap<String, Object> userParams = _getUserParams();

		if (userParams == null) {
			return 0;
		}

		return _userLocalService.searchCount(
			CompanyThreadLocal.getCompanyId(), keywords,
			WorkflowConstants.STATUS_APPROVED, userParams);
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

	private LinkedHashMap<String, Object> _getUserParams() {
		Long[] groupIds = _getGroupIds();

		if (ArrayUtil.isEmpty(groupIds)) {
			return null;
		}

		return LinkedHashMapBuilder.<String, Object>put(
			"inherit", Boolean.TRUE
		).put(
			"usersGroups", groupIds
		).build();
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.resource.v1_0;

import com.liferay.headless.cmp.dto.v1_0.UserGroup;
import com.liferay.headless.cmp.resource.v1_0.UserGroupResource;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.site.cms.site.initializer.user.groups.provider.CMSUserGroupsProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Larissa Ribeiro
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-group.properties",
	scope = ServiceScope.PROTOTYPE, service = UserGroupResource.class
)
public class UserGroupResourceImpl extends BaseUserGroupResourceImpl {

	@Override
	public Page<UserGroup> getProjectUserGroupsPage(
			Long projectId, String search, Pagination pagination)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(projectId);

		_groupModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(),
			objectEntry.getGroupId(), ActionKeys.ASSIGN_MEMBERS);

		return Page.of(
			transform(
				_cmsUserGroupsProvider.getUserGroups(
					search, pagination.getStartPosition(),
					pagination.getEndPosition()),
				this::_toUserGroup),
			pagination, _cmsUserGroupsProvider.getUserGroupsCount(search));
	}

	private UserGroup _toUserGroup(
		com.liferay.portal.kernel.model.UserGroup serviceBuilderUserGroup) {

		return new UserGroup() {
			{
				setExternalReferenceCode(
					serviceBuilderUserGroup::getExternalReferenceCode);
				setId(serviceBuilderUserGroup::getUserGroupId);
				setName(serviceBuilderUserGroup::getName);
				setUsersCount(
					() -> (long)_userLocalService.getUserGroupUsersCount(
						serviceBuilderUserGroup.getUserGroupId()));
			}
		};
	}

	@Reference
	private CMSUserGroupsProvider _cmsUserGroupsProvider;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.Group)"
	)
	private ModelResourcePermission<Group> _groupModelResourcePermission;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private UserLocalService _userLocalService;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.internal.dto.v1_0.converter;

import com.liferay.headless.admin.user.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.dto.v1_0.UserAccountBrief;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.PermissionUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.RoleBriefUtil;
import com.liferay.headless.admin.user.internal.dto.v1_0.util.UserAccountBriefUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.PermissionService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserGroupService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsSupplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Erick Monteiro
 */
@Component(
	property = {
		"dto.class.name=com.liferay.portal.kernel.model.UserGroup",
		"service.ranking:Integer=" + Integer.MAX_VALUE
	},
	service = DTOConverter.class
)
public class UserGroupResourceDTOConverter
	implements DTOConverter
		<UserGroup, com.liferay.headless.admin.user.dto.v1_0.UserGroup> {

	@Override
	public String getContentType() {
		return com.liferay.headless.admin.user.dto.v1_0.UserGroup.class.
			getSimpleName();
	}

	@Override
	public UserGroup getObject(String externalReferenceCode) throws Exception {
		UserGroup userGroup =
			_userGroupService.fetchUserGroupByExternalReferenceCode(
				externalReferenceCode, CompanyThreadLocal.getCompanyId());

		if (userGroup == null) {
			userGroup = _userGroupService.getUserGroup(
				GetterUtil.getLong(externalReferenceCode));
		}

		return userGroup;
	}

	@Override
	public com.liferay.headless.admin.user.dto.v1_0.UserGroup toDTO(
			DTOConverterContext dtoConverterContext, UserGroup userGroup)
		throws PortalException {

		if (userGroup == null) {
			return null;
		}

		return new com.liferay.headless.admin.user.dto.v1_0.UserGroup() {
			{
				setActions(dtoConverterContext::getActions);
				setCreator(
					() -> NestedFieldsSupplier.supply(
						"creator",
						fieldName -> CreatorUtil.toCreator(
							_portal,
							_userLocalService.fetchUser(
								userGroup.getUserId()))));
				setDateCreated(userGroup::getCreateDate);
				setDateModified(userGroup::getModifiedDate);
				setDescription(userGroup::getDescription);
				setExternalReferenceCode(userGroup::getExternalReferenceCode);
				setId(userGroup::getUserGroupId);
				setName(userGroup::getName);
				setPermissions(
					() -> NestedFieldsSupplier.supply(
						"permissions",
						nestedFieldNames -> PermissionUtil.toPermissions(
							userGroup.getCompanyId(), userGroup.getGroupId(),
							userGroup.getUserGroupId(),
							UserGroup.class.getName(), _permissionService,
							_resourceActionLocalService)));
				setRoleBriefs(
					() -> NestedFieldsSupplier.supply(
						"roleBriefs",
						fieldName -> TransformUtil.transformToArray(
							_roleService.getGroupRoles(userGroup.getGroupId()),
							role -> RoleBriefUtil.toRoleBrief(
								dtoConverterContext, role),
							RoleBrief.class)));
				setUserAccountBriefs(
					() -> NestedFieldsSupplier.supply(
						"userAccountBriefs",
						fieldName -> TransformUtil.transformToArray(
							_userLocalService.getUserGroupUsers(
								userGroup.getUserGroupId()),
							UserAccountBriefUtil::toUserAccountBrief,
							UserAccountBrief.class)));
				setUsersCount(
					() -> _userLocalService.getUserGroupUsersCount(
						userGroup.getUserGroupId(),
						WorkflowConstants.STATUS_APPROVED));
			}
		};
	}

	@Reference
	private PermissionService _permissionService;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private RoleService _roleService;

	@Reference
	private UserGroupService _userGroupService;

	@Reference
	private UserLocalService _userLocalService;

}